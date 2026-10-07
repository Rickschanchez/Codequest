package com.codequest.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.TaskAlt
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
import com.codequest.app.domain.model.Achievement
import com.codequest.app.domain.model.LearningProfile
import com.codequest.app.domain.model.Subjects
import com.codequest.app.domain.model.UserProgress
import com.codequest.app.data.auth.AuthUser
import com.codequest.app.ui.components.GlassButton
import com.codequest.app.ui.components.GlassButtonVariant
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.components.NeonProgressBar
import com.codequest.app.ui.components.SectionHeader
import com.codequest.app.ui.components.StatTile
import com.codequest.app.ui.components.Tag
import com.codequest.app.ui.components.toPercentText
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestShapes
import com.codequest.app.ui.theme.CodeQuestTypography

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
) {
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val profile by viewModel.learningProfile.collectAsStateWithLifecycle()

    val unlocked = achievements.count { it.isUnlocked }
    val topicsCompleted = topics.count { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = "YOUR PROGRESS",
                style = CodeQuestTypography.headlineMedium,
                color = CodeQuestColors.TextPrimary,
            )
            Text(
                text = "Track your mastery over DSA.",
                style = CodeQuestTypography.bodyMedium,
                color = CodeQuestColors.TextSecondary,
            )
        }

        IdentityCard(progress = progress)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                icon = Icons.Filled.LocalFireDepartment,
                label = "Streak",
                value = "${progress.streakDays} days",
                accent = CodeQuestColors.Accent,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                icon = Icons.Filled.Quiz,
                label = "Solved",
                value = "${progress.questionsSolved}",
                accent = CodeQuestColors.Emerald,
                modifier = Modifier.weight(1f),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                icon = Icons.Filled.TaskAlt,
                label = "Topics",
                value = "$topicsCompleted/${topics.size}",
                accent = CodeQuestColors.Accent,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                icon = Icons.Filled.MenuBook,
                label = "Lessons",
                value = "${progress.lessonsCompleted}",
                accent = CodeQuestColors.Emerald,
                modifier = Modifier.weight(1f),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                icon = Icons.Filled.Bolt,
                label = "Easy",
                value = "${progress.easySolved}",
                accent = CodeQuestColors.Emerald,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                icon = Icons.Filled.Bolt,
                label = "Medium",
                value = "${progress.mediumSolved}",
                accent = CodeQuestColors.Gold,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                icon = Icons.Filled.Bolt,
                label = "Hard",
                value = "${progress.hardSolved}",
                accent = CodeQuestColors.Rose,
                modifier = Modifier.weight(1f),
            )
        }

        DsaMasteryCard(progress = progress)

        LearningPathCard(profile = profile)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                title = "Achievements",
                accent = CodeQuestColors.Gold,
                trailing = {
                    Text(
                        text = "$unlocked/${achievements.size}",
                        style = CodeQuestTypography.labelMedium,
                        color = CodeQuestColors.TextSecondary,
                    )
                },
            )
            achievements.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowItems.forEach { achievement ->
                        AchievementTile(
                            achievement = achievement,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        AccountCard(
            user = currentUser,
            onSignOut = { viewModel.signOut() },
        )

        Spacer(Modifier.height(4.dp))
    }
}

/** Firebase identity + the way back out of the account. */
@Composable
private fun AccountCard(user: AuthUser?, onSignOut: () -> Unit) {
    val isDemo = user?.isDemo == true
    GlowCard(accent = CodeQuestColors.Outline) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(CodeQuestShapes.iconBox))
                    .background(CodeQuestColors.AccentSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = CodeQuestColors.Accent,
                    modifier = Modifier.size(19.dp),
                )
            }
            Spacer(Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isDemo) "PRESENTATION ACCOUNT" else "SIGNED IN AS",
                        style = CodeQuestTypography.labelMedium,
                        color = CodeQuestColors.TextSecondary,
                        letterSpacing = 0.8.sp,
                    )
                    if (isDemo) {
                        Spacer(Modifier.width(8.dp))
                        Tag(text = "DEMO", accent = CodeQuestColors.Gold, filled = true)
                    }
                }
                Text(
                    text = user?.email ?: user?.displayName ?: "Local account",
                    style = CodeQuestTypography.titleSmall,
                    color = CodeQuestColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(Modifier.height(13.dp))

        GlassButton(
            onClick = onSignOut,
            variant = GlassButtonVariant.SECONDARY,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = if (isDemo) "EXIT DEMO" else "SIGN OUT",
                style = CodeQuestTypography.labelLarge,
            )
        }
    }
}

@Composable
private fun IdentityCard(progress: UserProgress) {
    GlowCard(accent = CodeQuestColors.Accent) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(CodeQuestColors.Accent)
                    .border(2.dp, CodeQuestColors.AccentPressed, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = progress.displayName.take(1),
                    style = CodeQuestTypography.displaySmall,
                    color = CodeQuestColors.OnAccent,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = progress.displayName,
                    style = CodeQuestTypography.headlineMedium,
                    color = CodeQuestColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Tag(
                        text = "LEVEL ${progress.level.toString().padStart(2, '0')}",
                        accent = CodeQuestColors.Accent,
                        filled = true,
                    )
                    Spacer(Modifier.width(8.dp))
                    Tag(text = "${progress.totalXp} XP", accent = CodeQuestColors.Accent)
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    text = "Learn. Practise. Master.",
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(13.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "LEVEL PROGRESS",
                style = CodeQuestTypography.labelMedium,
                color = CodeQuestColors.TextSecondary,
                letterSpacing = 0.8.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${progress.xpIntoLevel} / ${progress.xpLevelCap} XP",
                style = CodeQuestTypography.labelMedium,
                color = CodeQuestColors.Accent,
            )
        }
        Spacer(Modifier.height(7.dp))
        NeonProgressBar(
            progress = progress.levelProgress,
            height = 7.dp,
            startColor = CodeQuestColors.Accent,
            endColor = CodeQuestColors.Accent,
        )
    }
}

@Composable
private fun DsaMasteryCard(progress: UserProgress) {
    GlowCard(accent = CodeQuestColors.Emerald) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DSA MASTERY",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.Emerald,
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "Overall roadmap progress",
                    style = CodeQuestTypography.titleSmall,
                    color = CodeQuestColors.TextPrimary,
                )
            }
            Text(
                text = progress.dsaProgress.toPercentText(),
                style = CodeQuestTypography.headlineSmall,
                color = CodeQuestColors.Emerald,
            )
        }

        Spacer(Modifier.height(12.dp))

        NeonProgressBar(
            progress = progress.dsaProgress,
            height = 8.dp,
            startColor = CodeQuestColors.Accent,
            endColor = CodeQuestColors.Accent,
        )

        Spacer(Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${progress.lessonsCompleted} lessons complete  ·  " +
                    "${progress.questionsSolved} questions solved",
                style = CodeQuestTypography.labelSmall,
                color = CodeQuestColors.TextSecondary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * The personalised path: every subject chosen at onboarding with the level it
 * currently sits at. Levels only ever move through the adaptive engine, so
 * this card is the honest picture of where the learner stands.
 */
@Composable
private fun LearningPathCard(profile: LearningProfile) {
    if (profile.selectedSubjects.isEmpty()) return

    val average = (profile.averageLevel * 10).toInt() / 10.0

    GlowCard(accent = CodeQuestColors.Accent) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "LEARNING PATH",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.Accent,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "${profile.selectedSubjects.size} subjects tracked",
                    style = CodeQuestTypography.titleSmall,
                    color = CodeQuestColors.TextPrimary,
                )
            }
            Tag(text = "AVG $average", accent = CodeQuestColors.Accent, filled = true)
        }

        profile.selectedSubjects.forEach { subjectId ->
            val subject = Subjects.find(subjectId) ?: return@forEach
            val level = profile.levelFor(subjectId)
            val initial = profile.initialLevelFor(subjectId)

            Spacer(Modifier.height(15.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = subject.emoji, fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = subject.name,
                        style = CodeQuestTypography.titleSmall,
                        color = CodeQuestColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = if (level == initial) {
                            "Started at Level $level"
                        } else {
                            "Started at Level $initial · now Level $level"
                        },
                        style = CodeQuestTypography.bodySmall,
                        color = CodeQuestColors.TextSecondary,
                    )
                    Spacer(Modifier.height(7.dp))
                    NeonProgressBar(
                        progress = level / 10f,
                        height = 4.dp,
                        startColor = if (level != initial) {
                            CodeQuestColors.Emerald
                        } else {
                            CodeQuestColors.Accent
                        },
                        endColor = if (level != initial) {
                            CodeQuestColors.Emerald
                        } else {
                            CodeQuestColors.Accent
                        },
                    )
                }
                Spacer(Modifier.width(12.dp))
                Tag(
                    text = "LEVEL ${level.toString().padStart(2, '0')}",
                    accent = if (level != initial) CodeQuestColors.Emerald else CodeQuestColors.Accent,
                    filled = level != initial,
                )
            }
        }

        Spacer(Modifier.height(15.dp))
        Text(
            text = "Levels move on their own: ten answers judged, then adjusted.",
            style = CodeQuestTypography.bodySmall,
            color = CodeQuestColors.TextTertiary,
        )
    }
}

@Composable
private fun AchievementTile(achievement: Achievement, modifier: Modifier = Modifier) {
    val unlocked = achievement.isUnlocked
    val accent = if (unlocked) CodeQuestColors.Gold else CodeQuestColors.Outline

    GlowCard(
        modifier = modifier,
        accent = accent,
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (unlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.weight(1f))
            Tag(text = achievement.progressLabel, accent = accent)
        }
        Spacer(Modifier.height(9.dp))
        Text(
            text = achievement.title,
            style = CodeQuestTypography.titleSmall,
            color = if (unlocked) CodeQuestColors.TextPrimary else CodeQuestColors.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = achievement.description,
            style = CodeQuestTypography.bodySmall,
            color = CodeQuestColors.TextTertiary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        NeonProgressBar(
            progress = achievement.progress,
            height = 4.dp,
            startColor = if (unlocked) accent else CodeQuestColors.TextTertiary,
            endColor = if (unlocked) accent else CodeQuestColors.TextTertiary,
            halo = unlocked,
        )
    }
}
