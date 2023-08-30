package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class TipCalcScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val buttonOne = hasText("1") and hasClickAction()
    private val buttonTwo = hasText("2") and hasClickAction()
    private val buttonThree = hasText("3") and hasClickAction()
    private val buttonFour = hasText("4") and hasClickAction()
    private val buttonFive = hasText("5") and hasClickAction()
    private val buttonSix = hasText("6") and hasClickAction()
    private val buttonSeven = hasText("7") and hasClickAction()
    private val buttonEight = hasText("8") and hasClickAction()
    private val buttonNine = hasText("9") and hasClickAction()
    private val buttonZero = hasText("0") and hasClickAction()
    private val buttonBackSpace = hasContentDescription("Backspace") and hasClickAction()
    private val buttonSubmit = hasContentDescription("Submit") and hasClickAction()
    private val textTipPercentage = hasContentDescription("Percentage") and hasClickAction()
    private val textKeyboardSubtotal = hasContentDescription("Keyboard Subtotal")
    private val textDisplaySubtotal = hasContentDescription("Display Subtotal")
    private val textDisplayTip = hasContentDescription("Display Tip")
    private val textDisplayGrandTotal = hasContentDescription("Display Grand Total")
    private val buttonTipSliderDecrease = hasContentDescription("Decrease")
    private val buttonTipSliderIncrease = hasContentDescription("Increase")
    private val buttonSettings = hasContentDescription("Settings") and hasClickAction()
    private val buttonSplit = hasText("Split") and hasClickAction()


    @Before
    fun setUp() {
        composeTestRule.setContent {
            TipCalcApp(rememberSwipeDismissableNavController())
        }
    }

    @After
    fun tearDown() {
        composeTestRule.onNode(textTipPercentage)
            .performTouchInput { longClick(center, 1000) }
    }

    @Test
    fun sixDigitsTyped_erased() {
        composeTestRule.onNode(buttonOne).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.01")).assertExists()
        composeTestRule.onNode(buttonTwo).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.12")).assertExists()
        composeTestRule.onNode(buttonThree).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("1.23")).assertExists()
        composeTestRule.onNode(buttonFour).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("12.34")).assertExists()
        composeTestRule.onNode(buttonFive).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("123.45")).assertExists()
        composeTestRule.onNode(buttonSix).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("1234.56")).assertExists()
        composeTestRule.onNode(buttonBackSpace).performTouchInput {
            longClick(center, 1000)
        }
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.00")).assertExists()
        composeTestRule.onNode(buttonFive).performClick()
        composeTestRule.onNode(buttonSix).performClick()
        composeTestRule.onNode(buttonSeven).performClick()
        composeTestRule.onNode(buttonEight).performClick()
        composeTestRule.onNode(buttonNine).performClick()
        composeTestRule.onNode(buttonZero).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("5678.90")).assertExists()
        repeat(3) { composeTestRule.onNode(buttonOne).performClick() }
        composeTestRule.onNode(textKeyboardSubtotal and hasText("5678.90")).assertExists()
        repeat(6) { composeTestRule.onNode(buttonBackSpace).performClick() }
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.00")).assertExists()
    }

    @Test
    fun zeroesTypedFirstDoNothing() {
        repeat(6) { composeTestRule.onNode(buttonZero).performClick() }
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.00")).assertExists()
    }


    @Test
    fun correctTotalsDisplayed() {
        composeTestRule.onNode(buttonThree).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.03")).assertExists()
        navigateToTipSelection()
        composeTestRule.onNode(textDisplaySubtotal and hasText("0.03")).assertExists()
        composeTestRule.onNode(textDisplayTip and hasText("0.00")).assertExists()
        composeTestRule.onNode(textDisplayGrandTotal and hasText("0.03")).assertExists()

        composeTestRule.onNode(textDisplayTip).performClick()
        composeTestRule.onNode(buttonFive).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.35")).assertExists()
        navigateToTipSelection()
        composeTestRule.onNode(textDisplaySubtotal and hasText("0.35")).assertExists()
        composeTestRule.onNode(textDisplayTip and hasText("0.05")).assertExists()
        composeTestRule.onNode(textDisplayGrandTotal and hasText("0.40")).assertExists()

        composeTestRule.onNode(textDisplayTip).performClick()
        composeTestRule.onNode(buttonSeven).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("3.57")).assertExists()
        navigateToTipSelection()
        composeTestRule.onNode(textDisplaySubtotal and hasText("3.57")).assertExists()
        composeTestRule.onNode(textDisplayTip and hasText("0.54")).assertExists()
        composeTestRule.onNode(textDisplayGrandTotal and hasText("4.11")).assertExists()

        composeTestRule.onNode(textDisplayTip).performClick()
        composeTestRule.onNode(buttonNine).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("35.79")).assertExists()
        navigateToTipSelection()
        composeTestRule.onNode(textDisplaySubtotal and hasText("35.79")).assertExists()
        composeTestRule.onNode(textDisplayTip and hasText("5.37")).assertExists()
        composeTestRule.onNode(textDisplayGrandTotal and hasText("41.16")).assertExists()
    }


    @Test
    fun correctRoundingTotalsDisplayed() {
        composeTestRule.onNode(buttonTwo).performClick()
        composeTestRule.onNode(buttonFour).performClick()
        composeTestRule.onNode(buttonSix).performClick()
        composeTestRule.onNode(buttonEight).performClick()
        composeTestRule.onNode(buttonZero).performClick()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("246.80")).assertExists()
        navigateToTipSelection()
        composeTestRule.onNode(textDisplaySubtotal and hasText("246.80")).assertExists()
        composeTestRule.onNode(textDisplayTip and hasText("37.02")).assertExists()
        composeTestRule.onNode(textDisplayGrandTotal and hasText("283.82")).assertExists()
        composeTestRule.onNode(buttonTipSliderDecrease)
            .performTouchInput { longClick(center, 1000) }
        composeTestRule.onNode(textDisplayGrandTotal and hasText("283.50")).assertExists()
        composeTestRule.onNode(buttonTipSliderDecrease)
            .performTouchInput { longClick(center, 1000) }
        composeTestRule.onNode(textDisplayGrandTotal and hasText("283.00")).assertExists()
        composeTestRule.onNode(buttonTipSliderIncrease)
            .performTouchInput { longClick(center, 1000) }
        composeTestRule.onNode(textDisplayGrandTotal and hasText("283.50")).assertExists()
        composeTestRule.onNode(buttonTipSliderIncrease).performClick()
        composeTestRule.onNode(textDisplayGrandTotal and hasText("283.82")).assertExists()
        composeTestRule.onNode(buttonTipSliderIncrease)
            .performTouchInput { longClick(center, 1000) }
        composeTestRule.onNode(textDisplayGrandTotal and hasText("284.00")).assertExists()
        composeTestRule.onNode(buttonTipSliderIncrease)
            .performTouchInput { longClick(center, 1000) }
        composeTestRule.onNode(textDisplayGrandTotal and hasText("284.50")).assertExists()
    }

    @Test
    fun tipSliderGreaterOrEqualZero() {
        navigateToTipSelection()
        repeat(101) { composeTestRule.onNode(buttonTipSliderDecrease).performClick() }
        composeTestRule.onNode(textDisplayTip and hasText("0.00")).assertExists()
        composeTestRule.onNode(textTipPercentage and hasText("0")).assertExists()
    }

    @Test
    fun tipSliderLessThanEqualOneHundred() {
        composeTestRule.onNode(buttonSubmit).performClick()
        repeat(101) { composeTestRule.onNode(buttonTipSliderIncrease).performClick() }
        composeTestRule.onNode(textTipPercentage and hasText("100")).assertExists()
    }

    @Test
    fun canResetTipPercentage() {
        navigateToTipSelection()
        repeat(5) { composeTestRule.onNode(buttonTipSliderIncrease).performClick() }
        composeTestRule.onNode(textTipPercentage and hasText("20")).assertExists()
        composeTestRule.onNode(textTipPercentage)
            .performTouchInput { longClick(center, 1000) }
        composeTestRule.onNode(textTipPercentage and hasText("15")).assertExists()
    }

    private fun navigateToTipSelection() {
        composeTestRule.onNode(buttonSubmit).performClick()
    }

}