package com.Kelompok4.wni.managers;

import com.Kelompok4.wni.entities.FallingObject;

import java.text.NumberFormat;
import java.util.Locale;

public class ScoreManager {

    public static final int DEFAULT_INITIAL_BALANCE = 500000;

    private int balance;
    private int score;

    public ScoreManager() {
        reset();
    }

    public void applyObject(FallingObject.Type type) {

        balance += type.getBalanceChange();
        score = Math.max(0, score + 1);
    }

    public int getBalance() {
        return balance;
    }

    public int getScore() {
        return score;
    }

    /**
     * Mengembalikan saldo dan skor dari data yang tersimpan.
     * @param savedBalance saldo terakhir pemain.
     * @param savedScore jumlah objek yang berhasil ditangkap.
     */
    public void restoreProgress(int savedBalance, int savedScore) {

        // Mengembalikan saldo yang tersimpan.
        balance = Math.max(0, savedBalance);

        // Mengembalikan skor yang tersimpan.
        score = Math.max(0, savedScore);
    }


    public boolean isGameOver() {
        return balance <= 0;
    }

    public String getFormattedBalance() {
        NumberFormat format =
            NumberFormat.getInstance(new Locale("id", "ID"));

        return "Rp " + format.format(balance);
    }

    public void reset() {
        balance = DEFAULT_INITIAL_BALANCE;;
        score = 0;
    }
}
