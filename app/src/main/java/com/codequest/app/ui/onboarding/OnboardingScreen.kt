package com.codequest.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.app.data.questions.QuestionBank
import com.codequest.app.domain.model.KnowledgeBand
import com.codequest.app.domain.model.Subjects
import com.codequest.app.ui.components.GlassButton
import com.codequest.app.ui.components.GlassChip
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.components.Tag
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestTypography

private const val STEP_SUBJECTS = 1
private const val STEP_ASSESSMENT = 2

/**
 * Two-step personalisation gate: pick the subjects, name the starting level.
 *
 * Shown before the main scaffold whenever onboarding has not completed, and
 * never again once it has — the persisted profile is the proof it ran.
 */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = viewModel(),
) {
    var step by rememberSaveable { mutableStateOf(STEP_SUBJECTS) }
    val selected = viewModel.selectedSubjects
    val band = viewModel.band

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.widthIn(max = 540.dp).fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Tag(text = "CODEQUEST", accent = CodeQuestColors.Accent, filled = true)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "STEP $step OF $STEP_ASSESSMENT",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.TextSecondary,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${selected.size} SELECTED",
                    style = CodeQuestTypography.labelMedium,
                    color = if (selected.isEmpty()) {
                        CodeQuestColors.TextTertiary
                    } else {
                        CodeQuestColors.Accent
                    },
                    letterSpacing = 1.sp,
                )
            }

            Spacer(Modifier.height(30.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    fadeIn(tween(240)) togetherWith fadeOut(tween(140))
                },
                label = "onboardingStep",
            ) { current ->
                when (current) {
                    STEP_SUBJECTS -> SubjectsStep(
                        selected = selected,
                        onToggle = viewModel::toggleSubject,
                    )
                    else -> AssessmentStep(
                        band = band,
                        onChoose = viewModel::chooseBand,
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            GlassButton(
                onClick = {
                    if (step == STEP_SUBJECTS) {
                        if (viewModel.canContinue) step = STEP_ASSESSMENT
                    } else {
                        viewModel.startPath()
                    }
                },
                enabled = if (step == STEP_SUBJECTS) viewModel.canContinue else viewModel.canFinish,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (step == STEP_SUBJECTS) "CONTINUE" else "BUILD MY PATH",
                    style = CodeQuestTypography.labelLarge,
                )
                Spacer(Modifier.width(9.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                )
            }

            if (step == STEP_ASSESSMENT) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { step = STEP_SUBJECTS }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = CodeQuestColors.TextSecondary,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = "CHANGE MY SUBJECTS",
                        style = CodeQuestTypography.labelMedium,
                        color = CodeQuestColors.TextSecondary,
                        letterSpacing = 1.sp,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubjectsStep(
    selected: List<String>,
    onToggle: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(
            text = "What do you want to master?",
            style = CodeQuestTypography.headlineMedium,
            color = CodeQuestColors.TextPrimary,
        )
        Text(
            text = "Choose every subject you want on your path. Nothing is locked " +
                "later — this only decides what your roadmap and levels track.",
            style = CodeQuestTypography.bodyMedium,
            color = CodeQuestColors.TextSecondary,
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Subjects.all.forEach { subject ->
                GlassChip(
                    text = "${subject.emoji}  ${subject.name}",
                    selected = subject.id in selected,
                    onClick = { onToggle(subject.id) },
                )
            }
        }

        GlowCard(accent = CodeQuestColors.Emerald, emphasis = true) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "DSA ROADMAP",
                        style = CodeQuestTypography.labelMedium,
                        color = CodeQuestColors.Emerald,
                        letterSpacing = 1.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${QuestionBank.topicOrder.size} topics · 10 levels each · " +
                            "${QuestionBank.all.size} questions",
                        style = CodeQuestTypography.titleSmall,
                        color = CodeQuestColors.TextPrimary,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "From Big-O foundations to dynamic programming mastery.",
                        style = CodeQuestTypography.bodySmall,
                        color = CodeQuestColors.TextTertiary,
                    )
                }
                Tag(text = "INCLUDED", accent = CodeQuestColors.Emerald, filled = true)
            }
        }
    }
}

@Composable
private fun AssessmentStep(
    band: KnowledgeBand?,
    onChoose: (KnowledgeBand) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "How much do you already know?",
            style = CodeQuestTypography.headlineMedium,
            color = CodeQuestColors.TextPrimary,
        )
        Text(
            text = "Be honest — the path starts where you actually are, and adapts " +
                "every ten answers from there.",
            style = CodeQuestTypography.bodyMedium,
            color = CodeQuestColors.TextSecondary,
        )

        Spacer(Modifier.height(4.dp))

        KnowledgeBand.entries.forEach { option ->
            val isSelected = option == band
            GlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChoose(option) },
                accent = if (isSelected) CodeQuestColors.Accent else CodeQuestColors.Outline,
                emphasis = isSelected,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = option.emoji, fontSize = 26.sp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = option.title,
                            style = CodeQuestTypography.titleMedium,
                            color = CodeQuestColors.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = option.subtitle,
                            style = CodeQuestTypography.bodySmall,
                            color = CodeQuestColors.TextSecondary,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = CodeQuestColors.Accent,
                            modifier = Modifier.size(20.dp),
                        )
                    } else {
                        Tag(
                            text = "LEVEL ${option.level}",
                            accent = CodeQuestColors.Outline,
                        )
                    }
                }
            }
        }

        if (band != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Every selected subject starts at Level ${band.level}. " +
                    "Your first daily set is drawn from that level.",
                style = CodeQuestTypography.bodySmall,
                color = CodeQuestColors.Accent,
            )
        }
    }
}
