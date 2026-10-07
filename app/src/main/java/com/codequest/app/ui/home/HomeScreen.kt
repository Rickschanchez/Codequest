package com.codequest.app.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.Lesson
import com.codequest.app.domain.model.UserProgress
import com.codequest.app.ui.components.EntranceSection
import com.codequest.app.ui.components.GlassButton
import com.codequest.app.ui.components.GlassButtonVariant
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.components.NeonProgressBar
import com.codequest.app.ui.components.SectionHeader
import com.codequest.app.ui.components.Tag
import com.codequest.app.ui.components.ThemeToggle
import com.codequest.app.ui.components.toPercentText
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestTypography

@Composable
fun HomeScreen(
    onOpenTopic: (String) -> Unit,
    onOpenDailyChallenge: () -> Unit,
    onOpenRoadmap: () -> Unit,
    onOpenPractice: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val profile by viewModel.learningProfile.collectAsStateWithLifecycle()

    val lesson = progress.continueLesson
    val currentTopic = lesson?.let { current -> topics.firstOrNull { it.id == current.topicId } }
    val liveTopics = topics.filter { it.isInteractive }

    val subjectCount = profile.selectedSubjects.size
    val greetingSubtitle = when {
        subjectCount == 0 -> "Learn. Practice. Level Up."
        profile.hasSubject("dsa") ->
            "$subjectCount subject${if (subjectCount == 1) "" else "s"} on your path · " +
                "DSA at Level ${profile.levelFor("dsa")}"
        else -> "$subjectCount subject${if (subjectCount == 1) "" else "s"} on your path"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EntranceSection(index = 0) { Masthead() }
        EntranceSection(index = 1) {
            GreetingHeader(progress = progress, subtitle = greetingSubtitle)
        }
        EntranceSection(index = 2) { HeroCard(progress = progress) }
        EntranceSection(index = 3) {
            ContinueLearningCard(
                lesson = lesson,
                topicName = currentTopic?.name,
                topicProgress = currentTopic?.progress ?: 0f,
                onResume = { currentTopic?.let { onOpenTopic(it.id) } },
            )
        }
        EntranceSection(index = 4) {
            DailyChallengeCard(
                progress = progress,
                onStart = {
                    viewModel.claimDailyChallenge()
                    onOpenDailyChallenge()
                },
            )
        }
        EntranceSection(index = 5) {
            QuickAccessRow(
                onOpenLearn = onOpenRoadmap,
                onOpenPractice = onOpenPractice,
            )
        }
        EntranceSection(index = 6) {
            ProgressSection(
                topics = liveTopics,
                overallProgress = progress.dsaProgress,
                onClick = onOpenRoadmap,
            )
        }
        EntranceSection(index = 7) {
            RecentlySolvedCard(title = progress.recentlySolvedTitle)
        }
        Spacer(Modifier.height(4.dp))
    }
}

/** The masthead: product name and the line the whole app is built around. */
@Composable
private fun Masthead() {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = "CODEQUEST",
            style = CodeQuestTypography.titleMedium,
            color = CodeQuestColors.TextPrimary,
            letterSpacing = 4.sp,
        )
        Text(
            text = "Learn. Practice. Level Up.",
            style = CodeQuestTypography.labelMedium,
            color = CodeQuestColors.TextTertiary,
            letterSpacing = 1.2.sp,
        )
    }
}

/** Two taps straight into the other pillars of the loop. */
@Composable
private fun QuickAccessRow(onOpenLearn: () -> Unit, onOpenPractice: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Jump back in")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction(
                icon = Icons.Filled.School,
                label = "LEARN",
                modifier = Modifier.weight(1f),
                onClick = onOpenLearn,
            )
            QuickAction(
                icon = Icons.Filled.Quiz,
                label = "PRACTICE",
                modifier = Modifier.weight(1f),
                onClick = onOpenPractice,
            )
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    GlassButton(
        onClick = onClick,
        variant = GlassButtonVariant.SECONDARY,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CodeQuestColors.Accent,
            modifier = Modifier.size(17.dp),
        )
        Spacer(Modifier.width(9.dp))
        Text(
            text = label,
            style = CodeQuestTypography.labelLarge,
            color = CodeQuestColors.TextPrimary,
        )
    }
}

@Composable
private fun GreetingHeader(progress: UserProgress, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(CodeQuestColors.AccentFill),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = progress.displayName.take(1),
                style = CodeQuestTypography.titleLarge,
                color = CodeQuestColors.OnAccent,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = progress.greeting,
                style = CodeQuestTypography.headlineSmall,
                color = CodeQuestColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = CodeQuestTypography.bodyMedium,
                color = CodeQuestColors.TextSecondary,
            )
        }
        Spacer(Modifier.width(10.dp))
        ThemeToggle()
    }
}

/**
 * The editorial hero: LEVEL 07 · 1,240 XP · progress · streak. Typography is
 * the visual event — no badges, no flames, no decoration.
 */
@Composable
private fun HeroCard(progress: UserProgress) {
    GlowCard(accent = CodeQuestColors.Accent) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                Text(
                    text = "LEVEL",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.TextSecondary,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = "%02d".format(progress.level),
                    style = CodeQuestTypography.displaySmall,
                    color = CodeQuestColors.Accent,
                )
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%,d XP".format(progress.totalXp),
                    style = CodeQuestTypography.headlineMedium,
                    color = CodeQuestColors.TextPrimary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "TOTAL EXPERIENCE",
                    style = CodeQuestTypography.labelSmall,
                    color = CodeQuestColors.TextTertiary,
                    letterSpacing = 0.8.sp,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        NeonProgressBar(
            progress = progress.levelProgress,
            height = 7.dp,
            startColor = CodeQuestColors.Accent,
            endColor = CodeQuestColors.Accent,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "%,d / %,d XP TO LEVEL %02d".format(
                progress.xpIntoLevel,
                progress.xpLevelCap,
                progress.level + 1,
            ),
            style = CodeQuestTypography.labelSmall,
            color = CodeQuestColors.TextSecondary,
            letterSpacing = 0.6.sp,
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = CodeQuestColors.Accent,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(7.dp))
            Text(
                text = "${progress.streakDays} DAY STREAK",
                style = CodeQuestTypography.titleMedium,
                color = CodeQuestColors.TextPrimary,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${progress.questionsSolved} SOLVED",
                style = CodeQuestTypography.labelMedium,
                color = CodeQuestColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun ContinueLearningCard(
    lesson: Lesson?,
    topicName: String?,
    topicProgress: Float,
    onResume: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Continue Learning")

        if (lesson == null || topicName == null) {
            GlowCard(accent = CodeQuestColors.Outline) {
                Text(
                    text = "Every lesson is complete. Jump into practice to keep the streak alive.",
                    style = CodeQuestTypography.bodyMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            }
            return
        }

        GlowCard(accent = CodeQuestColors.Accent, emphasis = true) {
            Text(
                text = topicName.uppercase(),
                style = CodeQuestTypography.labelMedium,
                color = CodeQuestColors.Accent,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = lesson.title,
                style = CodeQuestTypography.headlineSmall,
                color = CodeQuestColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${topicProgress.toPercentText()} COMPLETE",
                    style = CodeQuestTypography.titleMedium,
                    color = CodeQuestColors.Accent,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "+${lesson.xpReward} XP",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            }
            Spacer(Modifier.height(8.dp))
            NeonProgressBar(
                progress = topicProgress,
                height = 6.dp,
                startColor = CodeQuestColors.Accent,
                endColor = CodeQuestColors.Accent,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${lesson.durationMinutes} MIN LESSON",
                style = CodeQuestTypography.labelSmall,
                color = CodeQuestColors.TextTertiary,
                letterSpacing = 0.7.sp,
            )

            Spacer(Modifier.height(14.dp))

            GlassButton(
                onClick = onResume,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 18.dp,
                    vertical = 14.dp,
                ),
            ) {
                Text(
                    text = "CONTINUE",
                    style = CodeQuestTypography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun DailyChallengeCard(progress: UserProgress, onStart: () -> Unit) {
    val claimed = progress.dailyChallengeCompleted
    val accent by animateColorAsState(
        targetValue = if (claimed) CodeQuestColors.Emerald else CodeQuestColors.Accent,
        animationSpec = tween(durationMillis = 300),
        label = "dailyChallengeAccent",
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Daily Challenge", accent = accent)

        GlowCard(accent = accent, emphasis = !claimed) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (claimed) "Challenge cleared" else "Solve today's DSA challenge",
                        style = CodeQuestTypography.titleLarge,
                        color = CodeQuestColors.TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (claimed) {
                            "Reward banked. Resets tomorrow."
                        } else {
                            "One curated question, picked from your roadmap."
                        },
                        style = CodeQuestTypography.bodySmall,
                        color = CodeQuestColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Tag(
                    text = if (claimed) "BANKED" else "+${progress.dailyChallengeXp} XP",
                    accent = accent,
                    filled = !claimed,
                )
            }

            Spacer(Modifier.height(14.dp))

            GlassButton(
                onClick = onStart,
                enabled = !claimed,
                modifier = Modifier.fillMaxWidth(),
                accent = accent,
                contentPadding = PaddingValues(
                    horizontal = 18.dp,
                    vertical = 14.dp,
                ),
            ) {
                Text(
                    text = if (claimed) "CHALLENGE CLEARED" else "START CHALLENGE",
                    style = CodeQuestTypography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun ProgressSection(
    topics: List<DsaTopic>,
    overallProgress: Float,
    onClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(
            title = "Progress",
            trailing = {
                Text(
                    text = overallProgress.toPercentText(),
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.Accent,
                )
            },
        )

        GlowCard(accent = CodeQuestColors.Accent) {
            if (topics.isEmpty()) {
                Text(
                    text = "No roadmap topics are open yet.",
                    style = CodeQuestTypography.bodyMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            } else {
                topics.take(PROGRESS_ROWS).forEachIndexed { index, topic ->
                    if (index > 0) Spacer(Modifier.height(12.dp))
                    TopicProgressRow(topic = topic)
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "VIEW FULL ROADMAP",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.Accent,
                    letterSpacing = 0.8.sp,
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = CodeQuestColors.Accent,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun TopicProgressRow(topic: DsaTopic) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = topic.name,
            style = CodeQuestTypography.titleSmall,
            color = CodeQuestColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(104.dp),
        )
        Spacer(Modifier.width(10.dp))
        NeonProgressBar(
            progress = topic.progress,
            height = 5.dp,
            startColor = CodeQuestColors.Accent,
            endColor = CodeQuestColors.Accent,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = topic.progress.toPercentText(),
            style = CodeQuestTypography.labelMedium,
            color = if (topic.progress > 0f) {
                CodeQuestColors.Accent
            } else {
                CodeQuestColors.TextTertiary
            },
            modifier = Modifier.width(38.dp),
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RecentlySolvedCard(title: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Recently Solved", accent = CodeQuestColors.Emerald)

        GlowCard(
            accent = if (title == null) CodeQuestColors.Outline else CodeQuestColors.Emerald,
        ) {
            if (title == null) {
                Text(
                    text = "Nothing solved yet. Your first question is waiting in Practice.",
                    style = CodeQuestTypography.bodyMedium,
                    color = CodeQuestColors.TextSecondary,
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = CodeQuestColors.Emerald,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(11.dp))
                    Text(
                        text = title,
                        style = CodeQuestTypography.bodyMedium,
                        color = CodeQuestColors.TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private const val PROGRESS_ROWS = 5
