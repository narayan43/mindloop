package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

// 1. Weekly Questions Attempted Bar Chart (Mon-Sun)
@Composable
fun WeeklyQuestionsBarChart(
    dayCounts: List<Pair<String, Int>> = listOf(
        "Mon" to 42,
        "Tue" to 65,
        "Wed" to 38,
        "Thu" to 55,
        "Fri" to 72,
        "Sat" to 25,
        "Sun" to 10
    ),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Questions Attempted",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DeepIndigo
            )

            Spacer(modifier = Modifier.height(18.dp))

            val maxVal = dayCounts.maxOfOrNull { it.second } ?: 1

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dayCounts.forEach { (day, count) ->
                    val barHeightFraction = (count.toFloat() / maxVal.toFloat()).coerceIn(0.1f, 1f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$count",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height((80 * barHeightFraction).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(DeepIndigo)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = day,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// 2. Accuracy by Chapter Donut Chart with Legend
@Composable
fun AccuracyDonutChart(
    slices: List<Triple<String, Int, Color>> = listOf(
        Triple("Polity", 35, SageGreen),
        Triple("Geography", 25, Amber),
        Triple("History", 20, Terracotta),
        Triple("Current Affairs", 20, Color(0xFF7097B0))
    ),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Accuracy by Chapter",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DeepIndigo
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Donut Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.size(130.dp)) {
                    val strokeWidth = 32.dp.toPx()
                    val total = slices.sumOf { it.second }.toFloat()
                    var startAngle = -90f

                    slices.forEach { slice ->
                        val sweepAngle = (slice.second / total) * 360f
                        drawArc(
                            color = slice.third,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle - 2f, // small gap
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += sweepAngle
                    }
                }

                // Center percentage callout
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Avg",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "78%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Legend
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    slices.take(2).forEach { (label, percent, color) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(color, RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$label $percent%",
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    slices.drop(2).forEach { (label, percent, color) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(color, RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$label $percent%",
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// 3. Total Study Time Sparkline Card
@Composable
fun StudyTimeSparklineCard(
    totalTimeStr: String = "6h 42m",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Study Time This Week",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "⏱",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = totalTimeStr,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Wave Sparkline in Sage Green
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                val width = size.width
                val height = size.height

                val points = listOf(
                    Offset(0f, height * 0.8f),
                    Offset(width * 0.15f, height * 0.75f),
                    Offset(width * 0.35f, height * 0.5f),
                    Offset(width * 0.5f, height * 0.7f),
                    Offset(width * 0.65f, height * 0.3f),
                    Offset(width * 0.82f, height * 0.45f),
                    Offset(width, height * 0.35f)
                )

                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val midX = (prev.x + curr.x) / 2
                        val midY = (prev.y + curr.y) / 2
                        quadraticTo(prev.x, prev.y, midX, midY)
                    }
                    lineTo(points.last().x, points.last().y)
                }

                // Fill area under sparkline
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(SageGreen.copy(alpha = 0.35f), Color.Transparent)
                    )
                )

                // Line stroke
                drawPath(
                    path = path,
                    color = SageGreen,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

// 4. Horizontal Time Spent by Subject Bar Chart
@Composable
fun TimeSpentBySubjectChart(
    items: List<Pair<String, Pair<String, Float>>> = listOf(
        "Polity" to ("6h 17m" to 0.9f),
        "History" to ("6h 42m" to 0.95f),
        "Geography" to ("4h 10m" to 0.6f),
        "Current Affairs" to ("6h 42m" to 0.95f)
    ),
    colors: List<Color> = listOf(DeepIndigo, SageGreen, Amber, Terracotta),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Time Spent by Subject",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DeepIndigo
            )

            Spacer(modifier = Modifier.height(14.dp))

            items.forEachIndexed { index, (subject, timePair) ->
                val (timeStr, progress) = timePair
                val color = colors.getOrElse(index) { DeepIndigo }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subject,
                        fontSize = 12.5.sp,
                        color = TextPrimary,
                        modifier = Modifier.width(90.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFFEFF2F5))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(color)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = timeStr,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.width(55.dp)
                    )
                }
            }
        }
    }
}

// 5. GitHub-style Study Consistency Heatmap (5 weeks x 7 days)
@Composable
fun StudyConsistencyHeatmap(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Study Consistency",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DeepIndigo
                )
                Text(
                    text = "Last 4–5 weeks",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5 rows (Mon to Fri/Sun) x 12 columns grid simulating heatmap
            val weeks = 12
            val days = 5
            val matrix = listOf(
                listOf(0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 3, 3),
                listOf(0, 0, 0, 0, 0, 1, 1, 2, 3, 2, 3, 0),
                listOf(0, 0, 0, 0, 1, 1, 2, 3, 3, 3, 3, 0),
                listOf(0, 0, 0, 0, 0, 1, 2, 2, 3, 2, 3, 0),
                listOf(0, 0, 0, 0, 1, 2, 2, 3, 2, 2, 3, 0)
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                matrix.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        row.forEach { level ->
                            val color = when (level) {
                                0 -> Color(0xFFE2E8F0)
                                1 -> SageGreen.copy(alpha = 0.45f)
                                2 -> SageGreen.copy(alpha = 0.8f)
                                else -> Color(0xFF2C5530)
                            }
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend at bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No study days",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        Color(0xFFE2E8F0),
                        SageGreen.copy(alpha = 0.45f),
                        SageGreen.copy(alpha = 0.8f),
                        Color(0xFF2C5530)
                    ).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}
