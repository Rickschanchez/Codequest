package com.codequest.app

import com.codequest.app.data.CodingChallenges
import com.codequest.app.domain.code.CodeExecutor
import com.codequest.app.domain.code.ExecutionResult
import com.codequest.app.data.questions.QuestionBank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The coding ramp and the honesty contract of the runner: five challenges on
 * the 6-10 rungs, each with real test cases, and an executor that refuses to
 * invent a verdict.
 */
class CodingChallengesTest {

    private val challenges = CodingChallenges.all

    @Test
    fun `one challenge per coding level from six to ten`() {
        assertEquals(5, challenges.size)
        assertEquals(listOf(6, 7, 8, 9, 10), challenges.map { it.level }.sorted())
        assertEquals(challenges.size, challenges.map { it.id }.distinct().size)
    }

    @Test
    fun `every challenge carries a complete spec`() {
        challenges.forEach { challenge ->
            val spec = challenge.spec
            assertTrue("${challenge.id} has no statement", spec.problemStatement.isNotBlank())
            assertTrue("${challenge.id} has no starter code", spec.starterCode.isNotBlank())
            assertTrue("${challenge.id} has no constraints", spec.constraints.isNotEmpty())
            assertTrue("${challenge.id} has no expected behaviour", spec.expectedBehavior.isNotBlank())
            assertTrue("${challenge.id} needs visible test cases", spec.visibleTestCases.isNotEmpty())
            assertEquals(
                "${challenge.id} hidden count",
                spec.hiddenTestCount,
                spec.testCases.count { it.isHidden },
            )
            assertEquals(
                "${challenge.id} xp",
                QuestionBank.xpForLevel(challenge.level),
                challenge.xpReward,
            )
            assertTrue(
                "${challenge.id} points at an unknown topic",
                challenge.topicId in QuestionBank.topicOrder,
            )
            assertTrue("${challenge.id} level out of range", challenge.level in 6..10)
        }
    }

    @Test
    fun `runner reports unavailability instead of a verdict`() {
        val challenge = challenges.first()
        val verdict = CodeExecutor.execute("int x = 1;", challenge.spec)

        assertTrue(verdict is ExecutionResult.Unavailable)
        assertTrue(verdict.headline.isNotBlank())
        assertTrue(verdict.detail.contains("no sandboxed compiler"))
    }

    @Test
    fun `runner refuses empty code with its own message`() {
        val challenge = challenges.first()
        val verdict = CodeExecutor.execute("   \n  ", challenge.spec)

        assertTrue(verdict is ExecutionResult.Unavailable)
        assertEquals("Nothing to run", verdict.headline)
    }
}
