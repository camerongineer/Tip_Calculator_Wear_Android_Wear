package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.constants.TipCurrency
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplitViewModel(
    private val dataStore: DataStoreManager,
    subTotal: Int,
    tipAmount: Int,
    private val isPreciseSplit: MutableState<Boolean>
): ViewModel() {

    private val _numSplit = mutableIntStateOf(DataStoreManager.DEFAULT_NUM_SPLIT)

    private val _currencySymbol = mutableStateOf(TipCurrency.USD.symbol)
    fun getCurrencySymbol() = _currencySymbol.value

    init {
        viewModelScope.launch {
            _numSplit.intValue = if (dataStore.rememberNumSplitFlow.first()) {
                dataStore.numSplitFlow.first()
            } else {
                dataStore.defaultNumSplitFlow.first()
            }
            _currencySymbol.value = dataStore.currencySymbolFlow.first()
        }
    }

    private val _splitSubTotal = derivedStateOf {
        if (isPreciseSplit.value || subTotal % _numSplit.intValue == 0) {
            subTotal / _numSplit.intValue
        } else {
            (subTotal / _numSplit.intValue) + 1
        }
    }

    private val _splitSubTotalRemainder = derivedStateOf {
        val remainder = subTotal % _numSplit.intValue
        if (isPreciseSplit.value) remainder else 0
    }

    private val _splitTipAmount = derivedStateOf {
        tipAmount / _numSplit.intValue
    }

    private val _splitTipRemainder = derivedStateOf {
        val remainder = tipAmount % _numSplit.intValue
        if (isPreciseSplit.value) remainder else 0
    }

    private val _splitGrandTotal = derivedStateOf {
        _splitSubTotal.value + _splitTipAmount.value
    }


    fun onSplitUpClicked() {
        if (_numSplit.intValue < DataStoreManager.MAX_NUM_SPLIT) {
            _numSplit.intValue++
            saveNumSplit()
        }
    }

    fun onSplitDownClicked() {
        if (_numSplit.intValue > 2) {
            _numSplit.intValue--
            saveNumSplit()
        }
    }

    fun resetNumSplit() {
        viewModelScope.launch {
            _numSplit.intValue = dataStore.defaultNumSplitFlow.first()
            saveNumSplit()
        }
    }

    fun getNumSplit() = _numSplit.intValue

    fun getSplitSubTotalRemainder(): Int = _splitSubTotalRemainder.value

    fun getSplitTipAmountRemainder(): Int = _splitTipRemainder.value

    fun getFormattedSplitSubTotal() =
        com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString(_splitSubTotal.value)

    fun getFormattedSplitSubTotalRemainder() =
        com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString(_splitSubTotalRemainder.value)

    fun getFormattedSplitTipAmount() =
        com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString(_splitTipAmount.value)

    fun getFormattedSplitTipAmountRemainder() =
        com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString(_splitTipRemainder.value)

    fun getFormattedSplitGrandTotal() =
        com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString(_splitGrandTotal.value)

    private fun saveNumSplit() {
        viewModelScope.launch {
            dataStore.saveNumSplit(numSplit = _numSplit.intValue)
        }
    }
}