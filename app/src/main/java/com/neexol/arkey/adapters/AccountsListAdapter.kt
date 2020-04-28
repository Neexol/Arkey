package com.neexol.arkey.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Account
import kotlinx.android.synthetic.main.item_account.view.*

class AccountsListAdapter: RecyclerView.Adapter<AccountsListAdapter.AccountHolder>() {

    companion object {
        const val WITHOUT_CATEGORY = -1
    }

    private val dataList = mutableListOf<Account>()
    private val displayList = mutableListOf<Account>()

    private var selectedCategoryId: Int? = WITHOUT_CATEGORY

    fun updateDataList(newDataList: List<Account>) {
        dataList.clear()
        dataList.addAll(newDataList)
        selectCategory(selectedCategoryId)
        notifyDataSetChanged()
    }

    fun selectCategory(categoryId: Int?) {
        selectedCategoryId = categoryId
        displayList.clear()
        displayList.addAll(when(selectedCategoryId) {
            WITHOUT_CATEGORY -> dataList
            else -> dataList.filter { it.categoryId == selectedCategoryId }
        })
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

        fun bind(position: Int) {
            accountName.text = displayList[position].name
        }
    }
}