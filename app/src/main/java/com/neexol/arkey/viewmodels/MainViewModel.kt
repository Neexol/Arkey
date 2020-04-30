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
    private val accountsRepo: AccountsRepository,
    private val categoriesRepo: CategoriesRepository
): ViewModel() {
    private val allAccounts: LiveData<List<Account>> = accountsRepo.allAccounts

    private val selectedCategoryId = MutableLiveData(ALL_CATEGORIES_ID)
    fun selectCategory(categoryId: Int) = run { selectedCategoryId.value = categoryId }
    fun getSelectedCategoryId() = selectedCategoryId.value!!

    private val searchQuery = MutableLiveData("")
    fun setSearchQuery(query: String) = run { searchQuery.value = query }

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
                                searchQuery.value.orEmpty().toLowerCase().trim()
                            )
                        }
                    )
                }
            }
        }
        _displayAccounts.addSource(allAccounts, onChangedObserver)
        _displayAccounts.addSource(selectedCategoryId, onChangedObserver)
        _displayAccounts.addSource(searchQuery, onChangedObserver)
    }

    val allCategories: LiveData<List<Category>> = categoriesRepo.allCategories
}