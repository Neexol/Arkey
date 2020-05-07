package com.neexol.arkey.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neexol.arkey.db.entities.Category

@Dao
abstract class CategoryDao: BaseDao<Category> {
    @Query("SELECT * FROM categories ORDER BY name")
    abstract fun getAll(): LiveData<List<Category>>

    @Query("DELETE FROM categories WHERE id = :categoryId")
    abstract fun deleteById(categoryId: Int)
}