package com.example.e_disaster.data.local.database

enum class SyncStatus {
    SYNCED,           // Successfully synced with server
    PENDING_CREATE,   // Created offline, waiting to sync
    PENDING_UPDATE,   // Updated offline, waiting to sync
    PENDING_DELETE,   // Deleted offline, waiting to sync
    SYNC_FAILED       // Sync attempt failed
}

enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE
}