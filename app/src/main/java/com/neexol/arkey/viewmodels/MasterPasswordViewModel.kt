package com.neexol.arkey.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neexol.arkey.App
import com.neexol.arkey.R
import com.neexol.arkey.repositories.MasterPasswordRepository
import com.neexol.arkey.utils.ChangeMasterPassword
import com.neexol.arkey.utils.InputMasterPassword
import com.neexol.arkey.utils.MasterPasswordOperationType
import com.neexol.arkey.utils.NewMasterPassword

class MasterPasswordViewModel(
    app: Application,
    private val masterPasswordRepo: MasterPasswordRepository,
    val masterPasswordOperationType: MasterPasswordOperationType
): AndroidViewModel(app) {

    var masterPassword = ""
    var masterPasswordError = ObservableField("")

    var newMasterPassword = ""
    var newMasterPasswordError = ObservableField("")

    var confirmNewMasterPassword = ""
    var confirmNewMasterPasswordError = ObservableField("")

    private val _operationStatusLiveData = MutableLiveData(false)
    val operationStatusLiveData: LiveData<Boolean> = _operationStatusLiveData

    fun clearErrors() {
        masterPasswordError.set("")
        newMasterPasswordError.set("")
        confirmNewMasterPasswordError.set("")
    }

    fun checkAndExecuteOperation() {
        _operationStatusLiveData.value = when(masterPasswordOperationType) {
            InputMasterPassword -> {
                if (masterPasswordRepo.isMasterPasswordValid(masterPassword)) {
                    true
                } else {
                    masterPasswordError.set(
                        getApplication<App>().getString(R.string.current_master_password_error)
                    )
                    false
                }
            }
            NewMasterPassword -> {
                if (newMasterPassword == confirmNewMasterPassword) {
                    masterPasswordRepo.storeNewMasterPassword(newMasterPassword)
                    true
                } else {
                    newMasterPasswordError.set(
                        getApplication<App>().getString(R.string.master_password_equals_error)
                    )
                    confirmNewMasterPasswordError.set(
                        getApplication<App>().getString(R.string.master_password_equals_error)
                    )
                    false
                }
            }
            ChangeMasterPassword -> {
                if (newMasterPassword == confirmNewMasterPassword) {
                    if (masterPasswordRepo.isMasterPasswordValid(masterPassword)) {
                        masterPasswordRepo.storeNewMasterPassword(newMasterPassword)
                        true
                    } else {
                        masterPasswordError.set(
                            getApplication<App>().getString(R.string.current_master_password_error)
                        )
                        false
                    }
                } else {
                    newMasterPasswordError.set(
                        getApplication<App>().getString(R.string.master_password_equals_error)
                    )
                    confirmNewMasterPasswordError.set(
                        getApplication<App>().getString(R.string.master_password_equals_error)
                    )
                    false
                }
            }
        }
    }
}