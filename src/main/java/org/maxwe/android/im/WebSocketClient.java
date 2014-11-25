package org.maxwe.android.im;

import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by dingpengwei on 11/25/14.
 */
public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private OnWebSocketCallBack onWebSocketCallBack;

    public WebSocketClient(URI serverUri, OnWebSocketCallBack onWebSocketCallBack) {
        super(serverUri, new Draft_17());
        this.onWebSocketCallBack = onWebSocketCallBack;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.onWebSocketCallBack.onOpen();
    }

    @Override
    public void onMessage(String message) {
        this.onWebSocketCallBack.onMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        this.onWebSocketCallBack.onClose();
    }

    @Override
    public void onError(Exception exception) {
        this.onWebSocketCallBack.onError(exception);
    }

    interface OnWebSocketCallBack {
        public void onOpen();

        public void onMessage(String message);

        public void onClose();

        public void onError(Exception ex);
    }
}
