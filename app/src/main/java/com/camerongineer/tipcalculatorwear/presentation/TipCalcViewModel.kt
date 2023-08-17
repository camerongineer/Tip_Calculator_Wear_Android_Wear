package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.constants.TipCurrency
import com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString
import com.camerongineer.tipcalculatorwear.utils.roundDown
import com.camerongineer.tipcalculatorwear.utils.roundUp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt


class TipCalcViewModel(val dataStore: DataStoreManager) : ViewModel() {

    private val _subTotal = mutableIntStateOf(0)
    fun getSubtotal() = _subTotal.intValue

    private val _subTotalString = mutableStateOf("")

    private val _tipAmount = mutableIntStateOf(0)
    fun getTipAmount() = _tipAmount.intValue

    private val _tipPercentage =
        mutableDoubleStateOf(
            DataStoreManager.DEFAULT_TIP_PERCENTAGE.toDouble()
        )
    fun getTipPercentage() = _tipPercentage.doubleValue

    private var _isFirstLaunch = true
    fun isFirstLaunch() = _isFirstLaunch

    private val _currencySymbol = mutableStateOf(TipCurrency.USD.symbol)
    fun getCurrencySymbol() = _currencySymbol.value

    private val _roundingNum = DataStoreManager.DEFAULT_ROUNDING_NUM

    init {
        viewModelScope.launch {
            dataStore.incrementLaunchCount()
            _tipPercentage.doubleValue = if (dataStore.rememberTipPercentageFlow.first()) {
                dataStore.tipPercentageFlow.first().toDouble()
            } else {
                dataStore.defaultTipPercentageFlow.first().toDouble()
            }
            _currencySymbol.value = dataStore.currencySymbolFlow.first()
        }
    }


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
        if(_tipPercentage.doubleValue < DataStoreManager.MAX_TIP_PERCENT) {
            _tipPercentage.doubleValue = floor(_tipPercentage.doubleValue) + 1
            setTipAmount()
        }
    }

    fun tipPercentageDecrement() {
        if(_tipPercentage.doubleValue > 0) {
            _tipPercentage.doubleValue =
                minOf(ceil(_tipPercentage.doubleValue) - 1,
                    DataStoreManager.MAX_TIP_PERCENT.toDouble())
            setTipAmount()
        }
    }

    fun resetTipPercentage() {
        viewModelScope.launch {
            _tipPercentage.doubleValue = dataStore.defaultTipPercentageFlow.first().toDouble()
            setTipAmount()
        }
    }

    private fun setTipAmount() {
        _tipAmount.intValue =
            (_subTotal.intValue * (_tipPercentage.doubleValue / 100.0)).roundToInt()
        saveTipPercentage()
    }

    fun onRoundUpClicked() {
        viewModelScope.launch {
            if (_subTotal.intValue > 0) {
                val nextTotal = roundUp(_grandTotal.value, _roundingNum)
                _tipAmount.intValue = nextTotal - _subTotal.intValue
                setTipPercentage()
            }
        }
    }

    fun onRoundDownClicked() {
        viewModelScope.launch {
            if (_subTotal.intValue > 0) {
                val nextTotal = roundDown(_grandTotal.value, _roundingNum)
                _tipAmount.intValue = nextTotal - _subTotal.intValue
                setTipPercentage()
            }
        }
    }

    private fun setTipPercentage() {
        _tipPercentage.doubleValue =
            (_tipAmount.intValue.toDouble() / _subTotal.intValue.toDouble()) * 100
    }

    fun setSubTotalBlank() {
        _subTotalString.value = ""
        _subTotal.intValue = 0
        _tipAmount.intValue = 0
    }

    fun markAsNotFirstLaunch() {
        _isFirstLaunch = false
    }

    fun getFormattedSubTotal(): String {
        val subTotalDouble = _subTotalString.value.toDoubleOrNull()
        return if (subTotalDouble != null && subTotalDouble > 0) {
            getFormattedAmountString(subTotalDouble.roundToInt())
        } else {
            "0.00"
        }
    }

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

    fun getFormattedGrandTotal() =
        getFormattedAmountString(_grandTotal.value)

    private fun saveTipPercentage() {
        viewModelScope.launch {
            dataStore.saveTipPercentage(_tipPercentage.doubleValue.roundToInt())
        }
    }
}
