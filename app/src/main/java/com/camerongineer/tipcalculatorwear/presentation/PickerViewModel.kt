package com.camerongineer.tipcalculatorwear.presentation

import androidx.lifecycle.ViewModel
import com.camerongineer.tipcalculatorwear.presentation.constants.OptionsItem

class PickerViewModel(
    initialValue: Int,
    val optionsList: List<OptionsItem>
): ViewModel() {
    val initialIndex = optionsList.indexOfFirst { it.value == initialValue }
}