package com.neexol.arkey.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.db.entities.Category
import com.neexol.arkey.repositories.AccountsRepository
import com.neexol.arkey.repositories.CategoriesRepository
import com.neexol.arkey.utils.ALL_CATEGORIES_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val accountsRepo: AccountsRepository,
    private val categoriesRepo: CategoriesRepository
): ViewModel() {
    val allAccounts: LiveData<List<Account>> = accountsRepo.allAccounts

    val allCategories: LiveData<List<Category>> = categoriesRepo.allCategories

    private val _selectedCategoryId = MutableLiveData(ALL_CATEGORIES_ID)
    val selectedCategoryId: LiveData<Int> = _selectedCategoryId

    fun selectCategory(categoryId: Int) = run { _selectedCategoryId.value = categoryId }
}