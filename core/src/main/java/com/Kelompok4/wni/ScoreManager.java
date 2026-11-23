package com.Kelompok4.wni;

import java.text.NumberFormat;
import java.util.Locale;

public class ScoreManager {

    private int balance;
    private int score;

    public ScoreManager() {
        reset();
    }

    public void applyObject(FallingObject.Type type) {

        balance += type.getBalanceChange();
        score++;
    }

    public int getBalance() {
        return balance;
    }

    public int getScore() {
        return score;
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
        balance = 500000;
        score = 0;
    }
}
