package com.camerongineer.tipcalculatorwear.presentation.constants

import com.camerongineer.tipcalculatorwear.R

enum class TipLanguage(val languageNameID: Int) {
    DE(R.string.de),
    EN(R.string.en),
    ES(R.string.es),
    FR(R.string.fr),
    PT(R.string.pt);

    override fun toString(): String {
        return "$languageNameID"
    }

}