# 📱 Offline-First Pattern Guide

This guide explains how to implement offline-first features with Room database and server synchronization.

## 🏗️ Architecture Overview

```
UI Layer (Compose)
    ↓
ViewModel
    ↓
Repository (Offline-First)
    ├── Local Database (Room) ← Primary Source
    ├── NetworkMonitor ← Check connectivity
    ├── ImageManager ← Store images locally
    └── SyncManager ← Sync with server
        ↓
API Service (Retrofit)
```

## 📋 Implementation Pattern

### **Step 1: Create Entity & DAO**

1. **Entity** (`data/local/database/entities/YourEntity.kt`):
```kotlin
@Entity(tableName = "your_table")
data class YourEntity(
    @PrimaryKey val id: String,
    // ... your fields ...
    
    // Sync metadata (required)
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus,
    
    @ColumnInfo(name = "local_id")
    val localId: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long?
)
```

2. **DAO** (`data/local/database/dao/YourDao.kt`):
```kotlin
@Dao
interface YourDao {
    // Flow for reactive updates
    @Query("SELECT * FROM your_table WHERE ...")
    fun getItems(): Flow<List<YourEntity>>
    
    // Get pending sync items
    @Query("SELECT * FROM your_table WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncItems(): List<YourEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: YourEntity)
    
    @Update
    suspend fun updateItem(item: YourEntity)
    
    @Query("DELETE FROM your_table WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("UPDATE your_table SET sync_status = :status, last_synced_at = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long)
}
```

### **Step 2: Create Mapper**

Create `data/mapper/YourMapper.kt`:
```kotlin
object YourMapper {
    // Entity → Model
    fun YourEntity.toModel(): YourModel { ... }
    
    // Model → Entity
    fun YourModel.toEntity(
        syncStatus: SyncStatus,
        localId: String?,
        createdAt: Long,
        updatedAt: Long,
        lastSyncedAt: Long?
    ): YourEntity { ... }
    
    // DTO → Entity
    fun YourDto.toEntity(
        syncStatus: SyncStatus = SyncStatus.SYNCED,
        lastSyncedAt: Long? = null
    ): YourEntity { ... }
    
    // DTO → Model
    fun YourDto.toModel(): YourModel { ... }
    
    // Entity → DTO (for syncing)
    fun YourEntity.toDto(): YourDto { ... }
}
```

### **Step 3: Update Repository (Offline-First Pattern)**

```kotlin
@Singleton
class YourRepository @Inject constructor(
    private val apiService: YourApiService,
    private val dao: YourDao,
    private val networkMonitor: NetworkMonitor,
    private val imageManager: ImageManager, // If handling images
    private val pictureDao: PictureDao // If handling images
) {
    
    // READ: Always from local first
    suspend fun getItems(): List<YourModel> {
        // 1. Get from local database
        val localEntities = dao.getItems().first()
        if (localEntities.isNotEmpty()) {
            // 2. Sync in background if online
            if (networkMonitor.isOnline()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        syncManager.syncYourItems()
                    } catch (e: Exception) {
                        Log.e("YourRepo", "Background sync failed", e)
                    }
                }
            }
            return localEntities.map { with(YourMapper) { it.toModel() } }
        }
        
        // 3. If no local data and online, fetch from API
        if (networkMonitor.isOnline()) {
            try {
                val response = apiService.getItems()
                val entities = response.data.map { dto ->
                    with(YourMapper) { dto.toEntity(syncStatus = SyncStatus.SYNCED) }
                }
                dao.insertItems(entities)
                return entities.map { with(YourMapper) { it.toModel() } }
            } catch (e: Exception) {
                Log.e("YourRepo", "Failed to fetch from API", e)
            }
        }
        
        return emptyList()
    }
    
    // CREATE: Save locally first, then sync
    suspend fun addItem(item: YourModel, context: Context): YourResponse {
        val now = System.currentTimeMillis()
        val localId = "local_${now}_${item.id}"
        
        // 1. Save images locally (if any)
        val savedImagePaths = mutableListOf<String>()
        item.images?.forEachIndexed { index, uri ->
            val imageId = "${localId}_img_$index"
            val localPath = imageManager.saveImage(uri, imageId)
            localPath?.let { savedImagePaths.add(it) }
            
            // Save picture entity
            val pictureEntity = PictureEntity(
                id = imageId,
                foreignId = localId,
                type = "your_type",
                localPath = localPath,
                syncStatus = SyncStatus.PENDING_CREATE,
                // ... other fields
            )
            pictureDao.insertPicture(pictureEntity)
        }
        
        // 2. Save entity locally with PENDING_CREATE
        val entity = with(YourMapper) {
            item.toEntity(
                syncStatus = SyncStatus.PENDING_CREATE,
                localId = localId,
                createdAt = now,
                updatedAt = now,
                lastSyncedAt = null
            )
        }
        dao.insertItem(entity)
        
        // 3. Try to upload to server if online
        if (networkMonitor.isOnline()) {
            try {
                val response = apiService.addItem(/* ... */)
                
                // Update local entity with server response
                response.data?.let { dto ->
                    val updatedEntity = with(YourMapper) {
                        dto.toEntity(syncStatus = SyncStatus.SYNCED)
                    }
                    dao.insertItem(updatedEntity)
                    
                    // Update picture entities with server IDs
                    // ...
                }
                
                return response
            } catch (e: Exception) {
                Log.e("YourRepo", "Failed to upload, will sync later", e)
            }
        }
        
        // Return mock response for offline
        return YourResponse(
            message = "Item saved locally. Will sync when online.",
            data = null
        )
    }
    
    // UPDATE: Update locally first, then sync
    suspend fun updateItem(id: String, item: YourModel): YourResponse {
        // 1. Update local entity with PENDING_UPDATE
        val existing = dao.getItemById(id)
        val updated = existing?.copy(
            // ... update fields ...
            syncStatus = SyncStatus.PENDING_UPDATE,
            updatedAt = System.currentTimeMillis()
        )
        updated?.let { dao.updateItem(it) }
        
        // 2. Try to update on server if online
        if (networkMonitor.isOnline()) {
            try {
                val response = apiService.updateItem(id, /* ... */)
                
                // Update sync status
                dao.updateSyncStatus(id, SyncStatus.SYNCED, System.currentTimeMillis())
                
                return response
            } catch (e: Exception) {
                Log.e("YourRepo", "Failed to update on server", e)
            }
        }
        
        return YourResponse(message = "Updated locally. Will sync when online.", data = null)
    }
    
    // DELETE: Mark as PENDING_DELETE, then sync
    suspend fun deleteItem(id: String) {
        // 1. Mark as PENDING_DELETE locally
        val existing = dao.getItemById(id)
        existing?.let {
            val deleted = it.copy(
                syncStatus = SyncStatus.PENDING_DELETE,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateItem(deleted)
        }
        
        // 2. Try to delete on server if online
        if (networkMonitor.isOnline()) {
            try {
                apiService.deleteItem(id)
                dao.deleteById(id) // Remove from local DB
            } catch (e: Exception) {
                Log.e("YourRepo", "Failed to delete on server", e)
            }
        }
    }
}
```

### **Step 4: Add Sync Logic to SyncManager**

Update `data/local/sync/SyncManager.kt`:
```kotlin
suspend fun syncYourItems() {
    if (!networkMonitor.isOnline()) return
    
    try {
        // Push pending items
        val pending = yourDao.getPendingSyncItems()
        for (item in pending) {
            when (item.syncStatus) {
                SyncStatus.PENDING_CREATE -> {
                    // Upload to server
                    // Update local with server ID
                }
                SyncStatus.PENDING_UPDATE -> {
                    // Update on server
                    // Update sync status
                }
                SyncStatus.PENDING_DELETE -> {
                    // Delete on server
                    // Remove from local DB
                }
                else -> {}
            }
        }
        
        // Pull latest from server
        val response = apiService.getItems()
        val entities = response.data.map { dto ->
            with(YourMapper) { dto.toEntity(syncStatus = SyncStatus.SYNCED) }
        }
        yourDao.insertItems(entities)
    } catch (e: Exception) {
        Log.e("SyncManager", "Error syncing items", e)
    }
}
```

### **Step 5: Update AppModule**

Add providers in `di/AppModule.kt`:
```kotlin
@Provides
@Singleton
fun provideYourRepository(
    apiService: YourApiService,
    dao: YourDao,
    networkMonitor: NetworkMonitor,
    imageManager: ImageManager, // If needed
    pictureDao: PictureDao // If needed
): YourRepository {
    return YourRepository(apiService, dao, networkMonitor, imageManager, pictureDao)
}
```

## 🖼️ Image Handling Pattern

### **Saving Images Locally**
```kotlin
// Save image from URI
val imageId = "victim_${victimId}_img_${index}"
val localPath = imageManager.saveImage(uri, imageId)

// Save picture entity
val pictureEntity = PictureEntity(
    id = imageId,
    foreignId = victimId,
    type = "victim",
    localPath = localPath,
    syncStatus = SyncStatus.PENDING_CREATE,
    // ...
)
pictureDao.insertPicture(pictureEntity)
```

### **Loading Images**
```kotlin
// Check if image exists locally
val localPath = imageManager.getImagePath(imageId)
if (localPath != null) {
    // Use local image
    AsyncImage(model = Uri.fromFile(File(localPath)), ...)
} else {
    // Download and cache
    imageManager.saveImageFromUrl(serverUrl, imageId)
    // Then use local path
}
```

## 🔄 Sync Strategy

### **Automatic Sync**
- Sync happens automatically when:
  - App comes online (NetworkMonitor detects connectivity)
  - User performs actions (background sync)
  - Periodic sync (can be added with WorkManager)

### **Manual Sync**
```kotlin
// In ViewModel or Repository
viewModelScope.launch {
    syncManager.syncAll()
}
```

## ✅ Checklist for New Features

- [ ] Create Entity with sync metadata
- [ ] Create DAO with Flow queries
- [ ] Create Mapper (Entity ↔ Model ↔ DTO)
- [ ] Update Repository with offline-first pattern
- [ ] Handle images with ImageManager (if needed)
- [ ] Add sync logic to SyncManager
- [ ] Update AppModule with dependencies
- [ ] Test offline operations
- [ ] Test sync when coming online

## 📝 Notes

1. **Always read from local first** - This ensures instant UI updates
2. **Save locally immediately** - Don't wait for server response
3. **Sync in background** - Don't block UI with sync operations
4. **Handle errors gracefully** - Show user-friendly messages
5. **Use Flow for reactive updates** - UI automatically updates when data changes

## 🎯 Example: Complete Feature Implementation

See `DisasterVictimRepository.kt` for a complete example of:
- Offline-first reads
- Local-first writes
- Image storage
- Background sync
- Error handling

