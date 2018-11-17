package org.noses.games.homedefense.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DestinationDTO {

    String id;

    float lat;
    float lon;

    String name;
    String description;
}
