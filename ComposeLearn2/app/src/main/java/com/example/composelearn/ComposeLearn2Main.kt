package com.example.composelearn

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.composelearn.ui.theme.ComposeLearnTheme

class ComposeLearn2Main : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLearnTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column() {
                        Text("hello world")
                    }
                }
            }
        }
    }

}