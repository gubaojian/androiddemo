package com.example.testflow

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
                Scaffold(
                    bottomBar = {
                        BottomAppBar(
                            actions = {
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(Icons.Filled.Check, contentDescription = "Localized description")
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Localized description",
                                    )
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Mic,
                                        contentDescription = "Localized description",
                                    )
                                }
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        Icons.Filled.Image,
                                        contentDescription = "Localized description",
                                    )
                                }
                            },
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = { /* do something */ },
                                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                                ) {
                                    Icon(Icons.Filled.Add, "Localized description")
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
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
        Button(onClick = {}) { Text("Button") }
        Button(
            onClick = { /* Do something! */ }
        ) {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Localized description",
                modifier = Modifier.size(ButtonDefaults.MinHeight),
            )
            Spacer(Modifier.size(ButtonDefaults.MinHeight))
            Text("Like")
        }


        TextButton(onClick = {}) { Text("Text Button") }


        OutlinedButton(onClick = { /* Do something! */ }) { Text("Outlined Button") }



        ElevatedButton(onClick = { /* Do something! */ }) { Text("Elevated Button") }


        FilledTonalButton(onClick = { /* Do something! */ }) { Text("Filled Tonal Button") }

        Text(
            "AppBar Test",
            style = TextStyle(
                fontSize = 12.sp
            )
        )
        TopAppBar(
            title = {
                Text(
                    "AppBar Test",
                    style = TextStyle(
                        fontSize = nonScaledSp(12.0f)
                    )
                )
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

@Composable
fun nonScaledSp(size: Float): TextUnit {
    val fontScale = LocalDensity.current.fontScale
    return (size / fontScale).sp
}

@Composable
fun keyboardAsState(): MutableState<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    val view = LocalView.current
    val viewTreeObserver = view.viewTreeObserver
    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            keyboardState.value = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose { viewTreeObserver.removeOnGlobalLayoutListener(listener) }
    }
    return keyboardState
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestFlowTheme {
        Greeting("Android")
    }
}