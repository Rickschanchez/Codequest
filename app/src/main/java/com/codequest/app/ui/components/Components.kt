package com.codequest.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codequest.app.domain.model.Difficulty
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestGlass
import com.codequest.app.ui.theme.CodeQuestMotion
import com.codequest.app.ui.theme.CodeQuestShapes
import com.codequest.app.ui.theme.CodeQuestTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Accent colour for a difficulty band, kept here so every screen agrees. */
fun difficultyColor(difficulty: Difficulty): Color = when (difficulty) {
    Difficulty.EASY -> CodeQuestColors.Emerald
    Difficulty.MEDIUM -> CodeQuestColors.Gold
    Difficulty.HARD -> CodeQuestColors.Rose
}

/** Formats a 0f..1f fraction as a whole percentage, e.g. `0.72f` → `"72%"`. */
fun Float.toPercentText(): String = "${(this.coerceIn(0f, 1f) * 100).toInt()}%"

/**
 * Editorial content block: solid surface, subtle border. Kept as the everyday
 * card name so every screen shares one card language.
 */
@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    accent: Color = CodeQuestColors.Accent,
    emphasis: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    GlassCard(
        modifier = modifier,
        accent = accent,
        emphasis = emphasis,
        contentPadding = contentPadding,
        content = content,
    )
}

/** Section title with an accent tick and an optional trailing slot. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    accent: Color = CodeQuestColors.Accent,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(3.dp)
                .height(15.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title.uppercase(),
            style = CodeQuestTypography.titleMedium,
            color = CodeQuestColors.TextPrimary,
            letterSpacing = 0.8.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke()
    }
}

/**
 * Smooth progress bar: muted track, animated fill. Soft and precise — no
 * glow, no halo (the [halo] parameter is retained for compatibility only).
 */
@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    startColor: Color = CodeQuestColors.Accent,
    endColor: Color = startColor,
    trackColor: Color = CodeQuestColors.Track,
    halo: Boolean = false,
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "progressBar",
    )
    val shape = remember { RoundedCornerShape(percent = CodeQuestShapes.pill) }
    val brush = remember(startColor, endColor) {
        Brush.horizontalGradient(listOf(startColor, endColor))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .matchParentSize()
                .clip(shape)
                .background(trackColor),
        )

        if (animated > 0.001f) {
            Box(
                Modifier
                    .fillMaxWidth(animated)
                    .fillMaxHeight()
                    .clip(shape)
                    .background(brush),
            )
        }
    }
}

/** Compact icon + label + value readout used on Home and Profile. */
@Composable
fun StatTile(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CodeQuestShapes.tile)
    Row(
        modifier = modifier
            .clip(shape)
            .background(CodeQuestGlass.cardFill)
            .border(1.dp, CodeQuestGlass.border, shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(CodeQuestShapes.iconBox))
                .background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(17.dp),
            )
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = label.uppercase(),
                style = CodeQuestTypography.labelSmall,
                color = CodeQuestColors.TextSecondary,
                letterSpacing = 0.7.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val numeric = value.all { it.isDigit() || it == ',' }
            if (numeric) {
                AnimatedNumber(
                    text = value,
                    accent = accent,
                    style = CodeQuestTypography.headlineSmall,
                    color = CodeQuestColors.TextPrimary,
                )
            } else {
                Text(
                    text = value,
                    style = CodeQuestTypography.titleMedium,
                    color = CodeQuestColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Number that smoothly counts to its new value with a subtle scale pulse and
 * a warm accent emphasis when it changes (§29 — XP animation).
 */
@Composable
fun AnimatedNumber(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = CodeQuestColors.Accent,
    style: androidx.compose.ui.text.TextStyle = CodeQuestTypography.headlineSmall,
    color: Color = CodeQuestColors.TextPrimary,
) {
    val target = remember(text) { text.filter { it.isDigit() }.ifEmpty { "0" }.toInt() }
    val counter = remember { Animatable(target.toFloat()) }
    val pulse = remember { Animatable(0f) }

    LaunchedEffect(target) {
        if (counter.value != target.toFloat()) {
            launch {
                counter.animateTo(
                    targetValue = target.toFloat(),
                    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                )
            }
            pulse.snapTo(1f)
            pulse.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            )
        }
    }

    val scale = 1f + 0.07f * pulse.value
    val emphasized = lerpColor(color, accent, pulse.value * 0.85f)

    Text(
        text = "%,d".format(counter.value.toInt()),
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        style = style,
        color = emphasized,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun lerpColor(start: Color, end: Color, fraction: Float): Color =
    Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction,
    )

/** Small pill used for XP rewards, types and status labels. */
@Composable
fun Tag(
    text: String,
    accent: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
) {
    val background = if (filled) accent else accent.copy(alpha = 0.14f)
    val foreground = if (filled) CodeQuestColors.OnAccent else accent
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(7.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            style = CodeQuestTypography.labelSmall,
            color = foreground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Five step difficulty indicator. */
@Composable
fun DifficultyDots(
    level: Int,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Box(
                Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (index < level) accent else CodeQuestColors.Track),
            )
            if (index < 4) Spacer(Modifier.width(4.dp))
        }
    }
}

/**
 * Restrained staggered entrance: fade + small upward translation, delayed by
 * [index] * CodeQuestMotion.staggerStep milliseconds (§27).
 */
@Composable
fun EntranceSection(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((index * CodeQuestMotion.staggerStep).toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(CodeQuestMotion.cardEntrance)) +
            slideInVertically(tween(CodeQuestMotion.cardEntrance)) { it / 5 },
        modifier = modifier,
    ) {
        content()
    }
}

/** Restrained horizontal shake for incorrect-answer feedback (§21). */
fun Modifier.shake(trigger: Boolean): Modifier = composed {
    if (!trigger) return@composed this
    val shift = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        shift.snapTo(0f)
        shift.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 340
                0f at 0
                (-7f) at 55
                7f at 110
                (-5f) at 165
                5f at 215
                (-3f) at 265
                0f at 340
            },
        )
    }
    val density = androidx.compose.ui.platform.LocalDensity.current
    val amplitude = with(density) { 7.dp.toPx() }
    graphicsLayer {
        translationX = (shift.value / 7f) * amplitude
    }
}
