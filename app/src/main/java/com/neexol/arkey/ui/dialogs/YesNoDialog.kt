package com.neexol.arkey.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.neexol.arkey.R
import java.io.Serializable

class YesNoDialog: DialogFragment() {

    companion object {
        const val YES_NO_REQUEST_KEY ="YES_NO_REQUEST"
        const val YES_NO_KEY = "YES_NO"
        private const val TITLE_KEY = "TITLE"

        fun newInstance(title: String): YesNoDialog {
            return YesNoDialog().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, title)
                }
            }
        }
    }

    private val title by lazy { requireArguments().getString(TITLE_KEY) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setNegativeButton(R.string.no) { _, _ ->
                setFragmentResult(YES_NO_REQUEST_KEY, bundleOf(YES_NO_KEY to false))
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                setFragmentResult(YES_NO_REQUEST_KEY, bundleOf(YES_NO_KEY to true))
            }
            .create()
    }
}