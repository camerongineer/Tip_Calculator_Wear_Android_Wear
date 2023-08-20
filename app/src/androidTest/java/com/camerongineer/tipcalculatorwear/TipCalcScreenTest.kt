package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.TipCalcApp
import com.camerongineer.tipcalculatorwear.presentation.TipCalcViewModel
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import org.junit.Assert
import org.junit.Rule
import org.junit.Test


class TipCalcScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun test() {
        composeTestRule.setContent {
            val tipCalcViewModel =
                TipCalcViewModel(dataStore = DataStoreManager(LocalContext.current))

            TipCalculatorWearTheme {
                TipCalcApp(tipCalcViewModel = tipCalcViewModel)
            }

            composeTestRule.onNodeWithText("1").performClick()
            composeTestRule.onNodeWithText("SUBTOTAL").assertIsDisplayed()
        }

        Assert.assertTrue(true)
    }


    @Test
    fun testSomethingTest() {
        Assert.assertTrue(true)
    }
}