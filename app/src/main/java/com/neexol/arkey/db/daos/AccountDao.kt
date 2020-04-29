package com.neexol.arkey.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neexol.arkey.db.entities.Account

@Dao
abstract class AccountDao: BaseDao<Account> {
    @Query("SELECT * FROM accounts ORDER BY last_modified DESC")
    abstract fun getAll(): LiveData<List<Account>>

    @Query("DELETE FROM accounts WHERE id = :accountId")
    abstract fun deleteById(accountId: Int)
}