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
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Button
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
    val configuration = LocalConfiguration.current
    val haptics = LocalHapticFeedback.current
    val screenHeight = configuration.screenHeightDp.dp
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .fillMaxSize()

                    ) {
                        Spacer(Modifier.height(screenHeight * .18f))
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(.9f)
                        ) {
                            BillKeyboardDigit(
                                digitChar = '1',
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tipCalcViewModel.onDigitTyped('1')
                                },
                                modifier = Modifier.weight(.33f)
                            )
                            BillKeyboardDigit(
                                digitChar = '2',
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tipCalcViewModel.onDigitTyped('2')
                                },
                                modifier = Modifier.weight(.33f)
                            )
                            BillKeyboardDigit(
                                digitChar = '3',
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tipCalcViewModel.onDigitTyped('3')
                                },
                                modifier = Modifier.weight(.33f)
                            )
                        }


                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(.9f)
                        ) {
                            BillKeyboardDigit(
                                digitChar = '4',
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tipCalcViewModel.onDigitTyped('4')
                                },
                                modifier = Modifier.weight(.33f)
                            )
                            BillKeyboardDigit(
                                digitChar = '5',
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tipCalcViewModel.onDigitTyped('5')
                                },
                                modifier = Modifier.weight(.33f)
                            )
                            BillKeyboardDigit(
                                digitChar = '6',
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tipCalcViewModel.onDigitTyped('6')
                                },
                                modifier = Modifier.weight(.33f)
                            )
                        }


                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(.9f)
                        ) {
                            BillKeyboardDigit(
                                digitChar = '7',
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tipCalcViewModel.onDigitTyped('6')
                                },
                                modifier = Modifier.weight(.33f)
                            )
                            BillKeyboardDigit(
                                digitChar = '8',
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tipCalcViewModel.onDigitTyped('8')
                                },
                                modifier = Modifier.weight(.33f)
                            )
                            BillKeyboardDigit(
                                digitChar = '9',
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tipCalcViewModel.onDigitTyped('9')
                                },
                                modifier = Modifier.weight(.33f)
                            )
                        }


                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(.9f)
                        ) {
                            BillKeyboardButton(
                                icon = Icons.Default.KeyboardBackspace,
                                contentDescription = "Keyboard Backspace",
                                onClick = {
                                    tipCalcViewModel.onDeleteTyped()
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                          },
                                onLongClick = {
                                    tipCalcViewModel.setBillAmountBlank()
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                              },
                                modifier = Modifier.weight(.33f)
                            )
                            BillKeyboardDigit(
                                digitChar = '0',
                                onClick = { tipCalcViewModel.onDigitTyped('0') },
                                modifier = Modifier.weight(.33f)
                            )
                            BillKeyboardButton(
                                icon = Icons.Default.Done,
                                contentDescription = "Keyboard Bottom Of Screen",
                                onClick = scrollToBottom,
                                modifier = Modifier.weight(.33f)
                            )
                        }
                        BaseTotalDisplay(
                            billAmountString = tipCalcViewModel.getFormattedBillAmount(),
                            onClick = if (isScrolledToTop.value) scrollToBottom else scrollToTop,
                            modifier = modifier
                                .background(
                                    color = MaterialTheme.colors.surface,
                                    shape = CircleShape
                                )
                                .height(30.dp)
                                .fillMaxWidth(.8f)
                                .wrapContentHeight()
                        )
                    }
                }



                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .fillMaxSize()

                    ) {
                        Spacer(
                            Modifier
                                .height(16.dp)
                                .clickable { scrollToTop() })
                        TipSlider(
                            tipSliderValue = tipCalcViewModel.getTipPercentage(),
                            onTipSliderChange = {
                                tipCalcViewModel.onTipPercentChange(it)
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                },
                            modifier = modifier
                                .height(30.dp)
                                .fillMaxWidth(.9f)
                        )
                        GrandTotalDisplay(
                            billAmountString = tipCalcViewModel.getFormattedBillAmount(),
                            billAmountClicked = { scrollToTop() },
                            tipAmountString = tipCalcViewModel.getFormattedCalculatedTipAmount(),
                            tipAmountClicked = { scrollToBottom() },
                            grandTotalString = tipCalcViewModel.getFormattedGrandTotal(),
                            grandTotalClicked = { scrollToBottom() },
                            modifier = modifier.wrapContentSize()
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BillKeyboardDigit(
    digitChar: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(25),
        modifier = modifier
            .height(30.dp)
            .padding(start = 2.dp, end = 2.dp, bottom = 4.dp)
    ) {
        Text(
            text = digitChar.toString(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onPrimary
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BillKeyboardButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.secondaryButtonColors(),
        shape = RoundedCornerShape(25),
        modifier = modifier
            .height(30.dp)
            .padding(start = 2.dp, end = 2.dp, bottom = 4.dp)
    ) {
        Image(
            imageVector = icon,
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant),
            modifier = Modifier.combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick
            )
        )
    }
}

@Composable
fun BaseTotalDisplay(
    billAmountString: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.secondaryButtonColors(),
            modifier = modifier
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (billAmountString.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SmallText(text = "$", color = MaterialTheme.colors.primary)
                        Text(
                            text = "$billAmountString ",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.primaryVariant
                        )
                    }
                }
            }
        }
        InputLabel(
            labelText = stringResource(
                id = R.string.base_amount))
    }
}


@Composable
fun TipSlider(
    tipSliderValue: Int,
    onTipSliderChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp)
    ) {
        InlineSlider(
            value = tipSliderValue,
            onValueChange = onTipSliderChange,
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
        InputLabel(
            labelText = stringResource(
                id = R.string.tip_percentage))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .padding(bottom = 3.dp)
                .wrapContentSize()
        ) {
            Text(" $tipSliderValue", color = MaterialTheme.colors.error, fontSize = 16.sp)
            SmallText(text = "%", color = MaterialTheme.colors.error, fontSize = 12.sp)
        }
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
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = modifier
                .wrapContentHeight()
        ) {
            AmountDisplay(
                amount = tipAmountString,
                label = stringResource(id = R.string.display_tip),
                onClick = tipAmountClicked
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            AmountDisplay(
                amount = billAmountString,
                label = stringResource(id = R.string.display_bill),
                modifier = modifier.padding(top = 5.dp),
                onClick = billAmountClicked
            )
            AmountDisplay(
                amount = grandTotalString,
                label = stringResource(id = R.string.display_total),
                modifier = modifier.padding(top = 5.dp),
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
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        BasicTextField(
            value = amount,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .wrapContentSize()
                .padding(start = 5.dp, end = 5.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = modifier
                    .clickable { onClick() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallText(
                        text = "$",
                        color = MaterialTheme.colors.primary
                    )
                    Text(
                        color = MaterialTheme.colors.primaryVariant,
                        textAlign = TextAlign.Center,
                        text = "$amount ",
                        modifier = Modifier
                            .wrapContentSize()
                    )
                }
                Text(label, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun InputLabel(labelText: String) {
    Text(
        color = MaterialTheme.colors.primary,
        fontSize = 10.sp,
        textAlign = TextAlign.Center,
        text = labelText,
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 2.dp)
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
        textAlign = TextAlign.Left,
        modifier = modifier.padding(end=1.dp)
    )
}


@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun DefaultPreview() {
    TipCalcApp()
}