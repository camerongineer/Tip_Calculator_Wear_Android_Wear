package com.camerongineer.tipcalculatorwear

import com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * getFormattedAmountString renders cents as "units[.,]decimals" (decimal
 * separator follows the device locale), so these tests assert the locale
 * independent invariants instead of pinning "." or ",".
 */
class AmountFormatTest {

    private fun assertFormat(amount: Int, units: String, decimals: String) {
        val formatted = getFormattedAmountString(amount)
        val match = Regex("^(-?\\d+)[.,](\\d{2})$").find(formatted)
            ?: throw AssertionError("Unexpected amount format: '$formatted' for $amount")
        assertEquals("units part of '$formatted' for $amount", units, match.groupValues[1])
        assertEquals("decimals part of '$formatted' for $amount", decimals, match.groupValues[2])
    }

    @Test
    fun `zero is formatted as units 0 with two decimals`() {
        assertFormat(0, "0", "00")
    }

    @Test
    fun `one cent is formatted below one unit`() {
        assertFormat(1, "0", "01")
    }

    @Test
    fun `ninety nine cents is formatted below one unit`() {
        assertFormat(99, "0", "99")
    }

    @Test
    fun `fifty cents is formatted below one unit`() {
        assertFormat(50, "0", "50")
    }

    @Test
    fun `one dollar is exactly 1 with two decimals`() {
        assertFormat(100, "1", "00")
    }

    @Test
    fun `fifty dollars and twenty three cents keeps its cents`() {
        assertFormat(5023, "50", "23")
    }

    @Test
    fun `two hundred thirty five dollars is exact`() {
        assertFormat(23500, "235", "00")
    }

    @Test
    fun `amounts above one thousand get no thousands grouping`() {
        assertFormat(123456, "1234", "56")
    }

    @Test
    fun `rounding increments render with two decimals`() {
        setOf(50, 100, 250, 500).forEach { increment ->
            val formatted = getFormattedAmountString(increment)
            assertTrue("increment $increment not two decimals: '$formatted'",
                Regex("^\\d+[.,]\\d{2}$").matches(formatted))
        }
    }
}
