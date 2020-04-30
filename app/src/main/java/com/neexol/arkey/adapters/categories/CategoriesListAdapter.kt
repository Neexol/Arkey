package com.neexol.arkey.adapters.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Category
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesListAdapter: RecyclerView.Adapter<CategoriesListAdapter.CategoryHolder>() {

    private var clickListener: OnCategoriesListClickListener? = null

    private var dataList = listOf<Category>()

    fun updateDataList(newDataList: List<Category>) {
        GlobalScope.launch(Dispatchers.Main) {
            val categoriesDiffResult = withContext(Dispatchers.Default) {
                val diffUtilCallback =
                    CategoriesListDiffUtilCallback(
                        dataList,
                        newDataList
                    )
                DiffUtil.calculateDiff(diffUtilCallback)
            }
            dataList = newDataList
            categoriesDiffResult.dispatchUpdatesTo(this@CategoriesListAdapter)
        }
    }

    override fun getItemCount() = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoryHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_category, parent, false)
        )

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) = holder.bind(position)

    inner class CategoryHolder internal constructor(view: View): RecyclerView.ViewHolder(view) {
        private val categoryName: TextView = view.categoryNameTV

        init {
            categoryName.setOnClickListener {
                clickListener?.onCategoryClick(dataList[adapterPosition])
            }
        }

        fun bind(position: Int) {
            categoryName.text = dataList[position].name
        }
    }

    fun setOnCategoryClickListener(clickListener: OnCategoriesListClickListener) {
        this.clickListener = clickListener
    }

    interface OnCategoriesListClickListener {
        fun onCategoryClick(category: Category)
    }
}