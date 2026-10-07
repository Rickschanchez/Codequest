package com.codequest.app.ui.practice

import androidx.lifecycle.ViewModel
import com.codequest.app.data.ServiceLocator
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.LearningProfile
import com.codequest.app.domain.model.PracticeQuestion
import com.codequest.app.domain.practice.AdaptiveResult
import kotlinx.coroutines.flow.StateFlow

/** Backs the single question screen: reading a question and submitting an answer. */
class QuestionViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    val questions: StateFlow<List<PracticeQuestion>> = repository.questions

    val topics: StateFlow<List<DsaTopic>> = repository.topics

    val solvedQuestionIds: StateFlow<Set<String>> = repository.solvedQuestionIds

    /** Per-subject levels, so the screen can say which level just moved. */
    val learningProfile: StateFlow<LearningProfile> = repository.learningProfile

    /** A level verdict waiting to be acknowledged, e.g. “ready for Level 7”. */
    val levelChange: StateFlow<AdaptiveResult?> = repository.levelChange

    /**
     * Submits the selected option. Returns true when the answer is correct;
     * XP and solved state are only granted the first time.
     *
     * Every submission feeds the rolling window, and the engine judges it —
     * the verdict only arrives once ten answers are in, so one question can
     * never move the level on its own.
     */
    fun submitAnswer(questionId: String, selectedIndex: Int): Boolean {
        val correct = repository.submitAnswer(questionId, selectedIndex)
        repository.evaluateProgress(SUBJECT_DSA)
        return correct
    }

    /** Acknowledges the level announcement. */
    fun dismissLevelChange() = repository.clearLevelChange()

    private companion object {
        const val SUBJECT_DSA = "dsa"
    }
}
