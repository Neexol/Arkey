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
import kotlinx.android.synthetic.main.item_account.view.*
import kotlinx.coroutines.*

class AccountsListAdapter: RecyclerView.Adapter<AccountsListAdapter.AccountHolder>() {

    private var clickListener: OnAccountsListClickListener? = null

    private var dataList = listOf<Account>()

    fun updateDataList(newDataList: List<Account>) {
        GlobalScope.launch(Dispatchers.Main) {
            val accountsDiffResult = withContext(Dispatchers.Default) {
                val diffUtilCallback =
                    AccountsListDiffUtilCallback(
                        dataList,
                        newDataList
                    )
                DiffUtil.calculateDiff(diffUtilCallback)
            }
            dataList = newDataList
            accountsDiffResult.dispatchUpdatesTo(this@AccountsListAdapter)
        }
    }

    override fun getItemCount() = dataList.size

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
                clickListener?.onAccountClick(dataList[adapterPosition])
            }
        }

        fun bind(position: Int) {
            accountName.text = dataList[position].name
        }
    }

    fun setOnAccountClickListener(clickListener: OnAccountsListClickListener) {
        this.clickListener = clickListener
    }

    interface OnAccountsListClickListener {
        fun onAccountClick(account: Account)
    }
}