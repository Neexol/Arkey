package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.neexol.arkey.R
import com.neexol.arkey.adapters.accounts.AccountsListAdapter
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.ui.dialogs.InputTextDialog
import com.neexol.arkey.ui.dialogs.InputTextDialog.Companion.RESULT_INPUT_TEXT_KEY
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

    companion object {
        private const val CREATE_CATEGORY_REQUEST_KEY = "CREATE_CATEGORY_REQUEST"
        private const val RENAME_CATEGORY_REQUEST_KEY = "RENAME_CATEGORY_REQUEST"
    }

    private val viewModel: MainViewModel by sharedViewModel()

    private val accountsListAdapter: AccountsListAdapter = get()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_accounts_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initToolbarMenu()
        initBottomBarMenu()

        updateViewBasedOnSelectedCategory()

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        bottomAppBar.setNavigationOnClickListener { showMenu() }

        bottomAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_search_bottom -> showSearchView()
                R.id.action_rename_category_bottom -> showCategoryRenaming()
                R.id.action_delete_category -> viewModel.deleteCurrentCategory()
            }
            true
        }

        addAccountBtn.setOnClickListener {
            navigateModifyAccount()
        }

        childFragmentManager.setFragmentResultListener(
            RENAME_CATEGORY_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getString(RESULT_INPUT_TEXT_KEY)?.trim()
            if (!result.isNullOrBlank()) { viewModel.changeCategoryName(result) }
        }

        childFragmentManager.setFragmentResultListener(
            CREATE_CATEGORY_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getString(RESULT_INPUT_TEXT_KEY)?.trim()
            if (!result.isNullOrBlank()) { viewModel.createCategory(result) }
        }
    }

    private fun setObservers() {
        viewModel.displayAccounts.observe(viewLifecycleOwner, Observer {
            accountsListAdapter.updateDataList(it.toList())
        })
        viewModel.selectedCategoryId.observe(viewLifecycleOwner, Observer {
            updateViewBasedOnSelectedCategory()
        })
        viewModel.allCategories.observe(viewLifecycleOwner, Observer {
            updateViewBasedOnSelectedCategory()
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

    private fun initToolbarMenu() {
        toolbar.inflateMenu(R.menu.main_top)
        if (!viewModel.searchQuery.value.isNullOrEmpty()) {
            showSearchView()
        }
    }

    private fun initBottomBarMenu() {
        bottomAppBar.inflateMenu(R.menu.main_bottom)
    }

    private fun showMenu() {
        val dialog = NavigationMenuBottomDialog()
        dialog.show(childFragmentManager, null)

        childFragmentManager.setFragmentResultListener(
            NAV_MENU_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            when(val result = bundle.getInt(NAV_MENU_KEY)) {
                NEW_CATEGORY_ID -> showCategoryCreating()
                else -> viewModel.selectCategory(result)
            }
        }
    }

    private fun showSearchView() {
        val searchItem = toolbar.menu.findItem(R.id.action_search_top)
        searchItem.isVisible = true
        val searchView = (searchItem.actionView as SearchView)
        searchView.isIconified = false
        searchView.maxWidth = Int.MAX_VALUE

        searchView.setOnCloseListener {
            val imm = requireContext().getSystemService(InputMethodManager::class.java)
            imm.hideSoftInputFromWindow(searchView.windowToken, 0)
            searchItem.isVisible = false
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

    private fun showCategoryRenaming() {
        InputTextDialog.newInstance(
            RENAME_CATEGORY_REQUEST_KEY,
            getString(R.string.rename_category),
            toolbar.title.toString()
        ).show(childFragmentManager, null)
    }

    private fun showCategoryCreating() {
        InputTextDialog.newInstance(
            CREATE_CATEGORY_REQUEST_KEY,
            getString(R.string.create_category)
        ).show(childFragmentManager, null)
    }

    private fun updateViewBasedOnSelectedCategory() {
        val categoryId = viewModel.selectedCategoryId.value!!
        updateToolbarTitle(categoryId)
        updateCategoryOptionsVisible(categoryId)
    }

    private fun updateToolbarTitle(categoryId: Int) {
        toolbar.title = when(categoryId) {
            ALL_CATEGORIES_ID -> getString(R.string.all_accounts)
            WITHOUT_CATEGORY_ID -> getString(R.string.without_category)
            else -> viewModel.allCategories.value?.find { it.id == categoryId }?.name
        }
    }

    private fun updateCategoryOptionsVisible(categoryId: Int) {
        val isVisible = categoryId != ALL_CATEGORIES_ID && categoryId != WITHOUT_CATEGORY_ID
        with(bottomAppBar.menu) {
            findItem(R.id.action_rename_category_bottom).isVisible = isVisible
            findItem(R.id.action_delete_category).isVisible = isVisible
        }
    }

    private fun navigateModifyAccount(bundle: Bundle? = null) {
        findNavController().navigate(R.id.action_accountsListFragment_to_modifyAccountFragment, bundle)
    }
}