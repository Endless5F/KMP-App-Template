package com.jetbrains.kmpapp.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jetbrains.kmpapp.TemplatePage

@Composable
fun HomeDestination(appNavController: NavHostController, inerPadding: PaddingValues) {
    Column(modifier = Modifier.padding(inerPadding))  {
        Text("Home", modifier = Modifier.clickable {
            appNavController.navigate(TemplatePage)
        })
    }
}