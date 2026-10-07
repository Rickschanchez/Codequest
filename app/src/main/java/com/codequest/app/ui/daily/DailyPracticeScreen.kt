package com.codequest.app.ui.daily

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.ui.components.GlassButton
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.components.NeonProgressBar
import com.codequest.app.ui.components.SectionHeader
import com.codequest.app.ui.components.Tag
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestShapes
import com.codequest.app.ui.theme.CodeQuestTypography

/**
 * Today's practice set: ten questions picked deterministically from the
 * learner's current level, pinned for the whole calendar day.
 */
@Composable
fun DailyPracticeScreen(
    onBack: () -> Unit,
    onOpenQuestion: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DailyPracticeViewModel = viewModel(),
) {
    val solvedIds by viewModel.solvedQuestionIds.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()

    val questions = viewModel.questions
    val level = profile.levelFor("dsa")
    val solvedCount = questions.count { it.id in solvedIds }
    val isComplete = questions.isNotEmpty() && solvedCount == questions.size
    val nextId = viewModel.nextQuestion(solvedIds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(CodeQuestShapes.iconBox))
                    .background(CodeQuestColors.SurfaceMuted)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CodeQuestColors.TextPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(13.dp))
            Column {
                Text(
                    text = "DAILY PRACTICE",
                    style = CodeQuestTypography.headlineMedium,
                    color = CodeQuestColors.TextPrimary,
                )
                Text(
                    text = "${viewModel.dayLabel} · resets overnight",
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextSecondary,
                )
            }
        }

        GlowCard(accent = CodeQuestColors.Accent, emphasis = true) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "TODAY'S SET",
                        style = CodeQuestTypography.labelMedium,
                        color = CodeQuestColors.Accent,
                        letterSpacing = 1.sp,
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = "$solvedCount of ${questions.size} solved",
                        style = CodeQuestTypography.headlineSmall,
                        color = CodeQuestColors.TextPrimary,
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = "Level $level questions from across every roadmap topic.",
                        style = CodeQuestTypography.bodySmall,
                        color = CodeQuestColors.TextSecondary,
                    )
                }
                Tag(
                    text = "LEVEL ${level.toString().padStart(2, '0')}",
                    accent = CodeQuestColors.Accent,
                    filled = true,
                )
            }

            Spacer(Modifier.height(13.dp))

            NeonProgressBar(
                progress = if (questions.isEmpty()) 0f else solvedCount.toFloat() / questions.size,
                height = 7.dp,
                startColor = CodeQuestColors.Accent,
                endColor = CodeQuestColors.Accent,
            )
        }

        GlassButton(
            onClick = { nextId?.let(onOpenQuestion) },
            enabled = nextId != null && !isComplete,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = when {
                    isComplete -> "SET COMPLETE · BACK TOMORROW"
                    solvedCount == 0 -> "START TODAY'S SET"
                    else -> "CONTINUE TODAY'S SET"
                },
                style = CodeQuestTypography.labelLarge,
            )
            if (!isComplete) {
                Spacer(Modifier.width(9.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                )
            }
        }

        SectionHeader(
            title = "Your set",
            trailing = {
                Text(
                    text = "${questions.size} questions",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            },
        )

        questions.forEachIndexed { index, question ->
            val isSolved = question.id in solvedIds
            DailyQuestionRow(
                index = index + 1,
                question = question,
                topicName = topics.firstOrNull { it.id == question.topicId }?.name ?: "DSA",
                solved = isSolved,
                onClick = { onOpenQuestion(question.id) },
            )
        }

        Text(
            text = "The same ten questions stay pinned until midnight — " +
                "your level decides who they are, the date decides when.",
            style = CodeQuestTypography.bodySmall,
            color = CodeQuestColors.TextTertiary,
        )
    }
}

@Composable
private fun DailyQuestionRow(
    index: Int,
    question: PracticeQuestion,
    topicName: String,
    solved: Boolean,
    onClick: () -> Unit,
) {
    GlowCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        accent = if (solved) CodeQuestColors.Emerald else CodeQuestColors.Outline,
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        if (solved) {
                            CodeQuestColors.Emerald.copy(alpha = 0.16f)
                        } else {
                            CodeQuestColors.AccentSoft
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (solved) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Solved",
                        tint = CodeQuestColors.Emerald,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Text(
                        text = index.toString(),
                        style = CodeQuestTypography.labelMedium,
                        color = CodeQuestColors.Accent,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = topicName.uppercase(),
                        style = CodeQuestTypography.labelSmall,
                        color = CodeQuestColors.TextSecondary,
                        letterSpacing = 0.8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(8.dp))
                    Tag(
                        text = "L${question.level}",
                        accent = if (solved) CodeQuestColors.Emerald else CodeQuestColors.Gold,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = question.question,
                    style = CodeQuestTypography.bodyMedium,
                    color = CodeQuestColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = CodeQuestColors.TextTertiary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
