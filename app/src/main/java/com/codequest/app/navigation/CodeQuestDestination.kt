package com.codequest.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

/** The four top level destinations and their bottom navigation entries. */
enum class CodeQuestDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME("home", "Home", Icons.Filled.Home),
    LEARN("learn", "Learn", Icons.Filled.School),
    PRACTICE("practice", "Practice", Icons.Filled.Quiz),
    PROFILE("profile", "Profile", Icons.Filled.Person),
}

/** Extra routes outside the bottom navigation. */
object CodeQuestRoutes {
    const val TOPIC = "topic/{topicId}"
    const val QUESTION = "question/{questionId}"
    const val PRACTICE_FILTERED = "practice?topicId={topicId}"
    const val DAILY = "daily"
    const val CHALLENGE = "challenge/{challengeId}"

    fun topic(topicId: String) = "topic/$topicId"
    fun question(questionId: String) = "question/$questionId"
    fun practiceForTopic(topicId: String) = "practice?topicId=$topicId"
    fun challenge(challengeId: String) = "challenge/$challengeId"
}
