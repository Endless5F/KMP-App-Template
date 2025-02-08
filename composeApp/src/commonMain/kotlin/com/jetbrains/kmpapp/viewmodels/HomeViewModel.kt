package com.jetbrains.kmpapp.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.modules.Article
import com.jetbrains.kmpapp.repositories.HomeRepository
import com.jetbrains.kmpapp.repositories.PagingData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: HomeRepository
) : ViewModel() {

    private val _pagingState = mutableStateOf(PagingData<Article>())
    val pagingState: State<PagingData<Article>> = _pagingState
    private val _loadingState = mutableStateOf(false)
    val loadingState: State<Boolean> = _loadingState
    private val _isLoadingFinish = mutableStateOf(false)
    val isLoadingFinish: State<Boolean> = _isLoadingFinish

    init {
        loadInitial()
        viewModelScope.launch {
            repo.articleList.collectLatest { articles ->
                _pagingState.value = _pagingState.value.copy(
                    data = articles ?: emptyList(),
                    isLoading = false,
                    isRefreshing = false,
                    error = null,
                    hasMore = true // 根据实际 API 返回判断是否有更多数据
                )
            }
        }
    }

    private fun loadInitial() = viewModelScope.launch {
        _pagingState.value = _pagingState.value.copy(isLoading = true)
        try {
            repo.refresh()
        } catch (e: Exception) {
            _pagingState.value = _pagingState.value.copy(
                error = e,
                isLoading = false
            )
        }
    }

    fun refresh() = viewModelScope.launch {
        _pagingState.value = _pagingState.value.copy(isRefreshing = true)
        try {
            repo.refresh()
        } catch (e: Exception) {
            _pagingState.value = _pagingState.value.copy(
                error = e,
                isRefreshing = false
            )
        }
    }

    fun loadMore() = viewModelScope.launch {
        if (_pagingState.value.isLoading || !_pagingState.value.hasMore) return@launch

        _pagingState.value = _pagingState.value.copy(isLoading = true)
        try {
            _loadingState.value = true
            _isLoadingFinish.value = false
            repo.loadMore()
            _loadingState.value = false
            _isLoadingFinish.value = true
        } catch (e: Exception) {
            _pagingState.value = _pagingState.value.copy(
                error = e,
                isLoading = false
            )
        }
    }
}