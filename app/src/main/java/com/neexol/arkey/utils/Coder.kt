package com.neexol.arkey.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object Coder {

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val ANDROID_KEY_STORE = "ArkeyKeyStore"

    class Encoder {
        private val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)

        private fun getSecretKey(alias: String): SecretKey {
            keyGenerator.init(
                KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            return keyGenerator.generateKey()
        }

        fun encryptText(alias: String, textToEncrypt: String): Pair<ByteArray, ByteArray> {
            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias))
            return cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8"))) to cipher.iv
        }
    }

    class Decoder {
        private val keyStore by lazy {
            val ks = KeyStore.getInstance(ANDROID_KEY_STORE)
            ks.load(null)
            ks
        }

        private fun getSecretKey(alias: String): SecretKey {
            return (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
        }

        fun decryptData(alias: String, encryptedData: ByteArray, encryptionIv: ByteArray): String {
            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, encryptionIv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec)
            return String(cipher.doFinal(encryptedData), charset("UTF-8"))
        }
    }
}