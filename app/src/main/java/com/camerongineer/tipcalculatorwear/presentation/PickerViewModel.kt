package com.camerongineer.tipcalculatorwear.presentation

import androidx.lifecycle.ViewModel
import com.camerongineer.tipcalculatorwear.presentation.constants.OptionsItem

class PickerViewModel<T>(
    initialValue: T,
    val optionsList: List<OptionsItem<T>>
): ViewModel() {
    val initialIndex = optionsList.indexOfFirst { it.value == initialValue }
}