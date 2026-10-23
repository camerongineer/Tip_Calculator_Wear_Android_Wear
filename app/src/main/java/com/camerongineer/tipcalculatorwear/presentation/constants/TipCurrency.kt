package com.camerongineer.tipcalculatorwear.presentation.constants

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
    INR("₹"),
    MXN("$"),
    NZD("$"),
    RUB("₽"),
    SGD("$");

    companion object {
        /** Null-safe code lookup (inverse of the regional resolution). */
        fun forCodeOrNull(code: String?): TipCurrency? =
            entries.firstOrNull { it.name == code }
    }
}