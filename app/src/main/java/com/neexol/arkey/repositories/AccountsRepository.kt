package com.neexol.arkey.repositories

import androidx.lifecycle.LiveData
import com.neexol.arkey.db.daos.AccountDao
import com.neexol.arkey.db.entities.Account

class AccountsRepository(
    private val accountDao: AccountDao
) {
    val allAccounts: LiveData<List<Account>> = accountDao.getAll()

    fun insert(account: Account) = accountDao.insert(account)

    fun update(account: Account) = accountDao.update(account)

    fun delete(account: Account) = accountDao.delete(account)

    fun deleteById(accountId: Int) = accountDao.deleteById(accountId)
}