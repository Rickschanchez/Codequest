package com.codequest.app.ui.home

import androidx.lifecycle.ViewModel
import com.codequest.app.data.ServiceLocator
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.UserProgress
import kotlinx.coroutines.flow.StateFlow

/** Backs the Home dashboard: player stats, continue learning and daily challenge. */
class HomeViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    val userProgress: StateFlow<UserProgress> = repository.userProgress

    val topics: StateFlow<List<DsaTopic>> = repository.topics

    /** Chosen subjects and their levels — what makes the dashboard personal. */
    val learningProfile = repository.learningProfile

    /** Claims the daily challenge reward. Safe to call repeatedly. */
    fun claimDailyChallenge() {
        repository.claimDailyQuest()
    }
}
