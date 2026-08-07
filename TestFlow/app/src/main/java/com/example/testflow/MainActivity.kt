package com.example.testflow

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.testflow.ui.theme.TestFlowTheme
import com.securepreferences.SecurePreferences
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val mainViewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            launch {
                mainViewModel.flowTest.collect {
                    Log.e("MainActivity", "MainActivity" + it)
                }
            }
            launch {
                mainViewModel.flow2.collect {
                    Log.e("MainActivity", "MainActivity flow" + it)
                }
            }
        }


        val context = applicationContext
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val secureSp = EncryptedSharedPreferences.create(
            context,
            "secure_sp",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val prefs: SharedPreferences =
            SecurePreferences(context, "userpassword", "my_user_prefs.xml")
        prefs.edit().putString("hello", "world").commit()

        setContent {
            TestFlowTheme {
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestFlowTheme {
        Greeting("Android")
    }
}