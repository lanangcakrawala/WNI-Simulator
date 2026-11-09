package com.Kelompok4.wni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

public class Player {

    private final Rectangle bounds;
    private final float speed = 450f;

    public Player(float screenWidth) {
        bounds = new Rectangle(
            screenWidth / 2f - 30,
            30,
            60,
            30
        );
    }

    public void update(float delta, float screenWidth) {

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
            0,
            screenWidth - bounds.width
        );
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void reset(float screenWidth) {
        bounds.x = screenWidth / 2f - bounds.width / 2f;
        bounds.y = 30;
    }
}
