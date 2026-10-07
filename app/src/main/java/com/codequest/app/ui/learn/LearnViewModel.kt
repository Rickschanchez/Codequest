package com.codequest.app.ui.learn

import androidx.lifecycle.ViewModel
import com.codequest.app.data.ServiceLocator
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.UserProgress
import kotlinx.coroutines.flow.StateFlow

/** Backs the Learn screen: the DSA roadmap and overall progress. */
class LearnViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    val topics: StateFlow<List<DsaTopic>> = repository.topics

    val userProgress: StateFlow<UserProgress> = repository.userProgress
}
