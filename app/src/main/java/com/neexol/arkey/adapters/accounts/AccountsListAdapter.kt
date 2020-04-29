package com.neexol.arkey.adapters.accounts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.utils.ALL_CATEGORIES_ID
import com.neexol.arkey.utils.WITHOUT_CATEGORY_ID
import kotlinx.android.synthetic.main.item_account.view.*
import kotlinx.coroutines.*

class AccountsListAdapter: RecyclerView.Adapter<AccountsListAdapter.AccountHolder>() {

    private var clickListener: OnAccountsListClickListener? = null

    private var dataList = listOf<Account>()
    private var displayList = listOf<Account>()

    private var selectedCategoryId = ALL_CATEGORIES_ID

    fun updateDataList(newDataList: List<Account>) {
        dataList = newDataList
        updateDisplayList()
    }

    fun selectCategory(categoryId: Int) {
        selectedCategoryId = categoryId
        updateDisplayList()
    }

    fun searchQuery(query: String) = updateDisplayList(query)

    fun searchReset() = updateDisplayList()

    private fun updateDisplayList(searchQuery: String = "") {
        GlobalScope.launch(Dispatchers.Main) {
            val newDisplayList = generateNewDisplayList(searchQuery)
            val accountsDiffResult = withContext(Dispatchers.Default) {
                val diffUtilCallback =
                    AccountsListDiffUtilCallback(
                        displayList,
                        newDisplayList
                    )
                DiffUtil.calculateDiff(diffUtilCallback)
            }
            displayList = newDisplayList
            accountsDiffResult.dispatchUpdatesTo(this@AccountsListAdapter)
        }
    }

    private fun generateNewDisplayList(searchQuery: String): List<Account> {
        val newDisplayList = when(selectedCategoryId) {
            ALL_CATEGORIES_ID -> dataList
            WITHOUT_CATEGORY_ID -> dataList.filter { it.categoryId == null }
            else -> dataList.filter { it.categoryId == selectedCategoryId }
        }
        return newDisplayList.filter {
            it.name.toLowerCase().contains(
                searchQuery.toLowerCase().trim()
            )
        }
    }

    override fun getItemCount() = displayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AccountHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_account, parent, false)
        )

    override fun onBindViewHolder(holder: AccountHolder, position: Int) = holder.bind(position)

    inner class AccountHolder internal constructor(view: View): RecyclerView.ViewHolder(view) {
        private val accountIcon: ImageView = view.accountIcon
        private val accountName: TextView = view.accountName

        init {
            view.setOnClickListener {
                clickListener?.onAccountClick(displayList[adapterPosition])
            }
        }

        fun bind(position: Int) {
            accountName.text = displayList[position].name
        }
    }

    fun setOnAccountClickListener(clickListener: OnAccountsListClickListener) {
        this.clickListener = clickListener
    }

    interface OnAccountsListClickListener {
        fun onAccountClick(account: Account)
    }
}