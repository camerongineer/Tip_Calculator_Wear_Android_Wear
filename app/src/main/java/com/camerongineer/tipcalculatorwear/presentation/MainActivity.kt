package com.camerongineer.tipcalculatorwear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorWearTheme {
                TipCalcApp()
            }
        }
    }

}

@Composable
fun TipCalcApp(
    tipCalcViewModel: TipCalcViewModel = TipCalcViewModel()
) {
    val navController = rememberSwipeDismissableNavController()
    val numSplit = remember { mutableIntStateOf(SplitViewModel.DEFAULT_SPLIT_NUM) }
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "home")
    {
        composable("home") {
            TipCalcScreen(
                navController = navController,
                tipCalcViewModel = tipCalcViewModel
            )
        }
        composable("split") {
            SplitCalcScreen(
                navController = navController,
                subTotal = tipCalcViewModel.getSubtotal(),
                tipAmount = tipCalcViewModel.getTipAmount(),
                numSplit = numSplit
            )
        }
    }
}



@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_RECT, showSystemUi = true)
@Composable
fun DefaultPreview() {
    TipCalculatorWearTheme {
        TipCalcApp()
    }
}
