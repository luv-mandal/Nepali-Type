package com.example.model

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class ConversionMode {
    ROMAN_TO_NEPALI, // EN -> नेपाली
    NEPALI_TO_ROMAN  // नेपाली -> EN
}

data class TransliterationSettings(
    val autoConvert: Boolean = true,
    val autoCorrect: Boolean = true,
    val nepaliNumbers: Boolean = false,
    val naturalStyle: Boolean = true,
    val hapticFeedback: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM
)
