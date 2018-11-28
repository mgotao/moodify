package com.moodify.moodifyandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity {

    private int count = 0;
    private static final String CLIENT_ID = "70424a1a3b4047ceb03cbb0b4b9e762e";
    private static final String REDIRECT_URI = "com.moodify.moodifyandroid://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private TextView statusWindow;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusWindow = findViewById(R.id.statusWindow);
        statusWindow.setText("Not connected to API");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                        statusWindow.setText("Connected to API");

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {

                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            Log.d("MainActivity", track.name + " by " + track.artist.name);
                        }
                    }
                });
    }

    public void onPlaySongPressed(View view)
    {
        if(count==0) {
            mSpotifyAppRemote.getPlayerApi().play("spotify:track:5bTysxqGHXdSGiZVgljzCY");
            mSpotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(new Subscription.EventCallback<PlayerState>() {

                        public void onEvent(PlayerState playerState) {
                            final Track track = playerState.track;
                            if (track != null) {
                                statusWindow.setText("Track: "+track.name+"\nArtist: "+track.artist.name+"\nAlbum: "+track.album.name);
                            }
                        }
                    });
            count += 1;
        }
        else
        {
            mSpotifyAppRemote.getPlayerApi().resume();
            mSpotifyAppRemote.getPlayerApi().play("spotify:track:5bTysxqGHXdSGiZVgljzCY");
            mSpotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(new Subscription.EventCallback<PlayerState>() {

                        public void onEvent(PlayerState playerState) {
                            final Track track = playerState.track;
                            if (track != null) {
                                statusWindow.setText("Track: "+track.name+"\nArtist: "+track.artist+"\nAlbum: "+track.album);
                            }
                        }
                    });
        }
    }

    public void onPauseSongPressed(View view)
    {
        statusWindow.setText("Song paused");
        mSpotifyAppRemote.getPlayerApi().pause();
    }

}