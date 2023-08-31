package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.theme.OriginalTheme
import com.camerongineer.tipcalculatorwear.presentation.theme.Typography


@Composable
fun SplitCalcScreen(
    splitViewModel: SplitViewModel,
    onBackButtonPressed: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val topBottomMarginHeight = screenHeight * .10f
    val splitButtonsWidth = screenWidth * .22f


    Scaffold(
        timeText = { TimeText(
                timeTextStyle = TimeTextDefaults
                    .timeTextStyle(color = MaterialTheme.colors.onSecondary)) },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
                modifier = Modifier
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
                ) {
                    SplitDisplay(
                        currencySymbol = splitViewModel.getCurrencySymbol(),
                        splitSubTotalString = splitViewModel.getFormattedSplitSubTotal(),
                        splitTipString = splitViewModel.getFormattedSplitTipAmount(),
                        splitGrandTotalString = splitViewModel.getFormattedSplitGrandTotal(),
                        onClick = onBackButtonPressed
                    )
                }

                UnevenSplitWarning(
                    currencySymbol = splitViewModel.getCurrencySymbol(),
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
    currencySymbol: String,
    splitSubTotalString: String,
    splitTipString: String,
    splitGrandTotalString: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = withHaptics(block = onClick))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(start = 4.dp, end = 4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.split_grand_total),
                style = Typography.title2
            )
            AmountDisplay(
                currencySymbol = currencySymbol,
                amountString = splitGrandTotalString,
                style = Typography.display1
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
                    labelText = stringResource(id = R.string.display_subtotal),
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.height(14.dp))
                InputLabel(
                    labelText = stringResource(id = R.string.display_tip),
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.height(14.dp))
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                AmountDisplay(
                    currencySymbol = currencySymbol,
                    amountString = splitSubTotalString,
                    modifier = Modifier.height(14.dp)
                )
                AmountDisplay(
                    currencySymbol = currencySymbol,
                    amountString = splitTipString,
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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(end = 8.dp)
    ) {
        BillKeyboardButton(
            icon = Icons.Default.ArrowDropUp,
            contentDescription = stringResource(id = R.string.increase),
            onClick = onSplitUpClicked,
            modifier = Modifier
                .width(30.dp)
                .height(32.dp)
        )
        Text(
            text = "$numSplit",
            style = Typography.display2,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.combinedClickable(
                onLongClick = withHaptics(
                    block = onNumSplitLongClicked,
                    isLongPress = true
                ),
                onClick = withHaptics { }
            )
        )
        BillKeyboardButton(
            icon = Icons.Default.ArrowDropDown,
            contentDescription = stringResource(id = R.string.decrease),
            onClick = onSplitDownClicked,
            modifier = Modifier
                .width(30.dp)
                .height(32.dp)
        )
    }
}

@Composable
fun UnevenSplitWarning(
    currencySymbol: String,
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
                style = Typography.caption3
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
                            labelText = stringResource(id = R.string.display_subtotal),
                            color = MaterialTheme.colors.onBackground,
                            modifier = Modifier.height(16.dp)
                        )
                    }
                    if (tipAmountRemainder > 0) {
                        InputLabel(
                            labelText = stringResource(id = R.string.display_tip),
                            color = MaterialTheme.colors.onBackground,
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
                            currencySymbol = currencySymbol,
                            amountString = subTotalRemainderString,
                            modifier = Modifier.height(16.dp)
                        )
                    }
                    if (tipAmountRemainder > 0) {
                        AmountDisplay(
                            currencySymbol = currencySymbol,
                            amountString = tipAmountRemainderString,
                            modifier = Modifier.height(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_RECT, showSystemUi = true)
@Composable
fun SplitPreview() {
    OriginalTheme {
        SplitCalcScreen(
                SplitViewModel(dataStore = DataStoreManager(LocalContext.current), 3001, 251),
                onBackButtonPressed = {}
        )
    }
}
