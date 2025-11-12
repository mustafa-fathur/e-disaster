package com.example.e_disaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Komponen yang menampilkan sekelompok FilterChip yang dapat digunakan kembali.
 * Dibuat horizontal scrollable menggunakan LazyRow untuk menangani banyak opsi filter.
 *
 * @param filterOptions Daftar String yang akan ditampilkan sebagai opsi filter.
 * @param selectedFilter Opsi filter yang sedang dipilih.
 * @param onFilterSelected Lambda yang dipanggil ketika sebuah chip dipilih.
 * @param modifier Modifier untuk kustomisasi layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipGroup(
    filterOptions: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filterOptions) { filterText ->
            val isSelected = selectedFilter == filterText

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filterText) },
                label = { Text(filterText) },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = FilterChipDefaults.filterChipBorder(
                    selected = isSelected,
                    enabled = true,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    selectedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.5.dp
                )
            )
        }
    }
}
