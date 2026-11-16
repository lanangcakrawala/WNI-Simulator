
package com.Kelompok4.wni;

import com.badlogic.gdx.utils.Array;

// Mengatur alur utama gameplay dan koordinasi komponen permainan
public class GameController {

    // Menyimpan total waktu permainan
    private float elapsedTime;

    // Menghitung waktu menuju kemunculan objek berikutnya
    private float spawnTimer;

    // Interval kemunculan objek dalam detik
    private static final float SPAWN_INTERVAL = 1.5f;

    // Menyimpan kondisi permainan saat ini
    private GameState gameState;

    // Menyimpan objek pemain
    private Player player;

    // Menyimpan seluruh objek yang sedang aktif
    private final Array<FallingObject> fallingObjects;

    // Menampung objek yang akan dikembalikan ke Object Pool
    private final Array<FallingObject> objectsToRecycle;

    // Mengatur pembuatan dan penggunaan kembali objek
    private final ObjectFactory objectFactory;

    // Mengatur pemeriksaan tabrakan
    private final CollisionManager collisionManager;

    // Mengatur saldo dan skor permainan
    private final ScoreManager scoreManager;


    // Constructor untuk menginisialisasi komponen permainan
    public GameController(ObjectFactory objectFactory) {

        elapsedTime = 0f;
        spawnTimer = 0f;

        // Permainan dimulai dalam kondisi PLAYING
        gameState = GameState.PLAYING;

        // Menghubungkan ObjectFactory
        this.objectFactory = objectFactory;

        // Membuat kumpulan objek aktif dan objek daur ulang
        fallingObjects = new Array<>();
        objectsToRecycle = new Array<>();

        // Membuat pengelola tabrakan dan saldo
        collisionManager = new CollisionManager();
        scoreManager = new ScoreManager();
    }


    // Memperbarui kondisi permainan setiap frame
    public void update(
        float delta,
        float screenWidth,
        float screenHeight
    ) {

        // Update hanya dilakukan ketika permainan sedang berjalan
        if (gameState != GameState.PLAYING) {
            return;
        }

        // Mengabaikan delta yang tidak valid
        if (delta <= 0f) {
            return;
        }

        // Menghitung total waktu permainan
        elapsedTime += delta;

        // Menghitung waktu menuju spawn berikutnya
        spawnTimer += delta;

        // Membuat objek ketika interval spawn tercapai
        if (spawnTimer >= SPAWN_INTERVAL) {

            // Mengambil objek acak dari ObjectFactory
            FallingObject newObject =
                objectFactory.obtainRandomObject(
                    screenWidth,
                    screenHeight
                );

            // Menambahkan objek ke kumpulan objek aktif
            fallingObjects.add(newObject);

            // Mengurangi timer berdasarkan interval spawn
            spawnTimer -= SPAWN_INTERVAL;
        }

        // Memperbarui posisi pemain
        if (player != null) {
            player.update(delta);
        }

        // Memperbarui seluruh objek jatuh
        // Iterasi dari belakang agar aman ketika menghapus objek
        for (int i = fallingObjects.size - 1; i >= 0; i--) {

            FallingObject object = fallingObjects.get(i);

            // Memperbarui posisi objek
            object.update(delta);

            // Memeriksa apakah objek sudah keluar layar
            if (object.isOutOfScreen()) {

                // Menghapus objek dari daftar aktif
                FallingObject removed =
                    fallingObjects.removeIndex(i);

                // Mengembalikan objek ke Object Pool
                objectFactory.freeObject(removed);
            }
        }

        // Memeriksa tabrakan jika Player tersedia
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

            // Mengosongkan daftar objek yang sudah didaur ulang
            objectsToRecycle.clear();
        }

        // Memeriksa apakah saldo sudah mencapai nol atau kurang
        if (scoreManager.isGameOver()) {
            gameState = GameState.GAME_OVER;
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

    // Mengambil daftar objek aktif untuk proses render
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

    // Mengambil jumlah objek yang berhasil ditangkap
    public int getScore() {
        return scoreManager.getScore();
    }

    // Mengambil kondisi permainan
    public GameState getGameState() {
        return gameState;
    }

    // Memeriksa apakah permainan sudah berakhir
    public boolean isGameOver() {
        return gameState == GameState.GAME_OVER;
    }


    // Menghentikan sementara permainan
    public void pause() {

        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        }
    }

    // Melanjutkan permainan setelah dijeda
    public void resume() {

        if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
        }
    }


    // Mengatur ulang kondisi permainan
    public void reset() {

        // Mengembalikan seluruh objek aktif ke Object Pool
        for (int i = fallingObjects.size - 1; i >= 0; i--) {

            FallingObject object = fallingObjects.removeIndex(i);

            objectFactory.freeObject(object);
        }

        // Mengosongkan daftar objek daur ulang
        objectsToRecycle.clear();

        // Mengatur ulang waktu permainan
        elapsedTime = 0f;
        spawnTimer = 0f;

        // Mengembalikan saldo dan skor ke nilai awal
        scoreManager.reset();

        // Mengembalikan posisi pemain ke posisi awal
        if (player != null) {
            player.reset();
        }

        // Mengaktifkan kembali permainan
        gameState = GameState.PLAYING;
    }
}
