package com.neexol.arkey.db.daos

import androidx.room.*
import com.neexol.arkey.db.entities.Account

@Dao
abstract class AccountDao: BaseDao<Account> {
    @Query("SELECT * FROM accounts")
    abstract fun getAll(): List<Account>
}