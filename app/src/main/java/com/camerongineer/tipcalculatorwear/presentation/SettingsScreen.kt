package com.camerongineer.tipcalculatorwear.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.SwitchDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme

@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val listState = rememberScalingLazyListState()
    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW)


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
            modifier = Modifier.fillMaxWidth()
        ) {
            //Tip Settings
            item {
                Text(
                    text = "Tip Settings",
                    modifier = Modifier.padding(6.dp))
            }

            item {
                DefaultTipPercentageItem(
                    labelText = "Default Tip",
                    defaultTipPercentage = settingsViewModel.defaultTipPercentage,
                    onDefaultTipPercentageChanged = { navController.navigate("default_tip_picker") }
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
                    modifier = Modifier.padding(bottom = 6.dp, top = 12.dp))
            }

            item {
                DefaultSplitItem(
                    labelText = "Default Split",
                    defaultSplitValue = settingsViewModel.defaultNumSplit,
                    onDefaultSplitValueChanged = { navController.navigate("default_split_picker") }
                )
            }

            item {
                RetainNumSplitItem(
                    isRememberNumSplit = settingsViewModel.rememberNumSplit.value,
                    onRememberNumSplitChanged = settingsViewModel::setRememberNumSplit
                )
            }

            item {
                CompactChip(
                    label = {Text(text = "Back")},
                    onClick = withHaptics { navController.navigateUp() },
                    modifier = Modifier.padding(bottom = 30.dp)
                )
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        intent.data =
                            Uri.parse("https://play.google.com/store/apps/details?id=com.camerongineer.tipcalculatorwear")
                        context.startActivity(intent)
                    }
                ) {
                    Text(
                        text = "If you enjoy this app, please consider leaving a review in the Play Store!",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,

                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .background(Color.Black)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.play_store_logo),
                            contentDescription = "Play Store Logo",
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun DefaultTipPercentageItem(
    labelText: String,
    defaultTipPercentage: MutableIntState,
    onDefaultTipPercentageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsChip(
        labelText = labelText,
        defaultValue = defaultTipPercentage,
        onDefaultValueChanged = onDefaultTipPercentageChanged,
        modifier = modifier) {
        Text(
            text = "${defaultTipPercentage.intValue}",
            fontSize = 17.sp,
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Right,
            modifier = modifier
                .wrapContentWidth()
        )
        Text(
            text = "%",
            fontSize = 11.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .padding(2.dp)
        )
    }
}

@Composable
fun DefaultSplitItem(labelText: String,
                     defaultSplitValue: MutableIntState,
                     onDefaultSplitValueChanged: (Int) -> Unit,
                     modifier: Modifier = Modifier,
) {
    SettingsChip(
        labelText = labelText,
        defaultValue = defaultSplitValue,
        onDefaultValueChanged = onDefaultSplitValueChanged,
        modifier = modifier) {
        Text(
            text = "${defaultSplitValue.intValue}",
            fontSize = 17.sp,
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Right,
            modifier = modifier
                .wrapContentWidth()
        )
    }

}


@Composable
fun SettingsChip(
    labelText: String,
    defaultValue: MutableIntState,
    onDefaultValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Chip(
        onClick = withHaptics { onDefaultValueChanged(defaultValue.intValue) },
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colors.primary
        ),
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "$labelText:",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) { this@Chip.content() }
                }

            } },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(
                minHeight = ToggleChipDefaults.Height * .66f
            )
    )
}

@Composable
fun RetainTipPercentageItem(
    isRememberTipPercentage: Boolean,
    onRememberTipPercentageChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsToggleChip(
        labelText = "Save Last Tip %",
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
        labelText = "Save Last Split",
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
    ToggleChip(
        checked = checked,
        colors = ToggleChipDefaults.toggleChipColors(
            checkedStartBackgroundColor = MaterialTheme.colors.primary,
            checkedEndBackgroundColor = MaterialTheme.colors.primary
        ),
        onCheckedChange = onCheckedChanged,
        label = {
            Text(
                text = labelText,
                fontSize = 13.sp,
                color = if (checked) {
                    MaterialTheme.colors.background
                } else MaterialTheme.colors.primaryVariant,
                modifier = modifier
            ) },
        toggleControl = {
        Switch(
            checked = checked,
            colors = SwitchDefaults.colors(),
            modifier = Modifier.semantics {
                this.contentDescription = if (checked) "On" else "Off"
            }
        )
    },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(
                minHeight = ToggleChipDefaults.Height * .66f,
            )
    )
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
