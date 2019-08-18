package org.noses.games.homedefense.level;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnemyConfig {
    int health;
    int speedMultiplier;
    int damage;
    int value;
}