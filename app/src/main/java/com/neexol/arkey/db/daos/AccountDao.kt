package com.neexol.arkey.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neexol.arkey.db.entities.Account

@Dao
abstract class AccountDao: BaseDao<Account> {
    @Query("SELECT * FROM accounts ORDER BY id DESC")
    abstract fun getAll(): LiveData<List<Account>>
}