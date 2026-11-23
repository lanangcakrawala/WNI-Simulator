package com.Kelompok4.wni;

public class GameController {

    // Menyimpan total waktu permainan
    private float elapsedTime;

    // Constructor
    public GameController() {
        elapsedTime = 0f;
    }

    // Memperbarui kondisi permainan setiap frame
    public void update(float delta) {

        // Mengabaikan delta yang tidak valid
        if (delta <= 0f) {
            return;
        }

        // Menghitung waktu permainan
        elapsedTime += delta;

        /*
         * Integrasi komponen permainan akan ditambahkan di sini:
         *
         * 1. Update Player
         * 2. Update Falling Objects
         * 3. Collision Detection
         * 4. Update Balance
         */
    }

    // Mengambil waktu permainan
    public float getElapsedTime() {
        return elapsedTime;
    }

    // Mengatur ulang waktu permainan
    public void reset() {
        elapsedTime = 0f;
    }
}
