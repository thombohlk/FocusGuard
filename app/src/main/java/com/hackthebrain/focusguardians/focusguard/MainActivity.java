package com.hackthebrain.focusguardians.focusguard;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.java_websocket.drafts.Draft_17;

import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends ActionBarActivity
{
//    public static final String WEBSOCKET_ADDRESS = "ws://192.168.10.35:8080";
    public static final String WEBSOCKET_ADDRESS = "ws://192.168.10.33:8080";
//    public static final String WEBSOCKET_ADDRESS = "ws://192.168.0.15:8080";

    protected InterruptionController interruptionController;
    protected ServerCommunicator serverCommunicator;
    protected View view;
    protected TextView txtStatus;
    protected ToggleButton guardToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        view = this.getWindow().getDecorView();
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        guardToggle = (ToggleButton) findViewById(R.id.guardToggle);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Launch settings activity
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
        }
        return true;
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
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String ip = SP.getString("socketServerIp", "NA");
        String port = SP.getString("socketServerPort", "NA");
        String socketServerIp = "ws://" + ip + ":" + port;

        URI uri = null;
        try {
            uri = new URI(socketServerIp);
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
        changeBackgroundColor(Color.GREEN);
        serverCommunicator = createServerCommunicator();
        serverCommunicator.startListeningToServer();
    }

    public void stopGuarding()
    {
        interruptionController.setInterruptionMode(false);
        serverCommunicator.stopListeningToServer();
        changeBackgroundColor(Color.WHITE);
        txtStatus.setText(R.string.quote);
        guardToggle.setChecked(false);
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
        colorAnimation.setDuration(700);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((Integer)animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }
}
