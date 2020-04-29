package com.neexol.arkey.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.utils.ALL_CATEGORIES_ID
import com.neexol.arkey.utils.WITHOUT_CATEGORY_ID
import kotlinx.android.synthetic.main.item_account.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountsListAdapter: RecyclerView.Adapter<AccountsListAdapter.AccountHolder>() {

    private var clickListener: OnAccountsListClickListener? = null

    private val dataList = mutableListOf<Account>()
    private val displayList = mutableListOf<Account>()

    private var selectedCategoryId: Int = ALL_CATEGORIES_ID

    fun updateDataList(newDataList: List<Account>) {
        dataList.clear()
        dataList.addAll(newDataList)
        selectCategory(selectedCategoryId)
    }

    fun selectCategory(categoryId: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                selectedCategoryId = categoryId
                displayList.clear()
                displayList.addAll(when(selectedCategoryId) {
                    ALL_CATEGORIES_ID -> dataList
                    WITHOUT_CATEGORY_ID -> dataList.filter { it.categoryId == null }
                    else -> dataList.filter { it.categoryId == selectedCategoryId }
                })
            }
            notifyDataSetChanged()
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