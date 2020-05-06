package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.neexol.arkey.R
import com.neexol.arkey.databinding.FragmentModifyAccountBinding
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.ui.dialogs.InputTextDialog
import com.neexol.arkey.ui.dialogs.YesNoDialog
import com.neexol.arkey.ui.dialogs.YesNoDialog.Companion.RESULT_YES_NO_KEY
import com.neexol.arkey.ui.fragments.PasswordGeneratorFragment.Companion.IS_NEED_TO_SHOW_USE_BUTTON_KEY
import com.neexol.arkey.ui.fragments.PasswordGeneratorFragment.Companion.PASSWORD_RESULT_KEY
import com.neexol.arkey.utils.*
import com.neexol.arkey.viewmodels.ModifyAccountViewModel
import com.neexol.arkey.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_modify_account.*
import kotlinx.android.synthetic.main.view_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

sealed class ModifyAccountType: Serializable
    data class CreateAccount(val selectedCategoryId: Int): ModifyAccountType()
    data class EditAccount(val account: Account): ModifyAccountType()

class ModifyAccountFragment: Fragment() {

    companion object {
        const val MODIFY_ACCOUNT_TYPE_KEY = "MODIFY_ACCOUNT_TYPE"
        private const val CREATE_CATEGORY_REQUEST_KEY = "CREATE_CATEGORY_REQUEST"
        private const val DELETE_ACCOUNT_REQUEST_KEY = "DELETE_ACCOUNT_REQUEST"
    }

    private val modifyAccountType by lazy {
        requireArguments().getSerializable(MODIFY_ACCOUNT_TYPE_KEY) as ModifyAccountType
    }

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val viewModel: ModifyAccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentModifyAccountBinding>(
            inflater,
            R.layout.fragment_modify_account,
            container,
            false
        )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity().enableNavigateButton(toolbar)

        when(modifyAccountType) {
            is CreateAccount -> {
                toolbar.title = getString(R.string.account_creating)
                saveBtn.setOnClickListener { createAccount() }
            }
            is EditAccount -> {
                toolbar.title = getString(R.string.editing_account)
                viewModel.accountId
                    ?: viewModel.fillAccountData((modifyAccountType as EditAccount).account)
                saveBtn.setOnClickListener { editAccount() }
            }
        }

        setListeners()
        setObservers()
    }

    private fun initCategorySpinner(categoriesList: List<ModifyAccountViewModel.TitleWithId>) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoriesList.map { it.title ?: getString(R.string.without_category) }
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            val categoryIdForSelection =
                if (viewModel.categoryId == Categories.WITHOUT_CATEGORY.id) {
                    when(modifyAccountType) {
                        is CreateAccount -> {
                            val selectedCategoryId = (modifyAccountType as CreateAccount).selectedCategoryId
                            if (selectedCategoryId == Categories.ALL_CATEGORIES.id) {
                                Categories.WITHOUT_CATEGORY.id
                            } else {
                                selectedCategoryId
                            }
                        }
                        is EditAccount -> (modifyAccountType as EditAccount).account.categoryId
                    }
                } else {
                    viewModel.categoryId
                }

            withContext(Dispatchers.Main) {
                categorySpinner.adapter = adapter
                categorySpinner.setOnItemSelectedListener { spinnerIndex ->
                    if (spinnerIndex == categorySpinner.adapter.count - 1) {
                        showCategoryCreating()
                        categorySpinner.setSelection(
                            categoriesList.indexOfFirst { it.id == viewModel.categoryId }
                        )
                    } else {
                        viewModel.selectCategory(spinnerIndex)
                    }
                }
                categorySpinner.setSelection(
                    categoriesList.indexOfFirst { it.id == categoryIdForSelection }
                )
            }
        }
    }

    private fun setListeners() {
        deleteBtn.setOnClickListener { showDeleteConfirmationDialog() }

        passwordInput.setEndIconOnClickListener { navigateToPassGenerator() }

        childFragmentManager.setFragmentResultListener(
            CREATE_CATEGORY_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getString(InputTextDialog.RESULT_INPUT_TEXT_KEY)?.trim()
            if (!result.isNullOrBlank()) {
                viewModel.createCategory(result)
            }
        }

        childFragmentManager.setFragmentResultListener(
            DELETE_ACCOUNT_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean(RESULT_YES_NO_KEY)) {
                deleteAccount()
            }
        }
    }

    private fun setObservers() {
        viewModel.displayCategoriesLiveData.observe(viewLifecycleOwner, Observer {
            initCategorySpinner(it)
        })

        getNavigationResult<String>(PASSWORD_RESULT_KEY)?.observe(viewLifecycleOwner, Observer {
            viewModel.password = it
        })
    }

    private fun showCategoryCreating() {
        InputTextDialog.newInstance(
            CREATE_CATEGORY_REQUEST_KEY,
            getString(R.string.create_category),
            getString(R.string.category_name_hint)
        ).show(childFragmentManager, null)
    }

    private fun showDeleteConfirmationDialog() {
        YesNoDialog.newInstance(
            DELETE_ACCOUNT_REQUEST_KEY,
            getString(R.string.delete_account_confirmation)
        ).show(childFragmentManager, null)
    }

    private fun createAccount() {
        viewModel.createAccount()
        mainViewModel.selectCategory(viewModel.categoryId)
        findNavController().popBackStack()
        requireActivity().hideSoftInput(requireView().windowToken)
    }

    private fun editAccount() {
        viewModel.editAccount()
        mainViewModel.selectCategory(viewModel.categoryId)
        findNavController().popBackStack()
        requireActivity().hideSoftInput(requireView().windowToken)
    }

    private fun deleteAccount() {
        viewModel.deleteAccount()
        findNavController().popBackStack()
        requireActivity().hideSoftInput(requireView().windowToken)
    }

    private fun navigateToPassGenerator() {
        findNavController().navigate(
            R.id.action_modifyAccountFragment_to_passwordGeneratorFragment,
            bundleOf(IS_NEED_TO_SHOW_USE_BUTTON_KEY to true)
        )
    }
}