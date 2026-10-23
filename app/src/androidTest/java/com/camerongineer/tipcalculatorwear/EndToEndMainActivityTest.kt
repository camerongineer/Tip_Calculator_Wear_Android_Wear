package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.click
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.longClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso
import com.camerongineer.tipcalculatorwear.MainActivity
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end flows through the real MainActivity + SwipeDismissableNavHost.
 * One activity instance is shared by the class, so every test and teardown
 * returns to a clean keyboard first. Route screens (settings/split) are left
 * with Espresso.pressBack() (a back KEY — safe against SwipeDismissableNavHost,
 * unlike a swipe). Back is never pressed on the main route, where the same key
 * would dismiss the activity.
 */
@RunWith(AndroidJUnit4::class)
class EndToEndMainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val buttonBackspace = hasContentDescription("Backspace") and hasClickAction()
    private val buttonSubmit = hasContentDescription("Submit") and hasClickAction()
    private val buttonSettings = hasContentDescription("Settings") and hasClickAction()
    private val displayTip = hasContentDescription("Display Tip")
    private val displaySubtotal = hasContentDescription("Display Subtotal")

    private fun keyboardSubtotal(expected: String) =
        hasContentDescription("Keyboard Subtotal") and hasText(expected)

    private fun keyboardVisible() =
        composeTestRule.onAllNodes(hasContentDescription("Keyboard Subtotal"))
            .fetchSemanticsNodes().isNotEmpty()

    private fun tipSectionVisible() =
        composeTestRule.onAllNodes(displaySubtotal).fetchSemanticsNodes().isNotEmpty()

    /** Pops settings/split routes back to the main screen (key presses only). */
    private fun leaveRouteScreensIfAny() {
        repeat(3) {
            // Main route shows either the keyboard item or the tip selection item.
            if (keyboardVisible() || tipSectionVisible()) return
            Espresso.pressBack()
            composeTestRule.waitForIdle()
        }
    }

    /** From anywhere: end on the keyboard item with an empty subtotal. */
    private fun returnToCleanKeyboard() {
        leaveRouteScreensIfAny()
        if (!keyboardVisible() && tipSectionVisible()) {
            // The TIP display row's click scrolls back to the keyboard.
            composeTestRule.onNode(displayTip).performClickResilient()
            composeTestRule.waitForIdle()
        }
        if (keyboardVisible()) {
            // Long-press Backspace clears any leftovers; on an empty input it
            // is a no-op, so repeat pressing is harmless until 0.00 shows.
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                composeTestRule.onAllNodes(buttonBackspace).fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNode(buttonBackspace)
                .performTouchInput { longClick(center, 1000) }
            composeTestRule.waitForIdle()
            composeTestRule.onNode(keyboardSubtotal("0.00")).assertExists()
        }
    }

    @After
    fun tearDown() {
        runCatching {
            returnToCleanKeyboard()
            composeTestRule.navigateToTipSelection()
            composeTestRule.resetTipPercentageBestEffort()
            // restore the empty keyboard for the next test
            composeTestRule.onNode(displayTip).performClickResilient()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun keyboardToTipSelectionAndBackRoundTrip() {
        composeTestRule.typeDigits('1', '2', '3', '4')
        composeTestRule.navigateToTipSelection()
        // After the submit-scroll the keyboard item stays composed above the
        // fold, so scope to the tip section's own display rows.
        composeTestRule.onNode(
            displaySubtotal and hasText("12.34"), useUnmergedTree = true
        ).assertExists()
        composeTestRule.onNode(hasContentDescription("Display Grand Total"), useUnmergedTree = true)
            .assertExists()

        // The TIP row click scrolls back to the keyboard.
        composeTestRule.onNode(displayTip).performClickResilient()
        composeTestRule.waitForIdle()
        composeTestRule.onNode(keyboardSubtotal("12.34")).assertExists()
    }

    @Test
    fun splitButtonOpensSplitScreenAfterSubmit() {
        returnToCleanKeyboard()

        // Reaching the tip-selection item does not require a typed subtotal:
        // submitting the empty amount scrolls to the tip item, whose bottom
        // row exposes the Split and Settings buttons.
        composeTestRule.navigateToTipSelection()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText("Split")).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNode(hasText("Split")).assertExists()

        // An amount is only needed to verify the per-person math.
        composeTestRule.onNode(displayTip).performClickResilient()
        composeTestRule.waitForIdle()
        composeTestRule.typeDigits('1', '0', '0', '0', '0')
        composeTestRule.navigateToTipSelection()

        // Bottom-row Split button of the tip-selection item.
        // The visible "Split" Text overlays its own (bare) Button, so inject a
        // raw tap on the text; the underlying button receives the click.
        composeTestRule.onNode(hasText("Split")).performTouchInput { click(center) }
        composeTestRule.waitForIdle()

        // 100.00 + 15.00 tip split by 2 divides exactly: no uneven warning.
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText("50.00")).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNode(hasText("Uneven Split", substring = true), useUnmergedTree = true)
            .assertDoesNotExist()
        composeTestRule.onNode(hasText("50.00"), useUnmergedTree = true).assertExists()
    }

    private fun resStringForDisplay(id: Int) =
        androidx.test.core.app.ApplicationProvider
            .getApplicationContext<android.content.Context>()
            .resources
            .getString(id)

    @Test
    fun settingsButtonOpensSettingsScreen() {
        returnToCleanKeyboard()
        composeTestRule.navigateToTipSelection()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(buttonSettings).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNode(buttonSettings).performClickResilient()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText(resStringForDisplay(R.string.tip_settings)))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNode(hasText(resStringForDisplay(R.string.default_tip))).assertExists()
    }

    @Test
    fun tipPercentagePersistsAcrossActivityRecreate() {
        returnToCleanKeyboard()
        composeTestRule.navigateToTipSelection()
        val buttonTipIncrease = hasContentDescription("Increase") and hasClickAction()
        repeat(5) {
            composeTestRule.onNode(buttonTipIncrease)
                .performClickResilient()
            composeTestRule.waitForIdle()
        }
        composeTestRule.onNode(
            hasContentDescription("Percentage") and hasText("20")
        ).assertExists()

        // Destroy and recreate the activity (same as a relaunch); the saved
        // last-tip percentage must be restored by the fresh ViewModel.
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        composeTestRule.navigateToTipSelection()
        composeTestRule.onNode(
            hasContentDescription("Percentage") and hasText("20")
        ).assertExists()
    }
}
