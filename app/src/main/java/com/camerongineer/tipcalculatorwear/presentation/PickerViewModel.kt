package com.camerongineer.tipcalculatorwear.presentation

import androidx.compose.runtime.MutableIntState
import androidx.lifecycle.ViewModel
import com.camerongineer.tipcalculatorwear.presentation.constants.OptionsItem

class PickerViewModel(
    val state: MutableIntState,
    val optionsList: List<OptionsItem>
): ViewModel()