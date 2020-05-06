package com.neexol.arkey.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.neexol.arkey.R
import com.neexol.arkey.contracts.ChangeMasterPassContract.Companion.CHANGE_MASTER_REQUEST_KEY
import com.neexol.arkey.databinding.ActivityChangeMasterPasswordBinding
import com.neexol.arkey.databinding.ActivityInputMasterPasswordBinding
import com.neexol.arkey.databinding.ActivityNewMasterPasswordBinding
import com.neexol.arkey.repositories.MasterPasswordRepository
import com.neexol.arkey.ui.dialogs.YesNoDialog
import com.neexol.arkey.utils.*
import com.neexol.arkey.viewmodels.MasterPasswordViewModel
import kotlinx.android.synthetic.main.activity_input_master_password.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MasterPasswordActivity : AppCompatActivity() {

    companion object {
        private const val RESET_DATABASE_REQUEST_KEY = "RESET_DATABASE_REQUEST"
    }

    private val masterPasswordRepo: MasterPasswordRepository by inject()

    private val viewModel: MasterPasswordViewModel by viewModel {
        parametersOf(extractOperationType())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        blockScreenCapture()
        setContentView()
        paintStatusBar()
        observeOperationStatus()
    }

    private fun setContentView() {
        when(viewModel.masterPasswordOperationType) {
            InputMasterPassword -> {
                val binding = DataBindingUtil.setContentView<ActivityInputMasterPasswordBinding>(
                    this, R.layout.activity_input_master_password
                )
                binding.viewModel = viewModel
                initInputPasswordLayout(binding)
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
        return intent?.getSerializableExtra(CHANGE_MASTER_REQUEST_KEY)?.let {
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
                    InputMasterPassword -> startActivity(Intent(this, MainActivity::class.java))
                    NewMasterPassword -> startActivity(Intent(this, MainActivity::class.java))
                    ChangeMasterPassword -> setResult(Activity.RESULT_OK)
                }
                finish()
            }
        })
    }

    private fun initInputPasswordLayout(binding: ActivityInputMasterPasswordBinding) {
        with(binding.masterPasswordInput.editText!!) {
            requestFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            setOnEditorActionListener { _, actionId, event ->
                if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) ||
                    (actionId == EditorInfo.IME_ACTION_DONE)) {
                    confirmBtn.performClick()
                }
                false
            }
        }

        binding.forgotBtn.setOnClickListener {
            binding.forgotPanel.apply { isVisible = !isVisible }
        }

        binding.resetApplicationBtn.setOnClickListener {
            YesNoDialog.newInstance(
                RESET_DATABASE_REQUEST_KEY,
                getString(R.string.reset_db_confirmation)
            ).show(supportFragmentManager, null)
        }

        supportFragmentManager.setFragmentResultListener(
            RESET_DATABASE_REQUEST_KEY,
            this
        ) { _, bundle ->
            if (bundle.getBoolean(YesNoDialog.RESULT_YES_NO_KEY)) {
                resetApplication()
            }
        }
    }

    private fun resetApplication() {
        lifecycleScope.launch {
            masterPasswordRepo.deleteMasterPassword()
            deleteDatabase(getString(R.string.database_name))
            viewModelStore.clear()
            recreate()
        }
    }
}