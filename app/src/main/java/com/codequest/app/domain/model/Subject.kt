package com.codequest.app.domain.model

/**
 * A subject a learner can follow.
 *
 * Only DSA ships a full curriculum in this build; the remaining subjects are
 * tracked through onboarding, their own level and the learning path. Nothing
 * pretends content exists where it does not.
 */
data class Subject(
    val id: String,
    val name: String,
    val emoji: String,
    val blurb: String,
    val hasCurriculum: Boolean = false,
) {
    /** A short uppercase label for chips and card headers. */
    val shortLabel: String
        get() = name.uppercase()
}

object Subjects {

    val all: List<Subject> = listOf(
        Subject("dsa", "DSA", "\uD83E\uDDED", "Data structures, algorithms and complexity", hasCurriculum = true),
        Subject("sql", "SQL", "\uD83D\uDDC4\uFE0F", "Queries, joins and relational thinking"),
        Subject("python", "Python", "\uD83D\uDC0D", "Syntax, data types and scripting"),
        Subject("java", "Java", "☕", "OOP, collections and the JVM"),
        Subject("cpp", "C++", "⚙️", "Memory, pointers and performance"),
        Subject("dbms", "DBMS", "\uD83D\uDCBE", "Normalisation, transactions and schema"),
        Subject("os", "Operating Systems", "\uD83D\uDDA5\uFE0F", "Processes, memory and scheduling"),
        Subject("cn", "Computer Networks", "\uD83C\uDF10", "Protocols, layers and routing"),
        Subject("ai", "AI / ML", "\uD83E\uDD16", "Models, data and evaluation"),
        Subject("web", "Web Development", "\uD83C\uDF10", "HTML, CSS, APIs and requests"),
    )

    fun find(id: String): Subject? = all.firstOrNull { it.id == id }

    fun nameOf(id: String): String = find(id)?.name ?: id

    /** The subject the learning path leads with: DSA whenever it was chosen. */
    fun primaryOf(selected: List<String>): Subject? =
        selected.firstOrNull { it == "dsa" }?.let(::find) ?: selected.firstNotNullOfOrNull(::find)
}
