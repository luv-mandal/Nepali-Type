package com.example

import com.example.engine.TransliterationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPromptExample1_MeroNaamRamHo() {
        val result = TransliterationEngine.transliterateRomanNepali("mero naam ram ho")
        assertEquals("मेरो नाम राम हो", result)
    }

    @Test
    fun testPromptExample2_TimiKahaChau() {
        val result = TransliterationEngine.transliterateRomanNepali("timi kaha chau")
        assertEquals("तिमी कहाँ छौ", result)
    }

    @Test
    fun testPromptExample3_MalaiPaniJanuCha() {
        val result = TransliterationEngine.transliterateRomanNepali("malai pani janu cha")
        assertEquals("मलाई पनि जानु छ", result)
    }

    @Test
    fun testPromptExample4_AajaWeatherKastoCha() {
        val result = TransliterationEngine.transliterateRomanNepali("aaja weather kasto cha")
        assertEquals("आज weather कस्तो छ", result)
    }

    @Test
    fun testPromptExample5_SchoolHomework() {
        val result = TransliterationEngine.transliterateRomanNepali("dai mero school ko homework gardinu na")
        assertEquals("दाइ मेरो स्कूलको होमवर्क गरिदिनु न", result)
    }

    @Test
    fun testPromptExample6_TapaiLaiKastoCha() {
        val result = TransliterationEngine.transliterateRomanNepali("tapai lai kasto cha")
        assertEquals("तपाईंलाई कस्तो छ", result)
    }

    @Test
    fun testPromptExample7_MaBholiKathmanduJanchu() {
        val result = TransliterationEngine.transliterateRomanNepali("ma bholi Kathmandu janchu")
        assertEquals("म भोलि Kathmandu जान्छु", result)
    }

    @Test
    fun testPromptExample8_KhanaKhanuBhayo() {
        val result = TransliterationEngine.transliterateRomanNepali("khana khanu bhayo")
        assertEquals("खाना खानुभयो", result)
    }

    @Test
    fun testPromptExample9_RamroCha() {
        val result = TransliterationEngine.transliterateRomanNepali("ramro cha")
        assertEquals("राम्रो छ", result)
    }

    @Test
    fun testPromptExample10_MalaiThahaChaina() {
        val result = TransliterationEngine.transliterateRomanNepali("malai thaha chaina")
        assertEquals("मलाई थाहा छैन", result)
    }

    @Test
    fun testPromptExample11_MeroNaamLuvHo() {
        val result = TransliterationEngine.transliterateRomanNepali("mero naam luv ho")
        assertEquals("मेरो नाम लुव हो", result)
    }

    @Test
    fun testPromptExample12_MalaiEkCupChiyaDinu() {
        val result = TransliterationEngine.transliterateRomanNepali("malai ek cup chiya dinu")
        assertEquals("मलाई एक कप चिया दिनु", result)
    }

    @Test
    fun testNepaliNumbers() {
        val result = TransliterationEngine.transliterateRomanNepali("123", useNepaliNumbers = true)
        assertEquals("१२३", result)
    }

    @Test
    fun testSuggestions() {
        val suggestions = TransliterationEngine.getSuggestions("mero naam")
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.any { it.contains("मेरो नाम") })
    }
}
