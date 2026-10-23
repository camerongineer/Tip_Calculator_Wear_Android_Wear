package com.camerongineer.tipcalculatorwear

import com.camerongineer.tipcalculatorwear.presentation.constants.TipLanguage
import com.camerongineer.tipcalculatorwear.utils.getLocale
import com.camerongineer.tipcalculatorwear.utils.getTipLanguage
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.util.Locale

class LanguageUtilsTest {

    companion object {
        private val originalLocale = Locale.getDefault()

        @BeforeClass
        @JvmStatic
        fun setUp() {
            Locale.setDefault(Locale.US)
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun `getLocale builds a locale for the requested language`() {
        val locale = getLocale("de")
        assertEquals("de", locale.language)
    }

    @Test
    fun `getLocale keeps the device region`() {
        val locale = getLocale("fr")
        assertEquals(Locale.getDefault().country, locale.country)
    }

    @Test
    fun `getTipLanguage resolves valid codes case insensitively`() {
        assertEquals(TipLanguage.DE, getTipLanguage("DE"))
        assertEquals(TipLanguage.ES, getTipLanguage("es"))
        assertEquals(TipLanguage.FR, getTipLanguage("Fr"))
    }

    @Test
    fun `getTipLanguage resolves all known languages`() {
        TipLanguage.entries.forEach { language ->
            assertEquals(language, getTipLanguage(language.name.lowercase()))
        }
    }

    @Test
    fun `getTipLanguage falls back to the device language for unknown codes`() {
        assertEquals(TipLanguage.EN, getTipLanguage("xx"))
    }

    @Test
    fun `getTipLanguage falls back to english for a blank code`() {
        assertEquals(TipLanguage.EN, getTipLanguage(""))
    }
}
