package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SplitViewModel(
    subTotal: Int,
    tipAmount: Int,
    private val numSplit: MutableState<Int> = mutableStateOf(DEFAULT_SPLIT_NUM)
): ViewModel() {

    companion object {
        const val DEFAULT_SPLIT_NUM = 2
        const val MAX_NUM_SPLIT = 20

    }

    private val _splitSubTotal = derivedStateOf {
        subTotal / numSplit.value
    }

    private val _splitSubTotalRemainder = derivedStateOf {
        subTotal % numSplit.value
    }

    private val _splitTipAmount = derivedStateOf {
        tipAmount / numSplit.value
    }

    private val _splitTipRemainder = derivedStateOf {
        tipAmount % numSplit.value
    }

    private val _splitGrandTotal = derivedStateOf {
        _splitSubTotal.value + _splitTipAmount.value
    }


    fun onSplitUpClicked() {
        if (numSplit.value < MAX_NUM_SPLIT) numSplit.value++
    }

    fun onSplitDownClicked() {
        if (numSplit.value > 2) numSplit.value--
    }

    fun getNumSplit() = numSplit.value

    fun getFormattedSplitSubTotal() = getFormattedAmountString(_splitSubTotal.value)

    fun getFormattedSplitSubTotalRemainder()= getFormattedAmountString(_splitSubTotalRemainder.value)

    fun getFormattedSplitTipAmount() = getFormattedAmountString(_splitTipAmount.value)

    fun getFormattedSplitGrandTotal() = getFormattedAmountString(_splitGrandTotal.value)

    private fun getFormattedAmountString(amount: Int) = "%.2f".format(amount.toDouble() / 100)
}