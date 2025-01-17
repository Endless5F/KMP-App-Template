package com.jetbrains.kmpapp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jetbrains.kmpapp.compose.adaptive.NavigationSuiteScaffold
import com.jetbrains.kmpapp.compose.adaptive.NavigationSuiteScaffoldDefaults
import com.jetbrains.kmpapp.compose.adaptive.WindowAdaptiveInfoDefault
import com.jetbrains.kmpapp.page.FavoritesDestination
import com.jetbrains.kmpapp.page.HomeDestination
import com.jetbrains.kmpapp.page.ProfileDestination
import com.jetbrains.kmpapp.page.ShoppingDestination
import com.jetbrains.kmpapp.screens.detail.DetailScreen
import com.jetbrains.kmpapp.screens.list.ListScreen
import kotlinx.serialization.Serializable

@Serializable
object TemplatePage

@Serializable
object ListDestination

@Serializable
data class DetailDestination(val objectId: Int)

@Serializable
object MainPage

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        val appNavController = rememberNavController()
        SharedTransitionLayout {
            NavHost(navController = appNavController, startDestination = MainPage) {
                composable<MainPage> {
                    MainPage(appNavController)
                }
                composable<TemplatePage> {
                    TemplatePage(appNavController)
                }
            }
        }
    }
}

@Composable
fun TemplatePage(appNavController: NavHostController) {
    Surface {
        val navController: NavHostController = rememberNavController()
        NavHost(navController = navController, startDestination = ListDestination) {
            composable<ListDestination> {
                ListScreen(navigateToDetails = { objectId ->
                    navController.navigate(DetailDestination(objectId))
                })
            }
            composable<DetailDestination> { backStackEntry ->
                DetailScreen(
                    objectId = backStackEntry.toRoute<DetailDestination>().objectId,
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun MainPage(appNavController: NavHostController) {
    val currentDestination = rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val layoutType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(WindowAdaptiveInfoDefault)
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination.value,
                    onClick = { currentDestination.value = it }
                )
            }
        },
        layoutType = layoutType,
    ) { inerPadding ->
        when (currentDestination.value) {
            AppDestinations.HOME -> HomeDestination(appNavController, inerPadding)
            AppDestinations.FAVORITES -> FavoritesDestination(inerPadding)
            AppDestinations.SHOPPING -> ShoppingDestination(inerPadding)
            AppDestinations.PROFILE -> ProfileDestination(inerPadding)
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("home", Icons.Default.Home),
    FAVORITES("favorites", Icons.Default.Favorite),
    SHOPPING("shopping", Icons.Default.ShoppingCart),
    PROFILE("profile", Icons.Default.AccountBox),
}
