package com.neexol.arkey.di

import androidx.room.Room
import com.neexol.arkey.db.Database
import com.neexol.arkey.repositories.AccountsRepository
import com.neexol.arkey.repositories.CategoriesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { Room.databaseBuilder(androidContext(), Database::class.java, "accounts_db").build() }
    single { get<Database>().accountDao() }
    single { get<Database>().categoryDao() }
}

val repositoriesModule = module {
    single { AccountsRepository(get()) }
    single { CategoriesRepository(get()) }
}