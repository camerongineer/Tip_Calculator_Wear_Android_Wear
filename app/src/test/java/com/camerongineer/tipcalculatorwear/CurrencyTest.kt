package com.camerongineer.tipcalculatorwear

import com.camerongineer.tipcalculatorwear.presentation.constants.TipCurrency
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyTest {

    @Test
    fun `dollar currencies use the dollar symbol`() {
        assertEquals("$", TipCurrency.CAD.symbol)
        assertEquals("$", TipCurrency.HKD.symbol)
        assertEquals("$", TipCurrency.USD.symbol)
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
    fun `all currencies have a non blank unique-ish symbol`() {
        val symbolPool = setOf("$", "€", "£", "₺", "¥")
        TipCurrency.entries.forEach { currency ->
            assert(currency.symbol.isNotBlank())
            assert(currency.symbol in symbolPool)
        }
    }

    @Test
    fun `currency set is CAD CNY EUR GBP HKD TRY USD`() {
        assertEquals(
            listOf("CAD", "CNY", "EUR", "GBP", "HKD", "TRY", "USD"),
            TipCurrency.entries.map { it.name }
        )
    }
}
