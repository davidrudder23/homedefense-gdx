package org.noses.games.homedefense.client;

import lombok.Data;

@Data
public class Account {

    private String email;
    private String username;
    private String token;
    private float homeLongitude;
    private float homeLatitude;

}
