package com.example.sitaa.screen.seminar.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sitaa.R
import com.example.sitaa.api.response.SeminarResponse.SeminarDetail
import com.example.sitaa.utils.Constants.STATUS_APPROVED
import com.example.sitaa.utils.Constants.STATUS_PENDING
import com.example.sitaa.utils.Constants.STATUS_REJECTED
import com.example.sitaa.utils.DateTimeUtils
import com.example.sitaa.utils.SharedPref

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeminarListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SeminarListViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPref.getInstance(context) }

    LaunchedEffect(Unit) {
        viewModel.getSeminarList(sharedPref)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // Back button and Title row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Seminar TA",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Search Bar
                TextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    singleLine = true
                )
            }
        }

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = viewModel.selectedStatus == null,
                onClick = { viewModel.updateSelectedStatus(null, sharedPref) },
                label = {
                    Text("Semua", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFF8AAE),
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = viewModel.selectedStatus == STATUS_APPROVED,
                onClick = { viewModel.updateSelectedStatus(STATUS_APPROVED, sharedPref) },
                label = {
                    Text("Disetujui", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50),
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = viewModel.selectedStatus == STATUS_REJECTED,
                onClick = { viewModel.updateSelectedStatus(STATUS_REJECTED, sharedPref) },
                label = {
                    Text("Ditolak", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFE53935),
                    selectedLabelColor = Color.White
                )
            )
        }

        // Content
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (viewModel.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.filteredList) { seminar ->
                    SeminarCard(
                        seminar = seminar,
                        onClick = { onNavigateToDetail(seminar.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SeminarCard(
    seminar: SeminarDetail,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Photo
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = seminar.student.profilePhoto,
                    contentDescription = "Profile Photo",
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(id = R.drawable.avatars),
                    placeholder = painterResource(id = R.drawable.avatars),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Student Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = seminar.student.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = seminar.student.nim,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tanggal: ${seminar.seminarDate?.let { DateTimeUtils.formatDate(it) } ?: "Belum ditentukan"}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Status Chip
            StatusChip(
                status = seminar.status,
                modifier = Modifier.align(Alignment.Top)
            )
        }
    }
}

@Composable
private fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor, text) = when (status.lowercase()) {
        "pending" -> Triple(Color(0xFFFFB74D), Color.White, "Pending")
        "approved" -> Triple(Color(0xFF4CAF50), Color.White, "Disetujui")
        "rejected" -> Triple(Color(0xFFE53935), Color.White, "Ditolak")
        else -> Triple(Color.Gray, Color.White, status)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}