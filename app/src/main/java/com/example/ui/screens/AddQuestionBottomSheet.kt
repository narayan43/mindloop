package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.QuestionEntity
import com.example.ui.components.MindLoopPrimaryButton
import com.example.ui.components.tapAffordance
import com.example.ui.theme.AppBackground
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppSurface
import com.example.ui.theme.AppSurfaceElevated
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class BulkQuestionDraft(
    var questionText: String = "",
    var isTrueFalse: Boolean = false,
    var optionA: String = "",
    var optionB: String = "",
    var optionC: String = "",
    var optionD: String = "",
    var correctOptionIndex: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionBottomSheet(
    linkedNoteId: Long? = 1,
    subjectName: String = "Indian Polity",
    chapterName: String = "Fundamental Rights",
    sourceType: String = if (linkedNoteId != null) "note" else "note",
    sourceId: String = linkedNoteId?.toString() ?: "",
    initialTab: Int = 0, // 0: Single, 1: Bulk Add (3-5+), 2: CSV / File Import
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
    onSaveBulkQuestions: ((List<QuestionEntity>) -> Unit)? = null,
    onImportCsv: ((String) -> Unit)? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val isDark = LocalIsDarkTheme.current
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    // --- Tab 0: Single Question State ---
    var singleIsTrueFalse by remember { mutableStateOf(false) }
    var singleQuestionText by remember { mutableStateOf("") }
    var singleOptionA by remember { mutableStateOf("") }
    var singleOptionB by remember { mutableStateOf("") }
    var singleOptionC by remember { mutableStateOf("") }
    var singleOptionD by remember { mutableStateOf("") }
    var singleCorrectOptionIndex by remember { mutableIntStateOf(0) }
    var singleCorrectTfIndex by remember { mutableIntStateOf(0) }

    // --- Tab 1: Bulk Questions State (Starts with 3 to 5 questions) ---
    val bulkDrafts = remember {
        mutableStateListOf(
            BulkQuestionDraft(questionText = "", isTrueFalse = false, optionA = "", optionB = "", optionC = "", optionD = "", correctOptionIndex = 0),
            BulkQuestionDraft(questionText = "", isTrueFalse = false, optionA = "", optionB = "", optionC = "", optionD = "", correctOptionIndex = 0),
            BulkQuestionDraft(questionText = "", isTrueFalse = false, optionA = "", optionB = "", optionC = "", optionD = "", correctOptionIndex = 0)
        )
    }

    // --- Tab 2: CSV File / Content State ---
    var csvContentText by remember { mutableStateOf("") }

    // File picker launcher for CSV or Text files
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                    csvContentText = reader.readText()
                    Toast.makeText(context, "CSV file loaded successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Parsed preview questions from CSV text
    val parsedCsvQuestions by remember(csvContentText) {
        derivedStateOf {
            if (csvContentText.isBlank()) emptyList()
            else parseCsvQuestionsPreview(
                csvText = csvContentText,
                subject = subjectName,
                chapter = chapterName,
                sourceType = sourceType,
                sourceId = sourceId,
                linkedNoteId = linkedNoteId
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Source Badge and Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (sourceType == "reel") (if (isDark) Color(0xFF3B151E) else TerracottaLight) else (if (isDark) Color(0xFF133322) else SageGreenLight))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (sourceType == "reel") "Linked to: Reel #${sourceId.ifBlank { "Active" }}"
                               else "Linked to: Note #${linkedNoteId ?: 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sourceType == "reel") (if (isDark) Color(0xFFF87171) else Terracotta) else (if (isDark) Color(0xFF34D399) else SageGreen)
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.testTag("button_dismiss_sheet")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = AppTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (sourceType == "reel") "Add Questions to Reel" else "Add Practice Questions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextPrimary
            )
            Text(
                text = "$subjectName • $chapterName",
                fontSize = 13.sp,
                color = AppTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Selector: 3 Tabs (Single, Bulk 3-5+, CSV Import)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEAEDF3))
                    .padding(3.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Tab 0: Single
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 0) (if (isDark) Color(0xFF4F46E5) else DeepIndigo) else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .testTag("tab_single_question"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Single (1)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) Color.White else AppTextSecondary
                        )
                    }

                    // Tab 1: Bulk Add
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 1) (if (isDark) Color(0xFF4F46E5) else DeepIndigo) else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .testTag("tab_bulk_questions"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Bulk (3–5+)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) Color.White else AppTextSecondary
                        )
                    }

                    // Tab 2: CSV / File Import
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == 2) (if (isDark) Color(0xFF4F46E5) else DeepIndigo) else Color.Transparent)
                            .clickable { selectedTab = 2 }
                            .testTag("tab_csv_import"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = if (selectedTab == 2) Color.White else AppTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Import CSV",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 2) Color.White else AppTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =================================================================
            // TAB 0: SINGLE QUESTION ENTRY
            // =================================================================
            if (selectedTab == 0) {
                // Toggle MCQ vs True/False
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F3F8))
                        .padding(2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!singleIsTrueFalse) (if (isDark) Color(0xFFEF4444) else Terracotta) else Color.Transparent)
                                .clickable { singleIsTrueFalse = false }
                                .testTag("toggle_mcq"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Multiple Choice (MCQ)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (!singleIsTrueFalse) Color.White else AppTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (singleIsTrueFalse) (if (isDark) Color(0xFFEF4444) else Terracotta) else Color.Transparent)
                                .clickable { singleIsTrueFalse = true }
                                .testTag("toggle_tf"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "True / False",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (singleIsTrueFalse) Color.White else AppTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = singleQuestionText,
                    onValueChange = { singleQuestionText = it },
                    label = { Text("Write your question here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_question_text"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Color(0xFF818CF8) else DeepIndigo,
                        unfocusedBorderColor = AppBorder,
                        focusedTextColor = AppTextPrimary,
                        unfocusedTextColor = AppTextPrimary,
                        focusedContainerColor = AppSurface,
                        unfocusedContainerColor = AppSurface
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (singleIsTrueFalse) {
                    Text(
                        text = "Select Correct Answer:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (singleCorrectTfIndex == 0) (if (isDark) Color(0xFF133322) else SageGreenLight) else AppSurface)
                                .clickable { singleCorrectTfIndex = 0 }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = singleCorrectTfIndex == 0,
                                onClick = { singleCorrectTfIndex = 0 },
                                colors = RadioButtonDefaults.colors(selectedColor = if (isDark) Color(0xFF34D399) else SageGreen)
                            )
                            Text("True", fontWeight = FontWeight.Bold, color = AppTextPrimary)
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (singleCorrectTfIndex == 1) (if (isDark) Color(0xFF133322) else SageGreenLight) else AppSurface)
                                .clickable { singleCorrectTfIndex = 1 }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = singleCorrectTfIndex == 1,
                                onClick = { singleCorrectTfIndex = 1 },
                                colors = RadioButtonDefaults.colors(selectedColor = if (isDark) Color(0xFF34D399) else SageGreen)
                            )
                            Text("False", fontWeight = FontWeight.Bold, color = AppTextPrimary)
                        }
                    }
                } else {
                    Text(
                        text = "Options & Select Correct Answer:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val options = listOf(
                        Triple("Option A", singleOptionA, 0),
                        Triple("Option B", singleOptionB, 1),
                        Triple("Option C", singleOptionC, 2),
                        Triple("Option D", singleOptionD, 3)
                    )

                    options.forEach { (label, value, index) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = singleCorrectOptionIndex == index,
                                onClick = { singleCorrectOptionIndex = index },
                                colors = RadioButtonDefaults.colors(selectedColor = if (isDark) Color(0xFFF87171) else Terracotta),
                                modifier = Modifier.testTag("radio_option_$index")
                            )
                            OutlinedTextField(
                                value = value,
                                onValueChange = { newVal ->
                                    when (index) {
                                        0 -> singleOptionA = newVal
                                        1 -> singleOptionB = newVal
                                        2 -> singleOptionC = newVal
                                        3 -> singleOptionD = newVal
                                    }
                                },
                                label = { Text("$label ${if (singleCorrectOptionIndex == index) "(Correct)" else ""}") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_option_$index"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (singleCorrectOptionIndex == index) (if (isDark) Color(0xFFF87171) else Terracotta) else (if (isDark) Color(0xFF818CF8) else DeepIndigo),
                                    unfocusedBorderColor = AppBorder,
                                    focusedTextColor = AppTextPrimary,
                                    unfocusedTextColor = AppTextPrimary,
                                    focusedContainerColor = AppSurface,
                                    unfocusedContainerColor = AppSurface
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                MindLoopPrimaryButton(
                    onClick = {
                        val finalType = if (singleIsTrueFalse) "TRUE_FALSE" else "MULTIPLE_CHOICE"
                        val correctIdx = if (singleIsTrueFalse) singleCorrectTfIndex else singleCorrectOptionIndex
                        val qText = singleQuestionText.ifBlank { "Key Concept Question on $chapterName" }
                        val opA = if (singleIsTrueFalse) "True" else singleOptionA.ifBlank { "Option 1" }
                        val opB = if (singleIsTrueFalse) "False" else singleOptionB.ifBlank { "Option 2" }
                        val opC = if (singleIsTrueFalse) "" else singleOptionC.ifBlank { "Option 3" }
                        val opD = if (singleIsTrueFalse) "" else singleOptionD.ifBlank { "Option 4" }

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
                        Toast.makeText(context, "Question added to ${if (sourceType == "reel") "Reel" else "Chapter"}!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_question_button"),
                    shape = RoundedCornerShape(14.dp),
                    containerColor = if (isDark) Color(0xFF6366F1) else DeepIndigo
                ) {
                    Text(
                        text = "Save Question",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // =================================================================
            // TAB 1: BULK QUESTIONS ENTRY (3 TO 5+ QUESTIONS)
            // =================================================================
            if (selectedTab == 1) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    border = BorderStroke(1.dp, AppBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "⚡ Add 3 to 5+ Questions in One Step",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AppTextPrimary
                        )
                        Text(
                            text = "Prepare a quick active-recall test for this reel. Edit each question card below and add more as needed.",
                            fontSize = 12.sp,
                            color = AppTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // List of bulk question cards
                bulkDrafts.forEachIndexed { index, draft ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AppSurface),
                        border = BorderStroke(1.5.dp, if (index == 0) (if (isDark) Color(0xFFF87171) else Terracotta) else AppBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Card Header: Question Number, Type Toggle, and Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) Color(0xFF6366F1) else DeepIndigo),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Question #${index + 1}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = AppTextPrimary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Quick TF / MCQ toggle for this card
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (draft.isTrueFalse) (if (isDark) Color(0xFF133322) else SageGreenLight) else (if (isDark) Color(0xFF0F172A) else Color(0xFFEAEDF3)))
                                            .clickable {
                                                bulkDrafts[index] = draft.copy(isTrueFalse = !draft.isTrueFalse)
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (draft.isTrueFalse) "T/F" else "MCQ",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (draft.isTrueFalse) (if (isDark) Color(0xFF34D399) else SageGreen) else AppTextPrimary
                                        )
                                    }

                                    if (bulkDrafts.size > 1) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = { bulkDrafts.removeAt(index) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove Question",
                                                tint = AppTextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = draft.questionText,
                                onValueChange = { newText ->
                                    bulkDrafts[index] = draft.copy(questionText = newText)
                                },
                                label = { Text("Question #${index + 1} text") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bulk_question_${index}_text"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (isDark) Color(0xFF818CF8) else DeepIndigo,
                                    unfocusedBorderColor = AppBorder,
                                    focusedTextColor = AppTextPrimary,
                                    unfocusedTextColor = AppTextPrimary,
                                    focusedContainerColor = AppSurfaceElevated,
                                    unfocusedContainerColor = AppSurfaceElevated
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (draft.isTrueFalse) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (draft.correctOptionIndex == 0) (if (isDark) Color(0xFF133322) else SageGreenLight) else (if (isDark) Color(0xFF0F172A) else Color(0xFFF1F3F8)))
                                            .clickable { bulkDrafts[index] = draft.copy(correctOptionIndex = 0) }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = draft.correctOptionIndex == 0,
                                            onClick = { bulkDrafts[index] = draft.copy(correctOptionIndex = 0) },
                                            colors = RadioButtonDefaults.colors(selectedColor = if (isDark) Color(0xFF34D399) else SageGreen)
                                        )
                                        Text("True", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppTextPrimary)
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (draft.correctOptionIndex == 1) (if (isDark) Color(0xFF133322) else SageGreenLight) else (if (isDark) Color(0xFF0F172A) else Color(0xFFF1F3F8)))
                                            .clickable { bulkDrafts[index] = draft.copy(correctOptionIndex = 1) }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = draft.correctOptionIndex == 1,
                                            onClick = { bulkDrafts[index] = draft.copy(correctOptionIndex = 1) },
                                            colors = RadioButtonDefaults.colors(selectedColor = if (isDark) Color(0xFF34D399) else SageGreen)
                                        )
                                        Text("False", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppTextPrimary)
                                    }
                                }
                            } else {
                                // 4 Options for MCQ
                                val mcqOptions = listOf(
                                    "Option A" to draft.optionA,
                                    "Option B" to draft.optionB,
                                    "Option C" to draft.optionC,
                                    "Option D" to draft.optionD
                                )

                                mcqOptions.forEachIndexed { optIdx, (optLabel, optVal) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = draft.correctOptionIndex == optIdx,
                                            onClick = { bulkDrafts[index] = draft.copy(correctOptionIndex = optIdx) },
                                            colors = RadioButtonDefaults.colors(selectedColor = if (isDark) Color(0xFFF87171) else Terracotta),
                                            modifier = Modifier.size(32.dp)
                                        )
                                        OutlinedTextField(
                                            value = optVal,
                                            onValueChange = { newVal ->
                                                bulkDrafts[index] = when (optIdx) {
                                                    0 -> draft.copy(optionA = newVal)
                                                    1 -> draft.copy(optionB = newVal)
                                                    2 -> draft.copy(optionC = newVal)
                                                    else -> draft.copy(optionD = newVal)
                                                }
                                            },
                                            label = { Text("$optLabel ${if (draft.correctOptionIndex == optIdx) "(Correct)" else ""}", fontSize = 11.sp) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(52.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = if (draft.correctOptionIndex == optIdx) (if (isDark) Color(0xFFF87171) else Terracotta) else (if (isDark) Color(0xFF818CF8) else DeepIndigo),
                                                unfocusedBorderColor = AppBorder,
                                                focusedTextColor = AppTextPrimary,
                                                unfocusedTextColor = AppTextPrimary,
                                                focusedContainerColor = AppSurfaceElevated,
                                                unfocusedContainerColor = AppSurfaceElevated
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick add buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            bulkDrafts.add(BulkQuestionDraft())
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF818CF8) else DeepIndigo)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isDark) Color(0xFF818CF8) else DeepIndigo)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ 1 Question", fontSize = 12.sp, color = if (isDark) Color(0xFF818CF8) else DeepIndigo)
                    }

                    OutlinedButton(
                        onClick = {
                            repeat(3) { bulkDrafts.add(BulkQuestionDraft()) }
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFFF87171) else Terracotta)
                    ) {
                        Text("+ 3 Qs", fontSize = 12.sp, color = if (isDark) Color(0xFFF87171) else Terracotta, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            repeat(5) { bulkDrafts.add(BulkQuestionDraft()) }
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFFF87171) else Terracotta)
                    ) {
                        Text("+ 5 Qs", fontSize = 12.sp, color = if (isDark) Color(0xFFF87171) else Terracotta, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save All Bulk Questions CTA Button
                MindLoopPrimaryButton(
                    onClick = {
                        val questionsToSave = bulkDrafts.mapIndexed { idx, draft ->
                            val finalType = if (draft.isTrueFalse) "TRUE_FALSE" else "MULTIPLE_CHOICE"
                            val qText = draft.questionText.ifBlank { "Concept Check #${idx + 1} on $chapterName" }
                            val opA = if (draft.isTrueFalse) "True" else draft.optionA.ifBlank { "Option A" }
                            val opB = if (draft.isTrueFalse) "False" else draft.optionB.ifBlank { "Option B" }
                            val opC = if (draft.isTrueFalse) "" else draft.optionC.ifBlank { "Option C" }
                            val opD = if (draft.isTrueFalse) "" else draft.optionD.ifBlank { "Option D" }

                            QuestionEntity(
                                linkedNoteId = linkedNoteId,
                                examId = "UPSI",
                                subjectName = subjectName,
                                chapterName = chapterName,
                                questionType = finalType,
                                questionText = qText,
                                optionA = opA,
                                optionB = opB,
                                optionC = opC,
                                optionD = opD,
                                correctAnswerIndex = draft.correctOptionIndex,
                                isDue = true,
                                sourceType = sourceType,
                                sourceId = sourceId
                            )
                        }

                        if (onSaveBulkQuestions != null) {
                            onSaveBulkQuestions(questionsToSave)
                        } else {
                            questionsToSave.forEach { q ->
                                onSaveQuestionWithSource?.invoke(
                                    q.linkedNoteId, q.subjectName, q.chapterName,
                                    q.questionType, q.questionText, q.optionA, q.optionB,
                                    q.optionC, q.optionD, q.correctAnswerIndex, q.sourceType, q.sourceId
                                ) ?: onSaveQuestion(
                                    q.linkedNoteId, q.subjectName, q.chapterName,
                                    q.questionType, q.questionText, q.optionA, q.optionB,
                                    q.optionC, q.optionD, q.correctAnswerIndex
                                )
                            }
                        }
                        Toast.makeText(context, "${questionsToSave.size} questions added to ${if (sourceType == "reel") "Reel" else "Chapter"}!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_bulk_questions_button"),
                    shape = RoundedCornerShape(14.dp),
                    containerColor = if (isDark) Color(0xFFEF4444) else Terracotta
                ) {
                    Text(
                        text = "Save All ${bulkDrafts.size} Questions to Reel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // =================================================================
            // TAB 2: CSV / FILE IMPORT (FOR 3 TO 5+ OR 50+ QUESTIONS)
            // =================================================================
            if (selectedTab == 2) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    border = BorderStroke(1.dp, AppBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📂 Import Questions via CSV or File",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AppTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Upload a .csv file from your device or paste CSV questions below. Format: Question, Option A, Option B, Option C, Option D, Correct Answer",
                            fontSize = 12.sp,
                            color = AppTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: File Picker & Sample CSV Loader
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                filePickerLauncher.launch(arrayOf("text/*", "application/*"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open file picker: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF6366F1) else DeepIndigo),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Select CSV File", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            csvContentText = """
Question,Option A,Option B,Option C,Option D,Correct Answer
Under which Article of the Constitution can writs be issued?,Article 32,Article 226,Article 131,Article 143,Article 32
How many Fundamental Rights are currently guaranteed to Indian citizens?,6,7,8,10,6
Which Fundamental Right was omitted by the 44th Constitutional Amendment 1978?,Right to Property,Right to Speech,Right to Religion,Right to Equality,Right to Property
Right to Education is recognized as a fundamental right under Article 21A,True,False,,,True
Who is considered the ultimate protector and guarantor of Fundamental Rights?,Supreme Court,President,Parliament,Prime Minister,Supreme Court
                            """.trimIndent()
                            Toast.makeText(context, "Loaded 5-question sample template!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFFF87171) else Terracotta)
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = if (isDark) Color(0xFFF87171) else Terracotta, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Load 5 Qs Sample", fontSize = 12.sp, color = if (isDark) Color(0xFFF87171) else Terracotta, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // CSV Text Editor
                OutlinedTextField(
                    value = csvContentText,
                    onValueChange = { csvContentText = it },
                    label = { Text("Paste or edit CSV content...") },
                    placeholder = {
                        Text(
                            "Question,Option A,Option B,Option C,Option D,Correct Answer\n" +
                            "What is the tenure of Rajya Sabha?,6 years,5 years,4 years,2 years,6 years"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("input_csv_content"),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Color(0xFF818CF8) else DeepIndigo,
                        unfocusedBorderColor = AppBorder,
                        focusedTextColor = AppTextPrimary,
                        unfocusedTextColor = AppTextPrimary,
                        focusedContainerColor = AppSurface,
                        unfocusedContainerColor = AppSurface
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time Parsed Preview Status
                if (parsedCsvQuestions.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF133322) else SageGreenLight)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isDark) Color(0xFF34D399) else SageGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "✅ Detected ${parsedCsvQuestions.size} valid questions ready to import!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF34D399) else SageGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Scrollable preview cards of the first few parsed items
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        parsedCsvQuestions.take(5).forEachIndexed { idx, q ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF1F3F8))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Q${idx + 1}: ${q.questionText}",
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    color = AppTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isDark) Color(0xFF059669) else SageGreen)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = when (q.correctAnswerIndex) {
                                            0 -> q.optionA
                                            1 -> q.optionB
                                            2 -> q.optionC
                                            else -> q.optionD
                                        }.take(12),
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                } else if (csvContentText.isNotBlank()) {
                    Text(
                        text = "⚠️ No valid questions detected yet. Please check the CSV format.",
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFFF87171) else Terracotta
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Import CSV Action Button
                MindLoopPrimaryButton(
                    onClick = {
                        if (parsedCsvQuestions.isEmpty()) {
                            Toast.makeText(context, "No questions found in CSV content", Toast.LENGTH_SHORT).show()
                            return@MindLoopPrimaryButton
                        }

                        if (onSaveBulkQuestions != null) {
                            onSaveBulkQuestions(parsedCsvQuestions)
                        } else if (onImportCsv != null) {
                            onImportCsv(csvContentText)
                        } else {
                            parsedCsvQuestions.forEach { q ->
                                onSaveQuestionWithSource?.invoke(
                                    q.linkedNoteId, q.subjectName, q.chapterName,
                                    q.questionType, q.questionText, q.optionA, q.optionB,
                                    q.optionC, q.optionD, q.correctAnswerIndex, q.sourceType, q.sourceId
                                )
                            }
                        }
                        Toast.makeText(context, "Successfully imported ${parsedCsvQuestions.size} questions to ${if (sourceType == "reel") "Reel" else "Chapter"}!", Toast.LENGTH_LONG).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("import_csv_questions_button"),
                    enabled = parsedCsvQuestions.isNotEmpty(),
                    shape = RoundedCornerShape(14.dp),
                    containerColor = if (isDark) Color(0xFF059669) else SageGreen
                ) {
                    Text(
                        text = if (parsedCsvQuestions.isNotEmpty()) "Import ${parsedCsvQuestions.size} Questions to Reel" else "Import CSV Questions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Robust CSV parser supporting standard Question/Options CSV, MCQ/TF prefixed CSV,
 * pipe-delimited and tab-delimited formats.
 */
fun parseCsvQuestionsPreview(
    csvText: String,
    subject: String,
    chapter: String,
    sourceType: String,
    sourceId: String,
    linkedNoteId: Long?
): List<QuestionEntity> {
    val results = mutableListOf<QuestionEntity>()
    val lines = csvText.lines().map { it.trim() }.filter { it.isNotBlank() }
    var tempId = 1L

    for (line in lines) {
        if (line.startsWith("Type,", true) || line.startsWith("Type|", true) ||
            line.startsWith("Question,", true) || line.startsWith("Question|", true) ||
            line.startsWith("question_id,", true) || line.startsWith("#")) continue

        val delimiter = when {
            line.contains('|') && !line.contains(',') -> '|'
            line.contains('\t') && !line.contains(',') -> '\t'
            line.contains(';') && !line.contains(',') -> ';'
            else -> ','
        }

        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        for (char in line) {
            when {
                char == '\"' -> inQuotes = !inQuotes
                char == delimiter && !inQuotes -> {
                    tokens.add(sb.toString().trim().removeSurrounding("\""))
                    sb.clear()
                }
                else -> sb.append(char)
            }
        }
        tokens.add(sb.toString().trim().removeSurrounding("\""))

        if (tokens.size >= 3) {
            val firstCol = tokens[0].trim()
            val hasTypeHeader = firstCol.equals("MCQ", true) ||
                               firstCol.equals("MULTIPLE_CHOICE", true) ||
                               firstCol.contains("True", true) ||
                               firstCol.equals("TF", true)

            val type: String
            val qText: String
            val a: String
            val b: String
            val c: String
            val d: String
            val correctRaw: String

            if (hasTypeHeader) {
                type = if (firstCol.contains("True", true) || firstCol.equals("TF", true)) "TRUE_FALSE" else "MULTIPLE_CHOICE"
                qText = tokens.getOrNull(1) ?: "Question"
                a = tokens.getOrNull(2) ?: if (type == "TRUE_FALSE") "True" else "Option A"
                b = tokens.getOrNull(3) ?: if (type == "TRUE_FALSE") "False" else "Option B"
                c = if (type == "TRUE_FALSE") "" else tokens.getOrNull(4) ?: ""
                d = if (type == "TRUE_FALSE") "" else tokens.getOrNull(5) ?: ""
                correctRaw = tokens.getOrNull(if (type == "TRUE_FALSE") 4 else 6) ?: a
            } else {
                qText = tokens[0]
                val isTf = tokens.size <= 4 && (tokens.getOrNull(1)?.equals("True", true) == true || tokens.getOrNull(2)?.equals("False", true) == true)
                if (isTf) {
                    type = "TRUE_FALSE"
                    a = "True"
                    b = "False"
                    c = ""
                    d = ""
                    correctRaw = tokens.getOrNull(3) ?: tokens.getOrNull(2) ?: "True"
                } else {
                    type = "MULTIPLE_CHOICE"
                    a = tokens.getOrNull(1) ?: "Option A"
                    b = tokens.getOrNull(2) ?: "Option B"
                    c = tokens.getOrNull(3) ?: "Option C"
                    d = tokens.getOrNull(4) ?: "Option D"
                    correctRaw = tokens.getOrNull(5) ?: a
                }
            }

            val correctIdx = when {
                type == "TRUE_FALSE" -> {
                    if (correctRaw.equals("False", true) || correctRaw.equals("F", true) || correctRaw == "1" || correctRaw.equals("B", true)) 1 else 0
                }
                correctRaw.equals(a, true) || correctRaw.equals("A", true) || correctRaw == "0" || correctRaw == "1" -> 0
                correctRaw.equals(b, true) || correctRaw.equals("B", true) || correctRaw == "2" -> 1
                correctRaw.equals(c, true) || correctRaw.equals("C", true) || correctRaw == "3" -> 2
                correctRaw.equals(d, true) || correctRaw.equals("D", true) || correctRaw == "4" -> 3
                else -> 0
            }

            results.add(
                QuestionEntity(
                    id = tempId++,
                    linkedNoteId = linkedNoteId,
                    examId = "UPSI",
                    subjectName = subject,
                    chapterName = chapter,
                    questionType = type,
                    questionText = qText,
                    optionA = a,
                    optionB = b,
                    optionC = c,
                    optionD = d,
                    correctAnswerIndex = correctIdx,
                    isDue = true,
                    sourceType = sourceType,
                    sourceId = sourceId
                )
            )
        }
    }
    return results
}
