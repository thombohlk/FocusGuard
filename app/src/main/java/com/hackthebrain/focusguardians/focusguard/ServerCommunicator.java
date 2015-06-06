package com.hackthebrain.focusguardians.focusguard;

import org.java_websocket.client.WebSocketClient;

import java.util.ArrayList;

/**
 * Created by thomas on 6-6-15.
 */
public class ServerCommunicator
{
    protected WebSocketClient websocket;
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
        if (websocket != null) {
            websocket.close();
        }
    }

    protected void connectWebSocket()
    {
        websocket.connect();
    }

    public void setWebsocket(WebsocketClient websocket) {
        this.websocket = websocket;
    }
}
