package com.camerongineer.tipcalculatorwear.presentation.constants

import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString

class OptionsLists {
    companion object{
        val NUM_SPLIT_OPTIONS: List<OptionsItem> =
            (2..DataStoreManager.MAX_NUM_SPLIT)
                .map { OptionsItem(it, false) }
        val TIP_PERCENT_OPTIONS: List<OptionsItem> =
            (0..DataStoreManager.MAX_TIP_PERCENT)
                .map { OptionsItem(it, false) }
        val ROUNDING_INCREMENTS = listOf(50, 100, 250, 500)
            .map { OptionsItem(it, true) }
    }
}

class OptionsItem(val value: Int, private val isCurrency: Boolean) {
    override fun toString(): String {
        return if (isCurrency) getFormattedAmountString(value) else value.toString()
    }
}
