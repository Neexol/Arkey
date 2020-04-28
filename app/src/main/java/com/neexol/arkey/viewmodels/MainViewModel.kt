package com.neexol.arkey.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.repositories.AccountsRepository
import com.neexol.arkey.repositories.CategoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val accountsRepo: AccountsRepository,
    private val categoriesRepo: CategoriesRepository
): ViewModel() {
    val allAccounts: LiveData<List<Account>> = accountsRepo.allAccounts

    fun insertAccount(account: Account) = viewModelScope.launch(Dispatchers.IO) {
        accountsRepo.insert(account)
    }
}