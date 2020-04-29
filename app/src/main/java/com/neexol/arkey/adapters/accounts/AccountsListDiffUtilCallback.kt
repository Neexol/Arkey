package com.neexol.arkey.adapters.accounts

import androidx.recyclerview.widget.DiffUtil
import com.neexol.arkey.db.entities.Account

class AccountsListDiffUtilCallback(
    private val oldList: List<Account>,
    private val newList: List<Account>
): DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldAccount = oldList[oldItemPosition]
        val newAccount = newList[newItemPosition]
        return oldAccount.id == newAccount.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldAccount = oldList[oldItemPosition]
        val newAccount = newList[newItemPosition]
        return oldAccount.name == newAccount.name &&
                oldAccount.categoryId == newAccount.categoryId &&
                oldAccount.lastModified == newAccount.lastModified
    }

}