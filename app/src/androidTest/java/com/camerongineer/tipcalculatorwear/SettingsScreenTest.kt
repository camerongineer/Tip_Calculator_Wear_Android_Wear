package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.SettingsScreen
import com.camerongineer.tipcalculatorwear.presentation.SettingsViewModel
import com.camerongineer.tipcalculatorwear.presentation.theme.Theme
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

/**
 * Flows of the settings screen, hosted directly (SettingsViewModel + DataStore,
 * no nav dependency). Navigation destinations are recording no-ops. The list is
 * a scrollable ScalingLazyColumn, so rows outside the viewport are reached with
 * performScrollToNode (no touch drags).
 */
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var defaultTipNavigations = AtomicInteger(0)
    private var roundingNavigations = AtomicInteger(0)
    private var defaultSplitNavigations = AtomicInteger(0)
    private var languageNavigations = AtomicInteger(0)
    private var backNavigations = AtomicInteger(0)

    private val dataStore = DataStoreManager(ApplicationProvider.getApplicationContext())

    // Labels vary by res qualifiers/values-w220dp and locale, so derive every
    // asserted string from the app's own resources instead of hardcoding.
    private val res = ApplicationProvider.getApplicationContext<android.content.Context>().resources

    private fun resString(id: Int) = res.getString(id)

    private fun toggleRow(labelText: String, on: Boolean): SemanticsMatcher =
        // ToggleChip merges its label Text and the inner Switch ("On"/"Off")
        // content descriptions into one node, like the tip display rows.
        hasText(labelText) and hasContentDescription(on.let { if (it) "On" else "Off" })

    private fun scrollToRow(matcher: SemanticsMatcher) {
        composeTestRule.onNode(hasScrollAction()).performScrollToNode(matcher)
    }

    private fun clickRow(labelText: String) {
        val target = hasText(labelText) and hasClickAction()
        scrollToRow(target)
        composeTestRule.onNode(target).performClickResilient()
        composeTestRule.waitForIdle()
    }

    private fun hostSettings() {
        composeTestRule.setContent {
            TipCalculatorWearTheme {
                SettingsScreen(
                    settingsViewModel = SettingsViewModel(dataStore),
                    navigateToDefaultTipScreen = { defaultTipNavigations.incrementAndGet() },
                    navigateToRoundingNumScreen = { roundingNavigations.incrementAndGet() },
                    navigateToDefaultSplitScreen = { defaultSplitNavigations.incrementAndGet() },
                    navigateToLanguageSelectionScreen = { languageNavigations.incrementAndGet() },
                    onBackButtonPressed = { backNavigations.incrementAndGet() },
                )
            }
        }
        // The initial default values render asynchronously from DataStore.
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodes(hasText("15"))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Before
    fun setUp() {
        // Deterministic starting state independent of prior tests in the same
        // instrumentation process (settings persist across tests).
        runBlocking {
            dataStore.saveRememberTipPercentage(true)
            dataStore.saveRememberNumSplit(false)
            dataStore.savePreciseSplit(true)
            dataStore.saveLargeText(false)
        }
    }

    @After
    fun tearDown() {
        runCatching {
            runBlocking { dataStore.saveTheme(Theme.Dark.name) }
        }
    }

    /** Scroll the lazy column far enough that the row is actually composed. */
    private fun assertRow(label: String) {
        scrollToRow(hasText(label))
        composeTestRule.onNode(hasText(label)).assertExists()
    }

    @Test
    fun settingsSectionsAndDefaultValuesDisplayed() {
        hostSettings()
        composeTestRule.onNode(hasText(resString(R.string.tip_settings))).assertExists()
        assertRow(resString(R.string.default_tip))
        composeTestRule.onNode(hasText("15")).assertExists()
        composeTestRule.onNode(hasText("%")).assertExists() // trailing % text
        assertRow(resString(R.string.round_increment))
        composeTestRule.onNode(hasText("0.50")).assertExists() // $0.50 default step
        composeTestRule.onNode(hasText("$")).assertExists() // currency prefix
        assertRow(resString(R.string.default_split))
        composeTestRule.onNode(hasText("2")).assertExists()
        assertRow(resString(R.string.retain_num_split))
        assertRow(resString(R.string.back))
    }

    @Test
    fun togglesFlipAndPersist() {
        hostSettings()

        fun flipAndAssertPersisted(label: String, readFlow: suspend (DataStoreManager) -> Boolean) {
            val before = runBlocking { readFlow(dataStore) }
            clickRow(label)
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                runBlocking { readFlow(dataStore) } == !before
            }
        }

        flipAndAssertPersisted(resString(R.string.retain_tip_percentage)) { it.rememberTipPercentageFlow.first() }
        flipAndAssertPersisted(resString(R.string.precise_split_mode)) { it.preciseSplitFlow.first() }
        flipAndAssertPersisted(resString(R.string.retain_num_split)) { it.rememberNumSplitFlow.first() }
        flipAndAssertPersisted(resString(R.string.large_text)) { it.largeTextFlow.first() }
    }

    @Test
    fun togglesFlipOnScreen() {
        val retainTipLabel = resString(R.string.retain_tip_percentage)
        hostSettings()
        scrollToRow(toggleRow(retainTipLabel, on = true))
        composeTestRule.onNode(toggleRow(retainTipLabel, on = true)).assertExists()

        composeTestRule.onNode(hasText(retainTipLabel) and hasClickAction())
            .performClickResilient()
        composeTestRule.waitForIdle()
        composeTestRule.onNode(toggleRow(retainTipLabel, on = false)).assertExists()
    }

    @Test
    fun themeChipTogglesToLightTheme() {
        hostSettings()
        val darkChip = hasText(res.getString(R.string.enabled, resString(R.string.dark_theme)))
        val lightChip = hasText(res.getString(R.string.enabled, resString(R.string.light_theme)))
        scrollToRow(darkChip)
        composeTestRule.onNode(darkChip).assertExists()

        composeTestRule.onNode(darkChip and hasClickAction()).performClickResilient()
        composeTestRule.waitForIdle()
        composeTestRule.onNode(lightChip).assertExists()
    }

    @Test
    fun pickerAndBackCallbacksFire() {
        hostSettings()

        clickRow(resString(R.string.default_tip))
        clickRow(resString(R.string.round_increment))
        clickRow(resString(R.string.default_split))
        clickRow(res.getString(R.string.language, resString(R.string.en)))
        clickRow(resString(R.string.back))

        org.junit.Assert.assertEquals(1, defaultTipNavigations.get())
        org.junit.Assert.assertEquals(1, roundingNavigations.get())
        org.junit.Assert.assertEquals(1, defaultSplitNavigations.get())
        org.junit.Assert.assertEquals(1, languageNavigations.get())
        org.junit.Assert.assertEquals(1, backNavigations.get())
    }
}
