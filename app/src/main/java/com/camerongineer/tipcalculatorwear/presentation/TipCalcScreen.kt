package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TipCalcScreen(
    navController: NavHostController,
    tipCalcViewModel: TipCalcViewModel
) {
    val listState = rememberScalingLazyListState()
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val scrollToSection: (Int) -> Unit = { index ->
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        coroutineScope.launch {
            listState.animateScrollToItem(index)
        }
    }

    DisposableEffect(Unit) {
        coroutineScope.launch { listState.scrollToItem(0) }
        onDispose { }
    }

    Scaffold(
        timeText = { if (!listState.isScrollInProgress) {
            TimeText(
            timeTextStyle = TimeTextDefaults.timeTextStyle(
                color = MaterialTheme.colors.onSecondary)
            )
        } },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.background)
    ) {
        ScalingLazyColumn(state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 0),
            modifier = Modifier.fillMaxWidth(),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {

            item {
                KeyboardItem(
                    tipCalcViewModel = tipCalcViewModel,
                    scrollToSection = scrollToSection
                )
            }

            item {
                TipSelectionItem(
                    tipCalcViewModel = tipCalcViewModel,
                    scrollToSection = scrollToSection,
                    onSplitClicked = { navController.navigate("split") }
                )
            }

        }
    }
}

@Composable
fun KeyboardItem(
    tipCalcViewModel: TipCalcViewModel,
    scrollToSection: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val buttonHeight = screenHeight * (if (configuration.isScreenRound) .15f else .17f)
    val scrollToTipSection = remember { { scrollToSection(1) } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 6.dp
            )
            .fillMaxSize()

    ) {
        Spacer(modifier = Modifier.height(if (configuration.isScreenRound) 8.dp else 0.dp))
        BillDigitsRow(
            digits = listOf('1', '2', '3'),
            onDigitClick = tipCalcViewModel::onDigitTyped,
            modifier = Modifier.height(buttonHeight)
        )

        BillDigitsRow(
            digits = listOf('4', '5', '6'),
            onDigitClick = tipCalcViewModel::onDigitTyped,
            modifier = Modifier.height(buttonHeight)
        )

        BillDigitsRow(
            digits = listOf('7', '8', '9'),
            onDigitClick = tipCalcViewModel::onDigitTyped,
            modifier = Modifier.height(buttonHeight)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(.9f)
        ) {
            BillKeyboardButton(
                icon = Icons.Default.KeyboardBackspace,
                contentDescription = "Keyboard Backspace",
                onClick = tipCalcViewModel::onDeleteTyped,
                onLongClick = { tipCalcViewModel.setSubTotalBlank() },
                modifier = Modifier
                    .weight(.33f)
                    .height(buttonHeight)
            )
            BillKeyboardDigit(
                digitChar = '0',
                onClick = { tipCalcViewModel.onDigitTyped('0') },
                modifier = Modifier
                    .weight(.33f)
                    .height(buttonHeight)
            )
            BillKeyboardButton(
                icon = Icons.Default.Done,
                contentDescription = "Keyboard Bottom Of Screen",
                onClick = scrollToTipSection,
                modifier = Modifier
                    .weight(.33f)
                    .height(buttonHeight)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        SubTotalDisplay(
            billAmountString = tipCalcViewModel.getFormattedSubTotal(),
            onClick = scrollToTipSection,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = CircleShape
                )
                .height(min(buttonHeight, 24.dp))
                .fillMaxWidth(.8f)
        )
    }
}




@Composable
fun BillDigitsRow(
    digits: List<Char>,
    onDigitClick: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(.9f)
    ) {
        digits.forEach { digit ->
            BillKeyboardDigit(
                digitChar = digit,
                onClick = { onDigitClick(digit) },
                modifier = modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
fun BillKeyboardDigit(
    digitChar: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BillKeyboardButton(
        modifier = modifier,
        text = digitChar.toString(),
        buttonColors = ButtonDefaults.primaryButtonColors(),
        onClick = onClick)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BillKeyboardButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    buttonColors: ButtonColors = ButtonDefaults.secondaryButtonColors(),
    onLongClick: () -> Unit = {},
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Button(
        onClick = onClick,
        colors = buttonColors,
        shape = RoundedCornerShape(30),
        modifier = modifier
            .height(30.dp)
            .padding(start = 1.dp, end = 1.dp, top = 1.dp, bottom = 1.dp)
    ) {
        val buttonModifier = Modifier.combinedClickable(
            onLongClick = {
                onLongClick()
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            onClick = {
                onClick()
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        )
        icon?.let {
            Image(
                imageVector = icon,
                contentDescription = contentDescription,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant),
                modifier = buttonModifier
            )
        }
        if (icon == null) {
            text?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = buttonModifier
                )
            }
        }
    }
}

@Composable
fun SubTotalDisplay(
    billAmountString: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.secondaryButtonColors(),
        modifier = modifier
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InputLabel(
                    labelText = stringResource(id = R.string.display_sub_total),
                    onClick = onClick
                )
                SmallText(
                    text = "$",
                    color = MaterialTheme.colors.primary,
                    fontSize = 9.sp,
                )
                Text(
                    text = "$billAmountString ",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primaryVariant,
                    modifier = Modifier
                        .wrapContentHeight()
                )
            }
        }

    }
}


@Composable
fun TipSelectionItem(
    tipCalcViewModel: TipCalcViewModel,
    scrollToSection: (Int) -> Unit,
    onSplitClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val tipSliderHeight = screenHeight * .16f
    val scrollToKeyboard = remember { { scrollToSection(0) } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .height(screenHeight)
    ) {
        Spacer(
            modifier = Modifier
                .weight(.08f)
                .clickable(onClick = scrollToKeyboard)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(.8f)
        ) {
            TipSlider(
                tipPercentage = tipCalcViewModel.getTipPercentage().roundToInt(),
                tipPercentageString = tipCalcViewModel.getFormattedTipPercentage(),
                equalitySymbol = tipCalcViewModel.getEqualitySymbol(),
                onTipSliderUpClicked = tipCalcViewModel::tipPercentageIncrement,
                onTipSliderDownClicked = tipCalcViewModel::tipPercentageDecrement,
                tipSliderHeight = tipSliderHeight,
                maxTipPercentage = TipCalcViewModel.MAX_TIP_PERCENT,
                roundUpClicked = tipCalcViewModel::onRoundUpClicked,
                roundDownClicked = tipCalcViewModel::onRoundDownClicked,
                modifier = modifier
                    .padding(top = 2.dp, bottom = 2.dp)
                    .fillMaxWidth(.9f)
            ) { scrollToSection(1) }

            GrandTotalDisplay(
                billAmountString = tipCalcViewModel.getFormattedSubTotal(),
                billAmountClicked = { scrollToSection(0) },
                tipAmountString = tipCalcViewModel.getFormattedTipAmount(),
                tipAmountClicked = { scrollToSection(1) },
                grandTotalString = tipCalcViewModel.getFormattedGrandTotal(),
                grandTotalClicked = { },
                modifier = modifier
                    .padding(top = screenHeight / 15)
            )
        }
        Row(
            modifier = Modifier
                .weight(.12f)
        ) {
            SmallText(
                text = "Split",
                color = Color.Yellow,
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 4.dp)
                    .clickable(onClick = onSplitClicked)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TipSlider(
    tipPercentage: Int,
    tipPercentageString: String,
    equalitySymbol: String,
    onTipSliderUpClicked: () -> Unit,
    onTipSliderDownClicked: () -> Unit,
    tipSliderHeight: Dp,
    maxTipPercentage: Int,
    roundUpClicked: () -> Unit,
    roundDownClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        val haptics = LocalHapticFeedback.current

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .wrapContentSize()
        ) {
            InputLabel(
                labelText = stringResource(
                    id = R.string.tip_percentage),
                modifier = Modifier.padding(bottom = 4.dp),
                onClick = onClick)
            SmallText(
                text = " $equalitySymbol",
                color = MaterialTheme.colors.error,
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 4.dp))
            Text(
                text = tipPercentageString,
                color = MaterialTheme.colors.error,
                fontSize = 16.sp,
                modifier = Modifier.wrapContentHeight()
            )
            SmallText(
                text = "%",
                color = MaterialTheme.colors.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom=2.dp))
        }
        InlineSlider(
            value = tipPercentage,
            onValueChange = { },
            valueProgression = IntProgression.fromClosedRange(0, maxTipPercentage, 1),
            decreaseIcon = {
                Image(
                    imageVector = Icons.Default.ArrowLeft,
                    contentDescription = "Tip Percentage Down",
                    Modifier.combinedClickable(
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            roundDownClicked()
                        },
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onTipSliderDownClicked()
                        }
                    )
                )
            },
            increaseIcon = {
                Image(
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = "Tip Percentage Up",
                    Modifier.combinedClickable(
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            roundUpClicked()
                        },
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onTipSliderUpClicked()
                        }
                    )
                )
            },
            colors = InlineSliderDefaults.colors(selectedBarColor = MaterialTheme.colors.primary),
            modifier = modifier
                .height(tipSliderHeight)
        )
        SmallText(
            text = stringResource(
                id = R.string.round_up_down
            ),
            fontSize = 7.sp,
            color = MaterialTheme.colors.secondaryVariant)

    }
}

@Composable
fun GrandTotalDisplay(
    billAmountString: String,
    tipAmountString: String,
    grandTotalString: String,
    modifier: Modifier = Modifier,
    billAmountClicked: () -> Unit = {},
    tipAmountClicked: () -> Unit = {},
    grandTotalClicked: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth(.9f)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
        ) {
            InputLabel(
                labelText = stringResource(id = R.string.display_sub_total),
                color = MaterialTheme.colors.onBackground,
                onClick = billAmountClicked,
                modifier = Modifier.height(16.dp))
            InputLabel(
                labelText = stringResource(id = R.string.display_tip),
                color = MaterialTheme.colors.onBackground,
                onClick = tipAmountClicked,
                modifier = Modifier.height(16.dp))
            InputLabel(
                labelText = stringResource(id = R.string.display_total),
                color = MaterialTheme.colors.onBackground,
                onClick = grandTotalClicked,
                modifier = Modifier.height(16.dp))
        }
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
        ) {
            AmountDisplay(
                amountString = billAmountString,
                modifier = Modifier.height(16.dp),
                onClick = billAmountClicked
            )
            AmountDisplay(
                amountString = tipAmountString,
                modifier = Modifier.height(16.dp),
                onClick = tipAmountClicked
            )
            AmountDisplay(
                amountString = grandTotalString,
                modifier = Modifier.height(16.dp),
                onClick = grandTotalClicked
            )
        }

    }
}


@Composable
fun LabeledAmountDisplay(
    label: String,
    amountString: String,
    modifier: Modifier = Modifier,
    smallText: Boolean = false,
    onClick: () -> Unit = { }
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .wrapContentWidth()
                .clickable(onClick = onClick)
        ) {
            Text(
                text = "$label: ",
                fontSize = if (smallText) 9.sp else 10.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier
            )
            AmountDisplay(
                amountString = amountString,
                smallText = smallText
            )
        }
    }
}

@Composable
fun AmountDisplay(
    amountString: String,
    modifier: Modifier = Modifier,
    smallText: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .wrapContentSize()
    ) {
        SmallText(
            text = "$",
            color = MaterialTheme.colors.primary,
            fontSize = if (smallText) 7.sp else 9.sp
        )
        Text(
            color = MaterialTheme.colors.primaryVariant,
            text = amountString,
            fontSize = if (smallText) 9.sp else 12.sp,
            modifier = modifier
                .wrapContentSize()
                .clickable(onClick = onClick)
        )
    }
}

@Composable
fun InputLabel(
    labelText: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    fontSize: TextUnit = 10.sp,
    onClick: () -> Unit = {},
) {
    Text(
        color = color,
        fontSize = fontSize,
        textAlign = TextAlign.Right,
        text = "$labelText: ",
        modifier = modifier
            .wrapContentSize()
            .clickable(onClick = onClick)
    )
}

@Composable
fun SmallText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 11.sp
) {
    Text(
        color = color,
        text = text,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}


@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_RECT, showSystemUi = true)
@Composable
fun TipSelectionPreview() {
    TipCalculatorWearTheme {
        TipSelectionItem(
            tipCalcViewModel = TipCalcViewModel(),
            scrollToSection = {},
            onSplitClicked = {},
            modifier = Modifier.background(color = MaterialTheme.colors.background)
        )
    }
}
