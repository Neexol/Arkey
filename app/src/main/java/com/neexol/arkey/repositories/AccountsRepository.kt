package com.neexol.arkey.repositories

import androidx.lifecycle.LiveData
import com.neexol.arkey.db.daos.AccountDao
import com.neexol.arkey.db.entities.Account

class AccountsRepository(
    private val accountDao: AccountDao
) {
    val allAccounts: LiveData<List<Account>> = accountDao.getAll()

    suspend fun insert(account: Account) = accountDao.insert(account)

    suspend fun update(account: Account) = accountDao.update(account)

    suspend fun delete(account: Account) = accountDao.delete(account)
}