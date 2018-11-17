package org.noses.games.homedefense.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class NestDTO {
    int x;
    int y;

    float lat;
    float lon;

    long id;

    String type;
}