package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import com.camerongineer.tipcalculatorwear.presentation.theme.Typography
import com.camerongineer.tipcalculatorwear.utils.getFontMultiplier
import com.camerongineer.tipcalculatorwear.utils.scaleFont


@Composable
fun SplitCalcScreen(
    splitViewModel: SplitViewModel,
    onBackButtonPressed: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val isLargeFont by splitViewModel.getLargeTextFlow().collectAsState(initial = false)
    val fontMultiplier = getFontMultiplier(screenHeight, isLargeFont)

    Scaffold(
        timeText = { TimeText(
                timeTextStyle = TimeTextDefaults
                    .timeTextStyle(
                        color = MaterialTheme.colors.onSurfaceVariant,
                        fontSize = Typography.title3.fontSize
                    )
        ) },
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = screenWidth / 50,
                    bottom = screenHeight * .16f
                )
//                .onRotaryScrollEvent{
//                    coroutineScope.launch{
//                        if (it.verticalScrollPixels > 0) {
//                            splitViewModel.onSplitUpClicked()
//                        } else if (it.verticalScrollPixels < 0)  {
//                            splitViewModel.onSplitDownClicked()
//                        }
//                    }
//                    true
//                }
//                .focusRequester(focusRequester)
//                .focusable(),
        ) {
            val shrinkFont = isLargeFont && (splitViewModel.getSplitTipAmountRemainder() +
                    splitViewModel.getSplitSubTotalRemainder() > 0)
            Spacer(modifier = Modifier.height(2.dp))
            SplitDisplay(
                currencySymbol = splitViewModel.getCurrencySymbol(),
                splitSubTotalString = splitViewModel.getFormattedSplitSubTotal(),
                splitTipString = splitViewModel.getFormattedSplitTipAmount(),
                splitGrandTotalString = splitViewModel.getFormattedSplitGrandTotal(),
                fontMultiplier = fontMultiplier * (if (shrinkFont) .9f else 1f),
                onClick = onBackButtonPressed
            )
            UnevenSplitWarning(
                currencySymbol = splitViewModel.getCurrencySymbol(),
                subTotalRemainder = splitViewModel.getSplitSubTotalRemainder(),
                subTotalRemainderString = splitViewModel.getFormattedSplitSubTotalRemainder(),
                tipAmountRemainder = splitViewModel.getSplitTipAmountRemainder(),
                tipAmountRemainderString = splitViewModel.getFormattedSplitTipAmountRemainder(),
                fontMultiplier = fontMultiplier * .9f
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
        ) {
            SplitButtons(
                numSplit = splitViewModel.getNumSplit(),
                onSplitUpClicked = splitViewModel::onSplitUpClicked,
                onSplitDownClicked = splitViewModel::onSplitDownClicked,
                onNumSplitLongClicked = splitViewModel::resetNumSplit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = screenHeight / 20)
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
    fontMultiplier: Float,
    modifier: Modifier = Modifier
) {
    val lineHeight = 18.dp * fontMultiplier * .9f
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = withHaptics(block = onClick))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AmountDisplay(
                currencySymbol = currencySymbol,
                amountString = splitGrandTotalString,
                style = scaleFont(Typography.display1, fontMultiplier * 1.1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
            ){
                InputLabel(
                    labelText = stringResource(id = R.string.display_subtotal),
                    color = MaterialTheme.colors.onBackground,
                    style = scaleFont(Typography.caption2, fontMultiplier),
                    modifier = Modifier.height(lineHeight))
                InputLabel(
                    labelText = stringResource(id = R.string.display_tip),
                    color = MaterialTheme.colors.onBackground,
                    style = scaleFont(Typography.caption2, fontMultiplier),
                    modifier = Modifier.height(lineHeight))
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                AmountDisplay(
                    currencySymbol = currencySymbol,
                    amountString = splitSubTotalString,
                    style = scaleFont(Typography.caption1, fontMultiplier),
                    modifier = Modifier.height(lineHeight)
                )
                AmountDisplay(
                    currencySymbol = currencySymbol,
                    amountString = splitTipString,
                    style = scaleFont(Typography.caption1, fontMultiplier),
                    modifier = Modifier.height(lineHeight)
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
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BillKeyboardButton(
                icon = Icons.Default.ArrowLeft,
                contentDescription = stringResource(id = R.string.decrease),
                onClick = onSplitDownClicked,
                modifier = Modifier
                    .width(36.dp)
                    .height(32.dp)
            )

            Text(
                text = "$numSplit",
                style = Typography.display1,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .padding(start = 4.dp, end=4.dp)
                    .combinedClickable(
                    onLongClick = withHaptics(
                        block = onNumSplitLongClicked,
                        isLongPress = true
                    ),
                    onClick = withHaptics { }))
            BillKeyboardButton(
                icon = Icons.Default.ArrowRight,
                contentDescription = stringResource(id = R.string.increase),
                onClick = onSplitUpClicked,
                modifier = Modifier
                    .width(36.dp)
                    .height(32.dp)
            )
        }
    }
}

@Composable
fun UnevenSplitWarning(
    currencySymbol: String,
    subTotalRemainder: Int,
    subTotalRemainderString: String,
    tipAmountRemainder: Int,
    tipAmountRemainderString: String,
    fontMultiplier: Float,
    modifier: Modifier = Modifier
) {
    val lineHeight = 18.dp * fontMultiplier * .9f
    if (subTotalRemainder > 0 || tipAmountRemainder > 0) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(start = 4.dp, end = 2.dp, top = 2.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(.92f)
            ) {
                SmallText(
                    text = stringResource(id = R.string.split_warning),
                    color = MaterialTheme.colors.secondaryVariant,
                    style = scaleFont(Typography.caption3, fontMultiplier),
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 2.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                ){
                    if (subTotalRemainder > 0) {
                        InputLabel(
                            labelText = stringResource(id = R.string.display_subtotal),
                            color = MaterialTheme.colors.onBackground,
                            style = scaleFont(Typography.caption2, fontMultiplier),
                            modifier = Modifier.height(lineHeight)
                        )
                    }
                    if (tipAmountRemainder > 0) {
                        InputLabel(
                            labelText = stringResource(id = R.string.display_tip),
                            color = MaterialTheme.colors.onBackground,
                            style = scaleFont(Typography.caption2, fontMultiplier),
                            modifier = Modifier.height(lineHeight)
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
                            style = scaleFont(Typography.caption1, fontMultiplier),
                            modifier = Modifier.height(lineHeight)
                        )
                    }
                    if (tipAmountRemainder > 0) {
                        AmountDisplay(
                            currencySymbol = currencySymbol,
                            amountString = tipAmountRemainderString,
                            style = scaleFont(Typography.caption1, fontMultiplier),
                            modifier = Modifier.height(lineHeight)
                        )
                    }
                }
            }
        }
    }
}


@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true, locale = "FR")
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true, locale = "DE")
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_RECT, showSystemUi = true, locale = "ES")
@Composable
fun SplitPreview() {
    TipCalculatorWearTheme {
        SplitCalcScreen(
                SplitViewModel(dataStore = DataStoreManager(LocalContext.current), 999999, 75001),
                onBackButtonPressed = {}
        )
    }
}
