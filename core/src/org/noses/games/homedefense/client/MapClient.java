package org.noses.games.homedefense.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class MapClient {
    ObjectMapper objectMapper;
    OkHttpClient client;

    public MapClient() {
        objectMapper = new ObjectMapper();
        client = new OkHttpClient();
    }

    public Account register(String name,
                            String email,
                            String password,
                            float latitude,
                            float longitude) throws IOException {
        String registerURL = "http://localhost:8080/users/register";
        String updateURL = "http://localhost:8080/users/user";

        Register register = new Register();
        register.setUsername(name);
        register.setPassword(password);
        register.setEmail(email);
        String jsonPost = objectMapper.writeValueAsString(register);

        System.out.println("Registering with "+jsonPost);

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
        System.out.println("Register Response="+ account.getToken());

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
        System.out.println("Set Home Response="+responseJSON);

        // TODO
        objectMapper.readValue(responseJSON, Account.class);
    }

    public Account login(String name, String password) throws IOException {
        String url = "http://localhost:8080/users/login";

        Login login = new Login();
        login.setUsername(name);
        login.setPassword(password);
        String jsonPost = objectMapper.writeValueAsString(login);

        System.out.println("Logging in with "+jsonPost);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), jsonPost))
                .build();

        Response response = client.newCall(request).execute();
        Account account = objectMapper.readValue(response.body().string(), Account.class);
        System.out.println("Login Response="+ account.getToken());
        return account;
    }

    public Map getMap(Account account, double north, double south, double east, double west) throws IOException {
        String url = "http://localhost:8080/maps/"+north+"/"+south+"/"+east+"/"+west;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token", account.getToken())
                .build();

        Response response = client.newCall(request).execute();

        String mapJSON = response.body().string();
        //System.out.println(mapJSON);
        Map map = objectMapper.readValue(mapJSON, Map.class);
        return map;
    }
}
