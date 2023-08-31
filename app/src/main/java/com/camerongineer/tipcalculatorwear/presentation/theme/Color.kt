package com.camerongineer.tipcalculatorwear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Colors

val Blue10 = Color(0xFF031c30)
val Blue20 = Color(0xFF053861)
val Blue30 = Color(0xFF085391)
val Blue40 = Color(0xFF0a6fc2)
val Blue80 = Color(0xFF9ed1fa)
val Blue90 = Color(0xFFcfe8fc)


val Purple10 = Color(0xFF160e25)
val Purple20 = Color(0xFF2c1c4a)
val Purple30 = Color(0xFF422970)
val Purple40 = Color(0xFF583795)
val Purple80 = Color(0xFFc5b5e3)
val Purple90 = Color(0xFFe2daf1)


val Teal10 = Color(0xFF01322d)
val Teal20 = Color(0xFF02645b)
val Teal30 = Color(0xFF029788)
val Teal40 = Color(0xFF03c9b5)
val Teal80 = Color(0xFF9bfdf4)
val Teal90 = Color(0xFFcdfef9)


val Red10 = Color(0xFF330010)
val Red20 = Color(0xFF660020)
val Red30 = Color(0xFF990030)
val Red40 = Color(0xFFcc0041)
val Red70 = Color(0xFFff6696)
val Red80 = Color(0xFFff99b9)
val Red90 = Color(0xFFffccdc)


val Grey10 = Color(0xFF19191a)
val Grey20 = Color(0xFF313235)
val Grey30 = Color(0xFF4a4c4f)
val Grey40 = Color(0xFF636569)
val Grey70 = Color(0xFFb0b2b5)
val Grey80 = Color(0xFFcacbce)
val Grey90 = Color(0xFFe5e5e6)


internal val darkColorPalette: Colors = Colors(
    primary = Blue80,
    primaryVariant = Purple80,
    secondary = Purple40,
    secondaryVariant = Teal40,
    background = Color.Black,
    surface = Grey20,
    error = Red40,
    onPrimary = Blue10,
    onSecondary = Grey90,
    onBackground = Color.White,
    onSurface = Grey80,
    onSurfaceVariant = Purple80,
    onError = Color.Black
)

internal val lightColorPalette: Colors = Colors(
    primary = Blue40,
    primaryVariant = Purple20,
    secondary = Purple80,
    secondaryVariant = Teal20,
    background = Grey90,
    surface = Grey70,
    error = Red70,
    onPrimary = Blue90,
    onSecondary = Grey10,
    onBackground = Grey10,
    onSurface = Grey20,
    onSurfaceVariant = Purple20,
    onError = Color.White
)


@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun ColorKeyboardPreview(theme: Theme = Theme.Dark) {
    ThemeKeyboardPreview(theme)
}


@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun ColorTipSelectionPreview(theme: Theme = Theme.Dark) {
    ThemeTipSelectionPreview(theme)
}

@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun ColorSplitCalcPreview(theme: Theme = Theme.Dark) {
    ThemeSplitCalcPreview(theme)
}

@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun ColorSettingsPreview(theme: Theme = Theme.Dark) {
    ThemeSettingsPreview(theme)
}