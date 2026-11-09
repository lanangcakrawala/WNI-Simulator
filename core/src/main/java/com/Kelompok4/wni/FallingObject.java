
package com.Kelompok4.wni;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class FallingObject implements Pool.Poolable {

    public enum Type {
        FIFTY,
        HUNDRED,
        GAMBLE,
        TAX,
    }

    private final Rectangle bounds = new Rectangle();

    private Type type;
    private float speed;


    public void init(float x, float y, float size, float speed, Type type) {
        bounds.set(x, y, size, size);
        this.speed = speed;
        this.type = type;
    }

    public void update(float delta) {
        bounds.y -= speed * delta;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Type getType() {
        return type;
    }

    public boolean isOutOfScreen() {
        return bounds.y + bounds.height < 0;
    }

    @Override
    public void reset() {
        bounds.set(0, 0, 0, 0);
        speed = 0;
        type = null;
    }
}
