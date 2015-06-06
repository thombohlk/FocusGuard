package com.hackthebrain.focusguardians.focusguard;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
//    public static final String WEBSOCKET_ADDRESS = "ws://192.168.10.35:8080";
    public static final String WEBSOCKET_ADDRESS = "ws://192.168.0.15:8080";

    protected InterruptionController interruptionController;
    protected ServerCommunicator serverCommunicator;
    protected View view;
    protected TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = this.getWindow().getDecorView();
        txtStatus = (TextView) findViewById(R.id.txtStatus);

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
        changeFocusState(false);
        serverCommunicator = createServerCommunicator();
        serverCommunicator.startListeningToServer();
    }

    public void stopGuarding()
    {
        interruptionController.setInterruptionMode(false);
        serverCommunicator.stopListeningToServer();
        changeBackgroundColor(Color.WHITE);
        txtStatus.setText(R.string.quote);
    }

    public void changeFocusState(boolean isFocussed) {
        interruptionController.setInterruptionMode(isFocussed);
        if (isFocussed) {
            changeBackgroundColor(Color.RED);
            txtStatus.setText(R.string.focusModeText);
        } else {
            changeBackgroundColor(Color.GREEN);
            txtStatus.setText(R.string.nonFocusModeText);
        }
    }

    protected void changeBackgroundColor(int colorTo)
    {
        int colorFrom = Color.TRANSPARENT;
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            colorFrom = ((ColorDrawable) background).getColor();
        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(300);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((Integer)animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }
}
