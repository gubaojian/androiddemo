package com.zhongpin.mvvm_android.service

import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import com.blankj.utilcode.util.NotificationUtils
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.Utils

object UserNotificationUtil {
    fun  startService() {
        try {
            val intent = Intent(Utils.getApp(), NotificationService::class.java)
            intent.putExtra(NotificationService.SERVICE_ACTION, NotificationService.SERVICE_ACTION_START)
            ServiceUtils.startService(intent)
        } catch (e:Exception) {
            Log.d("NotificationService", "NotificationService startService", e)
        }
    }

    fun stopService() {
        try {
            val intent = Intent(Utils.getApp(), NotificationService::class.java)
            intent.putExtra(
                NotificationService.SERVICE_ACTION,
                NotificationService.SERVICE_ACTION_STOP
            )
            ServiceUtils.stopService(intent)
        }catch (e:Exception) {
                Log.d("NotificationService", "NotificationService stopService", e)
        }
    }

    fun initChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = Utils.getApp().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(NotificationUtils.ChannelConfig.DEFAULT_CHANNEL_CONFIG.getNotificationChannel())
        }
    }
}