package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.viewmodels.DetailViewModel
import com.jetbrains.kmpapp.viewmodels.ListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::ListViewModel)
    viewModelOf(::DetailViewModel)

//    viewModel { ListViewModel(museumRepository = get()) }
//    viewModel { DetailViewModel(museumRepository = get()) }
}