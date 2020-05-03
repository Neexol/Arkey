package com.neexol.arkey.repositories

import android.util.Base64
import androidx.lifecycle.MediatorLiveData
import com.neexol.arkey.db.daos.AccountDao
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.utils.Coder

class AccountsRepository(
    private val accountDao: AccountDao,
    private val coder: Coder
) {
    companion object {
        private const val PASSWORD_ALIAS = "PasswordAlias"
    }

    val allAccounts = MediatorLiveData<List<Account>>()

    init {
        allAccounts.addSource(accountDao.getAll()) {
            it.forEach { account ->
                account.password = coder.decryptData(
                    PASSWORD_ALIAS,
                    Base64.decode(account.password, Base64.DEFAULT),
                    Base64.decode(account.iv, Base64.DEFAULT)
                )
            }
            allAccounts.value = it
        }
    }

    fun insert(
        name: String,
        login: String,
        password: String,
        site: String,
        desc: String,
        categoryId: Int?
    ) {
        val encodedPair = coder.encryptText(PASSWORD_ALIAS, password)
        accountDao.insert(Account(
            null,
            name,
            login,
            Base64.encodeToString(encodedPair.first, Base64.DEFAULT),
            Base64.encodeToString(encodedPair.second, Base64.DEFAULT),
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
        val encodedPair = coder.encryptText(PASSWORD_ALIAS, password)
        accountDao.update(Account(
            id,
            name,
            login,
            Base64.encodeToString(encodedPair.first, Base64.DEFAULT),
            Base64.encodeToString(encodedPair.second, Base64.DEFAULT),
            site,
            desc,
            categoryId,
            System.currentTimeMillis().toString()
        ))
    }

    fun deleteById(accountId: Int) = accountDao.deleteById(accountId)
}