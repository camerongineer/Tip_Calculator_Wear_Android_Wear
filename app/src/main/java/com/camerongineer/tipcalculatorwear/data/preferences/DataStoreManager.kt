package com.camerongineer.tipcalculatorwear.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.camerongineer.tipcalculatorwear.presentation.constants.TipCurrency
import com.camerongineer.tipcalculatorwear.presentation.theme.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Currency
import java.util.Locale

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("SETTINGS_KEY")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        const val DEFAULT_TIP_PERCENTAGE = 15
        const val MAX_TIP_PERCENT = 100
        const val DEFAULT_NUM_SPLIT = 2
        const val MAX_NUM_SPLIT = 30
        const val DEFAULT_ROUNDING_NUM = 50

        val launchCountKey = intPreferencesKey("LAUNCH_COUNT_KEY")
        val tipPercentageKey = intPreferencesKey("TIP_PERCENT_KEY")
        val defaultTipPercentageKey = intPreferencesKey("DEFAULT_TIP_PERCENT_KEY")
        val rememberTipPercentageKey = booleanPreferencesKey("REMEMBER_TIP_PERCENTAGE_KEY")
        val numSplitKey = intPreferencesKey("NUM_SPLIT_KEY")
        val defaultNumSplitKey = intPreferencesKey("DEFAULT_NUM_SPLIT_KEY")
        val rememberNumSplitKey = booleanPreferencesKey("REMEMBER_NUM_SPLIT_KEY")
        val preciseSplitKey = booleanPreferencesKey("PRECISE_SPLIT_KEY")
        val roundNumKey = intPreferencesKey("ROUNDING_NUM_KEY")
        val currencySymbolKey = stringPreferencesKey("CURRENCY_SYMBOL_KEY")
        val themeKey = stringPreferencesKey("THEME_KEY")
        val languageKey = stringPreferencesKey("LANGUAGE_KEY")
    }

    private val tipCurrency = try {
        val currency: Currency = Currency.getInstance(Locale.getDefault())
        val currencyCode: String = currency.currencyCode
        Log.d("Currency", currencyCode)
        TipCurrency.valueOf(currencyCode)
    } catch (e: Exception) {
        TipCurrency.USD
    }

    val currencySymbolFlow: Flow<String> = dataFlow(
        key = currencySymbolKey,
        defaultValue = tipCurrency.symbol
    )

    val themeFlow: Flow<String> = dataFlow(
        key = themeKey,
        defaultValue = Theme.Dark.name
    )

    val languageFlow: Flow<String> = dataFlow(
        key = languageKey,
        defaultValue = ""
    )

    suspend fun saveTheme(themeName: String) {
        dataSave(themeName, themeKey)
    }

    val launchCountFlow: Flow<Int> = dataFlow(
        key = launchCountKey,
        defaultValue = 0
    )

    suspend fun incrementLaunchCount() {
        dataStore.edit {
            val currentLaunchCount = it[launchCountKey] ?: 0
            it[launchCountKey] = currentLaunchCount + 1
            Log.d("LAUNCH_COUNT", "The current launch count is ${it[launchCountKey]}")
        }
    }


    val tipPercentageFlow: Flow<Int> = dataFlow(
        key = tipPercentageKey,
        defaultValue = DEFAULT_TIP_PERCENTAGE
    )

    suspend fun saveTipPercentage(tipPercentage: Int) {
        dataSave(tipPercentage, tipPercentageKey)
    }


    val defaultTipPercentageFlow: Flow<Int> = dataFlow(
        key = defaultTipPercentageKey,
        defaultValue =  DEFAULT_TIP_PERCENTAGE
    )

    suspend fun saveDefaultTipPercentage(defaultTipPercentage: Int) {
        dataSave(defaultTipPercentage, defaultTipPercentageKey)
    }


    val rememberTipPercentageFlow: Flow<Boolean> = dataFlow(
        key = rememberTipPercentageKey,
        defaultValue = true
    )

    suspend fun saveRememberTipPercentage(rememberTipPercentage: Boolean) {
        dataSave(rememberTipPercentage, rememberTipPercentageKey)
    }


    val numSplitFlow: Flow<Int> = dataFlow(
        key = numSplitKey,
        defaultValue = DEFAULT_NUM_SPLIT
    )

    suspend fun saveNumSplit(numSplit: Int) {
        dataSave(numSplit, numSplitKey)
    }


    val defaultNumSplitFlow: Flow<Int> = dataFlow(
        key = defaultNumSplitKey,
        defaultValue = DEFAULT_NUM_SPLIT
    )

    suspend fun saveDefaultNumSplit(defaultNumSplit: Int) {
        dataSave(defaultNumSplit, defaultNumSplitKey)
    }


    val rememberNumSplitFlow: Flow<Boolean> = dataFlow(
        key = rememberNumSplitKey,
        defaultValue = false
    )

    suspend fun saveRememberNumSplit(rememberNumSplit: Boolean) {
        dataSave(rememberNumSplit, rememberNumSplitKey)
    }


    val preciseSplitFlow: Flow<Boolean> = dataFlow(
        key = preciseSplitKey,
        defaultValue = true
    )

    suspend fun savePreciseSplit(isPreciseSplit: Boolean) {
        dataSave(isPreciseSplit, preciseSplitKey)
    }

    val roundingNumFlow: Flow<Int> = dataFlow(
        key = roundNumKey,
        defaultValue = DEFAULT_ROUNDING_NUM
    )

    suspend fun saveRoundingNum(roundingNum: Int) {
        dataSave(roundingNum, roundNumKey)
    }

    suspend fun saveLanguage(languageCode: String) {
        dataSave(languageCode, languageKey)
    }

    private fun <T> dataFlow(
        key: Preferences.Key<T>,
        defaultValue: T
    ): Flow<T> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { it[key] ?: defaultValue }
    }

    private suspend fun <T> dataSave(
        value: T,
        key: Preferences.Key<T>
    ) {
        dataStore.edit { it[key] = value }
    }
}

