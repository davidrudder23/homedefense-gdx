package org.noses.games.homedefense.client;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MapDTO {
    List<WayDTO> ways;
    int width;
    int height;
    List<NestDTO> nests;

    public MapDTO() {
        ways = new ArrayList<>();
    }

    public void addWay(WayDTO way) {
        this.ways.add(way);
    }
}



