package com.neexol.arkey.persistence

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MasterPasswordPreferences(context: Context) {

    companion object {
        private const val MASTER_PASSWORD_HASH_KEY = "MASTER_PASSWORD_HASH"
    }

    private val preferences = EncryptedSharedPreferences.create(
        "MasterPasswordPrefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getMasterPasswordHash() = preferences.getString(MASTER_PASSWORD_HASH_KEY, null)

    fun storeMasterPasswordHash(masterPasswordHash: String) {
        preferences.edit().putString(MASTER_PASSWORD_HASH_KEY, masterPasswordHash).apply()
    }

    suspend fun deleteMasterPasswordHash() = withContext(Dispatchers.IO) {
        preferences.edit().remove(MASTER_PASSWORD_HASH_KEY).commit()
    }
}