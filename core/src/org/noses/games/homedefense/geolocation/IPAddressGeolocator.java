package org.noses.games.homedefense.geolocation;

import com.badlogic.gdx.utils.Timer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.geometry.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class IPAddressGeolocator extends Geolocator implements ClockTickHandler {
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
    public void clockTick(double delay) {
        delayCount+=delay;
        if (delayCount<1) {
            return;
        }
        delayCount = 0;

        geoLocation.setLatitude(geoLocation.getLatitude()+0.0001);
        updateLocation();
    }

    @Override
    public void kill() {

    }

    @Override
    public boolean isKilled() {
        return false;
    }

    @Override
    public Point getGeolocation() {
        return geoLocation;
    }

    private Point getGeoFromIP() {
        try {
            String ipAddress = "73.95.36.144";

            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            String ip = in.readLine(); //you get the IP as a String
            System.out.println(ip);

            String url = "http://api.ipstack.com/" + ipAddress + "?access_key=" + apiKey;

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
