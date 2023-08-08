package com.camerongineer.tipcalculatorwear.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme


@Composable
fun SplitCalcScreen(
    navController: NavHostController,
    subTotal: Int,
    tipAmount: Int,
    numSplit: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val splitViewModel = SplitViewModel(
        subTotal = subTotal,
        tipAmount = tipAmount,
        numSplit = numSplit
    )
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    Scaffold(
        timeText = {
            TimeText(
                timeTextStyle = TimeTextDefaults.timeTextStyle(
                    color = MaterialTheme.colors.onSecondary)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .height(screenHeight)
        ) {
            Row {
                SplitDisplay(
                    splitSubTotalString = splitViewModel.getFormattedSplitSubTotal(),
                    splitTipString = splitViewModel.getFormattedSplitSubTotalRemainder(),
                    splitGrandTotalString = splitViewModel.getFormattedSplitGrandTotal(),
                    onSplitSubtotalClicked = navController::navigateUp,
                    onSplitTipAmountClicked = navController::navigateUp,
                    onSplitGrandTotalClicked = navController::navigateUp
                )
                SplitButtons(
                    numSplit = splitViewModel.getNumSplit(),
                    onSplitUpClicked = splitViewModel::onSplitUpClicked,
                    onSplitDownClicked = splitViewModel::onSplitDownClicked
                )
            }
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        AmountDisplay(
            amount = splitSubTotalString,
            label = stringResource(id = R.string.display_sub_total),
            onClick = { onSplitSubtotalClicked() }
        )

        AmountDisplay(
            amount = splitTipString,
            label = stringResource(id = R.string.display_tip),
            onClick = { onSplitTipAmountClicked() }
        )

        AmountDisplay(
            amount = splitGrandTotalString,
            label = stringResource(id = R.string.display_total),
            onClick = { onSplitGrandTotalClicked() }
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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        BillKeyboardButton(
            onClick = onSplitUpClicked
        )
        Text(numSplit.toString())
        BillKeyboardButton(
            onClick = onSplitDownClicked
        )
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
            navController = rememberNavController(),
            subTotal = 2000,
            tipAmount = 300,
            numSplit = mutableStateOf(3),
            modifier = Modifier.background(color = Color.Black)
        )
    }
}
