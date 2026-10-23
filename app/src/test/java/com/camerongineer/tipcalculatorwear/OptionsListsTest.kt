package com.camerongineer.tipcalculatorwear

import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString
import com.camerongineer.tipcalculatorwear.presentation.constants.OptionsLists
import com.camerongineer.tipcalculatorwear.presentation.constants.TipLanguage
import org.junit.Assert.assertEquals
import org.junit.Test

class OptionsListsTest {

    @Test
    fun `split options cover two to the max`() {
        val options = OptionsLists.NUM_SPLIT_OPTIONS
        assertEquals(DataStoreManager.MAX_NUM_SPLIT - DataStoreManager.DEFAULT_NUM_SPLIT + 1, options.size)
        assertEquals(DataStoreManager.DEFAULT_NUM_SPLIT, options.first().value)
        assertEquals(DataStoreManager.MAX_NUM_SPLIT, options.last().value)
    }

    @Test
    fun `tip percent options cover zero to the max`() {
        val options = OptionsLists.TIP_PERCENT_OPTIONS
        assertEquals(DataStoreManager.MAX_TIP_PERCENT + 1, options.size)
        assertEquals(0, options.first().value)
        assertEquals(DataStoreManager.MAX_TIP_PERCENT, options.last().value)
    }

    @Test
    fun `rounding increments are the known money steps in ascending order`() {
        assertEquals(listOf(50, 100, 250, 500), OptionsLists.ROUNDING_INCREMENTS.map { it.value })
        assertEquals(
            OptionsLists.ROUNDING_INCREMENTS.map { it.value },
            OptionsLists.ROUNDING_INCREMENTS.map { it.value }.sorted()
        )
    }

    @Test
    fun `every supported language appears in the options`() {
        assertEquals(
            TipLanguage.entries.map { it.name }.sorted(),
            OptionsLists.LANGUAGE_OPTIONS.map { (it.value as TipLanguage).name }
        )
    }

    @Test
    fun `non currency items render as plain values`() {
        val percentItem = OptionsLists.TIP_PERCENT_OPTIONS.first { it.value == 15 }
        assertEquals("15", percentItem.toString())
    }
}
