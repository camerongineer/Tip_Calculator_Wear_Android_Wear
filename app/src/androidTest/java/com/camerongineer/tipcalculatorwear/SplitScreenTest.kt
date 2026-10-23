package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.SplitCalcScreen
import com.camerongineer.tipcalculatorwear.presentation.SplitViewModel
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test

/**
 * Flows of the split screen, hosted directly (SplitViewModel + DataStore, no
 * nav dependency). The Split button's own semantics live at the bottom of the
 * tip-selection item in the shared scaling lazy column and are not reachable
 * from the keyboard-centered state by clicks, so the screen is composed with
 * a real VM instead — same inputs the navigation produced
 * (SplitViewModel(dataStore, subTotal, tipAmount)).
 */
class SplitScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @After
    fun tearDown() {
        // Split buttons and precision mode persist settings; restore defaults so
        // later tests (a booted VM trusts the stored values) start clean.
        runCatching {
            runBlocking {
                val dataStore = DataStoreManager(ApplicationProvider.getApplicationContext())
                dataStore.saveNumSplit(DataStoreManager.DEFAULT_NUM_SPLIT)
                dataStore.savePreciseSplit(isPreciseSplit = true)
            }
        }
    }

    private val buttonSplitUp = hasContentDescription("Increase") and hasClickAction()
    private val buttonSplitDown = hasContentDescription("Decrease") and hasClickAction()

    /** Waits until the given exact per-person amount string is rendered. */
    private fun waitForPerPerson(expected: String) {
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText(expected), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun hostSplit(subTotal: Int, tipAmount: Int) {
        composeTestRule.setContent {
            TipCalculatorWearTheme {
                SplitCalcScreen(
                    splitViewModel = SplitViewModel(
                        dataStore = DataStoreManager(ApplicationProvider.getApplicationContext()),
                        subTotal = subTotal,
                        tipAmount = tipAmount,
                    ),
                    onBackButtonPressed = {},
                )
            }
        }
    }

    /**
     * Splitting 100.00 by the default 2 divides exactly (no warning); bumping
     * to 3 leaves a subtotal remainder, which shows the uneven split warning
     * with the 0.01 remainder and per-person amount 33.33 (precision mode on).
     */
    @Test
    fun unevenSplitByThreeShowsTheRemainderWarning() {
        hostSplit(subTotal = 10000, tipAmount = 1500)

        // Wait for the VM's asynchronous DataStore read to settle (numSplit 2,
        // precision mode on), which renders per-person 50.00 / 5.00 / 55.00.
        waitForPerPerson("50.00")
        // The warning is a single sentence ("Uneven Split, additional …"),
        // so match by substring.
        composeTestRule.onNode(hasText("Uneven Split", substring = true), useUnmergedTree = true)
            .assertDoesNotExist()

        composeTestRule.onNode(hasContentDescription("Increase") and hasClickAction())
            .performClick() // 2 -> 3

        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText("Uneven Split", substring = true), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNode(hasText("33.33"), useUnmergedTree = true).assertExists()
        composeTestRule.onNode(hasText("0.01"), useUnmergedTree = true).assertExists()
    }

    /**
     * The split picker steps 2 -> 3 -> 4 with Increase and back 4 -> 3 -> 2
     * with Decrease, following the per-person math, and Decrease on the already
     * minimal count of 2 is a no-op.
     */
    @Test
    fun splitCountStepsUpAndDown() {
        hostSplit(subTotal = 10000, tipAmount = 1500)
        waitForPerPerson("50.00")

        composeTestRule.onNode(buttonSplitUp).performClickResilient() // 2 -> 3
        waitForPerPerson("33.33")
        composeTestRule.onNode(buttonSplitUp).performClickResilient() // 3 -> 4
        waitForPerPerson("25.00")
        composeTestRule.onNode(buttonSplitDown).performClickResilient() // 4 -> 3
        waitForPerPerson("33.33")
        composeTestRule.onNode(buttonSplitDown).performClickResilient() // 3 -> 2
        waitForPerPerson("50.00")

        // Already at the minimum of 2: no further change.
        composeTestRule.onNode(buttonSplitDown).performClickResilient()
        composeTestRule.onNode(hasText("50.00"), useUnmergedTree = true).assertExists()
        composeTestRule.onNode(hasText("2"), useUnmergedTree = true).assertExists()
    }

    /**
     * Increase clamps the split count at the settings limit of 30 payees;
     * extra clicks past the clamp leave the count and per-person math
     * unchanged (100.00 / 30 = 3.33 per person, +5.00/30 = 0.50 tip).
     */
    @Test
    fun splitCountClampsAtThirty() {
        hostSplit(subTotal = 10000, tipAmount = 1500)
        waitForPerPerson("50.00")

        repeat(28) { composeTestRule.onNode(buttonSplitUp).performClickResilient() } // 2 -> 30
        waitForPerPerson("3.33")
        repeat(3) { composeTestRule.onNode(buttonSplitUp).performClickResilient() }
        composeTestRule.onNode(hasText("30"), useUnmergedTree = true).assertExists()
        composeTestRule.onNode(hasText("3.33"), useUnmergedTree = true).assertExists()
    }

    /**
     * Long-clicking the split count resets it to the stored default (2),
     * restoring the per-person math and count display.
     */
    @Test
    fun longClickResetsSplitToDefaultTwo() {
        hostSplit(subTotal = 10000, tipAmount = 1500)
        waitForPerPerson("50.00")

        repeat(3) { composeTestRule.onNode(buttonSplitUp).performClickResilient() } // 2 -> 5
        waitForPerPerson("20.00")

        composeTestRule.onNode(hasText("5") and hasClickAction())
            .performTouchInput { longClick(center, 1000) }
        waitForPerPerson("50.00")
        composeTestRule.onNode(hasText("2"), useUnmergedTree = true).assertExists()
    }

    /**
     * With precision mode off the remainder is absorbed instead of shown:
     * 100.00 / 3 rounds each share up to 33.34 (+1 cent), the tip still splits
     * evenly, and no uneven-split warning appears.
     */
    @Test
    fun approximateSplitAbsorbsTheRemainderAndHidesTheWarning() {
        runBlocking {
            DataStoreManager(ApplicationProvider.getApplicationContext())
                .savePreciseSplit(isPreciseSplit = false)
        }
        hostSplit(subTotal = 10000, tipAmount = 1500)

        composeTestRule.onNode(buttonSplitUp).performClickResilient() // 2 -> 3
        waitForPerPerson("33.34")
        composeTestRule.onNode(hasText("38.34"), useUnmergedTree = true).assertExists()
        composeTestRule.onNode(hasText("Uneven Split", substring = true), useUnmergedTree = true)
            .assertDoesNotExist()
    }
}
