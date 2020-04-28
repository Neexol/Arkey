package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.neexol.arkey.adapters.AccountsListAdapter
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Account
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
    ): View? {
        return inflater.inflate(R.layout.fragment_accounts_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.all_accounts)

        initRecyclerView()

        addAccountBtn.setOnClickListener {
            viewModel.insertAccount(
                Account(
                    null, "Name", "Login", "Password", "www", "desc", null, System.currentTimeMillis().toString()
                ))
        }

        viewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            accountsListAdapter.updateDataList(it.toList())
        })
    }

    private fun initRecyclerView() {
        recyclerView.adapter = accountsListAdapter
    }
}