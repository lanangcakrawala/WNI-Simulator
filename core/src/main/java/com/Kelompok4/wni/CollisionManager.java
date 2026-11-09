
package com.Kelompok4.wni;

import com.badlogic.gdx.utils.Array;

public class CollisionManager {

    public void checkCollisions(
        Player player,
        Array<FallingObject> objects,
        ScoreManager scoreManager
    ) {

        for (int i = objects.size - 1; i >= 0; i--) {

            FallingObject object = objects.get(i);

            if (player.getBounds().overlaps(
                object.getBounds())) {

                scoreManager.applyObject(object.getType());

                objects.removeIndex(i);
            }
        }
    }
}
