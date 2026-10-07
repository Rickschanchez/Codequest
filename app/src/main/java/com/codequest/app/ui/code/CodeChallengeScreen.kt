package com.codequest.app.ui.code

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.app.domain.code.ExecutionResult
import com.codequest.app.domain.model.CodingChallenge
import com.codequest.app.ui.components.GlassButton
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.components.SectionHeader
import com.codequest.app.ui.components.Tag
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestShapes
import com.codequest.app.ui.theme.CodeQuestTypography

/**
 * One write-code challenge: the statement, the tests, the editor and a RUN
 * button that reports honestly that nothing can execute in this build.
 */
@Composable
fun CodeChallengeScreen(
    challengeId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CodeChallengeViewModel = viewModel(),
) {
    val challenges by viewModel.challenges.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()

    val challenge = challenges.firstOrNull { it.id == challengeId } ?: return
    val topicName = topics.firstOrNull { it.id == challenge.topicId }?.name ?: "DSA"
    val currentLevel = profile.levelFor("dsa")
    val unlocked = currentLevel >= challenge.level

    var code by rememberSaveable(challengeId) {
        mutableStateOf(challenge.spec.starterCode)
    }
    var result by remember(challengeId) { mutableStateOf<ExecutionResult?>(null) }

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
            Column(Modifier.weight(1f)) {
                Text(
                    text = topicName.uppercase(),
                    style = CodeQuestTypography.titleMedium,
                    color = CodeQuestColors.TextPrimary,
                    letterSpacing = 0.6.sp,
                )
                Text(
                    text = "CODING CHALLENGE",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.TextTertiary,
                    letterSpacing = 0.8.sp,
                )
            }
            Tag(
                text = "LEVEL ${challenge.level}",
                accent = CodeQuestColors.Accent,
                filled = unlocked,
            )
        }

        GlowCard(accent = CodeQuestColors.Accent, emphasis = true) {
            Text(
                text = challenge.title,
                style = CodeQuestTypography.headlineSmall,
                color = CodeQuestColors.TextPrimary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = challenge.summary,
                style = CodeQuestTypography.bodyMedium,
                color = CodeQuestColors.TextSecondary,
            )
            Spacer(Modifier.height(11.dp))
            Tag(text = "+${challenge.xpReward} XP", accent = CodeQuestColors.Accent)
        }

        if (unlocked) {
            StatementCard(challenge = challenge)
            TestCaseCard(challenge = challenge)

            SectionHeader(title = "Your solution", accent = CodeQuestColors.Accent)
            EditorCard(
                code = code,
                onCodeChange = { code = it },
                onReset = {
                    code = challenge.spec.starterCode
                    result = null
                },
            )

            GlassButton(
                onClick = { result = viewModel.run(challenge, code) },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 13.dp, horizontal = 18.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "RUN", style = CodeQuestTypography.labelLarge)
            }

            result?.let { verdict -> ResultCard(verdict = verdict) }

            Text(
                text = "Nothing is compiled or executed in this build — RUN reports " +
                    "that honestly instead of guessing a verdict.",
                style = CodeQuestTypography.bodySmall,
                color = CodeQuestColors.TextTertiary,
            )
        } else {
            LockedCard(currentLevel = currentLevel, requiredLevel = challenge.level)
        }
    }
}

@Composable
private fun StatementCard(challenge: CodingChallenge) {
    val spec = challenge.spec

    GlowCard(accent = CodeQuestColors.Outline) {
        SectionHeader(title = "Problem", accent = CodeQuestColors.Accent)
        Spacer(Modifier.height(9.dp))
        Text(
            text = spec.problemStatement,
            style = CodeQuestTypography.bodyMedium,
            color = CodeQuestColors.TextSecondary,
        )

        Spacer(Modifier.height(14.dp))
        SectionHeader(title = "Constraints", accent = CodeQuestColors.Gold)
        Spacer(Modifier.height(9.dp))
        spec.constraints.forEach { constraint ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "-",
                    style = CodeQuestTypography.bodyMedium,
                    color = CodeQuestColors.Gold,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = constraint,
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextSecondary,
                )
            }
            Spacer(Modifier.height(5.dp))
        }

        Spacer(Modifier.height(9.dp))
        Text(
            text = spec.expectedBehavior,
            style = CodeQuestTypography.bodySmall,
            color = CodeQuestColors.Emerald,
        )
    }
}

@Composable
private fun TestCaseCard(challenge: CodingChallenge) {
    val spec = challenge.spec

    GlowCard(accent = CodeQuestColors.Emerald) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "TEST CASES",
                    style = CodeQuestTypography.labelMedium,
                    color = CodeQuestColors.Emerald,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${spec.visibleTestCases.size} visible · " +
                        "${spec.hiddenTestCount} hidden",
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextSecondary,
                )
            }
            Tag(
                text = spec.timeComplexityTarget,
                accent = CodeQuestColors.Emerald,
            )
        }

        Spacer(Modifier.height(11.dp))

        spec.visibleTestCases.forEachIndexed { index, testCase ->
            if (index > 0) Spacer(Modifier.height(9.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "IN",
                    style = CodeQuestTypography.labelSmall,
                    color = CodeQuestColors.TextTertiary,
                    letterSpacing = 0.8.sp,
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    text = testCase.input,
                    style = CodeQuestTypography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = CodeQuestColors.TextPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    text = "-> ${testCase.expectedOutput}",
                    style = CodeQuestTypography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = CodeQuestColors.Emerald,
                )
            }
        }
    }
}

@Composable
private fun EditorCard(code: String, onCodeChange: (String) -> Unit, onReset: () -> Unit) {
    val shape = RoundedCornerShape(CodeQuestShapes.card)

    GlowCard(
        accent = CodeQuestColors.Outline,
        contentPadding = PaddingValues(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Code,
                contentDescription = null,
                tint = CodeQuestColors.Accent,
                modifier = Modifier.size(15.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "CPP EDITOR",
                style = CodeQuestTypography.labelSmall,
                color = CodeQuestColors.TextSecondary,
                letterSpacing = 0.8.sp,
                modifier = Modifier.weight(1f),
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CodeQuestColors.SurfaceMuted)
                    .clickable(onClick = onReset)
                    .padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Reset to starter code",
                    tint = CodeQuestColors.TextSecondary,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "RESET",
                    style = CodeQuestTypography.labelSmall,
                    color = CodeQuestColors.TextSecondary,
                    letterSpacing = 0.6.sp,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        BasicTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp, max = 260.dp)
                .clip(shape)
                .background(CodeQuestColors.SurfaceMuted)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            textStyle = CodeQuestTypography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                color = CodeQuestColors.TextPrimary,
                lineHeight = 18.sp,
            ),
            cursorBrush = SolidColor(CodeQuestColors.Accent),
        )
    }
}

@Composable
private fun ResultCard(verdict: ExecutionResult) {
    GlowCard(accent = CodeQuestColors.Gold) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CodeQuestColors.Gold.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = CodeQuestColors.Gold,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(Modifier.width(11.dp))
            Text(
                text = verdict.headline.uppercase(),
                style = CodeQuestTypography.titleMedium,
                color = CodeQuestColors.Gold,
                letterSpacing = 0.6.sp,
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = verdict.detail,
            style = CodeQuestTypography.bodySmall,
            color = CodeQuestColors.TextSecondary,
        )
    }
}

@Composable
private fun LockedCard(currentLevel: Int, requiredLevel: Int) {
    GlowCard(accent = CodeQuestColors.Rose) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = CodeQuestColors.Rose,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "LOCKED UNTIL LEVEL $requiredLevel",
                    style = CodeQuestTypography.titleMedium,
                    color = CodeQuestColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "You are at Level $currentLevel. Practise and your level " +
                        "moves on its own once ten answers are judged.",
                    style = CodeQuestTypography.bodySmall,
                    color = CodeQuestColors.TextSecondary,
                )
            }
        }
    }
}
