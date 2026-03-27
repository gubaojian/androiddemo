package com.example.composelearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composelearn.ui.theme.ComposeLearnTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeLearnTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column() {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                        WaterCounter()
                        Parent()
                    }
                }
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

@Composable
fun WellnessTaskItem(
    taskName: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f).padding(start = 16.dp),
            text = taskName
        )
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}

@Composable
fun WaterCounter() {
    Column(modifier = Modifier.padding(16.dp)) {
        var count by remember { mutableStateOf(0) }
        if (count > 0) {
            var showTask by remember { mutableStateOf(true) }
            if (showTask) {
                WellnessTaskItem(
                    onClose = {
                        showTask = false
                    },
                    taskName = "Have you taken your 15 minute walk today?"
                )
            }
            Text("You've had $count glasses.")
        }

        Button(onClick = { count++ }, enabled = count < 10) {
            Text(
                "Add one",
            )
        }
        Button(
            onClick = { count = 0 },
            Modifier.padding(start = 8.dp)) {
            Text("Clear water count")
        }
        Text(
            "Test",
            modifier = Modifier.clickable {
                count++
            }
        )
    }
}

@Composable
fun ProblematicTimer(onFinish: () -> Unit) {
    val currentOnFinish by rememberUpdatedState(onFinish)
    LaunchedEffect(Unit) {
        delay(3000)
        onFinish() // 永远是第一次传入的回调，后续更新无效
    }
}

@Composable
fun Parent() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var count by remember { mutableStateOf(0) }
    ProblematicTimer(
        onFinish = {
            // 这里永远打印初始的 count=0
            println("Count: $count")
            count++
        }
    )
    ModalNavigationDrawer(
        drawerState = scaffoldState,
        drawerContent = {
            Button(onClick = {
               scope.launch {
                   scaffoldState.close()
               }
            }) { Text("close") }
        }
    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding()
        ) { innerPadding ->
                Button(onClick = {
                    scope.launch {
                        scaffoldState.open()
                    }
                }) { Text("Add") }
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