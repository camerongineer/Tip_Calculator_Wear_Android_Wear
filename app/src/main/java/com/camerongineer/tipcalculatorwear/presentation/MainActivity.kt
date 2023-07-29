package com.camerongineer.tipcalculatorwear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.rememberScalingLazyListState
import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme

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
    TipCalculatorWearTheme {
        Scaffold(
            timeText = { if (!listState.isScrollInProgress) { TimeText(
                timeTextStyle = TimeTextDefaults.timeTextStyle(
                    color = MaterialTheme.colors.primary
            )) } },
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(9.dp)
                    .fillMaxSize()

            ) {
                Spacer(Modifier.height(15.dp))
                BaseTotalInput(
                    billAmountString = tipCalcViewModel.getFormattedBillAmount(),
                    onBillAmountChange = tipCalcViewModel::onBillAmountChanged,
                    modifier = modifier
                        .background(
                            color = MaterialTheme.colors.surface,
                            shape = CircleShape
                        )
                        .height(30.dp)
                        .fillMaxSize()
                )
                TipSlider(
                    tipSliderValue = tipCalcViewModel.getTipPercentage(),
                    onTipSliderChange = tipCalcViewModel::onTipPercentChange,
                    modifier = modifier
                        .height(30.dp)
                        .fillMaxWidth(1f)
                )
                Spacer(Modifier.height(5.dp))
                GrandTotalDisplay(
                    billAmountString = tipCalcViewModel.getFormattedBillAmount(),
                    tipPercentage = tipCalcViewModel.getTipPercentage().toString(),
                    tipAmountString = tipCalcViewModel.getFormattedCalculatedTipAmount(),
                    grandTotalString = tipCalcViewModel.getFormattedGrandTotal(),
                    modifier = modifier.wrapContentSize()
                )
            }
        }
    }
}

@Composable
fun BaseTotalInput(
    billAmountString: String,
    onBillAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(5.dp)) {
        InputLabel(labelText = stringResource(id = R.string.base_amount))
        BasicTextField(
            modifier = modifier.align(Alignment.CenterVertically),
            value = billAmountString,
            singleLine = true,
            onValueChange = onBillAmountChange,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            )
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (billAmountString.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SmallText(text = "$", color = MaterialTheme.colors.primary)
                        Text(
                            text = billAmountString,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.primaryVariant
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TipSlider(
    tipSliderValue: Int,
    onTipSliderChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(5.dp)) {
        InputLabel(
            labelText = stringResource(
                id = R.string.tip_percentage))
        InlineSlider(
            value = tipSliderValue,
            onValueChange = onTipSliderChange,
            valueProgression = IntProgression.fromClosedRange(0, 50, 1),
            decreaseIcon = {
                Image(
                    imageVector = Icons.Default.ArrowLeft,
                    contentDescription = null,
                )
            },
            increaseIcon = {
                Image(
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = null,
                )
            },
            colors = InlineSliderDefaults.colors(selectedBarColor = MaterialTheme.colors.primary),
            modifier = modifier)
    }
}

@Composable
fun GrandTotalDisplay(
    billAmountString: String,
    tipPercentage: String,
    tipAmountString: String,
    grandTotalString: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(0.dp)) {
        AmountDisplay(amount = billAmountString, label = stringResource(id = R.string.display_bill), modifier.padding(top = 5.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(bottom = 3.dp)) {
                Text(tipPercentage, color = MaterialTheme.colors.error, fontSize = 13.sp)
                SmallText(text = "%", color = MaterialTheme.colors.error, fontSize = 9.sp)
            }
            AmountDisplay(amount = tipAmountString, label = stringResource(id = R.string.display_tip))
        }
        AmountDisplay(amount = grandTotalString, label = stringResource(id = R.string.display_total), modifier.padding(top = 5.dp))
    }
}

@Composable
fun AmountDisplay(amount: String, label: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.wrapContentSize()
    ) {
        BasicTextField(value = amount, onValueChange = {}, readOnly = true, modifier = Modifier
            .wrapContentSize()
            .padding(start = 5.dp, end = 5.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SmallText(text = "$", color = MaterialTheme.colors.primary)
                    Text(
                        color = MaterialTheme.colors.primaryVariant,
                        textAlign = TextAlign.Center,
                        text = amount,
                        modifier = Modifier
                            .padding(bottom = 0.dp)
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
            .padding(end = 5.dp)
            .wrapContentSize()
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
