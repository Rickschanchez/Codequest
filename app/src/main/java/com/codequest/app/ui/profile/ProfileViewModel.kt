package com.codequest.app.ui.profile

import androidx.lifecycle.ViewModel
import com.codequest.app.data.ServiceLocator
import com.codequest.app.data.auth.AuthUser
import com.codequest.app.domain.model.Achievement
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.LearningProfile
import com.codequest.app.domain.model.UserProgress
import kotlinx.coroutines.flow.StateFlow

/** Backs the Profile screen: DSA statistics, roadmap state and achievements. */
class ProfileViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    val userProgress: StateFlow<UserProgress> = repository.userProgress

    val achievements: StateFlow<List<Achievement>> = repository.achievements

    val topics: StateFlow<List<DsaTopic>> = repository.topics

    /** Chosen subjects and their per-subject levels, straight from onboarding. */
    val learningProfile: StateFlow<LearningProfile> = repository.learningProfile

    /** Firebase identity backing this learner. */
    val currentUser: StateFlow<AuthUser?> = ServiceLocator.authRepository.currentUser

    fun signOut() = ServiceLocator.authRepository.signOut()
}
