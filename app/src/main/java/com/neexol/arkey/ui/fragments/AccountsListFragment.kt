package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.neexol.arkey.adapters.AccountsListAdapter
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.utils.mainActivity
import com.neexol.arkey.ui.dialogs.NavigationMenuBottomDialog
import com.neexol.arkey.utils.ALL_CATEGORIES_ID
import com.neexol.arkey.utils.WITHOUT_CATEGORY_ID
import com.neexol.arkey.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_accounts_list.*
import kotlinx.android.synthetic.main.view_toolbar.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AccountsListFragment: Fragment() {

    private val viewModel: MainViewModel by sharedViewModel()

    private val accountsListAdapter: AccountsListAdapter = get()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_accounts_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = getString(R.string.all_accounts)

        initRecyclerView()

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        bottomAppBar.setNavigationOnClickListener { showMenu() }

        addAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.action_accountsListFragment_to_createEditAccountFragment)
        }
    }

    private fun setObservers() {
        viewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            accountsListAdapter.updateDataList(it.toList())
        })

        viewModel.selectedCategoryId.observe(viewLifecycleOwner, Observer { categoryId ->
            toolbar.title = when(categoryId) {
                ALL_CATEGORIES_ID -> getString(R.string.all_categories)
                WITHOUT_CATEGORY_ID -> getString(R.string.without_category)
                else -> viewModel.allCategories.value?.find { it.id == categoryId }?.name
            }
            accountsListAdapter.selectCategory(categoryId)
        })
    }

    private fun initRecyclerView() {
        recyclerView.adapter = accountsListAdapter
    }

    private val navigationMenuCallback = object : NavigationMenuBottomDialog.OnCategoryListener {
        override fun onNewCategory(categoryName: String) {
            TODO("Not yet implemented")
        }
    }

    private fun showMenu() {
        val dialog = NavigationMenuBottomDialog.newInstance(navigationMenuCallback)
        dialog.show(childFragmentManager, null)
    }
}