package com.example.sitaa.screen.thesis.list

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sitaa.R
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.ThesisResponse
import com.example.sitaa.utils.Constants.STATUS_APPROVED
import com.example.sitaa.utils.Constants.STATUS_PENDING
import com.example.sitaa.utils.Constants.STATUS_REJECTED
import com.example.sitaa.utils.SharedPref

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThesisListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ThesisListViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPref.getInstance(context) }

    LaunchedEffect(Unit) {
        viewModel.getThesisList(sharedPref)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header Card containing back button, title and search
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
                        text = "Pendaftar TA",
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
            ElevatedFilterChip(
                selected = viewModel.selectedStatus == null,
                onClick = { viewModel.updateSelectedStatus(null) },
                label = {
                    Text(
                        "Semua",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                enabled = true,
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    selectedContainerColor = Color(0xFFFF8AAE),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White
                )
            )
            ElevatedFilterChip(
                selected = viewModel.selectedStatus == STATUS_APPROVED,
                onClick = { viewModel.updateSelectedStatus(STATUS_APPROVED) },
                label = {
                    Text(
                        "Disetujui",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                enabled = true,
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White
                )
            )
            ElevatedFilterChip(
                selected = viewModel.selectedStatus == STATUS_REJECTED,
                onClick = { viewModel.updateSelectedStatus(STATUS_REJECTED) },
                label = {
                    Text(
                        "Ditolak",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                enabled = true,
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    selectedContainerColor = Color(0xFFE53935),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White
                )
            )
        }

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
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.filteredList) { thesis ->
                    ThesisCard(
                        thesis = thesis,
                        onClick = { onNavigateToDetail(thesis.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ThesisCard(
    thesis: ThesisResponse.ThesisDetail,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Photo with AsyncImage and error handling
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = thesis.student.profilePhoto?.let { RetrofitClient.getFullFileUrl(it) },
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
                    text = thesis.student.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = thesis.student.nim,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Status Chip with correct alignment
            StatusChip(
                status = thesis.status,
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