package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SurfaceWhite

// High-opacity, clearly visible drop shadow colors that make interactive
// elements appear physically raised/floating above the page background.
val InteractiveShadowSpot = Color(0x550F172A)     // ~33% opacity deep navy/slate directional drop shadow
val InteractiveShadowAmbient = Color(0x280F172A)  // ~16% opacity ambient shadow
val InteractiveCardBorder = Color(0xFFE2E8F0)
val StaticCardBorder = Color(0xFFF1F5F9)

/**
 * Reusable modifier for any tappable/clickable element that needs
 * a clearly visible, raised drop shadow to communicate unmistakable clickability.
 */
fun Modifier.tapAffordance(
    shape: Shape = RoundedCornerShape(14.dp),
    elevation: Dp = 5.dp
): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = InteractiveShadowAmbient,
    spotColor = InteractiveShadowSpot
)

/**
 * Press feedback animation modifier: provides a slight tactile scale-down
 * on press to give immediate physical confirmation that the click registered.
 */
@Composable
fun Modifier.pressableScale(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    scaleDown: Float = 0.96f
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "press_scale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Primary action button: unmistakable physical presence with solid background fill,
 * visible drop shadow, rounded corners (12-16px), and scale-down + shadow-flatten animation on tap.
 */
@Composable
fun MindLoopPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = DeepIndigo,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(14.dp),
    elevation: Dp = 5.dp,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "primary_btn_scale"
    )
    val currentElevation by animateDpAsState(
        targetValue = if (isPressed) 1.5.dp else elevation,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "primary_btn_elevation"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .tapAffordance(shape = shape, elevation = currentElevation),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            pressedElevation = 1.5.dp,
            focusedElevation = elevation + 1.dp,
            hoveredElevation = elevation + 1.dp
        ),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * Secondary / Outlined button: distinct solid white background fill (never transparent text),
 * high-contrast border, visible drop shadow, and tactile scale feedback on press.
 */
@Composable
fun MindLoopSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = if (LocalIsDarkTheme.current) Color(0xFF1E293B) else SurfaceWhite,
    contentColor: Color = if (LocalIsDarkTheme.current) Color(0xFFF1F5F9) else DeepIndigo,
    borderColor: Color = if (LocalIsDarkTheme.current) Color(0xFF475569) else DeepIndigo,
    shape: Shape = RoundedCornerShape(14.dp),
    elevation: Dp = 3.5.dp,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "secondary_btn_scale"
    )
    val currentElevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else elevation,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "secondary_btn_elevation"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .tapAffordance(shape = shape, elevation = currentElevation),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.6f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.5.dp, borderColor),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            pressedElevation = 1.dp,
            focusedElevation = elevation + 1.dp,
            hoveredElevation = elevation + 1.dp
        ),
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * Standard interactive card with consistent raised drop shadow, visible border,
 * and tactile press-down feedback.
 */
@Composable
fun InteractiveCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = if (LocalIsDarkTheme.current) Color(0xFF1E293B) else SurfaceWhite,
    border: BorderStroke? = BorderStroke(1.dp, if (LocalIsDarkTheme.current) Color(0xFF334155) else InteractiveCardBorder),
    elevation: Dp = 5.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )
    val currentElevation by animateDpAsState(
        targetValue = if (isPressed) 1.5.dp else elevation,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "card_elevation"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .tapAffordance(shape = shape, elevation = currentElevation)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            pressedElevation = 1.5.dp
        ),
        border = border,
        content = content
    )
}

/**
 * Standard static/informational card (purely informational, flat with subtle border).
 */
@Composable
fun InformationalCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = if (LocalIsDarkTheme.current) Color(0xFF1E293B) else SurfaceWhite,
    border: BorderStroke? = BorderStroke(1.dp, if (LocalIsDarkTheme.current) Color(0xFF334155) else StaticCardBorder),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = border,
        content = content
    )
}
