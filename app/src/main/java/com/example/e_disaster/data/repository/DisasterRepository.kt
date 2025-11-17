package com.example.e_disaster.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.service.DisasterApiService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisasterRepository @Inject constructor(
    private val apiService: DisasterApiService
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDisasters(): List<Disaster> {
        val response = apiService.getDisasters()

        return response.data.map { mapDisasterDtoToDisaster(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mapDisasterDtoToDisaster(dto: DisasterDto): Disaster {
        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))

        val formattedDate = try {
            val date = LocalDate.parse(dto.disasterDate, inputFormatter)
            date.format(outputFormatter)
        } catch (e: Exception) {
            "Tanggal tidak diketahui"
        }

        return Disaster(
            id = dto.id,
            name = dto.title,
            description = dto.description ?: "Tidak ada deskripsi",
            date = formattedDate
        )
    }
}