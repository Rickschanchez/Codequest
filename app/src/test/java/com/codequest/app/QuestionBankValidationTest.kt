package com.codequest.app

import com.codequest.app.data.questions.ArraysQuestions
import com.codequest.app.data.questions.BankTopic
import com.codequest.app.data.questions.DynamicProgrammingQuestions
import com.codequest.app.data.questions.FoundationsQuestions
import com.codequest.app.data.questions.GraphsQuestions
import com.codequest.app.data.questions.GreedyQuestions
import com.codequest.app.data.questions.LinkedListQuestions
import com.codequest.app.data.questions.QueuesQuestions
import com.codequest.app.data.questions.QuestionBank
import com.codequest.app.data.questions.SearchingQuestions
import com.codequest.app.data.questions.SortingQuestions
import com.codequest.app.data.questions.StacksQuestions
import com.codequest.app.data.questions.StringsQuestions
import com.codequest.app.data.questions.TreesQuestions
import com.codequest.app.domain.model.Difficulty
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.domain.model.QuestionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contract for the authored question bank.
 *
 * These are structural guarantees the UI and the progression depend on:
 * coverage (twelve topics, ten levels, ten questions per topic per level,
 * at least 1,200 in total), unique ids, four distinct options with exactly
 * one correct answer, and the level → type → difficulty → XP mapping.
 *
 * Question *content* is authored, never executed — no compiler runs here.
 */
class QuestionBankValidationTest {

    private val bankTopics: List<BankTopic> = listOf(
        FoundationsQuestions.topic,
        ArraysQuestions.topic,
        StringsQuestions.topic,
        SearchingQuestions.topic,
        SortingQuestions.topic,
        LinkedListQuestions.topic,
        StacksQuestions.topic,
        QueuesQuestions.topic,
        TreesQuestions.topic,
        GraphsQuestions.topic,
        GreedyQuestions.topic,
        DynamicProgrammingQuestions.topic,
    )

    private val questions: List<PracticeQuestion> = QuestionBank.all

    @Test
    fun `twelve topics are registered in roadmap order`() {
        val expected = listOf(
            "foundations", "arrays", "strings", "searching", "sorting",
            "linked-lists", "stacks", "queues", "trees", "graphs", "greedy", "dp",
        )
        assertEquals(expected.toSet(), QuestionBank.topicOrder.keys)
        assertEquals((1..12).toList(), QuestionBank.topicOrder.values.sorted())
        assertEquals(12, QuestionBank.topicNames.size)
    }

    @Test
    fun `bank holds at least twelve hundred questions`() {
        assertTrue(
            "Expected at least 1200 questions, found ${questions.size}",
            questions.size >= 1_200,
        )
        // 12 themes in foundations + 10 in each other topic, x 10 levels.
        assertEquals(1_220, questions.size)
    }

    @Test
    fun `every topic has at least ten questions at every level`() {
        bankTopics.forEach { topic ->
            (1..10).forEach { level ->
                val count = questions.count { it.topicId == topic.id && it.level == level }
                assertTrue(
                    "Topic ${topic.id} level $level has only $count questions",
                    count >= 10,
                )
            }
        }
    }

    @Test
    fun `every theme expands into a full ten level ladder`() {
        bankTopics.forEach { topic ->
            val themes = topic.themes.size
            val expected = themes * 10
            val actual = questions.count { it.topicId == topic.id }
            assertEquals("Topic ${topic.id}", expected, actual)
        }
    }

    @Test
    fun `question ids are unique`() {
        assertEquals(questions.size, questions.map { it.id }.distinct().size)
    }

    @Test
    fun `every question has four distinct options with exactly one correct answer`() {
        questions.forEach { question ->
            val tag = question.id
            assertEquals("$tag must offer four options", 4, question.options.size)
            assertTrue("$tag has blank options", question.options.none { it.isBlank() })
            assertEquals(
                "$tag has duplicate options",
                question.options.size,
                question.options.distinct().size,
            )
            assertTrue(
                "$tag correctIndex out of range",
                question.correctIndex in question.options.indices,
            )
            assertTrue("$tag question stem is blank", question.question.isNotBlank())
            assertTrue("$tag explanation is blank", question.explanation.isNotBlank())
            assertTrue(
                "$tag correct answer is blank",
                question.options[question.correctIndex].isNotBlank(),
            )
        }
    }

    @Test
    fun `level maps to a fixed type difficulty and xp reward`() {
        questions.forEach { question ->
            val expectedType = when (question.level) {
                1, 2 -> QuestionType.CONCEPT_MCQ
                3 -> QuestionType.PROBLEM_SOLVING
                4 -> QuestionType.OUTPUT_PREDICTION
                5 -> QuestionType.COMPLEXITY
                6 -> QuestionType.CODE_READING
                7 -> QuestionType.CODE_COMPLETION
                8 -> QuestionType.DEBUGGING
                9 -> QuestionType.IMPLEMENTATION
                else -> QuestionType.OPTIMIZATION
            }
            val expectedDifficulty = when (question.level) {
                1, 2, 3 -> Difficulty.EASY
                4, 5, 6, 7 -> Difficulty.MEDIUM
                else -> Difficulty.HARD
            }
            assertEquals("${question.id} type", expectedType, question.type)
            assertEquals("${question.id} difficulty", expectedDifficulty, question.difficulty)
            assertEquals(
                "${question.id} xp",
                QuestionBank.xpForLevel(question.level),
                question.xpReward,
            )
            assertTrue("${question.id} level out of range", question.level in 1..10)
        }
    }

    @Test
    fun `code levels carry code and conceptual levels do not`() {
        questions.forEach { question ->
            when (question.level) {
                4, 6, 7, 8 -> assertTrue(
                    "${question.id} should show code",
                    !question.code.isNullOrBlank(),
                )
                else -> assertEquals(
                    "${question.id} should not show code",
                    null,
                    question.code,
                )
            }
        }
    }

    @Test
    fun `complexity answers come from the shared vocabulary`() {
        questions.filter { it.level == 5 }.forEach { question ->
            assertTrue(
                "${question.id} has unexpected complexity ${question.correctAnswer}",
                question.correctAnswer in QuestionBank.complexityPool,
            )
        }
    }

    @Test
    fun `conceptual stems are unique across the whole bank`() {
        // Levels 1, 2, 3, 5, 9 and 10 are seeded from authored text, so a
        // duplicated label, scenario, operation, edge or optimisation would
        // surface as an identical question twice.
        val stems = questions
            .filter { it.level in setOf(1, 2, 3, 5, 9, 10) }
            .map { it.question }
        assertEquals(
            "Duplicate conceptual stems found",
            stems.size,
            stems.distinct().size,
        )
    }

    @Test
    fun `authored option pools are distinct within each topic`() {
        bankTopics.forEach { topic ->
            val themes = topic.themes
            assertDistinct(topic.id, "key", themes.map { it.key })
            assertDistinct(topic.id, "label", themes.map { it.label })
            assertDistinct(topic.id, "definition", themes.map { it.definition })
            assertDistinct(topic.id, "scenario", themes.map { it.scenario })
            assertDistinct(topic.id, "line", themes.map { it.line })
            assertDistinct(topic.id, "operation", themes.map { it.operation })
            assertDistinct(topic.id, "read", themes.map { it.read })
            assertDistinct(topic.id, "bug", themes.map { it.bug })
            assertDistinct(topic.id, "edge", themes.map { it.edge })
            assertDistinct(topic.id, "opt", themes.map { it.opt })

            themes.forEach { theme ->
                assertEquals(
                    "${topic.id}/${theme.key} needs four distinct outputs",
                    4,
                    theme.outs.distinct().size,
                )
                assertTrue(
                    "${topic.id}/${theme.key} complexity ${theme.complexity} not in pool",
                    theme.complexity in QuestionBank.complexityPool,
                )
                assertTrue(
                    "${topic.id}/${theme.key} head is blank",
                    theme.head.isNotBlank(),
                )
                assertTrue(
                    "${topic.id}/${theme.key} line is blank",
                    theme.line.isNotBlank(),
                )
                assertTrue(
                    "${topic.id}/${theme.key} tail is blank",
                    theme.tail.isNotBlank(),
                )
            }
        }
    }

    @Test
    fun `xp climbs with the level`() {
        assertEquals(50, QuestionBank.xpForLevel(1))
        assertEquals(50, QuestionBank.xpForLevel(3))
        assertEquals(75, QuestionBank.xpForLevel(4))
        assertEquals(75, QuestionBank.xpForLevel(6))
        assertEquals(100, QuestionBank.xpForLevel(7))
        assertEquals(100, QuestionBank.xpForLevel(8))
        assertEquals(250, QuestionBank.xpForLevel(9))
        assertEquals(250, QuestionBank.xpForLevel(10))
    }

    private fun assertDistinct(topicId: String, field: String, values: List<String>) {
        assertEquals(
            "Duplicate $field inside topic $topicId",
            values.size,
            values.distinct().size,
        )
        assertTrue(
            "Blank $field inside topic $topicId",
            values.none { it.isBlank() },
        )
    }
}
