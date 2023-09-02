package com.camerongineer.tipcalculatorwear.presentation.constants

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
            TipLanguage.PT).map {
                OptionsItem(it, false)
        }
    }
}

class OptionsItem<T>(val value: T, private val isCurrency: Boolean) {
    override fun toString(): String {
        return if (isCurrency) getFormattedAmountString(value.toString().toInt()) else value.toString()
    }
}
