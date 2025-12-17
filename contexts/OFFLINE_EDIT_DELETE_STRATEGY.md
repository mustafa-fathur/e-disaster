# Offline Edit/Delete Strategy for Collaborative Data

## Executive Summary

**Recommendation: YES, implement offline edit/delete, but with a hybrid approach:**
- ✅ **Allow offline edits** with conflict detection
- ✅ **Use soft deletes** instead of hard deletes for better conflict resolution
- ✅ **Implement Last-Write-Wins (LWW) with timestamps** for simplicity
- ✅ **Add conflict detection and user notifications** for awareness
- ✅ **Show sync status indicators** in UI
- ⚠️ **Consider requiring online confirmation for critical operations** (deletes)

---

## The Problem: Conflict Scenarios

### Scenario 1: Edit-Delete Conflict
```
Timeline:
- 10:00 AM: User A (offline) edits Victim X
- 10:05 AM: User B (online) deletes Victim X
- 10:10 AM: User A comes online → Conflict!
```

### Scenario 2: Edit-Edit Conflict
```
Timeline:
- 10:00 AM: User A (offline) edits Victim X: status = "Meninggal"
- 10:05 AM: User B (online) edits Victim X: status = "Luka Ringan"
- 10:10 AM: User A comes online → Conflict!
```

### Scenario 3: Delete-Edit Conflict
```
Timeline:
- 10:00 AM: User A (offline) deletes Victim X
- 10:05 AM: User B (online) edits Victim X
- 10:10 AM: User A comes online → Conflict!
```

---

## Strategy Options

### Option 1: Last-Write-Wins (LWW) ⭐ **RECOMMENDED**
**How it works:**
- Compare `updatedAt` timestamps
- The record with the latest timestamp wins
- Simpler to implement and understand

**Pros:**
- ✅ Simple implementation
- ✅ No user intervention needed
- ✅ Fast conflict resolution
- ✅ Works well for disaster management (latest info is usually most accurate)

**Cons:**
- ❌ Can lose data if someone edits offline for a long time
- ❌ No manual conflict resolution

**Best for:** Disaster management where latest information is most critical

---

### Option 2: Client-Wins (Optimistic)
**How it works:**
- Local changes always override server changes
- User's offline work is never lost

**Pros:**
- ✅ User's work is never lost
- ✅ Good for offline-first apps

**Cons:**
- ❌ Can overwrite other users' recent changes
- ❌ Poor for collaborative systems
- ❌ Can cause data inconsistency

**Best for:** Personal data, not collaborative systems

---

### Option 3: Server-Wins (Pessimistic)
**How it works:**
- Server changes always override local changes
- Offline edits are discarded if conflicts occur

**Pros:**
- ✅ Server is always authoritative
- ✅ No data conflicts

**Cons:**
- ❌ User's offline work can be lost
- ❌ Poor user experience
- ❌ Defeats purpose of offline mode

**Best for:** Read-only or single-user systems

---

### Option 4: Manual Conflict Resolution
**How it works:**
- Detect conflicts and show both versions to user
- User manually merges or chooses which version to keep

**Pros:**
- ✅ No data loss
- ✅ User has full control

**Cons:**
- ❌ Complex to implement
- ❌ Requires user intervention (bad UX for mobile)
- ❌ Can be confusing for non-technical users

**Best for:** Document editing (Google Docs), not disaster management

---

## Recommended Implementation: Hybrid LWW with Soft Deletes

### Core Strategy
1. **Last-Write-Wins (LWW)** for updates
2. **Soft deletes** instead of hard deletes
3. **Conflict detection** with user notifications
4. **Sync status indicators** in UI

### Implementation Details

#### 1. Soft Delete Pattern
Instead of immediately deleting, mark as `PENDING_DELETE`:

```kotlin
// When user deletes offline
suspend fun deleteDisasterVictim(disasterId: String, victimId: String) {
    val entity = victimDao.getVictimById(victimId)
    if (entity != null) {
        // Soft delete: mark as PENDING_DELETE
        val deletedEntity = entity.copy(
            syncStatus = SyncStatus.PENDING_DELETE,
            updatedAt = System.currentTimeMillis()
        )
        victimDao.insertVictim(deletedEntity) // Keep in DB
        
        // If online, try to delete on server
        if (networkMonitor.isOnline()) {
            try {
                apiService.deleteDisasterVictim(disasterId, victimId)
                // Only hard delete after server confirms
                victimDao.deleteById(victimId)
            } catch (e: Exception) {
                // Keep as PENDING_DELETE for later sync
            }
        }
    }
}
```

**Benefits:**
- If someone edits a "deleted" record, we can detect the conflict
- Can restore if delete was a mistake
- Better conflict resolution

#### 2. Conflict Detection on Sync

```kotlin
suspend fun syncVictim(victimEntity: DisasterVictimEntity) {
    if (victimEntity.syncStatus == SyncStatus.PENDING_UPDATE) {
        // Check if server version is newer
        val serverVersion = apiService.getDisasterVictimDetail(
            victimEntity.disasterId!!, 
            victimEntity.id
        )
        
        val serverUpdatedAt = parseTimestamp(serverVersion.data.updatedAt)
        
        if (serverUpdatedAt > victimEntity.updatedAt) {
            // Server has newer version - CONFLICT!
            // Option A: Server wins (safer for disaster data)
            // Option B: Client wins (preserve user's work)
            // Option C: Merge fields (complex)
            
            // For now: Server wins, but log conflict
            logConflict(victimEntity.id, "Server has newer version")
            // Update local with server version
            val serverEntity = serverVersion.data.toEntity()
            victimDao.insertVictim(serverEntity)
        } else {
            // Client version is newer or same - safe to update
            apiService.updateDisasterVictim(...)
            victimDao.updateSyncStatus(victimEntity.id, SyncStatus.SYNCED)
        }
    }
}
```

#### 3. UI Indicators

Show sync status in the UI:

```kotlin
// In victim list card
Row {
    Text(victim.name)
    when (victim.syncStatus) {
        SyncStatus.SYNCED -> Icon(Icons.Default.Check, "Synced")
        SyncStatus.PENDING_UPDATE -> Icon(Icons.Default.Sync, "Pending sync")
        SyncStatus.PENDING_DELETE -> Icon(Icons.Default.Delete, "Pending delete")
        SyncStatus.SYNC_FAILED -> Icon(Icons.Default.Error, "Sync failed")
    }
}
```

#### 4. Conflict Notifications

When conflicts are detected:

```kotlin
// Show notification to user
fun showConflictNotification(victimId: String, conflictType: String) {
    val message = when (conflictType) {
        "EDIT_DELETE" -> "Victim was deleted by another user. Your changes were not saved."
        "EDIT_EDIT" -> "Victim was updated by another user. Server version was used."
        "DELETE_EDIT" -> "Victim was edited after you deleted it. Deletion cancelled."
    }
    // Show snackbar or notification
}
```

---

## Specific Recommendations for e-Disaster

### For Updates (Edit)
✅ **Allow offline edits** with LWW strategy
- User can edit victim data offline
- When syncing, if server has newer version, show notification but use server version
- If client version is newer, update server

### For Deletes
⚠️ **Use soft deletes with confirmation**
- Mark as `PENDING_DELETE` instead of hard delete
- When syncing:
  - If server version exists and is newer → Cancel delete, show notification
  - If server version doesn't exist → Delete confirmed
  - If server version is older → Proceed with delete

### Conflict Resolution Priority
1. **Server timestamp is newer** → Server wins (use server version)
2. **Client timestamp is newer** → Client wins (update server)
3. **Same timestamp** → Server wins (safer default)

### Special Cases

#### Case 1: Victim was deleted on server, but user edited it offline
**Decision:** Cancel the edit, show notification: "This victim was deleted. Your changes were not saved."

#### Case 2: Victim was deleted offline, but someone edited it on server
**Decision:** Cancel the delete, show notification: "This victim was updated by another user. Deletion cancelled."

#### Case 3: Both users edited the same field
**Decision:** Use server version (LWW), but log what was overwritten for audit trail.

---

## Implementation Checklist

### Phase 1: Basic Offline Edit/Delete
- [ ] Update `updateDisasterVictim()` to work offline (mark as `PENDING_UPDATE`)
- [ ] Update `deleteDisasterVictim()` to use soft delete (mark as `PENDING_DELETE`)
- [ ] Update `SyncManager` to handle `PENDING_UPDATE` and `PENDING_DELETE`
- [ ] Add conflict detection logic

### Phase 2: Conflict Handling
- [ ] Implement timestamp comparison on sync
- [ ] Add conflict logging
- [ ] Show sync status indicators in UI
- [ ] Add conflict notification system

### Phase 3: User Experience
- [ ] Add "Sync Status" badge to victim cards
- [ ] Show conflict notifications
- [ ] Add "Retry Sync" button for failed syncs
- [ ] Add "View Conflicts" screen (optional, for advanced users)

---

## Alternative: Optimistic Locking (Advanced)

If you want more sophisticated conflict resolution, consider adding a `version` field:

```kotlin
data class DisasterVictimEntity(
    // ... existing fields ...
    val version: Int = 1  // Increment on each update
)
```

**How it works:**
- Each update increments `version`
- On sync, compare versions
- If server version > client version → Conflict detected
- If versions match → Safe to update

**Pros:** More precise conflict detection
**Cons:** Requires backend support for version tracking

---

## Conclusion

**For e-Disaster, I recommend:**
1. ✅ **Implement offline edit/delete** - Essential for field workers
2. ✅ **Use LWW with timestamps** - Simple and appropriate for disaster data
3. ✅ **Use soft deletes** - Better conflict resolution
4. ✅ **Show sync status** - Keep users informed
5. ✅ **Log conflicts** - For audit and debugging
6. ⚠️ **Consider requiring online confirmation for deletes** - Since deletes are critical operations

**The chaos is manageable** if you:
- Use timestamps for conflict resolution
- Show clear sync status to users
- Log conflicts for review
- Default to server wins for safety

**Remember:** In disaster management, **latest information is usually most critical**, so LWW makes sense. The occasional data loss from conflicts is acceptable compared to the benefit of offline capability.

