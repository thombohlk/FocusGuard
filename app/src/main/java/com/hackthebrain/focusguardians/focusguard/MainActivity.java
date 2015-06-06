package com.hackthebrain.focusguardians.focusguard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.java_websocket.drafts.Draft_17;

import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends Activity
{
    public static final String WEBSOCKET_ADDRESS = "ws://192.168.10.35:8080";

    protected InterruptionController interruptionController;
    protected ServerCommunicator serverCommunicator;
    protected View view;
    protected TextView console;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = this.getWindow().getDecorView();
        console = (TextView) findViewById(R.id.console);

        ToggleButton guardToggle = (ToggleButton) findViewById(R.id.guardToggle);
        guardToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startGuarding();
                } else {
                    stopGuarding();
                }
            }
        });

        interruptionController = createInterruptionController();
        serverCommunicator = createServerCommunicator();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        serverCommunicator.stopListeningToServer();
    }

    protected InterruptionController createInterruptionController()
    {
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        InterruptionController interruptionController = new InterruptionController();
        interruptionController.setAudioManager(audio);
        return interruptionController;
    }

    protected ServerCommunicator createServerCommunicator()
    {
        URI uri = null;
        try {
            uri = new URI(WEBSOCKET_ADDRESS);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }

        WebsocketClient websocket = new WebsocketClient(uri, new Draft_17());
        websocket.setActivity(this);

        ServerCommunicator serverCommunicator = new ServerCommunicator();
        serverCommunicator.addListener(this);
        serverCommunicator.setWebsocket(websocket);

        return serverCommunicator;
    }

    public void startGuarding()
    {
        serverCommunicator = createServerCommunicator();
        serverCommunicator.startListeningToServer();
        view.setBackgroundColor(Color.GREEN);
    }

    public void stopGuarding()
    {
        serverCommunicator.stopListeningToServer();
        view.setBackgroundColor(Color.WHITE);
    }

    public void changeFocusState(boolean isFocussed) {
        interruptionController.setInterruptionMode(isFocussed);
        if (isFocussed) {
            view.setBackgroundColor(Color.RED);
        } else {
            view.setBackgroundColor(Color.GREEN);
        }
    }
}
