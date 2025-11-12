package com.example.e_disaster.ui.features.disaster.detail.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.AppSearchBar
import com.example.e_disaster.ui.components.FilterChipGroup
import com.example.e_disaster.ui.components.badges.DisasterAidCategoryBadge
import com.example.e_disaster.ui.features.disaster.AidItem
import com.example.e_disaster.ui.features.disaster.detail.components.ListItemCard

@Composable
fun AidsTabContent(navController: NavController, aids: List<AidItem>) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    // Opsi filter untuk bantuan
    val filterOptions = listOf("Semua", "Makanan", "Pakaian", "Fasilitas", "Obat-obatan")

    val filteredAids = remember(searchQuery, selectedFilter, aids) {
        aids.filter { aid ->
            // Filter berdasarkan nama bantuan (misalnya, 'Paket Sembako')
            val matchesSearch = aid.title.contains(searchQuery, ignoreCase = true)

            // Filter berdasarkan kategori
            val matchesFilter = when (selectedFilter) {
                "Semua" -> true
                "Makanan" -> aid.category.equals("food", ignoreCase = true)
                "Pakaian" -> aid.category.equals("clothing", ignoreCase = true)
                "Fasilitas" -> aid.category.equals("housing", ignoreCase = true)
                "Obat-obatan" -> aid.category.equals("medicine", ignoreCase = true)
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Gunakan komponen SearchBar yang reusable
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholderText = "Cari data bantuan..."
                )
                // Gunakan komponen FilterChipGroup yang reusable
                FilterChipGroup(
                    filterOptions = filterOptions.map {
                        // Sesuaikan teks chip dengan kategori
                        when (it) {
                            "Pakaian" -> "Sandang"
                            "Makanan" -> "Pangan"
                            "Fasilitas" -> "Papan"
                            else -> it
                        }
                    },
                    selectedFilter = when (selectedFilter) {
                        "Pakaian" -> "Sandang"
                        "Makanan" -> "Pangan"
                        "Fasilitas" -> "Papan"
                        else -> selectedFilter
                    },
                    onFilterSelected = { newFilter ->
                        // Konversi kembali teks chip ke nilai filter
                        selectedFilter = when (newFilter) {
                            "Sandang" -> "Pakaian"
                            "Pangan" -> "Makanan"
                            "Papan" -> "Fasilitas"
                            else -> newFilter
                        }
                    }
                )
            }
        }

        items(filteredAids) { aid ->
            ListItemCard(
                onClick = { navController.navigate("disaster-aid-detail/${aid.id}") }
            ) {
                AidCardContent(aid = aid)
            }
        }
    }
}

@Composable
private fun AidCardContent(aid: AidItem) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Baris atas: Judul dan Jumlah
        Text(
            text = aid.title,
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Default),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = aid.amount,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Baris tengah: Deskripsi bantuan
        Text(
            text = aid.description,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Baris bawah: Pelapor, Waktu, dan Badge Kategori
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Informasi pelapor dan waktu
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Reporter",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Ahmad Wijaya • 2024-10-28 15:30", // Data placeholder
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Badge Kategori di sisi kanan
            Box {
                DisasterAidCategoryBadge(category = aid.category)
            }
        }
    }
}