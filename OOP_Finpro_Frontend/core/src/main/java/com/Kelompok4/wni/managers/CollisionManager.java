package com.Kelompok4.wni.managers;

import com.Kelompok4.wni.entities.FallingObject;
import com.Kelompok4.wni.entities.Player;
import com.badlogic.gdx.utils.Array;

public class CollisionManager {

    public void checkCollisions(
        Player player,
        Array<FallingObject> objects,
        ScoreManager scoreManager,
        Array<FallingObject> objectsToRecycle
    ) {
        if (objects == null) {
            return;
        }

        // Iterasi dari belakang ke depan (aman untuk penghapusan elemen Array libGDX)
        for (int i = objects.size - 1; i >= 0; i--) {
            FallingObject object = objects.get(i);
            if (object == null) {
                objects.removeIndex(i);
                continue;
            }

            // 1. Cek jika Player berhasil menangkap objek
            if (isColliding(player, object)) {
                if (scoreManager != null && object.getType() != null) {
                    scoreManager.applyObject(object.getType());
                }

                if (objectsToRecycle != null) {
                    objectsToRecycle.add(object);
                }

                objects.removeIndex(i);
            }
            // 2. Cek jika objek jatuh melewati batas bawah layar (keluar layar)
            else if (object.isOutOfScreen()) {
                if (objectsToRecycle != null) {
                    objectsToRecycle.add(object);
                }

                objects.removeIndex(i);
            }
        }
    }

    public boolean isColliding(Player player, FallingObject object) {
        if (player == null || object == null) {
            return false;
        }
        if (player.getBounds() == null || object.getBounds() == null) {
            return false;
        }
        return player.getBounds().overlaps(object.getBounds());
    }
}
