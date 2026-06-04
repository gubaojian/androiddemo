package com.example.navtest

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navtest.ui.theme.NavTestTheme


@Composable
fun VoucherAppliedTextTag() {
    Box(modifier = Modifier.size(68.dp)) {
        Image(
            modifier = Modifier.size(68.dp),
            painter = painterResource(R.drawable.voucher_applied_tag_bg),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .width(68.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    rotationZ = -45f
                    translationX = 8.dp.toPx()
                    translationY = 8.dp.toPx()
                },
            text = "Applied",
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight(800),
                color = Color(0xFFA05D0C),
                textAlign = TextAlign.Center,
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VoucherAppliedTextTagContentPreview() {
    NavTestTheme {
        VoucherAppliedTextTag()
    }
}