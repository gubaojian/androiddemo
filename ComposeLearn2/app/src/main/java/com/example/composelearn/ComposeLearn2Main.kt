package com.example.composelearn

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateRect
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.composelearn.ui.theme.ComposeLearnTheme

class ComposeLearn2Main : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLearnTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column() {
                        Text("hello world")
                        HelloAnimation()
                        HelloAnimation2()
                        HelloWorld()
                        HelloWorld2()
                        HelloWorld3()
                    }
                }
            }
        }
    }

}

@Composable
fun HelloAnimation() {
    var expandState by remember { mutableStateOf(false) }
    Box(modifier =
        Modifier
            .background(Color.Blue)
            .animateContentSize()
            .clickable() {
                expandState = !expandState
            }
    ) {
        Column() {
            Text("hello world")
            if (expandState) {
                Text("hello world expand animation")
            }
        }
    }
}
@Composable
fun HelloAnimation2() {
    var animationState by remember { mutableStateOf(false) }
    Column() {
        Box(modifier = Modifier
            .background(Color.Blue)
            .clickable() {
                animationState = !animationState
            }) {
            Text("hello world")
        }
        AnimatedVisibility(animationState) {
            Box(modifier = Modifier.background(Color.Red)) {
                Text("hello world animatedvisibility ${animationState}")
            }
        }
    }
}

@Composable
fun HelloWorld() {
    var startAnimation by remember { mutableStateOf(false) }
    val animationAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.1f,
        animationSpec = tween(
            durationMillis = 3000
        )
    )
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .alpha(animationAlpha)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan)) {
            Button(onClick = {
                startAnimation = !startAnimation
            }) {
                Text("start animation")
            }
        }
    }
}

@Composable
fun HelloWorld2() {
    var stateAnimation by remember { mutableStateOf(false) }
    val transition = updateTransition(stateAnimation)
    val rect by transition.animateRect(
        transitionSpec =  {
            tween(durationMillis = 3000)
        }
    ) { state ->
        if (state) {
            Rect(0f,0f, 400f, 100f)
        } else {
            Rect(0f,0f, 200f, 50f)
        }
    }
    val color by transition.animateColor(
        transitionSpec =  {
            tween(durationMillis = 3000)
        }
    ) { state ->
         if (state) {
             Color.Yellow
         } else {
             Color.Blue
         }
    }
    Box(modifier = Modifier
        .height(rect.height.dp)
        .width(rect.width.dp)
        .background(color)
    ) {
        Button(onClick = {
            stateAnimation = !stateAnimation
        }) {
            Text("start animation")
        }
    }
}

@Composable
fun HelloWorld3() {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        //对话框示例 https://composables.com/blog/dialogs
    }
    Box(modifier = Modifier
        .height(50.dp)
        .fillMaxWidth()
        .background(Color.Red)
    ) {
        Button(onClick = {
            showDialog = !showDialog
        }) {
            Text("show dialog")
        }
    }
    if (showDialog) {
        // 基础 Dialog 组件，完全自定义内容
        Dialog(onDismissRequest = { showDialog = false }) {
            // 自定义弹窗布局
            Column(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "自定义对话框", modifier = Modifier.padding(bottom = 16.dp))
                Text(text = "支持任意布局、样式、组件", modifier = Modifier.padding(bottom = 24.dp))

                Button(onClick = { showDialog = false }) {
                    Text("关闭弹窗")
                }
            }
        }
    }
}