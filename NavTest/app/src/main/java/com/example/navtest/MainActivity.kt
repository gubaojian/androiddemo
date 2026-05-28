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
data object HomeRoute : NavKey

@Serializable
data class UserProfile(var userId: String, var userName:String): NavKey

@Serializable
data class DetailRoute(var id:String): NavKey

@Composable
fun MainEntry() {
    val backStack = remember { mutableStateListOf<NavKey>(HomeRoute) }
    NavDisplay(
        backStack = backStack,
        onBack = {backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeRoute> { Home(backStack) }
            entry<UserProfile> { it -> UserProfileScreen(it, backStack) }
            entry<DetailRoute> { DetailRouteScreen(it, backStack) }
        }
    )
}

@Composable
fun Home(navStack: MutableList<NavKey>) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column() {
            Box(
                modifier = Modifier.padding(innerPadding)
            )
            Button(onClick = {
                navStack.add(UserProfile(userId = "222", userName = "gubaojian"))
            }) {
                Text("to user screen page")
            }
            Button(onClick = {
                navStack.add(DetailRoute(id = "1111"))
            }) {
                Text("to detail screen page")
            }
        }
    }
}

@Composable
fun UserProfileScreen(data: UserProfile, navStack: MutableList<NavKey>) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column {
            Box(
                modifier = Modifier.padding(innerPadding)
            )
            Text(text = "user profile screen ${data.userId}")
            Button(onClick = {
                navStack.removeLastOrNull()
            }) {
                Text("Back")
            }
        }
    }
}


@Composable
fun DetailRouteScreen(data: DetailRoute, navStack: MutableList<NavKey>) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column() {
            Box(
                modifier = Modifier.padding(innerPadding)
            )
            Text(text = "detail screen ${data.id}")
            Button(onClick = {
                navStack.removeLastOrNull()
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