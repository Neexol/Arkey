package com.neexol.arkey.viewmodels

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.repositories.AccountsRepository
import com.neexol.arkey.utils.Categories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyAccountViewModel(
    private val accountsRepo: AccountsRepository
): ViewModel() {

    var accountId: Int? = null
        private set

    val isValid = ObservableBoolean(false)

    var name = ""
    var login = ""
    var password = ""
    var site = ""
    var desc = ""
    var categoryId = Categories.WITHOUT_CATEGORY.id

    val categoryIdsList = mutableListOf<Int>()

    fun checkData() = run { isValid.set(!name.isBlank()) }

    fun selectCategory(spinnerIndex: Int) = run { categoryId = categoryIdsList[spinnerIndex] }

    fun fillAccountData(account: Account) {
        accountId = account.id
        name = account.name
        login = account.login
        password = account.password
        site = account.site
        desc = account.description
        categoryId = account.categoryId ?: Categories.WITHOUT_CATEGORY.id
        checkData()
    }

    fun createAccount() = viewModelScope.launch(Dispatchers.IO) {
        accountsRepo.insert(
            name.trim(),
            login.trim(),
            password.trim(),
            site.trim(),
            desc.trim(),
            if (categoryId == Categories.WITHOUT_CATEGORY.id) null else categoryId
        )
    }

    fun editAccount() = viewModelScope.launch(Dispatchers.IO) {
        accountsRepo.update(
            accountId!!,
            name.trim(),
            login.trim(),
            password.trim(),
            site.trim(),
            desc.trim(),
            if (categoryId == Categories.WITHOUT_CATEGORY.id) null else categoryId
        )
    }

    fun deleteAccount() = viewModelScope.launch(Dispatchers.IO) {
        accountsRepo.deleteById(accountId!!)
    }
}