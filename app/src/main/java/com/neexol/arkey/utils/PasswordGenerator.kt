package com.neexol.arkey.utils

class PasswordGenerator private constructor(
    private val length: Int,
    private val isUppercaseEnabled: Boolean,
    private val isLowercaseEnabled: Boolean,
    private val isDigitsEnabled: Boolean,
    private val isSpecialEnabled: Boolean
){
    data class Builder(
        private var length: Int = 16,
        private var isUppercaseEnabled: Boolean = true,
        private var isLowercaseEnabled: Boolean = true,
        private var isDigitsEnabled: Boolean = true,
        private var isSpecialEnabled: Boolean = true
    ) {
        fun length(length: Int) = apply { this.length = length }
        fun isUppercaseEnabled(isUppercaseEnabled: Boolean) = apply { this.isUppercaseEnabled = isUppercaseEnabled }
        fun isLowercaseEnabled(isLowercaseEnabled: Boolean) = apply { this.isLowercaseEnabled = isLowercaseEnabled }
        fun isDigitsEnabled(isDigitsEnabled: Boolean) = apply { this.isDigitsEnabled = isDigitsEnabled }
        fun isSpecialEnabled(isSpecialEnabled: Boolean) = apply { this.isSpecialEnabled = isSpecialEnabled }
        fun build() = PasswordGenerator(length, isUppercaseEnabled, isLowercaseEnabled, isDigitsEnabled, isSpecialEnabled)
    }

    private companion object {
        val uppercaseSet = ('A'..'Z').toSet()
        val lowercaseSet = ('a'..'z').toSet()
        val digitsSet = ('0'..'9').toSet()
        val specialSet = ("~!@#\$%^&*/+-=?_.,:;(){}[]<>\"\\").toSet()
    }

    private val symbolsPoolSets = mutableSetOf<Set<Char>>().apply {
        if (isUppercaseEnabled) add(uppercaseSet)
        if (isLowercaseEnabled) add(lowercaseSet)
        if (isDigitsEnabled) add(digitsSet)
        if (isSpecialEnabled) add(specialSet)
    }.toSet()

    fun generate(): String {
        val minSymbolsPerGroup = length / symbolsPoolSets.size

        val notShuffledPassword = mutableListOf<Char>()
        symbolsPoolSets.forEach { symbolsPool ->
            repeat(minSymbolsPerGroup) {
                notShuffledPassword.add(symbolsPool.random())
            }
        }
        while (notShuffledPassword.size < length) {
            notShuffledPassword.add(symbolsPoolSets.random().random())
        }

        val resultPassword = StringBuilder()
        repeat(length) {
            val nextSymbol = notShuffledPassword.random()
            notShuffledPassword.remove(nextSymbol)
            resultPassword.append(nextSymbol)
        }

        return resultPassword.toString()
    }
}