
package com.Kelompok4.wni.factory;

import com.Kelompok4.wni.entities.FallingObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

// Mengatur pembuatan dan penggunaan kembali FallingObject
public class ObjectFactory {

    // Menyimpan kumpulan objek yang bisa digunakan kembali
    private final Pool<FallingObject> objectPool;

    // Ukuran objek yang muncul
    private static final float OBJECT_SIZE = 40f;

    // Rentang kecepatan dasar objek jatuh
    private static final float MIN_SPEED = 150f;
    private static final float MAX_SPEED = 250f;

    // Constructor untuk membuat Object Pool
    public ObjectFactory() {

        // Membuat Object Pool dengan kapasitas awal 10 dan maksimum 50
        objectPool = new Pool<FallingObject>(10, 50) {

            @Override
            protected FallingObject newObject() {

                // Membuat objek baru jika Object Pool kosong
                return new FallingObject();
            }
        };
    }

    // Membuat objek dengan jenis dan kecepatan berdasarkan tingkat kesulitan
    public FallingObject obtainRandomObject(
        float screenWidth,
        float screenHeight,
        int laneIndex,
        int laneCount,
        float difficultyLevel
    ) {

        // Membatasi tingkat kesulitan dari level 0 sampai 4
        difficultyLevel = MathUtils.clamp(difficultyLevel, 0f, 4f);

        // Memastikan jumlah jalur minimal satu
        laneCount = Math.max(1, laneCount);

        // Mengambil objek dari Object Pool
        FallingObject object = objectPool.obtain();

        // Menghitung lebar setiap jalur
        float laneWidth = screenWidth / laneCount;

        // Memastikan indeks jalur valid
        laneIndex = MathUtils.clamp(laneIndex, 0, laneCount - 1);

        // Menentukan posisi objek di tengah jalurnya
        float x = laneIndex * laneWidth
            + (laneWidth - OBJECT_SIZE) / 2f;

        // Memastikan objek tidak keluar dari layar
        float maxX = Math.max(0f, screenWidth - OBJECT_SIZE);
        x = MathUtils.clamp(x, 0f, maxX);

        // Menghitung peluang setiap jenis objek
        float salaryChance = 35f - (6.25f * difficultyLevel);
        float bonusChance = 35f - (6.25f * difficultyLevel);
        float taxChance = 15f + (5f * difficultyLevel);

        // Menghasilkan angka acak antara 0 sampai kurang dari 100
        float randomChance = MathUtils.random() * 100f;

        // Menentukan jenis objek berdasarkan peluang kemunculannya
        FallingObject.Type type;

        if (randomChance < salaryChance) {

            // Objek Salary
            type = FallingObject.Type.SALARY;

        } else if (randomChance < salaryChance + bonusChance) {

            // Objek Bonus
            type = FallingObject.Type.BONUS;

        } else if (randomChance < salaryChance + bonusChance + taxChance) {

            // Objek Tax
            type = FallingObject.Type.TAX;

        } else {

            // Objek Judol
            type = FallingObject.Type.JUDOL;
        }

        // Meningkatkan kecepatan berdasarkan tingkat kesulitan
        float speedMultiplier = 1f + (difficultyLevel * 0.15f);

        // Menentukan kecepatan dasar secara acak
        float speed = MathUtils.random(MIN_SPEED, MAX_SPEED)
            * speedMultiplier;

        // Menginisialisasi objek sebelum digunakan
        object.init(
            x,
            screenHeight,
            OBJECT_SIZE,
            speed,
            type
        );

        // Mengembalikan objek yang sudah selesai diinisialisasi
        return object;
    }

    // Mengembalikan objek yang sudah tidak digunakan ke Object Pool
    public void freeObject(FallingObject object) {

        if (object != null) {

            // Memasukkan kembali objek ke dalam Object Pool
            objectPool.free(object);
        }
    }
}
