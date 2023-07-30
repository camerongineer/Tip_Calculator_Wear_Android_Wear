package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class TipCalcViewModel: ViewModel() {
    private val _billAmount = mutableStateOf(0f)
    private val _billAmountString = mutableStateOf("")
    private val _tipPercentValue = mutableStateOf(15)
    private val _calculatedTipAmount = derivedStateOf {
        _billAmount.value * (_tipPercentValue.value.toFloat() / 100)
    }

    private val _grandTotal = derivedStateOf {
        _billAmount.value + _calculatedTipAmount.value
    }

    fun onTipPercentChange(newAmount: Int) {
        _tipPercentValue.value = newAmount
    }

    fun onDigitTyped(char: Char) {
        if (_billAmountString.value.length < 6) {
            if (_billAmountString.value.isNotEmpty() || char != '0') {
                _billAmountString.value += char
            }
        }
    }

    fun onDeleteTyped() {
        if (_billAmountString.value.isNotEmpty()) {
            _billAmountString.value = _billAmountString.value
                .substring(0 until _billAmountString.value.length - 1)
        }
    }

    fun setBillAmountBlank() {
        _billAmountString.value = ""
    }

    fun getFormattedBillAmount(): String {
        val billFloat = _billAmountString.value.toFloatOrNull()
        return if (billFloat != null && billFloat > .01) {
            _billAmount.value = billFloat / 100
            "%.2f".format(billFloat / 100)
        } else {
            _billAmount.value = 0f
            "0.00"
        }
    }

    fun getTipPercentage() = _tipPercentValue.value

    fun getFormattedCalculatedTipAmount() = "%.2f".format(_calculatedTipAmount.value)

    fun getFormattedGrandTotal() = "%.2f".format(_grandTotal.value)

}