package com.neexol.arkey.repositories

import androidx.lifecycle.LiveData
import com.neexol.arkey.db.daos.AccountDao
import com.neexol.arkey.db.entities.Account

class AccountsRepository(
    private val accountDao: AccountDao
) {
    val allAccounts: LiveData<List<Account>> = accountDao.getAll()

    fun insert(
        name: String,
        login: String,
        password: String,
        site: String,
        desc: String,
        categoryId: Int?
    ) {
        accountDao.insert(Account(
            null,
            name,
            login,
            password,
            "iv",
            site,
            desc,
            categoryId,
            System.currentTimeMillis().toString()
        ))
    }

    fun update(
        id: Int,
        name: String,
        login: String,
        password: String,
        site: String,
        desc: String,
        categoryId: Int?
    ) {
        accountDao.update(Account(
            id,
            name,
            login,
            password,
            "iv",
            site,
            desc,
            categoryId,
            System.currentTimeMillis().toString()
        ))
    }

    fun deleteById(accountId: Int) = accountDao.deleteById(accountId)
}