package com.example.composelearn

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Modifier.background(Color.Blue).animateContentSize().clickable() {
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
        Box(modifier = Modifier.background(Color.Blue).clickable() {
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