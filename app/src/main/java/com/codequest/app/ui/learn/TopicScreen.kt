package com.codequest.app.ui.learn

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.app.domain.model.Difficulty
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.Lesson
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.ui.components.DifficultyDots
import com.codequest.app.ui.components.GlassButton
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.components.NeonProgressBar
import com.codequest.app.ui.components.SectionHeader
import com.codequest.app.ui.components.Tag
import com.codequest.app.ui.components.difficultyColor
import com.codequest.app.ui.components.toPercentText
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestTypography

@Composable
fun TopicScreen(
    topicId: String,
    onBack: () -> Unit,
    onPractice: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TopicViewModel = viewModel(),
) {
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val solvedIds by viewModel.solvedQuestionIds.collectAsStateWithLifecycle()

    val topic = remember(topics, topicId) { topics.firstOrNull { it.id == topicId } } ?: return
    val topicLessons = remember(lessons, topicId) {
        lessons.filter { it.topicId == topicId }.sortedBy { it.order }
    }
    val topicQuestions = remember(questions, topicId) {
        questions.filter { it.topicId == topicId }
    }

    var expandedLesson by rememberSaveable(topicId) { mutableIntStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TopBar(title = "Topic", onBack = onBack)

        TopicHeader(topic = topic)
        TopicProgressCard(topic = topic)

        LearnSection(
            lessons = topicLessons,
            expandedIndex = expandedLesson,
            onSelectLesson = { expandedLesson = it },
            onContinue = { index ->
                val lesson = topicLessons.getOrNull(index)
                if (lesson != null && !lesson.isCompleted) {
                    viewModel.completeLesson(lesson.id)
                }
                expandedLesson = if (index + 1 < topicLessons.size) index + 1 else -1
            },
        )

        PracticeSection(
            questions = topicQuestions,
            solvedIds = solvedIds,
            onOpenPractice = onPractice,
        )

        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
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
        Text(
            text = title,
            style = CodeQuestTypography.titleMedium,
            color = CodeQuestColors.TextSecondary,
        )
    }
}

@Composable
private fun TopicHeader(topic: DsaTopic) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = topic.name.uppercase(),
            style = CodeQuestTypography.headlineMedium,
            color = CodeQuestColors.TextPrimary,
        )
        Text(
            text = topic.description,
            style = CodeQuestTypography.bodyMedium,
            color = CodeQuestColors.TextSecondary,
        )
    }
}

@Composable
private fun TopicProgressCard(topic: DsaTopic) {
    val accent = if (topic.isCompleted) CodeQuestColors.Emerald else CodeQuestColors.Cyan

    GlowCard(accent = accent) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PROGRESS",
                    style = CodeQuestTypography.labelMedium,
                    color = accent,
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "${topic.lessonLabel}  ·  ${topic.questionLabel}",
                    style = CodeQuestTypography.titleSmall,
                    color = CodeQuestColors.TextPrimary,
                )
            }
            Text(
                text = topic.progress.toPercentText(),
                style = CodeQuestTypography.headlineSmall,
                color = accent,
            )
        }

        Spacer(Modifier.height(12.dp))

        NeonProgressBar(
            progress = topic.progress,
            height = 8.dp,
            startColor = accent,
            endColor = accent,
        )

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "DIFFICULTY",
                style = CodeQuestTypography.labelMedium,
                color = CodeQuestColors.TextSecondary,
                modifier = Modifier.weight(1f),
            )
            DifficultyDots(level = topic.difficultyDots, accent = accent)
            Spacer(Modifier.width(9.dp))
            Tag(text = topic.difficultyLabel, accent = accent)
        }
    }
}

@Composable
private fun LearnSection(
    lessons: List<Lesson>,
    expandedIndex: Int,
    onSelectLesson: (Int) -> Unit,
    onContinue: (Int) -> Unit,
) {
    val completed = lessons.count { it.isCompleted }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(
            title = "Learn",
            trailing = {
                Text(
                    text = "$completed/${lessons.size}",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            },
        )

        if (lessons.isEmpty()) {
            GlowCard(accent = CodeQuestColors.Outline) {
                Text(
                    text = "Lessons for this topic are on the way.",
                    style = CodeQuestTypography.bodyMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            }
        }

        lessons.forEachIndexed { index, lesson ->
            LessonCard(
                lesson = lesson,
                index = index,
                expanded = expandedIndex == index,
                onClick = { onSelectLesson(if (expandedIndex == index) -1 else index) },
                onContinue = { onContinue(index) },
            )
        }
    }
}

@Composable
private fun LessonCard(
    lesson: Lesson,
    index: Int,
    expanded: Boolean,
    onClick: () -> Unit,
    onContinue: () -> Unit,
) {
    val accent = if (lesson.isCompleted) CodeQuestColors.Emerald else CodeQuestColors.Cyan

    GlowCard(
        modifier = Modifier
            .animateContentSize(tween(260))
            .clickable(onClick = onClick),
        accent = if (expanded || lesson.isCompleted) accent else CodeQuestColors.OutlineSoft,
        contentPadding = PaddingValues(if (expanded) 16.dp else 13.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                if (lesson.isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = CodeQuestColors.Emerald,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Text(
                        text = "${index + 1}",
                        style = CodeQuestTypography.labelLarge,
                        color = accent,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = CodeQuestTypography.titleSmall,
                    color = CodeQuestColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!expanded) {
                    Text(
                        text = "${lesson.durationMinutes} min  ·  +${lesson.xpReward} XP",
                        style = CodeQuestTypography.bodySmall,
                        color = CodeQuestColors.TextTertiary,
                    )
                }
            }
            if (lesson.isCompleted) {
                Spacer(Modifier.width(8.dp))
                Tag(text = "Done", accent = CodeQuestColors.Emerald)
            }
        }

        if (expanded) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = lesson.explanation,
                style = CodeQuestTypography.bodyMedium,
                color = CodeQuestColors.TextSecondary,
            )

            Spacer(Modifier.height(12.dp))

            CodeExampleBox(text = lesson.example, accent = accent)

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "KEY TAKEAWAY",
                    style = CodeQuestTypography.labelMedium,
                    color = accent,
                    modifier = Modifier.width(104.dp),
                )
                Text(
                    text = lesson.keyTakeaway,
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextPrimary,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(14.dp))

            GlassButton(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (lesson.isCompleted) {
                        "CONTINUE"
                    } else {
                        "CONTINUE · +${lesson.xpReward} XP"
                    },
                    style = CodeQuestTypography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun CodeExampleBox(text: String, accent: Color) {
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
            text = "EXAMPLE",
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

/**
 * Practice block: per-difficulty solved counters (Easy / Medium / Hard) and
 * the orange CTA into the filtered practice list.
 */
@Composable
private fun PracticeSection(
    questions: List<PracticeQuestion>,
    solvedIds: Set<String>,
    onOpenPractice: () -> Unit,
) {
    val solvedCount = remember(questions, solvedIds) { questions.count { it.id in solvedIds } }
    val rows = remember(questions, solvedIds) {
        Difficulty.entries.map { difficulty ->
            val total = questions.count { it.difficulty == difficulty }
            val solved = questions.count { it.difficulty == difficulty && it.id in solvedIds }
            Triple(difficulty, solved, total)
        }.filter { it.third > 0 }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(
            title = "Practice",
            accent = CodeQuestColors.Accent,
            trailing = {
                Text(
                    text = "$solvedCount/${questions.size}",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            },
        )

        GlowCard(accent = CodeQuestColors.Accent) {
            if (rows.isEmpty()) {
                Text(
                    text = "Practice questions for this topic are on the way.",
                    style = CodeQuestTypography.bodyMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            } else {
                rows.forEachIndexed { index, (difficulty, solved, total) ->
                    if (index > 0) Spacer(Modifier.height(11.dp))
                    DifficultyRow(difficulty = difficulty, solved = solved, total = total)
                }
            }

            Spacer(Modifier.height(14.dp))

            GlassButton(
                onClick = onOpenPractice,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "START PRACTICE",
                    style = CodeQuestTypography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun DifficultyRow(difficulty: Difficulty, solved: Int, total: Int) {
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
            color = if (solved >= total && total > 0) {
                CodeQuestColors.Emerald
            } else {
                CodeQuestColors.TextSecondary
            },
        )
    }
}
