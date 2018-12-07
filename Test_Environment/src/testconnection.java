/*===============================================================
testconection.java
Author: Miguel Luis Gotao
ID#:    2264941
Email:  gotao100@mail.chapman.edu

This program is meant to showcase the Spotify API by pulling song
recommendations based off user input. The program connects to the
Spotify Web API via HttpUrlConnection, asking for a token based
off our applications ClientId and ClientSecret. The program then
prompts the user for input based off what they want the playlist
to be based upon (artist, track name, genre, etc.) and the number
of tracks they want to be on the playlist. The program will then
send a request to search for a track based off the user's input,
then use said track as a seed for generating the playlist.
===============================================================*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

public class testconnection {
    public static void main(String[] args) {
        String clientId = "2fc975d657f54a18828abf965dde398a";
        String clientSecret = "486c4e184db14be798e7402551a18b5a";
        String auth = clientId + ":" + clientSecret;
        String token = "";
        String seedTrack = "";
        List<JsonElement> trackList = new ArrayList<>();
        JsonParser parser = new JsonParser();
        int myLimit = 0;

        BufferedReader reader = new BufferedReader(new InputStreamReader((System.in)));

        //Connection #1: Request Token from API. The authorization code is a combination of the
        //clientId and clientSecret that is encoded into Base64. The token is stored into a string.
        try {
            URL authUrl = new URL ("https://accounts.spotify.com/api/token/?grant_type=client_credentials");
            HttpURLConnection authConn = (HttpURLConnection) authUrl.openConnection();
            authConn.setRequestMethod("POST");
            authConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            authConn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));

            if (authConn.getResponseCode() != 200) {
                throw new RuntimeException("ERROR CODE " + authConn.getResponseCode());
            }

            //BufferedReader reader = new BufferedReader(new InputStreamReader((auth_conn.getInputStream())));

            JsonObject tokenObject = (JsonObject) new JsonParser().parse(new InputStreamReader(authConn.getInputStream()));
            /*
            String output;
            while ((output = reader.readLine()) != null) {
                System.out.println(output);
            }
            */

            token = tokenObject.get("access_token").getAsString();
            //System.out.println(token);
            authConn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Connection #2: Input phase. User will be asked to enter a search term and the number of tracks they want
        //in the playlist. The search term will be used to look for a track that will be used as a seed in the next
        //connection. The connection returns a track id that is stored as a string.
        try {
            System.out.println("Enter the name of a track or artist you want to base the playlist on: ");
            String myQuery = reader.readLine();
            myQuery = myQuery.trim();

            //System.out.println(myQuery);

            myQuery = myQuery.replaceAll(" +", "+");

            //System.out.println(myQuery);

            System.out.println("Enter the length of your desired playlist: ");
            myLimit =  Integer.parseInt(reader.readLine());

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
            /*
            int buffer = 0;
            while(buffer < accessElements.size()){
                JsonObject track = accessElements.get(buffer).getAsJsonObject();
                System.out.println(track.get("uri"));
                System.out.println(track.get("artist"));
                trackList.add(buffer, track.get("id"));
                System.out.println(trackList.get(buffer));
                buffer++;
            }
            */

            /*
            BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = reader.readLine()) != null) {
                System.out.println(output);
            }
            */

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

            JsonArray playlistElements = playlistObj.getAsJsonArray("tracks");

            int buffer = 0;
            while(buffer < myLimit) {
                JsonObject track = playlistElements.get(buffer).getAsJsonObject();
                System.out.println(track.get("uri"));
                trackList.add(buffer, track.get("id"));
                System.out.println(trackList.get(buffer));
                buffer++;
            }
            } catch (IOException e) {
                e.printStackTrace();
            }


        /*
        StringBuffer trackIds = new StringBuffer();

        try{
            for(int i = 0; i < trackList.size(); ++i) {
                if (i == myLimit - 1) trackIds.append(trackList.get(i).getAsString());
                else {
                    trackIds.append((trackList.get(i).getAsString()) + ",");
                }
                //System.out.println(trackIds);
            }

            URL featureUrl = new URL ("https://api.spotify.com/v1/audio-features?ids=" + trackIds);
            HttpURLConnection featureConn = (HttpURLConnection) featureUrl.openConnection();
            featureConn.setRequestMethod("GET");
            featureConn.setRequestProperty("Accept", "application/json");
            featureConn.setRequestProperty("Content-Type", "application/json");
            featureConn.setRequestProperty("Authorization", "Bearer " + token);

            if (featureConn.getResponseCode() != 200) {
                throw new RuntimeException("ERROR CODE " + featureConn.getResponseCode());
            }

            JsonObject featureObj = (JsonObject) parser.parse(new InputStreamReader(featureConn.getInputStream()));

            System.out.println(featureObj);

        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}