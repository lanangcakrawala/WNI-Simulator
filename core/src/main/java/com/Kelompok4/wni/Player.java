package com.Kelompok4.wni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    private final Rectangle bounds;
    private final float speed = 450f;
    private final float screenWidth;

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
            0,
            screenWidth - bounds.width
        );
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void reset() {
        bounds.x = (screenWidth / 2f) - (bounds.width / 2f);
        bounds.y = 30f;
    }
}
