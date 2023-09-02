package com.camerongineer.tipcalculatorwear.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.camerongineer.tipcalculatorwear.presentation.constants.TipLanguage
import com.camerongineer.tipcalculatorwear.presentation.theme.Theme
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import com.camerongineer.tipcalculatorwear.presentation.theme.Typography
import com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString
import com.camerongineer.tipcalculatorwear.utils.getTipLanguage

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navigateToDefaultTipScreen: () -> Unit,
    navigateToRoundingNumScreen: () -> Unit,
    navigateToDefaultSplitScreen: () -> Unit,
    navigateToLanguageSelectionScreen: () -> Unit,
    onBackButtonPressed: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberScalingLazyListState()
    val intent = Intent(Intent.ACTION_VIEW)
    val playStoreUrl = Uri.parse(stringResource(id = R.string.play_store_url))


    Scaffold(
        timeText = { if (!listState.isScrollInProgress) {
            TimeText(
                timeTextStyle = TimeTextDefaults
                    .timeTextStyle(color = MaterialTheme.colors.onSurfaceVariant))
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
                    text = stringResource(id = R.string.tip_settings),
                    textAlign = TextAlign.Center,
                    style = Typography.title1,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                )
            }

            item {
                DefaultTipPercentageItem(
                    labelText = stringResource(id = R.string.default_tip),
                    defaultTipPercentage = settingsViewModel.defaultTipPercentage,
                    onDefaultTipPercentageChanged = {
                        navigateToDefaultTipScreen()
                    }
                )
            }

            item {
                RetainTipPercentageItem(
                    isRememberTipPercentage = settingsViewModel.rememberTipPercentage.value,
                    onRememberTipPercentageChanged = settingsViewModel::setRememberTipPercentage
                )
            }

            item {
                RoundIncrementItem(
                    labelText = stringResource(id = R.string.round_increment),
                    roundingNumValue = settingsViewModel.roundingNum,
                    currencySymbol = settingsViewModel.currencySymbol,
                    onRoundingNumChanged = {
                        navigateToRoundingNumScreen()
                    }
                )
            }

            //Split Settings
            item {
                Text(
                    text = stringResource(id = R.string.split_settings),
                    textAlign = TextAlign.Center,
                    style = Typography.title1,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp, top = 12.dp)
                )
            }

            item {
                DefaultSplitItem(
                    labelText = stringResource(id = R.string.default_split),
                    defaultSplitValue = settingsViewModel.defaultNumSplit,
                    onDefaultSplitValueChanged = {
                        navigateToDefaultSplitScreen()
                    }
                )
            }

            item {
                RetainNumSplitItem(
                    isRememberNumSplit = settingsViewModel.rememberNumSplit.value,
                    onRememberNumSplitChanged = settingsViewModel::setRememberNumSplit
                )
            }

            item {
                PreciseSplitModeItem(
                    isPreciseSplit = settingsViewModel.isPreciseSplit.value,
                    onPreciseSplitModeChanged = settingsViewModel::setIsPreciseSplit
                )
            }

            //Misc Settings
            item {
                Text(
                    text = stringResource(id = R.string.misc_settings),
                    textAlign = TextAlign.Center,
                    style = Typography.title1,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp, top = 12.dp)
                )
            }

            item {
                val languageCode by settingsViewModel.getLanguageFlow().collectAsState("")
                val currentTipLanguage = getTipLanguage(languageCode)
                LanguageSelectionItem(
                    tipLanguage = currentTipLanguage,
                    onLanguageSelected = {
                        navigateToLanguageSelectionScreen()
                    }
                )
            }

            item {
                val themeName by settingsViewModel.getThemeFlow().collectAsState(Theme.Dark.name)
                val theme = Theme.valueOf(themeName)
                ThemeSelectionItem(
                    theme = theme,
                    onThemeChanged = { settingsViewModel.saveTheme(if (theme == Theme.Dark) {
                        Theme.Light
                    } else Theme.Dark) }
                )
            }


            item {
                CompactChip(
                    label = { Text(
                        text = stringResource(id = R.string.back),
                        style = Typography.button
                    ) },
                    onClick = withHaptics {
                        onBackButtonPressed()
                    },
                    modifier = Modifier.padding(top = 10.dp, bottom = 30.dp)
                )
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        try {
                            intent.data = playStoreUrl
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.review_request),
                        style = Typography.body2,
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
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
            style = Typography.display2,
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Right,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            modifier = modifier
                .wrapContentSize()
        )
        Text(
            text = "%",
            style = Typography.caption1,
            color = MaterialTheme.colors.onPrimary,
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
                     modifier: Modifier = Modifier
) {
    SettingsChip(
        labelText = labelText,
        defaultValue = defaultSplitValue,
        onDefaultValueChanged = onDefaultSplitValueChanged,
        modifier = modifier) {
        Text(
            text = "${defaultSplitValue.intValue}",
            style = Typography.display2,
            color = MaterialTheme.colors.secondary,
            modifier = modifier
                .wrapContentWidth()
        )
    }
}

@Composable
fun RoundIncrementItem(
    labelText: String,
    roundingNumValue: MutableIntState,
    currencySymbol: MutableState<String>,
    onRoundingNumChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsChip(
        labelText = labelText,
        defaultValue = roundingNumValue,
        onDefaultValueChanged = onRoundingNumChanged,
        modifier = modifier) {
        val style = Typography.title2
        Text(
            text = currencySymbol.value,
            style = style,
            color = MaterialTheme.colors.onPrimary,
            fontSize = style.fontSize * .66,
            textAlign = TextAlign.Right,
            modifier = modifier
                .wrapContentWidth()
                .padding(end = 1.dp)
        )
        Text(
            text = getFormattedAmountString(roundingNumValue.intValue),
            style = style,
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Left,
            modifier = modifier
                .wrapContentWidth()
        )
    }
}

@Composable
fun LanguageSelectionItem(tipLanguage: TipLanguage, onLanguageSelected: () -> Unit) {
    Chip(
        onClick = withHaptics { onLanguageSelected() },
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colors.primary
        ),
        label = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = String.format(
                        stringResource(R.string.language),
                        stringResource(tipLanguage.languageNameID)
                    ),
                    style = Typography.button,
                    textAlign = TextAlign.Center,
                )
            } },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(
                minHeight = ToggleChipDefaults.Height * .75f
            )
    )
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
            ) {
                Text(
                    text = labelText,
                    style = Typography.button,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxWidth().weight(.4f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                    ) { this@Chip.content() }
                }
            } },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(
                minHeight = ToggleChipDefaults.Height * .75f
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
        labelText = stringResource(id = R.string.retain_tip_percentage),
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
        labelText = stringResource(id = R.string.retain_num_split),
        checked = isRememberNumSplit,
        onCheckedChanged = onRememberNumSplitChanged,
        modifier = modifier
    )
}

@Composable
fun PreciseSplitModeItem(
    isPreciseSplit: Boolean,
    onPreciseSplitModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsToggleChip(
        labelText = stringResource(id = R.string.precise_split_mode),
        checked = isPreciseSplit,
        onCheckedChanged = onPreciseSplitModeChanged,
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
        onCheckedChange = withHapticsAndValue(block = onCheckedChanged),
        label = {
            Text(
                text = labelText,
                style = Typography.button,
                color = if (checked) {
                    MaterialTheme.colors.onPrimary
                } else MaterialTheme.colors.onSurfaceVariant,
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
                minHeight = ToggleChipDefaults.Height * .75f,
            )
    )
}

@Composable
fun ThemeSelectionItem(
    theme: Theme,
    onThemeChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    Chip(
        onClick = withHaptics { onThemeChanged() },
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colors.surface
        ),
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val formattedString = String.format(
                    stringResource(R.string.enabled),
                    stringResource(theme.descriptionID)
                )
                Text(
                    text = formattedString,
                    style = Typography.title2,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(
                minHeight = ToggleChipDefaults.Height * .75f
            )
    )
}




@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true, locale = "DE")
@Preview(device = Devices.WEAR_OS_RECT, showSystemUi = true)
@Composable
fun SettingsPreview() {
    TipCalculatorWearTheme {
        SettingsScreen(
            SettingsViewModel(DataStoreManager(LocalContext.current)),
            {},
            {},
            {},
            {},
            {}
        )
    }
}
