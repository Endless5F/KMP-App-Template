package com.jetbrains.kmpapp.compose.index

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import com.jetbrains.kmpapp.compose.adaptive.LazyPagingItems
import com.jetbrains.kmpapp.compose.adaptive.collectAsLazyPagingItems
import com.jetbrains.kmpapp.modules.Article
import com.jetbrains.kmpapp.viewmodels.FavoritesViewModel
import com.lt.compose_views.other.VerticalSpace
import kmp_app_template.composeapp.generated.resources.Res
import kmp_app_template.composeapp.generated.resources.article_like
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Paging 实现分页列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesDestination(
    appNavController: NavHostController,
    innerPadding: PaddingValues, viewModel: FavoritesViewModel = koinViewModel()
) {
    val padding = PaddingValues(
        innerPadding.calculateStartPadding(LocalLayoutDirection.current),
        innerPadding.calculateTopPadding() + TopAppBarDefaults.TopAppBarExpandedHeight,
        innerPadding.calculateEndPadding(LocalLayoutDirection.current),
        innerPadding.calculateBottomPadding()
    )
    val state: LazyListState = rememberLazyListState()
    val pagingData = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    FavoritesArticleList(
        state = state,
        pagingData = pagingData,
        topRefresh = { pagingData.refresh() },
        modifier = Modifier.padding(padding)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesArticleList(
    state: LazyListState,
    pagingData: LazyPagingItems<Article>,
    topRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {

    val pagingDataLoadState = pagingData.loadState.refresh
    var isPullRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    if (isPullRefreshing && pagingDataLoadState !is LoadState.Loading) {
        coroutineScope.launch {
            isPullRefreshing = false
            state.scrollToItem(0)
        }
    }
    PullToRefreshBox(
        isRefreshing = isPullRefreshing,
        onRefresh = {
            topRefresh()
            isPullRefreshing = true
        },
        modifier = modifier
    ) {
        when (pagingDataLoadState) {
            is LoadState.Loading -> {
//                LoadingProgress()
            }

            is LoadState.Error -> {
                // 错误页
                RetryButton {
                    pagingData.refresh()
                }
            }

            is LoadState.NotLoading -> {
                if (pagingData.itemCount == 0) {
                    EmptyButton()
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), state = state, content = {
                        items(
                            pagingData.itemCount,
                            key = { index -> pagingData.peek(index)?.id ?: index }) { itemIndex ->
                            val article = pagingData[itemIndex]
                            if (article != null) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(6.dp, 10.dp, 6.dp, 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val author = if (article.author.isNullOrEmpty()) {
                                            article.shareUser ?: ""
                                        } else {
                                            article.author
                                        }
                                        Text(text = author)
                                        Text(text = article.niceDate ?: "")
                                    }
                                    VerticalSpace(dp = 3)
                                    Text(text = article.title)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = article.superChapterName + "." + article.chapterName)
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.article_like),
                                            contentDescription = "Like"
                                        )
                                    }
                                }
                            }
                        }

                        // 加载更多
                        if (pagingData.loadState.append == LoadState.Loading) {
                            item { LoadMoreProgress() }
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun LoadingProgress() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun LoadMoreProgress() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun RetryButton(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}

@Composable
fun EmptyButton() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
        Text("没有数据哦")
    }
}