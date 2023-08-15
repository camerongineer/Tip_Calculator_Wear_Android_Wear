package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme

@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val listState = rememberScalingLazyListState()
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        timeText = { if (!listState.isScrollInProgress) {
            TimeText(
                timeTextStyle = TimeTextDefaults
                    .timeTextStyle(color = MaterialTheme.colors.onSecondary))
        } },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState)},
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.background)
    ) {
        ScalingLazyColumn(
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 0),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            //Tip Settings
            item {
                Text(
                    text = "Tip Settings",
                    modifier = Modifier.padding(6.dp))
            }

            item {
                DefaultTipPercentageItem(
                    defaultTipPercentage = 15,
                    onDefaultTipPercentageChanged = {}
                )
            }

            item {
                RetainTipPercentageItem(
                    isRememberTipPercentage = settingsViewModel.rememberTipPercentage.value,
                    onRememberTipPercentageChanged = settingsViewModel::setRememberTipPercentage
                )
            }

            //Split Settings
            item {
                Text(
                    text = "Split Settings",
                    modifier = Modifier.padding(6.dp))
            }
            item {
                RetainNumSplitItem(
                    isRememberNumSplit = settingsViewModel.rememberNumSplit.value,
                    onRememberNumSplitChanged = settingsViewModel::setRememberNumSplit
                )
            }
            item {
                Card(onClick = { navController.navigate("default_tip_picker") }) {
                    
                }
            }

            item {
                CompactChip(label = {Text(text = "Back")}, onClick = navController::navigateUp)
            }


        }
    }
}

@Composable
fun DefaultTipPercentageItem(
    defaultTipPercentage: Int,
    onDefaultTipPercentageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
//    Chip(onClick = { /*TODO*/ }, colors = ChipColors.primaryChipColors(), border = ) {
//
//    }
}

@Composable
fun RetainTipPercentageItem(
    isRememberTipPercentage: Boolean,
    onRememberTipPercentageChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsToggleChip(
        labelText = "Retain Last Tip % ",
        checked = isRememberTipPercentage,
        onCheckedChanged = onRememberTipPercentageChanged,
        modifier = modifier
    )
}

@Composable
fun RetainNumSplitItem(
    isRememberNumSplit: Boolean,
    onRememberNumSplitChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsToggleChip(
        labelText = "Retain Last Split ",
        checked = isRememberNumSplit,
        onCheckedChanged = onRememberNumSplitChanged,
        modifier = modifier
    )
}

@Composable
fun SettingsToggleChip(
    labelText: String,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    ToggleChip(
        checked = checked,
        onCheckedChange = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onCheckedChanged(it) } ,
        label = {
            Text(
                text = labelText,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = modifier
            )
                },
        toggleControl = {
        Switch(
            checked = checked,
            modifier = Modifier.semantics {
                this.contentDescription = if (checked) "On" else "Off"
            }
        )
    },)
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_RECT, showSystemUi = true)
@Composable
fun SettingsPreview() {
    TipCalculatorWearTheme {
        SettingsScreen(
            navController = NavHostController(LocalContext.current),
            settingsViewModel = SettingsViewModel(DataStoreManager(LocalContext.current))
        )
    }
}
