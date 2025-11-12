package com.example.e_disaster.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Komponen SearchBar yang dapat digunakan kembali di seluruh aplikasi.
 *
 * @param query Teks saat ini yang akan ditampilkan di search bar.
 * @param onQueryChange Lambda yang dipanggil saat teks berubah.
 * @param placeholderText Teks placeholder yang ditampilkan saat search bar kosong.
 * @param modifier Modifier untuk kustomisasi layout.
 */
@Composable
fun AppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholderText,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) // Warna placeholder yang lebih netral
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search, // Menggunakan ikon pencarian
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        singleLine = true // Praktik yang baik untuk search bar
    )
}
