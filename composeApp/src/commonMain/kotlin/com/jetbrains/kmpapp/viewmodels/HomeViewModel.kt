package com.jetbrains.kmpapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.modules.Article
import com.jetbrains.kmpapp.repositories.HomeRepository
import com.jetbrains.kmpapp.repositories.PagingData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: HomeRepository
) : ViewModel() {

    private val _pagingState = MutableSharedFlow<PagingData<Article>>(replay = 1)
    val pagingState: StateFlow<PagingData<Article>> = _pagingState
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData())

    init {
        loadInitial()
        viewModelScope.launch {
            combine(
                repo.articleList,
                repo.isRefreshing,
                repo.isLoadingMore
            ) { articles, isRefreshing, isLoadingMore ->
                PagingData(
                    data = articles ?: emptyList(),
                    isLoading = isLoadingMore,
                    isRefreshing = isRefreshing,
                    error = null,
                    hasMore = true // 根据实际 API 返回判断是否有更多数据
                )
            }.collectLatest {
                _pagingState.emit(it)
            }
        }
    }

    private fun loadInitial() = viewModelScope.launch {
        _pagingState.emit(
            PagingData(
                data = pagingState.value.data,
                isRefreshing = true
            )
        )
        try {
            repo.refresh()
        } catch (e: Exception) {
            _pagingState.emit(
                PagingData(
                    data = pagingState.value.data,
                    error = e,
                    isRefreshing = false
                )
            )
        }
    }

    fun refresh() = viewModelScope.launch {
        _pagingState.emit(
            pagingState.value.copy(isRefreshing = true)
        )
        try {
            repo.refresh()
        } catch (e: Exception) {
            _pagingState.emit(
                pagingState.value.copy(
                    error = e,
                    isRefreshing = false
                )
            )
        }
    }

    fun loadMore() = viewModelScope.launch {
        if (pagingState.value.isLoading || !pagingState.value.hasMore) return@launch

        _pagingState.emit(
            pagingState.value.copy(isLoading = true)
        )
        try {
            repo.loadMore()
        } catch (e: Exception) {
            _pagingState.emit(
                pagingState.value.copy(
                    error = e,
                    isLoading = false
                )
            )
        }
    }
}

