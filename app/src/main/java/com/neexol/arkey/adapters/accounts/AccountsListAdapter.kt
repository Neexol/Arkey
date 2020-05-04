package com.neexol.arkey.adapters.accounts

import android.text.method.PasswordTransformationMethod
import android.view.*
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neexol.arkey.R
import com.neexol.arkey.databinding.ItemAccountBinding
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.utils.copyToClipboard
import com.neexol.arkey.utils.collapse
import com.neexol.arkey.utils.expand
import com.neexol.arkey.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AccountsListAdapter: RecyclerView.Adapter<AccountsListAdapter.AccountHolder>() {

    private var clickListener: OnAccountsListClickListener? = null

    private var dataList = listOf<Account>()

    private val expandedAccountsIds = mutableListOf<Int>()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
        val binding = DataBindingUtil.inflate<ItemAccountBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_account,
            parent,
            false
        )
        return AccountHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountHolder, position: Int) = holder.bind(position)

    inner class AccountHolder internal constructor(private val binding: ItemAccountBinding):
        RecyclerView.ViewHolder(binding.root) {
        private val expandedPanel: LinearLayout = binding.expandedPanel

        init {
            binding.editAccountBtn.setOnClickListener {
                clickListener?.onAccountEditClick(dataList[adapterPosition])
            }
            binding.collapsedPanel.setOnClickListener {
                val accountId = dataList[adapterPosition].id
                if (expandedAccountsIds.contains(accountId)) {
                    expandedAccountsIds.remove(accountId!!)
                } else {
                    expandedAccountsIds.add(accountId!!)
                }
                toggleLayout(adapterPosition)
            }
            binding.copyLoginBtn.setOnClickListener {
                it.context.copyToClipboard("login", dataList[adapterPosition].login)
                it.toast(it.context.getString(R.string.copied_clipboard))
            }
            binding.copyPasswordBtn.setOnClickListener {
                it.context.copyToClipboard("password", dataList[adapterPosition].password)
                it.toast(it.context.getString(R.string.copied_clipboard))
            }
            binding.visibilityPasswordBtn.setOnClickListener {
                if (it.isActivated) {
                    binding.passwordHolder.transformationMethod = PasswordTransformationMethod()
                } else {
                    binding.passwordHolder.transformationMethod = null
                }
                it.isActivated = !it.isActivated
            }
        }

        fun bind(position: Int) {
            val account = dataList[position]

            if (account.login.isEmpty() &&
                account.password.isEmpty() &&
                account.site.isEmpty() &&
                account.description.isEmpty()
            ) {
                binding.notFilledNotification.visibility = View.VISIBLE
            } else {
                binding.notFilledNotification.visibility = View.GONE
            }

            binding.account = account
            binding.executePendingBindings()
            toggleLayout(position)
        }

        private fun toggleLayout(position: Int) {
            val isExpanded = expandedAccountsIds.contains(dataList[position].id)
            if (isExpanded) {
                expandedPanel.expand()
            } else {
                expandedPanel.collapse()
            }
            if (isExpanded) {
                binding.editAccountBtn.visibility = View.VISIBLE
            } else {
                binding.editAccountBtn.visibility = View.GONE
            }
        }
    }

    fun setOnAccountClickListener(clickListener: OnAccountsListClickListener) {
        this.clickListener = clickListener
    }

    interface OnAccountsListClickListener {
        fun onAccountEditClick(account: Account)
    }
}