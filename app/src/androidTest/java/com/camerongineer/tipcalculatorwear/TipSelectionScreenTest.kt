package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.SemanticsMatcher
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

/**
 * Flows of the tip selection section: totals display, grand-total rounding via
 * the long-click arrows, and tip slider bounds (0%..100%) / percentage reset.
 * Split-screen flows live in SplitScreenTest; numpad in NumPadScreenTest.
 */
class TipSelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val textTipPercentage = hasContentDescription("Percentage") and hasClickAction()
    private val textDisplaySubtotal = hasContentDescription("Display Subtotal")
    private val textDisplayTip = hasContentDescription("Display Tip")
    private val textDisplayGrandTotal = hasContentDescription("Display Grand Total")
    private val buttonTipSliderDecrease = hasContentDescription("Decrease") and hasClickAction()
    private val buttonTipSliderIncrease = hasContentDescription("Increase") and hasClickAction()

    private fun textKeyboardSubtotal(expected: String): SemanticsMatcher =
        hasContentDescription("Keyboard Subtotal") and hasText(expected)

    /**
     * The subtotal/tip/grand-total display rows sit inside a clickable block,
     * so in the merged tree they fuse into ONE node that carries all three
     * content descriptions and all three amounts. Matching there would let
     * e.g. a grand-total assertion pass off the subtotal's text. Asserting on
     * the unmerged tree pins each row to its own single amount.
     */
    private fun onDisplayRow(matcher: SemanticsMatcher) =
        composeTestRule.onNode(matcher, useUnmergedTree = true)

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
    fun correctTotalsDisplayed() {
        composeTestRule.typeDigits('3')
        composeTestRule.onNode(textKeyboardSubtotal("0.03")).assertExists()
        composeTestRule.navigateToTipSelection()
        onDisplayRow(textDisplaySubtotal and hasText("0.03")).assertExists()
        onDisplayRow(textDisplayTip and hasText("0.00")).assertExists()
        onDisplayRow(textDisplayGrandTotal and hasText("0.03")).assertExists()

        composeTestRule.onNode(textDisplayTip).performClick()
        composeTestRule.typeDigits('5')
        composeTestRule.onNode(textKeyboardSubtotal("0.35")).assertExists()
        composeTestRule.navigateToTipSelection()
        onDisplayRow(textDisplaySubtotal and hasText("0.35")).assertExists()
        onDisplayRow(textDisplayTip and hasText("0.05")).assertExists()
        onDisplayRow(textDisplayGrandTotal and hasText("0.40")).assertExists()

        composeTestRule.onNode(textDisplayTip).performClick()
        composeTestRule.typeDigits('7')
        composeTestRule.onNode(textKeyboardSubtotal("3.57")).assertExists()
        composeTestRule.navigateToTipSelection()
        onDisplayRow(textDisplaySubtotal and hasText("3.57")).assertExists()
        onDisplayRow(textDisplayTip and hasText("0.54")).assertExists()
        onDisplayRow(textDisplayGrandTotal and hasText("4.11")).assertExists()

        composeTestRule.onNode(textDisplayTip).performClick()
        composeTestRule.typeDigits('9')
        composeTestRule.onNode(textKeyboardSubtotal("35.79")).assertExists()
        composeTestRule.navigateToTipSelection()
        onDisplayRow(textDisplaySubtotal and hasText("35.79")).assertExists()
        onDisplayRow(textDisplayTip and hasText("5.37")).assertExists()
        onDisplayRow(textDisplayGrandTotal and hasText("41.16")).assertExists()
    }

    /**
     * Long-click Decrease rounds the grand total down to the next rounding
     * step (default 0.50). From a divisible total it steps down; past the
     * subtotal the tip clamps at zero instead of going negative.
     */
    @Test
    fun roundDownLongClickNeverDropsTipBelowZero() {
        composeTestRule.typeDigits('4', '0', '0', '0')
        composeTestRule.navigateToTipSelection()
        onDisplayRow(textDisplayGrandTotal and hasText("46.00")).assertExists()
        // Every roundDown removes one 0.50 step; the exact-multiple rule only
        // moves where the first click lands. A 6.00 tip therefore reaches 0.00
        // after 12 clicks (6.00 / 0.50).
        repeat(12) {
            composeTestRule.onNode(buttonTipSliderDecrease)
                .performTouchInput { longClick(center, 1000) }
        }
        onDisplayRow(textDisplayGrandTotal and hasText("40.00")).assertExists()
        onDisplayRow(textDisplayTip and hasText("0.00")).assertExists()
        // Further roundDown clicks must keep tip at 0 (clamped, not negative).
        repeat(3) {
            composeTestRule.onNode(buttonTipSliderDecrease)
                .performTouchInput { longClick(center, 1000) }
        }
        onDisplayRow(textDisplayGrandTotal and hasText("40.00")).assertExists()
        onDisplayRow(textDisplayTip and hasText("0.00")).assertExists()
    }

    /**
     * The classic rounding walk: round down twice, up again, short-click +1%,
     * then further round-ups advance by the step.
     */
    @Test
    fun correctRoundingTotalsDisplayed() {
        composeTestRule.typeDigits('2', '4', '6', '8', '0')
        composeTestRule.onNode(textKeyboardSubtotal("246.80")).assertExists()
        composeTestRule.navigateToTipSelection()
        onDisplayRow(textDisplaySubtotal and hasText("246.80")).assertExists()
        onDisplayRow(textDisplayTip and hasText("37.02")).assertExists()
        onDisplayRow(textDisplayGrandTotal and hasText("283.82")).assertExists()
        composeTestRule.onNode(buttonTipSliderDecrease)
            .performTouchInput { longClick(center, 1000) }
        onDisplayRow(textDisplayGrandTotal and hasText("283.50")).assertExists()
        composeTestRule.onNode(buttonTipSliderDecrease)
            .performTouchInput { longClick(center, 1000) }
        onDisplayRow(textDisplayGrandTotal and hasText("283.00")).assertExists()
        composeTestRule.onNode(buttonTipSliderIncrease)
            .performTouchInput { longClick(center, 1000) }
        onDisplayRow(textDisplayGrandTotal and hasText("283.50")).assertExists()
        composeTestRule.onNode(buttonTipSliderIncrease).performClick ()
        onDisplayRow(textDisplayGrandTotal and hasText("283.82")).assertExists()
        composeTestRule.onNode(buttonTipSliderIncrease)
            .performTouchInput { longClick(center, 1000) }
        onDisplayRow(textDisplayGrandTotal and hasText("284.00")).assertExists()
        composeTestRule.onNode(buttonTipSliderIncrease)
            .performTouchInput { longClick(center, 1000) }
        onDisplayRow(textDisplayGrandTotal and hasText("284.50")).assertExists()
    }

    /**
     * 101 Increase clicks clamp the slider at the 100% maximum; with an empty
     * subtotal the clamped percentage still yields a zero tip.
     */
    @Test
    fun tipSliderLessThanEqualOneHundred() {
        composeTestRule.navigateToTipSelection()
        repeat(101) { composeTestRule.onNode(buttonTipSliderIncrease).performClickResilient() }
        composeTestRule.onNode(textTipPercentage and hasText("100")).assertExists()
        onDisplayRow(textDisplayTip and hasText("0.00")).assertExists()
    }

    /**
     * 101 Decrease clicks clamp the slider at the 0% minimum.
     */
    @Test
    fun tipSliderGreaterOrEqualZero() {
        composeTestRule.navigateToTipSelection()
        repeat(101) { composeTestRule.onNode(buttonTipSliderDecrease).performClickResilient() }
        onDisplayRow(textDisplayTip and hasText("0.00")).assertExists()
        composeTestRule.onNode(textTipPercentage and hasText("0")).assertExists()
    }

    @Test
    fun canResetTipPercentage() {
        composeTestRule.navigateToTipSelection()
        repeat(5) { composeTestRule.onNode(buttonTipSliderIncrease).performClick() }
        composeTestRule.onNode(textTipPercentage and hasText("20")).assertExists()
        composeTestRule.onNode(textTipPercentage)
            .performTouchInput { longClick(center, 1000) }
        composeTestRule.onNode(textTipPercentage and hasText("15")).assertExists()
    }
}
