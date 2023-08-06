package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt


class TipCalcViewModel: ViewModel() {

    companion object {
        const val MAX_TIP_PENCENT = 100
    }

    private val _billAmount = mutableStateOf(0f)
    private val _billAmountString = mutableStateOf("")
    private val _tipPercentValue = mutableStateOf(15.0)

    private val _calculatedTipAmount = derivedStateOf {
        _billAmount.value * (_tipPercentValue.value.toFloat() / 100)
    }

    private val _grandTotal = derivedStateOf {
        _billAmount.value + _calculatedTipAmount.value
    }

    fun tipSliderUpClicked() {
        if (floor(_tipPercentValue.value) + 1 <= MAX_TIP_PENCENT) {
            _tipPercentValue.value = floor(_tipPercentValue.value) + 1
        }
    }

    fun tipSliderDownClicked() {
        if (_tipPercentValue.value > 100) {
            _tipPercentValue.value = 100.0
        } else if (ceil(_tipPercentValue.value) - 1 >= 0) {
            _tipPercentValue.value = ceil(_tipPercentValue.value) - 1
        }
    }

    fun onDigitTyped(char: Char) {
        if (_billAmountString.value.length < 6) {
            if (_billAmountString.value.isNotEmpty() || char != '0') {
                _billAmountString.value += char
            }
        }
        _tipPercentValue.value = min(MAX_TIP_PENCENT.toDouble(), _tipPercentValue.value)
    }

    fun onDeleteTyped() {
        if (_billAmountString.value.isNotEmpty()) {
            _billAmountString.value = _billAmountString.value
                .substring(0 until _billAmountString.value.length - 1)
        }
        _tipPercentValue.value = min(MAX_TIP_PENCENT.toDouble(), _tipPercentValue.value)
    }

    fun onRoundUpClicked() {
        viewModelScope.launch {
            val multipliedGrandTotal = (_grandTotal.value * 100).roundToInt()
            val multipliedRemainder = multipliedGrandTotal % 50
            val nextTotal = (if (multipliedRemainder == 0) {
                multipliedGrandTotal + 50
            } else {
                (multipliedGrandTotal - multipliedRemainder + 50)
            }).toFloat() / 100.0

            val billAmountNormalized = _billAmount.value / 10000.0
            val increment = 1 / (billAmountNormalized * 50000)

            if (_billAmount.value > 0.00) {
                while (_grandTotal.value <= nextTotal) {
                    _tipPercentValue.value += increment
                }
            }
        }
    }

    fun onRoundDownClicked() {
        viewModelScope.launch {
            val multipliedGrandTotal = (_grandTotal.value * 100).toInt()
            val multipliedRemainder = multipliedGrandTotal % 50
            val nextTotal = (if (multipliedRemainder == 0) {
                multipliedGrandTotal - 50
            } else {
                (multipliedGrandTotal - multipliedRemainder)
            }).toFloat() / 100.0

            val billAmountNormalized = _billAmount.value / 10000.0
            val increment = 1 / (billAmountNormalized * 50000)

            if (_billAmount.value > 0.0) {
                while (_grandTotal.value > nextTotal && _tipPercentValue.value > 0.0) {
                    _tipPercentValue.value -= increment
                }
                _tipPercentValue.value = if (_tipPercentValue.value < 0) {
                    0.0
                } else {
                    _tipPercentValue.value
                }
            }
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

    fun getFormattedTipPercentage() = "${_tipPercentValue.value.roundToInt()}"

    fun getEqualitySymbol() : String {
        val roundedPercentage = round(_tipPercentValue.value)
        return if (abs(_tipPercentValue.value - roundedPercentage) < .1) {
            ""
        } else if (_tipPercentValue.value > roundedPercentage) {
            ">"
        } else {
            "<"
        }
    }

    fun getFormattedCalculatedTipAmount() = "%.2f".format(_calculatedTipAmount.value)

    fun getFormattedGrandTotal() = "%.2f".format(_grandTotal.value)
}