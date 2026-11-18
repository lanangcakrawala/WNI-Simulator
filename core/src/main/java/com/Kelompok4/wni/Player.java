package com.Kelompok4.wni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    private final Rectangle bounds;
    private final float speed = 450f;
    private float screenWidth;

    public Player(float screenWidth) {
        this.screenWidth = screenWidth;
        float width = 60f;
        float height = 30f;

        bounds = new Rectangle(
            (screenWidth / 2f) - (width / 2f),
            30f,
            width,
            height
        );
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.A) ||
            Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bounds.x -= speed * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D) ||
            Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bounds.x += speed * delta;
        }

        bounds.x = MathUtils.clamp(
            bounds.x,
            0f,
            Math.max(0f, screenWidth - bounds.width)
        );
    }
    // Menyesuaikan batas gerak Player ketika ukuran layar berubah
    public void resize(float screenWidth) {

        // Mengabaikan ukuran layar yang tidak valid
        if (screenWidth <= 0f) {
            return;
        }

        // Memperbarui lebar layar
        this.screenWidth = screenWidth;

        // Memastikan Player tetap berada di dalam layar
        bounds.x = MathUtils.clamp(
            bounds.x,
            0f,
            Math.max(0f, screenWidth - bounds.width)
        );
    }
    public Rectangle getBounds() {
        return bounds;
    }

    // Mengembalikan Player ke posisi awal
    public void reset() {

        // Menempatkan Player di tengah layar
        bounds.x = Math.max(
            0f,
            (screenWidth - bounds.width) / 2f
        );

        // Mengembalikan Player ke posisi vertikal awal
        bounds.y = 30f;
    }
}
