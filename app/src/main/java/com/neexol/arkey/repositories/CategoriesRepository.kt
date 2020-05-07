package com.neexol.arkey.repositories

import androidx.lifecycle.LiveData
import com.neexol.arkey.db.daos.CategoryDao
import com.neexol.arkey.db.entities.Category

class CategoriesRepository(
    private val categoryDao: CategoryDao
) {
    val allCategories: LiveData<List<Category>> = categoryDao.getAll()

    fun insert(categoryName: String): Long = categoryDao.insert(Category(null, categoryName))

    fun update(category: Category) = categoryDao.update(category)

    fun deleteById(categoryId: Int) = categoryDao.deleteById(categoryId)
}