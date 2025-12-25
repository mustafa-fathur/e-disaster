package com.example.e_disaster.utils

import com.example.e_disaster.data.model.History
import com.example.e_disaster.data.model.Participant
import com.example.e_disaster.data.model.Notification
import com.example.e_disaster.data.model.NotificationData
import com.example.e_disaster.data.model.NotificationPriority
import com.example.e_disaster.data.model.NotificationType
import androidx.core.telecom.util.ExperimentalAppActions


object DummyData {

    val dummyNotifications = listOf(
        Notification(
            id = "1",
            title = "Bencana Banjir Baru Terdeteksi",
            message = "Banjir dengan skala sedang telah terdeteksi di Jakarta Pusat. Tim rescue diharapkan segera merespons.",
            type = NotificationType.DISASTER.value,
            priority = NotificationPriority.HIGH.value,
            isRead = false,
            createdAt = "2024-01-15T10:30:00Z",
            data = NotificationData(
                disasterId = "disaster-001",
                disasterName = "Banjir Jakarta Pusat"
            )
        ),
        Notification(
            id = "2",
            title = "Bantuan Logistik Tersedia",
            message = "Bantuan makanan dan air bersih telah tersedia di gudang utama dan siap didistribusikan.",
            type = NotificationType.AID.value,
            priority = NotificationPriority.MEDIUM.value,
            isRead = false,
            createdAt = "2024-01-15T09:15:00Z",
            data = NotificationData(
                aidId = "aid-001",
                aidName = "Bantuan Makanan dan Air"
            )
        ),
        Notification(
            id = "3",
            title = "Korban Baru Dilaporkan",
            message = "5 korban baru telah dilaporkan membutuhkan bantuan medis segera di lokasi bencana.",
            type = NotificationType.VICTIM.value,
            priority = NotificationPriority.URGENT.value,
            isRead = true,
            createdAt = "2024-01-15T08:45:00Z",
            data = NotificationData(
                victimId = "victim-001",
                victimName = "Korban Banjir",
                disasterId = "disaster-001"
            )
        ),
        Notification(
            id = "4",
            title = "Update Status Bencana",
            message = "Status bencana di Bandung telah dinaikkan menjadi skala besar. Tambahan tim diperlukan.",
            type = NotificationType.DISASTER.value,
            priority = NotificationPriority.HIGH.value,
            isRead = true,
            createdAt = "2024-01-14T16:20:00Z",
            data = NotificationData(
                disasterId = "disaster-002",
                disasterName = "Bencana Bandung"
            )
        ),
        Notification(
            id = "5",
            title = "Bantuan Obat-obatan Habis",
            message = "Stok obat-obatan di posko bencana telah habis. Permintaan pengiriman segera diperlukan.",
            type = NotificationType.AID.value,
            priority = NotificationPriority.URGENT.value,
            isRead = false,
            createdAt = "2024-01-14T14:30:00Z",
            data = NotificationData(
                aidId = "aid-002",
                aidName = "Obat-obatan Medis"
            )
        ),
        Notification(
            id = "6",
            title = "Relawan Baru Ditugaskan",
            message = "10 relawan baru telah ditugaskan ke posko bencana Jakarta. Briefing akan dilakukan pagi ini.",
            type = NotificationType.SYSTEM.value,
            priority = NotificationPriority.MEDIUM.value,
            isRead = true,
            createdAt = "2024-01-14T12:00:00Z"
        ),
        Notification(
            id = "7",
            title = "Laporan Cuaca Ekstrem",
            message = "BMKG memprediksi hujan deras dengan intensitas tinggi di wilayah Jabodetabek selama 3 hari ke depan.",
            type = NotificationType.SYSTEM.value,
            priority = NotificationPriority.HIGH.value,
            isRead = false,
            createdAt = "2024-01-14T10:15:00Z"
        ),
        Notification(
            id = "8",
            title = "Bantuan Tenda Darurat Tersedia",
            message = "20 tenda darurat telah tersedia dan siap didistribusikan ke lokasi pengungsian.",
            type = NotificationType.AID.value,
            priority = NotificationPriority.MEDIUM.value,
            isRead = true,
            createdAt = "2024-01-13T18:30:00Z",
            data = NotificationData(
                aidId = "aid-003",
                aidName = "Tenda Darurat"
            )
        )
    )

//    val dummyDisasterAids = listOf(
//        DisasterAid(
//            id = "aid-001",
//            disasterId = "disaster-001",
//            name = "Bantuan Makanan Siap Saji",
//            type = "makanan",
//            quantity = 500,
//            unit = "paket",
//            status = "tersedia",
//            location = "Gudang Pusat Jakarta",
//            latitude = -6.2088,
//            longitude = 106.8456,
//            description = "Bantuan makanan siap saji untuk korban bencana banjir",
//            contactPerson = "Ahmad Santoso",
//            contactPhone = "08123456789",
//            estimatedArrival = "2024-01-16T10:00:00Z",
//            createdAt = "2024-01-15T08:00:00Z",
//            updatedAt = "2024-01-15T08:00:00Z",
//            distance = 2.5,
//            disasterName = "Banjir Jakarta Pusat"
//        ),
//        DisasterAid(
//            id = "aid-002",
//            disasterId = "disaster-001",
//            name = "Air Mineral",
//            type = "minuman",
//            quantity = 1000,
//            unit = "botol",
//            status = "dalam_perjalanan",
//            location = "Distributor Aqua",
//            latitude = -6.2000,
//            longitude = 106.8000,
//            description = "Air mineral untuk kebutuhan korban bencana",
//            contactPerson = "Siti Nurhaliza",
//            contactPhone = "08198765432",
//            estimatedArrival = "2024-01-16T14:00:00Z",
//            createdAt = "2024-01-15T09:00:00Z",
//            updatedAt = "2024-01-15T09:00:00Z",
//            distance = 5.2,
//            disasterName = "Banjir Jakarta Pusat"
//        ),
//        DisasterAid(
//            id = "aid-003",
//            disasterId = "disaster-002",
//            name = "Obat-obatan Medis",
//            type = "obat",
//            quantity = 200,
//            unit = "kotak",
//            status = "habis",
//            location = "RSUD Bandung",
//            latitude = -6.9175,
//            longitude = 107.6191,
//            description = "Obat-obatan untuk korban luka dan sakit",
//            contactPerson = "Dr. Budi Santoso",
//            contactPhone = "08134567890",
//            estimatedArrival = null,
//            createdAt = "2024-01-14T15:00:00Z",
//            updatedAt = "2024-01-15T10:00:00Z",
//            distance = 15.8,
//            disasterName = "Bencana Bandung"
//        ),
//        DisasterAid(
//            id = "aid-004",
//            disasterId = "disaster-001",
//            name = "Selimut dan Pakaian",
//            type = "pakaian",
//            quantity = 300,
//            unit = "paket",
//            status = "tersedia",
//            location = "Posko Utama Jakarta",
//            latitude = -6.2100,
//            longitude = 106.8500,
//            description = "Selimut dan pakaian hangat untuk korban bencana",
//            contactPerson = "Maya Sari",
//            contactPhone = "08145678901",
//            estimatedArrival = "2024-01-16T09:00:00Z",
//            createdAt = "2024-01-15T11:00:00Z",
//            updatedAt = "2024-01-15T11:00:00Z",
//            distance = 1.8,
//            disasterName = "Banjir Jakarta Pusat"
//        ),
//        DisasterAid(
//            id = "aid-005",
//            disasterId = "disaster-003",
//            name = "Tenda Darurat",
//            type = "tenda",
//            quantity = 50,
//            unit = "buah",
//            status = "terdistribusi",
//            location = "Gudang Darurat Surabaya",
//            latitude = -7.2575,
//            longitude = 112.7521,
//            description = "Tenda darurat untuk pengungsian sementara",
//            contactPerson = "Pak Ahmad",
//            contactPhone = "08156789012",
//            estimatedArrival = null,
//            createdAt = "2024-01-13T14:00:00Z",
//            updatedAt = "2024-01-15T12:00:00Z",
//            distance = 25.3,
//            disasterName = "Bencana Surabaya"
//        )
//    )

    @OptIn(ExperimentalAppActions::class)
    private val participants1 = listOf(
        Participant("u1", "Ahmad Budi", "officer", "https://i.pravatar.cc/150?u=a1"),
        Participant("u2", "Citra Lestari", "volunteer", "https://i.pravatar.cc/150?u=a2"),
        Participant("u3", "Doni Setiawan", "volunteer", "https://i.pravatar.cc/150?u=a3")
    )

    @OptIn(ExperimentalAppActions::class)
    private val participants2 = listOf(
        Participant("u4", "Eka Putri", "officer", "https://i.pravatar.cc/150?u=a4"),
        Participant("u5", "Fajar Nugraha", "volunteer", "https://i.pravatar.cc/150?u=a5")
    )

    private val _historyList = mutableListOf(
        History(
            id = "1",
            disasterName = "Banjir Bandang Garut",
            location = "Garut, Jawa Barat",
            date = "15 Juli 2024",
            description = "Banjir bandang melanda beberapa wilayah di Garut akibat hujan deras yang terus menerus selama 3 hari.",
            imageUrl = "https://picsum.photos/seed/banjir/400/400",
            status = "completed",
            participants = participants1
        ),
        History(
            id = "2",
            disasterName = "Tanah Longsor Sukabumi",
            location = "Sukabumi, Jawa Barat",
            date = "22 Mei 2024",
            description = "Tanah longsor terjadi di daerah perbukitan Sukabumi yang mengakibatkan beberapa rumah tertimbun.",
            imageUrl = "https://picsum.photos/seed/banjir/400/400",
            status = "completed",
            participants = participants2
        ),
        History(
            id = "3",
            disasterName = "Gempa Bumi Cianjur",
            location = "Cianjur, Jawa Barat",
            date = "21 November 2023",
            imageUrl = "https://picsum.photos/seed/banjir/400/400",
            status = "completed",
            description = "Gempa bumi berkekuatan 5.6 SR mengguncang wilayah Cianjur, menyebabkan kerusakan pada infrastruktur dan bangunan.",
            participants = participants1
        ),
        History(
            id = "4",
            disasterName = "Kekeringan Gunungkidul",
            location = "Gunungkidul, Yogyakarta",
            date = "10 Agustus 2023",
            imageUrl = "https://picsum.photos/seed/banjir/400/400",
            status = "completed",
            description = "Kekeringan melanda wilayah Gunungkidul akibat musim kemarau yang berkepanjangan, mengakibatkan krisis air bersih.",
            participants = participants2
        ),
        History(
            id = "5",
            disasterName = "Erupsi Gunung Merapi",
            location = "Sleman, Yogyakarta",
            date = "11 Maret 2023",
            imageUrl = "https://picsum.photos/seed/banjir/400/400",
            status = "completed",
            description = "Gunung Merapi mengalami erupsi yang mengeluarkan abu vulkanik dan lava, memaksa evakuasi warga sekitar.",
            participants = participants1
        )
    )
    val historyList: List<History> = _historyList

    fun getHistoryById(id: String?): History? {
        if (id == null) return null
        return historyList.find { it.id == id }
    }

    // Fungsi untuk mendapatkan notifikasi berdasarkan tipe
    fun getNotificationsByType(type: NotificationType): List<Notification> {
        return dummyNotifications.filter { it.type == type.value }
    }

    // Fungsi untuk mendapatkan notifikasi yang belum dibaca
    fun getUnreadNotifications(): List<Notification> {
        return dummyNotifications.filter { !it.isRead }
    }

    // Fungsi untuk memperbarui data history
    fun updateHistory(updatedHistory: History) {
        val index = _historyList.indexOfFirst { it.id == updatedHistory.id }
        if (index != -1) {
            _historyList[index] = updatedHistory
        }
    }

    // Fungsi untuk menambahkan data history baru
    fun addHistory(newHistory: History) {
        _historyList.add(newHistory)
    }

//    // Fungsi untuk mendapatkan bantuan berdasarkan status
//    fun getAidsByStatus(status: String): List<DisasterAid> {
//        return dummyDisasterAids.filter { it.status == status }
//    }
//
//    // Fungsi untuk mendapatkan bantuan berdasarkan tipe
//    fun getAidsByType(type: String): List<DisasterAid> {
//        return dummyDisasterAids.filter { it.type == type }
//    }
}
