package com.neexol.arkey.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neexol.arkey.db.daos.AccountDao
import com.neexol.arkey.db.daos.CategoryDao
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.db.entities.Category

@Database(entities = [Account::class, Category::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
}