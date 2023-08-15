package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SettingsViewModel(private val dataStore: DataStoreManager): ViewModel() {

    private val _defaultTipPercentage = mutableDoubleStateOf(DataStoreManager.DEFAULT_TIP_PERCENTAGE.toDouble())

    private val _rememberTipPercentage = mutableStateOf(true)
    val rememberTipPercentage = _rememberTipPercentage

    private val _defaultNumSplit = mutableIntStateOf(DataStoreManager.DEFAULT_NUM_SPLIT)

    private val _rememberNumSplit = mutableStateOf(false)
    val rememberNumSplit = _rememberNumSplit

    private val _isPreciseSplit = mutableStateOf(true)
    val isPreciseSplit = _isPreciseSplit

    private val _currencySymbol = mutableStateOf("$")
    val currencySymbol = _currencySymbol

    init {
        viewModelScope.launch {
            _defaultTipPercentage.doubleValue = dataStore.defaultTipPercentageFlow.first().toDouble()
            _rememberTipPercentage.value = dataStore.rememberTipPercentageFlow.first()
            _defaultNumSplit.intValue = dataStore.defaultNumSplitFlow.first()
            _rememberNumSplit.value = dataStore.rememberNumSplitFlow.first()
            _isPreciseSplit.value = dataStore.preciseSplitFlow.first()
            _currencySymbol.value = dataStore.currencySymbolFlow.first()
        }
    }

    fun getDefaultTipPercentage(): Int = _defaultTipPercentage.doubleValue.roundToInt()

    fun setDefaultTipPercentage(defaultTipPercentage: Int) {
        _defaultTipPercentage.doubleValue = defaultTipPercentage.toDouble()
        viewModelScope.launch {
            dataStore.saveDefaultTipPercentage(defaultTipPercentage)
        }
    }

    fun setRememberTipPercentage(rememberTipPercentage: Boolean) {
        _rememberTipPercentage.value = rememberTipPercentage
        viewModelScope.launch {
            dataStore.saveRememberTipPercentage(rememberTipPercentage)
        }
    }

    fun setDefaultNumSplit(defaultNumSplit: Int) {
        _defaultNumSplit.intValue = defaultNumSplit
        viewModelScope.launch {
            dataStore.saveDefaultNumSplit(defaultNumSplit)
        }
    }

    fun setRememberNumSplit(rememberNumSplit: Boolean) {
        _rememberNumSplit.value = rememberNumSplit
        viewModelScope.launch {
            dataStore.saveRememberNumSplit(rememberNumSplit)
        }
    }

    fun setIsPreciseSplit(isPreciseSplit: Boolean) {
        _isPreciseSplit.value = isPreciseSplit
        viewModelScope.launch {
            dataStore.savePreciseSplit(isPreciseSplit)
        }
    }
}
