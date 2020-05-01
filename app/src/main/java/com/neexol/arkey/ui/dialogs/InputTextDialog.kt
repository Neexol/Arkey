package com.neexol.arkey.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.neexol.arkey.R
import kotlinx.android.synthetic.main.view_input_text.view.*


class InputTextDialog: DialogFragment() {

    companion object {
        const val RESULT_INPUT_TEXT_KEY = "INPUT_TEXT"
        private const val INPUT_TEXT_REQUEST_KEY = "INPUT_TEXT_REQUEST"
        private const val TITLE_KEY = "TITLE"
        private const val INIT_TEXT_KEY = "INIT_TEXT"
        private const val HINT_KEY = "HINT"

        fun newInstance(
            requestKey: String,
            title: String,
            hintText: String = "",
            initText: String = ""
        ): InputTextDialog {
            return InputTextDialog().apply {
                arguments = bundleOf(
                    INPUT_TEXT_REQUEST_KEY to requestKey,
                    TITLE_KEY to title,
                    HINT_KEY to hintText,
                    INIT_TEXT_KEY to initText
                )
            }
        }
    }

    private val requestKey by lazy {
        requireArguments().getString(INPUT_TEXT_REQUEST_KEY)!!
    }
    private val title by lazy {
        requireArguments().getString(TITLE_KEY)!!
    }
    private val hintText by lazy {
        requireArguments().getString(HINT_KEY)
    }
    private val initText by lazy {
        requireArguments().getString(INIT_TEXT_KEY)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater
            .from(requireContext())
            .inflate(R.layout.view_input_text, null, false)

        view.editText.setText(initText)
        view.inputText.hint = hintText

        val dialog =  MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(view)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok) {_, _ ->
                val result = dialog?.findViewById<EditText>(R.id.editText)?.text.toString()
                setFragmentResult(
                    requestKey,
                    bundleOf(RESULT_INPUT_TEXT_KEY to result)
                )
            }
            .create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        view.editText.requestFocus()
        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        requireArguments().putString(
            INIT_TEXT_KEY,
            dialog?.findViewById<EditText>(R.id.editText)?.text.toString()
        )
    }
}