package com.neexol.arkey.di

import androidx.room.Room
import com.neexol.arkey.R
import com.neexol.arkey.adapters.accounts.AccountsListAdapter
import com.neexol.arkey.adapters.categories.CategoriesListAdapter
import com.neexol.arkey.db.Database
import com.neexol.arkey.generators.HashGenerator
import com.neexol.arkey.repositories.AccountsRepository
import com.neexol.arkey.repositories.CategoriesRepository
import com.neexol.arkey.utils.Coder
import com.neexol.arkey.generators.PasswordGenerator
import com.neexol.arkey.persistence.MasterPasswordPreferences
import com.neexol.arkey.repositories.MasterPasswordRepository
import com.neexol.arkey.utils.MasterPasswordOperationType
import com.neexol.arkey.viewmodels.ModifyAccountViewModel
import com.neexol.arkey.viewmodels.MainViewModel
import com.neexol.arkey.viewmodels.MasterPasswordViewModel
import com.neexol.arkey.viewmodels.PasswordGeneratorViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            Database::class.java,
            androidContext().getString(R.string.database_name)
        ).build()
    }
    single { get<Database>().accountDao() }
    single { get<Database>().categoryDao() }
}

val repositoriesModule = module {
    single { AccountsRepository(get(), get()) }
    single { CategoriesRepository(get()) }
    single { MasterPasswordRepository(get(), get()) }
}

val viewModelsModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { ModifyAccountViewModel(androidApplication(), get(), get()) }
    viewModel { PasswordGeneratorViewModel(get()) }
    viewModel { (operationType: MasterPasswordOperationType) ->
        MasterPasswordViewModel(androidApplication(), get(), operationType)
    }
}

val adaptersModule = module {
    single { AccountsListAdapter() }
    factory { CategoriesListAdapter() }
}

val coderModule = module {
    single { Coder() }
}

val generatorsModule = module {
    single { PasswordGenerator.Builder() }
    single { HashGenerator() }
}

val persistenceModule = module {
    single { MasterPasswordPreferences(androidContext()) }
}