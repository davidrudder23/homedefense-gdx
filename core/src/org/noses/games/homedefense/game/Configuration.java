package org.noses.games.homedefense.game;

import lombok.Data;

@Data
public class Configuration {

    int width = 640;
    int height = 480;
    String baseURL = "http://10.10.10.1:8080/";
}
