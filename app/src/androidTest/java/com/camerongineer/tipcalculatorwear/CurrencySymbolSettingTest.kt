package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.constants.TipCurrency
import com.camerongineer.tipcalculatorwear.utils.resolveTipCurrency
import androidx.test.espresso.Espresso
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

/**
 * Settings menu -> currency picker -> persistence, through the real nav host.
 * The datastore override is cleared in setUp/tearDown so the region-default
 * tests (CurrencyRegionTest) and the shared-instrumentation-process tests are
 * unaffected by the symbol this class stores.
 */
@RunWith(AndroidJUnit4::class)
class CurrencySymbolSettingTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val buttonSettings = hasContentDescription("Settings") and hasClickAction()
    private val submitNode = hasContentDescription("Submit") and hasClickAction()

    private val dataStore = DataStoreManager(
        ApplicationProvider.getApplicationContext()
    )
    private val res = ApplicationProvider.getApplicationContext<android.content.Context>().resources

    private fun resString(id: Int, vararg args: Any) = res.getString(id, *args)

    private fun storedCode() = runBlocking { dataStore.currencyCodeFlow.first() }

    private fun chipSymbolFor(storedCode: String) =
        TipCurrency.symbolForStoredCode(storedCode, resolveTipCurrency(Locale.getDefault()))

    private fun currencyChip(symbol: String) =
        hasText(resString(R.string.currency, symbol)) and hasClickAction()

    /** After a recreate the nav host restores the last route; pop back to main. */
    private fun returnToMainRoute() {
        fun mainVisible() =
            composeTestRule.onAllNodes(hasContentDescription("Keyboard Subtotal"))
                .fetchSemanticsNodes().isNotEmpty() ||
                composeTestRule.onAllNodes(hasContentDescription("Display Subtotal"))
                    .fetchSemanticsNodes().isNotEmpty()
        repeat(4) {
            if (mainVisible()) return
            Espresso.pressBack()
            composeTestRule.waitForIdle()
        }
    }

    private fun waitForNode(matcher: SemanticsMatcher, timeoutMillis: Long = 10_000) {
        val deadline = System.currentTimeMillis() + timeoutMillis
        // waitUntil polls the deadline predicate until the deadline passes or
        // the node appears; either outcome exits the wait.
        composeTestRule.waitUntil(timeoutMillis = timeoutMillis + 2_000) {
            composeTestRule.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty() ||
                System.currentTimeMillis() > deadline
        }
        if (composeTestRule.onAllNodes(matcher).fetchSemanticsNodes().isEmpty()) {
            throw AssertionError("node $matcher not found within ${timeoutMillis}ms; ${uiSnapshot()}")
        }
    }

    /** Main screen -> tip section -> settings route. */
    private fun openSettings() {
        returnToMainRoute()
        // Submit scrolls the main column from the keyboard item to the tip
        // selection item, where the display rows and the Settings button live.
        waitForNode(submitNode)
        composeTestRule.onNode(submitNode).performClickResilient()
        composeTestRule.waitForIdle()
        waitForNode(buttonSettings)
        composeTestRule.onNode(buttonSettings).performClickResilient()
        composeTestRule.waitForIdle()
        waitForNode(hasText(res.getString(R.string.tip_settings)))
    }

    /** Scroll-to-node with retry: a lazy settings column only composes rows
     * on demand, and after a recreate the list state re-seeds asynchronously. */
    private fun scrollToRow(matcher: SemanticsMatcher) {
        repeat(10) {
            try {
                composeTestRule.onNode(hasScrollAction()).performScrollToNode(matcher)
                return
            } catch (e: Exception) {
                composeTestRule.waitForIdle()
                Thread.sleep(400)
            }
        }
        throw AssertionError("row $matcher not reachable; ${uiSnapshot()}")
    }

    /** Which landmark rows are currently on screen; attached to failures. */
    private fun uiSnapshot(): String {
        fun present(matcher: SemanticsMatcher) =
            composeTestRule.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
        val regionChip = currencyChip(chipSymbolFor(storedCode()))
        return "snapshot[submit=${present(submitNode)} settings=${present(buttonSettings)} " +
            "keyboard=${present(hasContentDescription("Keyboard Subtotal"))} " +
            "tipSection=${present(hasContentDescription("Display Subtotal"))} " +
            "tipSettings=${present(hasText(res.getString(R.string.tip_settings)))} " +
            "currencyChip=${present(regionChip)} euroChip=${present(currencyChip("€"))} " +
            "currencyDefaultRow=${present(hasText(resString(R.string.currency_default)))} " +
            "euroRow=${present(hasText("€ Euro"))}]"
    }

    private fun pickCurrency(displayText: String) {
        val chipSymbol = chipSymbolFor(storedCode())
        openSettings()
        scrollToRow(currencyChip(chipSymbol))
        composeTestRule.onNode(currencyChip(chipSymbol)).performClickResilient()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText(resString(R.string.currency_default)))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNode(hasText(resString(R.string.currency_default))).assertExists()
        composeTestRule.onNode(hasText("$ Dollar")).assertExists()

        composeTestRule.onNode(hasText(displayText)).performClickResilient()
        composeTestRule.onNode(submitNode).performClickResilient()
        composeTestRule.waitForIdle()
    }

    @After
    fun tearDown() {
        runBlocking { dataStore.saveCurrencySymbol("") }
    }

    @Test
    fun pickingACurrencyPersistsAcrossActivityRecreation() {
        runBlocking { dataStore.saveCurrencySymbol("") }

        pickCurrency("€ Euro")
        composeTestRule.waitUntil(timeoutMillis = 10_000) { storedCode() == "EUR" }

        // Relaunch-equivalent: the fresh DataStoreManager/ViewModels read back
        // the stored override, so the settings chip shows it again.
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()
        openSettings()
        scrollToRow(currencyChip("€"))
        composeTestRule.onNode(currencyChip("€")).assertExists()
    }

    /** The pick reaches a main route that is already alive, not just a restart. */
    @Test
    fun pickingACurrencyShowsOnTheMainScreenWithoutRecreation() {
        runBlocking { dataStore.saveCurrencySymbol("") }

        pickCurrency("€ Euro")
        composeTestRule.waitUntil(timeoutMillis = 10_000) { storedCode() == "EUR" }

        returnToMainRoute()
        composeTestRule.waitForIdle()
        waitForNode(hasContentDescription("Keyboard Subtotal"))
        waitForNode(hasText("€"))
        composeTestRule.onAllNodes(hasText("$"), useUnmergedTree = true)
            .assertCountEquals(0)
    }

    @Test
    fun pickingTheDefaultOptionClearsTheOverride() {
        runBlocking { dataStore.saveCurrencySymbol("EUR") }
        assertEquals("€", runBlocking { dataStore.currencySymbolFlow.first() })

        pickCurrency(resString(R.string.currency_default))
        composeTestRule.waitUntil(timeoutMillis = 10_000) { storedCode() == "" }

        // A fresh instance maps the cleared code back to the region default.
        val freshStore = DataStoreManager(
            ApplicationProvider.getApplicationContext<android.content.Context>()
        )
        assertEquals(
            resolveTipCurrency(Locale.getDefault()).symbol,
            runBlocking { freshStore.currencySymbolFlow.first() }
        )
    }
}
