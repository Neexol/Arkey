package com.neexol.arkey.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neexol.arkey.db.entities.Category

@Dao
abstract class CategoryDao: BaseDao<Category> {
    @Query("SELECT * FROM categories")
    abstract fun getAll(): LiveData<List<Category>>
}