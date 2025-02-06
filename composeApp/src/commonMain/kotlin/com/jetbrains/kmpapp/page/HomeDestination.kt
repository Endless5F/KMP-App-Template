package com.jetbrains.kmpapp.page

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
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.TemplatePage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDestination(appNavController: NavHostController, innerPadding: PaddingValues) {
    val padding = PaddingValues(
        innerPadding.calculateStartPadding(LocalLayoutDirection.current),
        innerPadding.calculateTopPadding() + TopAppBarDefaults.TopAppBarExpandedHeight,
        innerPadding.calculateEndPadding(LocalLayoutDirection.current),
        innerPadding.calculateBottomPadding()
    )
    Column(modifier = Modifier.padding(padding))  {
        Text("Home", modifier = Modifier.clickable {
            appNavController.navigate(TemplatePage)
        })
    }
}