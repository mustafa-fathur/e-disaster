//package com.example.e_disaster.data.repository
//
//import com.example.e_disaster.data.model.CreateDisasterAidRequest
//import com.example.e_disaster.data.model.DisasterAid
//import com.example.e_disaster.data.model.UpdateDisasterAidRequest
//import com.example.e_disaster.utils.Resource
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import retrofit2.HttpException
//import java.io.IOException
//
//class DisasterAidRepository {
//
//    private val api = ApiClient.disasterAidApi
//
//    /**
//     * Mendapatkan daftar bantuan untuk bencana tertentu
//     */
//    fun getDisasterAids(
//        disasterId: String,
//        userLatitude: Double? = null,
//        userLongitude: Double? = null,
//        radius: Double? = null,
//        status: String? = null,
//        type: String? = null
//    ): Flow<Resource<List<DisasterAid>>> = flow {
//        try {
//            emit(Resource.Loading())
//
//            val response = api.getDisasterAids(
//                disasterId = disasterId,
//                latitude = userLatitude,
//                longitude = userLongitude,
//                radius = radius,
//                status = status,
//                type = type
//            )
//
//            if (response.isSuccessful) {
//                val aids = response.body() ?: emptyList()
//
//                // Hitung jarak jika koordinat pengguna tersedia
//                val aidsWithDistance = if (userLatitude != null && userLongitude != null) {
//                    aids.map { aid ->
//                        aid.copy(
//                            distance = calculateDistance(
//                                userLatitude, userLongitude,
//                                aid.latitude ?: 0.0, aid.longitude ?: 0.0
//                            )
//                        )
//                    }
//                } else {
//                    aids
//                }
//
//                emit(Resource.Success(aidsWithDistance))
//            } else {
//                emit(Resource.Error("Gagal memuat data bantuan: ${response.message()}"))
//            }
//        } catch (e: HttpException) {
//            emit(Resource.Error("Kesalahan server: ${e.localizedMessage}"))
//        } catch (e: IOException) {
//            emit(Resource.Error("Kesalahan koneksi: ${e.localizedMessage}"))
//        } catch (e: Exception) {
//            emit(Resource.Error("Kesalahan tidak dikenal: ${e.localizedMessage}"))
//        }
//    }
//
//    /**
//     * Mendapatkan daftar bantuan di sekitar lokasi pengguna
//     */
//    fun getNearbyAids(
//        latitude: Double,
//        longitude: Double,
//        radius: Double = 10.0,
//        status: String? = null,
//        type: String? = null
//    ): Flow<Resource<List<DisasterAid>>> = flow {
//        try {
//            emit(Resource.Loading())
//
//            val response = api.getNearbyAids(
//                latitude = latitude,
//                longitude = longitude,
//                radius = radius,
//                status = status,
//                type = type
//            )
//
//            if (response.isSuccessful) {
//                val aids = response.body() ?: emptyList()
//
//                // Hitung jarak dari lokasi pengguna
//                val aidsWithDistance = aids.map { aid ->
//                    aid.copy(
//                        distance = calculateDistance(
//                            latitude, longitude,
//                            aid.latitude ?: 0.0, aid.longitude ?: 0.0
//                        )
//                    )
//                }
//
//                emit(Resource.Success(aidsWithDistance))
//            } else {
//                emit(Resource.Error("Gagal memuat data bantuan sekitar: ${response.message()}"))
//            }
//        } catch (e: HttpException) {
//            emit(Resource.Error("Kesalahan server: ${e.localizedMessage}"))
//        } catch (e: IOException) {
//            emit(Resource.Error("Kesalahan koneksi: ${e.localizedMessage}"))
//        } catch (e: Exception) {
//            emit(Resource.Error("Kesalahan tidak dikenal: ${e.localizedMessage}"))
//        }
//    }
//
//    /**
//     * Membuat bantuan baru
//     */
//    fun createDisasterAid(
//        disasterId: String,
//        aidRequest: CreateDisasterAidRequest
//    ): Flow<Resource<DisasterAid>> = flow {
//        try {
//            emit(Resource.Loading())
//
//            val response = api.createDisasterAid(disasterId, aidRequest)
//
//            if (response.isSuccessful) {
//                val aid = response.body()
//                if (aid != null) {
//                    emit(Resource.Success(aid))
//                } else {
//                    emit(Resource.Error("Gagal membuat bantuan: Response kosong"))
//                }
//            } else {
//                emit(Resource.Error("Gagal membuat bantuan: ${response.message()}"))
//            }
//        } catch (e: HttpException) {
//            emit(Resource.Error("Kesalahan server: ${e.localizedMessage}"))
//        } catch (e: IOException) {
//            emit(Resource.Error("Kesalahan koneksi: ${e.localizedMessage}"))
//        } catch (e: Exception) {
//            emit(Resource.Error("Kesalahan tidak dikenal: ${e.localizedMessage}"))
//        }
//    }
//
//    /**
//     * Update bantuan
//     */
//    fun updateDisasterAid(
//        disasterId: String,
//        aidId: String,
//        aidRequest: UpdateDisasterAidRequest
//    ): Flow<Resource<DisasterAid>> = flow {
//        try {
//            emit(Resource.Loading())
//
//            val response = api.updateDisasterAid(disasterId, aidId, aidRequest)
//
//            if (response.isSuccessful) {
//                val aid = response.body()
//                if (aid != null) {
//                    emit(Resource.Success(aid))
//                } else {
//                    emit(Resource.Error("Gagal update bantuan: Response kosong"))
//                }
//            } else {
//                emit(Resource.Error("Gagal update bantuan: ${response.message()}"))
//            }
//        } catch (e: HttpException) {
//            emit(Resource.Error("Kesalahan server: ${e.localizedMessage}"))
//        } catch (e: IOException) {
//            emit(Resource.Error("Kesalahan koneksi: ${e.localizedMessage}"))
//        } catch (e: Exception) {
//            emit(Resource.Error("Kesalahan tidak dikenal: ${e.localizedMessage}"))
//        }
//    }
//
//    /**
//     * Hapus bantuan
//     */
//    fun deleteDisasterAid(
//        disasterId: String,
//        aidId: String
//    ): Flow<Resource<Unit>> = flow {
//        try {
//            emit(Resource.Loading())
//
//            val response = api.deleteDisasterAid(disasterId, aidId)
//
//            if (response.isSuccessful) {
//                emit(Resource.Success(Unit))
//            } else {
//                emit(Resource.Error("Gagal hapus bantuan: ${response.message()}"))
//            }
//        } catch (e: HttpException) {
//            emit(Resource.Error("Kesalahan server: ${e.localizedMessage}"))
//        } catch (e: IOException) {
//            emit(Resource.Error("Kesalahan koneksi: ${e.localizedMessage}"))
//        } catch (e: Exception) {
//            emit(Resource.Error("Kesalahan tidak dikenal: ${e.localizedMessage}"))
//        }
//    }
//
//    /**
//     * Menghitung jarak antara dua titik koordinat menggunakan formula Haversine
//     */
//    private fun calculateDistance(
//        lat1: Double, lon1: Double,
//        lat2: Double, lon2: Double
//    ): Double {
//        val earthRadius = 6371.0 // Radius bumi dalam km
//
//        val dLat = Math.toRadians(lat2 - lat1)
//        val dLon = Math.toRadians(lon2 - lon1)
//
//        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                Math.sin(dLon / 2) * Math.sin(dLon / 2)
//
//        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
//
//        return earthRadius * c
//    }
//}
