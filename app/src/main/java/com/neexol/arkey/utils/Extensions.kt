package com.neexol.arkey.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.neexol.arkey.ui.MainActivity

fun Fragment.mainActivity() = this.requireActivity() as MainActivity

@BindingAdapter("bindAfterTextChanged")
fun EditText.setAfterTextChangedListener(func: () -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            func.invoke()
        }
    })
}

fun Spinner.setOnItemSelectedListener(func: (spinnerIndex: Int) -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            func.invoke(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}