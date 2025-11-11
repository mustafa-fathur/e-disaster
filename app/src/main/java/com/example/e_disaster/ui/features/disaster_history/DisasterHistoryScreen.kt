package com.example.e_disaster.ui.features.disaster_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.e_disaster.ui.components.partials.AppBottomNavBar
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.components.partials.MainViewModel
import com.example.e_disaster.ui.features.disaster_history.components.HistoryItem
import com.example.e_disaster.utils.DummyData.historyList


@Composable
fun HistoryScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val historyList = historyList
    val user = mainViewModel.user
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Riwayat",
                profilePictureUrl = user?.profilePicture,
                canNavigateBack = false,
                onProfileClick = {
                    navController.navigate("profile")
                },
            )
        },
        bottomBar = {
            AppBottomNavBar(navController = navController as NavHostController)
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (historyList.isEmpty()) {
                    // Tampilan jika data riwayat kosong
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Anda belum memiliki riwayat penanganan bencana.",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Tampilkan daftar riwayat menggunakan LazyColumn
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(historyList, key = { it.id }) { history ->
                            HistoryItem(
                                history = history,
                                modifier = Modifier.clickable {
                                    navController.navigate("history-detail/${history.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    )

}