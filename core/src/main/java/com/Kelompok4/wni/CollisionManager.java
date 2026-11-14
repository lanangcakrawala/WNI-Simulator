package com.Kelompok4.wni;

import com.badlogic.gdx.utils.Array;

public class CollisionManager {

    public void checkCollisions(
        Player player,
        Array<FallingObject> objects,
        ScoreManager scoreManager,
        Array<FallingObject> objectsToRecycle
    ) {
        // Iterasi dari belakang ke depan (aman untuk penghapusan elemen Array libGDX)
        for (int i = objects.size - 1; i >= 0; i--) {
            FallingObject object = objects.get(i);

            // 1. Cek jika Player berhasil menangkap objek
            if (player.getBounds().overlaps(object.getBounds())) {
                // Update saldo dan skor via ScoreManager
                scoreManager.applyObject(object.getType());

                // Masukkan ke list recycle (untuk dikembalikan ke Pool nantinya)
                if (objectsToRecycle != null) {
                    objectsToRecycle.add(object);
                }

                // Hapus dari array aktif
                objects.removeIndex(i);
            }
            // 2. Cek jika objek jatuh melewati batas bawah layar (keluar layar)
            else if (object.isOutOfScreen()) {
                // Masukkan ke list recycle
                if (objectsToRecycle != null) {
                    objectsToRecycle.add(object);
                }

                // Hapus dari array aktif
                objects.removeIndex(i);
            }
        }
    }
}
