package com.neexol.arkey.viewmodels

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.repositories.AccountsRepository
import com.neexol.arkey.utils.WITHOUT_CATEGORY_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyAccountViewModel(
    private val accountsRepo: AccountsRepository
): ViewModel() {

    private var accountId: Int? = null

    val isValid = ObservableBoolean(false)

    var name = ""
    var login = ""
    var password = ""
    var site = ""
    var desc = ""
    var categoryId = WITHOUT_CATEGORY_ID

    val categoryIdsList = mutableListOf<Int>()

    fun checkData() = run { isValid.set(!name.isBlank()) }

    fun selectCategory(spinnerIndex: Int) = run { categoryId = categoryIdsList[spinnerIndex] }

    fun createAccount() = viewModelScope.launch(Dispatchers.IO) {
        accountsRepo.insert(
            Account(
                null,
                name.trim(),
                login.trim(),
                password.trim(),
                site.trim(),
                desc.trim(),
                if (categoryId == WITHOUT_CATEGORY_ID) null else categoryId,
                System.currentTimeMillis().toString()
            )
        )
    }

    fun fillAccountData(account: Account) {
        accountId = account.id
        name = account.name
        login = account.login
        password = account.password
        site = account.site
        desc = account.description
        categoryId = account.categoryId ?: WITHOUT_CATEGORY_ID
        checkData()
    }

    fun editAccount() = viewModelScope.launch(Dispatchers.IO) {
        accountsRepo.update(
            Account(
                accountId,
                name.trim(),
                login.trim(),
                password.trim(),
                site.trim(),
                desc.trim(),
                if (categoryId == WITHOUT_CATEGORY_ID) null else categoryId,
                System.currentTimeMillis().toString()
            )
        )
    }
}