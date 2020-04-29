package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.neexol.arkey.R
import com.neexol.arkey.databinding.FragmentCreateEditAccountBinding
import com.neexol.arkey.db.entities.Category
import com.neexol.arkey.utils.WITHOUT_CATEGORY_ID
import com.neexol.arkey.utils.mainActivity
import com.neexol.arkey.utils.setOnItemSelectedListener
import com.neexol.arkey.viewmodels.CreateEditAccountViewModel
import com.neexol.arkey.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_create_edit_account.*
import kotlinx.android.synthetic.main.view_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateEditAccountFragment: Fragment() {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val viewModel: CreateEditAccountViewModel by viewModel()

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
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity().enableNavigateButton(toolbar)
        toolbar.title = getString(R.string.account_creating)

        saveBtn.setOnClickListener { createAccount() }

        mainViewModel.allCategories.observe(viewLifecycleOwner, Observer {
            initCategorySpinner(it)
        })
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
            categorySpinner.adapter = adapter
            categorySpinner.setOnItemSelectedListener { viewModel.selectCategory(it) }

            categoryTV.visibility = View.VISIBLE
        }
    }

    private fun createAccount() {
        viewModel.createAccount()
        requireActivity().onBackPressed()
    }
}