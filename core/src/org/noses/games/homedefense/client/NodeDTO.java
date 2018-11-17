package org.noses.games.homedefense.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NodeDTO {
    int x;
    int y;

    float lat;
    float lon;

    long id;

    int order;
}
