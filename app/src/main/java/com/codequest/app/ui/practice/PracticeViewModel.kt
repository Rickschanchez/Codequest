package com.codequest.app.ui.practice

import androidx.lifecycle.ViewModel
import com.codequest.app.data.ServiceLocator
import com.codequest.app.domain.model.Difficulty
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.PracticeQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Backs the Practice screen: question browsing plus topic/difficulty/status filters. */
class PracticeViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    val questions: StateFlow<List<PracticeQuestion>> = repository.questions

    val topics: StateFlow<List<DsaTopic>> = repository.topics

    val solvedQuestionIds: StateFlow<Set<String>> = repository.solvedQuestionIds

    /** Per-subject levels, so the daily banner can name the level it drew from. */
    val learningProfile = repository.learningProfile

    /** Today's pinned ten, mirrored here for the banner at the top of the list. */
    val dailyQuestions: List<PracticeQuestion> = repository.dailyQuestions()

    /** Write-code challenges that ride the same level ladder. */
    val codingChallenges = repository.codingChallenges

    private val _topicFilter = MutableStateFlow("")
    private val _difficultyFilter = MutableStateFlow<Difficulty?>(null)
    private val _statusFilter = MutableStateFlow<Boolean?>(null)

    /** Empty string means every topic. */
    val topicFilter: StateFlow<String> = _topicFilter.asStateFlow()

    /** Null means every difficulty. */
    val difficultyFilter: StateFlow<Difficulty?> = _difficultyFilter.asStateFlow()

    /** Null means all questions, true only solved, false only unsolved. */
    val statusFilter: StateFlow<Boolean?> = _statusFilter.asStateFlow()

    fun setTopicFilter(topicId: String) {
        _topicFilter.value = topicId
    }

    fun setDifficultyFilter(difficulty: Difficulty?) {
        _difficultyFilter.value = difficulty
    }

    fun setStatusFilter(solved: Boolean?) {
        _statusFilter.value = solved
    }
}
