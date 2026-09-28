package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiResult
import com.example.ai.GeminiAiService
import com.example.engine.TransliterationEngine
import com.example.model.AppThemeMode
import com.example.model.ConversionMode
import com.example.model.PhraseItem
import com.example.model.TransliterationSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NepaliTypeUiState(
    val inputText: String = "",
    val outputText: String = "",
    val suggestions: List<String> = emptyList(),
    val settings: TransliterationSettings = TransliterationSettings(),
    val conversionMode: ConversionMode = ConversionMode.ROMAN_TO_NEPALI,
    val isCopied: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isAiLoading: Boolean = false,
    val aiMessage: String? = null,
    val showSettingsSheet: Boolean = false,
    val showEmojiSheet: Boolean = false,
    val showInfoDialog: Boolean = false,
    val activeTab: Int = 0 // 0: Transliterate, 1: Quick Phrases, 2: Keyboard / Info
)

class NepaliTypeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(NepaliTypeUiState())
    val uiState: StateFlow<NepaliTypeUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()
    private var copyFeedbackJob: Job? = null
    private var aiJob: Job? = null

    init {
        // Initial example text for quick demonstration
        val demoRoman = "mero naam ram ho"
        val demoDevanagari = TransliterationEngine.transliterateRomanNepali(demoRoman)
        _uiState.update {
            it.copy(
                inputText = demoRoman,
                outputText = demoDevanagari,
                suggestions = TransliterationEngine.getSuggestions(demoRoman)
            )
        }
    }

    fun onInputChanged(newText: String) {
        if (newText == _uiState.value.inputText) return

        // Push current text to undo stack
        if (_uiState.value.inputText.isNotEmpty()) {
            undoStack.add(_uiState.value.inputText)
            if (undoStack.size > 50) undoStack.removeAt(0)
            redoStack.clear()
        }

        performTransliteration(newText)
    }

    private fun performTransliteration(text: String) {
        val state = _uiState.value
        val converted = if (state.settings.autoConvert) {
            when (state.conversionMode) {
                ConversionMode.ROMAN_TO_NEPALI -> {
                    TransliterationEngine.transliterateRomanNepali(
                        input = text,
                        useNepaliNumbers = state.settings.nepaliNumbers,
                        naturalStyle = state.settings.naturalStyle
                    )
                }
                ConversionMode.NEPALI_TO_ROMAN -> {
                    TransliterationEngine.devanagariToRoman(text)
                }
            }
        } else {
            state.outputText
        }

        val suggestionsList = if (state.settings.autoCorrect && state.conversionMode == ConversionMode.ROMAN_TO_NEPALI) {
            TransliterationEngine.getSuggestions(text)
        } else {
            emptyList()
        }

        _uiState.update {
            it.copy(
                inputText = text,
                outputText = converted,
                suggestions = suggestionsList,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty(),
                aiMessage = null
            )
        }
    }

    fun onSuggestionSelected(suggestion: String) {
        triggerHaptic()
        undoStack.add(_uiState.value.inputText)
        redoStack.clear()

        _uiState.update {
            it.copy(
                outputText = suggestion,
                canUndo = true,
                canRedo = false
            )
        }
    }

    fun onCopyOutput() {
        triggerHaptic()
        val textToCopy = _uiState.value.outputText.ifEmpty { _uiState.value.inputText }
        if (textToCopy.isEmpty()) return

        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Nepali Text", textToCopy)
        clipboard.setPrimaryClip(clip)

        copyFeedbackJob?.cancel()
        copyFeedbackJob = viewModelScope.launch {
            _uiState.update { it.copy(isCopied = true) }
            delay(2000)
            _uiState.update { it.copy(isCopied = false) }
        }
    }

    fun onShareOutput(context: Context) {
        triggerHaptic()
        val textToShare = _uiState.value.outputText.ifEmpty { _uiState.value.inputText }
        if (textToShare.isEmpty()) return

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Nepali Text via")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun onClear() {
        triggerHaptic()
        if (_uiState.value.inputText.isNotEmpty()) {
            undoStack.add(_uiState.value.inputText)
            redoStack.clear()
        }
        _uiState.update {
            it.copy(
                inputText = "",
                outputText = "",
                suggestions = emptyList(),
                canUndo = undoStack.isNotEmpty(),
                canRedo = false,
                aiMessage = null
            )
        }
    }

    fun onUndo() {
        triggerHaptic()
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_uiState.value.inputText)
            performTransliteration(previous)
        }
    }

    fun onRedo() {
        triggerHaptic()
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_uiState.value.inputText)
            performTransliteration(next)
        }
    }

    fun onPhraseSelected(phrase: PhraseItem) {
        triggerHaptic()
        if (_uiState.value.inputText.isNotEmpty()) {
            undoStack.add(_uiState.value.inputText)
            redoStack.clear()
        }

        _uiState.update {
            it.copy(
                inputText = phrase.roman,
                outputText = phrase.nepali,
                suggestions = TransliterationEngine.getSuggestions(phrase.roman),
                canUndo = true,
                canRedo = false,
                activeTab = 0 // return to typing screen
            )
        }
    }

    fun onInsertText(charOrWord: String) {
        triggerHaptic()
        val current = _uiState.value.inputText
        onInputChanged(current + charOrWord)
    }

    fun onBackspace() {
        triggerHaptic()
        val current = _uiState.value.inputText
        if (current.isNotEmpty()) {
            onInputChanged(current.dropLast(1))
        }
    }

    fun onToggleConversionMode() {
        triggerHaptic()
        val newMode = if (_uiState.value.conversionMode == ConversionMode.ROMAN_TO_NEPALI) {
            ConversionMode.NEPALI_TO_ROMAN
        } else {
            ConversionMode.ROMAN_TO_NEPALI
        }

        // Swap input and output for seamless bidirectional work
        val currentOutput = _uiState.value.outputText
        val currentInput = _uiState.value.inputText

        _uiState.update {
            it.copy(
                conversionMode = newMode,
                inputText = currentOutput.ifEmpty { currentInput },
                outputText = currentInput
            )
        }
        performTransliteration(_uiState.value.inputText)
    }

    fun onAiImprove() {
        triggerHaptic()
        val input = _uiState.value.inputText
        if (input.isBlank()) return

        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true, aiMessage = "Improving Nepali phrasing with AI...") }
            when (val result = GeminiAiService.improveNepaliText(input)) {
                is AiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAiLoading = false,
                            outputText = result.improvedText,
                            aiMessage = "Enhanced with natural Nepali grammar ✨"
                        )
                    }
                }
                is AiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAiLoading = false,
                            outputText = result.fallbackText,
                            aiMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun updateSettings(newSettings: TransliterationSettings) {
        _uiState.update { it.copy(settings = newSettings) }
        performTransliteration(_uiState.value.inputText)
    }

    fun setActiveTab(index: Int) {
        triggerHaptic()
        _uiState.update { it.copy(activeTab = index) }
    }

    fun setShowSettingsSheet(show: Boolean) {
        _uiState.update { it.copy(showSettingsSheet = show) }
    }

    fun setShowEmojiSheet(show: Boolean) {
        _uiState.update { it.copy(showEmojiSheet = show) }
    }

    fun setShowInfoDialog(show: Boolean) {
        _uiState.update { it.copy(showInfoDialog = show) }
    }

    fun triggerHaptic() {
        if (!_uiState.value.settings.hapticFeedback) return
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(10)
            }
        } catch (e: Exception) {
            // Ignore if vibration unsupported
        }
    }
}
