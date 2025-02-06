package com.jetbrains.kmpapp.compose.index

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.TemplatePage
import com.jetbrains.kmpapp.viewmodels.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

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
    val articleList = viewModel.articleList.collectAsStateWithLifecycle()
    if (articleList.value == null) {
        println("articleList is null")
        Column(modifier = Modifier.padding(padding)) {
            Text("Home", modifier = Modifier.clickable {
                appNavController.navigate(TemplatePage)
            })
        }
        return
    }
    Column(modifier = Modifier.padding(padding)) {
        Text(articleList.value.toString(), modifier = Modifier.clickable {
            appNavController.navigate(TemplatePage)
        })
    }
}