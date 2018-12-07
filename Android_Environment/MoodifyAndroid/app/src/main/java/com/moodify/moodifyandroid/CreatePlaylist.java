package com.moodify.moodifyandroid;

 /*
 *  TITLE: CreatePlaylist.java
 *  AUTHORS: Miguel Gotao and Scott Weller
 *
 *  This class takes a search query String and an integer for number of songs
 *  and generates an ArrayList<String> of song URIs which is then sent to
 *  the PlaylistPlayer Activity. The API calls are made to Spotify's Web
 *  API via AsyncTask in a background thread. The AsyncTask parameters are passed
 *  via a custom class to contain search query and playlist limit variables
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.util.Base64;
import java.util.List;

public class CreatePlaylist extends AppCompatActivity {

    private EditText searchTermEditText;
    private EditText numSongsEditText;
    private Button backToPlayer;
    private String searchQuery;
    private Integer limit;
    private static final String CLIENT_ID = "70424a1a3b4047ceb03cbb0b4b9e762e";
    private static final String REDIRECT_URI = "com.moodify.moodifyandroid://callback";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        searchTermEditText = findViewById(R.id.searchTermEditText);
        numSongsEditText = findViewById(R.id.numSongsEditText);
        backToPlayer = findViewById(R.id.buttonBackToPlayer);
        backToPlayer.setVisibility(View.INVISIBLE);
        backToPlayer.setEnabled(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        backToPlayer.setEnabled(true);
        backToPlayer.setVisibility(View.VISIBLE);
    }

    public void onBackToPlayer(View view)
    {
        Intent j = new Intent(CreatePlaylist.this, PlaylistPlayer.class);
        finish();
        startActivity(j);
    }

    // Callback for submit button which generates playlist and opens new activity
    public void onGeneratePlaylist(View view)
    {
        searchQuery = searchTermEditText.getText().toString();
        limit = Integer.parseInt(numSongsEditText.getText().toString());
        AsyncParams playlistParams = new AsyncParams(searchQuery, limit);
        SpotifySearchAsyncTask generateTask = new SpotifySearchAsyncTask();
        generateTask.execute(playlistParams);
    }


    //Class to pass params to Async Task
    public static class AsyncParams {
        String searchString;
        ArrayList<JsonElement> asyncTrackList;
        Integer limit;

        AsyncParams(String searchString)
        {
            this.searchString = searchString;
            this.asyncTrackList = new ArrayList<>();
        }

        AsyncParams(String searchString, List<JsonElement> trackList)
        {
            this.searchString = searchString;
            this.asyncTrackList = new ArrayList<>(trackList);
        }

        AsyncParams(String searchString, Integer limit)
        {
            this.searchString = searchString;
            this.limit = limit;
        }

        public String getSearchString(){return searchString;}

        public ArrayList<JsonElement> getAsyncTrackList(){return asyncTrackList;}

        public Integer getLimit(){return limit;}

    }

    //Makes API calls in background thread, generates ArrayList<String> of
    // song URIs which is then passed via intent to PlaylistPlayer Activity
    private class SpotifySearchAsyncTask extends AsyncTask<AsyncParams, Void, String> {

        private String token;
        private ArrayList<String> trackList = new ArrayList<>();
        private JsonArray playlistElements;
        int playlistLimit;
        @Override
        protected String doInBackground(AsyncParams... params) {
            String clientId = "70424a1a3b4047ceb03cbb0b4b9e762e";
            String clientSecret = "49a0c1c89a804131ad9714e411ee4b21";
            String auth = clientId + ":" + clientSecret;
            String token = "";
            String seedTrack = "";
            JsonParser parser = new JsonParser();
            int myLimit = params[0].limit;
            playlistLimit = myLimit;



            //Connection #1: Request Token from API. The authorization code is a combination of the
            //clientId and clientSecret that is encoded into Base64. The token is stored into a string.
            try {
                URL authUrl = new URL ("https://accounts.spotify.com/api/token/?grant_type=client_credentials");
                HttpURLConnection authConn = (HttpURLConnection) authUrl.openConnection();
                authConn.setRequestMethod("POST");
                authConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                authConn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP));

                if (authConn.getResponseCode() != 200) {
                    throw new RuntimeException("ERROR CODE " + authConn.getResponseCode());
                }


                JsonObject tokenObject = (JsonObject) new JsonParser().parse(new InputStreamReader(authConn.getInputStream()));


                token = tokenObject.get("access_token").getAsString();
                this.token = token;
                //System.out.println(token);
                authConn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Connection #2: Input phase. User will be asked to enter a search term and the number of tracks they want
            //in the playlist. The search term will be used to look for a track that will be used as a seed in the next
            //connection. The connection returns a track id that is stored as a string.
            try {
                String myQuery = params[0].searchString;
                myQuery = myQuery.trim();
                myQuery = myQuery.replaceAll(" +", "+");
                URL searchUrl = new URL ("https://api.spotify.com/v1/search?q=" + myQuery +
                        "&type=track&limit=1");
                HttpURLConnection searchConn = (HttpURLConnection) searchUrl.openConnection();
                searchConn.setRequestMethod("GET");
                searchConn.setRequestProperty("Accept", "application/json");
                searchConn.setRequestProperty("Content-Type", "application/json");
                searchConn.setRequestProperty("Authorization", "Bearer " + token);

                if (searchConn.getResponseCode() != 200) {
                    throw new RuntimeException("ERROR CODE " + searchConn.getResponseCode());
                }


                JsonObject accessObj = (JsonObject) parser.parse(new InputStreamReader(searchConn.getInputStream()));

                seedTrack = accessObj.getAsJsonObject("tracks").getAsJsonArray("items").get(0)
                        .getAsJsonObject().get("id").getAsString();
                System.out.println(seedTrack);

                searchConn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Connection #3: Playlist generation. Using the seed track found in connection #2, we will generate a list of
            //track recommendations. The number of tracks generated mirrors user input in #2 as well.
            try {
                URL playlistUrl = new URL ("https://api.spotify.com/v1/recommendations?limit=" + myLimit + "&seed_tracks=" + seedTrack);
                HttpURLConnection playlistConn = (HttpURLConnection) playlistUrl.openConnection();
                playlistConn.setRequestMethod("GET");
                playlistConn.setRequestProperty("Accept", "application/json");
                playlistConn.setRequestProperty("Content-Type", "application/json");
                playlistConn.setRequestProperty("Authorization", "Bearer " + token);

                if (playlistConn.getResponseCode() != 200) {
                    throw new RuntimeException("ERROR CODE " + playlistConn.getResponseCode());
                }

                JsonObject playlistObj = (JsonObject) parser.parse(new InputStreamReader(playlistConn.getInputStream()));

                playlistElements = playlistObj.getAsJsonArray("tracks");

                int buffer = 0;
                while(buffer < myLimit) {
                    JsonObject track = playlistElements.get(buffer).getAsJsonObject();
                    System.out.println(track.get("uri"));
                    trackList.add(buffer, track.get("uri").getAsString());
                    System.out.println(trackList.get(buffer));
                    buffer++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {

            Intent i = new Intent(CreatePlaylist.this, PlaylistPlayer.class);
            i.putExtra("TRACKLIST", trackList);
            i.putExtra("LIMIT", playlistLimit);
            startActivity(i);
        }
    }
}