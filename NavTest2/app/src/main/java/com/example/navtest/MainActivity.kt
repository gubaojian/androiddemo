package com.example.navtest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.core.WheelTextPicker
import com.example.navtest.ui.theme.NavTestTheme
import com.jvziyaoyao.scale.image.viewer.ImageViewer
import com.jvziyaoyao.scale.zoomable.zoomable.ZoomableView
import com.jvziyaoyao.scale.zoomable.zoomable.rememberZoomableState
import kotlinx.coroutines.launch
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



/**优惠券配置：背景色、边框色、圆角、左右圆孔参数*/
data class CouponConfig(
    val backgroundColor: Color = Color(0xFFFFF4F4),
    val dashColor: Color = Color(0xFFFFDFDF),
    val borderColor: Color = Color(0xFFFFDBDB),
    val borderWidth: Dp = 0.5.dp,
    val cornerRadius: Dp = 12.dp,
    val notchRadius: Dp = 4.dp,      // 凹口“视觉半径”
    val dividerRatio: Float = 0.35f,
)

fun Modifier.voucherCouponBg(config: CouponConfig): Modifier = this.then(
    Modifier.drawBehind {

        val corner = config.cornerRadius.toPx()
        val notch = config.notchRadius.toPx()
        val strokeWidth = config.borderWidth.toPx()

        val w = size.width
        val h = size.height
        val centerX = 96.dp.toPx()

        val path = Path().apply {

            // =========================
            // ✅ 左上开始
            // =========================
            moveTo(corner, 0f)

            // 左上角
            quadraticBezierTo(0f, 0f, 0f, corner)

            // 左边
            lineTo(0f, h - corner)

            // 左下角
            quadraticBezierTo(0f, h, corner, h)

            // =========================
            // ✅ 底边 → 到凹口前
            // =========================
            lineTo(centerX - 2*notch, h)

            // ✅ 底部凹口（Bezier圆弧）
            cubicTo(
                centerX - notch, h,
                centerX - notch, h - notch,
                centerX, h - notch
            )
            cubicTo(
                centerX + notch, h - notch,
                centerX + notch, h,
                centerX + 2*notch, h
            )

            // =========================
            // ✅ 右下角
            // =========================
            lineTo(w - corner, h)
            quadraticBezierTo(w, h, w, h - corner)

            // =========================
            // ✅ 右边
            // =========================
            lineTo(w, corner)

            // 右上角
            quadraticBezierTo(w, 0f, w - corner, 0f)

            // =========================
            // ✅ 顶边 → 到上凹口
            // =========================
            lineTo(centerX + 2*notch, 0f)

            // ✅ 顶部凹口（Bezier圆弧）
            cubicTo(
                centerX + notch, 0f,
                centerX + notch, notch,
                centerX, notch
            )
            cubicTo(
                centerX - notch, notch,
                centerX - notch, 0f,
                centerX - 2*notch, 0f
            )

            // =========================
            // ✅ 回到起点
            // =========================
            lineTo(corner, 0f)

            close()
        }

        // =========================
        // ✅ 画背景
        // =========================
        drawPath(
            path = path,
            color = config.backgroundColor
        )

        // =========================
        // ✅ 画边框（沿路径）
        // =========================
        drawPath(
            path = path,
            color = config.borderColor,
            style = Stroke(width = strokeWidth)
        )

        // =========================
        // ✅ 画虚线
        // =========================
        drawLine(
            color = config.dashColor,
            start = Offset(centerX, 8.dp.toPx()),
            end = Offset(centerX, h - 8.dp.toPx()),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(10f, 8f)
            )
        )
    }
)



@Composable
fun CouponCard(
    modifier: Modifier = Modifier,
    width: Dp = 320.dp,
    height: Dp = 120.dp,
    config: CouponConfig = CouponConfig(),
    money: String = "50",
    title: String = "满100减50",
    tip: String = "全场通用 · 30天有效"
) {

    Box(
        modifier = Modifier
            .padding(16.dp)
            .height(95.dp)
            .fillMaxWidth()
            .voucherCouponBg(CouponConfig())
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFFFDEA8),
                    shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                )
                .width(58.dp)
                .height(16.dp)
                .padding(horizontal = 24.dp, vertical = 9.dp)
        ) {
            Text(
                text = "Cashback",
                fontSize = 10.sp,
                color = Color(0xFF7C4710),
                fontWeight = FontWeight.Bold
            )
        }


    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCoupon() {
    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CouponCard()
        Spacer(Modifier.height(20.dp))
        CouponCard(
            money = "80",
            title = "满200减80",
            tip = "美食专享券"
        )
    }
}

@Composable
fun Home(navStack: NavController) {
    val tabs = listOf("首页", "消息", "我的", "设置")

    var pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Button(onClick = {
                context.startActivity(Intent(context, TestActivity::class.java))
            }) {
                Text("hello world back top bar")
            }
        },
        bottomBar = {
            Button(onClick = {}) {
                Text("hello world back bottom bar")
            }
        }
    ) { innerPadding ->
        Column(
           modifier =  Modifier
               .padding(innerPadding)
               .verticalScroll(state = rememberScrollState())
        ) {
            Button(onClick = {
                context.startActivity(Intent(context, TestActivity::class.java))
            }) {
                Text("hello world back top bar")
            }
            Button(onClick = {
                navStack.navigate(NavRoute.UserProfile(userId = "222", userName = "gubaojian"))
            }) {
                Text("to user screen page ${innerPadding.calculateTopPadding()} ${innerPadding.calculateBottomPadding()}")
            }
            Button(onClick = {
                navStack.navigate(NavRoute.DetailRoute(id = "1111"))
            }) {
                Text("to detail screen page")
            }
            AsyncImage(
                modifier = Modifier.size(100.dp).background(color = Color.Red),
                model = "https://img.alicdn.com/bao/uploaded/i3/2222383086404/O1CN013Duay31xB5YBCjuPb_!!2222383086404.jpg_460x460q90.jpg_.webp",
                contentDescription = null,
            )
            WheelTextPicker(
                texts = listOf("hello", "world", "dd", "222"),
                rowCount = 3,
                size = DpSize(128.dp, 120.dp),
            )
            WheelDatePicker(
            )
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        height = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                       }
                    ) {
                        Text(text = title, modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
            HorizontalPager(
                state = pagerState,
                pageContent = { index->
                    Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                        Text("${index}", modifier = Modifier.align(Alignment.Center))
                    }
                }
            )

            DropdownMenuSample()

            TestPage()
            RedeemBottomSheetContent()

        }
    }
}

@Composable
fun DropdownMenuSample() {

    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        Button(
            onClick = {
                expanded = true
            }
        ) {
            Text("Show Menu")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text("Edit")
                },
                onClick = {
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text("Delete")
                },
                onClick = {
                    expanded = false
                }
            )
        }
    }
}
@Composable
fun UserProfileScreen(data: NavRoute.UserProfile, navStack: NavController) {
    var pagerState = rememberPagerState(pageCount = {3})
    val scope = rememberCoroutineScope()
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
            HorizontalPager(
                state = pagerState,
                pageContent = { index ->
                    val imagePainter = painterResource(R.drawable.test)
                    val state = rememberZoomableState(contentSize = imagePainter.intrinsicSize)
                    ImageViewer(
                        state = state,
                        model = painterResource(id = R.drawable.test),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailRouteScreen(data: NavRoute.DetailRoute, navStack: NavController) {
    var pagerState = rememberPagerState(pageCount = {3})
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
            HorizontalPager(
                state = pagerState,
                pageContent = { index ->
                    /**
                    ImageViewer {
                        Image(
                            painter = painterResource(R.drawable.test),
                            contentDescription = "Sample Image",
                            contentScale = ContentScale.FillBounds,
                        )
                    }*/
                }
            )
        }
    }

    BasicAlertDialog(
        onDismissRequest = {

        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column() {
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
        MainEntry()
    }
}