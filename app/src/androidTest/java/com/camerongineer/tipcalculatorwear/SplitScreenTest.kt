package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
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
        // onSplitUp/onSplitDown persist numSplit; restore the default so later
        // tests (which boot a VM that trusts the stored value) start clean.
        runCatching {
            runBlocking {
                DataStoreManager(ApplicationProvider.getApplicationContext())
                    .saveNumSplit(DataStoreManager.DEFAULT_NUM_SPLIT)
            }
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
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText("50.00"))
                .fetchSemanticsNodes().isNotEmpty()
        }
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
}
