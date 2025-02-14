package com.jetbrains.kmpapp.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.jetbrains.kmpapp.di.bodyData
import com.jetbrains.kmpapp.modules.Article
import com.jetbrains.kmpapp.modules.ArticleData
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow


interface FavoritesRepository {
    val pagingDataFlow: Flow<PagingData<Article>>

}

class FavoritesRepositoryImpl(private val client: HttpClient, applicationScope: CoroutineScope) :
    FavoritesRepository {

    override val pagingDataFlow: Flow<PagingData<Article>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { ExamplePagingSource(client) }
    ).flow.cachedIn(applicationScope)
}

class ExamplePagingSource(
    private val apiService: HttpClient
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val page = params.key ?: 1
            val result = apiService.get("article/list/0/json").bodyData<ArticleData>()
            LoadResult.Page(
                data = result.datas?.toList() ?: emptyList(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.pageCount > page) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
