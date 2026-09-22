package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppBackground
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppSurface
import com.example.ui.theme.AppSurfaceElevated
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SageGreen
import com.example.ui.theme.Terracotta
import com.example.ui.viewmodel.MindLoopViewModel

/**
 * Modal bottom sheet / dialog that allows users to clear or restore
 * the pre-loaded Starter Pack ("Indian Polity" curriculum).
 *
 * Guarantees that user-uploaded files, custom notes, created test questions,
 * and imported study packs remain 100% safe and untouched.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClearStarterPackBottomSheet(
    viewModel: MindLoopViewModel,
    onDismiss: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val isStarterCleared by viewModel.isStarterPackCleared.collectAsState()

    var isProcessing by remember { mutableStateOf(false) }
    var actionStatusMessage by remember { mutableStateOf<String?>(null) }
    var showAdvancedWipe by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("clear_starter_pack_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isStarterCleared) (if (isDark) Color(0xFF133322) else SageGreen.copy(alpha = 0.12f)) else (if (isDark) Color(0xFF3B151E) else Terracotta.copy(alpha = 0.12f))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isStarterCleared) Icons.Default.Refresh else Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = if (isStarterCleared) (if (isDark) Color(0xFF34D399) else SageGreen) else (if (isDark) Color(0xFFF87171) else Terracotta),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Starter Curriculum Pack",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTextPrimary
                        )
                        Text(
                            text = if (isStarterCleared) "Currently: Cleared (Custom Mode)" else "Currently: Pre-loaded Demo Data",
                            fontSize = 12.sp,
                            color = if (isStarterCleared) (if (isDark) Color(0xFF34D399) else SageGreen) else AppTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_starter_pack_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Safety Guarantee Banner
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                border = BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF133322) else SageGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF34D399) else SageGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Personal Data Is Protected",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Clearing the starter pack ONLY removes the built-in 'Indian Polity' sample curriculum. Any subjects you created, notes you added or uploaded, questions you created, and imported packs remain 100% safe and intact.",
                            fontSize = 12.sp,
                            color = AppTextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Starter Pack Content Breakdown Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AppSurface,
                border = BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Starter Pack Includes:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 1: Notes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF818CF8) else DeepIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "30 Revision Notes",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppTextPrimary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Indian Polity Chapters 1–30",
                            fontSize = 11.5.sp,
                            color = AppTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 2: Questions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF34D399) else SageGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "24 Test Questions",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppTextPrimary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "SRS Baseline Drill Sets",
                            fontSize = 11.5.sp,
                            color = AppTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 3: Reels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFFF87171) else Terracotta,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "10 Video Reels",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppTextPrimary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Micro-Lectures & Insights",
                            fontSize = 11.5.sp,
                            color = AppTextSecondary
                        )
                    }
                }
            }

            // Status message
            AnimatedVisibility(visible = actionStatusMessage != null) {
                actionStatusMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDark) Color(0xFF133322) else SageGreen.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF059669) else SageGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF34D399) else SageGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                fontSize = 12.5.sp,
                                color = AppTextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action Button: Clear or Restore
            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = if (isDark) Color(0xFF818CF8) else DeepIndigo,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else if (!isStarterCleared) {
                // Button to Clear Starter Pack
                Button(
                    onClick = {
                        isProcessing = true
                        actionStatusMessage = null
                        viewModel.clearStarterPack { result ->
                            isProcessing = false
                            actionStatusMessage = result.message
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFFEF4444) else Terracotta,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("action_clear_starter_pack")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Clear Starter Pack",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Button to Restore Starter Pack
                Button(
                    onClick = {
                        isProcessing = true
                        actionStatusMessage = null
                        viewModel.restoreStarterPack { success, msg ->
                            isProcessing = false
                            actionStatusMessage = msg
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF6366F1) else DeepIndigo,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("action_restore_starter_pack")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Restore Starter Pack",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Advanced Options Toggle
            TextButton(
                onClick = { showAdvancedWipe = !showAdvancedWipe },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (showAdvancedWipe) "Hide Advanced Options" else "Advanced Options (Fresh Start)",
                    fontSize = 12.sp,
                    color = AppTextSecondary
                )
            }

            AnimatedVisibility(visible = showAdvancedWipe) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color(0xFF2D1214) else Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF7F1D1D) else Color(0xFFFCA5A5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reset All App Data",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Clears everything including starter packs and any user-created notes or questions, leaving a completely blank slate.",
                            fontSize = 11.5.sp,
                            color = if (isDark) Color(0xFFFCA5A5) else Color(0xFF7F1D1D),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                isProcessing = true
                                viewModel.clearAllUserData { success, msg ->
                                    isProcessing = false
                                    actionStatusMessage = msg
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            border = BorderStroke(1.dp, if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("action_reset_all_data")
                        ) {
                            Text(
                                text = "Wipe All Data (Blank Slate)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
