package org.noses.games.homedefense.geolocation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.noses.games.homedefense.geometry.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class IPAddressGeolocator extends Geolocator {
    private String apiKey;
    OkHttpClient client;

    ObjectMapper objectMapper;

    Point geoLocation;

    double delayCount = 0;

    public IPAddressGeolocator(String apiKey) {
        this.apiKey = apiKey;
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client = new OkHttpClient();

        geoLocation = getGeoFromIP();

    }

    @Override
    public boolean hasLiveGeolocation() {
        return false;
    }

    @Override
    public Point getGeolocation() {
        return geoLocation;
    }

    private Point getGeoFromIP() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            if (in == null) {
                return null;
            }

            String ip = in.readLine(); //you get the IP as a String

            if (ip == null) {
                return null;
            }

            String url = "http://api.ipstack.com/" + ip + "?access_key=" + apiKey;

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String json = response.body().string();

            Point point = objectMapper.readValue(json,Point.class);
            return point;
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
        }
        return null;
    }
}
