package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.neexol.arkey.R
import com.neexol.arkey.adapters.accounts.AccountsListAdapter
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.ui.dialogs.InputTextDialog
import com.neexol.arkey.ui.dialogs.InputTextDialog.Companion.RESULT_INPUT_TEXT_KEY
import com.neexol.arkey.ui.dialogs.NavigationMenuBottomDialog
import com.neexol.arkey.ui.dialogs.NavigationMenuBottomDialog.Companion.NAV_MENU_KEY
import com.neexol.arkey.ui.dialogs.NavigationMenuBottomDialog.Companion.NAV_MENU_REQUEST_KEY
import com.neexol.arkey.ui.dialogs.YesNoDialog
import com.neexol.arkey.ui.fragments.ModifyAccountFragment.Companion.MODIFY_ACCOUNT_TYPE_KEY
import com.neexol.arkey.utils.*
import com.neexol.arkey.utils.DebouncingQueryTextListener
import com.neexol.arkey.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_accounts_list.*
import kotlinx.android.synthetic.main.view_toolbar.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AccountsListFragment: Fragment(), AccountsListAdapter.OnAccountsListClickListener {

    companion object {
        private const val CREATE_CATEGORY_REQUEST_KEY = "CREATE_CATEGORY_REQUEST"
        private const val RENAME_CATEGORY_REQUEST_KEY = "RENAME_CATEGORY_REQUEST"
        private const val DELETE_CATEGORY_REQUEST_KEY = "DELETE_CATEGORY_REQUEST"
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
                R.id.action_delete_category -> showDeleteCategoryConfirmation()
            }
            true
        }

        addAccountBtn.setOnClickListener {
            navigateModifyAccount(
                bundleOf(MODIFY_ACCOUNT_TYPE_KEY to CreateAccount(
                    viewModel.selectedCategoryId.value!!
                )),
                FragmentNavigatorExtras(addAccountBtn to "sharedModifyAccount")
            )
        }

        childFragmentManager.setFragmentResultListener(
            CREATE_CATEGORY_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getString(RESULT_INPUT_TEXT_KEY)?.trim()
            if (!result.isNullOrBlank()) { viewModel.createCategory(result) }
        }

        childFragmentManager.setFragmentResultListener(
            RENAME_CATEGORY_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result = bundle.getString(RESULT_INPUT_TEXT_KEY)?.trim()
            if (!result.isNullOrBlank()) { viewModel.changeCategoryName(result) }
        }

        childFragmentManager.setFragmentResultListener(
            DELETE_CATEGORY_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.getBoolean(YesNoDialog.RESULT_YES_NO_KEY)) {
                viewModel.deleteCurrentCategory()
            }
        }

        childFragmentManager.setFragmentResultListener(
            NAV_MENU_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            when(val result = bundle.getInt(NAV_MENU_KEY)) {
                Categories.NEW_CATEGORY.id -> showCategoryCreating()
                else -> viewModel.selectCategory(result)
            }
        }
    }

    private fun setObservers() {
        viewModel.displayAccounts.observe(viewLifecycleOwner, Observer {
            accountsListAdapter.updateDataList(it)
            invalidateList(it)
        })
        viewModel.selectedCategoryId.observe(viewLifecycleOwner, Observer {
            updateViewBasedOnSelectedCategory()
        })
        viewModel.allCategories.observe(viewLifecycleOwner, Observer {
            updateViewBasedOnSelectedCategory()
        })
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = accountsListAdapter.apply {
            setOnAccountClickListener(this@AccountsListFragment)
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
    }

    private fun showSearchView() {
        val searchItem = toolbar.menu.findItem(R.id.action_search_top)
        searchItem.isVisible = true
        val searchView = (searchItem.actionView as SearchView)
        searchView.isIconified = false
        searchView.maxWidth = Int.MAX_VALUE

        searchView.setOnCloseListener {
            requireActivity().hideSoftInput(searchView.windowToken)
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

    private fun showDeleteCategoryConfirmation() {
        YesNoDialog.newInstance(
            DELETE_CATEGORY_REQUEST_KEY,
            getString(R.string.delete_category_confirmation)
        ).show(childFragmentManager, null)
    }

    private fun showCategoryRenaming() {
        InputTextDialog.newInstance(
            RENAME_CATEGORY_REQUEST_KEY,
            getString(R.string.rename_category),
            getString(R.string.category_name_hint),
            toolbar.title.toString()
        ).show(childFragmentManager, null)
    }

    private fun showCategoryCreating() {
        InputTextDialog.newInstance(
            CREATE_CATEGORY_REQUEST_KEY,
            getString(R.string.create_category),
            getString(R.string.category_name_hint)
        ).show(childFragmentManager, null)
    }

    private fun updateViewBasedOnSelectedCategory() {
        val categoryId = viewModel.selectedCategoryId.value!!
        updateToolbarTitle(categoryId)
        updateCategoryOptionsVisible(categoryId)
    }

    private fun updateToolbarTitle(categoryId: Int) {
        toolbar.title = when(categoryId) {
            Categories.ALL_CATEGORIES.id -> getString(R.string.all_accounts)
            Categories.WITHOUT_CATEGORY.id -> getString(R.string.accounts_without_category)
            else -> viewModel.allCategories.value?.find { it.id == categoryId }?.name
        }
    }

    private fun invalidateList(accountsList: List<Account>) {
        if (accountsList.isEmpty()) {
            emptyListNotification.text = getString(
                when {
                    !viewModel.searchQuery.value.isNullOrBlank() -> {
                        R.string.empty_search_result_notification
                    }
                    viewModel.selectedCategoryId.value != Categories.ALL_CATEGORIES.id -> {
                        R.string.empty_category_notification
                    }
                    else -> {
                        R.string.empty_accounts_list_notification
                    }
                }
            )
            emptyListNotification.isVisible = true
        } else {
            emptyListNotification.isVisible = false
        }
    }

    private fun updateCategoryOptionsVisible(categoryId: Int) {
        val isVisible =
            categoryId != Categories.ALL_CATEGORIES.id &&
                    categoryId != Categories.WITHOUT_CATEGORY.id
        with(bottomAppBar.menu) {
            findItem(R.id.action_rename_category_bottom).isVisible = isVisible
            findItem(R.id.action_delete_category).isVisible = isVisible
        }
    }

    override fun onAccountEditClick(account: Account) {
        navigateModifyAccount(
            bundleOf(MODIFY_ACCOUNT_TYPE_KEY to EditAccount(account))
        )
    }

    override fun onCopyClick(text: String) {
        requireContext().copyToClipboard(text)
        Snackbar.make(
            requireView(),
            getString(R.string.copied_clipboard),
            Snackbar.LENGTH_SHORT
        ).apply {
            anchorView = addAccountBtn
        }.show()
    }

    private fun navigateModifyAccount(bundle: Bundle, extras: Navigator.Extras? = null) {
        findNavController().navigate(
            R.id.action_accountsListFragment_to_modifyAccountFragment,
            bundle,
            null,
            extras
        )
    }
}