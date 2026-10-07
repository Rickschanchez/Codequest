package com.codequest.app.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.codequest.app.data.ServiceLocator
import com.codequest.app.domain.model.KnowledgeBand
import com.codequest.app.domain.model.Subjects

/** Backs onboarding: subject choice, honest self-assessment, then the hand-off. */
class OnboardingViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    /** Chosen subject ids, in the order the learner tapped them. */
    var selectedSubjects by mutableStateOf<List<String>>(emptyList())
        private set

    /** The self-assessed band; deliberately null until the learner chooses. */
    var band by mutableStateOf<KnowledgeBand?>(null)
        private set

    val canContinue: Boolean
        get() = selectedSubjects.isNotEmpty()

    val canFinish: Boolean
        get() = canContinue && band != null

    fun toggleSubject(subjectId: String) {
        selectedSubjects = if (subjectId in selectedSubjects) {
            selectedSubjects - subjectId
        } else {
            selectedSubjects + subjectId
        }
    }

    fun chooseBand(value: KnowledgeBand) {
        band = value
    }

    /** Hands the choices to the repository, which persists them. */
    fun startPath() {
        val chosen = band ?: return
        if (selectedSubjects.isEmpty()) return

        repository.completeOnboarding(
            selectedSubjects = selectedSubjects.filter { Subjects.find(it) != null },
            initialLevels = selectedSubjects.associateWith { chosen.level },
        )
    }
}
