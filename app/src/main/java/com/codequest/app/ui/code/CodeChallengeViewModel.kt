package com.codequest.app.ui.code

import androidx.lifecycle.ViewModel
import com.codequest.app.data.ServiceLocator
import com.codequest.app.domain.code.CodeExecutor
import com.codequest.app.domain.code.ExecutionResult
import com.codequest.app.domain.model.CodingChallenge
import com.codequest.app.domain.model.DsaTopic
import com.codequest.app.domain.model.LearningProfile
import kotlinx.coroutines.flow.StateFlow

/** Backs the coding challenge screen: the ramp, the lock state and the run. */
class CodeChallengeViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    val challenges: StateFlow<List<CodingChallenge>> = repository.codingChallenges

    val profile: StateFlow<LearningProfile> = repository.learningProfile

    val topics: StateFlow<List<DsaTopic>> = repository.topics

    /**
     * The one call the screen makes when RUN is pressed. It never fabricates a
     * verdict — see [CodeExecutor] for why.
     */
    fun run(challenge: CodingChallenge, code: String): ExecutionResult =
        CodeExecutor.execute(code, challenge.spec)
}
