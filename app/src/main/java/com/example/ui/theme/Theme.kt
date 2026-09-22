package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val LocalIsDarkTheme = staticCompositionLocalOf { false }

private val LightColorScheme = lightColorScheme(
    primary = DeepIndigo,
    onPrimary = Color.White,
    primaryContainer = DeepIndigoSubtle,
    onPrimaryContainer = DeepIndigo,
    secondary = SageGreen,
    onSecondary = Color.White,
    secondaryContainer = SageGreenLight,
    onSecondaryContainer = SageGreenDark,
    tertiary = Amber,
    onTertiary = Color.White,
    tertiaryContainer = AmberLight,
    onTertiaryContainer = AmberDark,
    error = Terracotta,
    onError = Color.White,
    errorContainer = TerracottaLight,
    onErrorContainer = TerracottaDark,
    background = BackgroundOffWhite,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundOffWhite,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFE2E8F0),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF451A03),
    error = Color(0xFFF87171),
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155)
)

val MindLoopShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

val AppBackground: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF0F172A) else BackgroundOffWhite

val AppSurface: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF1E293B) else SurfaceWhite

val AppSurfaceElevated: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF273549) else Color(0xFFF8FAFC)

val AppTextPrimary: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFFF8FAFC) else DeepIndigo

val AppTextSecondary: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF94A3B8) else TextSecondary

val AppBorder: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF334155) else CardBorder

val AppTrackColor: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF334155) else Color(0xFFEFF2F6)

val AppOptionCardBg: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF243044) else BackgroundOffWhite

val AppTextMuted: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF64748B) else TextMuted

val AppSuccessText: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF34D399) else SageGreen

val AppSuccessBg: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF133322) else SageGreenLight

val AppErrorText: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFFF87171) else Terracotta

val AppErrorBg: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF3E1B1B) else TerracottaLight

val AppWarningText: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFFFBBF24) else Amber

val AppWarningBg: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF3B2D14) else AmberLight

val AppAccentPrimary: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF818CF8) else DeepIndigo

val AppIconCircleBg: Color
    @Composable
    get() = if (LocalIsDarkTheme.current) Color(0xFF243044) else DeepIndigo.copy(alpha = 0.08f)

@Composable
fun MindLoopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = MindLoopShapes,
            content = content
        )
    }
}
