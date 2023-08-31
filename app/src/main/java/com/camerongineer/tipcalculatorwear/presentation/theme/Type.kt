package com.camerongineer.tipcalculatorwear.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Typography

val Typography = Typography(
    title1 = TextStyle(fontSize = 20.sp),
    title2 = TextStyle(fontSize = 14.sp),
    title3 = TextStyle(fontSize = 12.sp),
    button = TextStyle(fontSize = 14.sp),
    display1 = TextStyle(fontSize = 24.sp),
    display2 = TextStyle(fontSize = 18.sp),
    display3 = TextStyle(fontSize = 16.sp),
    caption1 = TextStyle(fontSize = 13.sp),
    caption2 = TextStyle(fontSize = 11.sp),
    caption3 = TextStyle(fontSize = 9.sp)
)

val Typography.caption4: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 7.sp,
    )