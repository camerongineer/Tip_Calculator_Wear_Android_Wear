package com.camerongineer.tipcalculatorwear.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.KeyboardItem
import com.camerongineer.tipcalculatorwear.presentation.TipCalcViewModel
import com.camerongineer.tipcalculatorwear.presentation.TipSelectionItem


val md_theme_light_primary = Color(0xFF006494)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFCBE6FF)
val md_theme_light_onPrimaryContainer = Color(0xFF001E30)
val md_theme_light_secondary = Color(0xFF50606F)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFD3E4F6)
val md_theme_light_onSecondaryContainer = Color(0xFF0C1D29)
val md_theme_light_tertiary = Color(0xFF65587B)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFEBDCFF)
val md_theme_light_onTertiaryContainer = Color(0xFF211634)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF3E0021)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF3E0021)
val md_theme_light_surfaceVariant = Color(0xFFDDE3EA)
val md_theme_light_onSurfaceVariant = Color(0xFF41474D)
val md_theme_light_outline = Color(0xFF72787E)
val md_theme_light_inverseOnSurface = Color(0xFFFFECF0)
val md_theme_light_inverseSurface = Color(0xFF5D1137)
val md_theme_light_inversePrimary = Color(0xFF8FCDFF)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF006494)
val md_theme_light_outlineVariant = Color(0xFFC1C7CE)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFF8FCDFF)
val md_theme_dark_onPrimary = Color(0xFF00344F)
val md_theme_dark_primaryContainer = Color(0xFF004B71)
val md_theme_dark_onPrimaryContainer = Color(0xFFCBE6FF)
val md_theme_dark_secondary = Color(0xFFB8C8D9)
val md_theme_dark_onSecondary = Color(0xFF22323F)
val md_theme_dark_secondaryContainer = Color(0xFF394956)
val md_theme_dark_onSecondaryContainer = Color(0xFFD3E4F6)
val md_theme_dark_tertiary = Color(0xFFD0C0E8)
val md_theme_dark_onTertiary = Color(0xFF362B4A)
val md_theme_dark_tertiaryContainer = Color(0xFF4D4162)
val md_theme_dark_onTertiaryContainer = Color(0xFFEBDCFF)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF3E0021)
val md_theme_dark_onBackground = Color(0xFFFFD9E4)
val md_theme_dark_surface = Color(0xFF3E0021)
val md_theme_dark_onSurface = Color(0xFFFFD9E4)
val md_theme_dark_surfaceVariant = Color(0xFF41474D)
val md_theme_dark_onSurfaceVariant = Color(0xFFC1C7CE)
val md_theme_dark_outline = Color(0xFF8B9198)
val md_theme_dark_inverseOnSurface = Color(0xFF3E0021)
val md_theme_dark_inverseSurface = Color(0xFFFFD9E4)
val md_theme_dark_inversePrimary = Color(0xFF006494)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFF8FCDFF)
val md_theme_dark_outlineVariant = Color(0xFF41474D)
val md_theme_dark_scrim = Color(0xFF000000)


val seed = Color(0xFF90CAF9)


val Blue200 = Color(0xFF90CAF9)
val Purple200 = Color(0xFFB39DDB)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Red400 = Color(0xFFCF6679)
val LightBlue = Color(0xFFB3B9DD)

internal val originalColorPalette: Colors = Colors(
    primary = Blue200,
    primaryVariant = Purple200,
    secondary = Purple500,
    secondaryVariant = Teal200,
    error = Red400,
    onPrimary = Color.Black,
    onSecondary = LightBlue,
    onError = Color.Black,
    background = Color.Black,
    onBackground = Color.White
)

internal val lightColorPalette: Colors = Colors(
    primary = Blue200,
    primaryVariant = Purple200,
    secondary = Purple500,
    secondaryVariant = Teal200,
    error = Red400,
    onPrimary = Color.Black,
    onSecondary = LightBlue,
    onError = Color.Black,
    background = Color.White,
    onBackground = Color.White
)



@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun ColorKeyboardPreview() {
    OriginalTheme {
        Scaffold(
            timeText = {
                TimeText()
            },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            KeyboardItem(TipCalcViewModel(DataStoreManager(LocalContext.current)), {})
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun ColorTipSelectionPreview() {
    OriginalTheme {
        Scaffold(
            timeText = {
                TimeText()
             },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            TipSelectionItem(TipCalcViewModel(DataStoreManager(LocalContext.current)), {}, {}, {}) }
    }
}