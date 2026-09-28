package com.example.engine

object TransliterationRules {

    const val VIRAMA: Char = '\u094D' // ्
    const val ANUSVARA: Char = '\u0902' // ं
    const val CHANDRABINDU: Char = '\u0901' // ँ
    const val VISARGA: Char = '\u0903' // ः
    const val DANDA: Char = '\u0964' // ।
    const val DOUBLE_DANDA: Char = '\u0965' // ॥

    val digitsEnglishToNepali: Map<Char, Char> = mapOf(
        '0' to '०',
        '1' to '१',
        '2' to '२',
        '3' to '३',
        '4' to '४',
        '5' to '५',
        '6' to '६',
        '7' to '७',
        '8' to '८',
        '9' to '९'
    )

    val digitsNepaliToEnglish: Map<Char, Char> = digitsEnglishToNepali.entries.associate { (k, v) -> v to k }

    // Multi-character independent vowels (ordered by length descending)
    val independentVowels: List<Pair<String, String>> = listOf(
        "aau" to "औ",
        "aai" to "आई",
        "aa" to "आ",
        "ee" to "ई",
        "ii" to "ई",
        "oo" to "ऊ",
        "uu" to "ऊ",
        "ai" to "ऐ",
        "au" to "औ",
        "ri" to "ऋ",
        "a" to "अ",
        "i" to "इ",
        "u" to "उ",
        "e" to "ए",
        "o" to "ओ"
    )

    // Dependent matras (attached to consonant)
    val matras: List<Pair<String, String>> = listOf(
        "aau" to "ौ",
        "aai" to "ाइ",
        "aa" to "ा",
        "ee" to "ी",
        "ii" to "ी",
        "oo" to "ू",
        "uu" to "ू",
        "ai" to "ै",
        "au" to "ौ",
        "ri" to "ृ",
        "i" to "ि",
        "u" to "ु",
        "e" to "े",
        "o" to "ो",
        "a" to "" // inherent vowel, removes virama
    )

    // Consonant clusters & consonants (ordered by length descending)
    val consonants: List<Pair<String, String>> = listOf(
        "chhh" to "छ",
        "chha" to "छ",
        "chh" to "छ",
        "kshh" to "क्ष",
        "ksha" to "क्ष",
        "ksh" to "क्ष",
        "gya" to "ज्ञ",
        "shh" to "ष",
        "tra" to "त्र",
        "dhy" to "ध्य",
        "kh" to "ख",
        "gh" to "घ",
        "ng" to "ङ",
        "ch" to "च",
        "jh" to "झ",
        "th" to "थ",
        "dh" to "ध",
        "ph" to "फ",
        "bh" to "भ",
        "sh" to "श",
        "tr" to "त्र",
        "gy" to "ज्ञ",
        "k" to "क",
        "g" to "ग",
        "c" to "च",
        "j" to "ज",
        "t" to "त",
        "d" to "द",
        "n" to "न",
        "p" to "प",
        "f" to "फ",
        "b" to "ब",
        "m" to "म",
        "y" to "य",
        "r" to "र",
        "l" to "ल",
        "w" to "व",
        "v" to "व",
        "s" to "स",
        "h" to "ह",
        "x" to "क्ष", // colloquial Nepali Roman usage
        "q" to "क",
        "z" to "ज"
    )

    // Reverse mapping for Devanagari to Roman transliteration
    val devanagariToRoman: Map<String, String> = mapOf(
        "अ" to "a", "आ" to "aa", "इ" to "i", "ई" to "ee", "उ" to "u", "ऊ" to "oo",
        "ऋ" to "ri", "ए" to "e", "ऐ" to "ai", "ओ" to "o", "औ" to "au",
        "क" to "ka", "ख" to "kha", "ग" to "ga", "घ" to "gha", "ङ" to "nga",
        "च" to "cha", "छ" to "chha", "ज" to "ja", "झ" to "jha", "ञ" to "nya",
        "ट" to "ta", "ठ" to "tha", "ड" to "da", "ढ" to "dha", "ण" to "na",
        "त" to "ta", "थ" to "tha", "द" to "da", "ध" to "dha", "न" to "na",
        "प" to "pa", "फ" to "pha", "ब" to "ba", "भ" to "bha", "म" to "ma",
        "य" to "ya", "र" to "ra", "ल" to "la", "व" to "wa",
        "श" to "sha", "ष" to "sha", "स" to "sa", "ह" to "ha",
        "क्ष" to "ksha", "त्र" to "tra", "ज्ञ" to "gya",
        "ा" to "aa", "ि" to "i", "ी" to "ee", "ु" to "u", "ू" to "oo",
        "ृ" to "ri", "े" to "e", "ै" to "ai", "ो" to "o", "ौ" to "au",
        "ं" to "n", "ँ" to "n", "ः" to "h", "्" to "",
        "। " to ". ", "।" to ".", "॥" to ".."
    )
}
