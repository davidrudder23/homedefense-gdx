package org.noses.games.homedefense.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapClient {
    ObjectMapper objectMapper;
    OkHttpClient client;
    String baseURL;

    public MapClient(String baseURL) {
        objectMapper = new ObjectMapper();
        client = new OkHttpClient();

        this.baseURL = baseURL;
    }

    public Account register(String name,
                            String email,
                            String password,
                            float latitude,
                            float longitude) throws IOException {
        String registerURL = baseURL+"users/register";
        String updateURL = baseURL+"users/user";

        Register register = new Register();
        register.setUsername(name);
        register.setPassword(password);
        register.setEmail(email);
        String jsonPost = objectMapper.writeValueAsString(register);

        Request request = new Request.Builder()
                .url(registerURL)
                .post(RequestBody.create(MediaType.parse("application/json"), jsonPost))
                .build();

        Response response = client.newCall(request).execute();
        Account account = objectMapper.readValue(response.body().string(), Account.class);

        account = login(name, password);

        setHomeLocation(latitude, longitude, updateURL, account);

        return account;
    }

    private void setHomeLocation(float latitude, float longitude, String updateURL, Account account) throws IOException {
        account.setHomeLatitude(latitude);
        account.setHomeLongitude(longitude);
        String jsonPost = objectMapper.writeValueAsString(account);

        Request request = new Request.Builder()
                .url(updateURL)
                .addHeader("X-Authorization-Token", account.getToken())
                .post(RequestBody.create(MediaType.parse("application/json"), jsonPost))
                .build();

        Response response = client.newCall(request).execute();

        // Update is a write operation, just throw away the response
        String responseJSON = response.body().string();

        // TODO
        objectMapper.readValue(responseJSON, Account.class);
    }

    public Account login(String name, String password) throws IOException {
        String url = baseURL+"users/login";

        Login login = new Login();
        login.setUsername(name);
        login.setPassword(password);
        String jsonPost = objectMapper.writeValueAsString(login);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), jsonPost))
                .build();

        Response response = client.newCall(request).execute();
        Account account = objectMapper.readValue(response.body().string(), Account.class);
        return account;
    }

    public Map getMap(Account account, double north, double south, double east, double west) throws IOException {
        String url = baseURL+"maps/"+north+"/"+south+"/"+east+"/"+west;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token", account.getToken())
                .build();

        Response response = client.newCall(request).execute();

        String mapJSON = response.body().string();
        Map map = objectMapper.readValue(mapJSON, Map.class);
        return map;
    }

    public List<Destination> getDestinations() throws IOException {
        String url = baseURL+"maps/destinations";

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        String destinationsJSON = response.body().string();
        List<Destination> destinations = new ArrayList<>(Arrays.asList(objectMapper.readValue(destinationsJSON, Destination[].class)));
        return destinations;
    }

}
