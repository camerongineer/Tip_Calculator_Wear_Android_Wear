package com.camerongineer.tipcalculatorwear.utils

fun roundUp(startingValue: Int, increment: Int): Int {
    return if (startingValue % increment == 0) {
        startingValue + increment
    } else {
        startingValue - (startingValue % increment) + increment
    }
}

fun roundDown(startingValue: Int, increment: Int): Int {
    return if (startingValue % increment == 0) {
        startingValue - increment
    } else {
        startingValue - (startingValue % increment)
    }
}