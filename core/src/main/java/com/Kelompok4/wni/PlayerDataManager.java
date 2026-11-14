
package com.Kelompok4.wni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

/**
 * Mengelola data pemain dan penyimpanan username.
 *
 * Fitur:
 * - Maksimal lima pemain.
 * - Mencegah username kosong.
 * - Mencegah username duplikat.
 * - Menyimpan username secara permanen.
 * - Menyimpan dan mengambil waktu permainan.
 */
public class PlayerDataManager {

    // Batas maksimal jumlah pemain.
    public static final int MAX_PLAYERS = 5;

    // Nama penyimpanan lokal.
    private static final String PREFERENCES_NAME =
        "WNI_Simulator_PlayerData";

    // Kunci penyimpanan jumlah pemain.
    private static final String KEY_PLAYER_COUNT =
        "player_count";

    // Awalan kunci penyimpanan data pemain.
    private static final String KEY_USERNAME =
        "player_username_";

    private static final String KEY_ELAPSED_TIME =
        "player_elapsed_time_";

    // Objek untuk mengakses penyimpanan lokal.
    private final Preferences preferences;

    /**
     * Constructor PlayerDataManager.
     */
    public PlayerDataManager() {

        if (Gdx.app == null) {
            throw new IllegalStateException(
                "libGDX application belum diinisialisasi."
            );
        }

        preferences = Gdx.app.getPreferences(
            PREFERENCES_NAME
        );
    }

    /**
     * Mendaftarkan username pemain baru.
     *
     * @param username username yang akan didaftarkan.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean registerUsername(String username) {

        // Memastikan username tidak kosong.
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        String cleanUsername = username.trim();

        // Menolak username yang sudah terdaftar.
        if (isUsernameRegistered(cleanUsername)) {
            return false;
        }

        // Memeriksa batas maksimal pemain.
        int playerCount = getPlayerCount();

        if (playerCount >= MAX_PLAYERS) {
            return false;
        }

        // Menentukan slot pemain baru.
        String usernameKey = KEY_USERNAME + playerCount;

        String elapsedTimeKey = KEY_ELAPSED_TIME + playerCount;

        // Menyimpan username.
        preferences.putString(
            usernameKey,
            cleanUsername
        );

        // Menginisialisasi waktu permainan.
        preferences.putFloat(
            elapsedTimeKey,
            0f
        );

        // Memperbarui jumlah pemain.
        preferences.putInteger(
            KEY_PLAYER_COUNT,
            playerCount + 1
        );

        // Menyimpan perubahan secara permanen.
        preferences.flush();

        return true;
    }

    /**
     * Memeriksa apakah username sudah terdaftar.
     *
     * Pemeriksaan tidak membedakan huruf besar
     * dan huruf kecil.
     *
     * @param username username yang diperiksa.
     * @return true jika sudah terdaftar.
     */
    public boolean isUsernameRegistered(String username) {

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        String targetUsername = username.trim();

        int playerCount = getPlayerCount();

        for (int i = 0; i < playerCount; i++) {

            String registeredUsername =
                preferences.getString(
                    KEY_USERNAME + i,
                    ""
                );

            if (registeredUsername.equalsIgnoreCase(
                targetUsername
            )) {
                return true;
            }
        }

        return false;
    }

    /**
     * Mengambil seluruh username yang sudah terdaftar.
     *
     * @return Array berisi username pemain.
     */
    public Array<String> getRegisteredUsernames() {

        Array<String> usernames = new Array<>();

        int playerCount = getPlayerCount();

        for (int i = 0; i < playerCount; i++) {

            String username = preferences.getString(
                KEY_USERNAME + i,
                ""
            );

            if (!username.isEmpty()) {
                usernames.add(username);
            }
        }

        return usernames;
    }

    /**
     * Mengambil jumlah pemain yang terdaftar.
     *
     * @return jumlah pemain.
     */
    public int getPlayerCount() {

        int playerCount = preferences.getInteger(
            KEY_PLAYER_COUNT,
            0
        );

        // Melindungi data dari jumlah yang tidak valid.
        return Math.max(
            0,
            Math.min(playerCount, MAX_PLAYERS)
        );
    }

    /**
     * Menyimpan waktu permainan berdasarkan username.
     *
     * @param username username pemain.
     * @param elapsedTime total waktu permainan.
     * @return true jika berhasil disimpan.
     */
    public boolean saveElapsedTime(
        String username,
        float elapsedTime
    ) {

        if (username == null || Float.isNaN(elapsedTime)
            || Float.isInfinite(elapsedTime)
            || elapsedTime < 0f) {
            return false;
        }

        int playerIndex = findPlayerIndex(username);

        if (playerIndex == -1) {
            return false;
        }

        preferences.putFloat(
            KEY_ELAPSED_TIME + playerIndex,
            elapsedTime
        );

        preferences.flush();

        return true;
    }

    /**
     * Mengambil waktu permainan berdasarkan username.
     *
     * @param username username pemain.
     * @return waktu permainan, atau 0 jika tidak ditemukan.
     */
    public float getElapsedTime(String username) {

        if (username == null) {
            return 0f;
        }

        int playerIndex = findPlayerIndex(username);

        if (playerIndex == -1) {
            return 0f;
        }

        return preferences.getFloat(
            KEY_ELAPSED_TIME + playerIndex,
            0f
        );
    }

    /**
     * Mencari posisi pemain berdasarkan username.
     *
     * @param username username pemain.
     * @return indeks pemain, atau -1 jika tidak ditemukan.
     */
    private int findPlayerIndex(String username) {

        if (username == null) {
            return -1;
        }

        String targetUsername = username.trim();

        int playerCount = getPlayerCount();

        for (int i = 0; i < playerCount; i++) {

            String registeredUsername =
                preferences.getString(
                    KEY_USERNAME + i,
                    ""
                );

            if (registeredUsername.equalsIgnoreCase(
                targetUsername
            )) {
                return i;
            }
        }

        return -1;
    }
}
