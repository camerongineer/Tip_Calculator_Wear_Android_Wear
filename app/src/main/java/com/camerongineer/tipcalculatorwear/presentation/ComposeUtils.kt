package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
inline fun withHaptics(
    isLongPress: Boolean = false,
    crossinline block: () -> Unit
): () -> Unit {
    val haptics = LocalHapticFeedback.current
    return {
        haptics.performHapticFeedback(if (isLongPress) {
            HapticFeedbackType.LongPress
        } else HapticFeedbackType.TextHandleMove
        )
        block()
    }
}

@Composable
inline fun withBooleanHaptics(
    isLongPress: Boolean = false,
    crossinline block: (Boolean) -> Unit
): (Boolean) -> Unit {
    val haptics = LocalHapticFeedback.current
    return {
        haptics.performHapticFeedback(if (isLongPress) {
            HapticFeedbackType.LongPress
        } else HapticFeedbackType.TextHandleMove
        )
        block(it)
    }
}

