package com.neexol.arkey.repositories

import com.neexol.arkey.generators.HashGenerator
import com.neexol.arkey.persistence.MasterPasswordPreferences

class MasterPasswordRepository(
    private val hashGenerator: HashGenerator,
    private val masterPasswordPrefs: MasterPasswordPreferences
) {
    fun isMasterPasswordExist() = masterPasswordPrefs.isMasterPasswordExist()

    fun isMasterPasswordValid(masterPassword: String) =
        masterPasswordPrefs.isCorrectMasterPasswordHash(hashGenerator.generateHash(masterPassword))

    fun storeNewMasterPassword(masterPassword: String) =
        masterPasswordPrefs.storeMasterPasswordHash(hashGenerator.generateHash(masterPassword))

    suspend fun deleteMasterPassword() = masterPasswordPrefs.deleteMasterPassword()
}