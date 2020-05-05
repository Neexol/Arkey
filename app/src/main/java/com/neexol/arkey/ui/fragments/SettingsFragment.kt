package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.invoke
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.neexol.arkey.R
import com.neexol.arkey.contracts.ChangeMasterPassContract
import com.neexol.arkey.utils.mainActivity
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.view_toolbar.*

class SettingsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity().enableNavigateButton(toolbar)
        toolbar.title = getString(R.string.settings)

        setListeners()
    }

    private val changeMasterPassContractRegistration =
        registerForActivityResult(ChangeMasterPassContract()) {
            if (it) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.master_password_changed),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    private fun setListeners() {
        changeMasterBtn.setOnClickListener {
            changeMasterPassContractRegistration(true)
        }
    }
}