package com.example.e_disaster.ui.features.disaster

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.features.disaster.detail.AssignedDisasterContent
import com.example.e_disaster.ui.features.disaster.detail.JoinConfirmationDialog
import com.example.e_disaster.ui.features.disaster.detail.UnassignedDisasterContent
import com.example.e_disaster.ui.features.disaster.detail.components.SpeedDialFab
import com.example.e_disaster.ui.theme.EDisasterTheme

// --- DATA CLASSES (can be moved to a 'model' or 'domain' package later) ---
data class FabMenuItem(
    val icon: ImageVector? = null,
    val iconPainter: Painter? = null,
    val label: String,
    val onClick: () -> Unit
)
data class ReportItem(val id: String, val title: String, val date: String)
data class VictimItem(val id: String, val name: String, val status: String)
data class AidItem(val id: String, val type: String, val amount: String)
// -------------------------------------------------------------------------

@Composable
fun DisasterDetailScreen(navController: NavController, disasterId: String?) {
    val isAssigned by remember(disasterId) { mutableStateOf(disasterId == "2") }
    var showJoinDialog by remember { mutableStateOf(false) }
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    val fabMenuItems = listOf(
        FabMenuItem(
            iconPainter = painterResource(id = R.drawable.id_card),
            label = "Tambah Laporan",
            onClick = { navController.navigate("add-disaster-report/$disasterId") }
        ),
        FabMenuItem(
            icon = Icons.Default.Person,
            label = "Tambah Data Korban",
            onClick = { navController.navigate("add-disaster-victim/$disasterId") }
        ),
        FabMenuItem(
            iconPainter = painterResource(id = R.drawable.package_box),
            label = "Tambah Data Bantuan",
            onClick = { navController.navigate("add-disaster-aid/$disasterId") }
        )
    )

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Detail Bencana",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    if (isAssigned) {
                        TextButton(
                            onClick = { navController.navigate("update-disaster/$disasterId") },
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
            if (isAssigned) {
                SpeedDialFab(
                    isExpanded = isFabMenuExpanded,
                    onFabClick = { isFabMenuExpanded = !isFabMenuExpanded },
                    items = fabMenuItems
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isAssigned) {
                AssignedDisasterContent(navController, disasterId)
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    UnassignedDisasterContent(onJoinClick = { showJoinDialog = true })
                }
            }
        }

        if (showJoinDialog) {
            JoinConfirmationDialog(
                onConfirm = {
                    showJoinDialog = false
                    // TODO: In a real app, call ViewModel to join, then refresh state to `isAssigned = true`
                },
                onDismiss = { showJoinDialog = false }
            )
        }
    }
}

@Preview(showBackground = true, name = "Unassigned Light")
@Composable
fun DisasterDetailUnassignedPreview() {
    EDisasterTheme(darkTheme = false) {
        DisasterDetailScreen(navController = rememberNavController(), disasterId = "1")
    }
}

@Preview(showBackground = true, name = "Unassigned Dark")
@Composable
fun DisasterDetailUnassignedDarkPreview() {
    EDisasterTheme(darkTheme = true) {
        DisasterDetailScreen(navController = rememberNavController(), disasterId = "1")
    }
}

@Preview(showBackground = true, name = "Assigned Light")
@Composable
fun DisasterDetailAssignedPreview() {
    EDisasterTheme(darkTheme = false) {
        DisasterDetailScreen(navController = rememberNavController(), disasterId = "2")
    }
}

@Preview(showBackground = true, name = "Assigned Dark")
@Composable
fun DisasterDetailAssignedDarkPreview() {
    EDisasterTheme(darkTheme = true) {
        DisasterDetailScreen(navController = rememberNavController(), disasterId = "2")
    }
}
