package com.camerongineer.tipcalculatorwear

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

/**
 * The device region decides the app's currency symbol: the default locale's
 * currency code is mapped onto TipCurrency (UnsupportedRegion code falls back
 * to USD). That resolution lives in DataStoreManager, so it only executes
 * under Android. Each test swaps the process default locale before reading a
 * fresh DataStoreManager's currency symbol flow, and restores the original
 * default locale afterwards so nothing leaks into the other test classes.
 */
@RunWith(AndroidJUnit4::class)
class CurrencyRegionTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val originalDefaultLocale = Locale.getDefault()

    @After
    fun tearDown() {
        Locale.setDefault(originalDefaultLocale)
    }

    /** The symbol default is computed per DataStoreManager instance from the region. */
    private fun currencySymbolFor(region: Locale): String =
        runBlocking {
            Locale.setDefault(region)
            DataStoreManager(context).currencySymbolFlow.first()
        }

    @Test
    fun usRegionResolvesDollarSymbol() = assertEquals("$", currencySymbolFor(Locale.US))

    @Test
    fun canadaRegionResolvesDollarSymbol() = assertEquals("$", currencySymbolFor(Locale.CANADA))

    @Test
    fun germanyRegionResolvesEuroSymbol() = assertEquals("€", currencySymbolFor(Locale.GERMANY))

    @Test
    fun ukRegionResolvesPoundSymbol() = assertEquals("£", currencySymbolFor(Locale.UK))

    @Test
    fun chinaRegionResolvesYuanSymbol() = assertEquals("¥", currencySymbolFor(Locale.CHINA))

    @Test
    fun turkeyRegionResolvesLiraSymbol() = assertEquals("₺", currencySymbolFor(Locale("tr", "TR")))

    @Test
    fun indiaRegionResolvesRupeeSymbol() = assertEquals("₹", currencySymbolFor(Locale("", "IN")))

    @Test
    fun brazilRegionResolvesRealSymbol() = assertEquals("R$", currencySymbolFor(Locale("", "BR")))

    @Test
    fun switzerlandRegionResolvesFrancSymbol() = assertEquals("CHF", currencySymbolFor(Locale("", "CH")))

    @Test
    fun swedenRegionResolvesKronaSymbol() = assertEquals("kr", currencySymbolFor(Locale("", "SE")))

    @Test
    fun russiaRegionResolvesRubleSymbol() = assertEquals("₽", currencySymbolFor(Locale("", "RU")))

    @Test
    fun unsupportedRegionFallsBackToDollarSymbol() =
        assertEquals("$", currencySymbolFor(Locale.JAPAN))
}
