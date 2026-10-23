package com.camerongineer.tipcalculatorwear

import android.content.res.Configuration
import android.content.Context
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.camerongineer.tipcalculatorwear.MainActivity
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

/**
 * End-to-end region & language flows through the real MainActivity:
 * - the keyboard shows the device region's currency symbol,
 * - picking a language (the same DataStore write the picker issues) relocates
 *   the rendered UI from the values-de/... string tables, live and across
 *   activity recreation,
 * - the currency symbol follows the region, never the language.
 *
 * One activity instance is shared by the class; route screens are left with
 * Espresso.pressBack() (a back KEY — safe against SwipeDismissableNavHost,
 * unlike a swipe). Language is reset to the app default ("") in fixtures.
 */
@RunWith(AndroidJUnit4::class)
class LanguageUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val dataStoreManager = DataStoreManager(context)

    private val buttonSettings = hasContentDescription("Settings") and hasClickAction()
    private val displayTip = hasContentDescription("Display Tip")

    /** Gets a string as the localizer resolves it for the given language code. */
    private fun localizedString(language: String, id: Int): String {
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale(language))
        return context.createConfigurationContext(config).resources.getString(id)
    }

    private fun mainScreenVisible() =
        composeTestRule.onAllNodes(hasContentDescription("Keyboard Subtotal"))
            .fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodes(hasContentDescription("Display Subtotal"))
                .fetchSemanticsNodes().isNotEmpty()

    /** Pops settings/split routes back to the main screen (key presses only). */
    private fun leaveRouteScreensIfAny() {
        repeat(3) {
            if (mainScreenVisible()) return
            Espresso.pressBack()
            composeTestRule.waitForIdle()
        }
    }

    private fun navigateToSettings() {
        leaveRouteScreensIfAny()
        composeTestRule.waitForIdle()
        composeTestRule.navigateToTipSelection()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(buttonSettings).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNode(buttonSettings).performClickResilient()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(
                hasText(localizedString("de", R.string.tip_settings)) or
                    hasText(localizedString("en", R.string.tip_settings))
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun setLanguage(code: String) = runBlocking {
        dataStoreManager.saveLanguage(code)
    }

    @Before
    fun setUp() {
        setLanguage("")
    }

    @After
    fun tearDown() {
        runCatching {
            leaveRouteScreensIfAny()
        }
        runBlocking { dataStoreManager.saveLanguage("") }
    }

    @Test
    fun keyboardShowsTheDeviceRegionCurrencySymbol() {
        // CI/local AVDs default to en-US; the keyboard subtotal row shows the
        // region's symbol beside the entered amount (e.g. "SUBTOTAL: $ 0.00").
        val keyboardSubtotal = hasContentDescription("Keyboard Subtotal")
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(keyboardSubtotal).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNode(keyboardSubtotal and hasText("$", substring = true))
            .assertExists()

        // No other currency symbol leaks onto the keyboard row.
        listOf("€", "£", "₺", "¥", "₹", "₽").forEach { symbol ->
            composeTestRule.onNode(
                keyboardSubtotal and hasText(symbol, substring = true)
            ).assertDoesNotExist()
        }
    }

    @Test
    fun changingLanguageToGermanLocalizesTheUiInPlace() {
        navigateToSettings()
        val englishTitle = localizedString("en", R.string.tip_settings)
        val germanTitle = localizedString("de", R.string.tip_settings)
        composeTestRule.onNode(hasText(englishTitle)).assertExists()

        // The same DataStore write the language picker issues: the running
        // UI must relocate without any recreation or relaunch.
        setLanguage("DE")
        composeTestRule.onNode(hasText(germanTitle)).assertExists()
        composeTestRule.onNode(hasText(englishTitle)).assertDoesNotExist()
    }

    @Test
    fun languageChangePersistsAcrossActivityRecreate() {
        setLanguage("DE")
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        navigateToSettings()
        composeTestRule.onNode(hasText(localizedString("de", R.string.tip_settings)))
            .assertExists()
        composeTestRule.onNode(hasText(localizedString("en", R.string.tip_settings)))
            .assertDoesNotExist()
    }

    @Test
    fun currencySymbolIsUnaffectedByLanguageChange() {
        setLanguage("DE")
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        // Return to the keyboard: the currency symbol follows the device
        // region (USD on CI devices), never the display language.
        val keyboardSubtotal = hasContentDescription("Keyboard Subtotal")
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(keyboardSubtotal).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNode(keyboardSubtotal and hasText("$", substring = true))
            .assertExists()
        listOf("€", "£", "₺", "¥", "₹", "₽").forEach { symbol ->
            composeTestRule.onNode(
                keyboardSubtotal and hasText(symbol, substring = true)
            ).assertDoesNotExist()
        }
    }
}
