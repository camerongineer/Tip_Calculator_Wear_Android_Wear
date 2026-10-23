package com.camerongineer.tipcalculatorwear

import com.camerongineer.tipcalculatorwear.presentation.constants.TipCurrency
import com.camerongineer.tipcalculatorwear.utils.resolveTipCurrency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.Locale

/**
 * Ground truth for the region->currency resolution, curated once against
 * ISO 4217 / Unicode CLDR and treated as the authoritative reference:
 *
 * 1. every region in the review table resolves to its listed currency,
 * 2. the exhaustive sweep over ALL ISO country codes never throws and always
 *    agrees with the fallback rule (mapped code -> its currency, otherwise
 *    USD),
 * 3. no region that garbage-collects onto USD is accidentally listed as
 *    supporting a different symbol.
 */
class CurrencyResolutionTest {

    /** Country code -> expected TipCurrency; USA's fallback is implicit below. */
    private val reviewedRegions: Map<String, TipCurrency> = mapOf(
        // North America
        "US" to TipCurrency.USD, "CA" to TipCurrency.CAD, "MX" to TipCurrency.MXN,
        // South America
        "BR" to TipCurrency.BRL,
        // Europe (euro zone sample + national currencies)
        "DE" to TipCurrency.EUR, "FR" to TipCurrency.EUR, "NL" to TipCurrency.EUR,
        "ES" to TipCurrency.EUR, "IT" to TipCurrency.EUR, "IE" to TipCurrency.EUR,
        "AT" to TipCurrency.EUR, "BE" to TipCurrency.EUR, "PT" to TipCurrency.EUR,
        "FI" to TipCurrency.EUR, "GR" to TipCurrency.EUR,
        "GB" to TipCurrency.GBP, "CH" to TipCurrency.CHF,
        "SE" to TipCurrency.SEK, "NO" to TipCurrency.NOK, "DK" to TipCurrency.DKK,
        // Asia & Pacific
        "CN" to TipCurrency.CNY, "HK" to TipCurrency.HKD, "IN" to TipCurrency.INR,
        "SG" to TipCurrency.SGD, "AU" to TipCurrency.AUD, "NZ" to TipCurrency.NZD,
        // Middle East
        "TR" to TipCurrency.TRY,
        // Russia
        "RU" to TipCurrency.RUB
    )

    @Test
    fun `every reviewed region resolves to its listed currency`() {
        reviewedRegions.forEach { (country, expected) ->
            assertEquals("region $country", expected, resolveTipCurrency(Locale("", country)))
        }
    }

    @Test
    fun `every reviewed region also resolves through its Symbol`() {
        // The displayed symbol must be exactly the reviewed table's currency symbol.
        reviewedRegions.forEach { (country, expected) ->
            val resolved = resolveTipCurrency(Locale("", country))
            assertEquals(
                "region $country",
                expected.symbol,
                resolved.symbol
            )
        }
    }

    @Test
    fun `sweep over all Iso regions never throws and follows the fallback rule`() {
        for (country in Locale.getISOCountries()) {
            val locale = Locale("", country)
            val expected = try {
                val code = Currency.getInstance(locale).currencyCode
                TipCurrency.forCodeOrNull(code) ?: TipCurrency.USD
            } catch (e: Exception) {
                // An unresolvable region must fall back, never crash the app.
                TipCurrency.USD
            }
            assertEquals("region $country", expected, resolveTipCurrency(locale))
        }
    }

    @Test
    fun `unmapped markets fall back to USD`() {
        // Zero-decimal or otherwise unsupported markets stay on the USD default.
        listOf("JP", "KR", "PL", "ZA", "AE", "IL").forEach { country ->
            assertEquals(
                "region $country",
                TipCurrency.USD,
                resolveTipCurrency(Locale("", country))
            )
        }
    }

    @Test
    fun `fallback is a genuine member of the enum`() {
        assertTrue(TipCurrency.USD in TipCurrency.entries)
    }

    @Test
    fun `every symbol is usable inside the two-decimal amount renderer`() {
        // Zero-decimal symbols must stay out of the table until AmountFormat
        // learns getDefaultFractionDigits() (see TipCurrency docs).
        val zeroDecimalCodes = setOf("JPY", "KRW", "ISK", "HUF", "CLP", "VND")
        TipCurrency.entries.forEach { currency ->
            val digits = Currency.getInstance(currency.name).defaultFractionDigits
            assertTrue(
                "$currency renders with $digits fraction digit(s); " +
                    "not compatible with the 2-decimal renderer",
                digits in 2..2
            )
        }
    }
}
