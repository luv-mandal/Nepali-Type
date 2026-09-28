package com.example.engine

/**
 * Production-ready Roman Nepali to Devanagari transliteration engine.
 * Fast, pure Kotlin, context-aware, offline, and mobile-friendly.
 */
object TransliterationEngine {

    /**
     * Main entry point to transliterate a Romanized Nepali sentence into natural Devanagari.
     *
     * @param input Raw text typed by user (e.g. "dai mero school ko homework gardinu na")
     * @param useNepaliNumbers Whether to convert '0'-'9' to '०'-'९'
     * @param naturalStyle Whether to apply Nepali contextual idioms (e.g. "tapai lai" -> "तपाईंलाई")
     */
    fun transliterateRomanNepali(
        input: String,
        useNepaliNumbers: Boolean = false,
        naturalStyle: Boolean = true
    ): String {
        if (input.isEmpty()) return ""

        var processed = input

        // Step 1: Contextual multi-word phrase matching (case-insensitive)
        if (naturalStyle) {
            for ((romanPhrase, nepaliPhrase) in NepaliDictionary.multiWordPhrases) {
                val regex = Regex("(?i)\\b" + Regex.escape(romanPhrase) + "\\b")
                processed = regex.replace(processed, nepaliPhrase)
            }
        }

        // Step 2: Tokenize text while preserving exact whitespace, newlines, and punctuation
        val tokens = tokenize(processed)
        val sb = StringBuilder()

        for (i in tokens.indices) {
            val token = tokens[i]

            // If token is already Devanagari (from phrase replacement), or punctuation/whitespace, keep it
            if (token.isEmpty()) continue

            if (isWhitespaceOrPunctuation(token)) {
                // Convert punctuation if needed (e.g., standard danda substitution if user types '|' or '..')
                sb.append(processPunctuation(token))
            } else if (isAllDevanagari(token)) {
                sb.append(token)
            } else if (token.all { it.isDigit() }) {
                if (useNepaliNumbers) {
                    sb.append(token.map { TransliterationRules.digitsEnglishToNepali[it] ?: it }.joinToString(""))
                } else {
                    sb.append(token)
                }
            } else {
                // Check context with previous token for words like 'pani'
                val prevWord = findPreviousWord(tokens, i)?.lowercase()
                val converted = transliterateSingleWord(token, prevWord, naturalStyle)
                sb.append(converted)
            }
        }

        var result = sb.toString()

        // Step 3: Handle numbers across the entire string if enabled
        if (useNepaliNumbers) {
            val numConverted = StringBuilder()
            for (ch in result) {
                numConverted.append(TransliterationRules.digitsEnglishToNepali[ch] ?: ch)
            }
            result = numConverted.toString()
        }

        return result
    }

    /**
     * Transliterates a single Roman word into Devanagari.
     */
    fun transliterateSingleWord(
        word: String,
        prevWord: String? = null,
        naturalStyle: Boolean = true
    ): String {
        val lower = word.lowercase()

        // Check if preserved English loanword
        if (NepaliDictionary.preservedEnglishWords.contains(lower)) {
            return word
        }

        // If a word is explicitly capitalized in the middle of a sentence (like "Kathmandu" or "Google"),
        // preserve the user's English capitalization as an uncorrupted foreign/proper noun
        if (word.length > 1 && word[0].isUpperCase() && prevWord != null && lower !in listOf("ma", "mero", "timi", "tapai", "hami", "dai", "bhai", "aaja", "bholi")) {
            return word
        }

        // Contextual disambiguation for 'pani'
        if (lower == "pani") {
            if (prevWord in listOf("malai", "timilai", "tapailai", "tapai", "timi", "ma", "uslai", "unlai", "hamilai", "hami", "yo", "tyo", "sathi", "bhai", "dai")) {
                return "पनि"
            }
        }

        // Exact match in dictionary
        val exactMatch = NepaliDictionary.wordMap[lower]
        if (exactMatch != null) {
            return exactMatch
        }

        // Suffix / Postposition decomposition (e.g. tapailai -> तपाईं + लाई = तपाईंलाई)
        if (naturalStyle) {
            val suffixDecomp = decomposeSuffix(lower)
            if (suffixDecomp != null) {
                return suffixDecomp
            }
        }

        // Fallback to phonetic rule-based transliteration
        return phoneticTransliterate(lower)
    }

    /**
     * Decomposes common Nepali postpositions and suffixes attached to root words.
     */
    private fun decomposeSuffix(word: String): String? {
        val suffixes = listOf(
            "lai" to "लाई",
            "ko" to "को",
            "ka" to "का",
            "ki" to "की",
            "ma" to "मा",
            "le" to "ले",
            "bata" to "बाट",
            "baat" to "बाट",
            "dekhi" to "देखि",
            "sanga" to "सँग",
            "chhan" to "छन्",
            "chan" to "छन्",
            "chhin" to "छिन्",
            "chin" to "छिन्",
            "chhu" to "छु",
            "chu" to "छु",
            "chhau" to "छौ",
            "chau" to "छौ",
            "chha" to "छ",
            "cha" to "छ",
            "parcha" to "पर्छ",
            "parchha" to "पर्छ",
            "pardaina" to "पर्दैन",
            "dinu" to "दिनु",
            "dinus" to "दिनुस्"
        )

        for ((sfx, nepaliSfx) in suffixes) {
            if (word.length > sfx.length && word.endsWith(sfx)) {
                val root = word.substring(0, word.length - sfx.length)
                val rootNepali = NepaliDictionary.wordMap[root] ?: phoneticTransliterate(root)
                return rootNepali + nepaliSfx
            }
        }
        return null
    }

    /**
     * Phonetic rule-based transliterator for unknown Roman Nepali words.
     */
    fun phoneticTransliterate(word: String): String {
        if (word.isEmpty()) return ""

        val sb = StringBuilder()
        var i = 0
        val n = word.length
        var lastWasConsonant = false

        while (i < n) {
            val ch = word[i]

            // Check if char is not ASCII letter
            if (!ch.isLetter()) {
                sb.append(ch)
                lastWasConsonant = false
                i++
                continue
            }

            val remaining = word.substring(i)

            // If last was consonant, look for vowel matra
            if (lastWasConsonant) {
                var matchedMatra: Pair<String, String>? = null
                for (pair in TransliterationRules.matras) {
                    if (remaining.startsWith(pair.first)) {
                        matchedMatra = pair
                        break
                    }
                }

                if (matchedMatra != null) {
                    val matraStr = matchedMatra.second
                    // Remove virama if it was added
                    if (sb.isNotEmpty() && sb.last() == TransliterationRules.VIRAMA) {
                        sb.deleteCharAt(sb.length - 1)
                    }
                    sb.append(matraStr)
                    i += matchedMatra.first.length
                    lastWasConsonant = false
                    continue
                }
            }

            // Independent vowel at start of word or after another vowel
            var matchedIndepVowel: Pair<String, String>? = null
            for (pair in TransliterationRules.independentVowels) {
                if (remaining.startsWith(pair.first)) {
                    matchedIndepVowel = pair
                    break
                }
            }

            if (!lastWasConsonant && matchedIndepVowel != null) {
                sb.append(matchedIndepVowel.second)
                i += matchedIndepVowel.first.length
                lastWasConsonant = false
                continue
            }

            // Consonants
            var matchedConsonant: Pair<String, String>? = null
            for (pair in TransliterationRules.consonants) {
                if (remaining.startsWith(pair.first)) {
                    matchedConsonant = pair
                    break
                }
            }

            if (matchedConsonant != null) {
                // If previous was consonant and we didn't add vowel, the previous virama remains
                sb.append(matchedConsonant.second)
                sb.append(TransliterationRules.VIRAMA)
                i += matchedConsonant.first.length
                lastWasConsonant = true
                continue
            }

            // Single unknown character fallback
            sb.append(ch)
            lastWasConsonant = false
            i++
        }

        // Nepali schwa deletion rule:
        // In Nepali Roman typing, trailing virama on words (like 'nam' or 'ghar') is dropped
        // unless specifically intended.
        if (sb.isNotEmpty() && sb.last() == TransliterationRules.VIRAMA) {
            sb.deleteCharAt(sb.length - 1)
        }

        return sb.toString()
    }

    /**
     * Converts Devanagari text back to readable Roman Nepali.
     */
    fun devanagariToRoman(input: String): String {
        var res = input
        for ((dev, rom) in TransliterationRules.devanagariToRoman) {
            res = res.replace(dev, rom)
        }
        for ((nepDigit, engDigit) in TransliterationRules.digitsNepaliToEnglish) {
            res = res.replace(nepDigit, engDigit)
        }
        return res
    }

    /**
     * Generates context-aware smart suggestions for the current cursor/typed text.
     */
    fun getSuggestions(input: String): List<String> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return emptyList()

        val results = mutableListOf<String>()

        // 1. Current full transliteration
        val mainConversion = transliterateRomanNepali(trimmed)
        if (mainConversion.isNotEmpty()) {
            results.add(mainConversion)
        }

        // 2. Alternative polite / formal endings or punctuation
        if (!mainConversion.endsWith("।") && !mainConversion.endsWith("?") && !mainConversion.endsWith("!")) {
            results.add("$mainConversion।")
        }

        // 3. For single or last words, suggest common dictionary candidates
        val words = trimmed.split(Regex("\\s+"))
        val lastWord = words.lastOrNull()?.lowercase() ?: ""
        if (lastWord.isNotEmpty()) {
            // Check close dictionary matches
            val dictMatches = NepaliDictionary.wordMap.filterKeys { it.startsWith(lastWord) }
                .values.take(3)
            for (m in dictMatches) {
                val replaced = words.dropLast(1).joinToString(" ") + if (words.size > 1) " " else "" + m
                val transliteratedSentence = transliterateRomanNepali(replaced)
                if (!results.contains(transliteratedSentence)) {
                    results.add(transliteratedSentence)
                }
            }
        }

        // 4. Literal transliteration (without phrase replacement)
        val literal = transliterateRomanNepali(trimmed, naturalStyle = false)
        if (literal != mainConversion && !results.contains(literal)) {
            results.add(literal)
        }

        // 5. English Roman verbatim candidate so user is never locked in
        if (!results.contains(trimmed)) {
            results.add(trimmed)
        }

        return results.distinct().take(5)
    }

    private fun tokenize(text: String): List<String> {
        val tokens = mutableListOf<String>()
        var currentToken = StringBuilder()
        var inWord = false

        for (ch in text) {
            val isLetterOrDigit = ch.isLetterOrDigit() || ch == '_'
            if (isLetterOrDigit) {
                if (!inWord && currentToken.isNotEmpty()) {
                    tokens.add(currentToken.toString())
                    currentToken = StringBuilder()
                }
                inWord = true
                currentToken.append(ch)
            } else {
                if (inWord && currentToken.isNotEmpty()) {
                    tokens.add(currentToken.toString())
                    currentToken = StringBuilder()
                }
                inWord = false
                currentToken.append(ch)
            }
        }
        if (currentToken.isNotEmpty()) {
            tokens.add(currentToken.toString())
        }
        return tokens
    }

    private fun isWhitespaceOrPunctuation(str: String): Boolean {
        return str.all { it.isWhitespace() || !it.isLetterOrDigit() }
    }

    private fun isAllDevanagari(str: String): Boolean {
        return str.any { it in '\u0900'..'\u097F' } && str.none { it in 'a'..'z' || it in 'A'..'Z' }
    }

    private fun processPunctuation(str: String): String {
        return str.replace("..", "॥").replace("|", "।")
    }

    private fun findPreviousWord(tokens: List<String>, currentIndex: Int): String? {
        for (j in currentIndex - 1 downTo 0) {
            val t = tokens[j]
            if (!isWhitespaceOrPunctuation(t)) {
                return t
            }
        }
        return null
    }
}
