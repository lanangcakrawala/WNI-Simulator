
package com.Kelompok4.wni.game;

import com.Kelompok4.wni.entities.FallingObject;
import com.Kelompok4.wni.entities.Player;
import com.Kelompok4.wni.factory.ObjectFactory;
import com.Kelompok4.wni.managers.CollisionManager;
import com.Kelompok4.wni.managers.ScoreManager;
import com.badlogic.gdx.utils.Array;

// Mengatur alur utama gameplay dan koordinasi komponen permainan
public class GameController {

    // Menyimpan total waktu permainan
    private float elapsedTime;

    // Menghitung waktu menuju spawn berikutnya
    private float spawnTimer;

    // Interval awal kemunculan gelombang objek
    private static final float BASE_SPAWN_INTERVAL = 1.5f;

    // Interval spawn tercepat
    private static final float MIN_SPAWN_INTERVAL = 0.7f;

    // Setiap 30 detik tingkat kesulitan meningkat
    private static final float DIFFICULTY_STEP_SECONDS = 30f;

    // Jumlah objek yang muncul dalam satu gelombang
    private static final int OBJECTS_PER_WAVE = 3;

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

        // Mengabaikan delta yang tidak valid.
        if (delta <= 0f) {
            return;
        }
        // Membatasi delta maksimal sebesar 0,05 detik.
        // Mencegah objek bergerak terlalu jauh ketika terjadi lag.
        delta = Math.min(delta, 0.05f);

        // Menghitung total waktu permainan
        elapsedTime += delta;

        // Menghitung tingkat kesulitan berdasarkan waktu bermain
        // Level meningkat setiap 30 detik, maksimal level 4
        float difficultyLevel = Math.min(
            elapsedTime / DIFFICULTY_STEP_SECONDS,
            4f
        );

        // Menghitung interval spawn berdasarkan tingkat kesulitan
        float currentSpawnInterval = Math.max(
            MIN_SPAWN_INTERVAL,
            BASE_SPAWN_INTERVAL - (difficultyLevel * 0.2f)
        );

        // Menambahkan waktu frame ke timer spawn.
        // Timer dibatasi agar tidak menumpuk ketika terjadi frame drop.
        spawnTimer = Math.min(
            spawnTimer + delta,
            currentSpawnInterval
        );

        // Membuat maksimal satu gelombang objek dalam satu frame.
        if (spawnTimer >= currentSpawnInterval) {

            // Membuat tiga objek muncul bersamaan.
            spawnWave(
                screenWidth,
                screenHeight,
                difficultyLevel
            );

            // Mengatur ulang timer agar tidak terjadi spawn beruntun.
            spawnTimer = 0f;
        }

        // Memperbarui posisi pemain
        if (player != null) {
            player.update(delta);
        }

        // Memperbarui posisi seluruh objek yang sedang aktif
        for (FallingObject object : fallingObjects) {
            object.update(delta);
        }

        // Memeriksa tabrakan dan objek yang keluar layar
        if (player != null) {

            collisionManager.checkCollisions(
                player,
                fallingObjects,
                scoreManager,
                objectsToRecycle
            );

            // Mengembalikan objek yang sudah tidak digunakan ke Object Pool
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


    // Membuat beberapa objek muncul secara bersamaan

    private void spawnWave(
        float screenWidth,
        float screenHeight,
        float difficultyLevel
    ) {

        // Membuat tiga objek dengan posisi X yang acak.
        for (int i = 0; i < OBJECTS_PER_WAVE; i++) {

            // Mengambil objek dari ObjectFactory.
            FallingObject newObject =
                objectFactory.obtainRandomObject(
                    screenWidth,
                    screenHeight,
                    difficultyLevel
                );

            // Memasukkan objek ke daftar objek aktif.
            fallingObjects.add(newObject);
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

    // Mengembalikan waktu permainan dari data yang tersimpan.
    public void restoreElapsedTime(float savedElapsedTime) {

        // Memastikan waktu yang diterima valid.
        if (Float.isNaN(savedElapsedTime)
            || Float.isInfinite(savedElapsedTime)
            || savedElapsedTime < 0f) {

            return;
        }

        // Mengembalikan total waktu permainan.
        elapsedTime = savedElapsedTime;

        // Mengatur ulang timer spawn.
        spawnTimer = 0f;
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

    // Mengembalikan saldo dan skor pemain.
    public void restoreBalanceAndScore(
        int savedBalance,
        int savedScore
    ) {

        // Mengembalikan data melalui ScoreManager.
        scoreManager.restoreProgress(
            savedBalance,
            savedScore
        );

        // Memastikan status permainan sesuai saldo.
        if (scoreManager.isGameOver()) {
            gameState = GameState.GAME_OVER;
        } else {
            gameState = GameState.PLAYING;
        }
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
