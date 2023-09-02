package com.camerongineer.tipcalculatorwear.presentation.constants

import com.camerongineer.tipcalculatorwear.R

enum class TipLanguage(val languageNameID: Int) {
    DE(R.string.german),
    EN(R.string.english),
    ES(R.string.spanish),
    FR(R.string.french),
    PT(R.string.portuguese);

    override fun toString(): String {
        return "$languageNameID"
    }

}