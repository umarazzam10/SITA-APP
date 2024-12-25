package com.example.sitaa.screen.defense.detail

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
fun DefenseDetailScreen(
    defenseId: Int,
    onNavigateBack: () -> Unit,
    viewModel: DefenseDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPref.getInstance(context) }
    val scrollState = rememberScrollState()

    LaunchedEffect(defenseId) {
        viewModel.getDefenseDetail(defenseId, sharedPref)
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
                        text = "Detail Sidang",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
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
                viewModel.defense?.let { defense ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
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
                                        model = defense.student.profilePhoto?.let {
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
                                    text = defense.student.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = defense.student.nim,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Thesis & Defense Info
                                defense.seminar?.thesis?.let { thesis ->
                                    DetailInfoItem("Judul", thesis.title)
                                    DetailInfoItem("Objek Penelitian", thesis.researchObject)
                                    DetailInfoItem("Metodologi", thesis.methodology)
                                }

                                DetailInfoItem(
                                    "Tanggal Sidang",
                                    defense.defenseDate?.let {
                                        DateTimeUtils.formatDate(it)
                                    } ?: "Belum ditentukan"
                                )

                                if (!defense.approvalLetterFile.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = "Surat Persetujuan",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.align(Alignment.Start)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.viewApprovalLetter(
                                                    defenseId = defense.id,
                                                    context = context,
                                                    sharedPref = sharedPref
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2196F3)
                                            ),
                                            enabled = !viewModel.isDownloading
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_document),
                                                contentDescription = "View",
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Lihat")
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.downloadApprovalLetter(
                                                    defenseId = defense.id,
                                                    context = context,
                                                    sharedPref = sharedPref
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4CAF50)
                                            ),
                                            enabled = !viewModel.isDownloading
                                        ) {
                                            if (viewModel.isDownloading) {
                                                CircularProgressIndicator(
                                                    color = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            } else {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_download),
                                                    contentDescription = "Download",
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Download")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action Buttons for Pending Status
            viewModel.defense?.let { defense ->
                if (defense.status == STATUS_PENDING) {
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

    // Reject Dialog
    if (viewModel.showRejectDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showRejectDialog = false },
            title = { Text("Tolak Sidang") },
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
                        viewModel.rejectDefense(
                            viewModel.defense?.id ?: 0,
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
            title = { Text("Setujui Sidang") },
            text = {
                OutlinedTextField(
                    value = viewModel.suggestedDate,
                    onValueChange = { viewModel.suggestedDate = it },
                    label = { Text("Tanggal Sidang (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.approveDefense(
                            viewModel.defense?.id ?: 0,
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