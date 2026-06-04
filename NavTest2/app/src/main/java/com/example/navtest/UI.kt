package com.example.navtest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherRedeemBasicDialog(
    showDialog: Boolean,
    onCancel: () -> Unit,
    onRedeem: () -> Unit
) {
    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = onCancel,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            // 弹窗内容卡片：背景+圆角
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                // 标题
                Text(
                    text = "Redeem this voucher with 1,000 points?",
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                // 说明文案
                Text(
                    text = "This voucher will be selected automatically after redemption.",
                    color = Color.Gray,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(28.dp))

                // 底部双按钮 左右分开
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 取消
                    TextButton(onClick = onCancel) {
                        Text(text = "Cancel", color = Color(0xFF0066FF), fontSize = 20.sp)
                    }
                    // 确认兑换
                    Button(
                        onClick = onRedeem,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005CFF)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Redeem", color = Color.White, fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

// 使用示例
@Composable
fun TestPage() {
    var show by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize(), Arrangement.Center, androidx.compose.ui.Alignment.CenterHorizontally) {
        Button({ show = true }) { Text("打开弹窗") }
    }

    VoucherRedeemBasicDialog(
        showDialog = show,
        onCancel = { show = false },
        onRedeem = { show = false }
    )
}

