package com.neexol.arkey.repositories

import androidx.lifecycle.LiveData
import com.neexol.arkey.db.daos.CategoryDao
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.db.entities.Category

class CategoriesRepository(
    private val categoryDao: CategoryDao
) {
    val allCategories: LiveData<List<Category>> = categoryDao.getAll()

    fun insert(category: Category) = categoryDao.insert(category)

    fun update(category: Category) = categoryDao.update(category)

    fun delete(category: Category) = categoryDao.delete(category)

    fun deleteById(categoryId: Int) = categoryDao.deleteById(categoryId)
}