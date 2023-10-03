package com.camerongineer.tipcalculatorwear.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Typography

val Typography = Typography(
    title1 = TextStyle(fontSize = 22.sp, textGeometricTransform = TextGeometricTransform(.95f)),
    title2 = TextStyle(fontSize = 16.sp, textGeometricTransform = TextGeometricTransform(.9f)),
    title3 = TextStyle(fontSize = 12.sp, textGeometricTransform = TextGeometricTransform(.95f)),
    button = TextStyle(fontSize = 16.sp, textGeometricTransform = TextGeometricTransform(.95f)),
    display1 = TextStyle(fontSize = 24.sp, textGeometricTransform = TextGeometricTransform(.95f)),
    display2 = TextStyle(fontSize = 18.sp, textGeometricTransform = TextGeometricTransform(.95f)),
    display3 = TextStyle(fontSize = 16.sp, textGeometricTransform = TextGeometricTransform(.95f)),
    caption1 = TextStyle(fontSize = 14.sp, textGeometricTransform = TextGeometricTransform(.9f)),
    caption2 = TextStyle(fontSize = 13.sp, textGeometricTransform = TextGeometricTransform(.8f)),
    caption3 = TextStyle(fontSize = 9.sp, textGeometricTransform = TextGeometricTransform(.9f))
)

val Typography.caption4: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 7.sp,
        textGeometricTransform = TextGeometricTransform(.9f)
    )