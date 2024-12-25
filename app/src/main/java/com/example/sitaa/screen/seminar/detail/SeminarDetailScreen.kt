package com.example.sitaa.screen.seminar.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.utils.Constants.STATUS_PENDING
import com.example.sitaa.utils.DateTimeUtils
import com.example.sitaa.utils.SharedPref

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeminarDetailScreen(
    seminarId: Int,
    onNavigateBack: () -> Unit,
    viewModel: SeminarDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPref.getInstance(context) }
    val scrollState = rememberScrollState()

    LaunchedEffect(seminarId) {
        viewModel.getSeminarDetail(seminarId, sharedPref)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Seminar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )

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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    viewModel.seminar?.let { seminar ->
                        // Profile Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Profile Photo
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                ) {
                                    AsyncImage(
                                        model = seminar.student.profilePhoto?.let {
                                            RetrofitClient.getFullFileUrl(it)
                                        },
                                        contentDescription = "Profile Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        error = painterResource(id = R.drawable.avatars),
                                        placeholder = painterResource(id = R.drawable.avatars),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Student Info
                                Text(
                                    text = seminar.student.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = seminar.student.nim,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Seminar Details
                                DetailInfoItem("Judul", seminar.title)
                                DetailInfoItem(
                                    "Tanggal Seminar",
                                    seminar.seminarDate?.let { DateTimeUtils.formatDate(it) } ?: "Belum ditentukan"
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.getGuidanceHistory(seminarId, sharedPref) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF8AAE)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Riwayat Bimbingan")
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.downloadThesisReview(context, seminarId, sharedPref)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2196F3)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = !viewModel.isDownloading
                                    ) {
                                        if (viewModel.isDownloading) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Text("Review TA")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Action Buttons for Pending Status
                viewModel.seminar?.let { seminar ->
                    if (seminar.status == STATUS_PENDING) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.showRejectDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE57373)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Tolak")
                                }

                                Button(
                                    onClick = { viewModel.showApproveDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Setujui")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Guidance History Dialog
    if (viewModel.showGuidanceDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showGuidanceDialog = false },
            title = { Text("Riwayat Bimbingan") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    viewModel.guidanceHistory?.logbook?.entries?.forEach { entry ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = DateTimeUtils.formatDate(entry.date),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = entry.activity)
                                if (!entry.lecturerNotes.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Catatan: ${entry.lecturerNotes}",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.showGuidanceDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Reject Dialog
    if (viewModel.showRejectDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showRejectDialog = false },
            title = { Text("Tolak Seminar") },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.rejectionReason,
                        onValueChange = { viewModel.rejectionReason = it },
                        label = { Text("Alasan Penolakan") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.suggestedDate,
                        onValueChange = { viewModel.suggestedDate = it },
                        label = { Text("Saran Tanggal (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.rejectSeminar(
                            viewModel.seminar?.id ?: 0,
                            sharedPref,
                            onNavigateBack
                        )
                    }
                ) {
                    Text("Tolak")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showRejectDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Approve Dialog
    if (viewModel.showApproveDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showApproveDialog = false },
            title = { Text("Setujui Seminar") },
            text = {
                OutlinedTextField(
                    value = viewModel.suggestedDate,
                    onValueChange = { viewModel.suggestedDate = it },
                    label = { Text("Tanggal Seminar (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.approveSeminar(
                            viewModel.seminar?.id ?: 0,
                            sharedPref,
                            onNavigateBack
                        )
                    }
                ) {
                    Text("Setujui")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showApproveDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun DetailInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        Divider(
            modifier = Modifier.padding(top = 8.dp),
            color = Color(0xFFE0E0E0)
        )
    }
}