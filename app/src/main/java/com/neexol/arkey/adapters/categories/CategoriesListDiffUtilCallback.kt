package com.neexol.arkey.adapters.categories

import androidx.recyclerview.widget.DiffUtil
import com.neexol.arkey.db.entities.Category

class CategoriesListDiffUtilCallback(
    private val oldList: List<Category>,
    private val newList: List<Category>
): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new= newList[newItemPosition]
        return old.id == new.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.name == new.name
    }
}