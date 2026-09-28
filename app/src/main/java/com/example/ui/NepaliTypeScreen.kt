package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppThemeMode
import com.example.ui.components.*
import com.example.viewmodel.NepaliTypeViewModel

@Composable
fun NepaliTypeScreen(
    viewModel: NepaliTypeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showVirtualKeyboard by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NepaliTopAppBar(
                conversionMode = uiState.conversionMode,
                currentThemeMode = uiState.settings.themeMode,
                onToggleMode = { viewModel.onToggleConversionMode() },
                onToggleTheme = {
                    val nextTheme = when (uiState.settings.themeMode) {
                        AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
                        AppThemeMode.LIGHT -> AppThemeMode.DARK
                        AppThemeMode.DARK -> AppThemeMode.SYSTEM
                    }
                    viewModel.updateSettings(uiState.settings.copy(themeMode = nextTheme))
                },
                onOpenSettings = { viewModel.setShowSettingsSheet(true) },
                onOpenInfo = { viewModel.setShowInfoDialog(true) }
            )
        },
        bottomBar = {
            Column {
                // Animated Virtual Soft Keyboard
                AnimatedVisibility(
                    visible = showVirtualKeyboard,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    VirtualKeyboard(
                        onInsertText = { viewModel.onInsertText(it) },
                        onBackspace = { viewModel.onBackspace() },
                        onOpenEmoji = { viewModel.setShowEmojiSheet(true) }
                    )
                }

                // Bottom Navigation Bar
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    NavigationBarItem(
                        selected = uiState.activeTab == 0 && !showVirtualKeyboard,
                        onClick = {
                            viewModel.setActiveTab(0)
                            showVirtualKeyboard = false
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Editor") },
                        label = { Text("Type") },
                        modifier = Modifier.testTag("tab_type")
                    )

                    NavigationBarItem(
                        selected = showVirtualKeyboard,
                        onClick = {
                            viewModel.setActiveTab(0)
                            showVirtualKeyboard = !showVirtualKeyboard
                        },
                        icon = { Icon(Icons.Default.Keyboard, contentDescription = "Nepali Keys") },
                        label = { Text(if (showVirtualKeyboard) "Hide Keys" else "Nepali Keys") },
                        modifier = Modifier.testTag("tab_keyboard")
                    )

                    NavigationBarItem(
                        selected = uiState.activeTab == 1,
                        onClick = {
                            viewModel.setActiveTab(1)
                            showVirtualKeyboard = false
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Phrases") },
                        label = { Text("Phrases") },
                        modifier = Modifier.testTag("tab_phrases")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            when (uiState.activeTab) {
                0 -> {
                    // Suggestions Bar
                    if (uiState.settings.autoCorrect) {
                        SuggestionBar(
                            suggestions = uiState.suggestions,
                            onSuggestionSelected = { viewModel.onSuggestionSelected(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Main Transliteration Editor
                    TransliterationEditor(
                        inputText = uiState.inputText,
                        outputText = uiState.outputText,
                        conversionMode = uiState.conversionMode,
                        isCopied = uiState.isCopied,
                        canUndo = uiState.canUndo,
                        canRedo = uiState.canRedo,
                        isAiLoading = uiState.isAiLoading,
                        aiMessage = uiState.aiMessage,
                        onInputChanged = { viewModel.onInputChanged(it) },
                        onCopy = { viewModel.onCopyOutput() },
                        onShare = { viewModel.onShareOutput(context) },
                        onClear = { viewModel.onClear() },
                        onUndo = { viewModel.onUndo() },
                        onRedo = { viewModel.onRedo() },
                        onAiImprove = { viewModel.onAiImprove() },
                        onInsertPunctuation = { viewModel.onInsertText(it) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quick "Try It" Mini Strip on main tab for high discoverability
                    QuickPhrasesSection(
                        onSelectPhrase = { viewModel.onPhraseSelected(it) },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                1 -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    QuickPhrasesSection(
                        onSelectPhrase = { viewModel.onPhraseSelected(it) },
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }
        }

        // Settings Bottom Sheet
        if (uiState.showSettingsSheet) {
            SettingsBottomSheet(
                settings = uiState.settings,
                onSettingsChanged = { viewModel.updateSettings(it) },
                onDismiss = { viewModel.setShowSettingsSheet(false) }
            )
        }

        // Emoji Picker Sheet
        if (uiState.showEmojiSheet) {
            EmojiPickerSheet(
                onEmojiSelected = { viewModel.onInsertText(" $it ") },
                onDismiss = { viewModel.setShowEmojiSheet(false) }
            )
        }

        // Info Dialog
        if (uiState.showInfoDialog) {
            InfoDialog(
                onDismiss = { viewModel.setShowInfoDialog(false) }
            )
        }
    }
}
