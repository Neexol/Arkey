package com.neexol.arkey.repositories

import com.neexol.arkey.generators.HashGenerator
import com.neexol.arkey.persistence.MasterKeyPreferences

class MasterPasswordRepository(
    private val hashGenerator: HashGenerator,
    private val masterKeyPrefs: MasterKeyPreferences
) {
    fun isMasterKeyValid(masterKey: String) =
        masterKeyPrefs.isCorrectMasterKeyHash(hashGenerator.generateHash(masterKey))

    fun storeNewMasterKey(masterKey: String) =
        masterKeyPrefs.storeMasterKeyHash(hashGenerator.generateHash(masterKey))
}