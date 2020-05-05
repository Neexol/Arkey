package com.neexol.arkey.utils

import java.io.Serializable

sealed class MasterPasswordOperationType: Serializable
    object InputMasterPassword: MasterPasswordOperationType()
    object NewMasterPassword: MasterPasswordOperationType()
    object ChangeMasterPassword: MasterPasswordOperationType()