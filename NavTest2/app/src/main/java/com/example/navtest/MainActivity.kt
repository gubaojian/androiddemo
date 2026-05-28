package com.example.navtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEventHistory
import com.example.navtest.ui.theme.NavTestTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavTestTheme {
                MainEntry()
            }
        }
    }
}

@Serializable
sealed class NavRoute {
    @Serializable
    data object HomeRoute: NavRoute()

    @Serializable
    data class UserProfile(val userId: String, val userName:String): NavRoute()
    @Serializable
    data class DetailRoute(val id:String): NavRoute()
}

@Composable
fun MainEntry() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = NavRoute.HomeRoute) {
        composable<NavRoute.HomeRoute> { Home(navController) }
        composable<NavRoute.UserProfile> { backStackEntry ->
            val route: NavRoute.UserProfile = backStackEntry.toRoute()
            UserProfileScreen(route, navController)
        }

        composable<NavRoute.DetailRoute> { backStackEntry ->
            val route: NavRoute.DetailRoute = backStackEntry.toRoute()
            DetailRouteScreen(route, navController)
        }
    }

}

@Composable
fun Home(navStack: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column() {
            Box(
                modifier = Modifier.padding(innerPadding)
            )
            Button(onClick = {
                navStack.navigate(NavRoute.UserProfile(userId = "222", userName = "gubaojian"))
            }) {
                Text("to user screen page")
            }
            Button(onClick = {
                navStack.navigate(NavRoute.DetailRoute(id = "1111"))
            }) {
                Text("to detail screen page")
            }
        }
    }
}

@Composable
fun UserProfileScreen(data: NavRoute.UserProfile, navStack: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column {
            Box(
                modifier = Modifier.padding(innerPadding)
            )
            Text(text = "user profile screen ${data.userId}")
            Button(onClick = {
                navStack.popBackStack()
            }) {
                Text("Back")
            }
        }
    }
}


@Composable
fun DetailRouteScreen(data: NavRoute.DetailRoute, navStack: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column() {
            Box(
                modifier = Modifier.padding(innerPadding)
            )
            Text(text = "detail screen ${data.id}")
            Button(onClick = {
                navStack.popBackStack()
            }) {
                Text("Back")
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NavTestTheme {
        Greeting("Android")
    }
}