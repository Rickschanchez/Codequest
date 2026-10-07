package com.codequest.app.ui.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.ui.components.DifficultyDots
import com.codequest.app.ui.components.EntranceSection
import com.codequest.app.ui.components.GlassProgressCard
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.components.NeonProgressBar
import com.codequest.app.ui.components.SectionHeader
import com.codequest.app.ui.components.Tag
import com.codequest.app.ui.components.toPercentText
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestShapes
import com.codequest.app.ui.theme.CodeQuestTypography

@Composable
fun LearnScreen(
    onOpenTopic: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LearnViewModel = viewModel(),
) {
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val liveTopics = topics.count { it.isInteractive }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EntranceSection(index = 0) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = "LEARN",
                    style = CodeQuestTypography.headlineMedium,
                    color = CodeQuestColors.TextPrimary,
                )
                Text(
                    text = "Master Data Structures & Algorithms.",
                    style = CodeQuestTypography.bodyMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            }
        }

        EntranceSection(index = 1) {
            OverallProgressCard(
                progress = progress.dsaProgress,
                liveTopics = liveTopics,
                totalTopics = topics.size,
            )
        }

        SectionHeader(title = "Roadmap")

        topics.forEachIndexed { index, topic ->
            EntranceSection(index = index + 2) {
                RoadmapItem(
                    topic = topic,
                    isLast = index == topics.lastIndex,
                    onClick = { onOpenTopic(topic.id) },
                )
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun OverallProgressCard(
    progress: Float,
    liveTopics: Int,
    totalTopics: Int,
) {
    GlassProgressCard(
        title = "DSA PROGRESS",
        subtitle = "$liveTopics of $totalTopics topics unlocked",
        value = progress.toPercentText(),
        progress = progress,
        accent = CodeQuestColors.Accent,
        endAccent = CodeQuestColors.Accent,
    )
}

@Composable
private fun RoadmapItem(
    topic: DsaTopic,
    isLast: Boolean,
    onClick: () -> Unit,
) {
    val state = topicState(topic)
    val railColor = when (state) {
        RoadmapState.COMPLETED -> CodeQuestColors.Emerald.copy(alpha = 0.45f)
        RoadmapState.LOCKED -> CodeQuestColors.OutlineSoft
        else -> CodeQuestColors.Cyan.copy(alpha = 0.35f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = topic.isInteractive, onClick = onClick),
    ) {
        LeadingRail(
            topic = topic,
            state = state,
            isLast = isLast,
            railColor = railColor,
        )
        Spacer(Modifier.width(12.dp))
        TopicCard(
            topic = topic,
            state = state,
            modifier = Modifier.weight(1f),
        )
    }
}

private enum class RoadmapState { LOCKED, IN_PROGRESS, COMPLETED }

private fun topicState(topic: DsaTopic): RoadmapState = when {
    !topic.isInteractive -> RoadmapState.LOCKED
    topic.isCompleted -> RoadmapState.COMPLETED
    else -> RoadmapState.IN_PROGRESS
}

@Composable
private fun LeadingRail(
    topic: DsaTopic,
    state: RoadmapState,
    isLast: Boolean,
    railColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight(),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(CodeQuestShapes.iconBox))
                .background(
                    when (state) {
                        RoadmapState.COMPLETED -> CodeQuestColors.Emerald.copy(alpha = 0.16f)
                        RoadmapState.LOCKED -> CodeQuestColors.SurfaceMuted
                        RoadmapState.IN_PROGRESS -> CodeQuestColors.AccentSoft
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                RoadmapState.COMPLETED -> Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Completed",
                    tint = CodeQuestColors.Emerald,
                    modifier = Modifier.size(18.dp),
                )
                RoadmapState.LOCKED -> Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = CodeQuestColors.TextTertiary,
                    modifier = Modifier.size(16.dp),
                )
                RoadmapState.IN_PROGRESS -> Text(
                    text = topic.order.toString(),
                    style = CodeQuestTypography.titleMedium,
                    color = CodeQuestColors.Cyan,
                )
            }
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .background(railColor),
            )
        }
    }
}

@Composable
private fun TopicCard(
    topic: DsaTopic,
    state: RoadmapState,
    modifier: Modifier = Modifier,
) {
    val locked = state == RoadmapState.LOCKED
    val accent = when (state) {
        RoadmapState.COMPLETED -> CodeQuestColors.Emerald
        RoadmapState.LOCKED -> CodeQuestColors.Outline
        RoadmapState.IN_PROGRESS -> CodeQuestColors.Cyan
    }

    GlowCard(
        modifier = modifier,
        accent = accent,
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.name.uppercase(),
                    style = CodeQuestTypography.titleMedium,
                    color = if (locked) {
                        CodeQuestColors.TextTertiary
                    } else {
                        CodeQuestColors.TextPrimary
                    },
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = topic.description,
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextTertiary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(10.dp))
            StateIcon(state = state, accent = accent)
        }

        Spacer(Modifier.height(11.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            DifficultyDots(
                level = topic.difficultyDots,
                accent = if (locked) CodeQuestColors.Track else accent,
            )
            Spacer(Modifier.width(9.dp))
            Text(
                text = topic.difficultyLabel,
                style = CodeQuestTypography.labelSmall,
                color = CodeQuestColors.TextSecondary,
            )
        }

        Spacer(Modifier.height(11.dp))

        if (locked) {
            Tag(text = "Upcoming", accent = CodeQuestColors.TextTertiary)
        } else {
            NeonProgressBar(
                progress = topic.progress,
                height = 6.dp,
                startColor = accent,
                endColor = accent,
            )
            Spacer(Modifier.height(7.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${topic.lessonLabel}  ·  ${topic.questionLabel}",
                    style = CodeQuestTypography.labelSmall,
                    color = CodeQuestColors.TextSecondary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = topic.progress.toPercentText(),
                    style = CodeQuestTypography.labelMedium,
                    color = accent,
                )
            }
        }
    }
}

@Composable
private fun StateIcon(state: RoadmapState, accent: Color) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = when (state) {
                RoadmapState.COMPLETED -> Icons.Filled.Check
                RoadmapState.LOCKED -> Icons.Filled.Lock
                RoadmapState.IN_PROGRESS -> Icons.Filled.PlayArrow
            },
            contentDescription = null,
            tint = if (state == RoadmapState.LOCKED) {
                CodeQuestColors.TextTertiary
            } else {
                accent
            },
            modifier = Modifier.size(16.dp),
        )
    }
}
