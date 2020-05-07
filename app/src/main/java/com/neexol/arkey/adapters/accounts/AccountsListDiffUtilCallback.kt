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
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.id == new.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.name == new.name &&
                old.categoryId == new.categoryId &&
                old.lastModified == new.lastModified
    }
}