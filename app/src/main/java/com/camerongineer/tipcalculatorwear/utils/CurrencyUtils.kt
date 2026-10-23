package com.camerongineer.tipcalculatorwear.utils

import com.camerongineer.tipcalculatorwear.presentation.constants.TipCurrency
import java.util.Currency
import java.util.Locale

/**
 * Resolves a region to the prefab TipCurrency, mirroring the mapping used by
 * getLocale: the ISO currency code of the supplied region is looked up in the
 * (curated) TipCurrency table; regions without a listed currency fall back to
 * USD. Pure JVM logic so the exhaustive region sweep can be a plain unit test.
 */
fun resolveTipCurrency(locale: Locale): TipCurrency {
    return try {
        val currency: Currency = Currency.getInstance(locale)
        TipCurrency.forCodeOrNull(currency.currencyCode) ?: TipCurrency.USD
    } catch (e: Exception) {
        TipCurrency.USD
    }
}
