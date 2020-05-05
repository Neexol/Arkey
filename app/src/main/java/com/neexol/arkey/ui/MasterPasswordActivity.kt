package com.neexol.arkey.ui

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.neexol.arkey.R
import com.neexol.arkey.databinding.ActivityChangeMasterPasswordBinding
import com.neexol.arkey.databinding.ActivityInputMasterPasswordBinding
import com.neexol.arkey.databinding.ActivityNewMasterPasswordBinding
import com.neexol.arkey.repositories.MasterPasswordRepository
import com.neexol.arkey.utils.ChangeMasterPassword
import com.neexol.arkey.utils.InputMasterPassword
import com.neexol.arkey.utils.MasterPasswordOperationType
import com.neexol.arkey.utils.NewMasterPassword
import com.neexol.arkey.viewmodels.MasterPasswordViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MasterPasswordActivity : AppCompatActivity() {

    companion object {
        const val MASTER_PASSWORD_OPERATION_TYPE_KEY = "MASTER_PASSWORD_OPERATION_TYPE"
    }

    private val masterPasswordRepo: MasterPasswordRepository by inject()

    private val viewModel: MasterPasswordViewModel by viewModel {
        parametersOf(extractOperationType())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        paintStatusBar()
        observeOperationStatus()
    }

    private fun setContentView() {
        when(viewModel.masterPasswordOperationType) {
            InputMasterPassword -> {
                DataBindingUtil.setContentView<ActivityInputMasterPasswordBinding>(
                    this, R.layout.activity_input_master_password
                ).viewModel = viewModel
            }
            NewMasterPassword -> {
                DataBindingUtil.setContentView<ActivityNewMasterPasswordBinding>(
                    this, R.layout.activity_new_master_password
                ).viewModel = viewModel
            }
            ChangeMasterPassword -> {
                DataBindingUtil.setContentView<ActivityChangeMasterPasswordBinding>(
                    this, R.layout.activity_change_master_password
                ).viewModel = viewModel
            }
        }
    }

    private fun paintStatusBar() {
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = android.R.attr.windowBackground
    }

    private fun extractOperationType(): MasterPasswordOperationType {
        return intent?.getSerializableExtra(MASTER_PASSWORD_OPERATION_TYPE_KEY)?.let {
            ChangeMasterPassword
        } ?:run {
            if (masterPasswordRepo.isMasterPasswordExist()) {
                InputMasterPassword
            } else {
                NewMasterPassword
            }
        }
    }

    private fun observeOperationStatus() {
        viewModel.operationStatusLiveData.observe(this, Observer {
            if (it) {
                when(viewModel.masterPasswordOperationType) {
                    InputMasterPassword -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    NewMasterPassword -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    ChangeMasterPassword -> {}
                }
                finish()
            }
        })
    }
}