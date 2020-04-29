package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.neexol.arkey.R
import com.neexol.arkey.adapters.accounts.AccountsListAdapter
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.ui.dialogs.NavigationMenuBottomDialog
import com.neexol.arkey.ui.fragments.ModifyAccountFragment.Companion.MODIFY_ACCOUNT_TYPE_KEY
import com.neexol.arkey.utils.ALL_CATEGORIES_ID
import com.neexol.arkey.utils.DebouncingQueryTextListener
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
        initBottomAppBar()

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        bottomAppBar.setNavigationOnClickListener { showMenu() }
        bottomAppBar.setOnMenuItemClickListener {
            initAndShowSearchView()
            true
        }

        addAccountBtn.setOnClickListener {
            navigateModifyAccount()
        }
    }

    private fun setObservers() {
        viewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            accountsListAdapter.updateDataList(it.toList())
        })

        viewModel.selectedCategoryId.observe(viewLifecycleOwner, Observer { categoryId ->
            setToolbarTitle()
            accountsListAdapter.selectCategory(categoryId)
        })
    }

    private fun initRecyclerView() {
        recyclerView.adapter = accountsListAdapter.apply {
            setOnAccountClickListener(object : AccountsListAdapter.OnAccountsListClickListener {
                override fun onAccountClick(account: Account) {
                    val bundle = Bundle().apply {
                        putSerializable(MODIFY_ACCOUNT_TYPE_KEY, EditAccount(account))
                    }
                    navigateModifyAccount(bundle)
                }
            })
        }
    }

    private fun initBottomAppBar() {
        bottomAppBar.inflateMenu(R.menu.main_bottom)
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

    private fun initAndShowSearchView() {
        if (toolbar.menu.isEmpty()) {
            toolbar.inflateMenu(R.menu.main_top)
        }

        val menuItem = toolbar.menu.findItem(R.id.action_search_top)
        toolbar.title = ""
        val searchView = (menuItem.actionView as SearchView)
        searchView.maxWidth = Int.MAX_VALUE
        searchView.isIconified = false

        searchView.setOnCloseListener {
            val imm = requireContext().getSystemService(InputMethodManager::class.java)
            imm.hideSoftInputFromWindow(searchView.windowToken, 0)
            searchView.setQuery("", true)
            toolbar.menu.clear()
            setToolbarTitle()
            true
        }

        searchView.setOnQueryTextListener(
            DebouncingQueryTextListener(this.lifecycle) { newText ->
                newText?.let {
                    if (it.isEmpty()) {
                        accountsListAdapter.searchReset()
                    } else {
                        accountsListAdapter.searchQuery(it)
                    }
                }
            }
        )
    }

    private fun setToolbarTitle() {
        val categoryId = viewModel.selectedCategoryId.value
        toolbar.title = when(categoryId) {
            ALL_CATEGORIES_ID -> getString(R.string.all_categories)
            WITHOUT_CATEGORY_ID -> getString(R.string.without_category)
            else -> viewModel.allCategories.value?.find { it.id == categoryId }?.name
        }
    }

    private fun navigateModifyAccount(bundle: Bundle? = null) {
        findNavController().navigate(R.id.action_accountsListFragment_to_modifyAccountFragment, bundle)
    }
}