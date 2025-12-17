# Testing Guide: Disaster List & Victim Management (2 Devices)

## Prerequisites

- **Device A**: Primary device (e.g., Phone 1)
- **Device B**: Secondary device (e.g., Phone 2)
- Both devices logged in with **different user accounts** (to simulate real collaboration)
- Both devices connected to the same server
- Ability to toggle airplane mode / Wi-Fi on both devices

---

## 📋 Test Scenario 1: Disaster List Feature

### Test 1.1: Online Mode - View Disaster List

**Setup:**
- Device A: Online
- Device B: Online

**Steps:**
1. **Device A**: Open app → Navigate to "Bencana" (Disaster List)
2. **Device B**: Open app → Navigate to "Bencana" (Disaster List)

**Expected Results:**
- ✅ Both devices show the same disaster list
- ✅ Disasters load from local DB first (fast)
- ✅ Background sync updates data if server has newer data
- ✅ No duplicates in the list

**Verify:**
- Check logs: `DisasterRepo` should show "Found X disasters in local database"
- Check logs: Background sync should run if online

---

### Test 1.2: Offline Mode - View Cached Disasters

**Setup:**
- Device A: Was online, now go **offline** (airplane mode)
- Device B: Online

**Steps:**
1. **Device A**: (While online) Open disaster list → Wait for data to load
2. **Device A**: Turn on airplane mode (offline)
3. **Device A**: Navigate away from disaster list → Navigate back
4. **Device B**: (Online) Add a new disaster via server/admin panel

**Expected Results:**
- ✅ Device A shows cached disasters (from local DB)
- ✅ Device A does NOT show the new disaster from Device B (not yet synced)
- ✅ No crashes or errors
- ✅ When Device A goes back online, new disaster appears after refresh

**Verify:**
- Check logs: `DisasterRepo` should show "Found X disasters in local database"
- Check logs: Should NOT attempt API call when offline

---

### Test 1.3: Offline → Online Transition

**Setup:**
- Device A: Offline (has cached disasters)
- Device B: Online (adds new disaster)

**Steps:**
1. **Device A**: (Offline) View disaster list → See cached disasters
2. **Device B**: (Online) Add new disaster
3. **Device A**: Turn off airplane mode (go online)
4. **Device A**: Pull to refresh or navigate back/forward

**Expected Results:**
- ✅ Device A shows cached disasters immediately
- ✅ After going online, background sync fetches new disaster
- ✅ New disaster appears in list (may need refresh)
- ✅ No duplicates

**Verify:**
- Check logs: Should see "Pulled X disasters from server" after going online

---

## 👥 Test Scenario 2: Victim Management - Add Victim

### Test 2.1: Online Mode - Add Victim with Image

**Setup:**
- Device A: Online
- Device B: Online

**Steps:**
1. **Device A**: Open disaster detail → "Korban" tab
2. **Device A**: Click FAB → Add new victim with:
   - Name: "Test Victim A"
   - NIK: "1234567890"
   - Image: Add 1 photo
   - Status: "Luka Ringan"
3. **Device A**: Submit
4. **Device B**: Open same disaster → "Korban" tab
5. **Device B**: Pull to refresh

**Expected Results:**
- ✅ Device A: Victim appears immediately in list
- ✅ Device A: Image displays correctly
- ✅ Device B: After refresh, sees "Test Victim A"
- ✅ Device B: Image displays correctly
- ✅ **NO DUPLICATES** in either device
- ✅ Both devices show same victim ID

**Verify:**
- Check logs: Should see "Synced victim to server: [server-id]"
- Check logs: Should see "Deleted old localId record: [local-id]"
- Check database: Only ONE victim record with server ID

---

### Test 2.2: Offline Mode - Add Victim with Image

**Setup:**
- Device A: Online → Go **offline**
- Device B: Online

**Steps:**
1. **Device A**: (Offline) Open disaster detail → "Korban" tab
2. **Device A**: Click FAB → Add new victim:
   - Name: "Offline Victim A"
   - NIK: "9876543210"
   - Image: Add 1 photo
   - Status: "Luka Berat"
3. **Device A**: Submit
4. **Device A**: Check victim list
5. **Device B**: (Online) Open same disaster → "Korban" tab
6. **Device A**: Go online
7. **Device A**: Open victim detail (triggers sync)
8. **Device B**: Pull to refresh

**Expected Results:**
- ✅ Device A: Victim appears immediately (saved locally)
- ✅ Device A: Image displays from local storage
- ✅ Device A: Victim has `PENDING_CREATE` status (check logs)
- ✅ Device B: Does NOT see "Offline Victim A" (not synced yet)
- ✅ Device A: After going online and opening detail, sync happens
- ✅ Device B: After refresh, sees "Offline Victim A"
- ✅ **NO DUPLICATES** after sync
- ✅ Image syncs correctly to server

**Verify:**
- Check logs: Should see "Saved victim locally with ID: local_..."
- Check logs: Should see "Synced pending victim to server: [server-id]"
- Check logs: Should see "Deleted old record with localId: [local-id]"
- Check database: After sync, only ONE record with server ID

---

### Test 2.3: Two Devices Add Victims Offline Simultaneously

**Setup:**
- Device A: Offline
- Device B: Offline

**Steps:**
1. **Device A**: (Offline) Add victim "Offline Victim A"
2. **Device B**: (Offline) Add victim "Offline Victim B"
3. **Device A**: Go online → Open victim detail (triggers sync)
4. **Device B**: Go online → Open victim detail (triggers sync)
5. **Device A**: Refresh victim list
6. **Device B**: Refresh victim list

**Expected Results:**
- ✅ Both victims sync successfully
- ✅ Both devices see both victims after refresh
- ✅ **NO DUPLICATES**
- ✅ Each victim has unique server ID

**Verify:**
- Check logs: Both devices should sync successfully
- Check database: Two distinct victim records

---

## ✏️ Test Scenario 3: Victim Management - Edit Victim

### Test 3.1: Online Mode - Edit Victim

**Setup:**
- Device A: Online
- Device B: Online
- Both devices have victim "Test Victim" (already synced)

**Steps:**
1. **Device A**: Open victim detail → Edit → Change name to "Test Victim Updated A"
2. **Device A**: Save
3. **Device B**: Open same victim detail
4. **Device B**: Pull to refresh

**Expected Results:**
- ✅ Device A: Victim updated immediately
- ✅ Device B: After refresh, sees "Test Victim Updated A"
- ✅ No conflicts (Device B had older version)
- ✅ Single record in database

**Verify:**
- Check logs: Should see "Synced updated victim to server"
- Check database: One record with updated name

---

### Test 3.2: Offline Mode - Edit Victim

**Setup:**
- Device A: Offline
- Device B: Online

**Steps:**
1. **Device A**: (Offline) Open victim detail → Edit → Change name to "Offline Edit A"
2. **Device A**: Save
3. **Device B**: (Online) Open same victim → Edit → Change name to "Online Edit B"
4. **Device B**: Save
5. **Device A**: Go online → Open victim detail (triggers sync)

**Expected Results:**
- ✅ Device A: Victim updated locally (shows "Offline Edit A")
- ✅ Device B: Victim updated on server (shows "Online Edit B")
- ✅ Device A: After sync, **conflict resolution** happens
- ✅ **Last-Write-Wins**: Device B's version wins (newer timestamp)
- ✅ Device A: After sync, shows "Online Edit B" (server version)
- ✅ Log shows conflict detected

**Verify:**
- Check logs: Should see "Conflict detected: Server version is newer"
- Check logs: Should see "Resolved conflict: Used server version"
- Check database: Final record shows "Online Edit B"

---

### Test 3.3: Edit-Edit Conflict (Both Offline)

**Setup:**
- Device A: Offline
- Device B: Offline

**Steps:**
1. **Device A**: (Offline) Edit victim → Change status to "Meninggal"
2. **Device B**: (Offline) Edit same victim → Change status to "Luka Ringan"
3. **Device A**: Go online → Open victim detail (syncs)
4. **Device B**: Go online → Open victim detail (syncs)

**Expected Results:**
- ✅ Device A: Syncs first → Status becomes "Meninggal" on server
- ✅ Device B: Syncs second → Status becomes "Luka Ringan" on server (overwrites)
- ✅ Device A: After refresh, sees "Luka Ringan" (Device B's version wins)
- ✅ Last write wins (Device B synced later)

**Verify:**
- Check logs: Should see conflict resolution
- Check database: Final status is "Luka Ringan"

---

## 🗑️ Test Scenario 4: Victim Management - Delete Victim

### Test 4.1: Online Mode - Delete Victim

**Setup:**
- Device A: Online
- Device B: Online
- Both devices have victim "Test Victim"

**Steps:**
1. **Device A**: Open victim detail → Delete
2. **Device B**: Refresh victim list

**Expected Results:**
- ✅ Device A: Victim removed from list immediately
- ✅ Device B: After refresh, victim is gone
- ✅ Deleted from server and local DB

**Verify:**
- Check logs: Should see "Deleted victim from server and local database"
- Check database: Victim record removed

---

### Test 4.2: Offline Mode - Delete Victim

**Setup:**
- Device A: Offline
- Device B: Online

**Steps:**
1. **Device A**: (Offline) Open victim detail → Delete
2. **Device A**: Check victim list
3. **Device B**: (Online) Open same victim → Edit → Change name
4. **Device B**: Save
5. **Device A**: Go online → Open victim detail (triggers sync)

**Expected Results:**
- ✅ Device A: Victim marked for deletion locally (soft delete)
- ✅ Device A: Victim still visible but marked `PENDING_DELETE`
- ✅ Device B: Victim updated successfully
- ✅ Device A: After sync, **conflict detected**
- ✅ **Conflict resolution**: Delete cancelled (server has newer version)
- ✅ Device A: Victim restored with Device B's changes

**Verify:**
- Check logs: Should see "Conflict: Victim was edited on server after local delete"
- Check logs: Should see "Cancelled delete: Restored victim with server version"
- Check database: Victim exists with Device B's changes

---

### Test 4.3: Delete-Delete Conflict (Both Offline)

**Setup:**
- Device A: Offline
- Device B: Offline

**Steps:**
1. **Device A**: (Offline) Delete victim
2. **Device B**: (Offline) Delete same victim
3. **Device A**: Go online → Sync
4. **Device B**: Go online → Sync

**Expected Results:**
- ✅ Device A: Syncs first → Deletes on server
- ✅ Device B: Syncs second → Gets 404 (already deleted)
- ✅ Device B: Deletes locally (safe to delete)
- ✅ Both devices: Victim removed

**Verify:**
- Check logs: Device B should see "404" or "Not Found"
- Check logs: Device B should delete locally after 404
- Check database: Victim removed

---

## 📸 Test Scenario 5: Photo Management

### Test 5.1: Online Mode - Add Photo

**Setup:**
- Device A: Online
- Device B: Online

**Steps:**
1. **Device A**: Open victim detail → Add photo
2. **Device B**: Refresh victim detail

**Expected Results:**
- ✅ Device A: Photo appears immediately
- ✅ Device B: After refresh, sees new photo
- ✅ Photo synced to server

**Verify:**
- Check logs: Should see "Uploaded picture to server"

---

### Test 5.2: Offline Mode - Add Photo

**Setup:**
- Device A: Offline

**Steps:**
1. **Device A**: (Offline) Open victim detail → Add photo
2. **Device A**: Check photo appears
3. **Device A**: Go online → Open victim detail (triggers sync)

**Expected Results:**
- ✅ Device A: Photo appears immediately (from local storage)
- ✅ Device A: Photo has `PENDING_CREATE` status
- ✅ Device A: After sync, photo uploaded to server
- ✅ Photo visible on other devices after refresh

**Verify:**
- Check logs: Should see "Saved picture entity locally with PENDING_CREATE"
- Check logs: Should see "Uploaded picture to server" after going online

---

### Test 5.3: Offline Mode - Delete Photo

**Setup:**
- Device A: Offline

**Steps:**
1. **Device A**: (Offline) Open victim detail → Delete photo
2. **Device A**: Go online → Open victim detail (triggers sync)

**Expected Results:**
- ✅ Device A: Photo removed immediately (soft delete)
- ✅ Device A: After sync, photo deleted from server
- ✅ Photo removed from other devices after refresh

**Verify:**
- Check logs: Should see "Marked picture for deletion locally"
- Check logs: Should see "Deleted picture from server" after sync

---

## 🔄 Test Scenario 6: Synchronization Edge Cases

### Test 6.1: Rapid Add-Delete (Same Device)

**Setup:**
- Device A: Online

**Steps:**
1. **Device A**: Add victim "Rapid Test"
2. **Device A**: Immediately delete "Rapid Test" (before sync completes)
3. **Device A**: Refresh list

**Expected Results:**
- ✅ Victim should be deleted (both local and server)
- ✅ No orphaned records
- ✅ No crashes

**Verify:**
- Check database: No victim record exists
- Check logs: Should handle rapid operations gracefully

---

### Test 6.2: Network Interruption During Sync

**Setup:**
- Device A: Online → Offline during sync

**Steps:**
1. **Device A**: (Offline) Add victim
2. **Device A**: Go online → Open victim detail (starts sync)
3. **Device A**: Turn off network mid-sync
4. **Device A**: Go online again → Open victim detail

**Expected Results:**
- ✅ Sync retries when back online
- ✅ Victim eventually syncs successfully
- ✅ No data loss
- ✅ No duplicates

**Verify:**
- Check logs: Should see retry attempts
- Check database: Eventually one synced record

---

### Test 6.3: Duplicate Prevention

**Setup:**
- Device A: Online
- Device B: Online

**Steps:**
1. **Device A**: Add victim "Duplicate Test"
2. **Device B**: Add victim "Duplicate Test" (same NIK/name)
3. **Device A**: Refresh
4. **Device B**: Refresh

**Expected Results:**
- ✅ Both victims created (different IDs)
- ✅ No merge or conflict (different records)
- ✅ List shows both victims

**Verify:**
- Check database: Two distinct victim records
- Check logs: Both sync successfully

---

## ✅ Verification Checklist

After each test, verify:

- [ ] **No duplicates** in victim list
- [ ] **No crashes** or errors
- [ ] **Logs show** proper sync status
- [ ] **Database is clean** (no orphaned records)
- [ ] **Images display** correctly (online and offline)
- [ ] **Refresh works** (pull-to-refresh, navigation)
- [ ] **Conflicts resolved** properly (LWW strategy)
- [ ] **Offline operations** saved locally
- [ ] **Sync happens** when going online
- [ ] **UI updates** immediately after operations

---

## 🐛 Common Issues to Watch For

1. **Duplicates**: Check if both `localId` and server ID records exist
2. **Missing images**: Check if `localPath` is set correctly
3. **Sync failures**: Check network status and API responses
4. **Stale data**: Check if refresh is triggered properly
5. **Conflict errors**: Check if LWW resolution is working

---

## 📊 Expected Log Patterns

**Successful Add (Online):**
```
VictimRepo: Saved victim locally with ID: local_...
VictimRepo: Synced victim to server: [server-id]
VictimRepo: Deleted old localId record: local_...
```

**Successful Add (Offline):**
```
VictimRepo: Saved victim locally with ID: local_...
VictimRepo: Offline - victim saved locally, will sync when online
VictimRepo: Synced pending victim to server: [server-id]
```

**Conflict Resolution:**
```
SyncManager: Conflict detected: Server version is newer
SyncManager: Resolved conflict: Used server version
```

**Deduplication:**
```
VictimRepo: Removing duplicate victim with localId: [local-id]
```

---

## 🎯 Success Criteria

✅ All tests pass without crashes  
✅ No duplicate records in database  
✅ Offline operations work seamlessly  
✅ Sync happens automatically when online  
✅ Conflicts resolved correctly (LWW)  
✅ Images work in both online and offline modes  
✅ UI updates immediately after operations  
✅ Refresh works reliably  

---

## 📝 Notes

- Use **different user accounts** on each device for realistic testing
- Monitor **logcat** for both devices simultaneously
- Check **Room database** directly if needed (using Database Inspector)
- Test with **various network conditions** (slow, intermittent)
- Test with **different image sizes** (small, large)

