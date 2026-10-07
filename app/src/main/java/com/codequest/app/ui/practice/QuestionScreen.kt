package com.codequest.app.ui.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.domain.practice.AdaptiveResult
import com.codequest.app.domain.practice.LevelDirection
import com.codequest.app.ui.components.GlassButton
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.components.SectionHeader
import com.codequest.app.ui.components.Tag
import com.codequest.app.ui.components.difficultyColor
import com.codequest.app.ui.components.shake
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestMotion
import com.codequest.app.ui.theme.CodeQuestTypography

@Composable
fun QuestionScreen(
    questionId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuestionViewModel = viewModel(),
) {
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val solvedIds by viewModel.solvedQuestionIds.collectAsStateWithLifecycle()
    val levelChange by viewModel.levelChange.collectAsStateWithLifecycle()

    val question = remember(questions, questionId) {
        questions.firstOrNull { it.id == questionId }
    } ?: return
    val topicName = remember(topics, question.topicId) {
        topics.firstOrNull { it.id == question.topicId }?.name ?: "DSA"
    }
    val indexInTopic = remember(questions, question.topicId, question.id) {
        questions
            .filter { it.topicId == question.topicId }
            .indexOfFirst { it.id == question.id } + 1
    }

    var selectedIndex by rememberSaveable(questionId) { mutableIntStateOf(-1) }
    var revealed by rememberSaveable(questionId) { mutableStateOf(false) }
    var wasCorrect by rememberSaveable(questionId) { mutableStateOf(false) }
    var xpAwarded by rememberSaveable(questionId) { mutableStateOf(false) }

    val accent = difficultyColor(question.difficulty)
    val letters = listOf("A", "B", "C", "D", "E", "F")

    fun submit() {
        if (selectedIndex < 0 || revealed) return
        val alreadySolved = question.id in solvedIds
        val correct = viewModel.submitAnswer(question.id, selectedIndex)
        wasCorrect = correct
        xpAwarded = correct && !alreadySolved
        revealed = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CodeQuestColors.SurfaceMuted),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CodeQuestColors.TextPrimary,
                    modifier = Modifier.size(19.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topicName.uppercase(),
                    style = CodeQuestTypography.titleMedium,
                    color = CodeQuestColors.TextPrimary,
                    letterSpacing = 0.6.sp,
                )
                Text(
                    text = "QUESTION ${indexInTopic.toString().padStart(2, '0')}  ·  ${question.type.label.uppercase()}",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.TextTertiary,
                    letterSpacing = 0.6.sp,
                )
            }
            Tag(text = question.difficulty.label.uppercase(), accent = accent, filled = true)
        }

        GlowCard(accent = CodeQuestColors.Accent) {
            Text(
                text = question.question,
                style = CodeQuestTypography.titleLarge,
                color = CodeQuestColors.TextPrimary,
            )

            if (question.code != null) {
                Spacer(Modifier.height(12.dp))
                CodeBlock(text = question.code, accent = CodeQuestColors.Accent)
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Tag(text = "+${question.xpReward} XP", accent = CodeQuestColors.Accent)
                if (question.id in solvedIds) {
                    Spacer(Modifier.width(8.dp))
                    Tag(text = "Solved", accent = CodeQuestColors.Emerald)
                }
            }
        }

        Column(
            modifier = Modifier.shake(trigger = revealed && !wasCorrect),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            question.options.forEachIndexed { index, option ->
                val state = optionState(
                    index = index,
                    selectedIndex = selectedIndex,
                    correctIndex = question.correctIndex,
                    revealed = revealed,
                )
                OptionRow(
                    letter = letters.getOrElse(index) { "?" },
                    text = option,
                    state = state,
                    enabled = !revealed,
                    onClick = { selectedIndex = index },
                )
            }
        }

        AnimatedVisibility(
            visible = revealed,
            enter = fadeIn(tween(260)) +
                scaleIn(
                    animationSpec = CodeQuestMotion.softSpring,
                    initialScale = 0.96f,
                ),
        ) {
            FeedbackCard(
                correct = wasCorrect,
                correctAnswer = question.correctAnswer,
                explanation = question.explanation,
                xpAwarded = xpAwarded,
                xpAmount = question.xpReward,
            )
        }

        if (!revealed) {
            GlassButton(
                onClick = ::submit,
                enabled = selectedIndex >= 0,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 13.dp, horizontal = 18.dp),
            ) {
                Text(
                    text = "CHECK ANSWER",
                    style = CodeQuestTypography.labelLarge,
                )
            }
        } else {
            GlassButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 13.dp, horizontal = 18.dp),
            ) {
                Text(
                    text = "CONTINUE",
                    style = CodeQuestTypography.labelLarge,
                )
            }
        }

        Spacer(Modifier.height(4.dp))
    }

    // The engine's verdict surfaces once its window of answers is judged.
    levelChange?.let { verdict ->
        LevelChangeDialog(
            verdict = verdict,
            onDismiss = viewModel::dismissLevelChange,
        )
    }
}

/** Announces a level move decided by the adaptive engine. */
@Composable
private fun LevelChangeDialog(
    verdict: AdaptiveResult,
    onDismiss: () -> Unit,
) {
    val promoted = verdict.direction == LevelDirection.UP
    val accent = if (promoted) CodeQuestColors.Emerald else CodeQuestColors.Gold

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CodeQuestColors.SurfaceElevated,
        titleContentColor = CodeQuestColors.TextPrimary,
        textContentColor = CodeQuestColors.TextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = if (promoted) "LEVEL UP" else "LEVEL ADJUSTED",
                style = CodeQuestTypography.titleLarge,
                color = accent,
                letterSpacing = 1.sp,
            )
        },
        text = {
            Column {
                Text(
                    text = verdict.message,
                    style = CodeQuestTypography.titleMedium,
                    color = CodeQuestColors.TextPrimary,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Level ${verdict.fromLevel} → ${verdict.toLevel} · " +
                        "${(verdict.accuracy * 100).toInt()}% correct over the last " +
                        "${verdict.answered} answers.",
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextSecondary,
                )
            }
        },
        confirmButton = {
            GlassButton(onClick = onDismiss) {
                Text(text = "GOT IT", style = CodeQuestTypography.labelLarge)
            }
        },
    )
}

private enum class OptionState { NORMAL, SELECTED, CORRECT, WRONG, DIM }

private fun optionState(
    index: Int,
    selectedIndex: Int,
    correctIndex: Int,
    revealed: Boolean,
): OptionState = when {
    !revealed && index == selectedIndex -> OptionState.SELECTED
    !revealed -> OptionState.NORMAL
    index == correctIndex -> OptionState.CORRECT
    index == selectedIndex -> OptionState.WRONG
    else -> OptionState.DIM
}

@Composable
private fun OptionRow(
    letter: String,
    text: String,
    state: OptionState,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    val background by animateColorAsState(
        targetValue = when (state) {
            OptionState.SELECTED -> CodeQuestColors.Cyan.copy(alpha = 0.10f)
            OptionState.CORRECT -> CodeQuestColors.Emerald.copy(alpha = 0.12f)
            OptionState.WRONG -> CodeQuestColors.Rose.copy(alpha = 0.10f)
            OptionState.NORMAL -> CodeQuestColors.SurfaceMuted
            OptionState.DIM -> CodeQuestColors.Surface.copy(alpha = 0.6f)
        },
        animationSpec = tween(240),
        label = "optionBackground",
    )
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.SELECTED -> CodeQuestColors.Cyan.copy(alpha = 0.55f)
            OptionState.CORRECT -> CodeQuestColors.Emerald
            OptionState.WRONG -> CodeQuestColors.Rose
            OptionState.NORMAL -> CodeQuestColors.OutlineSoft
            OptionState.DIM -> CodeQuestColors.OutlineSoft.copy(alpha = 0.5f)
        },
        animationSpec = tween(240),
        label = "optionBorder",
    )
    val contentColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.DIM -> CodeQuestColors.TextTertiary
            OptionState.CORRECT -> CodeQuestColors.Emerald
            OptionState.WRONG -> CodeQuestColors.Rose
            else -> CodeQuestColors.TextPrimary
        },
        animationSpec = tween(240),
        label = "optionContent",
    )
    val badgeColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.SELECTED -> CodeQuestColors.Cyan
            OptionState.CORRECT -> CodeQuestColors.Emerald
            OptionState.WRONG -> CodeQuestColors.Rose
            else -> CodeQuestColors.Track
        },
        animationSpec = tween(240),
        label = "optionBadge",
    )
    val badgeTextColor = if (state == OptionState.NORMAL || state == OptionState.DIM) {
        CodeQuestColors.TextSecondary
    } else {
        CodeQuestColors.OnAccent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .border(1.dp, borderColor, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(badgeColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = letter,
                style = CodeQuestTypography.labelLarge,
                color = badgeTextColor,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = CodeQuestTypography.bodyLarge,
            color = contentColor,
            modifier = Modifier.weight(1f),
        )
        if (state == OptionState.CORRECT || state == OptionState.WRONG) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = if (state == OptionState.CORRECT) {
                    Icons.Filled.Check
                } else {
                    Icons.Filled.Close
                },
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun FeedbackCard(
    correct: Boolean,
    correctAnswer: String,
    explanation: String,
    xpAwarded: Boolean,
    xpAmount: Int,
) {
    val accent = if (correct) CodeQuestColors.Emerald else CodeQuestColors.Rose

    GlowCard(accent = accent) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (correct) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (correct) "CORRECT" else "NOT QUITE",
                    style = CodeQuestTypography.titleMedium,
                    color = CodeQuestColors.TextPrimary,
                    letterSpacing = 0.5.sp,
                )
                Text(
                    text = when {
                        !correct -> "Answer: $correctAnswer"
                        xpAwarded -> "+$xpAmount XP awarded"
                        else -> "Solved earlier — no extra XP"
                    },
                    style = CodeQuestTypography.bodySmall,
                    color = accent,
                )
            }
        }

        Spacer(Modifier.height(13.dp))

        SectionHeader(title = "WHY?", accent = accent)
        Spacer(Modifier.height(8.dp))
        Text(
            text = explanation,
            style = CodeQuestTypography.bodyMedium,
            color = CodeQuestColors.TextSecondary,
        )
    }
}

@Composable
private fun CodeBlock(text: String, accent: Color) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CodeQuestColors.SurfaceMuted)
            .border(1.dp, accent.copy(alpha = 0.18f), shape)
            .padding(12.dp),
    ) {
        Text(
            text = "CODE",
            style = CodeQuestTypography.labelSmall,
            color = accent,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = text,
            style = CodeQuestTypography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            color = CodeQuestColors.TextPrimary,
        )
    }
}
