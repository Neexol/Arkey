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
import com.neexol.arkey.utils.addToClipboard
import com.neexol.arkey.utils.collapse
import com.neexol.arkey.utils.expand
import com.neexol.arkey.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AccountsListAdapter: RecyclerView.Adapter<AccountsListAdapter.AccountHolder>() {

    private data class AccountItem(
        val account: Account,
        var isExpanded: Boolean = false
    )

    private var clickListener: OnAccountsListClickListener? = null

    private var dataList = listOf<AccountItem>()

    fun updateDataList(newDataList: List<Account>) {
        GlobalScope.launch(Dispatchers.Main) {
            val accountsDiffResult = withContext(Dispatchers.Default) {
                val diffUtilCallback =
                    AccountsListDiffUtilCallback(
                        dataList.map { it.account },
                        newDataList
                    )
                DiffUtil.calculateDiff(diffUtilCallback)
            }
            dataList = newDataList.map { AccountItem(it) }
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
                clickListener?.onAccountEditClick(dataList[adapterPosition].account)
            }
            binding.collapsedPanel.setOnClickListener {
                dataList[adapterPosition].isExpanded = !dataList[adapterPosition].isExpanded
                toggleLayout(adapterPosition)
            }
            binding.copyLoginBtn.setOnClickListener {
                it.context.addToClipboard("login", dataList[adapterPosition].account.login)
                it.toast(it.context.getString(R.string.copied_clipboard))
            }
            binding.copyPasswordBtn.setOnClickListener {
                it.context.addToClipboard("password", dataList[adapterPosition].account.password)
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
            val account = dataList[position].account

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
            val isExpanded = dataList[position].isExpanded
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