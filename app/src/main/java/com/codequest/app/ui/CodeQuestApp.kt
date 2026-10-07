package com.codequest.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codequest.app.data.ServiceLocator
import com.codequest.app.navigation.CodeQuestDestination
import com.codequest.app.navigation.CodeQuestNavHost
import com.codequest.app.ui.auth.AuthScreen
import com.codequest.app.ui.components.GlassBottomNavigation
import com.codequest.app.ui.onboarding.OnboardingScreen
import com.codequest.app.ui.theme.CodeQuestColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Root of the app: solid editorial background + anchored bottom navigation.
 *
 * Two gates compose in order: no Firebase user means the auth screen is the
 * whole tree; a user who has never onboarded sees the personalisation flow.
 * Only both cleared does the roadmap appear.
 */
@Composable
fun CodeQuestApp() {
    val currentUser by ServiceLocator.authRepository.currentUser
        .collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(CodeQuestColors.Background)) {
        when {
            currentUser == null -> AuthScreen()
            else -> {
                // Collect only the gate boolean: profile updates fire on every
                // answer, and the root tree must not recompose for each one.
                val repository = ServiceLocator.repository
                val onboardingCompleted by remember(repository) {
                    repository.learningProfile
                        .map { it.onboardingCompleted }
                        .distinctUntilChanged()
                }.collectAsStateWithLifecycle(
                    initialValue = repository.learningProfile.value.onboardingCompleted,
                )
                if (onboardingCompleted) {
                    MainScaffold()
                } else {
                    OnboardingScreen()
                }
            }
        }
    }
}

@Composable
private fun MainScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    fun switchTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            // Home must never restore a stale pushed stack (e.g. Topic),
            // otherwise the dashboard never shows.
            restoreState = route != CodeQuestDestination.HOME.route
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Box(modifier = Modifier.navigationBarsPadding().padding(bottom = 10.dp)) {
                GlassBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = ::switchTab,
                )
            }
        },
    ) { innerPadding ->
        CodeQuestNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
