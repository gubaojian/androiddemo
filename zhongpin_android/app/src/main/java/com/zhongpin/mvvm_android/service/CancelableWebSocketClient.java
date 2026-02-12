package com.zhongpin.mvvm_android.service;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;

public abstract class CancelableWebSocketClient extends WebSocketClient {

    private boolean shouldShutClose = false;

    public CancelableWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public boolean isShouldShutClose() {
        return shouldShutClose;
    }

    public void setShouldShutClose(boolean shouldShutClose) {
        this.shouldShutClose = shouldShutClose;
    }
}
