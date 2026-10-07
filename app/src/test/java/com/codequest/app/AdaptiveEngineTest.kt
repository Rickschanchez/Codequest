package com.codequest.app

import com.codequest.app.domain.model.SubjectStats
import com.codequest.app.domain.practice.AdaptiveEngine
import com.codequest.app.domain.practice.LevelDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The adaptive rules, stated as behaviour: promote at 80%, demote below 50%,
 * judge nothing on fewer than ten answers, always inside 1..10, and only over
 * the last twenty answers.
 */
class AdaptiveEngineTest {

    private fun stats(recent: List<Boolean>): SubjectStats = SubjectStats(
        attempted = recent.size,
        correct = recent.count { it },
        recent = recent,
    )

    /** [correct] right answers out of [answered], newest last. */
    private fun window(correct: Int, answered: Int): List<Boolean> =
        (1..answered).map { it <= correct }

    @Test
    fun `promotes at or above eighty percent`() {
        val verdict = AdaptiveEngine.evaluate(
            stats = stats(window(correct = 9, answered = 10)),
            currentLevel = 5,
            subjectId = "dsa",
        )

        assertEquals(LevelDirection.UP, verdict.direction)
        assertEquals(6, verdict.toLevel)
        assertTrue(verdict.levelChanged)
        assertTrue(verdict.message.contains("Level 6"))
        assertEquals(0.9f, verdict.accuracy, 0.001f)
    }

    @Test
    fun `holds between the thresholds`() {
        val verdict = AdaptiveEngine.evaluate(
            stats = stats(window(correct = 6, answered = 10)),
            currentLevel = 5,
            subjectId = "dsa",
        )

        assertEquals(LevelDirection.HOLD, verdict.direction)
        assertEquals(5, verdict.toLevel)
        assertFalse(verdict.levelChanged)
    }

    @Test
    fun `fifty percent exactly is still a hold`() {
        val verdict = AdaptiveEngine.evaluate(
            stats = stats(window(correct = 5, answered = 10)),
            currentLevel = 4,
            subjectId = "dsa",
        )

        assertEquals(LevelDirection.HOLD, verdict.direction)
        assertEquals(4, verdict.toLevel)
    }

    @Test
    fun `demotes below fifty percent`() {
        val verdict = AdaptiveEngine.evaluate(
            stats = stats(window(correct = 4, answered = 10)),
            currentLevel = 7,
            subjectId = "dsa",
        )

        assertEquals(LevelDirection.DOWN, verdict.direction)
        assertEquals(6, verdict.toLevel)
        assertTrue(verdict.levelChanged)
        assertTrue(verdict.message.contains("fundamentals"))
    }

    @Test
    fun `judges nothing before ten answers are in`() {
        val verdict = AdaptiveEngine.evaluate(
            stats = stats(window(correct = 10, answered = 9)),
            currentLevel = 5,
            subjectId = "dsa",
        )

        assertEquals(LevelDirection.HOLD, verdict.direction)
        assertEquals(5, verdict.toLevel)
        assertFalse(verdict.levelChanged)
        assertTrue(verdict.message.contains("1 more answer"))
    }

    @Test
    fun `holds at the top level even on a perfect window`() {
        val verdict = AdaptiveEngine.evaluate(
            stats = stats(window(correct = 10, answered = 10)),
            currentLevel = 10,
            subjectId = "dsa",
        )

        assertEquals(LevelDirection.UP, verdict.direction)
        assertEquals(10, verdict.toLevel)
        assertFalse(verdict.levelChanged)
    }

    @Test
    fun `holds at the bottom level on a failed window`() {
        val verdict = AdaptiveEngine.evaluate(
            stats = stats(window(correct = 0, answered = 10)),
            currentLevel = 1,
            subjectId = "dsa",
        )

        assertEquals(LevelDirection.DOWN, verdict.direction)
        assertEquals(1, verdict.toLevel)
        assertFalse(verdict.levelChanged)
    }

    @Test
    fun `only the last twenty answers count`() {
        val older = List(10) { true }                 // ignored
        val recent = List(16) { true } + List(4) { false }  // 16/20 = 80%
        val verdict = AdaptiveEngine.evaluate(
            stats = stats(older + recent),
            currentLevel = 5,
            subjectId = "dsa",
        )

        assertEquals(20, verdict.answered)
        assertEquals(0.8f, verdict.accuracy, 0.001f)
        assertEquals(LevelDirection.UP, verdict.direction)
        assertEquals(6, verdict.toLevel)
    }

    @Test
    fun `an empty window changes nothing`() {
        val verdict = AdaptiveEngine.evaluate(
            stats = SubjectStats(),
            currentLevel = 3,
            subjectId = "dsa",
        )

        assertEquals(0, verdict.answered)
        assertEquals(LevelDirection.HOLD, verdict.direction)
        assertEquals(3, verdict.toLevel)
    }

    @Test
    fun `thresholds are stated constants`() {
        assertEquals(20, AdaptiveEngine.WINDOW)
        assertEquals(10, AdaptiveEngine.MIN_ANSWERS)
        assertEquals(0.8f, AdaptiveEngine.PROMOTE_AT, 0.001f)
        assertEquals(0.5f, AdaptiveEngine.DEMOTE_BELOW, 0.001f)
    }
}
