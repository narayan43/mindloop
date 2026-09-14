package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.SageGreen

/**
 * Renders dedicated, rich hand-drawn study sketchnote sheets for psychology notes N053-N060.
 * Directly recreates the visual diagrams, charts, gauges, timelines, and decision flows
 * from the study notes so they serve as full visual study sheets.
 */
@Composable
fun VisualSketchnoteSheet(noteId: Long, modifier: Modifier = Modifier) {
    when (noteId) {
        53L -> BaselineSketchnoteSheet(modifier)
        54L -> NarrativeStructureSketchnoteSheet(modifier)
        55L -> PhysicalIndicatorsSketchnoteSheet(modifier)
        56L -> CognitiveLoadSketchnoteSheet(modifier)
        57L -> DRSClusteringSketchnoteSheet(modifier)
        58L -> TimeDistanceSketchnoteSheet(modifier)
        59L -> PersuasionLawSketchnoteSheet(modifier)
        60L -> SixMXProfilingSketchnoteSheet(modifier)
        else -> GenericVisualNoteCard(noteId, modifier)
    }
}

@Composable
private fun BaselineSketchnoteSheet(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFBF5))
            .border(1.5.dp, Color(0xFFE2D9CE), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        // Sketchnote Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2C3E66))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = "STEP 1: ESTABLISHING BEHAVIORAL BASELINE",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color(0xFFF6F8F5),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "\"You cannot spot a lie until you know what normal looks like.\"",
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFD4E0D2)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Golden Rule Ribbon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Rule: Baseline first, judgment later! Observe 3-5 min in casual chat.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Key Dimensions Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DimensionCard(
                title = "1. Speech Cadence",
                detail = "Tempo, rhythm, volume & natural vocal pitch",
                badge = "Acoustic",
                badgeColor = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )
            DimensionCard(
                title = "2. Blink Rate",
                detail = "Normal: 9-20 blinks/min\nSpike: 60+ /min",
                badge = "Ocular",
                badgeColor = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DimensionCard(
                title = "3. Linguistic Style",
                detail = "Use of \"I/we\" pronouns & natural contractions",
                badge = "Verbal",
                badgeColor = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            )
            DimensionCard(
                title = "4. Restlessness",
                detail = "Hand gestures, posture shifts & self-touching",
                badge = "Somatic",
                badgeColor = Color(0xFFF97316),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Blink Rate Gauge Visualization
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Blink Rate Baseline Gauge:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                            .background(Color(0xFF10B981))
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(14.dp)
                            .background(Color(0xFFF59E0B))
                    )
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                            .background(Color(0xFFEF4444))
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("9-20 (Normal)", fontSize = 10.sp, color = Color(0xFF047857), fontWeight = FontWeight.SemiBold)
                    Text("21-40 (Stress)", fontSize = 10.sp, color = Color(0xFFB45309), fontWeight = FontWeight.SemiBold)
                    Text("60+ (Cognitive Spike)", fontSize = 10.sp, color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun NarrativeStructureSketchnoteSheet(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFBF5))
            .border(1.5.dp, Color(0xFFE2D9CE), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E3A8A))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = "STEP 2: NARRATIVE STRUCTURE (Truth vs. Fabrication)",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = "Episodic Memory vs. Script Rehearsal",
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFBFDBFE)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Comparison Rows
        TruthFabricationRow(
            dimension = "1. Chronology",
            truth = "Starts with emotional climax, jumps naturally back/forth",
            fabrication = "Rigid step-by-step order; heavy unnecessary early detail"
        )
        Spacer(modifier = Modifier.height(6.dp))
        TruthFabricationRow(
            dimension = "2. Pronoun 'I'",
            truth = "High personal ownership (\"I saw\", \"I decided\")",
            fabrication = "Drops 'I' (\"Woke up, left\") to distance from guilt"
        )
        Spacer(modifier = Modifier.height(6.dp))
        TruthFabricationRow(
            dimension = "3. Contractions",
            truth = "Natural contractions (\"didn't\", \"won't\", \"couldn't\")",
            fabrication = "Stiff non-contractions (\"I did not do that\")"
        )
        Spacer(modifier = Modifier.height(6.dp))
        TruthFabricationRow(
            dimension = "4. Directness",
            truth = "Direct & explicit facts without hedging",
            fabrication = "Euphemisms (\"take\" vs \"steal\") & escape caveats"
        )
        Spacer(modifier = Modifier.height(6.dp))
        TruthFabricationRow(
            dimension = "5. Response Type",
            truth = "Directly denies specific accusation",
            fabrication = "\"Resume statements\" touting general good character"
        )
    }
}

@Composable
private fun PhysicalIndicatorsSketchnoteSheet(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFBF5))
            .border(1.5.dp, Color(0xFFE2D9CE), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4C1D95))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = "STEP 3: REAL-TIME PHYSICAL & PARALINGUISTIC CUES",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.5.sp,
                    color = Color.White
                )
                Text(
                    text = "Tracking Before (B), During (D) & After (A)",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFDDD6FE)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cues List
        CueBadgeCard(phase = "B/D/A", title = "Blink Rate Spike", desc = "Jump from 15/min to 60+ /min as working memory maxes out", color = Color(0xFFEF4444))
        Spacer(modifier = Modifier.height(6.dp))
        CueBadgeCard(phase = "B/D", title = "Vocal Hesitation & Pitch Rise", desc = "Unnatural pause to fabricate; vocal cords tighten under adrenaline", color = Color(0xFFF59E0B))
        Spacer(modifier = Modifier.height(6.dp))
        CueBadgeCard(phase = "D", title = "Cognitive Freeze", desc = "Hand & body gestures suddenly lock as brain routes CPU to lying", color = Color(0xFF3B82F6))
        Spacer(modifier = Modifier.height(6.dp))
        CueBadgeCard(phase = "B/D", title = "Pacifying / Shielding Gestures", desc = "Touching nose/mouth, lips compressed, tucking feet under chair", color = Color(0xFF8B5CF6))
        Spacer(modifier = Modifier.height(6.dp))
        CueBadgeCard(phase = "A", title = "Confirmation Glance", desc = "Rapid micro-glance right after claim to verify if you bought it", color = Color(0xFF10B981))
        Spacer(modifier = Modifier.height(6.dp))
        CueBadgeCard(phase = "D", title = "Micro-Incongruence", desc = "Mismatch: saying 'Yes' while subtly nodding 'No'", color = Color(0xFFDC2626))
    }
}

@Composable
private fun CognitiveLoadSketchnoteSheet(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFBF5))
            .border(1.5.dp, Color(0xFFE2D9CE), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F766E))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = "STEP 4: INCREASE COGNITIVE LOAD TO TEST INTEGRITY",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.5.sp,
                    color = Color.White
                )
                Text(
                    text = "\"Truth survives pressure; fabricated stories collapse.\"",
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFCCFBF1)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ProtocolCard(
            number = "1",
            title = "Reverse Chronology Recall",
            desc = "Ask subject to recount events backwards. Episodic memory traverses easily; fabricated linear scripts break down instantly."
        )
        Spacer(modifier = Modifier.height(6.dp))
        ProtocolCard(
            number = "2",
            title = "Sensory Detail Probing",
            desc = "Probe ambient sounds, smells, weather, lighting, textures. Real memories have rich sensory hooks; liars go blank."
        )
        Spacer(modifier = Modifier.height(6.dp))
        ProtocolCard(
            number = "3",
            title = "The Cognitive Freeze Check",
            desc = "Ask unexpected tangent questions. Watch for full body freeze, repeating the question, or prolonged throat clears."
        )
        Spacer(modifier = Modifier.height(6.dp))
        ProtocolCard(
            number = "4",
            title = "Perspective Shift",
            desc = "Ask what a third party bystander saw. Fabricated stories cannot re-render alternate viewing angles on the fly."
        )
    }
}

@Composable
private fun DRSClusteringSketchnoteSheet(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFBF5))
            .border(1.5.dp, Color(0xFFE2D9CE), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF991B1B))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = "STEP 5: SCORING VIA CLUSTERING (THE DRS RULE)",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = "\"One sign is noise. A cluster is a signal.\"",
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFFECACA)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // DRS 11+ Cluster Gauge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "★ The DRS 11+ Score Threshold",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF991B1B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Deception is indicated ONLY when a cluster of 11 or more points occurs in a single response cycle across B, D, and A.",
                    fontSize = 11.sp,
                    color = Color(0xFF7F1D1D),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5-Step Flow
        Text("5-Step Interrogation Flowchart:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = DeepIndigo)
        Spacer(modifier = Modifier.height(6.dp))

        val steps = listOf(
            "1. Observe initial cue",
            "2. Rule out alternative causes (fatigue, nervousness)",
            "3. Ask diagnostic follow-up question",
            "4. Track cluster across B/D/A (11+ DRS points)",
            "5. Form probabilistic conclusion"
        )

        steps.forEachIndexed { idx, s ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(0xFF2C3E66), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${idx + 1}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(s, fontSize = 11.sp, color = Color(0xFF1E293B))
            }
        }
    }
}

@Composable
private fun TimeDistanceSketchnoteSheet(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFBF5))
            .border(1.5.dp, Color(0xFFE2D9CE), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = "INFLUENCE & THE TIME-DISTANCE PROBLEM",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = "Change in Thought ➔ Change in Action",
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("The Time-Distance Metric:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepIndigo)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Goal: Get a subject to deviate from normal baseline to an extreme degree (Distance) in the shortest elapsed time (Time).",
                    fontSize = 11.sp,
                    color = Color(0xFF334155),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Core Hook Sequence
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFECFDF5), RoundedCornerShape(8.dp))
                .border(1.dp, SageGreen, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Core Progression Hook:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SageGreen)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    BadgePill("I (Self)")
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = DeepIndigo, modifier = Modifier.size(16.dp).padding(horizontal = 2.dp))
                    BadgePill("YOU (Subject)")
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = DeepIndigo, modifier = Modifier.size(16.dp).padding(horizontal = 2.dp))
                    BadgePill("SHIFT (Influence)")
                }
            }
        }
    }
}

@Composable
private fun PersuasionLawSketchnoteSheet(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFBF5))
            .border(1.5.dp, Color(0xFFE2D9CE), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF065F46))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = "THE 80/15/5 RULE OF PERSUASION",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = "Self-Mastery Governs Nonverbal Authority",
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFA7F3D0)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Donut breakdown
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PillarCard("80%", "Who You Are", "Your internal emotional state, certainty & nonverbal cues", Color(0xFF047857), Modifier.weight(1.3f))
            PillarCard("15%", "What You Do", "Tactics, timing & frame control", Color(0xFF2563EB), Modifier.weight(1f))
            PillarCard("5%", "The Subject", "Who the other person is", Color(0xFF9333EA), Modifier.weight(0.9f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("5 Pillars of Influence Presence:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = DeepIndigo)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "1. Confidence (Certainty)\n2. Discipline (Emotional control)\n3. Leadership (Frame setting)\n4. Gratitude (Disarming defensiveness)\n5. Enjoyment (High engagement)",
            fontSize = 11.sp,
            lineHeight = 18.sp,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun SixMXProfilingSketchnoteSheet(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFBF5))
            .border(1.5.dp, Color(0xFFE2D9CE), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4338CA))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = "THE SIX-MINUTE X-RAY (6MX) FRAMEWORK",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.5.sp,
                    color = Color.White
                )
                Text(
                    text = "6 Minutes to: Observe, Understand, Adapt",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFC7D2FE)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4 Quadrants
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuadrantCard("1. Needs", "Significance, Approval, Pity", Color(0xFFDC2626), Modifier.weight(1f))
            QuadrantCard("2. Decisions", "Conformity, Novelty, Deviance", Color(0xFF2563EB), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuadrantCard("3. Values", "Childhood lack & formative emotional triggers", Color(0xFF059669), Modifier.weight(1f))
            QuadrantCard("4. Baselines", "Resting blink rate, posture & stress micro-cues", Color(0xFFD97706), Modifier.weight(1f))
        }
    }
}

@Composable
private fun DimensionCard(title: String, detail: String, badge: String, badgeColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo)
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(badgeColor.copy(alpha = 0.15f)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                    Text(badge, fontSize = 9.sp, color = badgeColor, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(detail, fontSize = 10.sp, color = Color(0xFF475569), lineHeight = 14.sp)
        }
    }
}

@Composable
private fun TruthFabricationRow(dimension: String, truth: String, fabrication: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(dimension, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = DeepIndigo)
            Spacer(modifier = Modifier.height(3.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Column(modifier = Modifier.weight(1f).background(Color(0xFFF0FDF4), RoundedCornerShape(4.dp)).padding(6.dp)) {
                    Text("✓ TRUTH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    Text(truth, fontSize = 10.sp, color = Color(0xFF166534), lineHeight = 13.sp)
                }
                Column(modifier = Modifier.weight(1f).background(Color(0xFFFEF2F2), RoundedCornerShape(4.dp)).padding(6.dp)) {
                    Text("✗ FABRICATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    Text(fabrication, fontSize = 10.sp, color = Color(0xFF991B1B), lineHeight = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun CueBadgeCard(phase: String, title: String, desc: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(phase, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepIndigo)
            Text(desc, fontSize = 10.sp, color = Color(0xFF475569), lineHeight = 13.sp)
        }
    }
}

@Composable
private fun ProtocolCard(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0FDFA), RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFFCCFBF1), RoundedCornerShape(6.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color(0xFF0F766E), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF134E4A))
            Text(desc, fontSize = 10.sp, color = Color(0xFF115E59), lineHeight = 14.sp)
        }
    }
}

@Composable
private fun PillarCard(percent: String, title: String, desc: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(percent, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Text(title, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = DeepIndigo)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, fontSize = 9.sp, color = Color(0xFF475569), lineHeight = 12.sp)
        }
    }
}

@Composable
private fun QuadrantCard(title: String, desc: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, fontSize = 10.sp, color = Color(0xFF334155), lineHeight = 14.sp)
        }
    }
}

@Composable
private fun BadgePill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DeepIndigo)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun GenericVisualNoteCard(noteId: Long, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Visual Study Sheet #$noteId", fontSize = 13.sp, color = Color(0xFF64748B))
    }
}
