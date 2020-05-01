package com.neexol.arkey.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.neexol.arkey.R

class YesNoDialog: DialogFragment() {

    companion object {
        const val RESULT_YES_NO_KEY = "YES_NO"
        private const val YES_NO_REQUEST_KEY = "YES_NO_REQUEST"
        private const val TITLE_KEY = "TITLE"

        fun newInstance(requestKey: String, title: String): YesNoDialog {
            return YesNoDialog().apply {
                arguments = bundleOf(
                    YES_NO_REQUEST_KEY to requestKey,
                    TITLE_KEY to title
                )
            }
        }
    }

    private val requestKey by lazy {
        requireArguments().getString(YES_NO_REQUEST_KEY)!!
    }

    private val title by lazy {
        requireArguments().getString(TITLE_KEY)!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setNegativeButton(R.string.no) { _, _ ->
                setFragmentResult(
                    requestKey,
                    bundleOf(RESULT_YES_NO_KEY to false)
                )
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                setFragmentResult(requestKey,
                    bundleOf(RESULT_YES_NO_KEY to true)
                )
            }
            .create()
    }
}