package com.neexol.arkey.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.neexol.arkey.R
import com.neexol.arkey.ui.activities.MainActivity
import androidx.navigation.fragment.findNavController
import java.io.Serializable
import java.lang.Exception
import kotlin.properties.ReadWriteProperty

fun Fragment.mainActivity() = this.requireActivity() as MainActivity

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
    this.isVisible = true
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
                this@collapse.isVisible = false
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
    try {
        getSystemService(InputMethodManager::class.java)
            .hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: Exception) {}
}

fun <T> Fragment.getNavigationResult(key: String) =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.setNavigationResult(key: String, result: T) =
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)

fun Activity.blockScreenCapture() {
    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
}

fun <T> Bundle.put(key: String, value: T) {
    when (value) {
        is Boolean -> putBoolean(key, value)
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Short -> putShort(key, value)
        is Long -> putLong(key, value)
        is Byte -> putByte(key, value)
        is ByteArray -> putByteArray(key, value)
        is Char -> putChar(key, value)
        is CharArray -> putCharArray(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Float -> putFloat(key, value)
        is Bundle -> putBundle(key, value)
        is Parcelable -> putParcelable(key, value)
        is Serializable -> putSerializable(key, value)
        else -> throw IllegalStateException("Type of property $key is not supported")
    }
}

fun <T : Any> argument(): ReadWriteProperty<Fragment, T> = FragmentArgumentDelegate()