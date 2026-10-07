package com.codequest.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.codequest.app.data.auth.DemoSession
import com.codequest.app.data.local.LearningStore
import com.codequest.app.ui.CodeQuestApp
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestTheme
import com.codequest.app.ui.theme.ThemeMode
import com.codequest.app.ui.theme.ThemeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ThemeState.initialize(this)
        // Restore the offline presentation session before the gate composes.
        DemoSession.initialize(this)
        // Onboarding choices, levels and the daily set live on disk.
        LearningStore.initialize(this)
        // Apply the persisted appearance before the first frame to avoid a flash.
        val nightMask = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val systemDark = nightMask == Configuration.UI_MODE_NIGHT_YES
        CodeQuestColors.setBlendImmediate(
            when (ThemeState.mode) {
                ThemeMode.DARK -> 1f
                ThemeMode.LIGHT -> 0f
                ThemeMode.SYSTEM -> if (systemDark) 1f else 0f
            },
        )

        setContent {
            CodeQuestTheme {
                CodeQuestApp()
            }
        }
    }
}
