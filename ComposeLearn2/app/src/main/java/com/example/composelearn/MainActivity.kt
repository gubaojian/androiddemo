package com.example.composelearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdullahalhakimi.smoothmotion.animations.CircledDotsProgress
import com.abdullahalhakimi.smoothmotion.animations.DropCircleProgress
import com.abdullahalhakimi.smoothmotion.animations.LoadingDotsAnimation
import com.abdullahalhakimi.smoothmotion.animations.RotatingCircleProgress
import com.example.composelearn.ui.theme.ComposeLearnTheme
import com.king.ultraswiperefresh.NestedScrollMode
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.indicator.SwipeRefreshFooter
import com.king.ultraswiperefresh.indicator.SwipeRefreshHeader
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshFooter
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshHeader
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeLearnTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun GreetingCard(name: String, desc: String) {
    Row(
        modifier = Modifier.padding(20.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.clip(CircleShape).background(
                color = Color.Red
            )
        )
        Column(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            Text(
                text = "Hello $name!"
            )
            Text(
                text = "Hello $desc!"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val messages = listOf(
        "hello",
        "2",
        "3",
        "3",
        "hello",
        "2",
        "3",
        "3",
        "hello",
        "2",
        "3",
        "3",
        "hello",
        "2",
        "3",
        "3",
        "hello",
        "2",
        "3",
        "3",
        "hello",
        "2",
        "3",
        "3",
        "hello",
        "2",
        "3",
        "3"
    )
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val state = rememberUltraSwipeRefreshState()
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        GreetingCard(
            name = "gubaojian",
            desc = "desc"
        )
        DropCircleProgress(
            modifier = Modifier.size(90.dp),
            backgroundColor = Color.Gray.copy(alpha = 0.2f),
            color = MaterialTheme.colorScheme.primary,
        )
        CircledDotsProgress(
            modifier = Modifier.size(90.dp),
            backgroundColor = Color.Gray.copy(alpha = 0.2f),
            color = MaterialTheme.colorScheme.primary,
        )
        Box(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    coroutineScope.launch {
                        delay(1500)
                        isRefreshing = false
                    }
                },
                state = rememberPullToRefreshState()
            ) {
                LazyColumn() {
                    items(messages) { message ->
                        GreetingCard(name = message, desc = "desc ${message}")
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
        ) {
            UltraSwipeRefresh(
                state = state,
                refreshEnabled = true,
                onRefresh = {
                    state.isRefreshing = true
                    coroutineScope.launch {
                        delay(1500)
                        state.isRefreshing = false
                    }
                },
                onLoadMore = {
                    coroutineScope.launch {

                    }
                },
                headerScrollMode = NestedScrollMode.Translate,
                footerScrollMode = NestedScrollMode.Translate,
                headerIndicator = {
                    ClassicRefreshHeader(it)
                },
                footerIndicator = {
                    ClassicRefreshFooter(it)
                }
            ) {
                LazyColumn() {
                    items(messages) { message ->
                        GreetingCard(name = message, desc = "desc ${message}")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeLearnTheme {
        Greeting("Android")
    }
}