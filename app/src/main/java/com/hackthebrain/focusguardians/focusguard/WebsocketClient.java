package com.hackthebrain.focusguardians.focusguard;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by thomas on 6-6-15.
 */
public class WebsocketClient extends WebSocketClient {

    protected MainActivity activity;

    public WebsocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public void setActivity(MainActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.i("Websocket", "Opened");
        send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
    }

    @Override
    public void onMessage(String s) {
        Log.i("Websocket", "Received " + s);
        final String message = s;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isFocussed = false;

                if (message.equals("true")) {
                    isFocussed = true;
                }

                activity.changeFocusState(isFocussed);
            }
        });
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Log.i("Websocket", "Closed " + s);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.stopGuarding();
            }
        });
    }

    @Override
    public void onError(Exception e) {
        Log.i("Websocket", "Error " + e.getMessage());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), "Connection to server failed", Toast.LENGTH_SHORT).show();
                activity.stopGuarding();
            }
        });
    }
}
