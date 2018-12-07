package com.moodify.moodifyandroid;

/*
*  TITLE: SpotifyLoginActivity.java
*  Authors: Miguel Gotao and Scott Weller
*
*   Helper class used by Spotify Android SDK to authenticate
*   device
*
 */

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SpotifyLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_login);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();

        if (uri != null) {
            AuthenticationResponse response = AuthenticationResponse.fromUri(uri);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    break;

                // Auth flow returned an error
                case ERROR:
                    break;
                // Most likely auth flow was cancelled
                default:

            }
    }
}}
