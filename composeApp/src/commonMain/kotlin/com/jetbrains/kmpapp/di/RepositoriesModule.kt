package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.repositories.HomeRepository
import com.jetbrains.kmpapp.repositories.HomeRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoriesModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get(named(HTTP_NAMED)), get()) }
}