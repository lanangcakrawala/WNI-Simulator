
package com.Kelompok4.wni;

import com.badlogic.gdx.utils.Array;

// Mengatur alur utama gameplay dan koordinasi komponen permainan
public class GameController {

    // Menyimpan total waktu permainan
    private float elapsedTime;

    // Menghitung waktu menuju kemunculan objek berikutnya
    private float spawnTimer;

    // Interval awal kemunculan objek dalam detik
    private static final float SPAWN_INTERVAL = 1.5f;

    // Menyimpan objek pemain
    private Player player;

    // Menyimpan seluruh objek yang sedang aktif
    private final Array<FallingObject> fallingObjects;

    // Menampung objek yang tertangkap sebelum dikembalikan ke pool
    private final Array<FallingObject> objectsToRecycle;

    // Mengatur pembuatan dan penggunaan kembali objek
    private final ObjectFactory objectFactory;

    // Mengatur pemeriksaan tabrakan
    private final CollisionManager collisionManager;

    // Mengatur saldo dan skor permainan
    private final ScoreManager scoreManager;

    // Constructor untuk menginisialisasi seluruh komponen
    public GameController(ObjectFactory objectFactory) {

        elapsedTime = 0f;
        spawnTimer = 0f;

        this.objectFactory = objectFactory;

        fallingObjects = new Array<>();
        objectsToRecycle = new Array<>();

        collisionManager = new CollisionManager();
        scoreManager = new ScoreManager();
    }

    // Memperbarui seluruh kondisi permainan setiap frame
    public void update(
        float delta,
        float screenWidth,
        float screenHeight
    ) {

        // Mengabaikan delta yang tidak valid
        if (delta <= 0f) {
            return;
        }

        // Menghitung total waktu permainan
        elapsedTime += delta;

        // Menghitung waktu menuju spawn berikutnya
        spawnTimer += delta;

        // Membuat objek baru ketika interval spawn tercapai
        if (spawnTimer >= SPAWN_INTERVAL) {

            // Mengambil objek dari ObjectFactory
            FallingObject newObject =
                objectFactory.obtainRandomObject(
                    screenWidth,
                    screenHeight
                );

            // Memasukkan objek ke daftar objek aktif
            fallingObjects.add(newObject);

            // Mengatur ulang spawn timer
            spawnTimer = 0f;
        }

        // Memperbarui posisi pemain
        if (player != null) {
            player.update(delta);
        }

        // Memperbarui posisi seluruh objek jatuh
        for (int i = fallingObjects.size - 1; i >= 0; i--) {

            FallingObject object = fallingObjects.get(i);

            object.update(delta);

            // Memeriksa apakah objek sudah keluar dari layar
            if (object.isOutOfScreen()) {

                // Menghapus objek dari daftar aktif
                FallingObject removed =
                    fallingObjects.removeIndex(i);

                // Mengembalikan objek ke Object Pool
                objectFactory.freeObject(removed);
            }
        }

        // Memeriksa tabrakan jika pemain tersedia
        if (player != null) {

            collisionManager.checkCollisions(
                player,
                fallingObjects,
                scoreManager,
                objectsToRecycle
            );

            // Mengembalikan objek yang tertangkap ke Object Pool
            for (FallingObject object : objectsToRecycle) {
                objectFactory.freeObject(object);
            }

            // Mengosongkan daftar objek yang sudah dikembalikan
            objectsToRecycle.clear();
        }
    }

    // Menghubungkan objek Player ke GameController
    public void setPlayer(Player player) {
        this.player = player;
    }

    // Mengambil objek Player
    public Player getPlayer() {
        return player;
    }

    // Mengambil daftar objek yang sedang aktif untuk proses render
    public Array<FallingObject> getFallingObjects() {
        return fallingObjects;
    }

    // Mengambil total waktu permainan
    public float getElapsedTime() {
        return elapsedTime;
    }

    // Mengambil saldo pemain
    public int getBalance() {
        return scoreManager.getBalance();
    }

    // Mengambil saldo dalam format rupiah
    public String getFormattedBalance() {
        return scoreManager.getFormattedBalance();
    }

    // Mengambil jumlah objek yang berhasil ditangani
    public int getScore() {
        return scoreManager.getScore();
    }

    // Memeriksa apakah saldo sudah mencapai nol atau kurang
    public boolean isGameOver() {
        return scoreManager.isGameOver();
    }

    // Mengatur ulang kondisi permainan
    public void reset() {

        // Mengembalikan seluruh objek aktif ke Object Pool
        for (int i = fallingObjects.size - 1; i >= 0; i--) {

            FallingObject object = fallingObjects.removeIndex(i);
            objectFactory.freeObject(object);
        }

        // Mengosongkan daftar objek yang akan didaur ulang
        objectsToRecycle.clear();

        // Mengatur ulang waktu permainan
        elapsedTime = 0f;
        spawnTimer = 0f;

        // Mengatur ulang saldo dan skor
        scoreManager.reset();

        // Reset posisi pemain akan ditambahkan
        // setelah method reset milik Player tersedia
    }
}
