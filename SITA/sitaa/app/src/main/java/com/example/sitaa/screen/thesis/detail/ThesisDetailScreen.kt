package com.example.sitaa.screen.thesis.detail

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
import com.example.sitaa.utils.Constants.STATUS_PENDING
import com.example.sitaa.utils.SharedPref

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThesisDetailScreen(
    thesisId: Int,
    onNavigateBack: () -> Unit,
    viewModel: ThesisDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPref.getInstance(context) }

    LaunchedEffect(thesisId) {
        viewModel.getThesisDetail(thesisId, sharedPref)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 20.dp)
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
                viewModel.thesis?.let { thesis ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Profile Photo
                                AsyncImage(
                                    model = thesis.student.profilePhoto,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray),
                                    error = painterResource(id = R.drawable.avatars),
                                    placeholder = painterResource(id = R.drawable.avatars),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Name and NIM
                                Text(
                                    text = thesis.student.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = thesis.student.nim,
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Thesis Info
                                DetailInfoItem("Judul", thesis.title)
                                DetailInfoItem("Objek", thesis.researchObject)
                                DetailInfoItem("Metodologi", thesis.methodology)

                                Spacer(modifier = Modifier.height(32.dp))

                                // Download Button
                                Button(
                                    onClick = {
                                        thesis.attachmentFile?.let { fileName ->
                                            viewModel.downloadThesis(fileName, context, sharedPref)  // Menambahkan context sebagai parameter
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF8AAE)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "Download",
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Action Buttons
                    if (thesis.status == STATUS_PENDING) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
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
                                    Text(
                                        "Tolak",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.approveThesis(thesis.id, sharedPref, onNavigateBack)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "Approve",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Rejection Dialog
    if (viewModel.showRejectDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showRejectDialog = false },
            title = { Text("Tolak Pengajuan") },
            text = {
                OutlinedTextField(
                    value = viewModel.rejectionReason,
                    onValueChange = { viewModel.rejectionReason = it },
                    label = { Text("Alasan Penolakan") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.thesis?.let { thesis ->
                            viewModel.rejectThesis(thesis.id, sharedPref, onNavigateBack)
                        }
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
}

@Composable
private fun DetailInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "$label :",
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )
        Divider(
            modifier = Modifier.padding(top = 8.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}