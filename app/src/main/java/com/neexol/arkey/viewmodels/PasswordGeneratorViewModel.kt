package com.neexol.arkey.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.neexol.arkey.generators.PasswordGenerator

class PasswordGeneratorViewModel(
    private val passwordGeneratorBuilder: PasswordGenerator.Builder
): ViewModel() {
    val password = ObservableField("")

    var length: Int = 16
    var isUppercaseEnabled = true
    var isLowercaseEnabled = true
    var isDigitsEnabled = true
    var isSpecialEnabled = true

    init { generatePassword() }

    fun generatePassword() {
        password.set(
            passwordGeneratorBuilder
                .length(length)
                .isUppercaseEnabled(isUppercaseEnabled)
                .isLowercaseEnabled(isLowercaseEnabled)
                .isDigitsEnabled(isDigitsEnabled)
                .isSpecialEnabled(isSpecialEnabled)
                .build()
                .generate()
        )
    }
}