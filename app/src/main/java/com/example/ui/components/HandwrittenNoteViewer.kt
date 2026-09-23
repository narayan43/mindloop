package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.entity.NoteEntity
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextSecondary
import com.example.util.ImageStorageHelper
import com.example.util.OfflineImageManager
import kotlinx.coroutines.launch

@Composable
fun HandwrittenNoteViewer(
    note: NoteEntity?,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showFullscreenDialog by remember { mutableStateOf(false) }

    // Identify if this note is an Image Note or a Written Study Note
    val imageUris = remember(note?.imageUri) {
        ImageStorageHelper.parseImageUris(note?.imageUri)
    }
    val isImageNote = imageUris.isNotEmpty()

    val gestureModifier = if (scale > 1.05f) {
        Modifier.pointerInput(scale) {
            detectTransformGestures { _, pan, zoom, _ ->
                scale = (scale * zoom).coerceIn(1.0f, 3.5f)
                offset = Offset(
                    x = (offset.x + pan.x).coerceIn(-500f * scale, 500f * scale),
                    y = (offset.y + pan.y).coerceIn(-700f * scale, 700f * scale)
                )
                if (scale <= 1.05f) {
                    scale = 1f
                    offset = Offset.Zero
                }
            }
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isImageNote) Color(0xFFF1F5F9) else Color(0xFFE8DFD8)) // Kraft cardboard outer backing only for written notes
            .padding(if (isImageNote) 0.dp else 4.dp)
            .then(gestureModifier)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            shape = RoundedCornerShape(if (isImageNote) 14.dp else 10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (isImageNote) {
                // Pure Image Note: no text mashup, shows textbook / notes photo
                ImageNoteView(
                    note = note,
                    imageUris = imageUris,
                    onExpandFullscreen = { showFullscreenDialog = true }
                )
            } else {
                // Pure Written Study Note: clean lined note sheet with written content
                WrittenStudyNoteView(
                    note = note
                )
            }
        }

        // Floating Zoom Control Pill (bottom right)
        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(2.dp)
            ) {
                IconButton(
                    onClick = { scale = (scale + 0.25f).coerceAtMost(3.5f) },
                    modifier = Modifier.size(36.dp).testTag("zoom_in_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = DeepIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(1.dp)
                        .background(CardBorder)
                )

                IconButton(
                    onClick = {
                        scale = (scale - 0.25f).coerceAtLeast(1.0f)
                        if (scale <= 1.05f) {
                            scale = 1f
                            offset = Offset.Zero
                        }
                    },
                    modifier = Modifier.size(36.dp).testTag("zoom_out_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = DeepIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Fullscreen Dialog for Image Notes
    if (showFullscreenDialog && isImageNote) {
        FullscreenNoteImageDialog(
            note = note,
            imageUris = imageUris,
            onDismiss = { showFullscreenDialog = false }
        )
    }
}

/**
 * Pure Image Note: Displays image(s) clearly without any mashed-up text below it.
 */
@Composable
private fun ImageNoteView(
    note: NoteEntity?,
    imageUris: List<String>,
    onExpandFullscreen: () -> Unit
) {
    var activeImageIndex by remember(note?.id) { mutableIntStateOf(0) }
    val totalImages = imageUris.size
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        // Streamlined, compact Header (Single Row)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFFC1666B), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = note?.title?.ifBlank { "Image Note" } ?: "Image Note",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            val isOffline = remember(note?.id, note?.imageUri) {
                OfflineImageManager.isImageDownloadedLocally(context, note?.id ?: 0L, note?.imageUri)
            }
            if (isOffline) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F766E).copy(alpha = 0.12f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🔒 Offline",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                }
            }

            // Fullscreen Expand
            IconButton(
                onClick = onExpandFullscreen,
                modifier = Modifier.size(32.dp).testTag("expand_fullscreen_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Full Screen",
                    tint = DeepIndigo,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Multi-image switcher controls (if more than 1 image attached)
        if (totalImages > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { activeImageIndex = (activeImageIndex - 1).coerceAtLeast(0) },
                    enabled = activeImageIndex > 0,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Photo", tint = DeepIndigo)
                }

                Text(
                    text = "Photo ${activeImageIndex + 1} of $totalImages",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigo
                )

                IconButton(
                    onClick = { activeImageIndex = (activeImageIndex + 1).coerceAtMost(totalImages - 1) },
                    enabled = activeImageIndex < totalImages - 1,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Photo", tint = DeepIndigo)
                }
            }
        }

        // The Image Note: expands and fills the card cleanly
        val rawUri = imageUris.getOrNull(activeImageIndex) ?: imageUris.firstOrNull()
        val resolvedImage = remember(note?.id, rawUri) {
            ImageStorageHelper.resolveImageSource(context, note?.id ?: 0L, rawUri)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF8FAFC))
                .clickable(onClick = onExpandFullscreen),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = resolvedImage,
                contentDescription = note?.title ?: "Note Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * Pure Written Study Note: Displays clean handwritten/serif notebook text on lined paper.
 */
@Composable
private fun WrittenStudyNoteView(note: NoteEntity?) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFFC1666B), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = note?.title?.ifBlank { "Study Note" } ?: "Study Note",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E293B)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEDE9E3))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "📝 Written Note",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${note?.subjectName ?: ""} • ${note?.chapterName ?: ""}",
            fontSize = 11.5.sp,
            color = TextSecondary,
            fontFamily = FontFamily.Serif
        )

        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF86B3D1).copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Note Body (Lined notebook paper feel)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            val contentText = note?.summaryText ?: ""
            if (contentText.isNotBlank()) {
                val lines = contentText.lines()
                lines.forEach { line ->
                    Text(
                        text = line,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 23.sp,
                            color = Color(0xFF1A202C)
                        ),
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Key Takeaways & Review Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2C3E66).copy(alpha = 0.25f), RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F8F5))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Spaced Repetition Review",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = DeepIndigo
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val chapterDisplay = note?.chapterName ?: "Current Chapter"
                    Text(
                        text = "• Chapter: $chapterDisplay\n• Revisits recorded: ${note?.revisitCount ?: 1}\n• Active in practice quiz pool",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Fullscreen dialog for Image Notes
 */
@Composable
private fun FullscreenNoteImageDialog(
    note: NoteEntity?,
    imageUris: List<String>,
    onDismiss: () -> Unit
) {
    val totalSlides = imageUris.size
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { totalSlides.coerceAtLeast(1) })
    val coroutineScope = rememberCoroutineScope()
    var fullScale by remember { mutableFloatStateOf(1f) }
    var fullOffset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Horizontal sliding between images of this note
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(fullScale) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            fullScale = (fullScale * zoom).coerceIn(1f, 4f)
                            fullOffset = Offset(
                                x = (fullOffset.x + pan.x).coerceIn(-800f * fullScale, 800f * fullScale),
                                y = (fullOffset.y + pan.y).coerceIn(-1200f * fullScale, 1200f * fullScale)
                            )
                            if (fullScale <= 1.05f) {
                                fullScale = 1f
                                fullOffset = Offset.Zero
                            }
                        }
                    },
                userScrollEnabled = fullScale <= 1.05f
            ) { page ->
                val rawUri = imageUris.getOrNull(page)
                val context = LocalContext.current
                val resolvedImage = remember(note?.id, rawUri) {
                    ImageStorageHelper.resolveImageSource(context, note?.id ?: 0L, rawUri)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = fullScale,
                            scaleY = fullScale,
                            translationX = fullOffset.x,
                            translationY = fullOffset.y
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = resolvedImage,
                        contentDescription = note?.title ?: "Fullscreen Note Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Top overlay bar with close button & slide counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                if (totalSlides > 1) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1} / $totalSlides",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Reset zoom button
                if (fullScale > 1.05f) {
                    IconButton(
                        onClick = {
                            fullScale = 1f
                            fullOffset = Offset.Zero
                        },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(40.dp)
                    ) {
                        Text("1x", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            // Bottom controls: Previous / Next slide buttons if multiple photos exist for this note
            if (totalSlides > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                            }
                        },
                        enabled = pagerState.currentPage > 0,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = if (pagerState.currentPage > 0) 0.8f else 0.2f), CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = DeepIndigo)
                    }

                    Text(
                        text = "Swipe or tap arrows to view photos",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(totalSlides - 1))
                            }
                        },
                        enabled = pagerState.currentPage < totalSlides - 1,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = if (pagerState.currentPage < totalSlides - 1) 0.8f else 0.2f), CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = DeepIndigo)
                    }
                }
            }
        }
    }
}
