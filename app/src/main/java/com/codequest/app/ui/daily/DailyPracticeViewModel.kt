package com.codequest.app.ui.daily

import androidx.lifecycle.ViewModel
import com.codequest.app.data.ServiceLocator
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.LearningProfile
import com.codequest.app.domain.model.PracticeQuestion
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Backs today's pinned practice set: the ten questions, progress and level. */
class DailyPracticeViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    /**
     * Today's set, resolved once per screen. The repository pins the ids to
     * the calendar day, so a level change mid-day cannot reshuffle them.
     */
    val questions: List<PracticeQuestion> = repository.dailyQuestions()

    val solvedQuestionIds: StateFlow<Set<String>> = repository.solvedQuestionIds

    val profile: StateFlow<LearningProfile> = repository.learningProfile

    val topics: StateFlow<List<DsaTopic>> = repository.topics

    /** Readable date for the header, e.g. “Wed, 7 Oct”. */
    val dayLabel: String = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        .format(Date())

    /** The first question in the set that is still unsolved, else the first. */
    fun nextQuestion(solvedIds: Set<String>): String? =
        questions.firstOrNull { it.id !in solvedIds }?.id ?: questions.firstOrNull()?.id
}
