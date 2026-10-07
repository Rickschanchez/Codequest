package com.codequest.app.ui.learn

import androidx.lifecycle.ViewModel
import com.codequest.app.data.ServiceLocator
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.Lesson
import com.codequest.app.domain.model.PracticeQuestion
import kotlinx.coroutines.flow.StateFlow

/** Backs the Topic detail screen: lessons, practice counts and completion. */
class TopicViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    val topics: StateFlow<List<DsaTopic>> = repository.topics

    val lessons: StateFlow<List<Lesson>> = repository.lessons

    val questions: StateFlow<List<PracticeQuestion>> = repository.questions

    val solvedQuestionIds: StateFlow<Set<String>> = repository.solvedQuestionIds

    /** Marks a lesson completed and awards its XP once. */
    fun completeLesson(lessonId: String) {
        repository.completeLesson(lessonId)
    }
}
