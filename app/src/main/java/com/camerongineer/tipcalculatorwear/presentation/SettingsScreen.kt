package com.camerongineer.tipcalculatorwear.presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import com.camerongineer.tipcalculatorwear.utils.getFontMultiplier
import com.camerongineer.tipcalculatorwear.utils.getFormattedAmountString
import com.camerongineer.tipcalculatorwear.utils.getTipLanguage
import com.camerongineer.tipcalculatorwear.utils.scaleFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navigateToDefaultTipScreen: () -> Unit,
    navigateToRoundingNumScreen: () -> Unit,
    navigateToDefaultSplitScreen: () -> Unit,
    navigateToLanguageSelectionScreen: () -> Unit,
    onBackButtonPressed: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberScalingLazyListState()
    val intent = Intent(Intent.ACTION_VIEW)
    val playStoreUrl = stringResource(id = R.string.play_store_url).toUri()

    val tipSettingsTextID by mutableStateOf(stringResource(R.string.tip_settings))
    val splitSettingsTextID by mutableStateOf(stringResource(R.string.split_settings))
    val miscSettingsTextID by mutableStateOf(stringResource(R.string.misc_settings))

    val isLargeFont by settingsViewModel.getLargeTextFlow().collectAsState(initial = false)
    val fontMultiplier = getFontMultiplier(screenHeight, isLargeFont)
    val focusRequester = remember { FocusRequester() }

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
        positionIndicator = { PositionIndicator(scalingLazyListState = listState)},
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.background)
    ) {
        ScalingLazyColumn(
            autoCentering = AutoCenteringParams(itemIndex = 0),
            modifier = Modifier
                .fillMaxWidth()
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        listState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable(),
            state = listState
        ) {

            //Tip Settings
            item {
                SectionTextItem(
                    textResourceID = tipSettingsTextID,
                    fontMultiplier = fontMultiplier
                )
                LaunchedEffect(Unit) { focusRequester.requestFocus() }
            }

            item {
                DefaultTipPercentageItem(
                    labelText = stringResource(id = R.string.default_tip),
                    fontMultiplier = fontMultiplier,
                    defaultTipPercentage = settingsViewModel.defaultTipPercentage,
                    onDefaultTipPercentageChanged = {
                        navigateToDefaultTipScreen()
                    }
                )
            }

            item {
                RetainTipPercentageItem(
                    isRememberTipPercentage = settingsViewModel.rememberTipPercentage.value,
                    onRememberTipPercentageChanged = settingsViewModel::setRememberTipPercentage,
                    fontMultiplier = fontMultiplier
                )
            }

            item {
                RoundIncrementItem(
                    labelText = stringResource(id = R.string.round_increment),
                    fontMultiplier = fontMultiplier,
                    roundingNumValue = settingsViewModel.roundingNum,
                    currencySymbol = settingsViewModel.currencySymbol,
                    onRoundingNumChanged = {
                        navigateToRoundingNumScreen()
                    }
                )
            }

            //Split Settings
            item {
                SectionTextItem(
                    textResourceID = splitSettingsTextID,
                    fontMultiplier = fontMultiplier
                )
            }

            item {
                DefaultSplitItem(
                    labelText = stringResource(id = R.string.default_split),
                    fontMultiplier = fontMultiplier,
                    defaultSplitValue = settingsViewModel.defaultNumSplit,
                    onDefaultSplitValueChanged = {
                        navigateToDefaultSplitScreen()
                    }
                )
            }

            item {
                RetainNumSplitItem(
                    isRememberNumSplit = settingsViewModel.rememberNumSplit.value,
                    onRememberNumSplitChanged = settingsViewModel::setRememberNumSplit,
                    fontMultiplier = fontMultiplier
                )
            }

            item {
                PreciseSplitModeItem(
                    isPreciseSplit = settingsViewModel.isPreciseSplit.value,
                    onPreciseSplitModeChanged = settingsViewModel::setIsPreciseSplit,
                    fontMultiplier = fontMultiplier
                )
            }

            //Misc Settings
            item {
                SectionTextItem(
                    textResourceID = miscSettingsTextID,
                    fontMultiplier = fontMultiplier
                )
            }

            item {
                LargeFontModeItem(
                    isLargeFont = isLargeFont,
                    onLargeFontModeChanged = { settingsViewModel.saveLargeText(!isLargeFont) },
                    fontMultiplier = fontMultiplier
                )
            }

            item {
                val languageCode by settingsViewModel.getLanguageFlow().collectAsState("")
                val currentTipLanguage = getTipLanguage(languageCode)
                LanguageSelectionItem(
                    tipLanguage = currentTipLanguage,
                    fontMultiplier = fontMultiplier,
                    onClick = navigateToLanguageSelectionScreen
                )
            }

            item {
                val themeName by settingsViewModel.getThemeFlow().collectAsState(Theme.Dark.name)
                val theme = Theme.valueOf(themeName)
                ThemeSelectionItem(
                    theme = theme,
                    fontMultiplier = fontMultiplier,
                    onThemeChanged = { settingsViewModel.saveTheme(if (theme == Theme.Dark) {
                        Theme.Light
                    } else Theme.Dark) }
                )
            }


            item {
                BackButtonItem(
                    text = stringResource(id = R.string.back),
                    fontMultiplier = fontMultiplier,
                    onBackButtonPressed = onBackButtonPressed
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
                        style = scaleFont(Typography.body2, fontMultiplier),
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
        DisposableEffect(Unit) {
            if (settingsViewModel.isFirstLaunched.value) {
                coroutineScope.launch {
                    listState.scrollToItem(0)
                    delay(50)
                    listState.animateScrollToItem(1)
                }
                settingsViewModel.setIsFirstLaunchedFalse()
            }
            onDispose { }
        }
    }
}

@Composable
private fun SectionTextItem(
    textResourceID: String,
    fontMultiplier: Float
) {
    Text(
        text = textResourceID,
        textAlign = TextAlign.Center,
        style = scaleFont(Typography.title1, fontMultiplier),
        color = MaterialTheme.colors.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, top = 12.dp)
    )
}

@Composable
fun DefaultTipPercentageItem(
    labelText: String,
    defaultTipPercentage: MutableIntState,
    fontMultiplier: Float,
    onDefaultTipPercentageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsChip(
        labelText = labelText,
        defaultValue = defaultTipPercentage,
        fontMultiplier = fontMultiplier,
        onDefaultValueChanged = onDefaultTipPercentageChanged,
        labelWeight = .8f,
        valueWeight = .3f,
        modifier = modifier) {
        Text(
            text = "${defaultTipPercentage.intValue}",
            style = scaleFont(Typography.display2, fontMultiplier),
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Right,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            modifier = modifier
                .wrapContentSize()
        )
        Text(
            text = "%",
            style = scaleFont(Typography.caption2, fontMultiplier),
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
                     fontMultiplier: Float,
                     onDefaultSplitValueChanged: (Int) -> Unit,
                     modifier: Modifier = Modifier
) {
    SettingsChip(
        labelText = labelText,
        defaultValue = defaultSplitValue,
        fontMultiplier = fontMultiplier,
        onDefaultValueChanged = onDefaultSplitValueChanged,
        modifier = modifier) {
        Text(
            text = "${defaultSplitValue.intValue}",
            style = scaleFont(Typography.display2, fontMultiplier),
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
    fontMultiplier: Float,
    onRoundingNumChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsChip(
        labelText = labelText,
        defaultValue = roundingNumValue,
        fontMultiplier = fontMultiplier,
        onDefaultValueChanged = onRoundingNumChanged,
        labelWeight = 1f,
        valueWeight = .5f,
        modifier = modifier.wrapContentWidth()) {
        val style = scaleFont(Typography.title2, fontMultiplier)
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
fun LanguageSelectionItem(
    tipLanguage: TipLanguage,
    fontMultiplier: Float,
    onClick: () -> Unit
) {
    Chip(
        onClick = withHaptics { onClick() },
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
                    text = stringResource(
                        R.string.language,
                        stringResource(tipLanguage.languageNameID)
                    ),
                    style = scaleFont(Typography.button, fontMultiplier),
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
    fontMultiplier: Float,
    modifier: Modifier = Modifier,
    labelWeight: Float = .9f,
    valueWeight: Float = .2f,
    content: @Composable RowScope.() -> Unit
) {
    Chip(
        onClick = withHaptics { onDefaultValueChanged(defaultValue.intValue) },
        colors = ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colors.primary
        ),
        label = {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(labelWeight)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = labelText,
                            style = scaleFont(Typography.button, fontMultiplier),
                            textAlign = TextAlign.Start,
                        )
                    }
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(valueWeight)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxSize()
                        ) { this@Chip.content() }
                    }
                },
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
    fontMultiplier: Float,
    modifier: Modifier = Modifier
) {
    SettingsToggleChip(
        labelText = stringResource(id = R.string.retain_tip_percentage),
        checked = isRememberTipPercentage,
        fontMultiplier = fontMultiplier,
        onCheckedChanged = onRememberTipPercentageChanged,
        modifier = modifier
    )
}

@Composable
fun RetainNumSplitItem(
    isRememberNumSplit: Boolean,
    onRememberNumSplitChanged: (Boolean) -> Unit,
    fontMultiplier: Float,
    modifier: Modifier = Modifier
) {
    SettingsToggleChip(
        labelText = stringResource(id = R.string.retain_num_split),
        checked = isRememberNumSplit,
        fontMultiplier = fontMultiplier,
        onCheckedChanged = onRememberNumSplitChanged,
        modifier = modifier
    )
}

@Composable
fun PreciseSplitModeItem(
    isPreciseSplit: Boolean,
    onPreciseSplitModeChanged: (Boolean) -> Unit,
    fontMultiplier: Float,
    modifier: Modifier = Modifier
) {
    SettingsToggleChip(
        labelText = stringResource(id = R.string.precise_split_mode),
        checked = isPreciseSplit,
        fontMultiplier = fontMultiplier,
        onCheckedChanged = onPreciseSplitModeChanged,
        modifier = modifier
    )
}

@Composable
fun LargeFontModeItem(
    isLargeFont: Boolean,
    onLargeFontModeChanged: (Boolean) -> Unit,
    fontMultiplier: Float,
    modifier: Modifier = Modifier
) {
    SettingsToggleChip(
        labelText = stringResource(id = R.string.large_text),
        checked = isLargeFont,
        fontMultiplier = fontMultiplier,
        onCheckedChanged = onLargeFontModeChanged,
        modifier = modifier
    )
}

@Composable
fun SettingsToggleChip(
    labelText: String,
    checked: Boolean,
    fontMultiplier: Float,
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
                style = scaleFont(Typography.button, fontMultiplier),
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
    fontMultiplier: Float,
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
                val formattedString = stringResource(
                    R.string.enabled,
                    stringResource(theme.descriptionID)
                )
                Text(
                    text = formattedString,
                    style = scaleFont(Typography.title2, fontMultiplier),
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

@Composable
private fun BackButtonItem(
    text: String, onBackButtonPressed: () -> Unit,
    fontMultiplier: Float
) {
    CompactChip(
        label = {
            Text(
                text = text,
                style = scaleFont(Typography.button, fontMultiplier)
            )
        },
        onClick = withHaptics {
            onBackButtonPressed()
        },
        modifier = Modifier.padding(top = 10.dp, bottom = 30.dp)
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
