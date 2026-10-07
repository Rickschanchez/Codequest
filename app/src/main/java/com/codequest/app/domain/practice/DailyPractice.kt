package com.codequest.app.domain.practice

import com.codequest.app.domain.model.PracticeQuestion
import java.util.Calendar

/**
 * The daily practice set: ten questions drawn from the learner's current
 * level across every roadmap topic.
 *
 * Selection is a pure function of the calendar day and the question ids, so
 * the same day always shows the same ten questions in the same order — on any
 * device and any run — while the next day rotates them. No randomness, no
 * server: the rotation *is* the date.
 */
object DailyPractice {

    /** How many questions make up one day's set. */
    const val QUESTIONS_PER_DAY = 10

    /** Device-local calendar key for [millis], formatted `yyyy-MM-dd`. */
    fun dayKey(millis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }

    /** Device-local key for today. */
    fun todayKey(): String = dayKey(System.currentTimeMillis())

    /**
     * Picks the day's set.
     *
     * Candidates are the selectable questions at [level]; the pool falls back
     * to the whole bank only if that level holds fewer than [QUESTIONS_PER_DAY]
     * questions. Each candidate is ordered by a hash of the day and its id, so
     * the day pins the set and the level still decides who sees what.
     */
    fun pick(
        pool: List<PracticeQuestion>,
        level: Int,
        dayKey: String,
    ): List<PracticeQuestion> {
        val atLevel = pool.filter { it.level == level && it.isSelectable }
        val candidates = if (atLevel.size >= QUESTIONS_PER_DAY) {
            atLevel
        } else {
            pool.filter { it.isSelectable }
        }

        return candidates
            .sortedWith(compareBy({ rotation(dayKey, it.id) }, { it.id }))
            .take(QUESTIONS_PER_DAY)
    }

    /** Where one question sits in the order for [dayKey]. */
    private fun rotation(dayKey: String, questionId: String): Long {
        var hash = 0xcbf29ce484222325UL.toLong()
        for (ch in "$dayKey#$questionId") {
            hash = hash xor ch.code.toLong()
            hash *= 0x100000001b3L
        }
        return hash and Long.MAX_VALUE
    }
}
