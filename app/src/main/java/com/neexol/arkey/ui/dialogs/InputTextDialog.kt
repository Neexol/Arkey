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
import com.neexol.arkey.utils.argument
import kotlinx.android.synthetic.main.view_input_text.view.*


class InputTextDialog: DialogFragment() {

    companion object {
        const val RESULT_INPUT_TEXT_KEY = "INPUT_TEXT"

        fun newInstance(
            requestKey: String,
            title: String,
            hintText: String = "",
            initText: String = ""
        ): InputTextDialog {
            return InputTextDialog().apply {
                this.requestKey = requestKey
                this.title = title
                this.hintText = hintText
                this.initText = initText
            }
        }
    }

    private var requestKey: String by argument()
    private var title: String by argument()
    private var hintText: String by argument()
    private var initText: String by argument()

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
        initText = dialog?.findViewById<EditText>(R.id.editText)?.text.toString()
    }
}