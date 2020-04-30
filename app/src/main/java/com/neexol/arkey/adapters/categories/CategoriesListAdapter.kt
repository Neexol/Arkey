package com.neexol.arkey.adapters.categories

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Category
import com.neexol.arkey.utils.selectAsCategory
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesListAdapter: RecyclerView.Adapter<CategoriesListAdapter.CategoryHolder>() {

    private var clickListener: OnCategoriesListClickListener? = null

    private var dataList = listOf<Category>()

    private var selectedCategoryId: Int? = null

    fun updateDataList(newDataList: List<Category>) {
//        GlobalScope.launch(Dispatchers.Main) {
//            val categoriesDiffResult = withContext(Dispatchers.Default) {
//                val diffUtilCallback =
//                    CategoriesListDiffUtilCallback(
//                        dataList,
//                        newDataList
//                    )
//                DiffUtil.calculateDiff(diffUtilCallback)
//            }
//            dataList = newDataList
//            categoriesDiffResult.dispatchUpdatesTo(this@CategoriesListAdapter)
//        }
        dataList = newDataList
        notifyDataSetChanged()
    }

    fun highlightCategory(categoryId: Int) {
        selectedCategoryId = categoryId
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
            val category = dataList[position]
            categoryName.text = category.name
            if (selectedCategoryId == category.id) {
                categoryName.selectAsCategory()
            }
        }
    }

    fun setOnCategoryClickListener(clickListener: OnCategoriesListClickListener) {
        this.clickListener = clickListener
    }

    interface OnCategoriesListClickListener {
        fun onCategoryClick(category: Category)
    }
}