package com.camerongineer.tipcalculatorwear.utils

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun scaleFont(style: TextStyle, multiplier: Float): TextStyle {
    return style.copy(fontSize = style.fontSize * multiplier)
}

fun getFontMultiplier(screenSize: Dp, isLargeFont: Boolean): Float {
    return if (screenSize > 200.dp) {
        if (isLargeFont) 1.35f else 1.1f
    } else {
        if (isLargeFont) 1.15f else 1f
    }
}