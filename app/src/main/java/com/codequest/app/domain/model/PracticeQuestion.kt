package com.codequest.app.domain.model

/** Difficulty bands used across the practice system. */
enum class Difficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
}

/**
 * Question formats across the ten-level progression.
 *
 * Levels 1-5 stay conceptual; 6-10 move into code reading, completion,
 * debugging, implementation and optimisation.
 */
enum class QuestionType(val label: String) {
    CONCEPT_MCQ("Concept MCQ"),
    OUTPUT_PREDICTION("Output Prediction"),
    COMPLEXITY("Complexity"),
    DEBUGGING("Debugging"),
    PROBLEM_SOLVING("Problem Solving"),
    CODE_READING("Code Reading"),
    CODE_COMPLETION("Code Completion"),
    IMPLEMENTATION("Implementation"),
    OPTIMIZATION("Optimisation"),
}

/** One test case a coding challenge is judged against. */
data class TestCase(
    val input: String,
    val expectedOutput: String,
    val isHidden: Boolean = false,
)

/**
 * Everything a coding challenge needs beyond an MCQ.
 *
 * The shape is deliberately execution-agnostic: a future engine can consume
 * [testCases] and report back without the UI changing.
 */
data class CodingSpec(
    val language: String = "cpp",
    val problemStatement: String,
    val starterCode: String,
    val constraints: List<String>,
    val expectedBehavior: String,
    val testCases: List<TestCase>,
    val timeComplexityTarget: String,
    val spaceComplexityTarget: String,
    val hiddenTestCount: Int = 0,
) {
    val visibleTestCases: List<TestCase> get() = testCases.filterNot { it.isHidden }
}

/**
 * A practice question.
 *
 * @param level 1..10 — the rung of the learning progression this sits on.
 * @param correctIndex index into [options].
 * @param explanation shown after the answer is submitted.
 * @param xpReward awarded the first time the question is solved correctly.
 * @param coding populated only for challenges that leave the MCQ format.
 */
data class PracticeQuestion(
    val id: String,
    val topicId: String,
    val level: Int = 1,
    val type: QuestionType,
    val difficulty: Difficulty,
    val question: String,
    val code: String? = null,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val xpReward: Int,
    val coding: CodingSpec? = null,
) {
    val correctAnswer: String
        get() = options.getOrNull(correctIndex).orEmpty()

    /** True when the question can be answered from the option list alone. */
    val isSelectable: Boolean
        get() = coding == null && options.isNotEmpty() && correctIndex in options.indices
}
