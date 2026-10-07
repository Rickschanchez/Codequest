package com.codequest.app.domain.code

import com.codequest.app.domain.model.CodingSpec

/**
 * The verdict of a run.
 *
 * Deliberately narrow: this build never claims a solution passed or failed,
 * because no code is ever executed. The only honest outcome is [Unavailable].
 */
sealed interface ExecutionResult {
    val headline: String
    val detail: String

    /** Nothing ran — stated plainly instead of faking a judge. */
    data class Unavailable(
        override val headline: String,
        override val detail: String,
    ) : ExecutionResult
}

/**
 * The stand-in for a code runner.
 *
 * There is no compiler, no sandbox and no judge in this prototype, so
 * [execute] refuses to fabricate a result: it reports exactly why nothing
 * happened. When a real engine lands, this is the one seam to replace —
 * the UI already consumes [ExecutionResult].
 */
object CodeExecutor {

    const val REASON = "Execution unavailable in this build"

    fun execute(code: String, spec: CodingSpec): ExecutionResult {
        if (code.isBlank()) {
            return ExecutionResult.Unavailable(
                headline = "Nothing to run",
                detail = "Type a solution in the editor first — the starter code is only a scaffold.",
            )
        }

        return ExecutionResult.Unavailable(
            headline = REASON,
            detail = "CodeQuest ships no sandboxed compiler, so no code is executed and no " +
                "pass or fail is invented. Check your ${spec.language.uppercase()} against the " +
                "${spec.testCases.size} test cases above — ${spec.visibleTestCases.size} shown and " +
                "${spec.hiddenTestCount} held back, the way a real judge hides them.",
        )
    }
}
