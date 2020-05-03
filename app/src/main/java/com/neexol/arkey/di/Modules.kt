package com.neexol.arkey.di

import androidx.room.Room
import com.neexol.arkey.adapters.accounts.AccountsListAdapter
import com.neexol.arkey.adapters.categories.CategoriesListAdapter
import com.neexol.arkey.db.Database
import com.neexol.arkey.repositories.AccountsRepository
import com.neexol.arkey.repositories.CategoriesRepository
import com.neexol.arkey.utils.Coder
import com.neexol.arkey.viewmodels.ModifyAccountViewModel
import com.neexol.arkey.viewmodels.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single { Room.databaseBuilder(androidContext(), Database::class.java, "accounts_db").build() }
    single { get<Database>().accountDao() }
    single { get<Database>().categoryDao() }
}

val repositoriesModule = module {
    single { AccountsRepository(get(), get()) }
    single { CategoriesRepository(get()) }
}

val viewModelsModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { ModifyAccountViewModel(get()) }
}

val adaptersModule = module {
    single { AccountsListAdapter() }
    factory { CategoriesListAdapter() }
}

val codersModule = module {
    single { Coder() }
}