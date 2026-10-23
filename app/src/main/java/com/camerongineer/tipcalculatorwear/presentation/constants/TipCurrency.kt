package com.camerongineer.tipcalculatorwear.presentation.constants

/**
 * Region-resolvable currencies across the app's target markets. The symbol is
 * the currency's own display symbol (not the English-locale CLDR short form),
 * reviewed against ISO 4217 + Unicode CLDR once and pinned by
 * CurrencyResolutionTest.
 *
 * Only currencies whose amount format works with the app's decimal-max-2
 * rendering are listed: zero-decimal currencies (JPY ¥, KRW ₩) are
 * deliberately absent until AmountFormat respects getDefaultFractionDigits().
 */
enum class TipCurrency(val symbol: String) {
    CAD("$"),
    CNY("¥"),
    EUR("€"),
    GBP("£"),
    HKD("$"),
    TRY("₺"),
    USD("$"),
    AUD("$"),
    BRL("R$"),
    CHF("CHF"),
    DKK("kr"),
    INR("₹"),
    MXN("$"),
    NOK("kr"),
    NZD("$"),
    RUB("₽"),
    SEK("kr"),
    SGD("$");

    companion object {
        /** Null-safe code lookup (inverse of the regional resolution). */
        fun forCodeOrNull(code: String?): TipCurrency? =
            entries.firstOrNull { it.name == code }
    }
}