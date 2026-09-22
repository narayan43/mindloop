package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.BottomNavTab
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppSurface
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SurfaceWhite

@Composable
fun MindLoopBottomNavBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppSurface)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(
            color = AppBorder,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                label = "Home",
                icon = Icons.Outlined.Home,
                isSelected = selectedTab == BottomNavTab.HOME,
                testTag = "nav_tab_home",
                onClick = { onTabSelected(BottomNavTab.HOME) }
            )

            NavBarItem(
                label = "Study",
                icon = Icons.AutoMirrored.Outlined.MenuBook,
                isSelected = selectedTab == BottomNavTab.STUDY,
                testTag = "nav_tab_study",
                onClick = { onTabSelected(BottomNavTab.STUDY) }
            )

            NavBarItem(
                label = "Test",
                icon = Icons.Outlined.Psychology,
                isSelected = selectedTab == BottomNavTab.TEST,
                testTag = "nav_tab_test",
                onClick = { onTabSelected(BottomNavTab.TEST) }
            )

            NavBarItem(
                label = "Mistakes",
                icon = Icons.Outlined.TrackChanges,
                isSelected = selectedTab == BottomNavTab.MISTAKES,
                testTag = "nav_tab_mistakes",
                onClick = { onTabSelected(BottomNavTab.MISTAKES) }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val activeColor = if (isDark) Color(0xFF818CF8) else DeepIndigo
    val inactiveColor = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .testTag(testTag)
            .size(width = 76.dp, height = 56.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 28.dp),
                role = Role.Tab,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Dot indicator above icon
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(
                    if (isSelected) activeColor else Color.Transparent,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.height(3.dp))

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = if (isSelected) activeColor else inactiveColor,
            maxLines = 1
        )
    }
}
