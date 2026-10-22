package com.camerongineer.tipcalculatorwear.utils

import android.util.Log
import com.camerongineer.tipcalculatorwear.presentation.constants.TipLanguage
import java.util.Locale

fun getLocale(languageCode: String): Locale {
    return try {
        val tipLanguage = getTipLanguage(languageCode)
        val builder = Locale.Builder().setLanguage(tipLanguage.name)
        val country = Locale.getDefault().country
        if (country.isNotEmpty()) builder.setRegion(country)
        builder.build()
    } catch (e: Exception) {
        Log.d("Language", "Language not set")
        Locale.getDefault()
    }
}

fun getTipLanguage(languageCode: String): TipLanguage {
    return try {
        TipLanguage.valueOf(languageCode.uppercase())
    } catch (e: Exception) {
        null
    }
        ?: try {
            TipLanguage.valueOf(Locale.getDefault().language.uppercase())
        } catch (e: Exception) {
            TipLanguage.EN
        }
}