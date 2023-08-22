package com.camerongineer.tipcalculatorwear.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
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
import com.camerongineer.tipcalculatorwear.R
import com.camerongineer.tipcalculatorwear.presentation.constants.OptionsLists
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme

@Composable
fun PickerScreen(
    navController: NavHostController,
    pickerViewModel: PickerViewModel,
    callbackCommand: (Int) -> Unit
) {
    val startIndex = pickerViewModel.optionsList
        .indexOfFirst { it.value == pickerViewModel.state.intValue }
    val state = rememberPickerState(
        initialNumberOfOptions = pickerViewModel.optionsList.size,
        initiallySelectedOption = startIndex,
        repeatItems = false
    )

    val returnCommand = withHaptics {
        val savedOption = pickerViewModel.optionsList[state.selectedOption].value
        callbackCommand(savedOption)
        pickerViewModel.state.intValue = savedOption
        Log.d("NAV", "To Settings Screen")
        navController.navigateUp()
    }
    Scaffold(
        timeText = {
            TimeText(
                timeTextStyle = TimeTextDefaults
                    .timeTextStyle(color = MaterialTheme.colors.onSecondary))
        },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.9f)
        ) {
            Picker(
                state = state,
                contentDescription = stringResource(id = R.string.values),
                flingBehavior = PickerDefaults.flingBehavior(state = state)
            ) {
                Text(
                    text = pickerViewModel.optionsList[it].toString(),
                    fontSize = 28.sp,
                    color = MaterialTheme.colors.primaryVariant,
                )
            }
        }

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = returnCommand,
                modifier = Modifier
                    .size(60.dp)
                    .padding(10.dp)
            ) {
                Image(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.submit),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.background),
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
            PickerViewModel(remember {mutableIntStateOf(2)}, OptionsLists.ROUNDING_INCREMENTS),
        ) { }
    }
}