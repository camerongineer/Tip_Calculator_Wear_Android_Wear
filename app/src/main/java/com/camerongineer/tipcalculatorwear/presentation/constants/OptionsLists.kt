package com.camerongineer.tipcalculatorwear.presentation.constants

import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString

class OptionsLists {
    companion object{
        val NUM_SPLIT_OPTIONS =
            (2..DataStoreManager.MAX_NUM_SPLIT)
                .map { OptionsItem(it, false) }
        val TIP_PERCENT_OPTIONS =
            (0..DataStoreManager.MAX_TIP_PERCENT)
                .map { OptionsItem(it, false) }
        val ROUNDING_INCREMENTS = listOf(50, 100, 250, 500)
            .map { OptionsItem(it, true) }
        val LANGUAGE_OPTIONS = listOf(TipLanguage.DE,
            TipLanguage.EN,
            TipLanguage.ES,
            TipLanguage.FR,
            TipLanguage.HI,
            TipLanguage.PT).map {
                OptionsItem(it, false)
        }
        // Stored TipCurrency codes; "" (first row) = region default. Glyphs
        // are script characters, locale-invariant, so they are not resource strings.
        val CURRENCY_OPTIONS =
            listOf(OptionsItem("", nameResId = R.string.currency_default)) +
                TipCurrency.pickerCurrencies.map {
                    OptionsItem(it.name, nameResId = it.nameResId, symbol = it.symbol)
                }
    }
}

class OptionsItem<T>(
    val value: T,
    private val isCurrency: Boolean = false,
    val nameResId: Int? = null,
    val symbol: String = "",
) {
    override fun toString(): String {
        return if (isCurrency) getFormattedAmountString(value.toString().toInt()) else value.toString()
    }
}
