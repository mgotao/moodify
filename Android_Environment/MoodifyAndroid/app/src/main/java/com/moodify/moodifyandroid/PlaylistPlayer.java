package com.moodify.moodifyandroid;

/*
*  TITLE: PlaylistPlayer.java
*  AUTHORS: Miguel Gotao and Scott Weller
*
*  Class which receives ArrayLis<String> of song URIs from CreatePlaylist
*  Activity, creates SpotifyRemoteApp object, and plays user playlist. This object
*  uses the services of the Spotify app installed on your phone to handle playback.
*  Currently the Spotify SDK does not support playback on devices which do not
*  have Spotify installed.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import java.util.ArrayList;


public class PlaylistPlayer extends AppCompatActivity {


    private int index = 0;
    private int count = 0;
    private Integer limit;
    private static final String CLIENT_ID = "70424a1a3b4047ceb03cbb0b4b9e762e";
    private static final String REDIRECT_URI = "com.moodify.moodifyandroid://callback";
    private ArrayList<String> tracklist;
    private SpotifyAppRemote mSpotifyAppRemote;
    private TextView statusWindow;
    private Button playButton;
    private Button buttonSkipPrevious;
    private Button buttonSkipNext;
    private ImageView songIcon;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_player);
        statusWindow = findViewById(R.id.statusWindow);
        Bundle bundle = getIntent().getExtras();
        tracklist = (ArrayList<String>) bundle.getStringArrayList("TRACKLIST");
        limit = getIntent().getIntExtra("LIMIT",0);
        playButton = findViewById(R.id.playButton);
        buttonSkipPrevious = findViewById(R.id.buttonSkipPrevious);
        buttonSkipNext = findViewById(R.id.buttonSkipNext);
        songIcon = findViewById(R.id.songIcon);
        buttonSkipPrevious.setEnabled(false);
        buttonSkipNext.setEnabled(false);
        playButton.setEnabled(false);

    }

    //Connection to API occurs here
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
                        Log.d("PlaylistPlayer", "Connected");
                        playButton.setEnabled(true);
                        buttonSkipNext.setEnabled(true);
                        // Now you can start interacting with App Remote
                        connected();


                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("PlaylistPlayer", throwable.getMessage(), throwable);
                    }
                });
    }

 

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        finish();
    }

    private void connected() {

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {

                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            Log.d("CreatePlaylist", track.name + " by " + track.artist.name);
                        }
                    }
                });
    }

    // Callback for play button
    public void onPlaySongPressed(View view)
    {
        PlaySong();
    }

    // Callback to skip to previous song in queue
    public void onSkipPrevious(View view)
    {
        count = 0;
        if(index == 1)
        {
            index-=1;
            buttonSkipPrevious.setEnabled(false);

        }
        else if(index == limit)
        {
            index-=1;
            buttonSkipNext.setEnabled(true);
        }
        else
        {
            index-=1;
        }
        mSpotifyAppRemote.getPlayerApi().skipPrevious();
        PlaySong();
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
    }

    //Callback to skip to next song in queue
    public void onSkipNext(View view)
    {
        count = 0;
        if(index == limit-1)
        {
            index+=1;
            buttonSkipNext.setEnabled(false);

        }
        else if(index == 0)
        {
            buttonSkipPrevious.setEnabled(true);
            index+=1;
        }
        else
        {
            index+=1;
        }
        mSpotifyAppRemote.getPlayerApi().skipNext();
        PlaySong();
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
    }

    // Callback to pause playback
    public void onPauseSongPressed(View view)
    {

        mSpotifyAppRemote.getPlayerApi().pause();
    }

    // Callback to stop playback of sog entirely
    public void onStopSongPressed(View view)
    {

        mSpotifyAppRemote.getPlayerApi().pause();
        count = 0;
    }


    // Method which looks up song URIs and plays them
    private void PlaySong()
    {
        if(count==0) {
            mSpotifyAppRemote.getPlayerApi().queue(tracklist.get(index+1));
            mSpotifyAppRemote.getPlayerApi().play(tracklist.get(index));
            mSpotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(new Subscription.EventCallback<PlayerState>() {

                        public void onEvent(PlayerState playerState) {
                            final Track track = playerState.track;
                            if (track != null) {
                                statusWindow.setText("Track: "+track.name+"\nArtist: "+track.artist.name+"\nAlbum: "+track.album.name);
                                mSpotifyAppRemote.getImagesApi()
                                        .getImage(playerState.track.imageUri)
                                        .setResultCallback(bitmap -> songIcon.setImageBitmap(bitmap));
                            }
                        }
                    });

            count += 1;
        }
        else
        {
            mSpotifyAppRemote.getPlayerApi().resume();
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
        }
    }


}

