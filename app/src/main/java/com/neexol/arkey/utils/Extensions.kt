package com.neexol.arkey.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.neexol.arkey.R
import com.neexol.arkey.ui.MainActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout

fun Fragment.mainActivity() = this.requireActivity() as MainActivity

@BindingAdapter("handleErrorFrom")
fun TextInputLayout.handleError(error: String) {
    if (error.isNotEmpty()) {
        this.isErrorEnabled = true
        this.error = error
    } else {
        this.isErrorEnabled = false
    }
}

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

fun TextView.selectAsCategory() {
    this.setBackgroundResource(R.drawable.shape_category_selected)
    this.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
}

fun Context.copyToClipboard(text: String, label: String = "credentials") {
    val clipboard = this.getSystemService(ClipboardManager::class.java)
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun View.expand() {
    this.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val actualHeight: Int = this.measuredHeight
    this.layoutParams.height = 0
    this.visibility = View.VISIBLE
    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            this@expand.layoutParams.height =
                if (interpolatedTime == 1f) {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                }
                else {
                    (actualHeight * interpolatedTime).toInt()
                }
            this@expand.requestLayout()
        }
    }
    animation.duration = (actualHeight / this.context.resources.displayMetrics.density).toLong()
    this.startAnimation(animation)
}

fun View.collapse() {
    val actualHeight: Int = this.measuredHeight
    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1f) {
                this@collapse.visibility = View.GONE
            } else {
                this@collapse.layoutParams.height = actualHeight - (actualHeight * interpolatedTime).toInt()
                this@collapse.requestLayout()
            }
        }
    }
    animation.duration = (actualHeight / this.context.resources.displayMetrics.density).toLong()
    this.startAnimation(animation)
}

fun Activity.hideSoftInput(windowToken: IBinder) {
    getSystemService(InputMethodManager::class.java).hideSoftInputFromWindow(windowToken, 0)
}

fun <T> Fragment.getNavigationResult(key: String) =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.setNavigationResult(key: String, result: T) =
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)