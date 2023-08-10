package com.camerongineer.tipcalculatorwear.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
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
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme


@Composable
fun SplitCalcScreen(
    navController: NavHostController,
    subTotal: Int,
    tipAmount: Int,
    numSplit: MutableIntState,
    modifier: Modifier = Modifier
) {
    val splitViewModel = SplitViewModel(
        subTotal = subTotal,
        tipAmount = tipAmount,
        numSplit = numSplit
    )
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val topBottomMarginHeight = screenHeight * .10f
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
                verticalArrangement = Arrangement.SpaceAround,
                modifier = modifier
                    .height(screenHeight)
                    .wrapContentSize()
                    .padding(
                        top = topBottomMarginHeight,
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
                        onSplitSubtotalClicked = navController::navigateUp,
                        onSplitTipAmountClicked = navController::navigateUp,
                        onSplitGrandTotalClicked = navController::navigateUp
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
                modifier = Modifier
                    .fillMaxHeight()
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
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        Row(
            modifier = modifier.padding(start = 4.dp, end = 2.dp)
        ) {
            SmallText(
                text = "${stringResource(id = R.string.split_grand_total)}: ",
                color = Color.White)
            AmountDisplay(amountString = splitGrandTotalString)
        }
        LabeledAmountDisplay(
            amountString = splitSubTotalString,
            label = stringResource(id = R.string.display_sub_total),
            smallText = true,
            onClick = { onSplitSubtotalClicked() }
        )

        LabeledAmountDisplay(
            amountString = splitTipString,
            label = stringResource(id = R.string.display_tip),
            smallText = true,
            onClick = { onSplitTipAmountClicked() }
        )
    }
}


@Composable
fun SplitButtons(
    numSplit: Int,
    onSplitUpClicked: () -> Unit,
    onSplitDownClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(
                start = 8.dp,
                end = 12.dp)
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
                .height(36.dp)
        )
        Text(
            text = numSplit.toString(),
            fontSize = 30.sp,
            color = MaterialTheme.colors.primaryVariant
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
                .height(36.dp)
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
                .padding(top = 2.dp)
        ) {
            SmallText(
                text = stringResource(id = R.string.split_warning),
                color = Color.Red,
                fontSize = 8.sp,
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .padding(start = 4.dp)
            )
            if (subTotalRemainder > 0) {
                LabeledAmountDisplay(
                    amountString = subTotalRemainderString,
                    label = stringResource(id = R.string.display_sub_total),
                    smallText = true
                )
            }
            if (tipAmountRemainder > 0) {
                LabeledAmountDisplay(
                    amountString = tipAmountRemainderString,
                    label = stringResource(id = R.string.display_tip),
                    smallText = true
                )
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
            subTotal = 2000,
            tipAmount = 300,
            numSplit = mutableIntStateOf(2),
            modifier = Modifier.background(color = Color.Black)
        )
    }
}
