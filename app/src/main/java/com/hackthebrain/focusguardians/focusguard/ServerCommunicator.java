package com.hackthebrain.focusguardians.focusguard;

import org.java_websocket.client.WebSocketClient;

import java.util.ArrayList;

/**
 * Created by thomas on 6-6-15.
 */
public class ServerCommunicator
{

    protected WebSocketClient mWebSocketClient;
    protected ArrayList<MainActivity> listeners;

    public ServerCommunicator()
    {
        this.listeners = new ArrayList<MainActivity>();
    }

    public void addListener(MainActivity activity)
    {
        listeners.add(activity);
    }

    public void startListeningToServer()
    {
        connectWebSocket();
    }

    public void stopListeningToServer()
    {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
    }

    protected void connectWebSocket()
    {
//        URI uri;
//        try {
//            uri = new URI(WEBSOCKET_ADDRESS);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        mWebSocketClient = new WebSocketClient(uri, new Draft_17())
//        {
//            @Override
//            public void onOpen(ServerHandshake serverHandshake) {
//                Log.i("Websocket", "Opened");
//                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
//            }
//
//            @Override
//            public void onMessage(String s) {
//                final String message = s;
//                boolean isFocussed = false;
//
//                if (message.equals("isFocussed")) {
//                    isFocussed = true;
//                }
//
//                for (MainActivity listener : listeners) {
//                    listener.changeFocusState(isFocussed);
//                }
//            }
//
//            @Override
//            public void onClose(int i, String s, boolean b) {
//                Log.i("Websocket", "Closed " + s);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.i("Websocket", "Error " + e.getMessage());
//            }
//        };
        mWebSocketClient.connect();
    }

    public void setWebsocket(WebsocketClient websocket) {
        this.mWebSocketClient = websocket;
    }
}
