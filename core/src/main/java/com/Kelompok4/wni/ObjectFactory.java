
package com.Kelompok4.wni;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

// Mengatur pembuatan dan penggunaan kembali FallingObject
public class ObjectFactory {

    // Menyimpan kumpulan objek yang bisa digunakan kembali
    private final Pool<FallingObject> objectPool;

    // Ukuran objek yang muncul
    private static final float OBJECT_SIZE = 40f;

    // Rentang kecepatan objek jatuh
    private static final float MIN_SPEED = 150f;
    private static final float MAX_SPEED = 250f;

    // Jenis objek yang dapat dihasilkan
    private static final FallingObject.Type[] OBJECT_TYPES = {
        FallingObject.Type.SALARY,
        FallingObject.Type.BONUS,
        FallingObject.Type.TAX,
        FallingObject.Type.JUDOL
    };

    // Constructor untuk membuat Object Pool
    public ObjectFactory() {

        objectPool = new Pool<FallingObject>(10, 50) {

            @Override
            protected FallingObject newObject() {
                return new FallingObject();
            }
        };
    }

    // Membuat objek jatuh dengan tipe dan posisi acak
    public FallingObject obtainRandomObject(
        float screenWidth,
        float screenHeight
    ) {

        // Mengambil objek dari pool
        FallingObject object = objectPool.obtain();

        // Menentukan posisi horizontal secara acak
        float maxX = Math.max(0f, screenWidth - OBJECT_SIZE);
        float x = MathUtils.random(0f, maxX);

        // Menentukan kecepatan jatuh secara acak
        float speed = MathUtils.random(MIN_SPEED, MAX_SPEED);

        // Memilih jenis objek secara acak
        int randomIndex = MathUtils.random(
            0,
            OBJECT_TYPES.length - 1
        );

        FallingObject.Type type = OBJECT_TYPES[randomIndex];

        // Menginisialisasi objek sebelum digunakan
        object.init(
            x,
            screenHeight,
            OBJECT_SIZE,
            speed,
            type
        );

        return object;
    }

    // Mengembalikan objek yang sudah tidak digunakan ke pool
    public void freeObject(FallingObject object) {

        if (object != null) {
            objectPool.free(object);
        }
    }
}
