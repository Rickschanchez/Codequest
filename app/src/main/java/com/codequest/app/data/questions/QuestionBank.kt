package com.codequest.app.data.questions

import com.codequest.app.domain.model.Difficulty
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.domain.model.QuestionType

/**
 * One authored aspect of a topic.
 *
 * A theme is written once and expands into the ten rungs of the progression —
 * define it (1), recognise it (2), apply it (3), trace it (4), analyse it (5),
 * read the code (6), complete it (7), debug it (8), cover its edge case (9)
 * and optimise it (10). Everything that can be shared between rungs is: the
 * distractors for one rung are the correct answers of its neighbours, so no
 * option is ever a throwaway.
 *
 * @property key stable fragment used to build question ids.
 * @property head statements before the gap, [line] the single line that fills it,
 * [tail] the code that follows — so the same material reads, traces and completes.
 * @property outs first entry is the real output, the rest are plausible misses.
 */
internal data class Theme(
    val key: String,
    val label: String,
    val definition: String,
    val essence: String,
    val scenario: String,
    val applyWhy: String,
    val head: String,
    val line: String,
    val tail: String,
    val outs: List<String>,
    val outputWhy: String,
    val operation: String,
    val complexity: String,
    val complexityWhy: String,
    val read: String,
    val readWhy: String,
    val blankWhy: String,
    val bug: String,
    val bugWhy: String,
    val edge: String,
    val edgeWhy: String,
    val opt: String,
    val optWhy: String,
) {
    val code: String get() = "$head\n$line\n$tail"
    val blankCode: String get() = "$head\n___\n$tail"
}

/** Positional constructor — keeps the topic files dense and readable. */
internal fun theme(
    key: String,
    label: String,
    definition: String,
    essence: String,
    scenario: String,
    applyWhy: String,
    head: String,
    line: String,
    tail: String,
    outs: List<String>,
    outputWhy: String,
    operation: String,
    complexity: String,
    complexityWhy: String,
    read: String,
    readWhy: String,
    blankWhy: String,
    bug: String,
    bugWhy: String,
    edge: String,
    edgeWhy: String,
    opt: String,
    optWhy: String,
): Theme = Theme(
    key = key,
    label = label,
    definition = definition,
    essence = essence,
    scenario = scenario,
    applyWhy = applyWhy,
    head = head,
    line = line,
    tail = tail,
    outs = outs,
    outputWhy = outputWhy,
    operation = operation,
    complexity = complexity,
    complexityWhy = complexityWhy,
    read = read,
    readWhy = readWhy,
    blankWhy = blankWhy,
    bug = bug,
    bugWhy = bugWhy,
    edge = edge,
    edgeWhy = edgeWhy,
    opt = opt,
    optWhy = optWhy,
)

/** One roadmap stop backing its slice of the question bank. */
internal data class BankTopic(
    val id: String,
    val name: String,
    val order: Int,
    val themes: List<Theme>,
)

/**
 * The DSA question bank.
 *
 * Built once per process from the authored theme files under this package.
 * Every level of every topic is represented and every id, option list and
 * explanation is validated by `QuestionBankTest`.
 */
object QuestionBank {

    /** Complexity vocabulary shared by every level-5 question. */
    internal val complexityPool = listOf(
        "O(1)",
        "O(log n)",
        "O(n)",
        "O(n log n)",
        "O(n²)",
        "O(n³)",
        "O(2ⁿ)",
    )

    private val bank: List<BankTopic> = listOf(
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

    /** Roadmap order, mirrored from the topic blueprints on the Learn screen. */
    val topicOrder: Map<String, Int> = bank.associate { it.id to it.order }

    val topicNames: Map<String, String> = bank.associate { it.id to it.name }

    /** Every question, ordered topic → level → key. */
    val all: List<PracticeQuestion> by lazy {
        bank.flatMap { expand(it) }.sortedWith(
            compareBy(
                { topicOrder[it.topicId] ?: Int.MAX_VALUE },
                { it.level },
                { it.id },
            ),
        )
    }

    /** Questions at one rung of the progression, across every topic. */
    fun atLevel(level: Int): List<PracticeQuestion> = all.filter { it.level == level }

    // --------------------------------------------------------------- expansion

    private fun expand(topic: BankTopic): List<PracticeQuestion> {
        val themes = topic.themes
        val definitions = themes.map { it.definition }
        val labels = themes.map { it.label }
        val reads = themes.map { it.read }
        val lines = themes.map { it.line }
        val edges = themes.map { it.edge }
        val opts = themes.map { it.opt }

        return themes.flatMapIndexed { index, theme ->
            val ids = (1..10).associateWith { level -> topic.id + "-l" + level + "-" + theme.key }

            listOf(
                PracticeQuestion(
                    id = ids.getValue(1),
                    topicId = topic.id,
                    level = 1,
                    type = QuestionType.CONCEPT_MCQ,
                    difficulty = Difficulty.EASY,
                    question = "Which statement correctly defines ${theme.label}?",
                    options = optionSet(ids.getValue(1), theme.definition, definitions, index),
                    correctIndex = correctSlot(ids.getValue(1), theme.definition, definitions, index),
                    explanation = theme.essence,
                    xpReward = xpForLevel(1),
                ),
                PracticeQuestion(
                    id = ids.getValue(2),
                    topicId = topic.id,
                    level = 2,
                    type = QuestionType.CONCEPT_MCQ,
                    difficulty = Difficulty.EASY,
                    question = "Which concept is described as \u201C${theme.definition}\u201D?",
                    options = optionSet(ids.getValue(2), theme.label, labels, index),
                    correctIndex = correctSlot(ids.getValue(2), theme.label, labels, index),
                    explanation = theme.essence,
                    xpReward = xpForLevel(2),
                ),
                PracticeQuestion(
                    id = ids.getValue(3),
                    topicId = topic.id,
                    level = 3,
                    type = QuestionType.PROBLEM_SOLVING,
                    difficulty = Difficulty.EASY,
                    question = theme.scenario,
                    options = optionSet(ids.getValue(3), theme.label, labels, index),
                    correctIndex = correctSlot(ids.getValue(3), theme.label, labels, index),
                    explanation = theme.applyWhy,
                    xpReward = xpForLevel(3),
                ),
                PracticeQuestion(
                    id = ids.getValue(4),
                    topicId = topic.id,
                    level = 4,
                    type = QuestionType.OUTPUT_PREDICTION,
                    difficulty = Difficulty.MEDIUM,
                    question = "What does this code output?",
                    code = theme.code,
                    options = rotated(theme.outs, ids.getValue(4)),
                    correctIndex = rotated(theme.outs, ids.getValue(4))
                        .indexOf(theme.outs.first()),
                    explanation = theme.outputWhy,
                    xpReward = xpForLevel(4),
                ),
                PracticeQuestion(
                    id = ids.getValue(5),
                    topicId = topic.id,
                    level = 5,
                    type = QuestionType.COMPLEXITY,
                    difficulty = Difficulty.MEDIUM,
                    question = "What is the time complexity of ${theme.operation}?",
                    options = optionSet(
                        ids.getValue(5),
                        theme.complexity,
                        complexityPool,
                        complexityPool.indexOf(theme.complexity),
                    ),
                    correctIndex = correctSlot(
                        ids.getValue(5),
                        theme.complexity,
                        complexityPool,
                        complexityPool.indexOf(theme.complexity),
                    ),
                    explanation = theme.complexityWhy,
                    xpReward = xpForLevel(5),
                ),
                PracticeQuestion(
                    id = ids.getValue(6),
                    topicId = topic.id,
                    level = 6,
                    type = QuestionType.CODE_READING,
                    difficulty = Difficulty.MEDIUM,
                    question = "Which statement correctly describes this code?",
                    code = theme.code,
                    options = optionSet(ids.getValue(6), theme.read, reads, index),
                    correctIndex = correctSlot(ids.getValue(6), theme.read, reads, index),
                    explanation = theme.readWhy,
                    xpReward = xpForLevel(6),
                ),
                PracticeQuestion(
                    id = ids.getValue(7),
                    topicId = topic.id,
                    level = 7,
                    type = QuestionType.CODE_COMPLETION,
                    difficulty = Difficulty.MEDIUM,
                    question = "Which fragment completes the code above?",
                    code = theme.blankCode,
                    options = optionSet(ids.getValue(7), theme.line, lines, index),
                    correctIndex = correctSlot(ids.getValue(7), theme.line, lines, index),
                    explanation = theme.blankWhy,
                    xpReward = xpForLevel(7),
                ),
                PracticeQuestion(
                    id = ids.getValue(8),
                    topicId = topic.id,
                    level = 8,
                    type = QuestionType.DEBUGGING,
                    difficulty = Difficulty.HARD,
                    question = "This code prints \u201C${theme.outs[1]}\u201D instead of " +
                        "\u201C${theme.outs[0]}\u201D. What causes it?",
                    code = theme.code,
                    options = optionSet(ids.getValue(8), theme.bug, bugsOf(themes), index),
                    correctIndex = correctSlot(ids.getValue(8), theme.bug, bugsOf(themes), index),
                    explanation = theme.bugWhy,
                    xpReward = xpForLevel(8),
                ),
                PracticeQuestion(
                    id = ids.getValue(9),
                    topicId = topic.id,
                    level = 9,
                    type = QuestionType.IMPLEMENTATION,
                    difficulty = Difficulty.HARD,
                    question = "Solving ${theme.label} correctly requires which edge case?",
                    options = optionSet(ids.getValue(9), theme.edge, edges, index),
                    correctIndex = correctSlot(ids.getValue(9), theme.edge, edges, index),
                    explanation = theme.edgeWhy,
                    xpReward = xpForLevel(9),
                ),
                PracticeQuestion(
                    id = ids.getValue(10),
                    topicId = topic.id,
                    level = 10,
                    type = QuestionType.OPTIMIZATION,
                    difficulty = Difficulty.HARD,
                    question = "What is the decisive optimisation for ${theme.label}?",
                    options = optionSet(ids.getValue(10), theme.opt, opts, index),
                    correctIndex = correctSlot(ids.getValue(10), theme.opt, opts, index),
                    explanation = theme.optWhy,
                    xpReward = xpForLevel(10),
                ),
            )
        }
    }

    private fun bugsOf(themes: List<Theme>): List<String> = themes.map { it.bug }

    /**
     * Four distinct options with [correct] inserted at the slot derived from
     * [id], so the answer position varies without ever being random at runtime.
     */
    private fun optionSet(
        id: String,
        correct: String,
        pool: List<String>,
        correctIndexInPool: Int,
    ): List<String> {
        val others = pool.filterIndexed { i, _ -> i != correctIndexInPool }
        val start = (stableHash(id + "#start") % others.size).toInt()
        val slot = (stableHash(id + "#slot") % 4).toInt()
        val distractors = (0 until 3).map { others[(start + it) % others.size] }
        return distractors.toMutableList().also { it.add(slot, correct) }
    }

    private fun correctSlot(
        id: String,
        correct: String,
        pool: List<String>,
        correctIndexInPool: Int,
    ): Int = optionSet(id, correct, pool, correctIndexInPool).indexOf(correct)

    /** Deterministic rotation — same id, same order, on every device and run. */
    private fun rotated(items: List<String>, id: String): List<String> {
        val shift = (stableHash(id) % items.size).toInt()
        return items.drop(shift) + items.take(shift)
    }

    /** Stable across processes and releases, unlike `String.hashCode` churn. */
    internal fun stableHash(value: String): Long {
        var hash = 0xcbf29ce484222325UL.toLong()
        for (ch in value) {
            hash = hash xor ch.code.toLong()
            hash *= 0x100000001b3L
        }
        return hash and Long.MAX_VALUE
    }

    /** XP climbs with the rung: the deeper the level, the bigger the reward. */
    fun xpForLevel(level: Int): Int = when (level) {
        1, 2, 3 -> 50
        4, 5, 6 -> 75
        7, 8 -> 100
        else -> 250
    }
}
