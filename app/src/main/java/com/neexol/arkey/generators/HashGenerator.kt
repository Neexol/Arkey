package com.neexol.arkey.generators

import java.math.BigInteger
import java.security.MessageDigest

class HashGenerator {
    fun generateHash(input: String) = bin2hex(getHash(input))

    private fun getHash(input: String): ByteArray {
        return MessageDigest.getInstance("SHA-256").apply {
            reset()
            update(input.hashCode().toString().toByteArray())
        }.digest(input.toByteArray())
    }

    private fun bin2hex(data: ByteArray): String {
        return String.format("%0${data.size * 2}X", BigInteger(1, data))
    }
}