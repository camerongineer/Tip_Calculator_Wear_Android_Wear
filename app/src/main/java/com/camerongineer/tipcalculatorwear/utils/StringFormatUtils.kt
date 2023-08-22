package com.camerongineer.tipcalculatorwear.utils

fun getFormattedAmountString(amount: Int): String = "%.2f".format(amount.toDouble() / 100)