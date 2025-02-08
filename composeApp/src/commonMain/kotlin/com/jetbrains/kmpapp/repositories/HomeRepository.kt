package com.jetbrains.kmpapp.repositories

import com.jetbrains.kmpapp.di.bodyData
import com.jetbrains.kmpapp.modules.Article
import com.jetbrains.kmpapp.modules.ArticleData
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// 2. 定义分页状态类
data class PagingData<T>(
    val data: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: Throwable? = null,
    val hasMore: Boolean = true
)

interface HomeRepository {
    val articleList: StateFlow<List<Article>?>

    suspend fun refresh()

    suspend fun loadMore()
}

class HomeRepositoryImpl(private val client: HttpClient, applicationScope: CoroutineScope) :
    HomeRepository {

    private val _currentPage = MutableStateFlow(0)
    private val _allData = MutableStateFlow<List<Article>>(emptyList())

    override val articleList: StateFlow<List<Article>> = _allData
        .stateIn(applicationScope, SharingStarted.Eagerly, emptyList())

    override suspend fun refresh() {
        kotlin.runCatching {
            val result = client.get("article/list/0/json").bodyData<ArticleData>()
            _allData.value = result.datas ?: emptyList()
            _currentPage.value = 0
        }
    }

    override suspend fun loadMore() {
        val nextPage = _currentPage.value + 1
        kotlin.runCatching {
            val result = client.get("article/list/$nextPage/json").bodyData<ArticleData>()
            _allData.value += (result.datas ?: emptyList())
            _currentPage.value = nextPage
        }
    }
}
