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

    public AccountDTO register(String name,
                               String email,
                               String password,
                               float latitude,
                               float longitude) throws IOException {
        String registerURL = "http://localhost:8080/users/register";
        String updateURL = "http://localhost:8080/users/user";

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(name);
        registerDTO.setPassword(password);
        registerDTO.setEmail(email);
        String jsonPost = objectMapper.writeValueAsString(registerDTO);

        System.out.println("Registering with "+jsonPost);

        Request request = new Request.Builder()
                .url(registerURL)
                .post(RequestBody.create(MediaType.parse("application/json"), jsonPost))
                .build();

        Response response = client.newCall(request).execute();
        AccountDTO accountDTO = objectMapper.readValue(response.body().string(), AccountDTO.class);

        accountDTO = login(name, password);

        setHomeLocation(latitude, longitude, updateURL, accountDTO);

        return accountDTO;
    }

    private void setHomeLocation(float latitude, float longitude, String updateURL, AccountDTO accountDTO) throws IOException {
        System.out.println("Register Response="+accountDTO.getToken());

        accountDTO.setHomeLatitude(latitude);
        accountDTO.setHomeLongitude(longitude);
        String jsonPost = objectMapper.writeValueAsString(accountDTO);

        Request request = new Request.Builder()
                .url(updateURL)
                .addHeader("X-Authorization-Token", accountDTO.getToken())
                .post(RequestBody.create(MediaType.parse("application/json"), jsonPost))
                .build();

        Response response = client.newCall(request).execute();

        // Update is a write operation, just throw away the response
        String responseJSON = response.body().string();
        System.out.println("Set Home Response="+responseJSON);
        objectMapper.readValue(responseJSON, AccountDTO.class);
    }

    public AccountDTO login(String name, String password) throws IOException {
        String url = "http://localhost:8080/users/login";

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(name);
        loginDTO.setPassword(password);
        String jsonPost = objectMapper.writeValueAsString(loginDTO);

        System.out.println("Logging in with "+jsonPost);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), jsonPost))
                .build();

        Response response = client.newCall(request).execute();
        AccountDTO accountDTO = objectMapper.readValue(response.body().string(), AccountDTO.class);
        System.out.println("Login Response="+accountDTO.getToken());
        return accountDTO;
    }

    public MapDTO getMap(AccountDTO accountDTO, int width, int height) throws IOException {
        String url = "http://localhost:8080/maps/"+width+"/"+height;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token", accountDTO.getToken())
                .build();

        Response response = client.newCall(request).execute();

        String mapJSON = response.body().string();
        //System.out.println(mapJSON);
        MapDTO mapDTO = objectMapper.readValue(mapJSON, MapDTO.class);
        return mapDTO;
    }
}
