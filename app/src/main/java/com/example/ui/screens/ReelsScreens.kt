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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
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
import com.example.data.entity.QuestionEntity
import com.example.data.entity.ReelEntity
import com.example.data.model.CurriculumChapter
import com.example.data.model.CurriculumExam
import com.example.data.model.CurriculumSubject
import com.example.ui.components.InteractiveCard
import com.example.ui.components.InteractiveCardBorder
import com.example.ui.components.MindLoopPrimaryButton
import com.example.ui.components.MindLoopSecondaryButton
import com.example.ui.components.StaticCardBorder
import com.example.ui.components.tapAffordance
import com.example.ui.theme.Amber
import com.example.ui.theme.AmberLight
import com.example.ui.theme.BackgroundOffWhite
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.DeepIndigoLight
import com.example.ui.theme.DeepIndigoSubtle
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenDark
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.ReelVideoCacheManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

// =========================================================================
// CURRICULUM DIALOGS & CONTROLS
// =========================================================================
@Composable
fun AddOrEditCurriculumItemDialog(
    itemType: String, // "EXAM_OR_SUBJECT", "EXAM", "STANDALONE_SUBJECT", "SUBJECT", "CHAPTER"
    isEdit: Boolean = false,
    initialName: String = "",
    initialSubtitle: String = "",
    examNameForSubject: String? = null,
    subjectNameForChapter: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, subtitle: String, chosenType: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var subtitle by remember { mutableStateOf(initialSubtitle) }
    var chosenType by remember { mutableStateOf(if (itemType == "EXAM_OR_SUBJECT") "EXAM" else itemType) }

    val dialogTitle = when {
        isEdit -> when (itemType) {
            "EXAM" -> "Edit Exam Course"
            "CHAPTER" -> "Edit Chapter"
            else -> "Edit Subject"
        }
        itemType == "EXAM_OR_SUBJECT" -> "Add Exam or Subject"
        itemType == "SUBJECT" -> if (examNameForSubject != null) "Add Subject to $examNameForSubject" else "Add Subject"
        itemType == "CHAPTER" -> if (subjectNameForChapter != null) "Add Chapter to $subjectNameForChapter" else "Add Chapter"
        else -> "Add Exam Course"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isEdit) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null,
                    tint = Terracotta
                )
                Text(
                    text = dialogTitle,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isEdit && itemType == "EXAM_OR_SUBJECT") {
                    Text(
                        text = "Choose what you want to prepare:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Exam Option
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { chosenType = "EXAM" }
                                .testTag("select_type_exam"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (chosenType == "EXAM") DeepIndigo.copy(alpha = 0.1f) else SurfaceWhite
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (chosenType == "EXAM") DeepIndigo else StaticCardBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = if (chosenType == "EXAM") DeepIndigo else TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Exam Course",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (chosenType == "EXAM") DeepIndigo else TextSecondary
                                )
                            }
                        }

                        // Standalone Subject Option
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { chosenType = "STANDALONE_SUBJECT" }
                                .testTag("select_type_subject"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (chosenType == "STANDALONE_SUBJECT") Terracotta.copy(alpha = 0.1f) else SurfaceWhite
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (chosenType == "STANDALONE_SUBJECT") Terracotta else StaticCardBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Outlined.Psychology,
                                    contentDescription = null,
                                    tint = if (chosenType == "STANDALONE_SUBJECT") Terracotta else TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Self-Study Subject",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (chosenType == "STANDALONE_SUBJECT") Terracotta else TextSecondary
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            when {
                                chosenType == "EXAM" -> "Exam Name (e.g. SSC CGL)"
                                chosenType == "CHAPTER" -> "Chapter Name (e.g. 3. Directive Principles)"
                                else -> "Subject Name (e.g. Sociology)"
                            }
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("curriculum_input_name")
                )

                if (chosenType != "CHAPTER") {
                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = {
                            Text(if (chosenType == "EXAM") "Description / Key Subjects" else "Short Subtitle / Description")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("curriculum_input_subtitle")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), subtitle.trim(), chosenType)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("curriculum_dialog_confirm_button")
            ) {
                Text(if (isEdit) "Save" else "Create")
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

@Composable
fun ConfirmDeleteCurriculumDialog(
    itemType: String,
    itemName: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Terracotta)
                Text(
                    text = "Delete $itemType?",
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Text(
                text = "Are you sure you want to delete \"$itemName\"? This item will be removed from your curriculum.",
                fontSize = 14.sp,
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("Delete", color = Color.White)
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

// =========================================================================
// 1. REEL EXAM LIST SCREEN (Study Reels Landing Screen)
// =========================================================================
@Composable
fun ReelExamListScreen(
    onSelectExam: (String) -> Unit,
    onSelectSubject: (String) -> Unit,
    onUploadReelClick: () -> Unit,
    allReels: List<ReelEntity>,
    exams: List<CurriculumExam> = emptyList(),
    subjects: List<CurriculumSubject> = emptyList(),
    canModify: (String) -> Boolean = { false },
    onAddExam: (name: String, subtitle: String) -> Unit = { _, _ -> },
    onAddSubject: (name: String, examName: String?, subtitle: String) -> Unit = { _, _, _ -> },
    onEditExam: (id: String, name: String, subtitle: String) -> Unit = { _, _, _ -> },
    onDeleteExam: (id: String) -> Unit = {},
    onEditSubject: (id: String, name: String, subtitle: String) -> Unit = { _, _, _ -> },
    onDeleteSubject: (id: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingExam by remember { mutableStateOf<CurriculumExam?>(null) }
    var deletingExam by remember { mutableStateOf<CurriculumExam?>(null) }
    var editingSubject by remember { mutableStateOf<CurriculumSubject?>(null) }
    var deletingSubject by remember { mutableStateOf<CurriculumSubject?>(null) }

    if (showAddDialog) {
        AddOrEditCurriculumItemDialog(
            itemType = "EXAM_OR_SUBJECT",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, subtitle, chosenType ->
                showAddDialog = false
                if (chosenType == "EXAM") {
                    onAddExam(name, subtitle)
                } else {
                    onAddSubject(name, null, subtitle)
                }
            }
        )
    }

    editingExam?.let { exam ->
        AddOrEditCurriculumItemDialog(
            itemType = "EXAM",
            isEdit = true,
            initialName = exam.name,
            initialSubtitle = exam.subtitle,
            onDismiss = { editingExam = null },
            onConfirm = { name, subtitle, _ ->
                editingExam = null
                onEditExam(exam.id, name, subtitle)
            }
        )
    }

    deletingExam?.let { exam ->
        ConfirmDeleteCurriculumDialog(
            itemType = "Exam Course",
            itemName = exam.name,
            onDismiss = { deletingExam = null },
            onConfirmDelete = {
                deletingExam = null
                onDeleteExam(exam.id)
            }
        )
    }

    editingSubject?.let { subject ->
        AddOrEditCurriculumItemDialog(
            itemType = "STANDALONE_SUBJECT",
            isEdit = true,
            initialName = subject.name,
            initialSubtitle = subject.subtitle,
            onDismiss = { editingSubject = null },
            onConfirm = { name, subtitle, _ ->
                editingSubject = null
                onEditSubject(subject.id, name, subtitle)
            }
        )
    }

    deletingSubject?.let { subject ->
        ConfirmDeleteCurriculumDialog(
            itemType = "Subject",
            itemName = subject.name,
            onDismiss = { deletingSubject = null },
            onConfirmDelete = {
                deletingSubject = null
                onDeleteSubject(subject.id)
            }
        )
    }

    val standaloneSubjects = remember(subjects) {
        subjects.filter { it.isStandalone }
    }

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

            // Header: Title & Subtitle + Add Exam/Subject + Upload Reel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Study Reels",
                        fontSize = 28.sp,
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

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    // + Add Exam / Subject button
                    TextButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_exam_or_subject_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = DeepIndigo, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add", fontWeight = FontWeight.Bold, color = DeepIndigo, fontSize = 13.sp)
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Section 1: Enrolled Exam Course
                item {
                    Text(
                        text = "Enrolled Exam Course",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                items(exams) { exam ->
                    val examReelsCount = allReels.count { it.exam.equals(exam.name, ignoreCase = true) }
                    val userCanModify = canModify(exam.createdBy)

                    InteractiveCard(
                        onClick = { onSelectExam(exam.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reel_exam_card_${exam.name.lowercase().replace(" ", "_")}"),
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
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DeepIndigo.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = DeepIndigo,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exam.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                if (exam.subtitle.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = exam.subtitle,
                                        fontSize = 12.5.sp,
                                        color = TextSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(TerracottaLight)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$examReelsCount Reels",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Terracotta
                                        )
                                    }
                                    if (userCanModify) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE2E8F0))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Created by you",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = DeepIndigo
                                            )
                                        }
                                    }
                                }
                            }

                            // Edit/Delete buttons if user created this exam
                            if (userCanModify) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { editingExam = exam },
                                        modifier = Modifier.size(32.dp).testTag("edit_exam_${exam.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Exam", tint = DeepIndigo, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { deletingExam = exam },
                                        modifier = Modifier.size(32.dp).testTag("delete_exam_${exam.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Exam", tint = Terracotta, modifier = Modifier.size(16.dp))
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

                // Section 2: Self-Study & Micro-Learning
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Self-Study & Micro-Learning",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                items(standaloneSubjects) { subject ->
                    val subjectReelsCount = allReels.count { it.subject.equals(subject.name, ignoreCase = true) }
                    val userCanModify = canModify(subject.createdBy)

                    InteractiveCard(
                        onClick = { onSelectSubject(subject.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reel_standalone_subject_${subject.name.lowercase().replace(" ", "_")}"),
                        shape = RoundedCornerShape(16.dp),
                        elevation = 4.dp
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
                                    text = subject.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                if (subject.subtitle.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = subject.subtitle,
                                        fontSize = 12.5.sp,
                                        color = TextSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(TerracottaLight)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$subjectReelsCount Reels",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Terracotta
                                        )
                                    }
                                    if (userCanModify) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE2E8F0))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Created by you",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = DeepIndigo
                                            )
                                        }
                                    }
                                }
                            }

                            // Edit/Delete buttons if user created this subject
                            if (userCanModify) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { editingSubject = subject },
                                        modifier = Modifier.size(32.dp).testTag("edit_subject_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Subject", tint = DeepIndigo, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { deletingSubject = subject },
                                        modifier = Modifier.size(32.dp).testTag("delete_subject_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Subject", tint = Terracotta, modifier = Modifier.size(16.dp))
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
    subjects: List<CurriculumSubject> = emptyList(),
    canModify: (String) -> Boolean = { false },
    onAddSubject: (name: String, examName: String?, subtitle: String) -> Unit = { _, _, _ -> },
    onEditSubject: (id: String, name: String, subtitle: String) -> Unit = { _, _, _ -> },
    onDeleteSubject: (id: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<CurriculumSubject?>(null) }
    var deletingSubject by remember { mutableStateOf<CurriculumSubject?>(null) }

    if (showAddDialog) {
        AddOrEditCurriculumItemDialog(
            itemType = "SUBJECT",
            examNameForSubject = examName,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, subtitle, _ ->
                showAddDialog = false
                onAddSubject(name, examName, subtitle)
            }
        )
    }

    editingSubject?.let { subj ->
        AddOrEditCurriculumItemDialog(
            itemType = "SUBJECT",
            isEdit = true,
            initialName = subj.name,
            initialSubtitle = subj.subtitle,
            examNameForSubject = examName,
            onDismiss = { editingSubject = null },
            onConfirm = { name, subtitle, _ ->
                editingSubject = null
                onEditSubject(subj.id, name, subtitle)
            }
        )
    }

    deletingSubject?.let { subj ->
        ConfirmDeleteCurriculumDialog(
            itemType = "Subject",
            itemName = subj.name,
            onDismiss = { deletingSubject = null },
            onConfirmDelete = {
                deletingSubject = null
                onDeleteSubject(subj.id)
            }
        )
    }

    val examSubjects = remember(subjects, examName) {
        subjects.filter { it.examName?.equals(examName, ignoreCase = true) == true }
    }

    val examReels = remember(allReels, examName) {
        allReels.filter { it.exam.equals(examName, ignoreCase = true) }
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
            Column(modifier = Modifier.weight(1f)) {
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
            // + Add Subject button
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_subject_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Subject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(examSubjects) { subject ->
                val subjectReels = allReels.filter { it.subject.equals(subject.name, ignoreCase = true) }
                val totalDurationSec = subjectReels.sumOf { it.durationSeconds }
                val durationMin = totalDurationSec / 60
                val userCanModify = canModify(subject.createdBy)

                InteractiveCard(
                    onClick = { onSelectSubject(subject.name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reel_subject_card_${subject.name.lowercase().replace(" ", "_")}"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.5.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
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

                            if (userCanModify) {
                                Row {
                                    IconButton(
                                        onClick = { editingSubject = subject },
                                        modifier = Modifier.size(28.dp).testTag("edit_subject_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Subject", tint = DeepIndigo, modifier = Modifier.size(14.dp))
                                    }
                                    IconButton(
                                        onClick = { deletingSubject = subject },
                                        modifier = Modifier.size(28.dp).testTag("delete_subject_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Subject", tint = Terracotta, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = subject.name,
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
    onUploadChapterReel: ((String) -> Unit)? = null,
    allReels: List<ReelEntity>,
    chapters: List<CurriculumChapter> = emptyList(),
    canModify: (String) -> Boolean = { false },
    onAddChapter: (name: String, examName: String?, subjectName: String) -> Unit = { _, _, _ -> },
    onEditChapter: (id: String, name: String) -> Unit = { _, _ -> },
    onDeleteChapter: (id: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddChapterDialog by remember { mutableStateOf(false) }
    var editingChapter by remember { mutableStateOf<CurriculumChapter?>(null) }
    var deletingChapter by remember { mutableStateOf<CurriculumChapter?>(null) }

    if (showAddChapterDialog) {
        AddOrEditCurriculumItemDialog(
            itemType = "CHAPTER",
            subjectNameForChapter = subjectName,
            onDismiss = { showAddChapterDialog = false },
            onConfirm = { name, _, _ ->
                showAddChapterDialog = false
                onAddChapter(name, examName.ifBlank { null }, subjectName)
            }
        )
    }

    editingChapter?.let { ch ->
        AddOrEditCurriculumItemDialog(
            itemType = "CHAPTER",
            isEdit = true,
            initialName = ch.name,
            subjectNameForChapter = subjectName,
            onDismiss = { editingChapter = null },
            onConfirm = { name, _, _ ->
                editingChapter = null
                onEditChapter(ch.id, name)
            }
        )
    }

    deletingChapter?.let { ch ->
        ConfirmDeleteCurriculumDialog(
            itemType = "Chapter",
            itemName = ch.name,
            onDismiss = { deletingChapter = null },
            onConfirmDelete = {
                deletingChapter = null
                onDeleteChapter(ch.id)
            }
        )
    }

    val subjectReels = remember(allReels, subjectName) {
        allReels.filter { it.subject.equals(subjectName, ignoreCase = true) }
    }

    // Combine chapters from curriculum repository and any chapters found in existing reels
    val curriculumChaptersForSubject = remember(chapters, subjectName) {
        chapters.filter { it.subjectName.equals(subjectName, ignoreCase = true) }
    }

    // List of chapter items to display
    data class DisplayChapter(
        val id: String?,
        val name: String,
        val createdBy: String,
        val canUserModify: Boolean
    )

    val displayChapters = remember(curriculumChaptersForSubject, subjectReels) {
        val list = mutableListOf<DisplayChapter>()
        curriculumChaptersForSubject.forEach { ch ->
            list.add(DisplayChapter(id = ch.id, name = ch.name, createdBy = ch.createdBy, canUserModify = canModify(ch.createdBy)))
        }
        subjectReels.map { it.chapter }.distinct().forEach { reelChapterName ->
            if (list.none { it.name.equals(reelChapterName, ignoreCase = true) }) {
                list.add(DisplayChapter(id = null, name = reelChapterName, createdBy = "admin", canUserModify = false))
            }
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
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

            // Chapters Breakdown Header with + Add Chapter button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chapters",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "${displayChapters.size} Chapters",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = { showAddChapterDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_chapter_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chapter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Chapter Cards with "Watch Chapter" button & Edit/Delete
            items(displayChapters) { displayCh ->
                val chapterName = displayCh.name
                val reelsInChapter = subjectReels.filter { it.chapter.equals(chapterName, ignoreCase = true) }
                val totalSec = reelsInChapter.sumOf { it.durationSeconds }

                InteractiveCard(
                    onClick = { onWatchChapter(chapterName) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chapter_card_${chapterName.lowercase().replace(" ", "_")}"),
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
                                    text = chapterName,
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
                                    if (displayCh.canUserModify) {
                                        Text("•", fontSize = 12.sp, color = TextSecondary)
                                        Text(
                                            text = "Created by you",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = SageGreen
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (displayCh.canUserModify && displayCh.id != null) {
                                    IconButton(
                                        onClick = {
                                            val chObj = curriculumChaptersForSubject.find { it.id == displayCh.id }
                                            if (chObj != null) editingChapter = chObj
                                        },
                                        modifier = Modifier.size(30.dp).testTag("edit_chapter_${displayCh.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Chapter", tint = DeepIndigo, modifier = Modifier.size(15.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val chObj = curriculumChaptersForSubject.find { it.id == displayCh.id }
                                            if (chObj != null) deletingChapter = chObj
                                        },
                                        modifier = Modifier.size(30.dp).testTag("delete_chapter_${displayCh.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Chapter", tint = Terracotta, modifier = Modifier.size(15.dp))
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        if (onUploadChapterReel != null) {
                                            onUploadChapterReel(chapterName)
                                        } else {
                                            onUploadReelClick()
                                        }
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("upload_chapter_${chapterName.lowercase().replace(" ", "_")}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = "Upload Reel for $chapterName",
                                        tint = Terracotta,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Button(
                                    onClick = { onWatchChapter(chapterName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("watch_chapter_${chapterName.lowercase().replace(" ", "_")}")
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
                        }

                        // Preview of reels inside chapter
                        if (reelsInChapter.isNotEmpty()) {
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
    initialOpenUploadDialog: Boolean = false,
    onBackClick: () -> Unit,
    onAddQuestion: (reelId: Long, subject: String, chapter: String) -> Unit,
    onRecordWatch: (reelId: Long, watchSeconds: Long) -> Unit,
    onUploadReel: (title: String, description: String, exam: String, subject: String, chapter: String, uri: Uri) -> Unit,
    exams: List<CurriculumExam> = emptyList(),
    subjects: List<CurriculumSubject> = emptyList(),
    chapters: List<CurriculumChapter> = emptyList(),
    onAddExam: ((name: String, subtitle: String) -> Unit)? = null,
    onAddSubject: ((name: String, examName: String?, subtitle: String) -> Unit)? = null,
    onAddChapter: ((name: String, examName: String?, subjectName: String) -> Unit)? = null,
    allReels: List<ReelEntity>,
    allQuestions: List<QuestionEntity> = emptyList(),
    onSaveQuestionDirect: ((subj: String, chap: String, type: String, qText: String, opA: String, opB: String, opC: String, opD: String, correctIdx: Int, srcType: String, srcId: String) -> Unit)? = null,
    onSaveBulkQuestions: ((List<QuestionEntity>) -> Unit)? = null,
    onImportCsvQuestions: ((subject: String, chapter: String, reelId: Long, csv: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Filter reels based on navigation scope
    val feedReels = remember(allReels, filterExam, filterSubject, filterChapter) {
        var filtered = allReels
        if (!filterExam.isNullOrBlank()) {
            filtered = filtered.filter {
                it.exam.equals(filterExam, ignoreCase = true) ||
                (filterExam.contains("UPSI", ignoreCase = true) && it.exam.contains("UPSI", ignoreCase = true)) ||
                (filterExam.contains("Self-Study", ignoreCase = true) && it.exam.contains("Self-Study", ignoreCase = true))
            }
        }
        if (!filterSubject.isNullOrBlank()) {
            filtered = filtered.filter { it.subject.equals(filterSubject, ignoreCase = true) }
        }
        if (!filterChapter.isNullOrBlank() && filterChapter != "All Chapters" && filterChapter != "Entire Subject") {
            filtered = filtered.filter {
                it.chapter.equals(filterChapter, ignoreCase = true) ||
                it.chapter.equals("Entire Subject", ignoreCase = true) ||
                it.chapter.equals("All Chapters", ignoreCase = true)
            }
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
    var showUploadDialog by remember { mutableStateOf(initialOpenUploadDialog) }

    // Bottom sheet for jumping chapters
    var showChaptersSheet by remember { mutableStateOf(false) }

    // Question sheets state
    var showReelQuestionsHubSheet by remember { mutableStateOf(false) }
    var showReelQuizSheet by remember { mutableStateOf(false) }
    var showAddQuestionSheet by remember { mutableStateOf(false) }
    var initialAddQuestionTab by remember { mutableIntStateOf(0) }
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

                // Find questions attached to this reel
                val reelQuestions = remember(allQuestions, reel.id, reel.chapter, reel.subject) {
                    allQuestions.filter {
                        (it.sourceType == "reel" && it.sourceId == reel.id.toString()) ||
                        (it.chapterName.equals(reel.chapter, true) && it.subjectName.equals(reel.subject, true))
                    }
                }

                SingleReelPlayerItem(
                    reel = reel,
                    isActive = isCurrentPage,
                    isMuted = isMuted,
                    questionCount = reelQuestions.size,
                    onToggleMute = {
                        isMuted = !isMuted
                        showMuteIndicator = true
                    },
                    onOpenAddQuestion = {
                        currentReelForQuestion = reel
                        showReelQuestionsHubSheet = true
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

    // Modal Sheet: Reel Questions Hub (Quiz, Bulk Add, CSV Import, Single Add)
    if (showReelQuestionsHubSheet && currentReelForQuestion != null) {
        val targetReel = currentReelForQuestion!!
        val questionsForThisReel = remember(allQuestions, targetReel.id) {
            allQuestions.filter {
                (it.sourceType == "reel" && it.sourceId == targetReel.id.toString()) ||
                (it.chapterName.equals(targetReel.chapter, true) && it.subjectName.equals(targetReel.subject, true))
            }
        }
        ReelQuestionsHubSheet(
            reel = targetReel,
            questions = questionsForThisReel,
            onDismiss = { showReelQuestionsHubSheet = false },
            onStartQuiz = {
                showReelQuestionsHubSheet = false
                showReelQuizSheet = true
            },
            onOpenBulkAdd = {
                initialAddQuestionTab = 1
                showReelQuestionsHubSheet = false
                showAddQuestionSheet = true
            },
            onOpenCsvImport = {
                initialAddQuestionTab = 2
                showReelQuestionsHubSheet = false
                showAddQuestionSheet = true
            },
            onOpenSingleAdd = {
                initialAddQuestionTab = 0
                showReelQuestionsHubSheet = false
                showAddQuestionSheet = true
            }
        )
    }

    // Modal Sheet: Interactive Practice Quiz for this Reel
    if (showReelQuizSheet && currentReelForQuestion != null) {
        val targetReel = currentReelForQuestion!!
        val questionsForThisReel = remember(allQuestions, targetReel.id) {
            allQuestions.filter {
                (it.sourceType == "reel" && it.sourceId == targetReel.id.toString()) ||
                (it.chapterName.equals(targetReel.chapter, true) && it.subjectName.equals(targetReel.subject, true))
            }
        }
        ReelPracticeQuizSheet(
            reel = targetReel,
            questions = questionsForThisReel,
            onDismiss = { showReelQuizSheet = false }
        )
    }

    // Modal Sheet: Add Question to Reel (Single, Bulk 3-5+, or CSV Import)
    if (showAddQuestionSheet && currentReelForQuestion != null) {
        val targetReel = currentReelForQuestion!!
        AddQuestionBottomSheet(
            linkedNoteId = null,
            subjectName = targetReel.subject,
            chapterName = targetReel.chapter,
            sourceType = "reel",
            sourceId = targetReel.id.toString(),
            initialTab = initialAddQuestionTab,
            onDismiss = { showAddQuestionSheet = false },
            onSaveQuestion = { _, subj, chap, type, qText, opA, opB, opC, opD, correctIdx ->
                onSaveQuestionDirect?.invoke(
                    subj, chap, type, qText, opA, opB, opC, opD, correctIdx, "reel", targetReel.id.toString()
                ) ?: onAddQuestion(targetReel.id, subj, chap)
                showAddQuestionSheet = false
            },
            onSaveQuestionWithSource = { _, subj, chap, type, qText, opA, opB, opC, opD, correctIdx, srcType, srcId ->
                onSaveQuestionDirect?.invoke(
                    subj, chap, type, qText, opA, opB, opC, opD, correctIdx, srcType, srcId
                ) ?: onAddQuestion(targetReel.id, subj, chap)
                showAddQuestionSheet = false
            },
            onSaveBulkQuestions = { bulkList ->
                onSaveBulkQuestions?.invoke(bulkList)
                showAddQuestionSheet = false
            },
            onImportCsv = { csvContent ->
                onImportCsvQuestions?.invoke(targetReel.subject, targetReel.chapter, targetReel.id, csvContent)
                showAddQuestionSheet = false
            }
        )
    }

    // Dialog / Sheet: Upload Reel
    if (showUploadDialog) {
        val currentReel = if (feedReels.isNotEmpty()) {
            feedReels.getOrNull(pagerState.currentPage.coerceIn(0, feedReels.size - 1))
        } else null

        val effectiveExam = filterExam ?: currentReel?.exam ?: "UPSI – Police Sub-Inspector"
        val effectiveSubject = filterSubject ?: currentReel?.subject
        val effectiveChapter = filterChapter ?: currentReel?.chapter

        UploadReelDialog(
            defaultExam = effectiveExam,
            defaultSubject = effectiveSubject,
            defaultChapter = effectiveChapter,
            exams = exams,
            subjects = subjects,
            chapters = chapters,
            onDismiss = { showUploadDialog = false },
            onConfirmUpload = { title, desc, exam, subj, chap, uri ->
                showUploadDialog = false
                onUploadReel(title, desc, exam, subj, chap, uri)
            },
            onAddExam = onAddExam,
            onAddSubject = onAddSubject,
            onAddChapter = onAddChapter
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
    questionCount: Int = 0,
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
            // "Add Question / Reel Quiz" button linked to Reel ID (shows count if questions exist)
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
                        .background(if (questionCount > 0) SageGreen else Terracotta),
                    contentAlignment = Alignment.Center
                ) {
                    if (questionCount > 0) {
                        Text(
                            text = if (questionCount > 99) "99+" else "$questionCount",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Question",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (questionCount > 0) "$questionCount Qs" else "+ Question",
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadReelDialog(
    defaultExam: String? = null,
    defaultSubject: String? = null,
    defaultChapter: String? = null,
    exams: List<CurriculumExam> = emptyList(),
    subjects: List<CurriculumSubject> = emptyList(),
    chapters: List<CurriculumChapter> = emptyList(),
    onDismiss: () -> Unit,
    onConfirmUpload: (title: String, description: String, exam: String, subject: String, chapter: String, uri: Uri) -> Unit,
    onAddExam: ((name: String, subtitle: String) -> Unit)? = null,
    onAddSubject: ((name: String, examName: String?, subtitle: String) -> Unit)? = null,
    onAddChapter: ((name: String, examName: String?, subjectName: String) -> Unit)? = null
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

    // Detect if default context is Self-Study
    val isInitialSelfStudy = remember(defaultExam, defaultSubject) {
        defaultExam?.contains("Self-Study", ignoreCase = true) == true ||
        (defaultExam == null && defaultSubject != null && subjects.any { it.name.equals(defaultSubject, ignoreCase = true) && it.isStandalone })
    }

    var isSelfStudyMode by remember { mutableStateOf(isInitialSelfStudy) }

    // Fallback list of exams if empty
    val availableExams = remember(exams) {
        if (exams.isEmpty()) {
            listOf(CurriculumExam(id = "default_upsi", name = "UPSI – Police Sub-Inspector", subtitle = "Police Sub-Inspector Exam"))
        } else {
            exams
        }
    }

    var selectedExam by remember(defaultExam, isSelfStudyMode, availableExams) {
        mutableStateOf(
            if (isSelfStudyMode) "Self-Study & Micro-Learning"
            else {
                val match = availableExams.find { it.name.equals(defaultExam, ignoreCase = true) }
                match?.name ?: (if (!defaultExam.isNullOrBlank() && !defaultExam.contains("Self-Study", ignoreCase = true)) defaultExam else availableExams.first().name)
            }
        )
    }

    // Available subjects based on Track and selected exam
    val availableSubjects = remember(isSelfStudyMode, selectedExam, subjects) {
        if (isSelfStudyMode) {
            val list = subjects.filter { it.isStandalone || it.examName == null || it.examName.contains("Self-Study", ignoreCase = true) }
            if (list.isEmpty()) {
                listOf(CurriculumSubject(id = "self_psych", name = "Psychology", subtitle = "Cognitive Psychology", isStandalone = true))
            } else list
        } else {
            val list = subjects.filter {
                it.examName?.equals(selectedExam, ignoreCase = true) == true ||
                (!it.isStandalone && selectedExam.contains("UPSI", ignoreCase = true) && (it.examName == null || it.examName.contains("UPSI", ignoreCase = true)))
            }
            if (list.isEmpty()) {
                listOf(CurriculumSubject(id = "subj_polity", examName = selectedExam, name = "Indian Polity", subtitle = "Constitution"))
            } else list
        }
    }

    var selectedSubject by remember(defaultSubject, availableSubjects) {
        val match = availableSubjects.find { it.name.equals(defaultSubject, ignoreCase = true) }
        mutableStateOf(match?.name ?: availableSubjects.firstOrNull()?.name ?: (defaultSubject ?: "Indian Polity"))
    }

    // Synchronize selectedSubject if availableSubjects changes
    LaunchedEffect(availableSubjects) {
        if (!availableSubjects.any { it.name.equals(selectedSubject, ignoreCase = true) }) {
            selectedSubject = availableSubjects.firstOrNull()?.name ?: ""
        }
    }

    // Chapters for selectedSubject
    val rawChapters = remember(selectedSubject, chapters) {
        chapters.filter { it.subjectName.equals(selectedSubject, ignoreCase = true) }.map { it.name }
    }

    val entireSubjectOption = "Entire Subject (All Chapters / General)"

    var selectedChapter by remember(defaultChapter, selectedSubject, rawChapters) {
        val initialMatch = rawChapters.find { it.equals(defaultChapter, ignoreCase = true) }
        mutableStateOf(
            initialMatch ?: if (!defaultChapter.isNullOrBlank() && defaultChapter != "All Chapters" && defaultChapter != "Entire Subject") {
                defaultChapter
            } else {
                entireSubjectOption
            }
        )
    }

    LaunchedEffect(rawChapters) {
        if (selectedChapter != entireSubjectOption && !rawChapters.any { it.equals(selectedChapter, ignoreCase = true) }) {
            selectedChapter = rawChapters.firstOrNull() ?: entireSubjectOption
        }
    }

    // Dropdown expanded states
    var examMenuExpanded by remember { mutableStateOf(false) }
    var subjectMenuExpanded by remember { mutableStateOf(false) }
    var chapterMenuExpanded by remember { mutableStateOf(false) }

    // Inline dialog states
    var showAddExamDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddChapterDialog by remember { mutableStateOf(false) }

    var newExamName by remember { mutableStateOf("") }
    var newSubjectName by remember { mutableStateOf("") }
    var newChapterName by remember { mutableStateOf("") }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
        }
    }

    // Sub-dialogs for quick inline addition
    if (showAddExamDialog) {
        AlertDialog(
            onDismissRequest = { showAddExamDialog = false },
            title = { Text("Add Exam Course", fontWeight = FontWeight.Bold, color = DeepIndigo) },
            text = {
                OutlinedTextField(
                    value = newExamName,
                    onValueChange = { newExamName = it },
                    label = { Text("Exam Name (e.g. UPSC CSE, SSC CGL)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newExamName.isNotBlank()) {
                            onAddExam?.invoke(newExamName.trim(), "")
                            selectedExam = newExamName.trim()
                            newExamName = ""
                            showAddExamDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) { Text("Add & Select") }
            },
            dismissButton = {
                TextButton(onClick = { showAddExamDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Subject", fontWeight = FontWeight.Bold, color = DeepIndigo) },
            text = {
                OutlinedTextField(
                    value = newSubjectName,
                    onValueChange = { newSubjectName = it },
                    label = { Text("Subject Name (e.g. Ancient History)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSubjectName.isNotBlank()) {
                            onAddSubject?.invoke(newSubjectName.trim(), if (isSelfStudyMode) "" else selectedExam, "")
                            selectedSubject = newSubjectName.trim()
                            newSubjectName = ""
                            showAddSubjectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) { Text("Add & Select") }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAddChapterDialog) {
        AlertDialog(
            onDismissRequest = { showAddChapterDialog = false },
            title = { Text("Add Chapter to $selectedSubject", fontWeight = FontWeight.Bold, color = DeepIndigo) },
            text = {
                OutlinedTextField(
                    value = newChapterName,
                    onValueChange = { newChapterName = it },
                    label = { Text("Chapter Title (e.g. 5. Indus Valley Civilization)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newChapterName.isNotBlank()) {
                            onAddChapter?.invoke(newChapterName.trim(), if (isSelfStudyMode) null else selectedExam, selectedSubject)
                            selectedChapter = newChapterName.trim()
                            newChapterName = ""
                            showAddChapterDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) { Text("Add & Select") }
            },
            dismissButton = {
                TextButton(onClick = { showAddChapterDialog = false }) { Text("Cancel") }
            }
        )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Categorize your video clip by track, subject, and chapter so students can study sequentially.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                // Context auto-fill banner
                if (!defaultSubject.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SageGreenLight,
                        border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SageGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = "Auto-filled from active study context",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreenDark
                                )
                                Text(
                                    text = "${selectedSubject} • ${if (selectedChapter.startsWith("Entire Subject")) "Entire Subject" else selectedChapter}",
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                // 1. Reel Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reel Title *") },
                    placeholder = { Text("e.g. Article 21 Explained") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_reel_input_title")
                )

                // 2. Track Switcher: Exam Course vs Self-Study
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Curriculum Track *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepIndigoLight
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Exam Course Tab
                        OutlinedCard(
                            onClick = {
                                isSelfStudyMode = false
                                val defaultNonSelf = availableExams.firstOrNull { !it.name.contains("Self-Study", ignoreCase = true) }
                                selectedExam = defaultNonSelf?.name ?: "UPSI – Police Sub-Inspector"
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_track_exam_course"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                if (!isSelfStudyMode) 2.dp else 1.dp,
                                if (!isSelfStudyMode) DeepIndigo else CardBorder
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (!isSelfStudyMode) DeepIndigoSubtle else BackgroundOffWhite
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = null,
                                    tint = if (!isSelfStudyMode) DeepIndigo else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Exam Course",
                                    fontSize = 12.sp,
                                    fontWeight = if (!isSelfStudyMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (!isSelfStudyMode) DeepIndigo else TextSecondary
                                )
                            }
                        }

                        // Self-Study Tab
                        OutlinedCard(
                            onClick = {
                                isSelfStudyMode = true
                                selectedExam = "Self-Study & Micro-Learning"
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_track_self_study"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                if (isSelfStudyMode) 2.dp else 1.dp,
                                if (isSelfStudyMode) DeepIndigo else CardBorder
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelfStudyMode) DeepIndigoSubtle else BackgroundOffWhite
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = if (isSelfStudyMode) DeepIndigo else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Self-Study",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelfStudyMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelfStudyMode) DeepIndigo else TextSecondary
                                )
                            }
                        }
                    }
                }

                // 3. Exam Selector (Only if in Exam Course Mode)
                if (!isSelfStudyMode) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Exam *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepIndigoLight
                        )
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { examMenuExpanded = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("upload_select_exam"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, CardBorder),
                                colors = CardDefaults.outlinedCardColors(containerColor = BackgroundOffWhite)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = DeepIndigo, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = selectedExam.ifBlank { "Select Exam" },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Open dropdown", tint = DeepIndigo)
                                }
                            }

                            DropdownMenu(
                                expanded = examMenuExpanded,
                                onDismissRequest = { examMenuExpanded = false }
                            ) {
                                availableExams.filter { !it.name.contains("Self-Study", ignoreCase = true) }.forEach { exam ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                if (exam.name.equals(selectedExam, ignoreCase = true)) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Terracotta, modifier = Modifier.size(16.dp))
                                                }
                                                Text(exam.name, fontWeight = if (exam.name.equals(selectedExam, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal)
                                            }
                                        },
                                        onClick = {
                                            selectedExam = exam.name
                                            examMenuExpanded = false
                                        }
                                    )
                                }
                                if (onAddExam != null) {
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Icons.Default.Add, contentDescription = null, tint = Terracotta, modifier = Modifier.size(16.dp))
                                                Text("+ Add New Exam Course", color = Terracotta, fontWeight = FontWeight.SemiBold)
                                            }
                                        },
                                        onClick = {
                                            examMenuExpanded = false
                                            showAddExamDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Subject Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Subject *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepIndigoLight
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            onClick = { subjectMenuExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_select_subject"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorder),
                            colors = CardDefaults.outlinedCardColors(containerColor = BackgroundOffWhite)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = DeepIndigo, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = selectedSubject.ifBlank { "Select Subject" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Open dropdown", tint = DeepIndigo)
                            }
                        }

                        DropdownMenu(
                            expanded = subjectMenuExpanded,
                            onDismissRequest = { subjectMenuExpanded = false }
                        ) {
                            availableSubjects.forEach { subj ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (subj.name.equals(selectedSubject, ignoreCase = true)) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Terracotta, modifier = Modifier.size(16.dp))
                                            }
                                            Text(subj.name, fontWeight = if (subj.name.equals(selectedSubject, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal)
                                        }
                                    },
                                    onClick = {
                                        selectedSubject = subj.name
                                        subjectMenuExpanded = false
                                    }
                                )
                            }
                            if (onAddSubject != null) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Terracotta, modifier = Modifier.size(16.dp))
                                            Text("+ Add New Subject", color = Terracotta, fontWeight = FontWeight.SemiBold)
                                        }
                                    },
                                    onClick = {
                                        subjectMenuExpanded = false
                                        showAddSubjectDialog = true
                                    }
                                )
                            }
                        }
                    }
                }

                // 5. Chapter Selector (Includes "Entire Subject (All Chapters / General)" option)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Chapter / Scope *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepIndigoLight
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            onClick = { chapterMenuExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_select_chapter"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CardBorder),
                            colors = CardDefaults.outlinedCardColors(containerColor = BackgroundOffWhite)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = Terracotta, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = selectedChapter.ifBlank { "Select Chapter" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (selectedChapter.startsWith("Entire Subject")) Terracotta else TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Open dropdown", tint = DeepIndigo)
                            }
                        }

                        DropdownMenu(
                            expanded = chapterMenuExpanded,
                            onDismissRequest = { chapterMenuExpanded = false }
                        ) {
                            // Primary Option: Entire Subject
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (selectedChapter == entireSubjectOption) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Terracotta, modifier = Modifier.size(16.dp))
                                            }
                                            Text(
                                                text = "📌 Entire Subject (All Chapters / General)",
                                                fontWeight = FontWeight.Bold,
                                                color = Terracotta
                                            )
                                        }
                                        Text(
                                            text = "Reel covers full subject, overall concepts or multi-chapter revision",
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(start = 22.dp)
                                        )
                                    }
                                },
                                onClick = {
                                    selectedChapter = entireSubjectOption
                                    chapterMenuExpanded = false
                                }
                            )

                            if (rawChapters.isNotEmpty()) {
                                HorizontalDivider()
                                rawChapters.forEach { chap ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                if (chap.equals(selectedChapter, ignoreCase = true)) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = DeepIndigo, modifier = Modifier.size(16.dp))
                                                }
                                                Text(chap, fontWeight = if (chap.equals(selectedChapter, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal)
                                            }
                                        },
                                        onClick = {
                                            selectedChapter = chap
                                            chapterMenuExpanded = false
                                        }
                                    )
                                }
                            }

                            if (onAddChapter != null) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Terracotta, modifier = Modifier.size(16.dp))
                                            Text("+ Add New Chapter", color = Terracotta, fontWeight = FontWeight.SemiBold)
                                        }
                                    },
                                    onClick = {
                                        chapterMenuExpanded = false
                                        showAddChapterDialog = true
                                    }
                                )
                            }
                        }
                    }
                }

                // 6. Notes / Summary
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Short Summary / Notes (Optional)") },
                    placeholder = { Text("Key formulas, memory hooks, or notes about this reel...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_reel_input_notes")
                )

                Spacer(modifier = Modifier.height(2.dp))

                // 7. Video Picker Button
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
                        text = if (selectedVideoUri != null) "Video Clip Selected ✓ (Tap to change)" else "Choose Video from Device",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            val isFormValid = title.isNotBlank() && selectedVideoUri != null && selectedSubject.isNotBlank()
            Button(
                onClick = {
                    if (isFormValid) {
                        val finalChapter = if (selectedChapter.startsWith("Entire Subject")) {
                            "Entire Subject"
                        } else {
                            selectedChapter.trim()
                        }
                        val finalExam = if (isSelfStudyMode) {
                            "Self-Study & Micro-Learning"
                        } else {
                            selectedExam.trim().ifBlank { "UPSI – Police Sub-Inspector" }
                        }
                        onConfirmUpload(
                            title.trim(),
                            description.trim(),
                            finalExam,
                            selectedSubject.trim(),
                            finalChapter,
                            selectedVideoUri!!
                        )
                    }
                },
                enabled = isFormValid,
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

// =========================================================================
// 7. REELS TEST FLOW SCREENS (Matching Hierarchy, source_type == "reel")
// =========================================================================

/**
 * Level 1: Choose Exam Course or Self-Study Subject for Reels Test
 */
@Composable
fun ReelTestExamsScreen(
    onBackClick: () -> Unit,
    onSelectExam: (String) -> Unit,
    onSelectSubject: (String) -> Unit,
    exams: List<CurriculumExam>,
    subjects: List<CurriculumSubject>,
    reelQuestions: List<QuestionEntity>,
    modifier: Modifier = Modifier
) {
    val standaloneSubjects = remember(subjects) {
        subjects.filter { it.isStandalone }
    }

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
            Spacer(modifier = Modifier.height(14.dp))

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("reel_test_exams_back")
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
                        text = "Reels Practice Test",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo
                    )
                    Text(
                        text = "Tests generated exclusively from video reels",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Section 1: Enrolled Exam Courses
                item {
                    Text(
                        text = "Select Exam Course",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                items(exams) { exam ->
                    // Count reel questions for this exam
                    val qCount = reelQuestions.count { it.examId.equals(exam.name, ignoreCase = true) || it.examId.contains("UPSI", ignoreCase = true) && exam.name.contains("UPSI", ignoreCase = true) }

                    InteractiveCard(
                        onClick = { onSelectExam(exam.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reel_test_exam_card_${exam.name.lowercase().replace(" ", "_")}"),
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
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DeepIndigo.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = DeepIndigo,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exam.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                if (exam.subtitle.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = exam.subtitle,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (qCount > 0) TerracottaLight else Color(0xFFE2E8F0))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (qCount > 0) "$qCount Reel Questions" else "0 Questions",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (qCount > 0) Terracotta else TextSecondary
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

                // Section 2: Self-Study & Independent Subjects
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Self-Study & Micro-Learning",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                items(standaloneSubjects) { subject ->
                    val qCount = reelQuestions.count { it.subjectName.equals(subject.name, ignoreCase = true) }

                    InteractiveCard(
                        onClick = { onSelectSubject(subject.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reel_test_subject_card_${subject.name.lowercase().replace(" ", "_")}"),
                        shape = RoundedCornerShape(16.dp),
                        elevation = 4.dp
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
                                    text = subject.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                if (subject.subtitle.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = subject.subtitle,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (qCount > 0) TerracottaLight else Color(0xFFE2E8F0))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (qCount > 0) "$qCount Reel Questions" else "0 Questions",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (qCount > 0) Terracotta else TextSecondary
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

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

/**
 * Level 2: Subjects Grid for Reels Test (under an Exam)
 */
@Composable
fun ReelTestSubjectsGridScreen(
    examName: String,
    onBackClick: () -> Unit,
    onSelectSubject: (String) -> Unit,
    subjects: List<CurriculumSubject>,
    reelQuestions: List<QuestionEntity>,
    modifier: Modifier = Modifier
) {
    val examSubjects = remember(subjects, examName) {
        subjects.filter { it.examName?.equals(examName, ignoreCase = true) == true }
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
                modifier = Modifier.testTag("reel_test_subjects_back")
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
                    text = "$examName - Reels Test",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                Text(
                    text = "Select a subject to practice questions",
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
            items(examSubjects) { subject ->
                val qCount = reelQuestions.count { it.subjectName.equals(subject.name, ignoreCase = true) }

                InteractiveCard(
                    onClick = { onSelectSubject(subject.name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reel_test_subject_${subject.name.lowercase().replace(" ", "_")}"),
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
                                imageVector = Icons.Outlined.Quiz,
                                contentDescription = null,
                                tint = Terracotta,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = subject.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (qCount > 0) "$qCount Questions" else "0 Questions",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (qCount > 0) Terracotta else TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to practice",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Level 3: Subject Detail showing chapters with Start Test for Reels
 */
@Composable
fun ReelTestSubjectDetailScreen(
    examName: String,
    subjectName: String,
    onBackClick: () -> Unit,
    onStartChapterTest: (chapterName: String) -> Unit,
    onStartFullSubjectTest: () -> Unit,
    onNavigateToReels: (chapterName: String) -> Unit,
    chapters: List<CurriculumChapter>,
    reelQuestions: List<QuestionEntity>,
    modifier: Modifier = Modifier
) {
    val subjectReelQuestions = remember(reelQuestions, subjectName) {
        reelQuestions.filter { it.subjectName.equals(subjectName, ignoreCase = true) }
    }

    val subjectChapters = remember(chapters, subjectName, subjectReelQuestions) {
        val list = mutableListOf<String>()
        chapters.filter { it.subjectName.equals(subjectName, ignoreCase = true) }.forEach {
            list.add(it.name)
        }
        subjectReelQuestions.map { it.chapterName }.distinct().forEach { ch ->
            if (!list.contains(ch)) list.add(ch)
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("reel_test_detail_back")
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
                    text = "$examName • ${subjectReelQuestions.size} Reel Questions",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Full Subject Test Hero Card
            if (subjectReelQuestions.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .tapAffordance(shape = RoundedCornerShape(18.dp), elevation = 6.dp)
                            .testTag("reel_test_full_subject_button")
                            .clickable { onStartFullSubjectTest() },
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
                                    text = "FULL SUBJECT TEST",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Practice All Reel Questions",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${subjectReelQuestions.size} questions from $subjectName video reels",
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
                                    contentDescription = "Start Test",
                                    tint = Terracotta,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Chapters Header
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
                        text = "${subjectChapters.size} Chapters",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Chapter Cards
            items(subjectChapters) { chapterName ->
                val chapterQuestions = subjectReelQuestions.filter { it.chapterName.equals(chapterName, ignoreCase = true) }
                val hasQuestions = chapterQuestions.isNotEmpty()

                InteractiveCard(
                    onClick = {
                        if (hasQuestions) {
                            onStartChapterTest(chapterName)
                        } else {
                            onNavigateToReels(chapterName)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reel_test_chapter_${chapterName.lowercase().replace(" ", "_")}"),
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
                                    text = chapterName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigo
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (hasQuestions) "${chapterQuestions.size} Reel Questions" else "No questions yet",
                                    fontSize = 12.sp,
                                    color = if (hasQuestions) Terracotta else TextSecondary,
                                    fontWeight = if (hasQuestions) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }

                            if (hasQuestions) {
                                Button(
                                    onClick = { onStartChapterTest(chapterName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("start_test_chapter_${chapterName.lowercase().replace(" ", "_")}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Start Test", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { onNavigateToReels(chapterName) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    border = BorderStroke(1.dp, SageGreen),
                                    modifier = Modifier.testTag("watch_reels_chapter_${chapterName.lowercase().replace(" ", "_")}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Watch Reels", fontSize = 11.sp, color = SageGreen, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        // If no questions in this chapter, show friendly empty state
                        if (!hasQuestions) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QuestionAnswer,
                                        contentDescription = null,
                                        tint = Amber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "No questions created from reels yet. Watch a reel and tap + Question to add one!",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
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
// 8. REEL QUESTIONS HUB SHEET & INTERACTIVE PRACTICE QUIZ SHEET
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelQuestionsHubSheet(
    reel: ReelEntity,
    questions: List<QuestionEntity>,
    onDismiss: () -> Unit,
    onStartQuiz: () -> Unit,
    onOpenBulkAdd: () -> Unit,
    onOpenCsvImport: () -> Unit,
    onOpenSingleAdd: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(TerracottaLight)
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Reel #${reel.id} Study Hub",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Terracotta
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("dismiss_reel_hub_sheet")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepIndigo)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = reel.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )
            Text(
                text = "${reel.subject} • ${reel.chapter}",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // If questions exist, prominent Practice Quiz Button!
            if (questions.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SageGreenLight),
                    border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🎯 ${questions.size} Questions Available",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreen
                                )
                                Text(
                                    text = "Test your recall right now based on this video",
                                    fontSize = 12.sp,
                                    color = DeepIndigo
                                )
                            }
                            Button(
                                onClick = onStartQuiz,
                                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("button_start_reel_quiz")
                            ) {
                                Text("Practice", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundOffWhite),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 No questions added yet for this reel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add 3 to 5 questions or import from CSV so learners can practice active recall after watching this concept!",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "Add Questions to this Reel",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DeepIndigo
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Bulk Add (3-5+ Questions)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenBulkAdd() }
                    .testTag("action_open_bulk_add_reel"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.5.dp, Terracotta)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(TerracottaLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚡", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bulk Add (3–5+ Questions)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "Draft multiple questions with options in a single screen",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Import CSV / File
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenCsvImport() }
                    .testTag("action_open_csv_import_reel"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.5.dp, DeepIndigo)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAEDF3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = DeepIndigo)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Import CSV or Text File",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "Upload .csv file with 3 to 5+ questions or paste spreadsheet rows",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Single Question
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenSingleAdd() }
                    .testTag("action_open_single_add_reel"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundOffWhite),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAEDF3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = DeepIndigo)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Single Question",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepIndigo
                        )
                        Text(
                            text = "Manually add one question at a time",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Existing Questions list preview if available
            if (questions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Current Questions (${questions.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                Spacer(modifier = Modifier.height(8.dp))
                questions.forEachIndexed { qIdx, q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundOffWhite),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Q${qIdx + 1}: ${q.questionText}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepIndigo
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Answer: " + when (q.correctAnswerIndex) {
                                    0 -> if (q.questionType == "TRUE_FALSE") "True" else q.optionA
                                    1 -> if (q.questionType == "TRUE_FALSE") "False" else q.optionB
                                    2 -> q.optionC
                                    else -> q.optionD
                                },
                                fontSize = 12.sp,
                                color = SageGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelPracticeQuizSheet(
    reel: ReelEntity,
    questions: List<QuestionEntity>,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var quizCompleted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 Reel Quiz: ${reel.chapter}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = DeepIndigo)
                }
            }

            if (!quizCompleted && questions.isNotEmpty()) {
                val currentQ = questions[currentIndex]

                Spacer(modifier = Modifier.height(10.dp))

                // Progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Question ${currentIndex + 1} of ${questions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Terracotta
                    )
                    Text(
                        text = "Score: $score",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundOffWhite),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Text(
                        text = currentQ.questionText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigo,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val options = if (currentQ.questionType == "TRUE_FALSE") {
                    listOf(0 to "True", 1 to "False")
                } else {
                    listOf(
                        0 to currentQ.optionA,
                        1 to currentQ.optionB,
                        2 to currentQ.optionC,
                        3 to currentQ.optionD
                    ).filter { it.second.isNotBlank() }
                }

                options.forEach { (idx, optText) ->
                    val isCorrect = idx == currentQ.correctAnswerIndex
                    val isSelected = selectedOption == idx
                    val cardBg = when {
                        !hasAnswered -> SurfaceWhite
                        isSelected && isCorrect -> SageGreenLight
                        isSelected && !isCorrect -> Color(0xFFFFEBEE)
                        isCorrect -> SageGreenLight
                        else -> SurfaceWhite
                    }
                    val borderCol = when {
                        !hasAnswered && isSelected -> DeepIndigo
                        hasAnswered && isCorrect -> SageGreen
                        hasAnswered && isSelected && !isCorrect -> Color.Red
                        else -> CardBorder
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = !hasAnswered) {
                                selectedOption = idx
                                hasAnswered = true
                                if (isCorrect) score++
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.5.dp, borderCol)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${('A' + idx)}. $optText",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected || (hasAnswered && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                                color = DeepIndigo,
                                modifier = Modifier.weight(1f)
                            )
                            if (hasAnswered && isCorrect) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SageGreen, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (hasAnswered) {
                    Button(
                        onClick = {
                            if (currentIndex + 1 < questions.size) {
                                currentIndex++
                                selectedOption = null
                                hasAnswered = false
                            } else {
                                quizCompleted = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentIndex + 1 < questions.size) "Next Question" else "View Final Score",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else if (quizCompleted) {
                // Quiz completed result
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉 Quiz Complete!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepIndigo)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "You scored $score / ${questions.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (score >= questions.size / 2) SageGreen else Terracotta
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (score == questions.size) "Perfect recall! Knowledge retained." else "Great active practice! Keep repeating for maximum retention.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepIndigo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Finish")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
