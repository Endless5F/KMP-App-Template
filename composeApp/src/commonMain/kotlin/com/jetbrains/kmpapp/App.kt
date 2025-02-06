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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jetbrains.kmpapp.compose.adaptive.NavigationSuiteDefaults
import com.jetbrains.kmpapp.compose.adaptive.NavigationSuiteItemColors
import com.jetbrains.kmpapp.compose.adaptive.NavigationSuiteScaffold
import com.jetbrains.kmpapp.compose.adaptive.NavigationSuiteScaffoldDefaults
import com.jetbrains.kmpapp.compose.adaptive.WindowAdaptiveInfoDefault
import com.jetbrains.kmpapp.compose.index.FavoritesDestination
import com.jetbrains.kmpapp.compose.index.HomeDestination
import com.jetbrains.kmpapp.compose.index.ProfileDestination
import com.jetbrains.kmpapp.compose.index.ShoppingDestination
import com.jetbrains.kmpapp.compose.museum.DetailScreen
import com.jetbrains.kmpapp.compose.museum.ListScreen
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
    val navigationItemColors = defaultNavigationItemColors()
//    val navigationItemColors = when (currentDestination.value) {
//        AppDestinations.HOME -> homeNavigationItemColors()
//        else -> defaultNavigationItemColors()
//    }
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
//                    colors = navigationItemColors,
                    label = { Text(it.label) },
                    selected = it == currentDestination.value,
                    onClick = { currentDestination.value = it }
                )
            }
        },
        layoutType = layoutType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = Color.Transparent,
            navigationRailContainerColor = Color.Transparent
        )
    ) { innerPadding ->
        when (currentDestination.value) {
            AppDestinations.HOME -> HomeDestination(appNavController, innerPadding)
            AppDestinations.FAVORITES -> FavoritesDestination(innerPadding)
            AppDestinations.SHOPPING -> ShoppingDestination(innerPadding)
            AppDestinations.PROFILE -> ProfileDestination(innerPadding)
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

@Composable
fun defaultNavigationItemColors(): NavigationSuiteItemColors {
    return NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            indicatorColor = Color.Transparent
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = Color.Transparent
        )
    )
}

@Composable
fun homeNavigationItemColors(
    selectedColor: Color = Color(0xFF232323),
    unselectedColor: Color = Color(0xFF757575)
): NavigationSuiteItemColors {
    return NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            indicatorColor = Color.Transparent,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            indicatorColor = Color.Transparent,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            selectedContainerColor = Color.Transparent,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor
        )
    )
}