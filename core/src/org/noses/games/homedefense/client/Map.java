package org.noses.games.homedefense.client;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Map {
    List<Way> ways;
    int width;
    int height;
    List<Nest> nests;

    public Map() {
        ways = new ArrayList<>();
    }

    public void addWay(Way way) {
        this.ways.add(way);
    }
}



