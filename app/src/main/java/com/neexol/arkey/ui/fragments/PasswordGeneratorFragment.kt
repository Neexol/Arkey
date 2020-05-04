package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.neexol.arkey.R
import com.neexol.arkey.databinding.FragmentPasswordGeneratorBinding
import com.neexol.arkey.utils.copyToClipboard
import com.neexol.arkey.utils.mainActivity
import com.neexol.arkey.utils.setNavigationResult
import com.neexol.arkey.viewmodels.PasswordGeneratorViewModel
import kotlinx.android.synthetic.main.fragment_password_generator.*
import kotlinx.android.synthetic.main.view_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PasswordGeneratorFragment: Fragment() {

    companion object {
        const val IS_NEED_TO_SHOW_USE_BUTTON_KEY = "IS_NEED_TO_SHOW_USE_BUTTON"
        const val PASSWORD_RESULT_KEY = "PASSWORD_RESULT"
    }

    val viewModel: PasswordGeneratorViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentPasswordGeneratorBinding>(
            inflater,
            R.layout.fragment_password_generator,
            container,
            false
        )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.pass_generator)
        mainActivity().enableNavigateButton(toolbar)

        arguments?.getBoolean(IS_NEED_TO_SHOW_USE_BUTTON_KEY)?.let {
            usePasswordPanel.visibility = View.VISIBLE
        }

        setListeners()
    }

    private val onSwitchChangeListener = CompoundButton.OnCheckedChangeListener { view, isChecked ->
        val viewModelSwitch = when(view.id) {
            R.id.uppercaseSwitch -> PasswordGeneratorViewModel::isUppercaseEnabled
            R.id.lowercaseSwitch -> PasswordGeneratorViewModel::isLowercaseEnabled
            R.id.digitsSwitch -> PasswordGeneratorViewModel::isDigitsEnabled
            else -> PasswordGeneratorViewModel::isSpecialEnabled
        }
        if (viewModelSwitch.get(viewModel) != isChecked) {
            viewModelSwitch.set(viewModel, isChecked)
            viewModel.generatePassword()
        }

        val switchList = listOf<SwitchMaterial>(uppercaseSwitch, lowercaseSwitch, digitsSwitch, specialSwitch)
        val enabledSwitchList = switchList.filter { it.isChecked }
        if (enabledSwitchList.size == 1) {
            enabledSwitchList[0].isEnabled = false
        } else {
            switchList.forEach { it.isEnabled = true }
        }
    }

    private fun setListeners() {
        copyBtn.setOnClickListener {
            requireContext().copyToClipboard(viewModel.password.get()!!)
            Snackbar.make(
                requireView(),
                getString(R.string.copied_clipboard),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        lengthSlider.setOnChangeListener { _, value ->
            if (viewModel.length != value.toInt()) {
                viewModel.length = value.toInt()
                viewModel.generatePassword()
            }
            sliderValueLabel.text = value.toInt().toString()
        }

        usePasswordBtn.setOnClickListener {
            setNavigationResult(PASSWORD_RESULT_KEY, viewModel.password.get()!!)
            findNavController().popBackStack()
        }

        uppercaseSwitch.setOnCheckedChangeListener(onSwitchChangeListener)
        lowercaseSwitch.setOnCheckedChangeListener(onSwitchChangeListener)
        digitsSwitch.setOnCheckedChangeListener(onSwitchChangeListener)
        specialSwitch.setOnCheckedChangeListener(onSwitchChangeListener)
    }
}