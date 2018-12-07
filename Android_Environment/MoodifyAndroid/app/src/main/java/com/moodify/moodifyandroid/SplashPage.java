package com.moodify.moodifyandroid;

// Class that exists for no other reason than to show of the App logo for a few seconds on startup

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);
        CountDownTimer splashTimer = new CountDownTimer(1000,20) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent i = new Intent(SplashPage.this, CreatePlaylist.class);
                startActivity(i);
            }
        };

        splashTimer.start();
    }
}
