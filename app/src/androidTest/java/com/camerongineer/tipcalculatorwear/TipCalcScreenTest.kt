package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.TipCalcApp
import com.camerongineer.tipcalculatorwear.presentation.TipCalcViewModel
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import junit.framework.TestCase
import org.junit.Rule


class TipCalcScreenTest: TestCase() {



    @get:Rule
    val composeTestRule = createComposeRule()



    fun test() {
        val dataStore = DataStoreManager(null)
        val tipCalcViewModel = TipCalcViewModel(dataStore)
        composeTestRule.setContent {
            TipCalculatorWearTheme {
                TipCalcApp(tipCalcViewModel = tipCalcViewModel)
            }
        }

        composeTestRule.onNodeWithText("1").performClick()

        composeTestRule.onNodeWithText("SUBTOTAL").assertIsDisplayed()
    }


    fun testSomethingTest() {
        assertTrue(true)
    }
}