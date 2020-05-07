package com.neexol.arkey.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.neexol.arkey.ui.activities.MasterPasswordActivity

class ChangeMasterPassContract: ActivityResultContract<Boolean, Boolean>() {
    companion object {
        const val CHANGE_MASTER_REQUEST_KEY = "CHANGE_MASTER_REQUEST"
    }

    override fun createIntent(context: Context, input: Boolean) =
        Intent(context, MasterPasswordActivity::class.java).apply {
            putExtra(CHANGE_MASTER_REQUEST_KEY, input)
        }

    override fun parseResult(resultCode: Int, intent: Intent?) = when(resultCode) {
        Activity.RESULT_OK -> true
        else -> false
    }
}