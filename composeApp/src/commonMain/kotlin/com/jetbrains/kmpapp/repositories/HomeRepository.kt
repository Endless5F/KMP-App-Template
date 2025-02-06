package com.jetbrains.kmpapp.repositories

import com.jetbrains.kmpapp.di.bodyData
import com.jetbrains.kmpapp.modules.ArticleData
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take

interface HomeRepository {
    val articleList: StateFlow<ArticleData?>

}

class HomeRepositoryImpl(private val client: HttpClient, applicationScope: CoroutineScope) : HomeRepository {

    override val articleList: StateFlow<ArticleData?> = flow {
        val articleData = client.get("article/list/0/json").bodyData<ArticleData>()
        emit(articleData)
    }.catch {
        println("HomeRepositoryImpl, catch: $it")
    }.take(1).onEach {
        // TODO 保存数据
        println("HomeRepositoryImpl, articleList: $it")
    }.onStart {
        // TODO 读取缓存
        println("HomeRepositoryImpl, articleList: onStart")
    }.stateIn(applicationScope, SharingStarted.Eagerly, null)
}
