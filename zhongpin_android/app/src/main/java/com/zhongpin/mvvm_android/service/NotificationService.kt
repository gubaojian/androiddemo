package com.zhongpin.mvvm_android.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.blankj.utilcode.util.NotificationUtils
import com.zhilianshidai.pindan.app.R
import com.zhongpin.lib_base.utils.EventBusUtils
import com.zhongpin.lib_base.utils.LogUtils
import com.zhongpin.mvvm_android.ui.main.MainActivity
import com.zhongpin.mvvm_android.bean.LoginEvent
import com.zhongpin.mvvm_android.common.utils.Constant
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.atomic.AtomicInteger


class NotificationService : Service() {

    private val orderMessageId = AtomicInteger(1000);
    private val webSocketManager = WebSocketManager();
    init {
        Log.d("NotificationService", "NotificationService Constructor")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationService", "NotificationService onCreate")
    }

    override fun onTimeout(startId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
        super.onTimeout(startId)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NotificationService", "NotificationService onStartCommand enter action "
                + intent?.getStringExtra(NotificationService.SERVICE_ACTION))
        try {
            if (intent != null) {
                val action = intent.getStringExtra(NotificationService.SERVICE_ACTION);
                if (NotificationService.SERVICE_ACTION_START.equals(action)) {
                    compatForForegroundService()
                    webSocketManager.stopWebSocket()
                    webSocketManager.startWebSocket()
                    EventBusUtils.unRegister(this)
                    EventBusUtils.register(this)
                } else if (NotificationService.SERVICE_ACTION_STOP.equals(action)) {
                    //close websocket
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        stopForeground(Service.STOP_FOREGROUND_REMOVE)
                    }
                    EventBusUtils.unRegister(this)
                    webSocketManager.stopWebSocket();
                }
            }
            super.onStartCommand(intent, flags, startId);
        } catch (e:Throwable) {
            LogUtils.e("NotificationService", "onStartCommand" + e.message)
        }
        return Service.START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d("NotificationService", "NotificationService stopped")
        EventBusUtils.unRegister(this)
        webSocketManager.stopWebSocket();
        super.onDestroy()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderMessageEvent(loginEvent : LoginEvent){

    }

    private fun compatForForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification: Notification = NotificationUtils.getNotification(
                NotificationUtils.ChannelConfig.DEFAULT_CHANNEL_CONFIG, { param ->
                    val intent = Intent(baseContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    param.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("众品")
                        .setContentText("众品通知服务")
                        .setContentIntent(
                            PendingIntent.getActivity(
                                baseContext,
                                0,
                                intent,
                                PendingIntent.FLAG_IMMUTABLE
                            )
                        )
                        .setAutoCancel(false)
                }
            )
            startForeground(1, notification)
        }
    }

    private fun sendNotification() {
        if (!NotificationUtils.areNotificationsEnabled()){
            return
        }
        /**
        NotificationUtils.notify(orderMessageId.incrementAndGet(), {
                param ->
            val intent = Intent(baseContext, MainActivity::class.java)
            intent.putExtra("selectTab", Constant.NOTIFY)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            param.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("订单通知")
                .setContentText("订单通知描述")
                .setContentIntent(PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setVibrate(
                    longArrayOf(
                        100,
                        200,
                        300,
                        400,
                        500,
                        400,
                        300,
                        200,
                        400
                    )
                )
                .setAutoCancel(true)
        })*/
    }


    companion object  {
        val SERVICE_ACTION  = "serviceAction";
        val SERVICE_ACTION_STOP = "stop";
        val SERVICE_ACTION_START = "start";
    }
}