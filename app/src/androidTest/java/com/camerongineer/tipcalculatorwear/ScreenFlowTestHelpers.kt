package com.camerongineer.tipcalculatorwear

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput

/**
 * Shared plumbing for the screen-flow instrumented tests (numpad / tip
 * selection / split / settings). Matcher collections stay inside each test
 * class so they describe that class's flows; only the cross-class actions
 * live here.
 */

private val tipPercentageNode = hasContentDescription("Percentage") and hasClickAction()

private val submitNode = hasContentDescription("Submit") and hasClickAction()

/**
 * performClick with retry: on CI's software rendering (swiftshader) the
 * composition can transiently disappear mid-run ("No compose hierarchies
 * found"), especially during rapid click loops. Wait for it to return and
 * retry; surface the error if it never comes back.
 */
internal fun SemanticsNodeInteraction.performClickResilient(): SemanticsNodeInteraction {
    repeat(20) {
        runCatching { performClick() }
            .onSuccess { return it }
        Thread.sleep(500)
    }
    return performClick()
}

/** Scrolls (short Submit tap) from the numpad to the tip selection section. */
internal fun ComposeContentTestRule.navigateToTipSelection() {
    onNode(submitNode).performClick()
}

/**
 * Best-effort tip-percentage reset for @After teardown: under heavy CI flake
 * the composition can be transiently recreated, so don't fail if it's briefly
 * unavailable. Also neutralizes tip-percentage state saved by earlier tests.
 */
internal fun ComposeContentTestRule.resetTipPercentageBestEffort() {
    runCatching {
        waitUntil(timeoutMillis = 10_000) {
            onAllNodes(tipPercentageNode).fetchSemanticsNodes().isNotEmpty()
        }
        onNode(tipPercentageNode)
            .performTouchInput { longClick(center, 1000) }
    }
}

/** Types a digit sequence on the numpad. */
internal fun ComposeContentTestRule.typeDigits(vararg digits: Char) {
    digits.forEach { digit ->
        onNode(hasText(digit.toString()) and hasClickAction()).performClickResilient()
    }
}
