package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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

    private val _subTotal = mutableIntStateOf(0)
    private val _subTotalString = mutableStateOf("")
    private val _tipAmount = mutableIntStateOf(0)
    private val _tipPercentage = mutableDoubleStateOf(DEFAULT_TIP_PERCENTAGE)

    private val _grandTotal = derivedStateOf {
        _subTotal.intValue + _tipAmount.intValue
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
            _subTotal.intValue = subTotalDouble.roundToInt()
        } else {
            _subTotal.intValue = 0
        }
        setTipAmount()
    }

    fun tipPercentageIncrement() {
        if(_tipPercentage.doubleValue < MAX_TIP_PERCENT) {
            _tipPercentage.doubleValue = floor(_tipPercentage.doubleValue) + 1
            setTipAmount()
        }
    }

    fun tipPercentageDecrement() {
        if(_tipPercentage.doubleValue > 0) {
            _tipPercentage.doubleValue = minOf(ceil(_tipPercentage.doubleValue) - 1, MAX_TIP_PERCENT.toDouble())
            setTipAmount()
        }
    }

    private fun setTipAmount() {
        _tipAmount.intValue = (_subTotal.intValue * (_tipPercentage.doubleValue / 100.0)).roundToInt()
    }

    fun onRoundUpClicked() {
        viewModelScope.launch {
            val nextTotal = (if (_grandTotal.value % ROUNDING_NUM == 0) {
                _grandTotal.value + ROUNDING_NUM
            } else {
                _grandTotal.value - (_grandTotal.value % ROUNDING_NUM) + ROUNDING_NUM
            })

            if (_subTotal.intValue > 0) {
                while ((_subTotal.intValue + _tipAmount.intValue) < nextTotal) {
                    _tipAmount.intValue += 1
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

            if (_subTotal.intValue > 0) {
                while ((_subTotal.intValue + _tipAmount.intValue) > nextTotal && _tipAmount.intValue > 0) {
                    _tipAmount.intValue -= 1
                }
                setTipPercentage()
            }
        }
    }

    private fun setTipPercentage() {
        _tipPercentage.doubleValue = (_tipAmount.intValue.toDouble() / _subTotal.intValue.toDouble()) * 100
    }

    fun setSubTotalBlank() {
        _subTotalString.value = ""
        _subTotal.intValue = 0
        _tipAmount.intValue = 0
    }

    fun getFormattedSubTotal(): String {
        val subTotalDouble = _subTotalString.value.toDoubleOrNull()
        return if (subTotalDouble != null && subTotalDouble > 0) {
            "%.2f".format(subTotalDouble / 100)
        } else {
            "0.00"
        }
    }

    fun getSubtotal() = _subTotal.intValue

    fun getTipAmount() = _tipAmount.intValue

    fun getTipPercentage() = _tipPercentage.doubleValue

    fun getFormattedTipPercentage() = "${_tipPercentage.doubleValue.roundToInt()}"

    fun getEqualitySymbol() : String {
        val roundedTipPercentage = round(_tipPercentage.doubleValue)
        return if (abs(_tipPercentage.doubleValue - roundedTipPercentage) < .1) {
            ""
        } else if (_tipPercentage.doubleValue > roundedTipPercentage) {
            ">"
        } else {
            "<"
        }
    }

    fun getFormattedTipAmount(): String {
        return getFormattedAmountString(_tipAmount.intValue)
    }

    fun getFormattedGrandTotal() = getFormattedAmountString(_grandTotal.value)

    private fun getFormattedAmountString(amount: Int) = "%.2f".format(amount.toDouble() / 100)
}