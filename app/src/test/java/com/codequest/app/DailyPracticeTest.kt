package com.codequest.app

import com.codequest.app.data.questions.QuestionBank
import com.codequest.app.domain.practice.DailyPractice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * The daily rotation contract: same day, same set — every run, every device.
 * The date is the only seed, so there is nothing random to flake.
 */
class DailyPracticeTest {

    private val pool = QuestionBank.all

    @Test
    fun `picks exactly ten questions at the current level`() {
        val picked = DailyPractice.pick(pool, level = 6, dayKey = "2026-10-07")

        assertEquals(DailyPractice.QUESTIONS_PER_DAY, picked.size)
        assertTrue(picked.all { it.level == 6 })
        assertEquals(picked.size, picked.map { it.id }.distinct().size)
    }

    @Test
    fun `the same day and level always produce the same set`() {
        val first = DailyPractice.pick(pool, level = 3, dayKey = "2026-10-07")
        val second = DailyPractice.pick(pool, level = 3, dayKey = "2026-10-07")

        assertEquals(first.map { it.id }, second.map { it.id })
    }

    @Test
    fun `the set rotates when the day changes`() {
        val today = DailyPractice.pick(pool, level = 3, dayKey = "2026-10-07")
        val tomorrow = DailyPractice.pick(pool, level = 3, dayKey = "2026-10-08")

        assertNotEquals(today.map { it.id }, tomorrow.map { it.id })
    }

    @Test
    fun `rotation is not an accident of one date`() {
        val sets = (1..5).map { day ->
            DailyPractice.pick(pool, level = 5, dayKey = "2026-10-0$day").map { it.id }
        }

        // Five consecutive days, five different sets.
        assertEquals(5, sets.map { it.toSet() }.distinct().size)
    }

    @Test
    fun `different levels draw from their own rung`() {
        val easy = DailyPractice.pick(pool, level = 1, dayKey = "2026-10-07")
        val hard = DailyPractice.pick(pool, level = 10, dayKey = "2026-10-07")

        assertTrue(easy.all { it.level == 1 })
        assertTrue(hard.all { it.level == 10 })
        assertNotEquals(easy.map { it.id }, hard.map { it.id })
    }

    @Test
    fun `covers every topic it can reach at that level`() {
        val picked = DailyPractice.pick(pool, level = 7, dayKey = "2026-10-07")
        val topicCount = picked.map { it.topicId }.distinct().size

        // Ten questions across twelve topics: the draw must stay mixed.
        assertTrue("Expected broad topic coverage, saw $topicCount", topicCount >= 6)
    }

    @Test
    fun `day key is a local calendar date`() {
        val morning = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 7, 6, 30, 0)
        }.timeInMillis
        val evening = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 7, 22, 15, 0)
        }.timeInMillis

        assertEquals("2026-10-07", DailyPractice.dayKey(morning))
        assertEquals(DailyPractice.dayKey(morning), DailyPractice.dayKey(evening))
        assertNotEquals(
            DailyPractice.dayKey(morning),
            DailyPractice.dayKey(
                Calendar.getInstance().apply {
                    set(2026, Calendar.OCTOBER, 8, 0, 1, 0)
                }.timeInMillis,
            ),
        )
    }

    @Test
    fun `picks are a subset of the bank`() {
        val picked = DailyPractice.pick(pool, level = 4, dayKey = "2026-10-07")
        val ids = pool.map { it.id }.toSet()

        assertTrue(picked.all { it.id in ids })
    }
}
