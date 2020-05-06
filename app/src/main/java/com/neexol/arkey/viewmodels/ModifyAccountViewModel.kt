package com.neexol.arkey.viewmodels

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import com.neexol.arkey.App
import com.neexol.arkey.R
import com.neexol.arkey.db.entities.Account
import com.neexol.arkey.repositories.AccountsRepository
import com.neexol.arkey.repositories.CategoriesRepository
import com.neexol.arkey.utils.Categories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyAccountViewModel(
    app: Application,
    private val accountsRepo: AccountsRepository,
    private val categoriesRepo: CategoriesRepository
): AndroidViewModel(app) {

    data class TitleWithId (
        val id: Int,
        val title: String?
    )

    private val _displayCategoriesLiveData = MediatorLiveData<List<TitleWithId>>()
    val displayCategoriesLiveData: LiveData<List<TitleWithId>> = _displayCategoriesLiveData

    init {
        _displayCategoriesLiveData.addSource(categoriesRepo.allCategories) {
            val displayCategoriesList = mutableListOf<TitleWithId>()

            displayCategoriesList.add(TitleWithId(Categories.WITHOUT_CATEGORY.id, null))
            it.forEach { category ->
                displayCategoriesList.add(TitleWithId(category.id!!, category.name))
            }
            displayCategoriesList.add(TitleWithId(
                Categories.NEW_CATEGORY.id,
                "➕  " + getApplication<App>().getString(R.string.create_category)
            ))

            _displayCategoriesLiveData.value = displayCategoriesList.toList()
        }
    }

    var accountId: Int? = null
        private set

    val isValid = ObservableBoolean(false)

    var name = ""
    var login = ""
    var password = ""
    var site = ""
    var desc = ""
    var categoryId = Categories.WITHOUT_CATEGORY.id

    fun checkData() = run { isValid.set(!name.isBlank()) }

    fun selectCategory(spinnerIndex: Int) {
        displayCategoriesLiveData.value?.get(spinnerIndex)?.id?.let {
            categoryId = it
        }
    }

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

    fun createCategory(categoryName: String) = viewModelScope.launch(Dispatchers.IO) {
        val newCategoryId = categoriesRepo.insert(categoryName)
        categoryId = newCategoryId.toInt()
    }
}