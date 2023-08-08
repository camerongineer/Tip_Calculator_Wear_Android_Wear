package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt


class TipCalcViewModel: ViewModel() {

    companion object {
        const val MAX_TIP_PERCENT = 100
        const val DEFAULT_TIP_PERCENTAGE = 15.0
        const val ROUNDING_NUM = 50
    }

    private val _subTotal = mutableStateOf(0)
    private val _subTotalString = mutableStateOf("")
    private val _tipAmount = mutableStateOf(0)
    private val _tipPercentage = mutableStateOf(DEFAULT_TIP_PERCENTAGE)

    private val _grandTotal = derivedStateOf {
        _subTotal.value + _tipAmount.value
    }

    fun onDigitTyped(char: Char) {
        if (_subTotalString.value.length < 6) {
            if (_subTotalString.value.isNotEmpty() || char != '0') {
                _subTotalString.value += char
                setSubTotal()
            }
        }
    }

    fun onDeleteTyped() {
        if (_subTotalString.value.isNotEmpty()) {
            _subTotalString.value = _subTotalString.value
                .substring(0 until _subTotalString.value.length - 1)
            setSubTotal()
        }
    }

    private fun setSubTotal() {
        val subTotalDouble = _subTotalString.value.toDoubleOrNull()
        if (subTotalDouble != null) {
            _subTotal.value = subTotalDouble.roundToInt()
        } else {
            _subTotal.value = 0
        }
        setTipAmount()
    }

    fun tipPercentageIncrement() {
        if(_tipPercentage.value < MAX_TIP_PERCENT) {
            _tipPercentage.value = floor(_tipPercentage.value) + 1
            setTipAmount()
        }
    }

    fun tipPercentageDecrement() {
        if(_tipPercentage.value > 0) {
            _tipPercentage.value = minOf(ceil(_tipPercentage.value) - 1, MAX_TIP_PERCENT.toDouble())
            setTipAmount()
        }
    }

    private fun setTipAmount() {
        _tipAmount.value = (_subTotal.value * (_tipPercentage.value / 100.0)).roundToInt()
    }

    fun onRoundUpClicked() {
        viewModelScope.launch {
            val nextTotal = (if (_grandTotal.value % ROUNDING_NUM == 0) {
                _grandTotal.value + ROUNDING_NUM
            } else {
                _grandTotal.value - (_grandTotal.value % ROUNDING_NUM) + ROUNDING_NUM
            })

            if (_subTotal.value > 0) {
                while ((_subTotal.value + _tipAmount.value) < nextTotal) {
                    _tipAmount.value += 1
                }
                setTipPercentage()
            }
        }
    }

    fun onRoundDownClicked() {
        viewModelScope.launch {
            val nextTotal = (if (_grandTotal.value % ROUNDING_NUM == 0) {
                _grandTotal.value - ROUNDING_NUM
            } else {
                _grandTotal.value - (_grandTotal.value % ROUNDING_NUM)
            })

            if (_subTotal.value > 0) {
                while ((_subTotal.value + _tipAmount.value) > nextTotal && _tipAmount.value > 0) {
                    _tipAmount.value -= 1
                }
                setTipPercentage()
            }
        }
    }

    private fun setTipPercentage() {
        _tipPercentage.value = (_tipAmount.value.toDouble() / _subTotal.value.toDouble()) * 100
    }

    fun setSubTotalBlank() {
        _subTotalString.value = ""
    }

    fun getFormattedSubTotal(): String {
        val subTotalDouble = _subTotalString.value.toDoubleOrNull()
        return if (subTotalDouble != null && subTotalDouble > 0) {
            "%.2f".format(subTotalDouble / 100)
        } else {
            "0.00"
        }
    }

    fun getSubtotal() = _subTotal.value

    fun getTipAmount() = _tipAmount.value

    fun getTipPercentage() = _tipPercentage.value

    fun getFormattedTipPercentage() = "${_tipPercentage.value.roundToInt()}"

    fun getEqualitySymbol() : String {
        val roundedTipPercentage = round(_tipPercentage.value)
        return if (abs(_tipPercentage.value - roundedTipPercentage) < .1) {
            ""
        } else if (_tipPercentage.value > roundedTipPercentage) {
            ">"
        } else {
            "<"
        }
    }

    fun getFormattedTipAmount(): String {
        return getFormattedAmountString(_tipAmount.value)
    }

    fun getFormattedGrandTotal() = getFormattedAmountString(_grandTotal.value)

    private fun getFormattedAmountString(amount: Int) = "%.2f".format(amount.toDouble() / 100)
}