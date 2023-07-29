package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class TipCalcViewModel: ViewModel() {
    private val _billAmount = mutableStateOf(0f)
    private val _tipPercentValue = mutableStateOf(15)
    private val _calculatedTipAmount = derivedStateOf {
        _billAmount.value * (_tipPercentValue.value.toFloat() / 100.0)
    }

    private val _grandTotal = derivedStateOf {
        _billAmount.value + _calculatedTipAmount.value
    }

    fun onBillAmountChanged(newAmount: String) {
        val newBillAmount = newAmount.replace(".", "").toIntOrNull()
        if (newBillAmount != null) {
            _billAmount.value = newBillAmount.toFloat() / 100
        }
    }

    fun onTipPercentChange(newAmount: Int) {
        _tipPercentValue.value = newAmount
    }

    fun getFormattedBillAmount(): String = "%.2f".format(_billAmount.value)

    fun getTipPercentage() = _tipPercentValue.value

    fun getFormattedCalculatedTipAmount() = "%.2f".format(_calculatedTipAmount.value)

    fun getFormattedGrandTotal() = "%.2f".format(_grandTotal.value)

}