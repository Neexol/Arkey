package com.neexol.arkey.viewmodels

import androidx.lifecycle.*
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.db.entities.Category
import com.neexol.arkey.repositories.AccountsRepository
import com.neexol.arkey.repositories.CategoriesRepository
import com.neexol.arkey.utils.ALL_CATEGORIES_ID
import com.neexol.arkey.utils.WITHOUT_CATEGORY_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    accountsRepo: AccountsRepository,
    private val categoriesRepo: CategoriesRepository
): ViewModel() {
    private val allAccounts: LiveData<List<Account>> = accountsRepo.allAccounts

    private val _selectedCategoryId = MutableLiveData(ALL_CATEGORIES_ID)
    val selectedCategoryId: LiveData<Int> = _selectedCategoryId
    fun selectCategory(categoryId: Int) = run { _selectedCategoryId.value = categoryId }

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery
    fun setSearchQuery(query: String) = run { _searchQuery.value = query }

    private val _displayAccounts = MediatorLiveData<List<Account>>()
    val displayAccounts: LiveData<List<Account>> = _displayAccounts

    init {
        initDisplayAccountsLiveData()
    }

    private fun initDisplayAccountsLiveData() {
        val onChangedObserver = Observer<Any> {
            viewModelScope.launch(Dispatchers.IO) {
                allAccounts.value?.let { accountsList ->
                    val displayList = when(selectedCategoryId.value) {
                        ALL_CATEGORIES_ID -> accountsList.toMutableList()
                        WITHOUT_CATEGORY_ID -> accountsList.filter { it.categoryId == null }
                        else -> accountsList.filter { it.categoryId == selectedCategoryId.value }
                    }
                    _displayAccounts.postValue(
                        displayList.filter {
                            it.name.toLowerCase().contains(
                                _searchQuery.value.orEmpty().toLowerCase().trim()
                            )
                        }
                    )
                }
            }
        }
        _displayAccounts.addSource(allAccounts, onChangedObserver)
        _displayAccounts.addSource(_selectedCategoryId, onChangedObserver)
        _displayAccounts.addSource(_searchQuery, onChangedObserver)
    }

    val allCategories: LiveData<List<Category>> = categoriesRepo.allCategories

    fun createCategory(categoryName: String) = viewModelScope.launch(Dispatchers.IO) {
        val newCategoryId = categoriesRepo.insert(categoryName)
        _selectedCategoryId.postValue(newCategoryId.toInt())
    }

    fun changeCategoryName(newName: String) = viewModelScope.launch(Dispatchers.IO) {
        categoriesRepo.update(Category(selectedCategoryId.value!!, newName))
    }

    fun deleteCurrentCategory() = viewModelScope.launch(Dispatchers.IO) {
        val deletingCategoryId = selectedCategoryId.value
        categoriesRepo.deleteById(deletingCategoryId!!)
        _selectedCategoryId.postValue(WITHOUT_CATEGORY_ID)
    }
}