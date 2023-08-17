package com.camerongineer.tipcalculatorwear.presentation.constants

import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager

class OptionsLists {
    companion object{
        val NUM_SPLIT_OPTIONS: List<String> =
            (2..DataStoreManager.MAX_NUM_SPLIT)
                .map { it.toString() }
        val TIP_PERCENT_OPTIONS: List<String> =
            (0..DataStoreManager.MAX_TIP_PERCENT)
                .map { it.toString() }
    }
}
