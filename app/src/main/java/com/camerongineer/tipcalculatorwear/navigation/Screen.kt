package com.camerongineer.tipcalculatorwear.navigation

sealed class Screen(val route: String) {

    object MainScreen : Screen("main_screen")
    object SplitScreen : Screen("split_screen")
    object SettingsScreen : Screen("settings_screen")
    object DefaultTipScreen : Screen("default_tip_screen")
    object DefaultSplitScreen : Screen("default_split_screen")
    object RoundingNumScreen : Screen("rounding_num_screen")


    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

}