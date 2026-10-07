package com.codequest.app.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.app.domain.model.CodingChallenge
import com.codequest.app.domain.model.Difficulty
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.domain.model.QuestionType
import com.codequest.app.ui.components.DifficultyDots
import com.codequest.app.ui.components.GlassChip
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.components.NeonProgressBar
import com.codequest.app.ui.components.SectionHeader
import com.codequest.app.ui.components.Tag
import com.codequest.app.ui.components.difficultyColor
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestShapes
import com.codequest.app.ui.theme.CodeQuestTypography

@Composable
fun PracticeScreen(
    topicFilter: String?,
    onOpenQuestion: (String) -> Unit,
    onOpenDaily: () -> Unit,
    onOpenChallenge: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PracticeViewModel = viewModel(),
) {
    LaunchedEffect(topicFilter) {
        viewModel.setTopicFilter(topicFilter.orEmpty())
    }

    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val solvedIds by viewModel.solvedQuestionIds.collectAsStateWithLifecycle()
    val activeTopicId by viewModel.topicFilter.collectAsStateWithLifecycle()
    val activeDifficulty by viewModel.difficultyFilter.collectAsStateWithLifecycle()
    val activeStatus by viewModel.statusFilter.collectAsStateWithLifecycle()
    val profile by viewModel.learningProfile.collectAsStateWithLifecycle()
    val codingChallenges by viewModel.codingChallenges.collectAsStateWithLifecycle()

    // Derived once per input change — not on every recomposition.
    val askableTopics = remember(topics) { topics.filter { it.questionCount > 0 } }
    val visible = remember(questions, activeTopicId, activeDifficulty, activeStatus, solvedIds) {
        questions.filter { question ->
            (activeTopicId.isEmpty() || question.topicId == activeTopicId) &&
                (activeDifficulty == null || question.difficulty == activeDifficulty) &&
                (activeStatus == null || (question.id in solvedIds) == activeStatus)
        }
    }
    val summaryScope = remember(questions, activeTopicId) {
        questions.filter { activeTopicId.isEmpty() || it.topicId == activeTopicId }
    }
    val summaryTitle = remember(topics, activeTopicId) {
        topics.firstOrNull { it.id == activeTopicId }?.name ?: "All topics"
    }
    val topicNames = remember(topics) { topics.associate { it.id to it.name } }
    val dailySolved = remember(viewModel.dailyQuestions, solvedIds) {
        viewModel.dailyQuestions.count { it.id in solvedIds }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = "PRACTICE",
                    style = CodeQuestTypography.headlineMedium,
                    color = CodeQuestColors.TextPrimary,
                )
                Text(
                    text = "Sharpen your DSA skills.",
                    style = CodeQuestTypography.bodyMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            }
        }

        item(key = "scoreboard") {
            DifficultyScoreboard(
                title = summaryTitle,
                questions = summaryScope,
                solvedIds = solvedIds,
            )
        }

        item(key = "daily") {
            DailySetBanner(
                solved = dailySolved,
                total = viewModel.dailyQuestions.size,
                level = profile.levelFor("dsa"),
                onClick = onOpenDaily,
            )
        }

        item(key = "coding-header") {
            SectionHeader(
                title = "Coding",
                trailing = {
                    Text(
                        text = "levels 6-10",
                        style = CodeQuestTypography.labelMedium,
                        color = CodeQuestColors.TextSecondary,
                    )
                },
            )
        }
        items(codingChallenges, key = { "challenge-${it.id}" }) { challenge ->
            CodingChallengeCard(
                challenge = challenge,
                unlocked = profile.levelFor("dsa") >= challenge.level,
                onClick = { onOpenChallenge(challenge.id) },
            )
        }

        item(key = "filter-topic") {
            FilterRow(label = "TOPIC") {
                GlassChip(
                    text = "All",
                    selected = activeTopicId.isEmpty(),
                    onClick = { viewModel.setTopicFilter("") },
                )
                askableTopics.forEach { topic ->
                    GlassChip(
                        text = topic.name,
                        selected = activeTopicId == topic.id,
                        onClick = { viewModel.setTopicFilter(topic.id) },
                    )
                }
            }
        }

        item(key = "filter-difficulty") {
            FilterRow(label = "DIFFICULTY") {
                GlassChip(
                    text = "All",
                    selected = activeDifficulty == null,
                    onClick = { viewModel.setDifficultyFilter(null) },
                )
                Difficulty.entries.forEach { difficulty ->
                    GlassChip(
                        text = difficulty.label,
                        selected = activeDifficulty == difficulty,
                        onClick = { viewModel.setDifficultyFilter(difficulty) },
                    )
                }
            }
        }

        item(key = "filter-status") {
            FilterRow(label = "STATUS") {
                GlassChip(
                    text = "All",
                    selected = activeStatus == null,
                    onClick = { viewModel.setStatusFilter(null) },
                )
                GlassChip(
                    text = "Solved",
                    selected = activeStatus == true,
                    onClick = { viewModel.setStatusFilter(true) },
                )
                GlassChip(
                    text = "Unsolved",
                    selected = activeStatus == false,
                    onClick = { viewModel.setStatusFilter(false) },
                )
            }
        }

        item(key = "questions-header") {
            SectionHeader(
                title = "Questions",
                trailing = {
                    Text(
                        text = "${visible.size} · ${solvedIds.size} solved",
                        style = CodeQuestTypography.labelMedium,
                        color = CodeQuestColors.TextSecondary,
                    )
                },
            )
        }

        if (visible.isEmpty()) {
            item(key = "empty") {
                GlowCard(accent = CodeQuestColors.Outline) {
                    Text(
                        text = "No questions match these filters.",
                        style = CodeQuestTypography.bodyMedium,
                        color = CodeQuestColors.TextSecondary,
                    )
                }
            }
        } else {
            items(visible, key = { it.id }) { question ->
                QuestionCard(
                    question = question,
                    topicName = topicNames[question.topicId] ?: "DSA",
                    solved = question.id in solvedIds,
                    onClick = { onOpenQuestion(question.id) },
                )
            }
        }
    }
}

/**
 * Per-difficulty scoreboard for the active scope — the practice pillar's
 * headline data block (EASY 3 / 5 solved, MEDIUM 4 / 8 …).
 */
@Composable
private fun DifficultyScoreboard(
    title: String,
    questions: List<PracticeQuestion>,
    solvedIds: Set<String>,
) {
    val rows = remember(questions, solvedIds) {
        Difficulty.entries.map { difficulty ->
            val total = questions.count { it.difficulty == difficulty }
            val solved = questions.count { it.difficulty == difficulty && it.id in solvedIds }
            Triple(difficulty, solved, total)
        }
    }
    val scopeSolved = remember(questions, solvedIds) { questions.count { it.id in solvedIds } }

    GlowCard(accent = CodeQuestColors.Accent) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title.uppercase(),
                style = CodeQuestTypography.labelMedium,
                color = CodeQuestColors.Accent,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$scopeSolved/${questions.size} SOLVED",
                style = CodeQuestTypography.labelMedium,
                color = CodeQuestColors.TextSecondary,
                letterSpacing = 0.6.sp,
            )
        }

        Spacer(Modifier.height(14.dp))

        rows.forEachIndexed { index, (difficulty, solved, total) ->
            if (index > 0) Spacer(Modifier.height(11.dp))
            ScoreRow(difficulty = difficulty, solved = solved, total = total)
        }
    }
}

@Composable
private fun ScoreRow(difficulty: Difficulty, solved: Int, total: Int) {
    val accent = difficultyColor(difficulty)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(accent),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = difficulty.label.uppercase(),
            style = CodeQuestTypography.labelMedium,
            color = CodeQuestColors.TextPrimary,
            letterSpacing = 0.7.sp,
            modifier = Modifier.width(74.dp),
        )
        Spacer(Modifier.width(6.dp))
        NeonProgressBar(
            progress = if (total == 0) 0f else solved.toFloat() / total,
            height = 5.dp,
            startColor = accent,
            endColor = accent,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "$solved / $total",
            style = CodeQuestTypography.labelMedium,
            color = if (total > 0 && solved >= total) {
                CodeQuestColors.Emerald
            } else {
                CodeQuestColors.TextSecondary
            },
        )
    }
}

/** Entry point to today's pinned ten, shown above the browsing list. */
@Composable
private fun DailySetBanner(
    solved: Int,
    total: Int,
    level: Int,
    onClick: () -> Unit,
) {
    val complete = total > 0 && solved >= total
    val accent = if (complete) CodeQuestColors.Emerald else CodeQuestColors.Gold

    GlowCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        accent = accent,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "TODAY'S SET",
                    style = CodeQuestTypography.labelMedium,
                    color = accent,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (complete) {
                        "All $total solved — a fresh set arrives tomorrow"
                    } else {
                        "$solved of $total solved · drawn from Level $level"
                    },
                    style = CodeQuestTypography.titleMedium,
                    color = CodeQuestColors.TextPrimary,
                )
                Spacer(Modifier.height(11.dp))
                NeonProgressBar(
                    progress = if (total == 0) 0f else solved.toFloat() / total,
                    height = 5.dp,
                    startColor = accent,
                    endColor = accent,
                )
            }
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Open today's set",
                tint = CodeQuestColors.TextTertiary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/** One write-code challenge in the Practice list, shown with its lock state. */
@Composable
private fun CodingChallengeCard(
    challenge: CodingChallenge,
    unlocked: Boolean,
    onClick: () -> Unit,
) {
    val accent = if (unlocked) CodeQuestColors.Gold else CodeQuestColors.Outline

    GlowCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        accent = accent,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(CodeQuestShapes.iconBox))
                    .background(if (unlocked) CodeQuestColors.Gold.copy(alpha = 0.14f) else CodeQuestColors.SurfaceMuted),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (unlocked) Icons.Filled.Code else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(17.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    style = CodeQuestTypography.titleSmall,
                    color = CodeQuestColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = if (unlocked) {
                        challenge.summary
                    } else {
                        "Unlocks at Level ${challenge.level}"
                    },
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(10.dp))
            Tag(
                text = "L${challenge.level}",
                accent = if (unlocked) CodeQuestColors.Gold else CodeQuestColors.Rose,
            )
        }
    }
}

@Composable
private fun FilterRow(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = CodeQuestTypography.labelMedium,
            color = CodeQuestColors.TextSecondary,
            letterSpacing = 0.8.sp,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = { content() },
        )
    }
}

@Composable
private fun QuestionCard(
    question: PracticeQuestion,
    topicName: String,
    solved: Boolean,
    onClick: () -> Unit,
) {
    val accent = if (solved) CodeQuestColors.Emerald else difficultyColor(question.difficulty)

    GlowCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        accent = accent,
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(CodeQuestShapes.iconBox))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = iconFor(question.type),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(19.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = question.question,
                    style = CodeQuestTypography.titleSmall,
                    color = CodeQuestColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "$topicName  ·  ${question.type.label}",
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (solved) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Solved",
                    tint = CodeQuestColors.Emerald,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(Modifier.height(11.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            DifficultyDots(level = question.difficulty.dots, accent = accent)
            Spacer(Modifier.width(10.dp))
            Tag(text = question.difficulty.label.uppercase(), accent = accent)
            Spacer(Modifier.weight(1f))
            Text(
                text = "+${question.xpReward} XP",
                style = CodeQuestTypography.labelMedium,
                color = CodeQuestColors.Accent,
            )
        }
    }
}

private val Difficulty.dots: Int
    get() = when (this) {
        Difficulty.EASY -> 1
        Difficulty.MEDIUM -> 3
        Difficulty.HARD -> 5
    }

private fun iconFor(type: QuestionType): ImageVector = when (type) {
    QuestionType.CONCEPT_MCQ -> Icons.Filled.Quiz
    QuestionType.OUTPUT_PREDICTION -> Icons.Filled.Code
    QuestionType.COMPLEXITY -> Icons.Filled.Speed
    QuestionType.DEBUGGING -> Icons.Filled.BugReport
    QuestionType.PROBLEM_SOLVING -> Icons.Filled.Lightbulb
    QuestionType.CODE_READING -> Icons.Filled.MenuBook
    QuestionType.CODE_COMPLETION -> Icons.Filled.PostAdd
    QuestionType.IMPLEMENTATION -> Icons.Filled.Build
    QuestionType.OPTIMIZATION -> Icons.Filled.TrendingUp
}
