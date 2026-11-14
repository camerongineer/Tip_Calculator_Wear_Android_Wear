package com.camerongineer.tipcalculatorwear.presentation.constants

import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.utils.resolveTipCurrency
import java.util.Locale

/**
 * Region-resolvable currencies across the app's target markets. The symbol is
 * the currency's own display symbol, reviewed against ISO 4217 + Unicode CLDR
 * once and pinned by CurrencyResolutionTest.
 *
 * Only currencies whose amount format works with the app's decimal-max-2
 * rendering are listed: zero-decimal currencies (JPY ¥, KRW ₩) are
 * deliberately absent until AmountFormat respects getDefaultFractionDigits().
 * Two-character display symbols (BRL "R$", CHF, SEK/NOK/DKK "kr") are also
 * deferred for now; those regions currently resolve to USD.
 *
 * The picker representative (one currency per distinct symbol) additionally
 * carries a localized currency-name resource; codes sharing a symbol reuse
 * the representative's row.
 */
enum class TipCurrency(
    val symbol: String,
    val nameResId: Int? = null,
) {
    CAD("$"),
    CNY("¥", R.string.currency_yuan),
    EUR("€", R.string.currency_euro),
    GBP("£", R.string.currency_pound),
    HKD("$"),
    TRY("₺", R.string.currency_lira),
    USD("$", R.string.currency_dollar),
    AUD("$"),
    INR("₹", R.string.currency_rupee),
    MXN("$"),
    NZD("$"),
    RUB("₽", R.string.currency_ruble),
    SGD("$");

    companion object {
        // One representative per distinct display symbol, in picker display
        // order: the USD row covers every "$" region (CA/HK/AU/MX/NZ/SG).
        val pickerCurrencies = listOf(
            USD, EUR, GBP, TRY, CNY, INR, RUB
        )

        /** Representative currency code per picker symbol, in picker order. */
        val defaultCurrencyCodes: List<String> =
            pickerCurrencies.map { it.name }

        /** Null-safe code lookup (inverse of the regional resolution). */
        fun forCodeOrNull(code: String?): TipCurrency? =
            entries.firstOrNull { it.name == code }

        /** Symbol shown for a stored picker code; region currency when unset/stale. */
        fun symbolForStoredCode(storedCode: String, regionCurrency: TipCurrency): String =
            (forCodeOrNull(storedCode) ?: regionCurrency).symbol

        fun regionSymbol(locale: Locale = Locale.getDefault()): String =
            resolveTipCurrency(locale).symbol
    }
}
