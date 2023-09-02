package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.constants.TipCurrency
import com.camerongineer.tipcalculatorwear.presentation.constants.TipLanguage
import com.camerongineer.tipcalculatorwear.presentation.theme.Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStore: DataStoreManager): ViewModel() {

    private val _defaultTipPercentage = mutableIntStateOf(DataStoreManager.DEFAULT_TIP_PERCENTAGE)
    val defaultTipPercentage = _defaultTipPercentage

    private val _rememberTipPercentage = mutableStateOf(true)
    val rememberTipPercentage = _rememberTipPercentage

    private val _defaultNumSplit = mutableIntStateOf(DataStoreManager.DEFAULT_NUM_SPLIT)
    val defaultNumSplit = _defaultNumSplit

    private val _rememberNumSplit = mutableStateOf(false)
    val rememberNumSplit = _rememberNumSplit

    private val _isPreciseSplit = mutableStateOf(true)
    val isPreciseSplit = _isPreciseSplit

    private val _currencySymbol = mutableStateOf(TipCurrency.USD.symbol)
    val currencySymbol = _currencySymbol

    private val _roundingNum = mutableIntStateOf(DataStoreManager.DEFAULT_ROUNDING_NUM)
    val roundingNum = _roundingNum

    fun getThemeFlow() = dataStore.themeFlow
    fun saveTheme(theme: Theme) = viewModelScope.launch { dataStore.saveTheme(theme.name) }

    fun getLanguageFlow() = dataStore.languageFlow
    fun saveLanguage(tipLanguage: TipLanguage) = viewModelScope.launch {
        dataStore.saveLanguage(tipLanguage.name)
    }

    init {
        viewModelScope.launch {
            _defaultTipPercentage.intValue = dataStore.defaultTipPercentageFlow.first()
            _rememberTipPercentage.value = dataStore.rememberTipPercentageFlow.first()
            _defaultNumSplit.intValue = dataStore.defaultNumSplitFlow.first()
            _rememberNumSplit.value = dataStore.rememberNumSplitFlow.first()
            _isPreciseSplit.value = dataStore.preciseSplitFlow.first()
            _roundingNum.intValue = dataStore.roundingNumFlow.first()
            _currencySymbol.value = dataStore.currencySymbolFlow.first()
        }
    }

    fun setDefaultTipPercentage(defaultTipPercentage: Int) {
        _defaultTipPercentage.intValue = defaultTipPercentage
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

    fun setRoundingNum(roundingNum: Int) {
        _roundingNum.intValue = roundingNum
        viewModelScope.launch {
            dataStore.saveRoundingNum(roundingNum)
        }
    }
}
