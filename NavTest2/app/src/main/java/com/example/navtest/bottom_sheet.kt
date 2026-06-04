package com.example.navtest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.navtest.ui.theme.NavTestTheme


@Composable
fun RedeemBottomSheetContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {

        // ✅ 主卡片
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp) // 给顶部图标留空间
                .background(
                    color = Color(0xFFF6F1E8),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {

            // ✅ 标题
            Text(
                text = "We have a voucher for you!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8A5A00)
            )

            Spacer(Modifier.height(16.dp))

            // ✅ 中间卡片区
            VoucherCardSection()

            Spacer(Modifier.height(20.dp))

            // ✅ 描述
            Text(
                "We've detected a voucher you can redeem.",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "After payment is successful, please show to cashier to scan the code.",
                fontSize = 13.sp,
                color = Color(0xFF9C6B2F)
            )

            Spacer(Modifier.height(20.dp))

            // ✅ 按钮
            ActionButtons()
        }

        // ✅ 顶部浮动图标
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // 自己替换
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(90.dp)
                .offset(x = (-12).dp, y = (-10).dp)
        )
    }
}


@Composable
fun VoucherCardSection() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFEDE7DD),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // 左卡片
            LeftPromoCard(
                title = "Promo code\n5% OFF",
                bgColor = Color(0xFFFFA726),
                modifier = Modifier
                    .weight(1f).graphicsLayer {
                    rotationZ = -8f
                    transformOrigin = TransformOrigin(1f, 0f)
                }
            )
            Spacer(Modifier.width(12.dp))

            // 中间箭头
            Image(
               modifier = Modifier.padding(top = 8.dp),
               painter = painterResource(R.drawable.test),
                contentDescription = null
            )

            Spacer(Modifier.width(12.dp))

            // 右卡片
            SmallCard(
                title = "1090\npoints",
                bgColor = Color(0xFFFFF3E0),
                textColor = Color(0xFF2962FF),
                modifier = Modifier.weight(1f).graphicsLayer {
                    rotationZ = 8f
                    transformOrigin = TransformOrigin(0f, 0f)
                }
            )
        }
    }
}


@Composable
fun LeftPromoCard(
    title: String,
    bgColor: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(width = 1.dp, color = Color(0xFFFFF4D4), shape = RoundedCornerShape(size = 8.dp))
            .width(123.dp)
           .height(123.dp)
          .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 8.dp)),
          contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier
            .width(65.dp)
            .height(100.dp)
            .align(Alignment.Center)) {
            Image(
                modifier = Modifier
                    .width(65.dp)
                    .height(100.dp),
                painter = painterResource(R.drawable.rectangle34624432),
                contentDescription = null
            )
            Column(
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(
                    text = title,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun SmallCard(
    title: String,
    bgColor: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(0.8f)
            .background(
                color = bgColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = textColor,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedButton(
            onClick = {},
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50)
        ) {
            Text("Pay directly")
        }

        Button(
            onClick = {},
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50)
        ) {
            Text("Redeem and use")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RedeemBottomSheetContentPreview() {
    NavTestTheme {
        RedeemBottomSheetContent()
    }
}
