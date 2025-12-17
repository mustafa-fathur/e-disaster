package com.example.e_disaster.ui.features.disaster.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.features.disaster.detail.components.SpeedDialFab
import com.example.e_disaster.ui.features.disaster.detail.contents.AssignedDisasterContent
import com.example.e_disaster.ui.features.disaster.detail.contents.JoinConfirmationDialog
import com.example.e_disaster.ui.features.disaster.detail.contents.UnassignedDisasterContent
import kotlinx.coroutines.launch

data class FabMenuItem(
    val icon: ImageVector? = null,
    val iconPainter: Painter? = null,
    val label: String,
    val onClick: () -> Unit
)
data class ReportItem(val id: String, val title: String, val date: String)
data class VictimItem(val id: String, val name: String, val description: String, val status: String, val isEvacuated: Boolean)
data class AidItem(
    val id: String,
    val title: String,
    val amount: String,
    val description: String,
    val category: String
)

@Composable
fun DisasterDetailScreen(
    navController: NavController,
    disasterId: String?,
    viewModel: DisasterDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var showJoinDialog by remember { mutableStateOf(false) }
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val victimUpdateResult = navController.currentBackStackEntry
        ?.savedStateHandle?.getLiveData<Boolean>("victim_updated")

    LaunchedEffect(victimUpdateResult) {
        victimUpdateResult?.observeForever { updated ->
            if (updated) {
                scope.launch {
                    viewModel.refreshVictims()
                    selectedTabIndex = 2
                }
                navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("victim_updated")
            }
        }
    }

    LaunchedEffect(disasterId) {
        if (!disasterId.isNullOrEmpty()) {
            viewModel.getDisasterDetails(disasterId)
        }
    }

    LaunchedEffect(uiState.joinStatusMessage) {
        uiState.joinStatusMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearJoinStatusMessage()
        }
    }

    val fabMenuItems = listOf(
        FabMenuItem(
            iconPainter = painterResource(id = R.drawable.id_card),
            label = "Tambah Laporan",
            onClick = { uiState.disaster?.id?.let { navController.navigate("add-disaster-report/$it") } }
        ),
        FabMenuItem(
            icon = Icons.Default.Person,
            label = "Tambah Data Korban",
            onClick = { if (!disasterId.isNullOrEmpty()) navController.navigate("add-disaster-victim/$disasterId") }
        ),
        FabMenuItem(
            iconPainter = painterResource(id = R.drawable.package_box),
            label = "Tambah Data Bantuan",
            onClick = { uiState.disaster?.id?.let { navController.navigate("add-disaster-aid/$it") } }
        )
    )

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = if (uiState.disaster != null) "Detail Bencana" else "Memuat...",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    if (uiState.isAssigned && uiState.disaster != null) {
                        TextButton(
                            onClick = { navController.navigate("update-disaster/${uiState.disaster!!.id}") },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Ubah",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ubah")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.isAssigned) {
                SpeedDialFab(
                    isExpanded = isFabMenuExpanded,
                    onFabClick = { isFabMenuExpanded = !isFabMenuExpanded },
                    items = fabMenuItems
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                uiState.disaster != null -> {
                    if (uiState.isAssigned) {
                        AssignedDisasterContent(
                            navController = navController,
                            disaster = uiState.disaster!!,
                            victims = uiState.victims,
                            initialTabIndex = selectedTabIndex,
                            onTabSelected = { selectedTabIndex = it }
                        )
                    } else {
                        UnassignedDisasterContent(
                            disaster = uiState.disaster!!,
                            onJoinClick = { showJoinDialog = true }
                        )
                    }
                }
            }
        }

        if (showJoinDialog) {
            JoinConfirmationDialog(
                onConfirm = {
                    showJoinDialog = false
                    viewModel.joinDisaster()
                },
                onDismiss = { showJoinDialog = false }
            )
        }
    }
}
