package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.neexol.arkey.R
import com.neexol.arkey.databinding.FragmentCreateEditAccountBinding
import com.neexol.arkey.utils.mainActivity
import kotlinx.android.synthetic.main.view_toolbar.*

class CreateEditAccountFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCreateEditAccountBinding>(
            inflater,
            R.layout.fragment_create_edit_account,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity().enableNavigateButton(toolbar)
        toolbar.title = getString(R.string.account_creating)
    }
}