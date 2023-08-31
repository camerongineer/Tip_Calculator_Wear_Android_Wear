package com.camerongineer.tipcalculatorwear.presentation.theme


import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.camerongineer.tipcalculatorwear.data.preferences.DataStoreManager
import com.camerongineer.tipcalculatorwear.presentation.KeyboardItem
import com.camerongineer.tipcalculatorwear.presentation.SettingsScreen
import com.camerongineer.tipcalculatorwear.presentation.SettingsViewModel
import com.camerongineer.tipcalculatorwear.presentation.SplitCalcScreen
import com.camerongineer.tipcalculatorwear.presentation.SplitViewModel
import com.camerongineer.tipcalculatorwear.presentation.TipCalcViewModel
import com.camerongineer.tipcalculatorwear.presentation.TipSelectionItem


enum class Theme(val colors: Colors, val description: String) {
    Dark(darkColorPalette, "Dark Theme"),
    Light(lightColorPalette, "Light Theme");
}

@Composable
fun TipCalculatorWearTheme(
    themeName: String = Theme.Dark.name,
    content: @Composable () -> Unit
) {
    val theme = Theme.valueOf(themeName)
    MaterialTheme(
        colors = theme.colors,
        typography = Typography,
        content = content
    )
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun ThemeKeyboardPreview(theme: Theme = Theme.Dark) {
    TipCalculatorWearTheme(theme.name) {
        Scaffold(
            timeText = {
                TimeText(
                    timeTextStyle = TimeTextDefaults
                        .timeTextStyle(color = MaterialTheme.colors.onSurfaceVariant))
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
fun ThemeTipSelectionPreview(theme: Theme = Theme.Dark) {
    TipCalculatorWearTheme(theme.name) {
        Scaffold(
            timeText = {
                TimeText(
                    timeTextStyle = TimeTextDefaults
                        .timeTextStyle(color = MaterialTheme.colors.onSurfaceVariant))
            },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            TipSelectionItem(TipCalcViewModel(DataStoreManager(LocalContext.current)), {}, {}, {}) }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun ThemeSplitCalcPreview(theme: Theme = Theme.Dark) {
    TipCalculatorWearTheme(theme.name) {
        Scaffold(
            timeText = {
                TimeText(
                    timeTextStyle = TimeTextDefaults
                        .timeTextStyle(color = MaterialTheme.colors.onSurfaceVariant))
            },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            SplitCalcScreen(SplitViewModel(DataStoreManager(LocalContext.current), 3001, 251), {})
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun ThemeSettingsPreview(theme: Theme = Theme.Dark) {
    TipCalculatorWearTheme(theme.name) {
        Scaffold(
            timeText = {
                TimeText(
                    timeTextStyle = TimeTextDefaults
                        .timeTextStyle(color = MaterialTheme.colors.onSurfaceVariant))
             },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            SettingsScreen(SettingsViewModel(DataStoreManager(LocalContext.current)), {}, {}, {}, {})
        }
    }
}