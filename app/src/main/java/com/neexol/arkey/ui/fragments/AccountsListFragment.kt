package com.neexol.arkey.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neexol.arkey.AccountsListAdapter
import com.neexol.arkey.R
import kotlinx.android.synthetic.main.fragment_accounts_list.*
import kotlinx.android.synthetic.main.view_toolbar.*

class AccountsListFragment: Fragment() {

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
    }

    private fun initRecyclerView() {
        recyclerView.adapter = AccountsListAdapter().apply {
            updateDataList(listOf(
                "Instagram",
                "Вконтакте",
                "Facebook",
                "Slack",
                "Discord",
                "Microsoft",
                "Apple",
                "Samsung",
                "Xiaomi",
                "Google",
                "Yandex",
                "Telecom"
            ))
        }
    }
}