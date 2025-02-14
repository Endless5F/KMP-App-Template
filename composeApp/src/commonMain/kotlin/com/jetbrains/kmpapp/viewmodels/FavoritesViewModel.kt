package com.jetbrains.kmpapp.viewmodels

import androidx.lifecycle.ViewModel
import com.jetbrains.kmpapp.repositories.FavoritesRepository

class FavoritesViewModel(
    private val repo: FavoritesRepository
) : ViewModel() {

    val pagingDataFlow = repo.pagingDataFlow
}


