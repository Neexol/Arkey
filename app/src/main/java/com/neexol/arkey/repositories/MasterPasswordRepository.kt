package com.neexol.arkey.repositories

import com.neexol.arkey.generators.HashGenerator
import com.neexol.arkey.persistence.MasterPasswordPreferences

class MasterPasswordRepository(
    private val hashGenerator: HashGenerator,
    private val masterPasswordPrefs: MasterPasswordPreferences
) {
    fun isMasterPasswordExist() = masterPasswordPrefs.getMasterPasswordHash() != null

    fun isMasterPasswordValid(masterPassword: String) =
        hashGenerator.generateHash(masterPassword) == masterPasswordPrefs.getMasterPasswordHash()

    fun storeNewMasterPassword(masterPassword: String) =
        masterPasswordPrefs.storeMasterPasswordHash(hashGenerator.generateHash(masterPassword))

    suspend fun deleteMasterPassword() = masterPasswordPrefs.deleteMasterPasswordHash()
}