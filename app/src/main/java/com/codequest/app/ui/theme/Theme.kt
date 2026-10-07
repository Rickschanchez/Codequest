package com.codequest.app.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue

/**
 * The CodeQuest theme.
 *
 * - Resolves Light / Dark / System Default from [ThemeState].
 * - Drives [CodeQuestColors] through a smooth blend so every screen
 *   transitions between themes instead of snapping.
 * - Material roles are assembled exclusively from [CodeQuestColors].
 */
@Composable
fun CodeQuestTheme(
    content: @Composable () -> Unit,
) {
    val isDark = ThemeState.resolvedIsDark()
    val blend by animateFloatAsState(
        targetValue = if (isDark) 1f else 0f,
        animationSpec = tween(durationMillis = CodeQuestMotion.themeDuration),
        label = "themeBlend",
    )
    SideEffect {
        CodeQuestColors.setBlend(blend)
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = CodeQuestColors.Cyan,
            onPrimary = CodeQuestColors.OnAccent,
            secondary = CodeQuestColors.Violet,
            onSecondary = CodeQuestColors.OnAccent,
            tertiary = CodeQuestColors.Amber,
            onTertiary = CodeQuestColors.OnAccent,
            background = CodeQuestColors.Background,
            onBackground = CodeQuestColors.TextPrimary,
            surface = CodeQuestColors.Surface,
            onSurface = CodeQuestColors.TextPrimary,
            surfaceVariant = CodeQuestColors.SurfaceMuted,
            onSurfaceVariant = CodeQuestColors.TextSecondary,
            outline = CodeQuestColors.Outline,
            outlineVariant = CodeQuestColors.OutlineSoft,
            error = CodeQuestColors.Rose,
            onError = CodeQuestColors.OnAccent,
        )
    } else {
        lightColorScheme(
            primary = CodeQuestColors.CyanDeep,
            onPrimary = CodeQuestColors.OnAccent,
            secondary = CodeQuestColors.Violet,
            onSecondary = CodeQuestPalette.WarmWhite,
            tertiary = CodeQuestColors.Violet,
            onTertiary = CodeQuestPalette.WarmWhite,
            background = CodeQuestColors.Background,
            onBackground = CodeQuestColors.TextPrimary,
            surface = CodeQuestColors.Surface,
            onSurface = CodeQuestColors.TextPrimary,
            surfaceVariant = CodeQuestColors.SurfaceMuted,
            onSurfaceVariant = CodeQuestColors.TextSecondary,
            outline = CodeQuestColors.Outline,
            outlineVariant = CodeQuestColors.OutlineSoft,
            error = CodeQuestColors.Rose,
            onError = CodeQuestPalette.WarmWhite,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CodeQuestTypography,
        content = content,
    )
}
