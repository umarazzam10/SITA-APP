package com.example.sitaa.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sitaa.R
import com.example.sitaa.utils.SharedPref
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToThesisList: () -> Unit,
    onNavigateToSeminarList: () -> Unit,
    onNavigateToDefenseList: () -> Unit,
    onNavigateToLogbookList: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPref.getInstance(context) }

    // Definisi gradasi warna biru dengan kontras yang lebih terlihat
    val gradientColors = listOf(
        Color(0xFF1976D2),
        Color(0xFF0D47A1)
    )

    LaunchedEffect(Unit) {
        viewModel.getCurrentUser(sharedPref)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 50.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(Color.White)
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.clickable { }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_home),
                                contentDescription = "Home",
                                tint = Color(0xFFFF4081),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Home",
                                color = Color(0xFFFF4081),
                                fontSize = 12.sp
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.clickable { }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_notification),
                                contentDescription = "Notifikasi",
                                tint = Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Notifikasi",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.clickable { onNavigateToProfile() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_profile),
                                contentDescription = "Profile",
                                tint = Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Profile",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = gradientColors
                        )
                    )
            ) {
                // Header section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 48.dp, bottom = 48.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(160.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Column {
                            Text(
                                text = "HELLO,",
                                color = Color.Black,
                                fontSize = 48.sp,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = viewModel.currentUser?.name ?: "Budi",
                                color = Color.Black,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp)) // Menambah jarak sebelum container

                // White container for menu grid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight() // Menggunakan fillMaxHeight agar mengisi sisa ruang
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(4) { index ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.85f)
                                    .clickable {
                                        when (index) {
                                            0 -> onNavigateToThesisList()
                                            1 -> onNavigateToSeminarList()
                                            2 -> onNavigateToDefenseList()
                                            3 -> onNavigateToLogbookList()
                                        }
                                    },
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 8.dp,
                                    pressedElevation = 4.dp,
                                ),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = when (index) {
                                            0 -> painterResource(R.drawable.img_pendaftar_ta)
                                            1 -> painterResource(R.drawable.img_seminar)
                                            2 -> painterResource(R.drawable.img_approval_sidang)
                                            else -> painterResource(R.drawable.img_logbook)
                                        },
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .padding(bottom = 16.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Text(
                                        text = when (index) {
                                            0 -> "Pendaftar TA"
                                            1 -> "Seminar"
                                            2 -> "Approval Sidang"
                                            else -> "Logbook"
                                        },
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        color = Color.Black,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}