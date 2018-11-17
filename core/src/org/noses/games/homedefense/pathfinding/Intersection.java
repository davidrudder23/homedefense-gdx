package org.noses.games.homedefense.pathfinding;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.noses.games.homedefense.client.MapDTO;

import java.util.List;

@Data
@Slf4j
public class Intersection {

    float longitude;
    float latitude;

    int x;
    int y;

    List<MapDTO> mapDTOList;
}
