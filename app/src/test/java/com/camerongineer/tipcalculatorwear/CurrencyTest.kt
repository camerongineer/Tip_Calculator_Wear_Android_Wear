package com.camerongineer.tipcalculatorwear

import com.camerongineer.tipcalculatorwear.presentation.constants.TipCurrency
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyTest {

    @Test
    fun `dollar currencies use the dollar symbol`() {
        listOf(
            TipCurrency.CAD, TipCurrency.HKD, TipCurrency.USD,
            TipCurrency.AUD, TipCurrency.MXN, TipCurrency.NZD, TipCurrency.SGD
        ).forEach { assertEquals("$", it.symbol) }
    }

    @Test
    fun `euro uses the euro symbol`() {
        assertEquals("€", TipCurrency.EUR.symbol)
    }

    @Test
    fun `pound uses the pound symbol`() {
        assertEquals("£", TipCurrency.GBP.symbol)
    }

    @Test
    fun `lira uses the lira symbol`() {
        assertEquals("₺", TipCurrency.TRY.symbol)
    }

    @Test
    fun `yuan uses the yuan symbol`() {
        assertEquals("¥", TipCurrency.CNY.symbol)
    }

    @Test
    fun `rupee uses the rupee symbol`() {
        assertEquals("₹", TipCurrency.INR.symbol)
    }

    @Test
    fun `ruble uses the ruble symbol`() {
        assertEquals("₽", TipCurrency.RUB.symbol)
    }

    @Test
    fun `every symbol is a single character`() {
        // Reviewer constraint: one-glyph symbols only, until the two-character
        // variants (R$ / CHF / kr) get their own layout handling.
        TipCurrency.entries.forEach { currency ->
            assertEquals("'$currency' symbol ${currency.symbol}", 1, currency.symbol.length)
        }
    }

    @Test
    fun `all currencies have a symbol in the reviewed symbol pool`() {
        val symbolPool = setOf("$", "€", "£", "₺", "¥", "₹", "₽")
        TipCurrency.entries.forEach { currency ->
            assert(currency.symbol.isNotBlank())
            assert(currency.symbol in symbolPool)
        }
    }

    @Test
    fun `currency set matches the reviewed Iso list`() {
        assertEquals(
            listOf(
                "CAD", "CNY", "EUR", "GBP", "HKD", "TRY", "USD",
                "AUD", "INR", "MXN", "NZD", "RUB", "SGD"
            ),
            TipCurrency.entries.map { it.name }
        )
    }

    @Test
    fun `forCodeOrNull resolves and rejects codes`() {
        assertEquals(TipCurrency.EUR, TipCurrency.forCodeOrNull("EUR"))
        assertEquals(TipCurrency.USD, TipCurrency.forCodeOrNull("USD"))
        assertEquals(null, TipCurrency.forCodeOrNull("JPY"))
        assertEquals(null, TipCurrency.forCodeOrNull(null))
        assertEquals(null, TipCurrency.forCodeOrNull(""))
    }
}
