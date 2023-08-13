package com.camerongineer.tipcalculatorwear.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme


@Composable
fun SplitCalcScreen(
    navController: NavHostController,
    splitViewModel: SplitViewModel,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val haptics = LocalHapticFeedback.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val topBottomMarginHeight = screenHeight * .10f
    val splitButtonsWidth = screenWidth * .22f

    val returnToKeyboard = {
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        navController.navigateUp()
    }
    Scaffold(
        timeText = {
            TimeText(
                timeTextStyle = TimeTextDefaults.timeTextStyle(
                    color = MaterialTheme.colors.onSecondary)
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .height(screenHeight)
                    .width(screenWidth - splitButtonsWidth)
                    .padding(
                        top = topBottomMarginHeight,
                        start = 10.dp,
                        bottom = 4.dp
                    )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top,
                    modifier = modifier
                ) {
                    SplitDisplay(
                        splitSubTotalString = splitViewModel.getFormattedSplitSubTotal(),
                        splitTipString = splitViewModel.getFormattedSplitTipAmount(),
                        splitGrandTotalString = splitViewModel.getFormattedSplitGrandTotal(),
                        onSplitSubtotalClicked = returnToKeyboard,
                        onSplitTipAmountClicked = returnToKeyboard,
                        onSplitGrandTotalClicked = returnToKeyboard
                    )
                }

                UnevenSplitWarning(
                    subTotalRemainder = splitViewModel.getSplitSubTotalRemainder(),
                    subTotalRemainderString = splitViewModel.getFormattedSplitSubTotalRemainder(),
                    tipAmountRemainder = splitViewModel.getSplitTipAmountRemainder(),
                    tipAmountRemainderString = splitViewModel.getFormattedSplitTipAmountRemainder()
                )
            }

            SplitButtons(
                numSplit = splitViewModel.getNumSplit(),
                onSplitUpClicked = splitViewModel::onSplitUpClicked,
                onSplitDownClicked = splitViewModel::onSplitDownClicked,
                onNumSplitLongClicked = splitViewModel::resetNumSplit,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(splitButtonsWidth)
            )
        }
    }
}

@Composable
fun SplitDisplay(
    splitSubTotalString: String,
    splitTipString: String,
    splitGrandTotalString: String,
    onSplitSubtotalClicked: () -> Boolean,
    onSplitTipAmountClicked: () -> Boolean,
    onSplitGrandTotalClicked: () -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(start = 4.dp, end = 4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.split_grand_total),
                fontSize = 14.sp,
                color = Color.White)
            AmountDisplay(
                amountString = splitGrandTotalString,
                fontSize = 24.sp,
                onClick = { onSplitGrandTotalClicked() }
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
            ){
                InputLabel(
                    labelText = stringResource(id = R.string.display_sub_total),
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 10.sp,
                    onClick = { onSplitSubtotalClicked() },
                    modifier = Modifier.height(14.dp))
                InputLabel(
                    labelText = stringResource(id = R.string.display_tip),
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 10.sp,
                    onClick = { onSplitTipAmountClicked() },
                    modifier = Modifier.height(14.dp))
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                AmountDisplay(
                    amountString = splitSubTotalString,
                    fontSize = 12.sp,
                    onClick = { onSplitSubtotalClicked() },
                    modifier = Modifier.height(14.dp)
                )
                AmountDisplay(
                    amountString = splitTipString,
                    fontSize = 12.sp,
                    onClick = { onSplitTipAmountClicked() },
                    modifier = Modifier.height(14.dp)
                )
            }
        }

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplitButtons(
    numSplit: Int,
    onSplitUpClicked: () -> Unit,
    onSplitDownClicked: () -> Unit,
    onNumSplitLongClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(end = 8.dp)
    ) {
        BillKeyboardButton(
            icon = Icons.Default.ArrowDropUp,
            contentDescription = "Split Up",
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSplitUpClicked()
            },
            modifier = Modifier
                .width(30.dp)
                .height(32.dp)
        )
        Text(
            text = numSplit.toString(),
            fontSize = 28.sp,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.combinedClickable(
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onNumSplitLongClicked()
                },
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            )
        )
        BillKeyboardButton(
            icon = Icons.Default.ArrowDropDown,
            contentDescription = "Split Down",
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSplitDownClicked()
            },
            modifier = Modifier
                .width(30.dp)
                .height(32.dp)
        )
    }
}

@Composable
fun UnevenSplitWarning(
    subTotalRemainder: Int,
    subTotalRemainderString: String,
    tipAmountRemainder: Int,
    tipAmountRemainderString: String,
    modifier: Modifier = Modifier
) {
    if (subTotalRemainder > 0 || tipAmountRemainder > 0) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(start = 4.dp, end = 2.dp, top = 4.dp)
        ) {
            SmallText(
                text = stringResource(id = R.string.split_warning),
                color = MaterialTheme.colors.secondaryVariant,
                fontSize = 9.sp,
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                ){
                    if (subTotalRemainder > 0) {
                        InputLabel(
                            labelText = stringResource(id = R.string.display_sub_total),
                            color = MaterialTheme.colors.onBackground,
                            fontSize = 12.sp,
                            modifier = Modifier.height(16.dp)
                        )
                    }
                    if (tipAmountRemainder > 0) {
                        InputLabel(
                            labelText = stringResource(id = R.string.display_tip),
                            color = MaterialTheme.colors.onBackground,
                            fontSize = 12.sp,
                            modifier = Modifier.height(16.dp)
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    if (subTotalRemainder > 0) {
                        AmountDisplay(
                            amountString = subTotalRemainderString,
                            fontSize = 14.sp,
                            modifier = Modifier.height(16.dp)
                        )
                    }
                    if (tipAmountRemainder > 0) {
                        AmountDisplay(
                            amountString = tipAmountRemainderString,
                            fontSize = 14.sp,
                            modifier = Modifier.height(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_RECT, showSystemUi = true)
@Composable
fun SplitPreview() {
    TipCalculatorWearTheme {
        SplitCalcScreen(
            navController = rememberSwipeDismissableNavController(),
            splitViewModel = SplitViewModel(
                datastore = DataStoreManager(LocalContext.current),
                subTotal = 3000,
                tipAmount = 1000,
            ),
            modifier = Modifier.background(color = Color.Black)
        )
    }
}
