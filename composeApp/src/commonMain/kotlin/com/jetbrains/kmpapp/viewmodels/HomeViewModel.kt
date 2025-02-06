package com.jetbrains.kmpapp.viewmodels

import androidx.lifecycle.ViewModel
import com.jetbrains.kmpapp.modules.ArticleData
import com.jetbrains.kmpapp.repositories.HomeRepository
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(private val homeViewModel: HomeRepository) : ViewModel() {
    val articleList: StateFlow<ArticleData?> = homeViewModel.articleList

}
