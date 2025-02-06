package com.jetbrains.kmpapp.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            appModule
        )
    }
}

val appModule = listOf(httpModule, museumModule, viewModelModule)

