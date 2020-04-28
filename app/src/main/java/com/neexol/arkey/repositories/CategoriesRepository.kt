package com.neexol.arkey.repositories

import com.neexol.arkey.db.daos.CategoryDao
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.db.entities.Category

class CategoriesRepository(
    private val categoryDao: CategoryDao
) {
    suspend fun getAll(): List<Category> = categoryDao.getAll()

    suspend fun insert(category: Category) = categoryDao.insert(category)

    suspend fun update(category: Category) = categoryDao.update(category)

    suspend fun delete(category: Category) = categoryDao.delete(category)
}