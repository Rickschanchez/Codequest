package com.codequest.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.codequest.app.ui.home.HomeScreen
import com.codequest.app.ui.code.CodeChallengeScreen
import com.codequest.app.ui.daily.DailyPracticeScreen
import com.codequest.app.ui.learn.LearnScreen
import com.codequest.app.ui.learn.TopicScreen
import com.codequest.app.ui.practice.PracticeScreen
import com.codequest.app.ui.practice.QuestionScreen
import com.codequest.app.ui.profile.ProfileScreen

/**
 * Graph for the whole app. The app always launches on [CodeQuestDestination.HOME].
 */
@Composable
fun CodeQuestNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    fun switchTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = route != CodeQuestDestination.HOME.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = CodeQuestDestination.HOME.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(tween(220)) + slideInHorizontally(tween(240)) { it / 10 }
        },
        exitTransition = {
            fadeOut(tween(160))
        },
        popEnterTransition = {
            fadeIn(tween(220))
        },
        popExitTransition = {
            fadeOut(tween(160)) + slideOutHorizontally(tween(240)) { it / 10 }
        },
    ) {
        composable(CodeQuestDestination.HOME.route) {
            HomeScreen(
                onOpenTopic = { navController.navigate(CodeQuestRoutes.topic(it)) },
                onOpenDailyChallenge = { navController.navigate(CodeQuestRoutes.DAILY) },
                onOpenRoadmap = { switchTab(CodeQuestDestination.LEARN.route) },
                onOpenPractice = { switchTab(CodeQuestDestination.PRACTICE.route) },
            )
        }
        composable(CodeQuestDestination.LEARN.route) {
            LearnScreen(
                onOpenTopic = { navController.navigate(CodeQuestRoutes.topic(it)) },
            )
        }
        composable(
            route = CodeQuestRoutes.PRACTICE_FILTERED,
            arguments = listOf(navArgument("topicId") { defaultValue = "" }),
        ) { entry ->
            PracticeScreen(
                topicFilter = entry.arguments?.getString("topicId")
                    ?.takeIf { it.isNotBlank() },
                onOpenQuestion = { navController.navigate(CodeQuestRoutes.question(it)) },
                onOpenDaily = { navController.navigate(CodeQuestRoutes.DAILY) },
                onOpenChallenge = { navController.navigate(CodeQuestRoutes.challenge(it)) },
            )
        }
        composable(CodeQuestDestination.PROFILE.route) {
            ProfileScreen()
        }
        composable(CodeQuestRoutes.DAILY) {
            DailyPracticeScreen(
                onBack = { navController.popBackStack() },
                onOpenQuestion = { navController.navigate(CodeQuestRoutes.question(it)) },
            )
        }
        composable(
            route = CodeQuestRoutes.CHALLENGE,
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType }),
        ) { entry ->
            CodeChallengeScreen(
                challengeId = entry.arguments?.getString("challengeId").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = CodeQuestRoutes.TOPIC,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType }),
        ) { entry ->
            val topicId = entry.arguments?.getString("topicId").orEmpty()
            TopicScreen(
                topicId = topicId,
                onBack = { navController.popBackStack() },
                onPractice = { navController.navigate(CodeQuestRoutes.practiceForTopic(topicId)) },
            )
        }
        composable(
            route = CodeQuestRoutes.QUESTION,
            arguments = listOf(navArgument("questionId") { type = NavType.StringType }),
        ) { entry ->
            QuestionScreen(
                questionId = entry.arguments?.getString("questionId").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }
    }
}
