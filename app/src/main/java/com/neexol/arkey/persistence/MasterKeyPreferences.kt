package com.neexol.arkey.persistence

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class MasterKeyPreferences(context: Context) {

    companion object {
        private const val MASTER_KEY_HASH_KEY = "MASTER_KEY_HASH"
    }

    private val preferences = EncryptedSharedPreferences.create(
        "MasterKeyPrefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun storeMasterKeyHash(masterKeyHash: String) {
        preferences.edit().putString(MASTER_KEY_HASH_KEY, masterKeyHash).apply()
    }

    fun isCorrectMasterKeyHash(masterKeyHash: String): Boolean {
        val storedMasterKeyHash = preferences.getString(MASTER_KEY_HASH_KEY, null)
        return storedMasterKeyHash == masterKeyHash
    }
}