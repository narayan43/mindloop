package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.NoteEntity
import com.example.ui.components.AccuracyDonutChart
import com.example.ui.components.InformationalCard
import com.example.ui.components.InteractiveCardBorder
import com.example.ui.components.StudyConsistencyHeatmap
import com.example.ui.components.StudyTimeSparklineCard
import com.example.ui.components.TimeSpentBySubjectChart
import com.example.ui.components.WeeklyQuestionsBarChart
import com.example.ui.components.tapAffordance
import com.example.ui.theme.Amber
import com.example.ui.theme.BackgroundOffWhite
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenDark
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    dueNotesCount: Int = 14,
    dueQuestionsCount: Int = 28,
    mistakesCount: Int = 5,
    todayStudyMinutes: Int = 135,
    todayQuestionsDone: Int = 45,
    todayAccuracy: Int = 82,
    streakDays: Int = 12,
    weeklyDayCounts: List<Pair<String, Int>> = listOf(
        "Mon" to 42, "Tue" to 65, "Wed" to 38, "Thu" to 55, "Fri" to 72, "Sat" to 25, "Sun" to 10
    ),
    timeSpentBySubject: List<Pair<String, Pair<String, Float>>> = listOf(
        "Polity" to ("6h 17m" to 0.9f),
        "History" to ("6h 42m" to 0.95f),
        "Geography" to ("4h 10m" to 0.6f),
        "Current Affairs" to ("6h 42m" to 0.95f)
    ),
    avgTimePerQuestionSec: Long = 24L,
    totalAttemptsCount: Int = 342,
    overallAccuracyPercent: Int = 78,
    totalStudyTimeThisWeekStr: String = "6h 42m",
    chapterAccuracies: List<com.example.data.firestore.DashboardChapterAccuracy> = emptyList(),
    mostRevisitedNotes: List<NoteEntity> = emptyList(),
    userName: String = "Ankit Sharma",
    userEmail: String = "ankit.sharma@gmail.com",
    onStudyNotesClick: () -> Unit,
    onReelsClick: () -> Unit = {},
    onReviewQuestionsClick: () -> Unit,
    onMistakesClick: () -> Unit,
    reelsCount: Int = 10,
    onNoteClick: (Long) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSyncClick: () -> Unit = {},
    onAdminClick: (() -> Unit)? = null,
    isAdmin: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val dateString = SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH).format(Date())

    val studyHours = todayStudyMinutes / 60
    val studyMins = todayStudyMinutes % 60
    val formattedStudyTime = if (studyHours > 0) "${studyHours}h ${studyMins}m" else "${studyMins}m"

    val slateBorder = Color(0xFFF1F5F9)
    val slate400 = Color(0xFF94A3B8)
    val slate500 = Color(0xFF64748B)

    val initials = remember(userName) {
        val parts = userName.trim().split(" ").filter { it.isNotBlank() }
        if (parts.size >= 2) {
            "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
        } else if (parts.isNotEmpty()) {
            parts[0].take(2).uppercase()
        } else {
            "AS"
        }
    }

    val firstName = remember(userName) {
        userName.trim().split(" ").firstOrNull()?.ifBlank { "Aspirant" } ?: "Aspirant"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundOffWhite)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header: Title + Subtitle on Left, Cloud Sync + Avatar on Right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good Morning, $firstName",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    color = DeepIndigo
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateString,
                    fontSize = 14.sp,
                    color = slate500,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Admin / Owner Dashboard shortcut button - strictly visible to authorized admin
                if (isAdmin && onAdminClick != null) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(elevation = 2.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1B4B))
                            .border(1.dp, Color(0xFFFDE047), CircleShape)
                            .clickable(onClick = onAdminClick)
                            .testTag("admin_dashboard_shortcut_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Admin Dashboard",
                            tint = Color(0xFFFACC15),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Cloud Sync button to trigger Firestore sync
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(elevation = 2.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                        .clickable(onClick = onSyncClick)
                        .testTag("firestore_sync_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudSync,
                        contentDescription = "Sync to Firestore",
                        tint = DeepIndigo,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Avatar circle - Clickable to open Auth / Account screen
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(elevation = 3.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E7FF))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable(onClick = onProfileClick)
                        .testTag("user_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF4338CA)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Streak Card: bg-white p-4 rounded-2xl shadow-sm border border-slate-100
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("streak_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, slateBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF7ED)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Streak Flame",
                        tint = Color(0xFFF97316),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "$streakDays Day Streak!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "You're in the top 5% this week. Keep it going!",
                        fontSize = 12.sp,
                        color = slate500
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3 Action Cards (Vertical Stack with space-y-3 / 10.dp spacing)
        // Raised with visible tap affordance shadow to indicate pressability
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Study Notes Card (Deep Indigo)
            MinimalActionRowCard(
                category = "STUDY",
                title = "Study Notes",
                badgeText = "$dueNotesCount Notes Due",
                backgroundColor = DeepIndigo,
                shadowColor = Color(0xFFC7D2FE).copy(alpha = 0.5f),
                testTag = "action_study_notes",
                onClick = onStudyNotesClick
            )

            // Reels Card (Terracotta #C1666B) - Between Study Notes and Review Questions
            MinimalActionRowCard(
                category = "REELS",
                title = "Study Reels",
                badgeText = "$reelsCount Reels",
                backgroundColor = Terracotta,
                shadowColor = Color(0xFFFECDD3).copy(alpha = 0.5f),
                testTag = "action_reels",
                onClick = onReelsClick
            )

            // Review Questions Card (Sage Green #7FA98F)
            MinimalActionRowCard(
                category = "TEST",
                title = "Review Questions",
                badgeText = "$dueQuestionsCount Ready",
                backgroundColor = SageGreen,
                shadowColor = Color(0xFFA7F3D0).copy(alpha = 0.5f),
                testTag = "action_review_questions",
                onClick = onReviewQuestionsClick
            )

            // Fix Mistakes Card (Amber #E0A458)
            MinimalActionRowCard(
                category = "CORRECT",
                title = "Fix Mistakes",
                badgeText = "$mistakesCount Concepts",
                backgroundColor = Amber,
                shadowColor = Color(0xFFFDE68A).copy(alpha = 0.5f),
                testTag = "action_mistakes",
                onClick = onMistakesClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Snapshot Card (Informational, flat with subtle border)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("today_snapshot_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = BorderStroke(1.dp, slateBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "TODAY'S SNAPSHOT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = slate400
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Studied
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Studied",
                            fontSize = 12.sp,
                            color = slate400
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedStudyTime,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                    }

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(slateBorder)
                    )

                    // Solved
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Solved",
                            fontSize = 12.sp,
                            color = slate400
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$todayQuestionsDone",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                    }

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(slateBorder)
                    )

                    // Accuracy
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Accuracy",
                            fontSize = 12.sp,
                            color = slate400
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$todayAccuracy%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ====================================================
        // STATS SECTION (Integrated into Home continuous scroll)
        // ====================================================
        Text(
            text = "Stats",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DeepIndigo,
            modifier = Modifier.testTag("section_stats_heading")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Streak card showing day-count and a row of 7 small day-indicator squares (Mon-Sun), sage green fill for completed days
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stats_streak_grid_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, slateBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$streakDays Day Streak",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigo
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Target: 45m / day • 6 of 7 days completed",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Text("🔥", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEachIndexed { idx, day ->
                            val isCompleted = idx <= 5 // Mon to Sat completed, Sun pending
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isCompleted) SageGreen else Color(0xFFEFF2F5))
                                        .border(
                                            width = 1.dp,
                                            color = if (isCompleted) SageGreenDark.copy(alpha = 0.25f) else Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isCompleted) DeepIndigo else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 2. Weekly Questions Attempted bar chart card (Mon-Sun bars)
            WeeklyQuestionsBarChart(dayCounts = weeklyDayCounts)

            // 3. Accuracy by Chapter donut chart card with a color-coded legend
            val chartColors = listOf(SageGreen, Amber, Terracotta, Color(0xFF7097B0))
            val donutSlices = if (chapterAccuracies.isNotEmpty()) {
                chapterAccuracies.take(4).mapIndexed { idx, item ->
                    Triple(item.chapter.take(14), item.accuracyPercent.coerceAtLeast(1), chartColors[idx % chartColors.size])
                }
            } else {
                listOf(
                    Triple("Polity", 35, SageGreen),
                    Triple("Geography", 25, Amber),
                    Triple("History", 20, Terracotta),
                    Triple("Current Affairs", 20, Color(0xFF7097B0))
                )
            }
            AccuracyDonutChart(slices = donutSlices)

            // 4. Row of three compact stat tiles: 'Avg. Time/Question', 'Total Attempts', 'Accuracy %'
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeStatTile(
                    label = "Avg. Time/Question",
                    value = "${avgTimePerQuestionSec}s",
                    modifier = Modifier.weight(1f)
                )
                HomeStatTile(
                    label = "Total Attempts",
                    value = "$totalAttemptsCount",
                    modifier = Modifier.weight(1f)
                )
                HomeStatTile(
                    label = "Accuracy %",
                    value = "$overallAccuracyPercent%",
                    modifier = Modifier.weight(1f)
                )
            }

            // 5. Total Study Time This Week card with a sparkline graph
            StudyTimeSparklineCard(totalTimeStr = totalStudyTimeThisWeekStr)

            // 6. Time Spent by Subject horizontal bar chart card
            TimeSpentBySubjectChart(items = timeSpentBySubject)

            // 7. Most Revisited Notes list card (top 3 notes with revisit counts)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stats_most_revisited_notes_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, slateBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Most Revisited Notes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DeepIndigo
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val topNotes = if (mostRevisitedNotes.isNotEmpty()) {
                        mostRevisitedNotes.take(3)
                    } else {
                        listOf(
                            NoteEntity(1, "UPSI", "Indian Polity", "Fundamental Rights", 3, "Part III: Fundamental Rights & Article 19-21", "", revisitCount = 9),
                            NoteEntity(2, "UPSI", "Indian Polity", "Preamble", 2, "Preamble & Objective Resolution", "", revisitCount = 6),
                            NoteEntity(5, "UPSI", "Geography", "Indian Drainage System", 2, "Himalayan vs Peninsular River Systems", "", revisitCount = 3)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        topNotes.forEach { note ->
                            // Each note item is clickable and has tap affordance shadow to indicate pressability
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .tapAffordance(shape = RoundedCornerShape(14.dp), elevation = 4.5.dp)
                                    .clickable { onNoteClick(note.id) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, InteractiveCardBorder),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 4.5.dp,
                                    pressedElevation = 1.5.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEEF2F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                        contentDescription = null,
                                        tint = DeepIndigo,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = note.title,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DeepIndigo,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${note.subjectName} • Chapter ${note.chapterNumber}",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SageGreenLight)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${note.revisitCount}x",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreen
                                    )
                                }
                            }
                        }
                    }
                    }
                }
            }

            // 8. Study Consistency heatmap card (GitHub-style contribution grid, last 4-5 weeks)
            StudyConsistencyHeatmap()
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun HomeStatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.5.sp,
                color = TextSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )
        }
    }
}

@Composable
private fun MinimalActionRowCard(
    category: String,
    title: String,
    badgeText: String,
    backgroundColor: Color,
    shadowColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "action_card_scale"
    )
    val currentElevation by animateDpAsState(
        targetValue = if (isPressed) 1.5.dp else 5.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "action_card_elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .tapAffordance(shape = RoundedCornerShape(16.dp), elevation = currentElevation)
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.5.dp,
            pressedElevation = 1.5.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}


