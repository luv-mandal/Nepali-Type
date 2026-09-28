package com.example.model

data class PhraseItem(
    val id: String,
    val category: String,
    val roman: String,
    val nepali: String,
    val description: String
)

object QuickPhrasesData {
    val samplePhrases: List<PhraseItem> = listOf(
        // Prompt specific examples
        PhraseItem(
            id = "p_homework",
            category = "Daily / Homework",
            roman = "dai mero school ko homework gardinu na",
            nepali = "दाइ मेरो स्कूलको होमवर्क गरिदिनु न",
            description = "Brother, please help with school homework"
        ),
        PhraseItem(
            id = "p_kasto_cha",
            category = "Greetings",
            roman = "tapai lai kasto cha?",
            nepali = "तपाईंलाई कस्तो छ?",
            description = "How are you? (Polite)"
        ),
        PhraseItem(
            id = "p_namaste",
            category = "Greetings",
            roman = "namaste dai",
            nepali = "नमस्ते दाइ",
            description = "Hello brother"
        ),
        PhraseItem(
            id = "p_khana",
            category = "Everyday",
            roman = "khana khanu bhayo?",
            nepali = "खाना खानुभयो?",
            description = "Have you eaten? (Standard greeting)"
        ),
        PhraseItem(
            id = "p_help",
            category = "Everyday",
            roman = "malai help garnu na",
            nepali = "मलाई help गर्नु न",
            description = "Please help me"
        ),
        PhraseItem(
            id = "p_bhetam",
            category = "Social",
            roman = "bholi bhetam",
            nepali = "भोलि भेटौँ",
            description = "Let's meet tomorrow"
        ),
        PhraseItem(
            id = "p_chiya",
            category = "Everyday",
            roman = "malai ek cup chiya dinu",
            nepali = "मलाई एक कप चिया दिनु",
            description = "Give me a cup of tea"
        ),
        PhraseItem(
            id = "p_ktm",
            category = "Travel",
            roman = "ma bholi Kathmandu janchu",
            nepali = "म भोलि Kathmandu जान्छु",
            description = "I am going to Kathmandu tomorrow"
        ),
        PhraseItem(
            id = "p_school",
            category = "Daily / Homework",
            roman = "aaja school janu parcha",
            nepali = "आज स्कूल जानुपर्छ",
            description = "Have to go to school today"
        ),
        PhraseItem(
            id = "p_weather",
            category = "Casual",
            roman = "aaja weather kasto cha?",
            nepali = "आज weather कस्तो छ?",
            description = "How is the weather today?"
        ),
        PhraseItem(
            id = "p_thaha_chaina",
            category = "Casual",
            roman = "malai thaha chaina",
            nepali = "मलाई थाहा छैन",
            description = "I don't know"
        ),
        PhraseItem(
            id = "p_timi_kaha",
            category = "Casual",
            roman = "timi kaha chau?",
            nepali = "तिमी कहाँ छौ?",
            description = "Where are you?"
        ),
        PhraseItem(
            id = "p_ramro",
            category = "Casual",
            roman = "ramro cha",
            nepali = "राम्रो छ",
            description = "It is nice / good"
        )
    )
}
