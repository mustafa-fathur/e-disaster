# 📋 Dokumentasi Fungsi Bantuan dan Logistik

## 🎯 **Fitur yang Telah Diimplementasi**

### **1. Data Models**

-   `DisasterAid.kt` - Model data untuk bantuan bencana
-   `CreateDisasterAidRequest.kt` - Request untuk membuat bantuan baru
-   `UpdateDisasterAidRequest.kt` - Request untuk update bantuan
-   Enum classes untuk tipe dan status bantuan

### **2. API Integration**

-   `DisasterAidApi.kt` - Interface Retrofit untuk komunikasi dengan Laravel API
-   `ApiClient.kt` - Konfigurasi Retrofit dengan authentication
-   Support untuk filtering berdasarkan lokasi, status, dan tipe

### **3. Repository Pattern**

-   `DisasterAidRepository.kt` - Mengelola data dari API
-   Error handling dan state management
-   Perhitungan jarak menggunakan formula Haversine

### **4. State Management**

-   `DisasterAidViewModel.kt` - ViewModel untuk state management
-   Support untuk filtering dan pencarian
-   Reactive state dengan StateFlow

### **5. UI Components**

-   `DisasterAidListScreen.kt` - Menampilkan daftar bantuan untuk bencana tertentu
-   `NearbyAidsScreen.kt` - Menampilkan bantuan di sekitar lokasi pengguna
-   Filter chips untuk status, tipe, dan radius
-   Loading states dan error handling

## 🚀 **Cara Penggunaan**

### **1. Menampilkan Bantuan untuk Bencana Tertentu**

```kotlin
// Di dalam screen atau composable
@Composable
fun DisasterDetailScreen(
    navController: NavController,
    disasterId: String?,
    viewModel: DisasterAidViewModel = viewModel()
) {
    val disasterAids by viewModel.disasterAids.collectAsState()

    LaunchedEffect(disasterId) {
        disasterId?.let {
            // Load bantuan untuk bencana ini
            viewModel.loadDisasterAids(it)
        }
    }

    // UI untuk menampilkan daftar bantuan
    when (disasterAids) {
        is Resource.Success -> {
            AidList(aids = (disasterAids as Resource.Success).data)
        }
        // Handle other states...
    }
}
```

### **2. Menampilkan Bantuan di Sekitar Lokasi**

```kotlin
@Composable
fun NearbyAidsScreen(
    navController: NavController,
    latitude: Double,
    longitude: Double,
    viewModel: DisasterAidViewModel = viewModel()
) {
    val nearbyAids by viewModel.nearbyAids.collectAsState()

    LaunchedEffect(latitude, longitude) {
        viewModel.loadNearbyAids(latitude, longitude)
    }

    // UI untuk menampilkan bantuan terdekat
    when (nearbyAids) {
        is Resource.Success -> {
            NearbyAidList(aids = (nearbyAids as Resource.Success).data)
        }
        // Handle other states...
    }
}
```

### **3. Filtering dan Pencarian**

```kotlin
// Set filter status
viewModel.setStatusFilter(AidStatus.TERSEDIA)

// Set filter tipe bantuan
viewModel.setTypeFilter(AidType.MAKANAN)

// Set radius pencarian (dalam km)
viewModel.setRadius(5.0)

// Reset semua filter
viewModel.resetFilters()
```

### **4. CRUD Operations**

```kotlin
// Membuat bantuan baru
viewModel.createDisasterAid(
    disasterId = "disaster-id",
    name = "Beras 10kg",
    type = "makanan",
    quantity = 100,
    unit = "kg",
    location = "Gudang A",
    latitude = -6.2088,
    longitude = 106.8456,
    description = "Beras premium untuk korban bencana"
)

// Update bantuan
viewModel.updateDisasterAid(
    disasterId = "disaster-id",
    aidId = "aid-id",
    status = "dalam_perjalanan"
)

// Hapus bantuan
viewModel.deleteDisasterAid("disaster-id", "aid-id")
```

## 🔧 **Konfigurasi yang Diperlukan**

### **1. Base URL API**

Edit `ApiClient.kt`:

```kotlin
private const val BASE_URL = "https://your-laravel-api.com/api/"
```

### **2. Authentication Token**

Implementasi di `ApiClient.kt`:

```kotlin
private fun getAuthToken(): String? {
    return SharedPreferencesManager.getAuthToken() // Implementasi sesuai kebutuhan
}
```

### **3. Location Services**

Untuk mendapatkan lokasi pengguna:

```kotlin
// Di dalam Activity atau ViewModel
val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

fusedLocationClient.lastLocation.addOnSuccessListener { location ->
    location?.let {
        val latitude = it.latitude
        val longitude = it.longitude
        viewModel.loadNearbyAids(latitude, longitude)
    }
}
```

## 📱 **Navigation Routes**

### **Routes yang Tersedia**

```kotlin
// Daftar bantuan untuk bencana tertentu
"disaster-aid-list/{disasterId}"

// Bantuan di sekitar lokasi
"nearby-aids/{latitude}/{longitude}"

// Tambah bantuan baru
"add-disaster-aid/{disasterId}"

// Update bantuan
"update-disaster-aid/{aidId}"
```

## 🎨 **UI Features**

### **1. DisasterAidListScreen**

-   ✅ Menampilkan daftar bantuan dengan informasi lengkap
-   ✅ Filter chips untuk status dan tipe bantuan
-   ✅ Floating Action Button untuk tambah bantuan
-   ✅ Pull to refresh
-   ✅ Loading states dan error handling
-   ✅ Empty state untuk daftar kosong

### **2. NearbyAidsScreen**

-   ✅ Menampilkan bantuan di sekitar lokasi pengguna
-   ✅ Info lokasi dan radius pencarian
-   ✅ Sorting berdasarkan jarak
-   ✅ Filter berdasarkan status dan tipe
-   ✅ Visual indicator untuk jarak

## 🔍 **Filtering Capabilities**

### **Filter Options**

1. **Status**: tersedia, habis, dalam_perjalanan, terdistribusi
2. **Type**: makanan, minuman, obat, pakaian, tenda, dll
3. **Radius**: Dalam kilometer (default 10km)
4. **Location-based**: Berdasarkan koordinat pengguna

### **Location-based Features**

-   ✅ Perhitungan jarak menggunakan Haversine formula
-   ✅ Sorting otomatis berdasarkan jarak terdekat
-   ✅ Visual indicator untuk jarak dari lokasi pengguna
-   ✅ Filter radius pencarian yang dapat disesuaikan

## 📊 **Data Structure**

### **DisasterAid Model**

```kotlin
data class DisasterAid(
    val id: String,
    val disasterId: String,
    val name: String,
    val type: String,
    val quantity: Int,
    val unit: String,
    val status: String,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val description: String?,
    val contactPerson: String?,
    val contactPhone: String?,
    val estimatedArrival: String?,
    val createdAt: String,
    val updatedAt: String,
    val distance: Double? = null, // Calculated distance
    val disasterName: String? = null
)
```

## 🚨 **Error Handling**

### **Error Types**

-   **Network Error**: Kesalahan koneksi internet
-   **Server Error**: Response error dari API
-   **Location Error**: Tidak dapat mengakses lokasi pengguna
-   **Authentication Error**: Token tidak valid atau expired

### **Error States UI**

-   Loading spinner saat memuat data
-   Error message dengan retry button
-   Empty state untuk daftar kosong
-   Offline indicator

## 🔒 **Security & Authentication**

### **API Security**

-   ✅ JWT Token authentication
-   ✅ Role-based access control
-   ✅ Secure API communication
-   ✅ Request/response encryption (opsional)

## 📈 **Performance Optimizations**

### **Caching Strategy**

-   ✅ ViewModel state persistence
-   ✅ Smart refresh mechanism
-   ✅ Debounced search/filtering

### **Memory Management**

-   ✅ Proper coroutine scope management
-   ✅ Resource cleanup
-   ✅ Efficient state updates

## 🧪 **Testing Recommendations**

### **Unit Tests**

-   Repository methods
-   ViewModel state management
-   Utility functions (distance calculation)

### **Integration Tests**

-   API communication
-   Authentication flow
-   Location services integration

## 🎯 **Use Cases**

### **1. Emergency Response**

-   Petugas mencari bantuan terdekat saat bencana terjadi
-   Koordinasi distribusi bantuan berdasarkan lokasi

### **2. Logistics Management**

-   Tracking status bantuan dari gudang ke lokasi bencana
-   Monitoring ketersediaan bantuan real-time

### **3. Resource Allocation**

-   Visualisasi distribusi bantuan berdasarkan kebutuhan
-   Optimasi rute pengiriman bantuan

## 🔄 **Next Steps**

### **Immediate (High Priority)**

1. ✅ Implementasi location services untuk mendapatkan koordinat pengguna
2. ✅ Setup authentication system
3. ✅ Configure base URL API
4. ✅ Add proper error handling UI

### **Medium Priority**

1. 🔄 Real-time updates dengan WebSocket atau polling
2. 🔄 Offline support dengan local database
3. 🔄 Push notifications untuk update bantuan
4. 🔄 Map integration untuk visualisasi lokasi

### **Low Priority**

1. 🔄 Advanced filtering (date range, priority level)
2. 🔄 Bulk operations untuk multiple aids
3. 🔄 Export data ke PDF/Excel
4. 🔄 Analytics dan reporting dashboard

---

**Status**: ✅ **SIAP DIGUNAKAN** - Fitur bantuan dan logistik telah diimplementasi dengan lengkap dan siap untuk integrasi dengan Laravel API backend.
