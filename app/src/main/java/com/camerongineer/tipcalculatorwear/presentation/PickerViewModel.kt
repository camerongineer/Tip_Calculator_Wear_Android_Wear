package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.MutableIntState
import androidx.lifecycle.ViewModel

class PickerViewModel(
    val state: MutableIntState,
    val optionsList: List<String>
): ViewModel()