package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.wear.tooling.preview.devices.WearDevices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.ToggleChipDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.presentation.constants.OptionsItem
import com.camerongineer.tipcalculatorwear.presentation.constants.OptionsLists
import com.camerongineer.tipcalculatorwear.presentation.constants.TipLanguage
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import com.camerongineer.tipcalculatorwear.presentation.theme.Typography
import kotlinx.coroutines.launch

/**
 * Scrollable single-choice option list (one chip per option) that replaces the
 * former Wear Picker. A Picker never shows a scrollbar, which failed the Wear
 * App Quality "show the scrollbar" review; a ScalingLazyColumn with a
 * PositionIndicator satisfies it and keeps every option inside the round-bezel
 * safe area. Selection is click-driven; the check button (contentDescription
 * "Submit", same automation surface as before) hands the selected value on.
 */
@Composable
fun <T>PickerScreen(
    initialValue: T,
    optionsList: List<OptionsItem<T>>,
    isResourceString: Boolean = false,
    onSubmitPressed: (T) -> Unit = {}
) {
    val pickerViewModel = remember { PickerViewModel(initialValue, optionsList) }
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var selectedOption by remember { mutableIntStateOf(pickerViewModel.initialIndex) }
    // Initially-selected option centered in place of the old Picker's
    // mid-list carousel. (AutoCenteringParams alone does not scroll a deep
    // initial item into view — the state's initialCenterItemIndex does.)
    val listState = rememberScalingLazyListState(
        initialCenterItemIndex = pickerViewModel.initialIndex
    )
    val valuesContentDescription = stringResource(id = R.string.values)

    Scaffold(
        timeText = { if (!listState.isScrollInProgress) {
            TimeText(
                timeTextStyle = TimeTextDefaults
                    .timeTextStyle(
                        color = MaterialTheme.colors.onSurfaceVariant,
                        fontSize = Typography.title3.fontSize
                    )
            )
        } },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) },
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        ScalingLazyColumn(
            state = listState,
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            modifier = Modifier
                .fillMaxWidth()
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        listState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
                .semantics { contentDescription = valuesContentDescription }
        ) {
            items(pickerViewModel.optionsList.size) { index ->
                OptionRow(
                    rawText = pickerViewModel.optionsList[index].toString(),
                    isResourceString = isResourceString,
                    selected = index == selectedOption,
                    onClick = withHaptics { selectedOption = index }
                )
            }
        }

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = withHaptics {
                    onSubmitPressed(pickerViewModel.optionsList[selectedOption].value)
                },
                modifier = Modifier
                    .size(60.dp)
                    .padding(10.dp)
            ) {
                Image(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.submit),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.background),
                )
            }
        }

        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}

@Composable
private fun OptionRow(
    rawText: String,
    isResourceString: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    Chip(
        onClick = onClick,
        colors = if (selected) {
            ChipDefaults.chipColors(
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary
            )
        } else {
            ChipDefaults.chipColors(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurfaceVariant
            )
        },
        label = {
            // Same text resolution the Picker used: language options carry a
            // string resource id; currency options resolve via toString().
            Text(
                text = if (isResourceString) stringResource(rawText.toInt()) else rawText,
                style = Typography.button,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = ToggleChipDefaults.Height * .75f)
    )
}

@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Preview(device = WearDevices.RECT, showSystemUi = true)
@Composable
fun PickerPreview() {
    TipCalculatorWearTheme {
        PickerScreen(
            TipLanguage.EN, OptionsLists.LANGUAGE_OPTIONS, true
        )
    }
}
