package com.camerongineer.tipcalculatorwear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStore = DataStoreManager(this)
        val tipCalcViewModel = TipCalcViewModel(dataStore)
        setContent {
            TipCalculatorWearTheme {
                TipCalcApp(tipCalcViewModel)
            }
        }
    }
}

@Composable
fun TipCalcApp(
    tipCalcViewModel: TipCalcViewModel
) {
    val navController = rememberSwipeDismissableNavController()
    val splitViewModel = SplitViewModel(
        datastore = tipCalcViewModel.dataStore,
        subTotal = tipCalcViewModel.getSubtotal(),
        tipAmount = tipCalcViewModel.getTipAmount(),
    )
    val settingsViewModel = SettingsViewModel(dataStore = tipCalcViewModel.dataStore)

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
                splitViewModel = splitViewModel
            )
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                settingsViewModel = settingsViewModel
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
        TipCalcApp(TipCalcViewModel(DataStoreManager(LocalContext.current)))
    }
}
