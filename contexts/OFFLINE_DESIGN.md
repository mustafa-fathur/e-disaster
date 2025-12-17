# 📱 Offline Support Design: Room Database Implementation

## 🎯 Overview

This document outlines the design for implementing offline support in the e-Disaster Android app using Room database. The goal is to allow users to access and manage disaster data even when offline, with automatic synchronization when connectivity is restored.

---

## 📁 Proposed Folder Structure

```
app/src/main/java/com/example/e_disaster/
├── data/
│   ├── local/
│   │   ├── database/                    # Room Database
│   │   │   ├── EDisasterDatabase.kt      # Main database class
│   │   │   ├── entities/                 # Room Entities
│   │   │   │   ├── DisasterEntity.kt
│   │   │   │   ├── DisasterReportEntity.kt
│   │   │   │   ├── DisasterVictimEntity.kt
│   │   │   │   ├── DisasterAidEntity.kt
│   │   │   │   ├── DisasterVolunteerEntity.kt
│   │   │   │   ├── PictureEntity.kt
│   │   │   │   ├── NotificationEntity.kt
│   │   │   │   ├── UserEntity.kt
│   │   │   │   └── SyncStatusEntity.kt   # Track sync status
│   │   │   ├── dao/                      # Data Access Objects
│   │   │   │   ├── DisasterDao.kt
│   │   │   │   ├── DisasterReportDao.kt
│   │   │   │   ├── DisasterVictimDao.kt
│   │   │   │   ├── DisasterAidDao.kt
│   │   │   │   ├── DisasterVolunteerDao.kt
│   │   │   │   ├── PictureDao.kt
│   │   │   │   ├── NotificationDao.kt
│   │   │   │   ├── UserDao.kt
│   │   │   │   └── SyncStatusDao.kt
│   │   │   └── converters/              # Type converters
│   │   │       ├── DateConverters.kt
│   │   │       └── EnumConverters.kt
│   │   ├── sync/                        # Synchronization logic
│   │   │   ├── SyncManager.kt            # Main sync orchestrator
│   │   │   ├── SyncWorker.kt            # WorkManager worker
│   │   │   ├── ConflictResolver.kt      # Handle sync conflicts
│   │   │   └── NetworkMonitor.kt        # Monitor connectivity
│   │   └── UserPreferences.kt           # (Existing - keep as is)
│   │
│   ├── model/                           # (Existing - keep as is)
│   │   ├── Disaster.kt
│   │   ├── User.kt
│   │   └── ...
│   │
│   ├── mapper/                          # NEW: Map between Entity ↔ Model ↔ DTO
│   │   ├── DisasterMapper.kt
│   │   ├── UserMapper.kt
│   │   ├── DisasterReportMapper.kt
│   │   ├── DisasterVictimMapper.kt
│   │   └── DisasterAidMapper.kt
│   │
│   ├── remote/                          # (Existing - keep as is)
│   │   ├── dto/
│   │   └── service/
│   │
│   └── repository/                      # UPDATED: Add local data source
│       ├── DisasterRepository.kt       # Updated to use local + remote
│       ├── AuthRepository.kt            # Updated for offline auth
│       └── ...
```

---

## 🗄️ Room Database Design

### **1. Database Schema**

```kotlin
@Database(
    entities = [
        UserEntity::class,
        DisasterEntity::class,
        DisasterVolunteerEntity::class,
        DisasterReportEntity::class,
        DisasterVictimEntity::class,
        DisasterAidEntity::class,
        PictureEntity::class,
        NotificationEntity::class,
        SyncStatusEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class, EnumConverters::class)
abstract class EDisasterDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun disasterDao(): DisasterDao
    abstract fun disasterVolunteerDao(): DisasterVolunteerDao
    abstract fun disasterReportDao(): DisasterReportDao
    abstract fun disasterVictimDao(): DisasterVictimDao
    abstract fun disasterAidDao(): DisasterAidDao
    abstract fun pictureDao(): PictureDao
    abstract fun notificationDao(): NotificationDao
    abstract fun syncStatusDao(): SyncStatusDao
}
```

### **2. Entity Design**

#### **Key Design Decisions:**

1. **Sync Flags**: Each entity needs flags to track sync status
2. **Local IDs**: Temporary IDs for offline-created records
3. **Timestamps**: Track creation/update times for conflict resolution
4. **Relationships**: Use foreign keys with proper cascading

#### **Example: DisasterEntity**

```kotlin
@Entity(
    tableName = "disasters",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["status"]),
        Index(value = ["sync_status"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["reported_by"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DisasterEntity(
    @PrimaryKey
    val id: String,
    
    // Core fields
    val reportedBy: String?,
    val source: String, // "BMKG" or "manual"
    val types: String, // disaster type enum
    val status: String, // disaster status enum
    val title: String,
    val description: String?,
    val date: String?,
    val time: String?,
    val location: String?,
    val coordinate: String?,
    val lat: Double?,
    val long: Double?,
    val magnitude: Double?,
    val depth: Double?,
    
    // Sync metadata
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    
    @ColumnInfo(name = "local_id")
    val localId: String? = null, // For offline-created records
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)

enum class SyncStatus {
    SYNCED,           // Successfully synced with server
    PENDING_CREATE,   // Created offline, waiting to sync
    PENDING_UPDATE,   // Updated offline, waiting to sync
    PENDING_DELETE,   // Deleted offline, waiting to sync
    SYNC_FAILED       // Sync attempt failed
}
```

#### **SyncStatusEntity** (Track pending operations)

```kotlin
@Entity(tableName = "sync_status")
data class SyncStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "entity_type")
    val entityType: String, // "disaster", "report", "victim", "aid"
    
    @ColumnInfo(name = "entity_id")
    val entityId: String,
    
    @ColumnInfo(name = "operation")
    val operation: SyncOperation, // CREATE, UPDATE, DELETE
    
    @ColumnInfo(name = "local_id")
    val localId: String? = null,
    
    @ColumnInfo(name = "server_id")
    val serverId: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "retry_count")
    val retryCount: Int = 0,
    
    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null
)

enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE
}
```

### **3. DAO Design Pattern**

#### **Example: DisasterDao**

```kotlin
@Dao
interface DisasterDao {
    // Query operations
    @Query("SELECT * FROM disasters ORDER BY updated_at DESC")
    fun getAllDisasters(): Flow<List<DisasterEntity>>
    
    @Query("SELECT * FROM disasters WHERE id = :id")
    suspend fun getDisasterById(id: String): DisasterEntity?
    
    @Query("SELECT * FROM disasters WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncDisasters(): List<DisasterEntity>
    
    @Query("SELECT * FROM disasters WHERE status = :status")
    fun getDisastersByStatus(status: String): Flow<List<DisasterEntity>>
    
    // Insert/Update operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisaster(disaster: DisasterEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisasters(disasters: List<DisasterEntity>)
    
    @Update
    suspend fun updateDisaster(disaster: DisasterEntity)
    
    @Delete
    suspend fun deleteDisaster(disaster: DisasterEntity)
    
    // Sync operations
    @Query("UPDATE disasters SET sync_status = :status, last_synced_at = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long)
    
    @Query("SELECT * FROM disasters WHERE local_id = :localId")
    suspend fun getDisasterByLocalId(localId: String): DisasterEntity?
}
```

---

## 🔄 Repository Pattern Update

### **Single Source of Truth Pattern**

The repository will act as the single source of truth, deciding between local and remote data:

```kotlin
@Singleton
class DisasterRepository @Inject constructor(
    private val apiService: DisasterApiService,
    private val disasterDao: DisasterDao,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager
) {
    
    // Strategy: Always read from local first, sync in background
    fun getDisasters(): Flow<List<Disaster>> {
        return disasterDao.getAllDisasters()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)
            .onEach { 
                // Sync in background if online
                if (networkMonitor.isOnline()) {
                    syncManager.syncDisasters()
                }
            }
    }
    
    suspend fun getDisasterById(id: String): Disaster {
        // Try local first
        val localEntity = disasterDao.getDisasterById(id)
        if (localEntity != null) {
            // Sync in background if online
            if (networkMonitor.isOnline()) {
                syncManager.syncDisaster(id)
            }
            return localEntity.toModel()
        }
        
        // If not in local DB and online, fetch from API
        if (networkMonitor.isOnline()) {
            val dto = apiService.getDisasterById(id)
            val entity = dto.toEntity()
            disasterDao.insertDisaster(entity)
            return entity.toModel()
        }
        
        throw NoLocalDataException("Disaster not found locally and offline")
    }
    
    suspend fun createDisaster(disaster: Disaster): Disaster {
        val localId = UUID.randomUUID().toString()
        val entity = disaster.toEntity(
            localId = localId,
            syncStatus = if (networkMonitor.isOnline()) {
                SyncStatus.SYNCED
            } else {
                SyncStatus.PENDING_CREATE
            }
        )
        
        // Save locally first
        disasterDao.insertDisaster(entity)
        
        // Try to sync immediately if online
        if (networkMonitor.isOnline()) {
            try {
                val response = apiService.createDisaster(disaster.toDto())
                // Update with server ID
                val syncedEntity = entity.copy(
                    id = response.id,
                    localId = null,
                    syncStatus = SyncStatus.SYNCED,
                    lastSyncedAt = System.currentTimeMillis()
                )
                disasterDao.updateDisaster(syncedEntity)
                return syncedEntity.toModel()
            } catch (e: Exception) {
                // Mark as failed, will retry later
                disasterDao.updateSyncStatus(
                    localId,
                    SyncStatus.SYNC_FAILED,
                    System.currentTimeMillis()
                )
                throw e
            }
        }
        
        // Queue for sync
        syncManager.queueForSync(
            entityType = "disaster",
            entityId = localId,
            operation = SyncOperation.CREATE
        )
        
        return entity.toModel()
    }
    
    suspend fun updateDisaster(disaster: Disaster): Disaster {
        val entity = disaster.toEntity(
            syncStatus = if (networkMonitor.isOnline()) {
                SyncStatus.SYNCED
            } else {
                SyncStatus.PENDING_UPDATE
            },
            updatedAt = System.currentTimeMillis()
        )
        
        disasterDao.updateDisaster(entity)
        
        if (networkMonitor.isOnline()) {
            try {
                apiService.updateDisaster(disaster.id, disaster.toDto())
                disasterDao.updateSyncStatus(
                    disaster.id,
                    SyncStatus.SYNCED,
                    System.currentTimeMillis()
                )
            } catch (e: Exception) {
                disasterDao.updateSyncStatus(
                    disaster.id,
                    SyncStatus.SYNC_FAILED,
                    System.currentTimeMillis()
                )
                throw e
            }
        } else {
            syncManager.queueForSync(
                entityType = "disaster",
                entityId = disaster.id,
                operation = SyncOperation.UPDATE
            )
        }
        
        return entity.toModel()
    }
}
```

---

## 🔐 Authentication Offline Strategy

### **Recommendation: Token-Based Offline Access**

**YES, allow offline access if token exists**, but with limitations:

#### **Implementation:**

```kotlin
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val userDao: UserDao,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.LOADING)
    val authState = _authState.asStateFlow()

    init {
        checkUserLoginState()
    }

    private fun checkUserLoginState() {
        viewModelScope.launch {
            val token = userPreferences.authToken.first()
            
            if (!token.isNullOrBlank()) {
                // Token exists - check if we have user data locally
                val localUser = userDao.getCurrentUser()
                
                if (localUser != null) {
                    // We have local user data - allow offline access
                    _authState.value = AuthState.AUTHENTICATED
                    
                    // Try to refresh user data in background if online
                    if (networkMonitor.isOnline()) {
                        try {
                            refreshUserProfile(token)
                        } catch (e: Exception) {
                            // Continue with local data
                            Log.w("Splash", "Failed to refresh profile: ${e.message}")
                        }
                    }
                } else {
                    // Token exists but no local user data
                    if (networkMonitor.isOnline()) {
                        // Try to fetch user profile
                        try {
                            refreshUserProfile(token)
                            _authState.value = AuthState.AUTHENTICATED
                        } catch (e: Exception) {
                            // Token might be invalid
                            userPreferences.clearAll()
                            _authState.value = AuthState.UNAUTHENTICATED
                        }
                    } else {
                        // Offline and no local user - require login
                        _authState.value = AuthState.UNAUTHENTICATED
                    }
                }
            } else {
                _authState.value = AuthState.UNAUTHENTICATED
            }
        }
    }
    
    private suspend fun refreshUserProfile(token: String) {
        // Fetch from API and save to local DB
        // Implementation in AuthRepository
    }
}
```

#### **Offline Access Limitations:**

1. **Read-Only for Unassigned Disasters**: Can view disasters but cannot join
2. **Assigned Disasters Only**: Can only manage disasters user is already assigned to
3. **No New Assignments**: Cannot join new disasters offline
4. **Token Validation**: On next online session, validate token with server

---

## 🔄 Synchronization Strategy

### **1. Sync Architecture**

```
┌─────────────────────────────────────────────────────────┐
│                    Sync Manager                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   Conflict   │  │   Network    │  │   WorkManager│ │
│  │   Resolver   │  │   Monitor    │  │   Worker     │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
         ↓                    ↓                    ↓
┌─────────────────────────────────────────────────────────┐
│              Room Database (Local)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   Entities   │  │  Sync Status │  │   Pending    │ │
│  │   (Data)     │  │   (Metadata) │  │   Operations │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### **2. Sync Triggers**

1. **App Startup**: Sync all pending operations
2. **Network Reconnection**: Automatic sync when network restored
3. **Manual Refresh**: User-initiated pull-to-refresh
4. **Periodic Sync**: WorkManager periodic sync (every 15-30 minutes)
5. **After CRUD Operations**: Immediate sync attempt if online

### **3. Sync Process Flow**

#### **Pull Sync (Server → Local)**

```kotlin
suspend fun syncDisasters() {
    if (!networkMonitor.isOnline()) return
    
    try {
        // Fetch from API
        val response = apiService.getDisasters()
        val serverDisasters = response.data.map { it.toEntity() }
        
        // Get local disasters
        val localDisasters = disasterDao.getAllDisasters().first()
        
        // Merge strategy: Server wins for conflicts
        serverDisasters.forEach { serverEntity ->
            val localEntity = localDisasters.find { it.id == serverEntity.id }
            
            when {
                // New from server
                localEntity == null -> {
                    disasterDao.insertDisaster(serverEntity)
                }
                // Conflict resolution
                localEntity.syncStatus == SyncStatus.PENDING_UPDATE -> {
                    // Check timestamps - server wins if newer
                    if (serverEntity.updatedAt > localEntity.updatedAt) {
                        disasterDao.insertDisaster(serverEntity)
                    } else {
                        // Keep local, will push later
                        // Could show conflict dialog to user
                    }
                }
                // Local is synced, update if server is newer
                localEntity.syncStatus == SyncStatus.SYNCED -> {
                    if (serverEntity.updatedAt > localEntity.updatedAt) {
                        disasterDao.insertDisaster(serverEntity)
                    }
                }
            }
        }
        
        // Mark as synced
        serverDisasters.forEach {
            disasterDao.updateSyncStatus(
                it.id,
                SyncStatus.SYNCED,
                System.currentTimeMillis()
            )
        }
    } catch (e: Exception) {
        Log.e("Sync", "Failed to sync disasters: ${e.message}")
    }
}
```

#### **Push Sync (Local → Server)**

```kotlin
suspend fun syncPendingOperations() {
    if (!networkMonitor.isOnline()) return
    
    val pendingOperations = syncStatusDao.getPendingOperations()
    
    pendingOperations.forEach { syncStatus ->
        try {
            when (syncStatus.operation) {
                SyncOperation.CREATE -> {
                    val entity = getEntityByType(syncStatus.entityType, syncStatus.localId)
                    val response = createOnServer(entity)
                    // Update entity with server ID
                    updateEntityWithServerId(syncStatus.entityType, syncStatus.localId, response.id)
                }
                SyncOperation.UPDATE -> {
                    val entity = getEntityByType(syncStatus.entityType, syncStatus.entityId)
                    updateOnServer(entity)
                }
                SyncOperation.DELETE -> {
                    deleteOnServer(syncStatus.entityType, syncStatus.entityId)
                }
            }
            
            // Mark as synced
            syncStatusDao.delete(syncStatus)
            updateEntitySyncStatus(syncStatus.entityType, syncStatus.entityId, SyncStatus.SYNCED)
            
        } catch (e: Exception) {
            // Increment retry count
            syncStatusDao.incrementRetryCount(syncStatus.id)
            
            if (syncStatus.retryCount > MAX_RETRIES) {
                // Mark as failed, notify user
                syncStatusDao.markAsFailed(syncStatus.id, e.message)
            }
        }
    }
}
```

### **4. Conflict Resolution Strategy**

**Recommended: Last-Write-Wins with Timestamp**

1. **Server Timestamp > Local Timestamp**: Server wins
2. **Local Timestamp > Server Timestamp**: Keep local, push to server
3. **Equal Timestamps**: Server wins (single source of truth)

**Alternative: User Intervention**
- Show conflict dialog
- Let user choose which version to keep
- More complex but better UX

### **5. WorkManager Integration**

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val syncManager = // Inject via Hilt
            syncManager.syncAll()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

// Schedule periodic sync
val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
    15, TimeUnit.MINUTES
).build()

WorkManager.getInstance(context).enqueue(syncRequest)
```

---

## 🖼️ Image Storage Strategy

### **Approach: Hybrid Storage**

1. **Local Images**: Store in app's internal/external storage
2. **Database**: Store file paths in Room
3. **Sync**: Upload images when online, download thumbnails

### **Implementation:**

```kotlin
@Entity(tableName = "pictures")
data class PictureEntity(
    @PrimaryKey
    val id: String,
    
    val foreignId: String, // disaster_id, report_id, etc.
    val type: String, // "disaster", "report", "victim", "aid"
    val caption: String?,
    
    // Local file path
    @ColumnInfo(name = "local_path")
    val localPath: String?,
    
    // Server URL (after upload)
    @ColumnInfo(name = "server_url")
    val serverUrl: String?,
    
    // Sync status
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING_CREATE,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

// Image Manager
class ImageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val imagesDir = File(context.filesDir, "images")
    
    init {
        imagesDir.mkdirs()
    }
    
    suspend fun saveImage(uri: Uri, foreignId: String, type: String): String {
        // Copy image to app storage
        val fileName = "${type}_${UUID.randomUUID()}.jpg"
        val file = File(imagesDir, fileName)
        
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        return file.absolutePath
    }
    
    suspend fun uploadImage(localPath: String, foreignId: String, type: String): String {
        val file = File(localPath)
        val requestFile = RequestBody.create(
            MediaType.parse("image/jpeg"),
            file
        )
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        
        val response = pictureApiService.uploadPicture(type, foreignId, body)
        return response.url // Server URL
    }
    
    fun getImageUri(localPath: String?): Uri? {
        return localPath?.let { File(it).toUri() }
    }
}
```

### **Image Sync Flow:**

1. **User selects image** → Save to local storage
2. **Create PictureEntity** with `localPath`, `syncStatus = PENDING_CREATE`
3. **When online** → Upload to server
4. **Update entity** with `serverUrl`, `syncStatus = SYNCED`
5. **Display**: Prefer `serverUrl`, fallback to `localPath`

---

## 👥 Volunteer Assignment Offline Logic

### **Recommendation: Restrict New Assignments Offline**

**Strategy:**

1. **Can View All Disasters**: User can browse all disasters offline
2. **Can Only Manage Assigned Disasters**: Can only create/edit reports/victims/aids for disasters user is already assigned to
3. **Cannot Join New Disasters**: Disable "Join Disaster" button when offline
4. **Queue Assignment Request**: If user tries to join offline, show message: "You'll be assigned when connection is restored"

### **Implementation:**

```kotlin
// In DisasterDetailViewModel
fun canManageDisaster(disasterId: String): Flow<Boolean> {
    return combine(
        disasterVolunteerDao.isUserAssigned(disasterId, currentUserId),
        networkMonitor.isOnlineFlow()
    ) { isAssigned, isOnline ->
        // Can manage if:
        // 1. User is assigned AND (online OR was assigned before going offline)
        isAssigned && (isOnline || wasAssignedBeforeOffline(disasterId))
    }
}

fun canJoinDisaster(disasterId: String): Flow<Boolean> {
    return networkMonitor.isOnlineFlow().map { isOnline ->
        // Can only join when online
        isOnline
    }
}

// Store assignment requests for offline
suspend fun queueAssignmentRequest(disasterId: String) {
    if (!networkMonitor.isOnline()) {
        // Save to local queue
        assignmentRequestDao.insert(
            AssignmentRequestEntity(
                disasterId = disasterId,
                userId = currentUserId,
                status = RequestStatus.PENDING
            )
        )
        
        // Show message to user
        _uiState.value = _uiState.value.copy(
            message = "Assignment request queued. You'll be assigned when connection is restored."
        )
    }
}
```

### **UI Behavior:**

```kotlin
@Composable
fun DisasterDetailContent(
    isAssigned: Boolean,
    isOnline: Boolean,
    onJoinClick: () -> Unit
) {
    if (!isAssigned) {
        if (isOnline) {
            Button(onClick = onJoinClick) {
                Text("Join Disaster")
            }
        } else {
            Button(
                onClick = { /* Show offline message */ },
                enabled = false
            ) {
                Text("Join Disaster (Offline)")
            }
            Text(
                "You need internet connection to join a disaster",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    } else {
        // Show action buttons (Add Report, Add Victim, etc.)
        SpeedDialFab(...)
    }
}
```

---

## 📋 Implementation Checklist

### **Phase 1: Database Setup**
- [ ] Add Room dependencies to `build.gradle.kts`
- [ ] Create database class (`EDisasterDatabase.kt`)
- [ ] Create all entity classes
- [ ] Create all DAO interfaces
- [ ] Create type converters
- [ ] Create mapper classes (Entity ↔ Model ↔ DTO)

### **Phase 2: Repository Updates**
- [ ] Update `DisasterRepository` to use local + remote
- [ ] Update `AuthRepository` for offline auth
- [ ] Update `DisasterReportRepository`
- [ ] Update `DisasterVictimRepository`
- [ ] Update `DisasterAidRepository`

### **Phase 3: Sync Infrastructure**
- [ ] Create `NetworkMonitor`
- [ ] Create `SyncManager`
- [ ] Create `ConflictResolver`
- [ ] Create `SyncWorker` (WorkManager)
- [ ] Implement pull sync
- [ ] Implement push sync

### **Phase 4: Image Management**
- [ ] Create `ImageManager`
- [ ] Update `PictureEntity`
- [ ] Implement local image storage
- [ ] Implement image upload sync
- [ ] Update UI to use local images

### **Phase 5: Offline UI Logic**
- [ ] Update `SplashViewModel` for offline auth
- [ ] Update `DisasterDetailViewModel` for assignment logic
- [ ] Add offline indicators in UI
- [ ] Add sync status indicators
- [ ] Handle offline error messages

### **Phase 6: Testing**
- [ ] Test offline CRUD operations
- [ ] Test sync on reconnect
- [ ] Test conflict resolution
- [ ] Test image storage/upload
- [ ] Test volunteer assignment logic

---

## 🎯 Key Design Decisions Summary

1. **Offline Access**: ✅ Allow if token exists + local user data
2. **Sync Strategy**: Pull on app start + periodic, push on network reconnect
3. **Conflict Resolution**: Last-write-wins with timestamp comparison
4. **Image Storage**: Local file system + Room for metadata
5. **Volunteer Assignment**: Restrict new assignments offline, allow management of existing assignments
6. **Repository Pattern**: Single source of truth, local-first with background sync

---

## ⚠️ Considerations & Edge Cases

1. **Token Expiration**: Validate token on next online session
2. **Large Data Sets**: Implement pagination for local queries
3. **Storage Limits**: Monitor local storage usage, implement cleanup
4. **Sync Failures**: Retry mechanism with exponential backoff
5. **Partial Sync**: Handle cases where some entities sync but others fail
6. **User Experience**: Clear indicators for offline mode and sync status

---

This design provides a solid foundation for implementing offline support. The key is to start with Phase 1 (database setup) and gradually build up the sync infrastructure.
