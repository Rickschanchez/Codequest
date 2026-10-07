package com.codequest.app.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * CodeQuest core palette — an editorial, premium identity built on three
 * colours: charcoal `#1D1D1D`, accent orange `#F59115` and deep brown
 * `#46332E`. Everything else is carefully derived from these.
 *
 * Screens must never hardcode a hex value — they reference the semantic
 * tokens in [CodeQuestColors] instead.
 */
object CodeQuestPalette {
    // ---- Core identity (never changes between themes) ----

    /** Primary dark — the flagship background. */
    val Charcoal = Color(0xFF1D1D1D)

    /** Accent orange — the single interactive colour of the brand. */
    val Orange = Color(0xFFF59115)

    /** Deep brown — secondary atmospheric / material colour. */
    val Brown = Color(0xFF46332E)

    // ---- Dark theme surfaces ----

    val CharcoalDeep = Color(0xFF161616)
    val SurfaceDark = Color(0xFF242424)
    val SurfaceRaised = Color(0xFF2A2A2A)
    val TrackDark = Color(0xFF343434)

    // ---- Light theme surfaces (warm editorial paper) ----

    val Paper = Color(0xFFF6F1EA)
    val PaperSurface = Color(0xFFFCF9F5)
    val PaperRaised = Color(0xFFFFFFFF)
    val PaperMuted = Color(0xFFECE4DA)
    val TrackLight = Color(0xFFE4DBD0)

    // ---- Accent derivatives ----

    /** Pressed state of the CTA fill. */
    val OrangePressed = Color(0xFFCF7A0C)

    /** Deeper orange — keeps accent-as-text readable on paper. */
    val OrangeDeep = Color(0xFFB4700D)

    // ---- Brown derivatives ----

    /** Readable brown tint for dark-mode secondary accents. */
    val BrownTint = Color(0xFF9C7A6B)

    /** Soft brown surface used by secondary controls in light mode. */
    val BrownLight = Color(0xFFEFE6DE)

    // ---- Foregrounds ----

    val WarmWhite = Color(0xFFF4EFE9)
    val WarmGrayDark = Color(0xFFA8A19A)
    val WarmGrayLight = Color(0xFF6B615A)
    val MutedGrayDark = Color(0xFF8B847C)
    val MutedGrayLight = Color(0xFF7C736A)

    // ---- Semantic ----

    val SuccessDark = Color(0xFF8CA98A)
    val SuccessLight = Color(0xFF46684A)
    val ErrorDark = Color(0xFFD97F6E)
    val ErrorLight = Color(0xFF9A4A3A)
    val GoldDark = Color(0xFFD9A85F)
    val GoldLight = Color(0xFF96631B)
}

/**
 * Theme-aware semantic colour tokens.
 *
 * Every token interpolates between its intentional Dark and Light value using
 * a blend factor driven by [CodeQuestTheme], so switching themes is a smooth,
 * coordinated transition instead of a snap. Screens read these tokens during
 * composition and automatically recompose when the blend changes.
 *
 * The historic token names ([Cyan], [Violet], [Emerald], [Amber], [Rose]) are
 * retained as stable aliases so no screen had to be rewritten during the
 * editorial redesign — but their values now belong to the charcoal / orange /
 * brown system.
 */
object CodeQuestColors {
    private val blend = mutableStateOf(1f)

    /** 0f = light, 1f = dark. Driven from CodeQuestTheme. */
    internal fun setBlend(value: Float) {
        val clamped = value.coerceIn(0f, 1f)
        if (blend.value != clamped) blend.value = clamped
    }

    /** Set the initial blend without animation (called before first frame). */
    fun setBlendImmediate(value: Float) = setBlend(value)

    /** Current theme blend: 0f = light, 1f = dark. */
    internal val themeBlend: Float get() = blend.value

    private fun mix(dark: Color, light: Color): Color = lerp(light, dark, blend.value)

    private val p get() = CodeQuestPalette

    // ---- Surfaces (charcoal dominates, paper warms the light theme) ----
    val Background: Color get() = mix(p.Charcoal, p.Paper)
    val Surface: Color get() = mix(p.SurfaceDark, p.PaperSurface)
    val SurfaceMuted: Color get() = mix(p.CharcoalDeep, p.PaperMuted)
    val SurfaceElevated: Color get() = mix(p.SurfaceRaised, p.PaperRaised)
    val SurfaceBrown: Color get() = p.Brown
    val Outline: Color get() = mix(p.WarmWhite.copy(alpha = 0.12f), p.Charcoal.copy(alpha = 0.16f))
    val OutlineSoft: Color get() = mix(p.WarmWhite.copy(alpha = 0.07f), p.Charcoal.copy(alpha = 0.09f))

    // ---- Primary accent: orange ----
    /** Accent used for icons, text and fills — deepens slightly on paper. */
    val Accent: Color get() = mix(p.Orange, p.OrangeDeep)
    /** The exact CTA fill — `#F59115` in both themes, always with dark text. */
    val AccentFill: Color get() = p.Orange
    val AccentFillPressed: Color get() = p.OrangePressed
    /** Pressed / active accent. */
    val AccentPressed: Color get() = mix(p.OrangePressed, p.OrangeDeep)
    /** Restrained low-opacity orange wash. */
    val AccentSoft: Color get() = Accent.copy(alpha = 0.12f)

    // ---- Secondary accent: brown ----
    /** Brown as an accent/text colour — tinted on charcoal, deep on paper. */
    val Brown: Color get() = mix(p.BrownTint, p.Brown)

    // ---- Semantic roles (historic aliases) ----
    /** Primary accent — buttons, bars, selected states, labels. */
    val Cyan: Color get() = Accent
    /** Primary accent as a fill. */
    val CyanDeep: Color get() = mix(p.OrangePressed, p.Orange)
    /** Supporting accent — brown family. */
    val Violet: Color get() = Brown
    /** Success / completed / correct. Restrained compatible green. */
    val Emerald: Color get() = mix(p.SuccessDark, p.SuccessLight)
    /** Rewards, XP and highlights — orange, the brand's reward colour. */
    val Amber: Color get() = Accent
    /** Streak / warm emphasis. */
    val Orange: Color get() = Accent
    /** Medium difficulty — warm gold, deliberately distinct from the accent. */
    val Gold: Color get() = mix(p.GoldDark, p.GoldLight)
    /** Error / hard difficulty / incorrect feedback. Restrained warm red. */
    val Rose: Color get() = mix(p.ErrorDark, p.ErrorLight)

    /** Semantic success alias. */
    val Success: Color get() = Emerald
    /** Semantic error alias. */
    val Error: Color get() = Rose

    // ---- Text ----
    val TextPrimary: Color get() = mix(p.WarmWhite, p.Charcoal)
    val TextSecondary: Color get() = mix(p.WarmGrayDark, p.WarmGrayLight)
    val TextTertiary: Color get() = mix(p.MutedGrayDark, p.MutedGrayLight)

    /** Foreground on top of any accent fill (orange CTAs, tags, badges). */
    val OnAccent: Color get() = p.Charcoal

    /** Muted track behind every progress bar. */
    val Track: Color get() = mix(p.TrackDark, p.TrackLight)
}
