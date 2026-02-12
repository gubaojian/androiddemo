package com.zhongpin.mvvm_android.service

import android.os.Handler
import android.os.Looper
import com.blankj.utilcode.util.ThreadUtils
import com.zhongpin.lib_base.utils.LogUtils
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class WebSocketManager {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val clientUri = URI("ws://192.168.110.191:9001/wsg?role=client&appId=434608808308&appToken=mtnXNik41BYaUSNgLDxWmxoDCmUyl9El");

    private var shouldRetry = true;
    @Volatile
    private  var websocketClient: CancelableWebSocketClient? = null;
    private var retryRunnable : Runnable? = null;

    @Synchronized
    fun startWebSocket() {
        retryRunnable?.let {
            mainHandler.removeCallbacks(it)
        }
        if (websocketClient != null) {
            shouldRetry = false;
            websocketClient?.setShouldShutClose(true);
            websocketClient?.close();
            websocketClient = null
        }
        shouldRetry = true;
        websocketClient = object : CancelableWebSocketClient(clientUri) {
            override fun onOpen(serverHandshake: ServerHandshake) {
                LogUtils.d("WebSocketManager", "WebSocketManager onopen " + serverHandshake)
            }

            override fun onMessage(s: String) {

            }

            override fun onClose(code: Int, s: String, b: Boolean) {
                LogUtils.d("WebSocketManager", "WebSocketManager onclose code "
                        + code + " should retry " + !isShouldShutClose() + " " + Thread.currentThread().name)
                if (shouldRetry && !isShouldShutClose()) {
                    retryConnectWebSocketLater();
                }
            }

            override fun onError(e: Exception) {
                if (shouldRetry) {
                    retryConnectWebSocketLater();
                }
            }
        };
        websocketClient?.connect()
    }

    @Synchronized
    fun stopWebSocket() {
        shouldRetry = false;
        websocketClient?.setShouldShutClose(true);
        websocketClient?.close();
        websocketClient = null;
        retryRunnable?.let {
            mainHandler.removeCallbacks(it)
        }
    }

    private fun retryConnectWebSocketLater() {
        stopWebSocket()
        retryRunnable = Runnable {
            ThreadUtils.getSinglePool().execute{
                startWebSocket()
            }
        };
        retryRunnable?.let {
            mainHandler.postDelayed(it, 20000 + (60*1000*Math.random()).toLong())
        }
    }
}