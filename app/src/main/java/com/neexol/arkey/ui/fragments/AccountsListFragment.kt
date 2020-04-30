package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.neexol.arkey.R
import com.neexol.arkey.adapters.accounts.AccountsListAdapter
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.ui.dialogs.NavigationMenuBottomDialog
import com.neexol.arkey.ui.dialogs.NavigationMenuBottomDialog.Companion.NAV_MENU_KEY
import com.neexol.arkey.ui.dialogs.NavigationMenuBottomDialog.Companion.NAV_MENU_REQUEST_KEY
import com.neexol.arkey.ui.fragments.ModifyAccountFragment.Companion.MODIFY_ACCOUNT_TYPE_KEY
import com.neexol.arkey.utils.ALL_CATEGORIES_ID
import com.neexol.arkey.utils.DebouncingQueryTextListener
import com.neexol.arkey.utils.NEW_CATEGORY_ID
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
        viewModel.displayAccounts.observe(viewLifecycleOwner, Observer {
            accountsListAdapter.updateDataList(it.toList())
        })
    }

    private fun initRecyclerView() {
        recyclerView.adapter = accountsListAdapter.apply {
            setOnAccountClickListener(object : AccountsListAdapter.OnAccountsListClickListener {
                override fun onAccountClick(account: Account) {
                    val bundle = bundleOf(MODIFY_ACCOUNT_TYPE_KEY to EditAccount(account))
                    navigateModifyAccount(bundle)
                }
            })
        }
    }

    private fun initBottomAppBar() = bottomAppBar.inflateMenu(R.menu.main_bottom)

    private fun showMenu() {
        val dialog = NavigationMenuBottomDialog()
        dialog.show(childFragmentManager, null)

        childFragmentManager.setFragmentResultListener(
            NAV_MENU_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            when(val result = bundle.getInt(NAV_MENU_KEY)) {
                NEW_CATEGORY_ID -> {TODO()}
                else -> {
                    viewModel.selectCategory(result)
                    setToolbarTitle(result)
                }
            }
        }
    }

    private fun initAndShowSearchView() {
        if (toolbar.menu.isEmpty()) {
            toolbar.inflateMenu(R.menu.main_top)
        }

        val menuItem = toolbar.menu.findItem(R.id.action_search_top)
        val searchView = (menuItem.actionView as SearchView)
        searchView.maxWidth = Int.MAX_VALUE
        searchView.isIconified = false

        searchView.setOnCloseListener {
            val imm = requireContext().getSystemService(InputMethodManager::class.java)
            imm.hideSoftInputFromWindow(searchView.windowToken, 0)
            searchView.setQuery("", true)
            toolbar.menu.clear()
            true
        }

        searchView.setOnQueryTextListener(
            DebouncingQueryTextListener(this.lifecycle) { newText ->
                newText?.let {
                    viewModel.setSearchQuery(it)
                }
            }
        )
    }

    private fun setToolbarTitle(categoryId: Int) {
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