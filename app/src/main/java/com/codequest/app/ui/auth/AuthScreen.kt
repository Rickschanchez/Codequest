package com.codequest.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.app.ui.components.GlassButton
import com.codequest.app.ui.components.GlassButtonVariant
import com.codequest.app.ui.components.GlowCard
import com.codequest.app.ui.theme.CodeQuestColors
import com.codequest.app.ui.theme.CodeQuestShapes
import com.codequest.app.ui.theme.CodeQuestTypography

/**
 * Gate screen: Email/Password and Google sign-in before the roadmap.
 *
 * Google uses Credential Manager with a Google ID credential (not the legacy
 * GoogleSignInClient), so the flow works with or without Google Play services
 * UI and hands Firebase a standard ID token.
 */
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    fun submit() {
        focusManager.clearFocus()
        viewModel.submit()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CodeQuestColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ----------------------------------------------------------- brand
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(CodeQuestShapes.iconBox))
                    .background(CodeQuestColors.Accent),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "CQ",
                    style = CodeQuestTypography.labelLarge,
                    color = CodeQuestColors.OnAccent,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(11.dp))
            Text(
                text = "CODEQUEST",
                style = CodeQuestTypography.titleMedium,
                color = CodeQuestColors.TextPrimary,
                letterSpacing = 2.sp,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = if (state.isCreate) "CREATE ACCOUNT" else "WELCOME BACK",
                style = CodeQuestTypography.headlineMedium,
                color = CodeQuestColors.TextPrimary,
            )
            Text(
                text = if (state.isCreate) {
                    "Start your DSA roadmap — progress begins at lesson one."
                } else {
                    "Master Data Structures & Algorithms, one deliberate session at a time."
                },
                style = CodeQuestTypography.bodyMedium,
                color = CodeQuestColors.TextSecondary,
            )
        }

        if (!viewModel.isConfigured) {
            ConfigNoticeCard()
        }

        // ------------------------------------------------------ credentials
        GlowCard(accent = CodeQuestColors.Accent) {
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                if (state.isCreate) {
                    AuthTextField(
                        label = "NAME",
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        placeholder = "Shekhar",
                        leadingIcon = Icons.Filled.Person,
                        imeAction = ImeAction.Next,
                    )
                }
                AuthTextField(
                    label = "EMAIL",
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    placeholder = "you@example.com",
                    leadingIcon = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                )
                AuthTextField(
                    label = "PASSWORD",
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    placeholder = "At least 6 characters",
                    leadingIcon = Icons.Filled.Lock,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    passwordVisible = passwordVisible,
                    trailingIcon = {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = CodeQuestColors.TextSecondary,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { passwordVisible = !passwordVisible },
                        )
                    },
                    onImeAction = { submit() },
                )

                state.error?.let { message ->
                    Text(
                        text = message,
                        style = CodeQuestTypography.bodySmall,
                        color = CodeQuestColors.Rose,
                    )
                }

                GlassButton(
                    onClick = { submit() },
                    enabled = !state.isLoading && viewModel.isConfigured,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        vertical = 14.dp,
                        horizontal = 18.dp,
                    ),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = CodeQuestColors.OnAccent,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = if (state.isCreate) "CREATE ACCOUNT" else "SIGN IN",
                            style = CodeQuestTypography.labelLarge,
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------- or divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(CodeQuestColors.Outline),
            )
            Text(
                text = "  OR  ",
                style = CodeQuestTypography.labelSmall,
                color = CodeQuestColors.TextTertiary,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(CodeQuestColors.Outline),
            )
        }

        // ------------------------------------------------------------ google
        GlassButton(
            onClick = {
                focusManager.clearFocus()
                viewModel.signInWithGoogle(context)
            },
            enabled = !state.isLoading && viewModel.isConfigured,
            variant = GlassButtonVariant.SECONDARY,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                vertical = 14.dp,
                horizontal = 18.dp,
            ),
        ) {
            GoogleGlyph()
            Spacer(Modifier.width(10.dp))
            Text(
                text = "CONTINUE WITH GOOGLE",
                style = CodeQuestTypography.labelLarge,
            )
        }

        // -------------------------------------------------------- mode switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (state.isCreate) {
                    "Already have an account? "
                } else {
                    "New to CodeQuest? "
                },
                style = CodeQuestTypography.bodyMedium,
                color = CodeQuestColors.TextSecondary,
            )
            Text(
                text = if (state.isCreate) "Sign in" else "Create one",
                style = CodeQuestTypography.bodyMedium,
                color = CodeQuestColors.Accent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(onClick = viewModel::toggleMode)
                    .padding(2.dp),
            )
        }

        // ------------------------------------------------------ demo / offline
        DemoEntry(
            enabled = !state.isLoading,
            onContinue = {
                focusManager.clearFocus()
                viewModel.continueAsDemo()
            },
        )
    }
}

/**
 * Clearly labelled offline path for presentations.
 *
 * It never reports a Firebase or Google success — it enters a local, seeded
 * prototype learner so the roadmap is reachable with no network and no
 * credentials.
 */
@Composable
private fun DemoEntry(enabled: Boolean, onContinue: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(CodeQuestColors.Outline),
            )
            Text(
                text = "  PRESENTATION  ",
                style = CodeQuestTypography.labelSmall,
                color = CodeQuestColors.TextTertiary,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(CodeQuestColors.Outline),
            )
        }

        GlassButton(
            onClick = onContinue,
            enabled = enabled,
            variant = GlassButtonVariant.SECONDARY,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 18.dp),
        ) {
            Text(
                text = "CONTINUE AS DEMO STUDENT",
                style = CodeQuestTypography.labelLarge,
            )
        }

        Text(
            text = "Offline prototype account with seeded progress. " +
                "No network, no Firebase and no Google sign-in.",
            style = CodeQuestTypography.bodySmall,
            color = CodeQuestColors.TextTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Readable stand-in until the official Google G asset is added. */
@Composable
private fun GoogleGlyph() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "G",
            style = CodeQuestTypography.labelLarge,
            color = Color(0xFF4285F4),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ConfigNoticeCard() {
    GlowCard(accent = CodeQuestColors.Gold) {
        Text(
            text = "FIREBASE NOT CONFIGURED",
            style = CodeQuestTypography.labelMedium,
            color = CodeQuestColors.Gold,
            letterSpacing = 0.8.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Drop the console-generated google-services.json into the app module to enable " +
                "Email and Google sign-in.",
            style = CodeQuestTypography.bodySmall,
            color = CodeQuestColors.TextSecondary,
        )
    }
}

/**
 * Labelled field styled with the app's own surfaces rather than Material's
 * outlined defaults, so it matches the editorial dark theme exactly.
 */
@Composable
private fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    passwordVisible: Boolean = false,
    trailingIcon: (@Composable () -> Unit)? = null,
    onImeAction: (() -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Text(
            text = label,
            style = CodeQuestTypography.labelMedium,
            color = if (focused) CodeQuestColors.Accent else CodeQuestColors.TextSecondary,
            letterSpacing = 0.8.sp,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CodeQuestShapes.cardSmall))
                .background(CodeQuestColors.SurfaceMuted)
                .border(
                    width = 1.dp,
                    color = if (focused) CodeQuestColors.Accent else CodeQuestColors.Outline,
                    shape = RoundedCornerShape(CodeQuestShapes.cardSmall),
                )
                .onFocusChanged { focused = it.isFocused }
                .padding(horizontal = 13.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (focused) CodeQuestColors.Accent else CodeQuestColors.TextTertiary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(10.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = CodeQuestTypography.bodyMedium,
                        color = CodeQuestColors.TextTertiary,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = CodeQuestTypography.bodyMedium.copy(
                        color = CodeQuestColors.TextPrimary,
                    ),
                    cursorBrush = SolidColor(CodeQuestColors.Accent),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else if (keyboardType == KeyboardType.Password) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            if (onImeAction != null) {
                                onImeAction()
                            } else {
                                defaultKeyboardAction(ImeAction.Next)
                            }
                        },
                        onDone = {
                            if (onImeAction != null) {
                                onImeAction()
                            } else {
                                defaultKeyboardAction(ImeAction.Done)
                            }
                        },
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focused = it.isFocused },
                )
            }
            if (trailingIcon != null) {
                Spacer(Modifier.width(8.dp))
                trailingIcon()
            }
        }
    }
}

