package com.zhongpin.mvvm_android.ui.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.net.toUri

object ApkDownloadUtil {

    /**
     * 通过系统浏览器下载APK
     * @param context 上下文
     * @param downloadUrl APK下载地址（必须是http/https协议）
     */
    fun downloadByBrowser(context: Context?, downloadUrl: String?) {
        // 空参数校验
        if (context == null || downloadUrl.isNullOrEmpty()) {
            showToast(context, "参数错误")
            return
        }

        // 校验URL合法性（仅检查http/https前缀，更高效）
        if (!downloadUrl.startsWith("http://", ignoreCase = true) 
            && !downloadUrl.startsWith("https://", ignoreCase = true)) {
            showToast(context, "下载链接必须是http/https协议")
            return
        }

        try {
            // 创建浏览器意图
            val intent = Intent(Intent.ACTION_VIEW, downloadUrl.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            showToast(context, "下载失败：${e.message ?: "未知错误"}")
        }
    }

    /**
     * 确保在主线程显示Toast
     */
    private fun showToast(context: Context?, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
    