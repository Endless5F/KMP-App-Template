@file:Suppress("NAME_SHADOWING")

package com.jetbrains.kmpapp.compose.index

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.TemplatePage
import com.jetbrains.kmpapp.modules.Article
import com.jetbrains.kmpapp.repositories.PagingData
import com.jetbrains.kmpapp.viewmodels.HomeViewModel
import com.lt.compose_views.compose_pager.ComposePager
import com.lt.compose_views.compose_pager.rememberComposePagerState
import com.lt.compose_views.other.FpsText
import com.lt.compose_views.other.VerticalSpace
import com.lt.compose_views.refresh_layout.PullToRefresh
import com.lt.compose_views.refresh_layout.RefreshContentStateEnum
import com.lt.compose_views.refresh_layout.RefreshLayout
import com.lt.compose_views.refresh_layout.RefreshLayoutState
import com.lt.compose_views.refresh_layout.VerticalRefreshableLayout
import com.lt.compose_views.refresh_layout.refresh_content.EllipseRefreshContent
import com.lt.compose_views.refresh_layout.rememberRefreshLayoutState
import com.lt.compose_views.util.ComposePosition
import kmp_app_template.composeapp.generated.resources.Res
import kmp_app_template.composeapp.generated.resources.article_like
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * 传统逻辑实现分页列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDestination(
    appNavController: NavHostController,
    innerPadding: PaddingValues,
    viewModel: HomeViewModel = koinViewModel()
) {
    val padding = PaddingValues(
        innerPadding.calculateStartPadding(LocalLayoutDirection.current),
        innerPadding.calculateTopPadding() + TopAppBarDefaults.TopAppBarExpandedHeight,
        innerPadding.calculateEndPadding(LocalLayoutDirection.current),
        innerPadding.calculateBottomPadding()
    )
    val pagingData = viewModel.pagingState.collectAsStateWithLifecycle()
    if (pagingData.value.data.isEmpty()) {
        println("articleList is null")
        Column(modifier = Modifier.padding(padding)) {
            Text("ArticleList is null", modifier = Modifier.clickable {
                appNavController.navigate(TemplatePage)
            })
        }
    } else {
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
//            ComposeContent()
            HomeArticleList(
                { pagingData.value },
                { viewModel.refresh() },
                { viewModel.loadMore() }
            )
        }
    }
}

/**
 * 注：如果使用Paging组件显示，
 * 则直接使用系统提供的 PullToRefreshBox来实现下拉刷新，
 * 上拉加载Paging组件会自动处理只需要处理上拉加载时的UI显示即可
 */
@Composable
fun HomeArticleList(
    pagingData: () -> PagingData<Article>,
    topRefresh: () -> Unit,
    bottomRefresh: () -> Unit
) {
    val pagingData = pagingData()

    val topRefreshState = rememberRefreshLayoutState(onRefreshListener = {
        topRefresh()
    })
    val bottomRefreshState = rememberRefreshLayoutState(onRefreshListener = {
        bottomRefresh()
    })

    // 使用 LaunchedEffect 监听状态变化
    LaunchedEffect(pagingData.isRefreshing) {
        if (pagingData.isRefreshing) {
            topRefreshState.setRefreshState(RefreshContentStateEnum.Refreshing)
        } else {
            topRefreshState.setRefreshState(RefreshContentStateEnum.Stop)
        }
    }
    var isLoadFinish by remember { mutableStateOf(false) }
    LaunchedEffect(pagingData.isLoading) {
        if (!pagingData.hasMore) {
            isLoadFinish = true
        }
        if (!pagingData.isLoading) {
            bottomRefreshState.setRefreshState(RefreshContentStateEnum.Stop)
        }
    }

    VerticalRefreshableLayout(
        // 顶部刷新的状态
        topRefreshLayoutState = topRefreshState,
        // 底部刷新的状态
        bottomRefreshLayoutState = bottomRefreshState,
        modifier = Modifier.fillMaxSize(),
        bottomIsLoadFinish = isLoadFinish
    ) {
        val articleList = pagingData.data
        LazyColumn(modifier = Modifier.fillMaxSize(), content = {
            repeat(articleList.size) {
                item(key = it) {
                    Column(modifier = Modifier.fillMaxWidth().padding(6.dp, 10.dp, 6.dp, 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val author = if (articleList[it].author.isNullOrEmpty()) {
                                articleList[it].shareUser ?: ""
                            } else {
                                articleList[it].author ?: ""
                            }
                            Text(text = author)
                            Text(text = articleList[it].niceDate)
                        }
                        VerticalSpace(dp = 3)
                        Text(text = articleList[it].title)
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = articleList[it].superChapterName + "." + articleList[it].chapterName)
//                            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Like")
                            Icon(
                                imageVector = vectorResource(Res.drawable.article_like),
                                contentDescription = "Like"
                            )
                        }
                    }
                }
            }
        })
    }
}


@Composable
fun ComposeContent() {
    val topRefreshState = createState()
    val bottomRefreshState = createState()
    val startRefreshState = createState()
    val endRefreshState = createState()
    LaunchedEffect(key1 = Unit) {
        topRefreshState.setRefreshState(RefreshContentStateEnum.Refreshing)
        bottomRefreshState.setRefreshState(RefreshContentStateEnum.Refreshing)
        startRefreshState.setRefreshState(RefreshContentStateEnum.Refreshing)
        endRefreshState.setRefreshState(RefreshContentStateEnum.Refreshing)
    }

    Row {
        Column(
            Modifier
                .fillMaxHeight()
                .width(260.dp)
                .background(Color.LightGray)
        ) {
            Menu(topRefreshState, bottomRefreshState, startRefreshState, endRefreshState)

            TopRefreshLayout(topRefreshState)
            VerticalSpace(dp = 20)
            BottomRefreshLayout(bottomRefreshState)
            VerticalSpace(dp = 20)
            StartRefreshLayout(startRefreshState)
            VerticalSpace(dp = 20)
            EndRefreshLayout(endRefreshState)
        }
        Column(Modifier.fillMaxSize()) {
            MyPullToRefresh()
            VerticalSpace(dp = 20)
            MyRefreshableLazyColumn()
            VerticalSpace(dp = 20)
            MyRefreshablePager()
        }
    }
}

@Composable
private fun Menu(
    topRefreshState: RefreshLayoutState,
    bottomRefreshState: RefreshLayoutState,
    startRefreshState: RefreshLayoutState,
    endRefreshState: RefreshLayoutState
) {
    Column() {
        FpsText(modifier = Modifier)
        Text(text = "Top状态:${topRefreshState.getRefreshContentState().value}")
        Text(text = "Bottom状态:${bottomRefreshState.getRefreshContentState().value}")
        Text(text = "Start状态:${startRefreshState.getRefreshContentState().value}")
        Text(text = "End状态:${endRefreshState.getRefreshContentState().value}")
    }
}

private val colors = mutableStateListOf(
    Color(150, 105, 61, 255),
    Color(122, 138, 55, 255),
    Color(50, 134, 74, 255),
    Color(112, 62, 11, 255),
    Color(114, 61, 101, 255),
)

@Composable
private fun MyRefreshablePager() {
    val state = rememberComposePagerState()
    VerticalRefreshableLayout(
        topRefreshLayoutState = createState(),
        bottomRefreshLayoutState = createState(),
        topUserEnable = state.getCurrSelectIndex() == 0,
        bottomUserEnable = state.getCurrSelectIndex() == colors.size - 1,
    ) {
        ComposePager(
            pageCount = colors.size,
            orientation = Orientation.Vertical,
            composePagerState = state,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors[index])
            ) {
                Text(text = index.toString(), modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun MyRefreshableLazyColumn(mainScope: CoroutineScope = koinInject()) {
    val isLoadFinish = remember { mutableStateOf(false) }
    VerticalRefreshableLayout(
        //顶部刷新的状态
        topRefreshLayoutState = createState(),
        //底部刷新的状态
        bottomRefreshLayoutState = rememberRefreshLayoutState(onRefreshListener = {
            mainScope.launch {
//                "加载数据了".showToast()
                delay(2000)
                setRefreshState(RefreshContentStateEnum.Stop)
                isLoadFinish.value = true
            }
        }), modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        bottomIsLoadFinish = isLoadFinish.value
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), content = {
            repeat(20) {
                item(key = it) {
                    Text(text = "内容区域${it + 1}")
                }
            }
        })
    }
}

@Composable
private fun MyPullToRefresh() {
    PullToRefresh(refreshLayoutState = createState()) {
        Column(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "内容区域1")
            Text(text = "内容区域2")
            Text(text = "内容区域3")
            Text(text = "内容区域4")
            Text(text = "内容区域5")
            Text(text = "内容区域6")
            Text(text = "内容区域7")
            Text(text = "内容区域8")
            Text(text = "内容区域9")
        }
    }
}

@Composable
private fun createState(mainScope: CoroutineScope = koinInject()) = rememberRefreshLayoutState {
    mainScope.launch {
//        "刷新了".showToast()
        delay(2000)
        setRefreshState(RefreshContentStateEnum.Stop)
    }
}

@Composable
private fun TopRefreshLayout(refreshState: RefreshLayoutState) {
    RefreshLayout(
        {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = "下拉刷新",
                    modifier = Modifier
                        .background(Color.Red)
                        .align(Alignment.Center)
                )
            }
        },
        refreshLayoutState = refreshState,
        composePosition = ComposePosition.Top,
    ) {
        Column(
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .background(Color.Gray)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "内容区域1")
            Text(text = "内容区域2")
            Text(text = "内容区域3")
            Text(text = "内容区域4")
            Text(text = "内容区域5")
            Text(text = "内容区域6")
            Text(text = "内容区域7")
            Text(text = "内容区域8")
            Text(text = "内容区域9")
        }
    }
}

@Composable
private fun BottomRefreshLayout(refreshState: RefreshLayoutState) {
    RefreshLayout(
        {
            EllipseRefreshContent()
        },
        refreshContentThreshold = 30.dp,
        refreshLayoutState = refreshState,
        composePosition = ComposePosition.Bottom,
    ) {
        Column(
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .background(Color.Gray)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "内容区域1")
            Text(text = "内容区域2")
            Text(text = "内容区域3")
            Text(text = "内容区域4")
            Text(text = "内容区域5")
            Text(text = "内容区域6")
            Text(text = "内容区域7")
            Text(text = "内容区域8")
            Text(text = "内容区域9")
        }
    }
}

@Composable
private fun StartRefreshLayout(refreshState: RefreshLayoutState) {
    RefreshLayout(
        {
            Box(Modifier.fillMaxHeight()) {
                Text(
                    text = "下拉刷新",
                    modifier = Modifier
                        .background(Color.Red)
                        .align(Alignment.Center)
                )
            }
        },
        refreshLayoutState = refreshState,
        composePosition = ComposePosition.Start,
    ) {
        Row(
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .background(Color.Gray)
                .horizontalScroll(rememberScrollState())
        ) {
            Text(text = "内容区域1")
            Text(text = "内容区域2")
            Text(text = "内容区域3")
            Text(text = "内容区域4")
        }
    }
}

@Composable
private fun EndRefreshLayout(refreshState: RefreshLayoutState) {
    RefreshLayout(
        {
            Box(Modifier.fillMaxHeight()) {
                Text(
                    text = "下拉刷新",
                    modifier = Modifier
                        .background(Color.Red)
                        .align(Alignment.Center)
                )
            }
        },
        refreshLayoutState = refreshState,
        composePosition = ComposePosition.End,
        dragEfficiency = 2f,
        refreshContentThreshold = 100.dp
    ) {
        Row(
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .background(Color.Gray)
                .horizontalScroll(rememberScrollState())
        ) {
            Text(text = "内容区域1")
            Text(text = "内容区域2")
            Text(text = "内容区域3")
            Text(text = "内容区域4")
        }
    }
}
