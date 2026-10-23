package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performTouchInput
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Flows of the numpad screen: digit typing rules (progression, zeros, capping)
 * and backspace behaviors (delete, empty no-op, long-click clear).
 */
class NumPadScreenTest {

    companion object {
        private const val KEYBOARD_SUBTOTAL = "Keyboard Subtotal"
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    private val textKeyboardSubtotal = hasContentDescription(KEYBOARD_SUBTOTAL)

    @Before
    fun setUp() {
        composeTestRule.setContent {
            TipCalcApp(rememberSwipeDismissableNavController())
        }
    }

    @After
    fun tearDown() {
        composeTestRule.resetTipPercentageBestEffort()
    }

    @Test
    fun typingProgression_updatesErasesAndCapsTheAmount() {
        composeTestRule.typeDigits('1')
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.01")).assertExists()
        composeTestRule.typeDigits('2')
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.12")).assertExists()
        composeTestRule.typeDigits('3')
        composeTestRule.onNode(textKeyboardSubtotal and hasText("1.23")).assertExists()
        composeTestRule.typeDigits('4')
        composeTestRule.onNode(textKeyboardSubtotal and hasText("12.34")).assertExists()
        composeTestRule.typeDigits('5')
        composeTestRule.onNode(textKeyboardSubtotal and hasText("123.45")).assertExists()
        composeTestRule.typeDigits('6')
        composeTestRule.onNode(textKeyboardSubtotal and hasText("1234.56")).assertExists()
        // Long-press Backspace clears the whole amount.
        composeTestRule.onNode(hasContentDescription("Backspace") and hasClickAction())
            .performTouchInput { longClick(center, 1000) }
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.00")).assertExists()
        composeTestRule.typeDigits('5', '6', '7', '8', '9')
        composeTestRule.typeDigits('0')
        composeTestRule.onNode(textKeyboardSubtotal and hasText("5678.90")).assertExists()
        // More than six digits are capped.
        repeat(3) { composeTestRule.typeDigits('1') }
        composeTestRule.onNode(textKeyboardSubtotal and hasText("5678.90")).assertExists()
        // Backspace deletes digit by digit back down to zero.
        repeat(6) {
            composeTestRule.onNode(hasContentDescription("Backspace") and hasClickAction())
                .performClickResilient()
        }
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.00")).assertExists()
    }

    @Test
    fun typingOnlyZerosDoesNothing() {
        repeat(6) { composeTestRule.typeDigits('0') }
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.00")).assertExists()
    }

    /** A zero typed before any non-zero digit is ignored by the numpad rules. */
    @Test
    fun aLeadingZeroIsIgnored() {
        composeTestRule.typeDigits('0', '5')
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.05")).assertExists()
    }

    @Test
    fun backspaceOnAnEmptyAmountIsANoOp() {
        composeTestRule.onNode(hasContentDescription("Backspace") and hasClickAction())
            .performClickResilient()
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.00")).assertExists()
        composeTestRule.typeDigits('2')
        composeTestRule.onNode(textKeyboardSubtotal and hasText("0.02")).assertExists()
    }
}
