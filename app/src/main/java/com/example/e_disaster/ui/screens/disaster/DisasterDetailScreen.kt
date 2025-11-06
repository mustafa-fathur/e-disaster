
package com.example.e_disaster.ui.screens.disaster

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme

// Data class for the speed dial items
data class FabMenuItem(
    val icon: ImageVector? = null,
    val iconPainter: Painter? = null,
    val label: String,
    val onClick: () -> Unit
)

// Mock Data Classes for list items
data class ReportItem(val id: String, val title: String, val date: String)
data class VictimItem(val id: String, val name: String, val status: String)
data class AidItem(val id: String, val type: String, val amount: String)

@Composable
fun DisasterDetailScreen(navController: NavController, disasterId: String?) {
    val isAssigned by remember(disasterId) { mutableStateOf(disasterId == "2") }
    var showJoinDialog by remember { mutableStateOf(false) }
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    // Define FAB menu items
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
                            onClick = {
                                navController.navigate("update-disaster/$disasterId")
                            }, colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
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
            // Use a different scroll modifier depending on content
        ) {
            if (isAssigned) {
                // Scroll is handled inside each tab's LazyColumn
                AssignedDisasterContent(navController, disasterId)
            } else {
                // Unassigned content is shorter and can use a simple column scroll
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

/**
 * A Speed Dial Floating Action Button that expands to show multiple menu items.
 */
@Composable
fun SpeedDialFab(
    isExpanded: Boolean,
    onFabClick: () -> Unit,
    items: List<FabMenuItem>
) {
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 45f else 0f, label = "fab_rotation")

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable(onClick = item.onClick)
                    ) {
                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                        SmallFloatingActionButton(
                            onClick = item.onClick,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            if (item.icon != null) {
                                Icon(imageVector = item.icon, contentDescription = item.label)
                            } else if (item.iconPainter != null) {
                                Icon(painter = item.iconPainter, contentDescription = item.label)
                            }
                        }
                    }
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = onFabClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Toggle Menu",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}


@Composable
fun UnassignedDisasterContent(onJoinClick: () -> Unit) {
    val images = listOf(R.drawable.placeholder, R.drawable.placeholder, R.drawable.placeholder)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ImageSlider(images = images)
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Gempa Bumi Cianjur", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text("Gempa bumi berkekuatan 5.6 SR mengguncang wilayah Cianjur dan sekitarnya", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Anda belum bergabung menangani bencana ini. Bergabung untuk menambahkan laporan, data korban, dan bantuan.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onJoinClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Bergabung Menangani Bencana Ini")
                    }
                }
            }
        }
    }
}

@Composable
fun AssignedDisasterContent(navController: NavController, disasterId: String?) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Identitas", "Laporan", "Korban", "Bantuan")

    // Mock data for the lists
    val reports = listOf(ReportItem("r1", "Kondisi Terkini", "12 Nov 2025"), ReportItem("r2", "Kerusakan Infrastruktur", "11 Nov 2025"))
    val victims = listOf(VictimItem("v1", "Budi Santoso", "Luka Ringan"), VictimItem("v2", "Siti Aminah", "Hilang"))
    val aids = listOf(AidItem("a1", "Makanan", "100 dus"), AidItem("a2", "Pakaian", "50 karung"))

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        when (selectedTabIndex) {
            0 -> IdentityTabContent()
            1 -> ReportsTabContent(navController, reports)
            2 -> VictimsTabContent(navController, victims)
            3 -> AidsTabContent(navController, aids)
        }
    }
}

@Composable
fun IdentityTabContent() {
    val images = listOf(R.drawable.placeholder, R.drawable.placeholder, R.drawable.placeholder)
    // Use verticalScroll as content is static and of a known size
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ImageSlider(images = images)
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Gempa Bumi Cianjur", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Text("Gempa bumi berkekuatan 5.6 SR mengguncang wilayah Cianjur dan sekitarnya", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun ReportsTabContent(navController: NavController, reports: List<ReportItem>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reports) { report ->
            ListItemCard(
                title = report.title,
                subtitle = report.date,
                onClick = { navController.navigate("disaster-report-detail/${report.id}") }
            )
        }
    }
}

@Composable
fun VictimsTabContent(navController: NavController, victims: List<VictimItem>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(victims) { victim ->
            ListItemCard(
                title = victim.name,
                subtitle = victim.status,
                onClick = { navController.navigate("disaster-victim-detail/${victim.id}") }
            )
        }
    }
}

@Composable
fun AidsTabContent(navController: NavController, aids: List<AidItem>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(aids) { aid ->
            ListItemCard(
                title = aid.type,
                subtitle = aid.amount,
                onClick = { navController.navigate("disaster-aid-detail/${aid.id}") }
            )
        }
    }
}

@Composable
fun ListItemCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * A reusable composable for a slideable image gallery with dot indicators.
 */
@Composable
fun ImageSlider(images: List<Int>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Disaster Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { index ->
                val color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .size(8.dp)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun JoinConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Penugasan") },
        text = { Text("Apakah Anda yakin ingin bergabung menangani bencana ini? Anda akan dapat menambahkan laporan, data korban, dan bantuan.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Ya, Bergabung")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

// Previews
@Preview(showBackground = true, name = "Unassigned Light")
@Composable
fun DisasterDetailLight() {
    EDisasterTheme(darkTheme = false) {
        DisasterDetailScreen(navController = NavController(LocalContext.current), disasterId = "1")
    }
}

@Preview(showBackground = true, name = "Unassigned Dark")
@Composable
fun DisasterDetailDark() {
    EDisasterTheme(darkTheme = true) {
        DisasterDetailScreen(navController = NavController(LocalContext.current), disasterId = "1")
    }
}

@Preview(showBackground = true, name = "Assigned Light")
@Composable
fun DisasterDetailLight2() {
    EDisasterTheme(darkTheme = false) {
        DisasterDetailScreen(navController = NavController(LocalContext.current), disasterId = "2")
    }
}

@Preview(showBackground = true, name = "Assigned Dark")
@Composable
fun DisasterDetailDark2() {
    EDisasterTheme(darkTheme = true) {
        DisasterDetailScreen(navController = NavController(LocalContext.current), disasterId = "2")
    }
}
