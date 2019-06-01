package org.noses.games.homedefense.geolocation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.noses.games.homedefense.geometry.Point;

import java.io.IOException;

public class IPAddressGeolocator implements Geolocator {
    private String apiKey;
    OkHttpClient client;

    ObjectMapper objectMapper;

    public IPAddressGeolocator(String apiKey) {
        this.apiKey = apiKey;
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client = new OkHttpClient();
    }


    @Override
    public Point getGeolocation() {
        try {
            String ipAddress = "73.95.36.144";
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
