package com.Kelompok4.wni.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    // Rasio ukuran & posisi relatif terhadap layar (bisa disesuaikan sesuai kebutuhan game)
    private static final float WIDTH_RATIO = 0.08f;      // ~8% dari lebar layar
    private static final float ASPECT_RATIO = 40f / 60f; // Menjaga proporsi tinggi : lebar
    private static final float MIN_WIDTH = 36f;
    private static final float MAX_WIDTH = 160f;
    private static final float INITIAL_Y_RATIO = 0.10f; // ~10% dari tinggi layar saat awal
    private static final float RESET_Y_RATIO = 0.06f;   // ~6% dari tinggi layar saat reset
    private static final float BASE_SPEED_RATIO = 0.55f;// Kecepatan gerak per detik relatif ke lebar layar

    private final Rectangle bounds;
    private float screenWidth;
    private float screenHeight;
    private float speed;

    // Constructor utama dengan lebar & tinggi layar
    public Player(float screenWidth, float screenHeight) {
        this.screenWidth = Math.max(1f, screenWidth);
        this.screenHeight = Math.max(1f, screenHeight);

        float width = calculateWidth(this.screenWidth);
        float height = calculateHeight(width);

        this.bounds = new Rectangle(
            (this.screenWidth / 2f) - (width / 2f),
            this.screenHeight * INITIAL_Y_RATIO,
            width,
            height
        );
        this.speed = this.screenWidth * BASE_SPEED_RATIO;
    }

    // Overload constructor kalau di kelas lain masih manggil Player(screenWidth) saja
    public Player(float screenWidth) {
        this(screenWidth, Gdx.graphics != null && Gdx.graphics.getHeight() > 0
            ? Gdx.graphics.getHeight()
            : screenWidth * 0.75f);
    }

    private float calculateWidth(float width) {
        return MathUtils.clamp(width * WIDTH_RATIO, MIN_WIDTH, MAX_WIDTH);
    }

    private float calculateHeight(float width) {
        return width * ASPECT_RATIO;
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

        clampToScreen();
    }

    // Menyesuaikan ukuran, kecepatan, dan batas gerak Player ketika ukuran layar berubah
    public void resize(float screenWidth, float screenHeight) {
        if (screenWidth <= 0f || screenHeight <= 0f) {
            return;
        }

        // Menjaga posisi horizontal relatif agar player tetap di porsi layar yang wajar
        float previousWidth = Math.max(1f, this.screenWidth);
        float xRatio = bounds.x / previousWidth;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        float newWidth = calculateWidth(screenWidth);
        float newHeight = calculateHeight(newWidth);

        bounds.width = newWidth;
        bounds.height = newHeight;
        bounds.x = xRatio * screenWidth;
        bounds.y = MathUtils.clamp(bounds.y, 0f, Math.max(0f, screenHeight - bounds.height));
        this.speed = screenWidth * BASE_SPEED_RATIO;

        clampToScreen();
    }

    // Overload resize(screenWidth) kalau hanya lebar layar yang dikirim
    public void resize(float screenWidth) {
        float currentHeight = (Gdx.graphics != null && Gdx.graphics.getHeight() > 0)
            ? Gdx.graphics.getHeight()
            : this.screenHeight;
        resize(screenWidth, currentHeight);
    }

    private void clampToScreen() {
        bounds.x = MathUtils.clamp(
            bounds.x,
            0f,
            Math.max(0f, screenWidth - bounds.width)
        );
        bounds.y = MathUtils.clamp(
            bounds.y,
            0f,
            Math.max(0f, screenHeight - bounds.height)
        );
    }

    public Rectangle getBounds() {
        return bounds;
    }

    // Mengembalikan Player ke posisi awal
    public void reset() {
        float width = calculateWidth(screenWidth);
        float height = calculateHeight(width);

        bounds.width = width;
        bounds.height = height;
        bounds.x = Math.max(0f, (screenWidth - bounds.width) / 2f);
        bounds.y = MathUtils.clamp(
            screenHeight * RESET_Y_RATIO,
            0f,
            Math.max(0f, screenHeight - bounds.height)
        );
    }

    public void renderBounds(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(
            bounds.x,
            bounds.y,
            bounds.width,
            bounds.height
        );
    }
}
