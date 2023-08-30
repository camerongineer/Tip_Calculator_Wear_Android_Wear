package com.camerongineer.tipcalculatorwear.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.PickerScreen
import com.camerongineer.tipcalculatorwear.presentation.SettingsScreen
import com.camerongineer.tipcalculatorwear.presentation.SettingsViewModel
import com.camerongineer.tipcalculatorwear.presentation.SplitCalcScreen
import com.camerongineer.tipcalculatorwear.presentation.SplitViewModel
import com.camerongineer.tipcalculatorwear.presentation.TipCalcScreen
import com.camerongineer.tipcalculatorwear.presentation.TipCalcViewModel
import com.camerongineer.tipcalculatorwear.presentation.constants.OptionsLists

@Composable
fun Navigation(navController: NavHostController) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        val tipCalcViewModel = TipCalcViewModel(dataStoreManager)
        composable(route = Screen.MainScreen.route) {
            TipCalcScreen(
                tipCalcViewModel = tipCalcViewModel,
                onSettingsButtonClicked = { navController.navigate("settings") },
                onSplitButtonClicked = { navController.navigate(Screen.SplitScreen.route) }
            )
            Log.d("NAV", "To Main Screen")
        }
        composable(route = Screen.SplitScreen.route) {
            val splitViewModel = SplitViewModel(
                dataStore = dataStoreManager,
                subTotal = tipCalcViewModel.subTotal.intValue,
                tipAmount = tipCalcViewModel.tipAmount.intValue
            )
            SplitCalcScreen(
                splitViewModel = splitViewModel,
                onBackButtonPressed = navController::navigateUp
            )
            Log.d("NAV", "To Split Screen")
        }
        navigation(
            startDestination = Screen.SettingsScreen.route,
            route = "settings"
        ) {
            val settingsViewModel = SettingsViewModel(dataStoreManager)
            composable(route = Screen.SettingsScreen.route) {
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    navigateToDefaultTipScreen = { navController.navigate(Screen.DefaultTipScreen.route) },
                    navigateToRoundingNumScreen = { navController.navigate(Screen.RoundingNumScreen.route) },
                    navigateToDefaultSplitScreen = { navController.navigate(Screen.DefaultSplitScreen.route) },
                    onBackButtonPressed = { navController.navigateUp() }
                )
                Log.d("NAV", "To Settings Screen")
            }
            composable(route = Screen.DefaultTipScreen.route) {
                PickerScreen(
                    initialValue = settingsViewModel.defaultTipPercentage.intValue,
                    optionsList = OptionsLists.TIP_PERCENT_OPTIONS,
                    onSubmitPressed = {
                        navController.navigateUp()
                        settingsViewModel.setDefaultTipPercentage(it)
                    }
                )
                Log.d("NAV", "To Default Tip Picker Screen")
            }
            composable(route = Screen.DefaultSplitScreen.route) {
                PickerScreen(
                    initialValue = settingsViewModel.defaultNumSplit.intValue,
                    optionsList = OptionsLists.NUM_SPLIT_OPTIONS,
                    onSubmitPressed = {
                        navController.navigateUp()
                        settingsViewModel.setDefaultNumSplit(it)
                    }
                )
                Log.d("NAV", "To Default Split Picker Screen")
            }
            composable(route = Screen.RoundingNumScreen.route) {
                PickerScreen(
                    initialValue = settingsViewModel.roundingNum.intValue,
                    optionsList = OptionsLists.ROUNDING_INCREMENTS,
                    onSubmitPressed = {
                        navController.navigateUp()
                        settingsViewModel.setRoundingNum(it)
                        tipCalcViewModel.roundingNum.intValue = it
                    }
                )
                Log.d("NAV", "To Rounding Num Picker Screen")
            }
        }
    }
}