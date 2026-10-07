package com.codequest.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.codequest.app.navigation.CodeQuestDestination
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestGlass
import com.codequest.app.ui.theme.CodeQuestMotion
import com.codequest.app.ui.theme.CodeQuestPalette
import com.codequest.app.ui.theme.CodeQuestShapes
import com.codequest.app.ui.theme.CodeQuestSpacing
import com.codequest.app.ui.theme.CodeQuestTypography
import com.codequest.app.ui.theme.ThemeState

/**
 * Editorial content block: solid dark surface, subtle neutral border, soft
 * shadow and strong internal spacing. [emphasis] opts a card into an accent
 * border for featured moments (hero cards, CTAs).
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accent: Color = CodeQuestColors.Accent,
    emphasis: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(CodeQuestSpacing.xl),
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(CodeQuestShapes.card)
    Box(
        modifier = modifier
            .shadow(
                elevation = 7.dp,
                shape = shape,
                ambientColor = CodeQuestGlass.shadow,
                spotColor = CodeQuestGlass.shadow,
            )
            .clip(shape)
            .background(CodeQuestGlass.cardFill)
            .border(
                width = 1.dp,
                color = if (emphasis) accent.copy(alpha = 0.45f) else CodeQuestGlass.border,
                shape = shape,
            ),
    ) {
        Column(Modifier.padding(contentPadding), content = content)
    }
}

/** Button variants: primary = accent orange CTA, secondary = brown surface. */
enum class GlassButtonVariant { PRIMARY, SECONDARY }

/**
 * The CodeQuest button.
 *
 * Primary — solid `#F59115` fill with charcoal label (the reference CTA).
 * Secondary — deep brown surface, subtle border, light label.
 * Press → slightly darker fill, small press-scale · Release → smooth return.
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: GlassButtonVariant = GlassButtonVariant.PRIMARY,
    accent: Color = CodeQuestColors.Accent,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(CodeQuestShapes.button)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) CodeQuestMotion.pressedScale else 1f,
        animationSpec = CodeQuestMotion.softSpring,
        label = "buttonScale",
    )
    val elevation by animateFloatAsState(
        targetValue = if (pressed && enabled) 3f else 7f,
        animationSpec = CodeQuestMotion.smooth(180),
        label = "buttonElevation",
    )

    val restingFill = when {
        !enabled -> CodeQuestColors.SurfaceMuted
        variant == GlassButtonVariant.PRIMARY -> CodeQuestGlass.primaryFill
        else -> CodeQuestGlass.secondaryFill
    }
    val fill by animateColorAsState(
        targetValue = when {
            !enabled -> restingFill
            pressed && variant == GlassButtonVariant.PRIMARY -> CodeQuestGlass.primaryFillPressed
            pressed -> restingFill.copy(alpha = (restingFill.alpha + 0.10f).coerceAtMost(1f))
            else -> restingFill
        },
        animationSpec = CodeQuestMotion.smooth(180),
        label = "buttonFill",
    )
    val border by animateColorAsState(
        targetValue = when {
            !enabled -> CodeQuestColors.OutlineSoft
            variant == GlassButtonVariant.PRIMARY -> CodeQuestColors.OnAccent.copy(alpha = 0.20f)
            else -> CodeQuestColors.Outline
        },
        animationSpec = CodeQuestMotion.smooth(180),
        label = "buttonBorder",
    )
    val contentColor = when {
        !enabled -> CodeQuestColors.TextTertiary
        variant == GlassButtonVariant.PRIMARY -> CodeQuestColors.OnAccent
        else -> CodeQuestColors.TextPrimary
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Row(
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = elevation.dp,
                    shape = shape,
                    ambientColor = CodeQuestGlass.shadow,
                    spotColor = CodeQuestGlass.shadow,
                )
                .clip(shape)
                .background(fill)
                .border(1.dp, border, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                )
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

/** Restrained filter chip with an animated accent selected state. */
@Composable
fun GlassChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = CodeQuestColors.Accent,
) {
    val shape = RoundedCornerShape(CodeQuestShapes.chip)
    val background by animateColorAsState(
        targetValue = if (selected) accent.copy(alpha = 0.16f) else CodeQuestColors.SurfaceMuted,
        animationSpec = tween(200),
        label = "chipBackground",
    )
    val border by animateColorAsState(
        targetValue = if (selected) accent.copy(alpha = 0.65f) else CodeQuestColors.OutlineSoft,
        animationSpec = tween(200),
        label = "chipBorder",
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) accent else CodeQuestColors.TextSecondary,
        animationSpec = tween(200),
        label = "chipText",
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(1.dp, border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
    ) {
        Text(
            text = text,
            style = CodeQuestTypography.labelLarge,
            color = textColor,
            maxLines = 1,
        )
    }
}

/**
 * Progress card: label, subtitle, animated percentage and progress bar —
 * an editorial data block rather than a Material card.
 */
@Composable
fun GlassProgressCard(
    title: String,
    value: String,
    progress: Float,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    accent: Color = CodeQuestColors.Accent,
    endAccent: Color = accent,
    emphasis: Boolean = false,
    footer: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        accent = accent,
        emphasis = emphasis,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = CodeQuestTypography.labelMedium,
                    color = accent,
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = subtitle,
                        style = CodeQuestTypography.titleSmall,
                        color = CodeQuestColors.TextPrimary,
                    )
                }
            }
            Text(
                text = value,
                style = CodeQuestTypography.headlineSmall,
                color = accent,
            )
        }

        Spacer(Modifier.height(12.dp))

        NeonProgressBar(
            progress = progress,
            height = 6.dp,
            startColor = accent,
            endColor = endAccent,
        )

        if (footer != null) {
            Spacer(Modifier.height(12.dp))
            footer()
        }
    }
}

/**
 * Custom compact Light/Dark control for the Home header.
 *
 * Dark mode: [ ☾          ● ]  ·  Light mode: [ ●          ☀ ]
 * Thumb slides, icons crossfade and subtly rotate — all coordinated with the
 * app theme change. No Android Switch, no bounce, no particles.
 */
@Composable
fun ThemeToggle(
    modifier: Modifier = Modifier,
) {
    val isDark = ThemeState.resolvedIsDark()
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val trackWidth = 66.dp
    val trackHeight = 34.dp
    val thumbSize = 26.dp
    val inset = 4.dp

    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = CodeQuestMotion.smooth(180),
        label = "themeTogglePress",
    )
    val thumbOffset by animateFloatAsState(
        targetValue = if (isDark) {
            (trackWidth - thumbSize - inset).value
        } else {
            inset.value
        },
        animationSpec = CodeQuestMotion.smooth(CodeQuestMotion.toggleDuration),
        label = "themeToggleThumb",
    )
    val trackFill by animateColorAsState(
        targetValue = CodeQuestGlass.secondaryFill,
        animationSpec = CodeQuestMotion.smooth(CodeQuestMotion.toggleDuration),
        label = "themeToggleTrack",
    )
    val trackBorder by animateColorAsState(
        targetValue = if (isDark) {
            CodeQuestPalette.Orange.copy(alpha = 0.5f)
        } else {
            CodeQuestColors.Outline
        },
        animationSpec = CodeQuestMotion.smooth(CodeQuestMotion.toggleDuration),
        label = "themeToggleBorder",
    )
    val thumbColor by animateColorAsState(
        targetValue = if (isDark) CodeQuestPalette.Orange else CodeQuestPalette.Charcoal,
        animationSpec = CodeQuestMotion.smooth(CodeQuestMotion.toggleDuration),
        label = "themeToggleThumbColor",
    )
    val moonAlpha by animateFloatAsState(
        targetValue = if (isDark) 1f else 0f,
        animationSpec = CodeQuestMotion.smooth(CodeQuestMotion.toggleDuration),
        label = "themeToggleMoonAlpha",
    )
    val moonRotation by animateFloatAsState(
        targetValue = if (isDark) 0f else -28f,
        animationSpec = CodeQuestMotion.smooth(CodeQuestMotion.toggleDuration),
        label = "themeToggleMoonRotation",
    )
    val sunAlpha by animateFloatAsState(
        targetValue = if (isDark) 0f else 1f,
        animationSpec = CodeQuestMotion.smooth(CodeQuestMotion.toggleDuration),
        label = "themeToggleSunAlpha",
    )
    val sunRotation by animateFloatAsState(
        targetValue = if (isDark) 28f else 0f,
        animationSpec = CodeQuestMotion.smooth(CodeQuestMotion.toggleDuration),
        label = "themeToggleSunRotation",
    )

    val shape = RoundedCornerShape(CodeQuestShapes.pill)
    val description = if (isDark) "Switch to light mode" else "Switch to dark mode"

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .size(width = trackWidth, height = trackHeight)
            .shadow(
                elevation = 5.dp,
                shape = shape,
                ambientColor = CodeQuestGlass.shadow,
                spotColor = CodeQuestGlass.shadow,
            )
            .clip(shape)
            .background(trackFill)
            .border(1.dp, trackBorder, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { ThemeState.toggle(isDark) },
            )
            .semantics { contentDescription = description },
        contentAlignment = Alignment.CenterStart,
    ) {
        Icon(
            imageVector = Icons.Filled.DarkMode,
            contentDescription = null,
            tint = CodeQuestPalette.WarmWhite,
            modifier = Modifier
                .offset(x = 9.dp)
                .size(15.dp)
                .graphicsLayer {
                    alpha = moonAlpha
                    rotationZ = moonRotation
                },
        )
        Icon(
            imageVector = Icons.Filled.LightMode,
            contentDescription = null,
            tint = CodeQuestPalette.Charcoal,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-9).dp)
                .size(15.dp)
                .graphicsLayer {
                    alpha = sunAlpha
                    rotationZ = sunRotation
                },
        )
        Box(
            modifier = Modifier
                .offset(x = thumbOffset.dp)
                .size(thumbSize)
                .clip(CircleShape)
                .background(thumbColor),
        )
    }
}

/**
 * Floating anchored bottom navigation: solid charcoal pill, soft shadow,
 * Manrope labels and a smoothly animated orange active indicator.
 */
@Composable
fun GlassBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = CodeQuestColors.Accent
    val shape = RoundedCornerShape(CodeQuestShapes.bottomBar)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 14.dp,
                    shape = shape,
                    ambientColor = CodeQuestGlass.shadow,
                    spotColor = CodeQuestGlass.shadow,
                )
                .clip(shape)
                .background(CodeQuestColors.SurfaceElevated)
                .border(1.dp, CodeQuestGlass.border, shape)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CodeQuestDestination.entries.forEach { destination ->
                val selected = currentRoute == destination.route ||
                    currentRoute?.startsWith("${destination.route}?") == true
                GlassNavItems(
                    destination = destination,
                    selected = selected,
                    accent = accent,
                    onClick = { onNavigate(destination.route) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun GlassNavItems(
    destination: CodeQuestDestination,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val indicator by animateColorAsState(
        targetValue = if (selected) accent.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(CodeQuestMotion.screenDuration),
        label = "navIndicator",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.94f,
        animationSpec = CodeQuestMotion.smooth(CodeQuestMotion.screenDuration),
        label = "navIconScale",
    )
    val iconColor by animateColorAsState(
        targetValue = if (selected) accent else CodeQuestColors.TextTertiary,
        animationSpec = tween(CodeQuestMotion.screenDuration),
        label = "navIconColor",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) CodeQuestColors.TextPrimary else CodeQuestColors.TextTertiary,
        animationSpec = tween(CodeQuestMotion.screenDuration),
        label = "navLabelColor",
    )
    val itemShape = RoundedCornerShape(CodeQuestShapes.cardSmall)

    Box(
        modifier = modifier
            .clip(itemShape)
            .background(indicator)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.label,
                tint = iconColor,
                modifier = Modifier
                    .size(21.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    },
            )
            Text(
                text = destination.label,
                style = if (selected) {
                    CodeQuestTypography.labelSmall.copy(fontWeight = FontWeight.Bold)
                } else {
                    CodeQuestTypography.labelSmall
                },
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
