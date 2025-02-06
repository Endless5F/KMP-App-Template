package com.jetbrains.kmpapp.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class DispatcherType {
    IO, Main
}

val coroutineModule = module {
    single<CoroutineScope> { MainScope() }
    // tips: Dispatchers.IO is not available in Kotlin/JS ，see #https://github.com/Kotlin/kotlinx.coroutines
    single<CoroutineDispatcher>(named(DispatcherType.IO)) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(DispatcherType.Main)) { Dispatchers.Main }
}