package com.camerongineer.tipcalculatorwear.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.camerongineer.tipcalculatorwear.presentation.theme.Theme
import com.camerongineer.tipcalculatorwear.presentation.theme.TipCalculatorWearTheme
import com.camerongineer.tipcalculatorwear.utils.getLocale
import com.camerongineer.tipcalculatorwear.utils.getTipLanguage
import java.util.Locale

@Composable
fun Navigation(navController: NavHostController) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val themeName by dataStoreManager.themeFlow.collectAsState(Theme.Dark.name)
    val languageCode = dataStoreManager.languageFlow.collectAsState("")

    if (languageCode.value.isNotEmpty()) {
        val config = LocalContext.current.resources.configuration
        val locale = getLocale(languageCode.value)
        Locale.setDefault(locale)
        config.setLocale(locale)
        LocalContext.current.resources.updateConfiguration(
            config,
            LocalContext.current.resources.displayMetrics
        )
    }

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        val tipCalcViewModel = TipCalcViewModel(dataStoreManager)
        val settingsViewModel = SettingsViewModel(dataStoreManager)
        composable(route = Screen.MainScreen.route) {
            TipCalculatorWearTheme(themeName) {
                TipCalcScreen(
                    tipCalcViewModel = tipCalcViewModel,
                    onSettingsButtonClicked = {
                        settingsViewModel.setIsFirstLaunchedTrue()
                        navController.navigate("settings") },
                    onSplitButtonClicked = { navController.navigate(Screen.SplitScreen.route) }
                )
            }
            Log.d("NAV", "To Main Screen")
        }
        composable(route = Screen.SplitScreen.route) {
            val splitViewModel = SplitViewModel(
                dataStore = dataStoreManager,
                subTotal = tipCalcViewModel.subTotal.intValue,
                tipAmount = tipCalcViewModel.tipAmount.intValue
            )
            TipCalculatorWearTheme(themeName) {
                SplitCalcScreen(
                    splitViewModel = splitViewModel,
                    onBackButtonPressed = navController::navigateUp
                )
            }
            Log.d("NAV", "To Split Screen")
        }
        navigation(
            startDestination = Screen.SettingsScreen.route,
            route = "settings"
        ) {
            composable(route = Screen.SettingsScreen.route) {
                TipCalculatorWearTheme(themeName) {
                    SettingsScreen(
                        settingsViewModel = settingsViewModel,
                        navigateToDefaultTipScreen = {
                            navController.navigate(Screen.DefaultTipScreen.route) },
                        navigateToRoundingNumScreen = {
                            navController.navigate(Screen.RoundingNumScreen.route) },
                        navigateToDefaultSplitScreen = {
                            navController.navigate(Screen.DefaultSplitScreen.route) },
                        navigateToLanguageSelectionScreen = {
                            navController.navigate(Screen.LanguageSelectionScreen.route) },
                        onBackButtonPressed = { navController.popBackStack(route = "settings", inclusive = true) }
                    )
                }
                Log.d("NAV", "To Settings Screen")
            }
            composable(route = Screen.DefaultTipScreen.route) {
                TipCalculatorWearTheme(themeName) {
                    PickerScreen(
                        initialValue = settingsViewModel.defaultTipPercentage.intValue,
                        optionsList = OptionsLists.TIP_PERCENT_OPTIONS,
                        onSubmitPressed = {
                            settingsViewModel.setDefaultTipPercentage(it)
                            navController.navigateUp()
                        }
                    )
                }
                Log.d("NAV", "To Default Tip Picker Screen")
            }
            composable(route = Screen.DefaultSplitScreen.route) {
                TipCalculatorWearTheme(themeName) {
                    PickerScreen(
                        initialValue = settingsViewModel.defaultNumSplit.intValue,
                        optionsList = OptionsLists.NUM_SPLIT_OPTIONS,
                        onSubmitPressed = {
                            settingsViewModel.setDefaultNumSplit(it)
                            navController.navigateUp()
                        }
                    )
                }
                Log.d("NAV", "To Default Split Picker Screen")
            }
            composable(route = Screen.RoundingNumScreen.route) {
                TipCalculatorWearTheme(themeName) {
                    PickerScreen(
                        initialValue = settingsViewModel.roundingNum.intValue,
                        optionsList = OptionsLists.ROUNDING_INCREMENTS,
                        onSubmitPressed = {
                            settingsViewModel.setRoundingNum(it)
                            tipCalcViewModel.roundingNum.intValue = it
                            navController.navigateUp()
                        }
                    )
                }
                Log.d("NAV", "To Rounding Num Picker Screen")
            }
            composable(route = Screen.LanguageSelectionScreen.route) {
                val tipLanguage = getTipLanguage(languageCode.value)
                TipCalculatorWearTheme(themeName) {
                    PickerScreen(
                        initialValue = tipLanguage,
                        optionsList = OptionsLists.LANGUAGE_OPTIONS,
                        isResourceString = true,
                        onSubmitPressed = {
                            settingsViewModel.saveLanguage(it)
                            navController.navigateUp()
                        }
                    )
                }
                Log.d("NAV", "To Language Selection Screen")
            }
        }
    }
}