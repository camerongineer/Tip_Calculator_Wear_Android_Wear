package com.camerongineer.tipcalculatorwear.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.camerongineer.tipcalculatorwear.presentation.SplitViewModel
import com.camerongineer.tipcalculatorwear.presentation.TipCalcViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager(context: Context) {

    private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("SETTINGS_KEY")
    private val dataStore = context.dataStore

    companion object {
        val tipPercentageKey = doublePreferencesKey("TIP_PERCENT_KEY")
        val defaultTipPercentageKey = doublePreferencesKey("DEFAULT_TIP_PERCENT_KEY")
        val numSplitKey = intPreferencesKey("NUM_SPLIT_KEY")
        val defaultNumSplitKey = intPreferencesKey("DEFAULT_NUM_SPLIT_KEY")

    }



    val tipPercentageFlow: Flow<Double> = dataFlow(
        key = tipPercentageKey,
        defaultValue = TipCalcViewModel.DEFAULT_TIP_PERCENTAGE
    )

    suspend fun saveTipPercentage(tipPercentage: Double) {
        dataSave(tipPercentage, tipPercentageKey)
    }



    val defaultTipPercentageFlow: Flow<Double> = dataFlow(
        key = defaultTipPercentageKey,
        defaultValue = TipCalcViewModel.DEFAULT_TIP_PERCENTAGE
    )

    suspend fun saveDefaultTipPercentage(defaultTipPercentage: Double) {
        dataSave(defaultTipPercentage, defaultTipPercentageKey)
    }



    val numSplitFlow: Flow<Int> = dataFlow(
        key = numSplitKey,
        defaultValue = SplitViewModel.DEFAULT_SPLIT_NUM
    )

    suspend fun saveNumSplit(numSplit: Int) {
        dataSave(numSplit, numSplitKey)
    }



    val defaultNumSplitFlow: Flow<Int> = dataFlow(
        key = defaultNumSplitKey,
        defaultValue = SplitViewModel.DEFAULT_SPLIT_NUM
    )

    suspend fun saveDefaultNumSplit(defaultNumSplit: Int) {
        dataSave(defaultNumSplit, defaultNumSplitKey)
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

