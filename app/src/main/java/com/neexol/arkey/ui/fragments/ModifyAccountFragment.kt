package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.neexol.arkey.R
import com.neexol.arkey.databinding.FragmentModifyAccountBinding
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.db.entities.Category
import com.neexol.arkey.ui.dialogs.YesNoDialog
import com.neexol.arkey.ui.dialogs.YesNoDialog.Companion.RESULT_YES_NO_KEY
import com.neexol.arkey.utils.ALL_CATEGORIES_ID
import com.neexol.arkey.utils.WITHOUT_CATEGORY_ID
import com.neexol.arkey.utils.mainActivity
import com.neexol.arkey.utils.setOnItemSelectedListener
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
    object CreateAccount: ModifyAccountType()
    data class EditAccount(val account: Account): ModifyAccountType()

class ModifyAccountFragment: Fragment() {

    companion object {
        const val MODIFY_ACCOUNT_TYPE_KEY = "MODIFY_ACCOUNT_TYPE"
        private const val DELETE_ACCOUNT_REQUEST_KEY = "DELETE_ACCOUNT_REQUEST"
    }

    private val modifyAccountType by lazy {
        (arguments?.getSerializable(MODIFY_ACCOUNT_TYPE_KEY) as? EditAccount) ?: CreateAccount
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
            CreateAccount -> {
                toolbar.title = getString(R.string.account_creating)
                saveBtn.setOnClickListener { createAccount() }
            }
            is EditAccount -> {
                toolbar.title = getString(R.string.editing_account)
                viewModel.fillAccountData((modifyAccountType as EditAccount).account)
                saveBtn.setOnClickListener { editAccount() }
            }
        }

        setListeners()
        setObservers()
    }

    private fun initCategorySpinner(categoriesList: List<Category>) {
        viewLifecycleOwner.lifecycleScope.launch {
            val adapter = withContext(Dispatchers.IO) {
                val categoryNamesList = mutableListOf<String>()
                viewModel.categoryIdsList.clear()

                categoryNamesList.add(getString(R.string.without_category))
                viewModel.categoryIdsList.add(WITHOUT_CATEGORY_ID)
                categoriesList.forEach { category ->
                    categoryNamesList.add(category.name)
                    viewModel.categoryIdsList.add(category.id!!)
                }

                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categoryNamesList
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            }
            categorySpinner.setOnItemSelectedListener { viewModel.selectCategory(it) }
            categorySpinner.adapter = adapter

            (modifyAccountType as? CreateAccount)?.let {
                val selectedCategoryId = mainViewModel.selectedCategoryId.value
                if (selectedCategoryId != ALL_CATEGORIES_ID) {
                    categorySpinner.setSelection(viewModel.categoryIdsList.indexOf(selectedCategoryId))
                }
            } ?:run {
                (modifyAccountType as? EditAccount)?.account?.categoryId?.let {
                    categorySpinner.setSelection(viewModel.categoryIdsList.indexOf(it))
                }
            }
        }
    }

    private fun setListeners() {
        deleteBtn.setOnClickListener { showDeleteConfirmationDialog() }

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
        mainViewModel.allCategories.observe(viewLifecycleOwner, Observer {
            initCategorySpinner(it)
        })
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
        requireActivity().onBackPressed()
    }

    private fun editAccount() {
        viewModel.editAccount()
        mainViewModel.selectCategory(viewModel.categoryId)
        requireActivity().onBackPressed()
    }

    private fun deleteAccount() {
        viewModel.deleteAccount()
        requireActivity().onBackPressed()
    }
}