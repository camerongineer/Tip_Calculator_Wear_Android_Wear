package com.camerongineer.tipcalculatorwear.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.camerongineer.tipcalculatorwear.presentation.SettingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("SETTINGS_KEY")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val rememberSettingsKey = booleanPreferencesKey("REMEMBER_SETTINGS_KEY")
        val launchCountKey = intPreferencesKey("LAUNCH_COUNT_KEY")
        val tipPercentageKey = doublePreferencesKey("TIP_PERCENT_KEY")
        val defaultTipPercentageKey = doublePreferencesKey("DEFAULT_TIP_PERCENT_KEY")
        val numSplitKey = intPreferencesKey("NUM_SPLIT_KEY")
        val defaultNumSplitKey = intPreferencesKey("DEFAULT_NUM_SPLIT_KEY")
        val preciseSplitKey = booleanPreferencesKey("PRECISE_SPLIT_KEY")
    }


    val rememberSettingsFlow: Flow<Boolean> = dataFlow(
        key = rememberSettingsKey,
        defaultValue = true
    )

    suspend fun saveRememberSettings(rememberSettings: Boolean) {
        dataSave(rememberSettings, rememberSettingsKey)
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


    val tipPercentageFlow: Flow<Double> = dataFlow(
        key = tipPercentageKey,
        defaultValue = SettingsViewModel.DEFAULT_TIP_PERCENTAGE
    )

    suspend fun saveTipPercentage(tipPercentage: Double) {
        dataSave(tipPercentage, tipPercentageKey)
    }



    val defaultTipPercentageFlow: Flow<Double> = dataFlow(
        key = defaultTipPercentageKey,
        defaultValue = SettingsViewModel.DEFAULT_TIP_PERCENTAGE
    )

    suspend fun saveDefaultTipPercentage(defaultTipPercentage: Double) {
        dataSave(defaultTipPercentage, defaultTipPercentageKey)
    }



    val numSplitFlow: Flow<Int> = dataFlow(
        key = numSplitKey,
        defaultValue = SettingsViewModel.DEFAULT_NUM_SPLIT
    )

    suspend fun saveNumSplit(numSplit: Int) {
        dataSave(numSplit, numSplitKey)
    }



    val defaultNumSplitFlow: Flow<Int> = dataFlow(
        key = defaultNumSplitKey,
        defaultValue = SettingsViewModel.DEFAULT_NUM_SPLIT
    )

    suspend fun saveDefaultNumSplit(defaultNumSplit: Int) {
        dataSave(defaultNumSplit, defaultNumSplitKey)
    }



    val preciseSplitFlow: Flow<Boolean> = dataFlow(
        key = preciseSplitKey,
        defaultValue = true
    )

    suspend fun savePreciseSplit(isPreciseSplit: Boolean) {
        dataSave(isPreciseSplit, preciseSplitKey)
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

