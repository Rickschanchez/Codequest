package com.codequest.app.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Centralised corner-radius tokens. Restrained geometry: 8 / 12 / 16 with a
 * slightly larger radius reserved for hero cards. Nothing is a giant pill.
 */
object CodeQuestShapes {
    val card = 16.dp
    val cardSmall = 12.dp
    val tile = 12.dp
    val button = 12.dp
    val chip = 8.dp
    val iconBox = 10.dp
    val pill = 50
    val bottomBar = 16.dp
    val sheet = 20.dp
}

/** Centralised spacing tokens — generous whitespace is part of the identity. */
object CodeQuestSpacing {
    val xxs = 3.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val screenHorizontal = 20.dp
    val sectionGap = 20.dp
}

/**
 * Surface recipes for the editorial card language.
 *
 * The name [CodeQuestGlass] is kept for compatibility, but the visual role has
 * changed: cards are now SOLID dark surfaces with subtle borders rather than
 * frosted glass. Translucency is no longer part of the dominant language.
 */
object CodeQuestGlass {
    /** Solid card fill — charcoal panel on dark, warm white on paper. */
    val cardFill: Color get() = CodeQuestColors.Surface

    /** Subtle neutral card border. */
    val border: Color get() = CodeQuestColors.Outline

    /** Soft shadow tint — charcoal in dark, warm brown on paper. */
    val shadow: Color
        get() = androidx.compose.ui.graphics.lerp(
            CodeQuestPalette.Charcoal.copy(alpha = 0.55f),
            CodeQuestPalette.Brown.copy(alpha = 0.16f),
            CodeQuestColors.themeBlend,
        )

    /** Primary CTA fill — the exact accent orange in both themes. */
    val primaryFill: Color get() = CodeQuestColors.AccentFill

    /** Primary CTA pressed state. */
    val primaryFillPressed: Color get() = CodeQuestColors.AccentFillPressed

    /** Secondary control fill — deep brown on dark, soft brown tint on paper. */
    val secondaryFill: Color
        get() = androidx.compose.ui.graphics.lerp(
            CodeQuestPalette.BrownLight,
            CodeQuestPalette.Brown,
            CodeQuestColors.themeBlend,
        )
}

/**
 * Motion tokens. Everything is fast, smooth and purposeful — no bouncing, no
 * particles, no infinite decoration.
 */
object CodeQuestMotion {
    /** Light ↔ Dark theme transition. */
    const val themeDuration = 320

    /** Screen enter / exit. */
    const val screenDuration = 240

    /** Card entrance (fade + small translation). */
    const val cardEntrance = 340

    /** Stagger step between list items. */
    const val staggerStep = 50

    /** Theme toggle thumb / icon motion. */
    const val toggleDuration = 300

    /** Pressed-scale target. */
    const val pressedScale = 0.975f

    /** Generic smooth tween — [T] is inferred from the animation it drives. */
    fun <T> smooth(duration: Int = toggleDuration): TweenSpec<T> =
        tween(duration, easing = FastOutSlowInEasing)

    /** Soft spring used for presses and feedback — smooth, barely settles. */
    val softSpring = spring<Float>(
        dampingRatio = 0.86f,
        stiffness = Spring.StiffnessMediumLow,
    )
}
