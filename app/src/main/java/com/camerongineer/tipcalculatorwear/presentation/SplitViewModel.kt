package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplitViewModel(
    private val dataStore: DataStoreManager,
    subTotal: Int,
    tipAmount: Int,
): ViewModel() {

    private val _numSplit = mutableIntStateOf(DataStoreManager.DEFAULT_NUM_SPLIT)

    private val _currencySymbol = mutableStateOf("$")
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
        subTotal / _numSplit.intValue
    }

    private val _splitSubTotalRemainder = derivedStateOf {
        subTotal % _numSplit.intValue
    }

    private val _splitTipAmount = derivedStateOf {
        tipAmount / _numSplit.intValue
    }

    private val _splitTipRemainder = derivedStateOf {
        tipAmount % _numSplit.intValue
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

    fun getFormattedSplitSubTotal() = getFormattedAmountString(_splitSubTotal.value)

    fun getFormattedSplitSubTotalRemainder() = getFormattedAmountString(_splitSubTotalRemainder.value)

    fun getFormattedSplitTipAmount() = getFormattedAmountString(_splitTipAmount.value)

    fun getFormattedSplitTipAmountRemainder() = getFormattedAmountString(_splitTipRemainder.value)

    fun getFormattedSplitGrandTotal() = getFormattedAmountString(_splitGrandTotal.value)

    private fun saveNumSplit() {
        viewModelScope.launch {
            dataStore.saveNumSplit(numSplit = _numSplit.intValue)
        }
    }
}

private fun getFormattedAmountString(amount: Int) = "%.2f".format(amount.toDouble() / 100)
