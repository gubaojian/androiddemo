package com.example.testflow

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            SecurePreferences(context, "userpassword", "my_user_prefs")
        prefs.edit().putString("hello", "world").commit()

        prefs.getString("hello", "")?.let {
            Log.e("MainActivity",  "MainActivity back value " + it)
        }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Text(
            text = buildAnnotatedString {
                append("同意")
                withLink(
                    link = LinkAnnotation.Url(
                        url = "http://www.baidu.com",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                textDecoration = TextDecoration.None
                            )
                        ),
                        linkInteractionListener = { it ->
                            val url = it as LinkAnnotation.Url
                            url.url
                        }
                    ),
                ) {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Red
                        )
                    ) {
                        append("隐私协议")
                    }
                }
            }
        )
        HorizontalDivider(
            color = Color.Red
        )
        Box(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        ) {
            BadgedBox(
                badge = {
                    Badge(

                        modifier = Modifier.size(6.dp)
                    ) {}
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            }
        }
        Box(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        ) {
            BadgedBox(
                badge = {
                    Badge(
                    ) {
                        Text("100")
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            }
        }
        TopAppBar(
            title = {
                Text("AppBar Test")
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = Color.Red
            )
        )
        LargeTopAppBar(
            title = {
                Text("AppBar Test")
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = Color.Green
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestFlowTheme {
        Greeting("Android")
    }
}