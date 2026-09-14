package com.example.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.entity.ReelEntity
import com.example.ui.components.InteractiveCard
import com.example.ui.components.InteractiveCardBorder
import com.example.ui.components.MindLoopPrimaryButton
import com.example.ui.components.MindLoopSecondaryButton
import com.example.ui.components.StaticCardBorder
import com.example.ui.components.tapAffordance
import com.example.ui.theme.Amber
import com.example.ui.theme.AmberLight
import com.example.ui.theme.BackgroundOffWhite
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.DeepIndigoLight
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.ReelVideoCacheManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

// =========================================================================
// 1. REEL EXAM LIST SCREEN (Mirrors StudyExamListScreen)
// =========================================================================
@Composable
fun ReelExamListScreen(
    onSelectExam: (String) -> Unit,
    onSelectSubject: (String) -> Unit,
    onUploadReelClick: () -> Unit,
    allReels: List<ReelEntity>,
    customSubjects: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    val upsiReels = remember(allReels) { allReels.filter { it.exam.equals("UPSI", ignoreCase = true) } }
    val psychologyReels = remember(allReels) { allReels.filter { it.subject.equals("Psychology", ignoreCase = true) } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // Header: Title & Subtitle + Upload Reel action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Study Reels",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Bite-sized micro-learning video feed",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                IconButton(
                    onClick = onUploadReelClick,
                    modifier = Modifier.testTag("upload_reel_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload Reel",
                        tint = Terracotta
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Section 1: Enrolled Exam
                item {
                    Text(
                        text = "Enrolled Exam Course",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                item {
                    InteractiveCard(
                        onClick = { onSelectExam("UPSI") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reel_exam_card_upsi"),
                        shape = RoundedCornerShape(16.dp),
                        elevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DeepIndigo.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = DeepIndigo,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "UPSI - Police Sub-Inspector",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Polity, Law, General Hindi, Mental Aptitude",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(TerracottaLight)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${upsiReels.size} Video Reels",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Terracotta
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SageGreenLight)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Active Course",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SageGreen
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = DeepIndigo
                            )
                        }
                    }
                }

                // Section 2: Self-Study & Custom Subjects
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Self-Study & Micro-Learning",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                // Psychology card
                item {
                    InteractiveCard(
                        onClick = { onSelectSubject("Psychology") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reel_custom_subject_psychology"),
                        shape = RoundedCornerShape(16.dp),
                        elevation = 5.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Terracotta.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Psychology,
                                    contentDescription = null,
                                    tint = Terracotta,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Psychology",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Cognitive Psychology, Memory & Attention Reels",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(TerracottaLight)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                    Text(
                                        text = "${psychologyReels.size} Reels",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Terracotta
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = DeepIndigo
                            )
                        }
                    }
                }

                // Additional custom subjects
                val extraCustom = customSubjects.filter { !it.equals("Psychology", ignoreCase = true) && !it.equals("Indian Polity", ignoreCase = true) }
                items(extraCustom) { subj ->
                    val subjReels = allReels.filter { it.subject.equals(subj, ignoreCase = true) }
                    InteractiveCard(
                        onClick = { onSelectSubject(subj) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reel_custom_subject_${subj.lowercase().replace(" ", "_")}"),
                        shape = RoundedCornerShape(16.dp),
                        elevation = 5.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(DeepIndigo.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoLibrary,
                                    contentDescription = null,
                                    tint = DeepIndigo,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subj,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${subjReels.size} Video Reels",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = DeepIndigo
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // Floating Action Button to Upload Reel
        FloatingActionButton(
            onClick = onUploadReelClick,
            containerColor = Terracotta,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("fab_upload_reel")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = "Upload Reel")
                Text("Upload Reel", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// =========================================================================
// 2. REEL SUBJECTS GRID SCREEN (Mirrors StudySubjectsGridScreen)
// =========================================================================
@Composable
fun ReelSubjectsGridScreen(
    examName: String = "UPSI",
    onBackClick: () -> Unit,
    onSelectSubject: (String) -> Unit,
    allReels: List<ReelEntity>,
    modifier: Modifier = Modifier
) {
    val examReels = remember(allReels, examName) {
        allReels.filter { it.exam.equals(examName, ignoreCase = true) }
    }

    val distinctSubjects = remember(examReels) {
        val list = examReels.map { it.subject }.distinct().toMutableList()
        if (!list.contains("Indian Polity") && examName.equals("UPSI", ignoreCase = true)) {
            list.add(0, "Indian Polity")
        }
        if (!list.contains("Psychology")) {
            list.add("Psychology")
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("reel_subjects_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepIndigo
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "$examName - Video Reels",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                Text(
                    text = "Select a subject to explore reels",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(distinctSubjects) { subject ->
                val subjectReels = allReels.filter { it.subject.equals(subject, ignoreCase = true) }
                val totalDurationSec = subjectReels.sumOf { it.durationSeconds }
                val durationMin = totalDurationSec / 60

                InteractiveCard(
                    onClick = { onSelectSubject(subject) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reel_subject_card_${subject.lowercase().replace(" ", "_")}"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.5.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TerracottaLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = Terracotta,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = subject,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${subjectReels.size} Reels",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Amber,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (durationMin > 0) "${durationMin}m total" else "${totalDurationSec}s total",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 3. REEL SUBJECT DETAIL SCREEN (Mirrors StudySubjectDetailScreen)
// =========================================================================
@Composable
fun ReelSubjectDetailScreen(
    examName: String = "UPSI",
    subjectName: String = "Indian Polity",
    onBackClick: () -> Unit,
    onWatchFullSubject: () -> Unit,
    onWatchChapter: (String) -> Unit,
    onSelectReel: (Long) -> Unit,
    onUploadReelClick: () -> Unit,
    allReels: List<ReelEntity>,
    modifier: Modifier = Modifier
) {
    val subjectReels = remember(allReels, subjectName) {
        allReels.filter { it.subject.equals(subjectName, ignoreCase = true) }
    }

    val chaptersMap = remember(subjectReels) {
        subjectReels.groupBy { it.chapter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("reel_subject_detail_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepIndigo
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = subjectName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    Text(
                        text = "$examName • ${subjectReels.size} Reels",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            IconButton(
                onClick = onUploadReelClick,
                modifier = Modifier.testTag("reel_detail_upload_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = "Upload Reel",
                    tint = Terracotta
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // "Watch Full Subject" Hero Button
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tapAffordance(shape = RoundedCornerShape(18.dp), elevation = 6.dp)
                        .testTag("watch_full_subject_button")
                        .clickable { onWatchFullSubject() },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Terracotta)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WATCH FULL SUBJECT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Play All Reels Sequentially",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Continuous vertical feed for $subjectName",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Terracotta,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Chapters Breakdown Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chapters",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    Text(
                        text = "${chaptersMap.size} Chapters",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Chapter Cards with "Watch Chapter" button
            items(chaptersMap.keys.toList()) { chapter ->
                val reelsInChapter = chaptersMap[chapter] ?: emptyList()
                val totalSec = reelsInChapter.sumOf { it.durationSeconds }

                InteractiveCard(
                    onClick = { onWatchChapter(chapter) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chapter_card_${chapter.lowercase().replace(" ", "_")}"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = chapter,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${reelsInChapter.size} reels",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    Text("•", fontSize = 12.sp, color = TextSecondary)
                                    Text(
                                        text = "${totalSec}s total",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Button(
                                onClick = { onWatchChapter(chapter) },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("watch_chapter_${chapter.lowercase().replace(" ", "_")}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Watch", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Preview of reels inside chapter
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            reelsInChapter.forEach { reel ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .clickable { onSelectReel(reel.id) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = Terracotta,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = reel.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "${reel.durationSeconds}s",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

// =========================================================================
// 4. REEL FEED SCREEN (Fullscreen Vertical-Swipe Video Feed)
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelFeedScreen(
    initialReelId: Long? = null,
    filterExam: String? = null,
    filterSubject: String? = null,
    filterChapter: String? = null,
    onBackClick: () -> Unit,
    onAddQuestion: (reelId: Long, subject: String, chapter: String) -> Unit,
    onRecordWatch: (reelId: Long, watchSeconds: Long) -> Unit,
    onUploadReel: (title: String, description: String, subject: String, chapter: String, uri: Uri) -> Unit,
    allReels: List<ReelEntity>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Filter reels based on navigation scope
    val feedReels = remember(allReels, filterExam, filterSubject, filterChapter) {
        var filtered = allReels
        if (!filterExam.isNullOrBlank()) {
            filtered = filtered.filter { it.exam.equals(filterExam, ignoreCase = true) }
        }
        if (!filterSubject.isNullOrBlank()) {
            filtered = filtered.filter { it.subject.equals(filterSubject, ignoreCase = true) }
        }
        if (!filterChapter.isNullOrBlank()) {
            filtered = filtered.filter { it.chapter.equals(filterChapter, ignoreCase = true) }
        }
        if (filtered.isEmpty()) allReels else filtered
    }

    val initialIndex = remember(feedReels, initialReelId) {
        if (initialReelId != null) {
            val idx = feedReels.indexOfFirst { it.id == initialReelId }
            if (idx >= 0) idx else 0
        } else 0
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { feedReels.size }
    )

    // Global volume mute state (muted by default as specified in prompt)
    var isMuted by remember { mutableStateOf(true) }
    var showMuteIndicator by remember { mutableStateOf(false) }

    // Bottom sheet for uploading reel
    var showUploadDialog by remember { mutableStateOf(false) }

    // Bottom sheet for jumping chapters
    var showChaptersSheet by remember { mutableStateOf(false) }

    // Bottom sheet for adding question to the current reel
    var showAddQuestionSheet by remember { mutableStateOf(false) }
    var currentReelForQuestion by remember { mutableStateOf<ReelEntity?>(null) }

    // Auto-caching current and nearby reels in background
    LaunchedEffect(pagerState.currentPage, feedReels) {
        if (feedReels.isNotEmpty()) {
            val current = feedReels[pagerState.currentPage]
            ReelVideoCacheManager.cacheRemoteVideo(context, current.id, current.videoUrl)

            // Pre-cache next reel
            if (pagerState.currentPage + 1 < feedReels.size) {
                val next = feedReels[pagerState.currentPage + 1]
                ReelVideoCacheManager.cacheRemoteVideo(context, next.id, next.videoUrl)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (feedReels.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No Reels Available", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tap below to upload your first study reel", color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { showUploadDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) {
                    Text("Upload Reel")
                }
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("reel_vertical_pager"),
                beyondViewportPageCount = 1
            ) { page ->
                val reel = feedReels[page]
                val isCurrentPage = pagerState.currentPage == page

                SingleReelPlayerItem(
                    reel = reel,
                    isActive = isCurrentPage,
                    isMuted = isMuted,
                    onToggleMute = {
                        isMuted = !isMuted
                        showMuteIndicator = true
                    },
                    onOpenAddQuestion = {
                        currentReelForQuestion = reel
                        showAddQuestionSheet = true
                    },
                    onOpenChapters = {
                        showChaptersSheet = true
                    },
                    onRecordWatch = onRecordWatch,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Top Overlay Header (Back Button, Subject/Chapter chip, Mute Indicator, Upload Reel)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("reel_feed_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            if (feedReels.isNotEmpty()) {
                val currentReel = feedReels[pagerState.currentPage]
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${currentReel.subject} • ${currentReel.chapter}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Quick Mute / Unmute Header Button
                IconButton(
                    onClick = {
                        isMuted = !isMuted
                        showMuteIndicator = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .testTag("reel_mute_toggle_top")
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = if (isMuted) "Unmute" else "Mute",
                        tint = Color.White
                    )
                }

                // Upload Reel Button
                IconButton(
                    onClick = { showUploadDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .testTag("reel_feed_upload_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload Reel",
                        tint = Terracotta
                    )
                }
            }
        }

        // Animated Center Mute / Unmute Splash Notification
        AnimatedVisibility(
            visible = showMuteIndicator,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            LaunchedEffect(showMuteIndicator) {
                if (showMuteIndicator) {
                    delay(1200)
                    showMuteIndicator = false
                }
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
        }
    }

    // Modal Sheet: Jump Chapters
    if (showChaptersSheet && feedReels.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showChaptersSheet = false },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            val chapters = remember(feedReels) { feedReels.map { it.chapter }.distinct() }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Jump to Chapter",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                Spacer(modifier = Modifier.height(14.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(chapters) { chapter ->
                        val firstIndex = feedReels.indexOfFirst { it.chapter == chapter }
                        val isCurrent = feedReels[pagerState.currentPage].chapter == chapter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrent) TerracottaLight else BackgroundOffWhite)
                                .clickable {
                                    if (firstIndex >= 0) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(firstIndex)
                                        }
                                    }
                                    showChaptersSheet = false
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = chapter,
                                fontSize = 15.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCurrent) Terracotta else DeepIndigo
                            )
                            if (isCurrent) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Terracotta)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Modal Sheet: Add Question to Reel
    if (showAddQuestionSheet && currentReelForQuestion != null) {
        val targetReel = currentReelForQuestion!!
        AddQuestionBottomSheet(
            linkedNoteId = null,
            subjectName = targetReel.subject,
            chapterName = targetReel.chapter,
            sourceType = "reel",
            sourceId = targetReel.id.toString(),
            onDismiss = { showAddQuestionSheet = false },
            onSaveQuestion = { _, _, _, _, _, _, _, _, _, _ -> },
            onSaveQuestionWithSource = { _, subj, chap, type, qText, opA, opB, opC, opD, correctIdx, srcType, srcId ->
                onAddQuestion(targetReel.id, subj, chap)
                showAddQuestionSheet = false
            }
        )
    }

    // Dialog / Sheet: Upload Reel
    if (showUploadDialog) {
        UploadReelDialog(
            defaultExam = filterExam ?: "UPSI",
            defaultSubject = filterSubject ?: "Indian Polity",
            defaultChapter = filterChapter ?: "Fundamental Rights",
            onDismiss = { showUploadDialog = false },
            onConfirmUpload = { title, desc, subj, chap, uri ->
                showUploadDialog = false
                onUploadReel(title, desc, subj, chap, uri)
            }
        )
    }
}

// =========================================================================
// 5. SINGLE REEL PLAYER ITEM (With VideoView & Controls Overlay)
// =========================================================================
@Composable
private fun SingleReelPlayerItem(
    reel: ReelEntity,
    isActive: Boolean,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onOpenAddQuestion: () -> Unit,
    onOpenChapters: () -> Unit,
    onRecordWatch: (reelId: Long, watchSeconds: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var showDescriptionExpanded by remember { mutableStateOf(false) }

    // Playback tracking
    var videoProgress by remember { mutableFloatStateOf(0f) }
    var currentPositionSec by remember { mutableIntStateOf(0) }
    var watchSecondsAccumulator by remember { mutableLongStateOf(0L) }

    // VideoView & MediaPlayer reference
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    // Handle Mute changes
    LaunchedEffect(isMuted, mediaPlayerRef) {
        mediaPlayerRef?.let { mp ->
            try {
                if (isMuted) {
                    mp.setVolume(0f, 0f)
                } else {
                    mp.setVolume(1f, 1f)
                }
            } catch (e: Exception) {
                // Handle release or preparation states safely
            }
        }
    }

    // Handle Active/Inactive page transitions
    LaunchedEffect(isActive, videoViewRef) {
        videoViewRef?.let { vv ->
            try {
                if (isActive) {
                    if (!vv.isPlaying) {
                        vv.start()
                    }
                    isPlaying = true
                } else {
                    if (vv.isPlaying) {
                        vv.pause()
                    }
                    isPlaying = false
                    // Record watch duration when user leaves this reel
                    if (watchSecondsAccumulator > 0) {
                        onRecordWatch(reel.id, watchSecondsAccumulator)
                        watchSecondsAccumulator = 0L
                    }
                }
            } catch (e: Exception) {
                // Handle safely
            }
        }
    }

    // Periodic watch time & progress polling loop
    LaunchedEffect(isActive, isPlaying) {
        while (isActive && isPlaying) {
            delay(1000)
            watchSecondsAccumulator += 1
            videoViewRef?.let { vv ->
                try {
                    val pos = vv.currentPosition
                    val dur = vv.duration
                    if (dur > 0) {
                        videoProgress = (pos.toFloat() / dur.toFloat()).coerceIn(0f, 1f)
                        currentPositionSec = pos / 1000
                    }
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
    }

    DisposableEffect(reel.id) {
        onDispose {
            if (watchSecondsAccumulator > 0) {
                onRecordWatch(reel.id, watchSecondsAccumulator)
            }
            try {
                videoViewRef?.stopPlayback()
                mediaPlayerRef = null
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Tap anywhere on video toggles mute (with audio feedback overlay)
                        onToggleMute()
                    },
                    onDoubleTap = {
                        // Double tap toggles play/pause
                        videoViewRef?.let { vv ->
                            try {
                                if (vv.isPlaying) {
                                    vv.pause()
                                    isPlaying = false
                                } else {
                                    vv.start()
                                    isPlaying = true
                                }
                            } catch (e: Exception) {
                                // Ignore
                            }
                        }
                    }
                )
            }
    ) {
        // Aesthetic dark backdrop so there is never a harsh unstyled flash
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DeepIndigo.copy(alpha = 0.95f),
                            Color(0xFF1E2432),
                            Color.Black
                        )
                    )
                )
        )

        // Video Player using standard Android VideoView (Zero external dependencies)
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    tag = reel.id

                    val localUri = ReelVideoCacheManager.getLocalPlaybackUri(ctx, reel.id, reel.videoUrl)
                    if (localUri.startsWith("/") || localUri.startsWith("file://")) {
                        setVideoPath(localUri)
                    } else {
                        setVideoURI(Uri.parse(localUri))
                    }

                    setOnPreparedListener { mp ->
                        mediaPlayerRef = mp
                        mp.isLooping = true
                        try {
                            if (isMuted) {
                                mp.setVolume(0f, 0f)
                            } else {
                                mp.setVolume(1f, 1f)
                            }
                        } catch (e: Exception) {
                            // Safe fallback
                        }
                        if (isActive) {
                            start()
                            isPlaying = true
                        }
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.w("SingleReelPlayerItem", "Playback error what=$what extra=$extra for reel ${reel.id}. Recovering with local fallback...")
                        try {
                            post {
                                try {
                                    stopPlayback()
                                    val fallbackUri = ReelVideoCacheManager.getBundledSampleVideoUri(ctx, reel.id)
                                    setVideoURI(Uri.parse(fallbackUri))
                                    if (isActive) {
                                        start()
                                    }
                                } catch (e: Exception) {
                                    Log.e("SingleReelPlayerItem", "Failed to switch to fallback: ${e.message}")
                                }
                            }
                        } catch (e: Exception) {
                            // Ignore
                        }
                        true
                    }

                    videoViewRef = this
                }
            },
            update = { vv ->
                videoViewRef = vv
                val currentTag = vv.tag as? Long
                if (currentTag != reel.id) {
                    vv.tag = reel.id
                    try {
                        vv.stopPlayback()
                        val localUri = ReelVideoCacheManager.getLocalPlaybackUri(vv.context, reel.id, reel.videoUrl)
                        if (localUri.startsWith("/") || localUri.startsWith("file://")) {
                            vv.setVideoPath(localUri)
                        } else {
                            vv.setVideoURI(Uri.parse(localUri))
                        }
                        if (isActive) {
                            vv.start()
                        }
                    } catch (e: Exception) {
                        Log.e("SingleReelPlayerItem", "Error updating video URI: ${e.message}")
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for bottom readable text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Right Vertical Action Rail (Add Question, Mute, Views, Chapter Picker)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // "Add Question" button linked to Reel ID
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onOpenAddQuestion() }
                    .testTag("action_add_question_to_reel")
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Terracotta),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Question",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "+ Question",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Mute / Unmute Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onToggleMute() }
                    .testTag("action_toggle_mute_rail")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isMuted) "Muted" else "Sound",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }

            // Chapters Selector
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onOpenChapters() }
                    .testTag("action_open_chapters")
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistPlay,
                        contentDescription = "Chapters",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Chapters",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }

            // Views Counter
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Views",
                        tint = SageGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${reel.watchCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Bottom Left Info: Title, Description, Subject Chip, Scrubber
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .navigationBarsPadding()
                .padding(start = 20.dp, bottom = 24.dp)
        ) {
            // Subject & Chapter Tag
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Terracotta)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = reel.subject,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = reel.chapter,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = reel.title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Description
            if (reel.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reel.description,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    maxLines = if (showDescriptionExpanded) 6 else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { showDescriptionExpanded = !showDescriptionExpanded }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Video Scrubber / Progress Bar
            LinearProgressIndicator(
                progress = { videoProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Terracotta,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

// =========================================================================
// 6. UPLOAD REEL DIALOG
// =========================================================================
@Composable
fun UploadReelDialog(
    defaultExam: String,
    defaultSubject: String,
    defaultChapter: String,
    onDismiss: () -> Unit,
    onConfirmUpload: (title: String, description: String, subject: String, chapter: String, uri: Uri) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf(defaultSubject) }
    var chapter by remember { mutableStateOf(defaultChapter) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Terracotta)
                Text(
                    text = "Upload Study Reel",
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select an MP4 video clip from your device and categorize it by subject & chapter.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reel Title (e.g. Article 21 Explained)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_reel_input_title")
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject (e.g. Indian Polity)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = chapter,
                    onValueChange = { chapter = it },
                    label = { Text("Chapter (e.g. Fundamental Rights)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Short Summary / Notes") },
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Video selection button
                Button(
                    onClick = {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedVideoUri != null) SageGreen else DeepIndigo
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_video_button")
                ) {
                    Icon(
                        imageVector = if (selectedVideoUri != null) Icons.Default.Movie else Icons.Default.CloudUpload,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedVideoUri != null) "Video Clip Selected ✓" else "Choose Video from Device",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedVideoUri != null) {
                        onConfirmUpload(
                            title.trim(),
                            description.trim(),
                            subject.trim().ifBlank { "Indian Polity" },
                            chapter.trim().ifBlank { "Fundamental Rights" },
                            selectedVideoUri!!
                        )
                    }
                },
                enabled = title.isNotBlank() && selectedVideoUri != null,
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_upload_reel_button")
            ) {
                Text("Upload Reel")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(18.dp)
    )
}
