package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.camerongineer.tipcalculatorwear.presentation.PickerScreen
import com.camerongineer.tipcalculatorwear.presentation.constants.OptionsLists
import com.camerongineer.tipcalculatorwear.presentation.constants.TipLanguage
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import org.junit.Rule
import org.junit.Test

/**
 * Flows of the picker screen, hosted directly with the real option lists the
 * navigation uses. Selection changes are scroll/rotary driven (no swipe
 * gestures in this suite), so tests cover the initial selection and the Submit
 * hand-off of the currently selected value.
 */
class PickerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val buttonSubmit = hasContentDescription("Submit") and hasClickAction()

    private val res = ApplicationProvider.getApplicationContext<android.content.Context>().resources

    private fun resStringFor(id: Int) = res.getString(id)

    @Test
    fun tipPercentPickerShowsInitialSelection() {
        val selected = mutableListOf<Any>()
        composeTestRule.setContent {
            TipCalculatorWearTheme {
                PickerScreen(
                    initialValue = 15,
                    optionsList = OptionsLists.TIP_PERCENT_OPTIONS,
                    onSubmitPressed = { selected += it },
                )
            }
        }

        composeTestRule.onNode(hasText("15"), useUnmergedTree = true).assertExists()
        composeTestRule.onNode(hasText("14"), useUnmergedTree = true).assertExists() // adjacent option
        composeTestRule.onNode(buttonSubmit).performClickResilient()
        org.junit.Assert.assertEquals(listOf(15), selected)
    }

    @Test
    fun roundingStepPickerShowsTheFormattedInitialValue() {
        val selected = mutableListOf<Any>()
        composeTestRule.setContent {
            TipCalculatorWearTheme {
                PickerScreen(
                    initialValue = 50,
                    optionsList = OptionsLists.ROUNDING_INCREMENTS,
                    onSubmitPressed = { selected += it },
                )
            }
        }

        // Currency-formatted rendering of the 50-cent step.
        composeTestRule.onNode(hasText("0.50"), useUnmergedTree = true).assertExists()
        composeTestRule.onNode(buttonSubmit).performClickResilient()
        org.junit.Assert.assertEquals(listOf(50), selected)
    }

    @Test
    fun languagePickerShowsTheLocalizedInitialOption() {
        val selected = mutableListOf<Any>()
        composeTestRule.setContent {
            TipCalculatorWearTheme {
                PickerScreen(
                    initialValue = TipLanguage.EN,
                    optionsList = OptionsLists.LANGUAGE_OPTIONS,
                    isResourceString = true,
                    onSubmitPressed = { selected += it },
                )
            }
        }

        composeTestRule.onNode(hasText(resStringFor(R.string.en)), useUnmergedTree = true).assertExists()
        composeTestRule.onNode(buttonSubmit).performClickResilient()
        org.junit.Assert.assertEquals(listOf(TipLanguage.EN), selected)
    }
}
