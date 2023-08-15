package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PickerDefaults
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import kotlin.reflect.KFunction1

@Composable
fun PickerScreen(
    navController: NavHostController,
    pickerViewModel: PickerViewModel,
    callbackCommand: KFunction1<Int, Unit>?
) {
    val state = rememberPickerState(
        initialNumberOfOptions = pickerViewModel.maximumValue - pickerViewModel.minimumValue,
        initiallySelectedOption = pickerViewModel.initialValue,
        repeatItems = false
    )
    Scaffold(
        timeText = {
            TimeText(
                timeTextStyle = TimeTextDefaults
                    .timeTextStyle(color = MaterialTheme.colors.onSecondary))
        },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize(),
        ) {
            Picker(
                state = state,
                contentDescription = "Select Value",
                modifier = Modifier.weight(.7f),
                flingBehavior = PickerDefaults.flingBehavior(state = state),
                readOnly = true
            ) {
                Text(
                    text = it.toString(),
                    fontSize = 28.sp
                )
            }
            Button(
                onClick = {
                    callbackCommand?.invoke(state.selectedOption)
                    navController.navigateUp()
                },
                modifier = Modifier
                    .size(60.dp)
                    .padding(10.dp)
            ) {
                Image(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Value Selected",
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                )
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_RECT, showSystemUi = true)
@Composable
fun PickerPreview() {
    TipCalculatorWearTheme {
        PickerScreen(
            rememberSwipeDismissableNavController(),
            PickerViewModel(15, 0, 100),
            null,
        )
    }
}