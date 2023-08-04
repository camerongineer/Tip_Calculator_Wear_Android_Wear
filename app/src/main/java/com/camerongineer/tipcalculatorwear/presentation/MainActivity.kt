package com.camerongineer.tipcalculatorwear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalcApp()
        }
    }
}

@Composable
fun TipCalcApp(
    modifier: Modifier = Modifier,
    tipCalcViewModel: TipCalcViewModel = TipCalcViewModel()
) {
    val listState = rememberScalingLazyListState()
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val isScrolledToTop = remember { mutableStateOf(true) }
    val scrollToTop: () -> Unit = {
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        coroutineScope.launch {
            listState.animateScrollToItem(0)
        }
        isScrolledToTop.value = true
    }
    val scrollToBottom: () -> Unit = {
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        coroutineScope.launch {
            listState.animateScrollToItem(1)
        }
        isScrolledToTop.value = false
    }


    DisposableEffect(Unit) {
        coroutineScope.launch { listState.scrollToItem(0) }
        onDispose { }
    }

    TipCalculatorWearTheme {
        Scaffold(
            timeText = { if (!listState.isScrollInProgress) { TimeText(
                timeTextStyle = TimeTextDefaults.timeTextStyle(
                    color = MaterialTheme.colors.onSecondary
            )) } },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = MaterialTheme.colors.background)
        ) {
            ScalingLazyColumn(state = listState,
                autoCentering = AutoCenteringParams(itemIndex = 0),
                modifier = modifier.fillMaxWidth(),
                anchorType = ScalingLazyListAnchorType.ItemCenter,
                contentPadding = PaddingValues(horizontal = 0.dp)) {

                item {
                    KeyboardItem(
                        tipCalcViewModel = tipCalcViewModel,
                        isScrolledToTop = isScrolledToTop,
                        scrollToTop = scrollToTop,
                        scrollToBottom = scrollToBottom
                    )
                }

                item {
                    TipSelectionItem(
                        tipCalcViewModel = tipCalcViewModel,
                        scrollToTop = scrollToTop,
                        scrollToBottom = scrollToBottom
                    )
                }
            }
        }
    }
}

@Composable
fun KeyboardItem(
    tipCalcViewModel: TipCalcViewModel,
    isScrolledToTop: MutableState<Boolean>,
    scrollToTop: () -> Unit,
    scrollToBottom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val buttonHeight = screenHeight * (if (configuration.isScreenRound) .15f else .17f)

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
            onDigitClick = { tipCalcViewModel.onDigitTyped(it) },
            modifier = Modifier.height(buttonHeight)
        )

        BillDigitsRow(
            digits = listOf('4', '5', '6'),
            onDigitClick = { tipCalcViewModel.onDigitTyped(it) },
            modifier = Modifier.height(buttonHeight)
        )

        BillDigitsRow(
            digits = listOf('7', '8', '9'),
            onDigitClick = { tipCalcViewModel.onDigitTyped(it) },
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
                onClick = { tipCalcViewModel.onDeleteTyped() },
                onLongClick = { tipCalcViewModel.setBillAmountBlank() },
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
                onClick = { scrollToBottom() },
                modifier = Modifier
                    .weight(.33f)
                    .height(buttonHeight)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        BaseTotalDisplay(
            billAmountString = tipCalcViewModel.getFormattedBillAmount(),
            onClick = if (isScrolledToTop.value) scrollToBottom else scrollToTop,
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
    ) { onClick() }
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
fun BaseTotalDisplay(
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                InputLabel(
                    labelText = stringResource(
                        id = R.string.sub_total) + ": "
                )
                SmallText(
                    text = "$",
                    color = MaterialTheme.colors.primary,
                    fontSize = 9.sp
                )
                Text(
                    text = "$billAmountString ",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primaryVariant,
                    modifier = Modifier.wrapContentHeight()
                )
            }
        }

    }
}


@Composable
fun TipSelectionItem(
    tipCalcViewModel: TipCalcViewModel,
    scrollToTop: () -> Unit,
    scrollToBottom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 4.dp
            )
            .fillMaxSize()
            .height(screenHeight)
    ) {
        TipSlider(
            tipSliderValue = tipCalcViewModel.getTipPercentage(),
            onTipSliderChange = { tipCalcViewModel.onTipPercentChange(it) },
            modifier = modifier
                .height(30.dp)
                .fillMaxWidth(.9f)
        ) { scrollToTop() }

        GrandTotalDisplay(
            billAmountString = tipCalcViewModel.getFormattedBillAmount(),
            billAmountClicked = { scrollToTop() },
            tipAmountString = tipCalcViewModel.getFormattedCalculatedTipAmount(),
            tipAmountClicked = { scrollToBottom() },
            grandTotalString = tipCalcViewModel.getFormattedGrandTotal(),
            grandTotalClicked = { scrollToBottom() },
            modifier = modifier.wrapContentSize()
        )
    }
}

@Composable
fun TipSlider(
    tipSliderValue: Int,
    onTipSliderChange: (Int) -> Unit,
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
                    id = R.string.tip_percentage) + ":",
                modifier = Modifier.padding(bottom = 4.dp),
                onClick = onClick)
            Text(
                " $tipSliderValue",
                color = MaterialTheme.colors.error,
                fontSize = 16.sp,
                modifier = Modifier.wrapContentHeight()
            )
            SmallText(
                text = "%",
                color = MaterialTheme.colors.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(2.dp))
        }
        InlineSlider(
            value = tipSliderValue,
            onValueChange = {
                onTipSliderChange(it)
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
            valueProgression = IntProgression.fromClosedRange(0, 50, 1),
            decreaseIcon = {
                Image(
                    imageVector = Icons.Default.ArrowLeft,
                    contentDescription = "Tip Percentage Down",
                )
            },
            increaseIcon = {
                Image(
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = "Tip Percentage Up",
                )
            },
            colors = InlineSliderDefaults.colors(selectedBarColor = MaterialTheme.colors.primary),
            modifier = modifier)
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth(.9f)
            .padding(top = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            AmountDisplay(
                amount = billAmountString,
                label = stringResource(id = R.string.display_bill),
                modifier = modifier.padding(bottom = 2.dp),
                onClick = billAmountClicked
            )
            AmountDisplay(
                amount = tipAmountString,
                label = stringResource(id = R.string.display_tip),
                modifier = modifier.padding(top = 2.dp, bottom = 2.dp),
                onClick = tipAmountClicked
            )
            AmountDisplay(
                amount = grandTotalString,
                label = stringResource(id = R.string.display_total),
                modifier = modifier.padding(top = 2.dp, bottom = 2.dp),
                onClick = grandTotalClicked
            )
        }
    }
}

@Composable
fun AmountDisplay(
    amount: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        BasicTextField(
            value = amount,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .clickable { onClick() }
            ) {
                Text(
                    text = "$label: ",
                    fontSize = 10.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.width(70.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallText(
                        text = "$",
                        color = MaterialTheme.colors.primary,
                    )
                    Text(
                        color = MaterialTheme.colors.primaryVariant,
                        textAlign = TextAlign.Center,
                        text = "$amount ",
                        modifier = Modifier
                            .wrapContentSize()
                    )
                }
            }
        }
    }
}

@Composable
fun InputLabel(
    labelText: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 10.sp,
    onClick: () -> Unit = {},
) {
    Text(
        color = MaterialTheme.colors.primary,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        text = labelText,
        modifier = modifier
            .wrapContentSize()
            .clickable { onClick() }
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
fun DefaultPreview() {
    TipCalculatorWearTheme {
        TipSelectionItem(
            tipCalcViewModel = TipCalcViewModel(),
            scrollToTop = {},
            scrollToBottom = {},
            modifier = Modifier.background(color = Color.Black)
        )
    }
}



//@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
//@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
//@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    TipCalcApp()
//}