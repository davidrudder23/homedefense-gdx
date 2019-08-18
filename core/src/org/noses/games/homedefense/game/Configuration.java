package org.noses.games.homedefense.game;

import lombok.Data;

@Data
public class Configuration {

    int width = 640;
    int height = 480;
    String baseURL = "http://homedefense.noses.org:8080/";

    boolean debug = false;

    public String getBaseURL() {
        if ((baseURL == null) || (baseURL.length()==0)) {
            return "http://homedefense.noses.org:8080/";
        }

        if (baseURL.endsWith("/")) {
            return baseURL;
        }

        return baseURL+"/";
    }
}
