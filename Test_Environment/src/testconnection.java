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
        List<JsonElement> trackList = new ArrayList<>();
        JsonParser parser = new JsonParser();
        int myLimit = 0;

        BufferedReader reader = new BufferedReader(new InputStreamReader((System.in)));

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

        try {
            System.out.println("Search term: ");
            String myQuery = reader.readLine();
            myQuery = myQuery.trim();

            //System.out.println(myQuery);

            myQuery = myQuery.replaceAll(" +", "+");

            //System.out.println(myQuery);

            System.out.println("Limit: ");
            myLimit =  Integer.parseInt(reader.readLine());

            URL searchUrl = new URL ("https://api.spotify.com/v1/search?q=" + myQuery +
                    "&type=track&limit=" + myLimit);
            HttpURLConnection searchConn = (HttpURLConnection) searchUrl.openConnection();
            searchConn.setRequestMethod("GET");
            searchConn.setRequestProperty("Accept", "application/json");
            searchConn.setRequestProperty("Content-Type", "application/json");
            searchConn.setRequestProperty("Authorization", "Bearer " + token);

            if (searchConn.getResponseCode() != 200) {
                throw new RuntimeException("ERROR CODE " + searchConn.getResponseCode());
            }


            JsonObject accessObj = (JsonObject) parser.parse(new InputStreamReader(searchConn.getInputStream()));

            JsonArray accessElements = accessObj.getAsJsonObject("tracks").getAsJsonArray("items");

            for(int i = 0;i < myLimit; ++i){
                JsonObject track = accessElements.get(i).getAsJsonObject();
                trackList.add(i, track.get("id"));
                //System.out.println(trackList.get(i));
            }

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

        StringBuffer trackIds = new StringBuffer();

        try{
            for(int i = 0; i < myLimit; ++i) {
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
    }
}