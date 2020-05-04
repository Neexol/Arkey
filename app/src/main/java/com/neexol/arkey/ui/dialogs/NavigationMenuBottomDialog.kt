package com.neexol.arkey.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neexol.arkey.R
import com.neexol.arkey.adapters.categories.CategoriesListAdapter
import com.neexol.arkey.utils.Categories
import com.neexol.arkey.utils.selectAsCategory
import com.neexol.arkey.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.dialog_bottom_nav_menu.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NavigationMenuBottomDialog: BottomSheetDialogFragment() {

    companion object {
        const val NAV_MENU_REQUEST_KEY = "NAV_MENU_REQUEST"
        const val NAV_MENU_KEY = "NAV_MENU"
    }

    private val viewModel: MainViewModel by sharedViewModel()

    private val categoriesListAdapter: CategoriesListAdapter = get()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_bottom_nav_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        highlightSelectedCategory()
        recyclerView.adapter = categoriesListAdapter
        setListeners()

        viewModel.allCategories.observe(viewLifecycleOwner, Observer {
            categoriesListAdapter.updateDataList(it.toList())
        })
    }

    private val onMenuItemClickListener = View.OnClickListener {
        when(it.id) {
            R.id.allAccountsTV -> sendResult(Categories.ALL_CATEGORIES.id)
            R.id.withoutCategoryTV -> sendResult(Categories.WITHOUT_CATEGORY.id)
            R.id.newCategoryTV -> sendResult(Categories.NEW_CATEGORY.id)
        }
    }

    private fun setListeners() {
        allAccountsTV.setOnClickListener(onMenuItemClickListener)
        withoutCategoryTV.setOnClickListener(onMenuItemClickListener)
        newCategoryTV.setOnClickListener(onMenuItemClickListener)

        categoriesListAdapter.setOnCategoryClickListener{ sendResult(it) }

        passGeneratorTV.setOnClickListener { navigateToPassGenerator() }
    }

    private fun highlightSelectedCategory() {
        when(val categoryId = viewModel.selectedCategoryId.value!!) {
            Categories.ALL_CATEGORIES.id -> allAccountsTV.selectAsCategory()
            Categories.WITHOUT_CATEGORY.id -> withoutCategoryTV.selectAsCategory()
            else -> categoriesListAdapter.highlightCategory(categoryId)
        }
    }

    private fun sendResult(resultId: Int) {
        setFragmentResult(NAV_MENU_REQUEST_KEY, bundleOf(NAV_MENU_KEY to resultId))
        dismiss()
    }

    private fun navigateToPassGenerator() {
        findNavController().navigate(R.id.action_accountsListFragment_to_passwordGeneratorFragment)
    }
}