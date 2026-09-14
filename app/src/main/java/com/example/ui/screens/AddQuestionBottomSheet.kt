package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MindLoopPrimaryButton
import com.example.ui.components.tapAffordance
import com.example.ui.theme.BackgroundOffWhite
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionBottomSheet(
    linkedNoteId: Long? = 1,
    subjectName: String = "Indian Polity",
    chapterName: String = "Fundamental Rights",
    sourceType: String = if (linkedNoteId != null) "note" else "note",
    sourceId: String = linkedNoteId?.toString() ?: "",
    onDismiss: () -> Unit,
    onSaveQuestion: (
        linkedNoteId: Long?,
        subject: String,
        chapter: String,
        type: String,
        questionText: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correctIdx: Int
    ) -> Unit,
    onSaveQuestionWithSource: ((
        linkedNoteId: Long?,
        subject: String,
        chapter: String,
        type: String,
        questionText: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correctIdx: Int,
        sourceType: String,
        sourceId: String
    ) -> Unit)? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var isTrueFalse by remember { mutableStateOf(false) }
    var questionText by remember { mutableStateOf("") }

    // MCQ fields
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctOptionIndex by remember { mutableIntStateOf(0) }

    // True/False correct answer: 0 for True, 1 for False
    var correctTfIndex by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "Linked to: Note #X" pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SageGreenLight)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Linked to: Note #${linkedNoteId ?: 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SageGreen
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepIndigo)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Add Practice Question",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Toggle for True/False vs. Multiple Choice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAEDF3))
                    .padding(3.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isTrueFalse) DeepIndigo else Color.Transparent)
                            .clickable { isTrueFalse = false }
                            .testTag("toggle_mcq"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Multiple Choice",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (!isTrueFalse) Color.White else DeepIndigo
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isTrueFalse) DeepIndigo else Color.Transparent)
                            .clickable { isTrueFalse = true }
                            .testTag("toggle_tf"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "True / False",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isTrueFalse) Color.White else DeepIndigo
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Question Input Field
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Write your question here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_question_text"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepIndigo,
                    unfocusedBorderColor = CardBorder
                ),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isTrueFalse) {
                // True / False correct answer selector
                Text(
                    text = "Select Correct Answer:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(0 to "True", 1 to "False").forEach { (idx, label) ->
                        val isSelected = correctTfIndex == idx
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .tapAffordance(shape = RoundedCornerShape(12.dp), elevation = 3.dp)
                                .clickable { correctTfIndex = idx },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SageGreenLight else SurfaceWhite
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) SageGreen else CardBorder
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) SageGreen else DeepIndigo
                                )
                            }
                        }
                    }
                }
            } else {
                // Multiple Choice: 4 options with radio selector
                Text(
                    text = "Answer Choices (Select radio for correct answer):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val options = listOf(
                    Triple(0, "Option A", optionA),
                    Triple(1, "Option B", optionB),
                    Triple(2, "Option C", optionC),
                    Triple(3, "Option D", optionD)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { (idx, label, currentVal) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = correctOptionIndex == idx,
                                onClick = { correctOptionIndex = idx },
                                colors = RadioButtonDefaults.colors(selectedColor = SageGreen)
                            )
                            OutlinedTextField(
                                value = currentVal,
                                onValueChange = { newVal ->
                                    when (idx) {
                                        0 -> optionA = newVal
                                        1 -> optionB = newVal
                                        2 -> optionC = newVal
                                        3 -> optionD = newVal
                                    }
                                },
                                label = { Text(label) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_option_$idx"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DeepIndigo,
                                    unfocusedBorderColor = CardBorder
                                ),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Question Button
            MindLoopPrimaryButton(
                onClick = {
                    val finalType = if (isTrueFalse) "TRUE_FALSE" else "MULTIPLE_CHOICE"
                    val correctIdx = if (isTrueFalse) correctTfIndex else correctOptionIndex
                    val qText = questionText.ifBlank { "Sample practice question on $chapterName" }
                    val opA = if (isTrueFalse) "True" else optionA.ifBlank { "Option 1" }
                    val opB = if (isTrueFalse) "False" else optionB.ifBlank { "Option 2" }
                    val opC = if (isTrueFalse) "" else optionC.ifBlank { "Option 3" }
                    val opD = if (isTrueFalse) "" else optionD.ifBlank { "Option 4" }

                    if (onSaveQuestionWithSource != null) {
                        onSaveQuestionWithSource(
                            linkedNoteId,
                            subjectName,
                            chapterName,
                            finalType,
                            qText,
                            opA,
                            opB,
                            opC,
                            opD,
                            correctIdx,
                            sourceType,
                            sourceId
                        )
                    } else {
                        onSaveQuestion(
                            linkedNoteId,
                            subjectName,
                            chapterName,
                            finalType,
                            qText,
                            opA,
                            opB,
                            opC,
                            opD,
                            correctIdx
                        )
                    }
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_question_button"),
                shape = RoundedCornerShape(14.dp),
                containerColor = DeepIndigo
            ) {
                Text(
                    text = "Save Question",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
