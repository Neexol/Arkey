package com.neexol.arkey.viewmodels

import androidx.lifecycle.ViewModel
import com.neexol.arkey.utils.PasswordGenerator

class PasswordGeneratorViewModel(
    private val passwordGeneratorBuilder: PasswordGenerator.Builder
): ViewModel() {
    var password = ""
        private set

    var length = 16
    var isUppercaseEnabled = true
    var isLowercaseEnabled = true
    var isDigitsEnabled = true
    var isSpecialEnabled = true

    init { generatePassword() }

    fun generatePassword() {
        password = passwordGeneratorBuilder
            .length(length)
            .isUppercaseEnabled(isUppercaseEnabled)
            .isLowercaseEnabled(isLowercaseEnabled)
            .isDigitsEnabled(isDigitsEnabled)
            .isSpecialEnabled(isSpecialEnabled)
            .build()
            .generate()
    }
}