package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStore: DataStoreManager): ViewModel() {

    companion object {
        const val DEFAULT_TIP_PERCENTAGE = 15.0
        const val DEFAULT_NUM_SPLIT = 2
    }

    private val _rememberSettings = mutableStateOf(true)
    private val _defaultTipPercentage = mutableDoubleStateOf(DEFAULT_TIP_PERCENTAGE)
    private val _defaultNumSplit = mutableIntStateOf(DEFAULT_NUM_SPLIT)
    private val _isPreciseSplit = mutableStateOf(true)

    init {
        viewModelScope.launch {
            _rememberSettings.value = dataStore.rememberSettingsFlow.first()
            _defaultTipPercentage.doubleValue = dataStore.defaultTipPercentageFlow.first()
            _defaultNumSplit.intValue = dataStore.defaultNumSplitFlow.first()
            _isPreciseSplit.value = dataStore.preciseSplitFlow.first()
        }
    }
}