package com.neexol.arkey.utils

import androidx.fragment.app.Fragment
import com.neexol.arkey.ui.MainActivity

fun Fragment.mainActivity() = this.requireActivity() as MainActivity