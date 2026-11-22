
package com.Kelompok4.wni.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class FallingObject implements Pool.Poolable {

    // Menentukan perubahan saldo untuk setiap jenis objek
    public enum Type {

        // Objek yang menambah saldo
        SALARY(50000),
        BONUS(50000),

        // Objek yang mengurangi saldo
        TAX(-100000),
        JUDOL(-200000);

        private final int balanceChange;

        Type(int balanceChange) {
            this.balanceChange = balanceChange;
        }

        // Mengambil perubahan saldo dari jenis objek
        public int getBalanceChange() {
            return balanceChange;
        }
    }

    private final Rectangle bounds = new Rectangle();

    private Type type;

    private float speed;

    // Menginisialisasi ulang instance yang diperoleh dari pool.
    public void init(
        float x,
        float y,
        float size,
        float speed,
        Type type
    ) {
        bounds.set(x, y, size, size);
        this.speed = speed;
        this.type = type;
    }

    // Memperbarui posisi objek.
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

    // Dipanggil otomatis ketika instance dikembalikan ke pool.
    @Override
    public void reset() {
        bounds.set(0, 0, 0, 0);
        speed = 0;
        type = null;
    }
}
