package com.neexol.arkey.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.neexol.arkey.R
import java.io.Serializable

class YesNoDialog: DialogFragment() {

    companion object {
        private const val LISTENER_KEY = "LISTENER"
        private const val TITLE_KEY = "TITLE"

        fun newInstance(title: String, onYesClick: OnYesClickListener): YesNoDialog {
            return YesNoDialog().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, title)
                    putSerializable(LISTENER_KEY, onYesClick)
                }
            }
        }
    }

    private val title by lazy {
        requireArguments().getString(TITLE_KEY)
    }
    private val listener by lazy {
        requireArguments().getSerializable(LISTENER_KEY) as OnYesClickListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                listener.onYesClick()
            }
            .create()
    }

    interface OnYesClickListener: Serializable {
        fun onYesClick()
    }
}