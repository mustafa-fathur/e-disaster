# Bug Analysis & Solutions

## 🔴 Critical Issues Identified

### Issue 1: Disaster List Not Refreshing Automatically

**Root Cause:**
- `DisasterListViewModel` uses `mutableStateOf<List<Disaster>>` which is **not reactive**
- It only fetches once in `init`, then never updates
- Background sync happens but UI doesn't know about it
- User must manually navigate away/back to trigger re-fetch

**Solution:**
- Change `DisasterListViewModel` to observe `Flow<List<DisasterEntity>>` from Room
- Use `stateIn()` or `collectAsState()` to make it reactive
- When Room data changes (from background sync), UI automatically updates
- Add manual refresh function that forces API fetch and updates Room

**Implementation:**
```kotlin
// In DisasterListViewModel
val disasters: StateFlow<List<Disaster>> = flow {
    disasterDao.getAllDisasters()
        .map { entities -> entities.map { with(DisasterMapper) { it.toModel() } } }
}
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

fun refreshDisasters() {
    viewModelScope.launch {
        // Force fetch from API and update Room
        disasterRepository.refreshDisasters()
    }
}
```

---

### Issue 2: Add Victim Needs Manual Trigger to Sync

**Root Cause:**
- After adding victim, sync only happens when opening detail screen
- `getDisasterVictims()` does background sync but doesn't trigger UI update
- The `savedStateHandle` mechanism works, but `refreshVictims()` might not be called reliably

**Solution:**
- After successful add, immediately trigger sync for that specific victim
- Call `refreshVictims()` in ViewModel after add operation completes
- Ensure `refreshVictims()` actually updates the UI state
- Add explicit sync trigger in `addDisasterVictim()` after saving locally

**Implementation:**
```kotlin
// In addDisasterVictim() after saving locally
if (networkMonitor.isOnline()) {
    // Try immediate sync
    CoroutineScope(Dispatchers.IO).launch {
        syncPendingVictim(victimEntity, disasterId)
    }
}

// In AddDisasterVictimViewModel after success
LaunchedEffect(uiState.isSuccess) {
    if (uiState.isSuccess) {
        // Trigger refresh on parent screen
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("victim_updated", true)
        navController.popBackStack()
    }
}
```

---

### Issue 3: Update Always Server Wins (Not True LWW)

**Root Cause:**
- `updateDisasterVictim()` when online **directly calls API** and overwrites local with server response
- It doesn't check for conflicts first (doesn't compare timestamps)
- The LWW logic exists in `SyncManager` but is **never called** for online updates
- Online updates bypass conflict detection entirely

**Solution:**
- When online, **still check for conflicts** before updating
- Fetch server version first, compare timestamps
- If server is newer → Use server version (show notification)
- If client is newer → Update server
- Move conflict detection logic to repository level, not just SyncManager

**Implementation:**
```kotlin
// In updateDisasterVictim() when online
if (networkMonitor.isOnline()) {
    // 1. Check for conflicts first
    val serverResponse = try {
        apiService.getDisasterVictimDetail(disasterId, victimId)
    } catch (e: Exception) {
        // Victim doesn't exist, proceed with update
        null
    }
    
    if (serverResponse != null) {
        val serverUpdatedAt = parseDateString(serverResponse.data.updatedAt)
        if (serverUpdatedAt > existingVictim.updatedAt) {
            // Server wins - conflict!
            // Update local with server version
            val serverEntity = serverResponse.data.toEntity()
            victimDao.insertVictim(serverEntity)
            throw Exception("Data telah diubah oleh pengguna lain. Perubahan Anda tidak disimpan.")
        }
    }
    
    // 2. Client version is newer or same - proceed with update
    val response = apiService.updateDisasterVictim(...)
    // Update local...
}
```

---

### Issue 4: Delete Not Working Locally (Victim Still Shows)

**Root Cause:**
- `getVictimsByDisasterId()` query **doesn't filter out `PENDING_DELETE`** victims
- Soft delete marks victim as `PENDING_DELETE` but query still returns it
- UI shows deleted victims because they're still in the query results

**Solution:**
- Filter out `PENDING_DELETE` victims in the query
- Add `WHERE sync_status != 'PENDING_DELETE'` to DAO query
- Or filter in repository before returning to UI
- Only show `PENDING_DELETE` in a "Deleted Items" section if needed

**Implementation:**
```kotlin
// In DisasterVictimDao
@Query("SELECT * FROM disaster_victims WHERE disaster_id = :disasterId AND sync_status != 'PENDING_DELETE' ORDER BY created_at DESC")
fun getVictimsByDisasterId(disasterId: String): Flow<List<DisasterVictimEntity>>

// OR filter in repository
val activeEntities = localEntities.filter { it.syncStatus != SyncStatus.PENDING_DELETE }
```

---

### Issue 5: Photo Delete Not Working Locally

**Root Cause:**
- Same issue as victim delete
- `getPicturesByForeignId()` doesn't filter out `PENDING_DELETE` pictures
- Soft delete marks picture but query still returns it

**Solution:**
- Filter out `PENDING_DELETE` pictures in query or repository
- Same approach as victim delete

**Implementation:**
```kotlin
// In PictureDao
@Query("SELECT * FROM pictures WHERE foreign_id = :foreignId AND type = :type AND sync_status != 'PENDING_DELETE' ORDER BY created_at DESC")
fun getPicturesByForeignId(foreignId: String, type: String): Flow<List<PictureEntity>>
```

---

### Issue 6: Assignment Status Lost When Offline

**Root Cause:**
- `isUserAssigned()` checks local DB, but assignment might not be saved properly
- When online, assignment is created on server but local record might have wrong ID
- When offline, if local record doesn't exist, returns `false`
- The assignment entity might not be properly linked to disaster

**Solution:**
- Ensure assignment is **always saved locally** when created (online or offline)
- When checking assignment offline, look for **any** assignment record for that disaster+user
- Don't rely on exact ID matching - use disasterId + userId combination
- Save assignment immediately after `joinDisaster()` succeeds

**Implementation:**
```kotlin
// In joinDisaster() after server response
if (networkMonitor.isOnline()) {
    val response = apiService.joinDisaster(disasterId)
    // Get server assignment ID if available
    val serverAssignmentId = response.id ?: localId
    // Update local assignment with server ID
    val updatedAssignment = assignmentEntity.copy(
        id = serverAssignmentId,
        syncStatus = SyncStatus.SYNCED,
        lastSyncedAt = System.currentTimeMillis()
    )
    volunteerDao.insertVolunteer(updatedAssignment)
}

// In isUserAssigned() - improve offline check
val localAssignment = volunteerDao.isUserAssigned(disasterId, currentUser.id)
if (localAssignment != null) {
    // Check if it's PENDING_CREATE and online - try to sync
    if (localAssignment.syncStatus == SyncStatus.PENDING_CREATE && networkMonitor.isOnline()) {
        // Try to verify with server
        try {
            val apiResponse = apiService.checkAssignment(disasterId)
            if (apiResponse.assigned) {
                // Update local record
                volunteerDao.updateSyncStatus(localAssignment.id, SyncStatus.SYNCED, System.currentTimeMillis())
            }
        } catch (e: Exception) {
            // Keep local assignment
        }
    }
    return true // Always return true if local assignment exists
}
```

---

### Issue 7: Refresh Button Not Working Properly

**Root Cause:**
- `refreshVictims()` calls `getDisasterVictims()` which returns `List<DisasterVictim>`
- This is a **one-time fetch**, not reactive
- If ViewModel uses `mutableStateOf`, it updates state but UI might not recompose
- Background sync happens but doesn't trigger UI update

**Solution:**
- Make `refreshVictims()` **force a fresh fetch** from API (if online)
- Update Room database with fresh data
- Since Room Flow is observed, UI will automatically update
- Add explicit state update after refresh

**Implementation:**
```kotlin
// In DisasterDetailViewModel
fun refreshVictims() {
    viewModelScope.launch {
        currentDisasterId?.let { id ->
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Force refresh: fetch from API if online, update Room
                if (networkMonitor.isOnline()) {
                    // Force API fetch
                    val response = victimRepository.apiService.getDisasterVictims(id)
                    val entities = response.data.map { dto ->
                        with(DisasterVictimMapper) {
                            dto.toEntity(disasterId = id, syncStatus = SyncStatus.SYNCED)
                        }
                    }
                    victimRepository.victimDao.insertVictims(entities)
                }
                // Get from Room (will trigger Flow update)
                val victims = victimRepository.getDisasterVictims(id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        victims = victims
                    )
                }
            } catch (e: Exception) {
                // Fallback to local data
                val victims = victimRepository.getDisasterVictims(id)
                _uiState.update {
                    it.copy(isLoading = false, victims = victims)
                }
            }
        }
    }
}
```

---

## 🔧 Additional Issues

### Issue 8: Background Sync Doesn't Trigger UI Updates

**Root Cause:**
- Background syncs update Room database
- But ViewModels use `mutableStateOf` which doesn't observe Room changes
- UI only updates when ViewModel function is explicitly called

**Solution:**
- Convert ViewModels to observe Room `Flow` directly
- Use `stateIn()` or `collectAsState()` for reactive updates
- When Room data changes, UI automatically recomposes

---

### Issue 9: Date Format Issue in Update

**Root Cause:**
- `updateDisasterVictim()` tries to parse `dateOfBirth` as "dd/MM/yyyy"
- But entity stores it as "yyyy-MM-dd" (from add operation)
- Format mismatch causes parsing errors

**Solution:**
- Store date in consistent format in entity
- Or handle both formats in update function
- Use the format that entity already has

---

## 📋 Implementation Priority

### High Priority (Critical Bugs)
1. ✅ **Filter PENDING_DELETE from queries** (Issue 4 & 5)
2. ✅ **Fix assignment status offline** (Issue 6)
3. ✅ **Add conflict detection to online update** (Issue 3)
4. ✅ **Make refresh actually refresh** (Issue 7)

### Medium Priority (UX Improvements)
5. ✅ **Make disaster list reactive** (Issue 1)
6. ✅ **Auto-sync after add victim** (Issue 2)
7. ✅ **Convert ViewModels to observe Room Flow** (Issue 8)

### Low Priority (Polish)
8. ✅ **Fix date format in update** (Issue 9)

---

## 🎯 Recommended Implementation Order

1. **First**: Fix delete issues (Issue 4 & 5) - Quick fix, high impact
2. **Second**: Fix assignment status (Issue 6) - Critical for offline access
3. **Third**: Fix refresh mechanism (Issue 7) - Improves UX significantly
4. **Fourth**: Add conflict detection to update (Issue 3) - Prevents data loss
5. **Fifth**: Make lists reactive (Issue 1) - Long-term improvement
6. **Sixth**: Auto-sync after add (Issue 2) - Nice to have

---

## 🔍 Testing After Fixes

After implementing fixes, verify:

1. ✅ Delete victim → Immediately disappears from list
2. ✅ Delete photo → Immediately disappears from UI
3. ✅ Offline assignment → Still shows as assigned when offline
4. ✅ Refresh button → Actually refreshes data
5. ✅ Update conflict → Shows proper conflict resolution
6. ✅ Add victim → Auto-syncs without manual trigger
7. ✅ Disaster list → Updates automatically when data changes

---

## 💡 Key Architectural Changes Needed

1. **Reactive Data Flow**: ViewModels should observe Room `Flow`, not use `mutableStateOf` with one-time fetches
2. **Consistent Conflict Detection**: All update operations should check conflicts, not just background sync
3. **Proper Filtering**: Queries should exclude `PENDING_DELETE` items by default
4. **Explicit Refresh**: Refresh functions should force API fetch and update Room, triggering Flow updates

