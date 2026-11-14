
package com.Kelompok4.wni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class ContinueScreen extends ScreenAdapter {

    private static final int MAX_PLAYERS =
        PlayerDataManager.MAX_PLAYERS;

    private final WNISimulatorGame game;
    private final PlayerDataManager playerDataManager;

    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout layout;
    private final BitmapFont font;

    private Array<String> usernames;

    private float panelX;
    private float panelY;
    private float panelWidth;
    private float panelHeight;

    private float backX;
    private float backY;
    private float backWidth;
    private float backHeight;

    private final float[] slotX;
    private final float[] slotY;

    private float slotWidth;
    private float slotHeight;

    private float playWidth;
    private float playHeight;

    public ContinueScreen(WNISimulatorGame game) {

        this.game = game;

        playerDataManager = new PlayerDataManager();

        camera = new OrthographicCamera();

        camera.setToOrtho(
            false,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        shapeRenderer = new ShapeRenderer();

        layout = new GlyphLayout();

        font = game.getFont();

        usernames = new Array<>();

        slotX = new float[MAX_PLAYERS];
        slotY = new float[MAX_PLAYERS];

        calculateLayout(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
    }

    @Override
    public void show() {

        refreshUsernames();

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(
                int screenX,
                int screenY,
                int pointer,
                int button
            ) {

                if (button != Input.Buttons.LEFT) {
                    return false;
                }

                float x = screenX;
                float y = Gdx.graphics.getHeight() - screenY;

                handleTouch(x, y);

                return true;
            }

            @Override
            public boolean keyDown(int keycode) {

                if (keycode == Input.Keys.ESCAPE) {
                    returnToMainMenu();
                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Mengambil daftar username yang sudah tersimpan.
     */
    private void refreshUsernames() {

        usernames = playerDataManager.getRegisteredUsernames();
    }

    /**
     * Memproses klik tombol Back dan Play.
     */
    private void handleTouch(float x, float y) {

        if (contains(
            x, y,
            backX, backY,
            backWidth, backHeight
        )) {

            returnToMainMenu();
            return;
        }

        for (int i = 0; i < usernames.size; i++) {

            float playX = slotX[i]
                + slotWidth - playWidth - 15f;

            float playY = slotY[i]
                + (slotHeight - playHeight) / 2f;

            if (contains(
                x, y,
                playX, playY,
                playWidth, playHeight
            )) {

                selectPlayer(usernames.get(i));
                return;
            }
        }
    }

    /**
     * Memilih pemain berdasarkan username.
     */
    private void selectPlayer(String username) {

        if (username == null || username.trim().isEmpty()) {
            return;
        }

        Gdx.input.setInputProcessor(null);

        /*
         * Integrasi pemuatan progress berdasarkan username
         * dilakukan pada tahap integrasi GameScreen.
         */
        game.setScreen(new GameScreen(game));
    }

    /**
     * Kembali ke MainMenuScreen.
     */
    private void returnToMainMenu() {

        Gdx.input.setInputProcessor(null);

        game.setScreen(new MainMenuScreen(game));
    }

    /**
     * Memeriksa apakah titik berada di dalam area tombol.
     */
    private boolean contains(
        float x,
        float y,
        float rectX,
        float rectY,
        float rectWidth,
        float rectHeight
    ) {

        return x >= rectX
            && x <= rectX + rectWidth
            && y >= rectY
            && y <= rectY + rectHeight;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(
            0.05f,
            0.05f,
            0.12f,
            1f
        );

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);

        drawBackground();

        drawInterface();
    }

    /**
     * Menggambar background dan komponen berbentuk persegi.
     */
    private void drawBackground() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background utama.
        shapeRenderer.setColor(
            new Color(0.08f, 0.10f, 0.18f, 1f)
        );

        shapeRenderer.rect(
            0,
            0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        // Panel utama.
        shapeRenderer.setColor(
            new Color(0.13f, 0.16f, 0.25f, 1f)
        );

        shapeRenderer.rect(
            panelX,
            panelY,
            panelWidth,
            panelHeight
        );

        // Slot username.
        for (int i = 0; i < MAX_PLAYERS; i++) {

            shapeRenderer.setColor(
                new Color(0.20f, 0.23f, 0.32f, 1f)
            );

            shapeRenderer.rect(
                slotX[i],
                slotY[i],
                slotWidth,
                slotHeight
            );

            // Tombol Play hanya untuk slot terisi.
            if (i < usernames.size) {

                float playX = slotX[i]
                    + slotWidth - playWidth - 15f;

                float playY = slotY[i]
                    + (slotHeight - playHeight) / 2f;

                shapeRenderer.setColor(
                    new Color(0.10f, 0.60f, 0.35f, 1f)
                );

                shapeRenderer.rect(
                    playX,
                    playY,
                    playWidth,
                    playHeight
                );
            }
        }

        // Tombol Back.
        shapeRenderer.setColor(
            new Color(0.65f, 0.20f, 0.25f, 1f)
        );

        shapeRenderer.rect(
            backX,
            backY,
            backWidth,
            backHeight
        );

        shapeRenderer.end();
    }

    /**
     * Menggambar seluruh teks pada layar.
     */
    private void drawInterface() {

        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();

        drawCentered(
            "CONTINUE",
            panelY + panelHeight - 45f,
            2f,
            Color.WHITE
        );

        if (usernames.isEmpty()) {

            drawCentered(
                "Belum ada pemain yang terdaftar.",
                panelY + panelHeight / 2f,
                1f,
                Color.LIGHT_GRAY
            );

            drawCentered(
                "Silakan buat pemain baru terlebih dahulu.",
                panelY + panelHeight / 2f - 35f,
                0.8f,
                Color.GRAY
            );

        } else {

            for (int i = 0; i < usernames.size; i++) {
                drawPlayerSlot(i, usernames.get(i));
            }
        }

        drawCentered(
            "BACK",
            backY + backHeight / 2f + 7f,
            0.9f,
            Color.WHITE
        );

        game.getBatch().end();
    }

    /**
     * Menggambar username dan tombol Play pada satu slot.
     */
    private void drawPlayerSlot(int index, String username) {

        float textX = slotX[index] + 20f;

        float textY = slotY[index]
            + slotHeight / 2f + 8f;

        font.getData().setScale(1f);
        font.setColor(Color.WHITE);

        font.draw(
            game.getBatch(),
            username,
            textX,
            textY
        );

        // Menghitung posisi tombol Play.
        float playX = slotX[index]
            + slotWidth - playWidth - 15f;

        float playY = slotY[index]
            + (slotHeight - playHeight) / 2f;

        // Menghitung posisi teks PLAY agar tepat di tengah tombol.
        font.getData().setScale(0.8f);
        layout.setText(font, "PLAY");

        float textPlayX = playX
            + (playWidth - layout.width) / 2f;

        float textPlayY = playY
            + (playHeight + layout.height) / 2f;

        font.draw(
            game.getBatch(),
            "PLAY",
            textPlayX,
            textPlayY
        );

        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }

    /**
     * Menggambar teks dengan posisi horizontal di tengah layar.
     */
    private void drawCentered(
        String text,
        float y,
        float scale,
        Color color
    ) {

        if (text == null || text.isEmpty()) {
            return;
        }

        font.getData().setScale(scale);
        font.setColor(color);

        layout.setText(font, text);

        float x = (
            Gdx.graphics.getWidth() - layout.width
        ) / 2f;

        font.draw(
            game.getBatch(),
            text,
            x,
            y
        );

        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }

    /**
     * Mengatur posisi komponen berdasarkan ukuran layar.
     */
    private void calculateLayout(int width, int height) {

        panelWidth = Math.min(600f, width - 40f);
        panelHeight = Math.min(560f, height - 40f);

        panelX = (width - panelWidth) / 2f;
        panelY = (height - panelHeight) / 2f;

        // Tombol Back.
        backWidth = 75f;
        backHeight = 35f;

        backX = width - backWidth - 20f;
        backY = height - backHeight - 20f;

        // Slot username.
        slotWidth = panelWidth - 60f;

        slotHeight = Math.min(
            65f,
            (panelHeight - 180f) / 5f
        );

        // Tombol Play.
        playWidth = 85f;
        playHeight = 40f;

        float firstSlotY = panelY + panelHeight - 145f;

        float slotSpacing = slotHeight + 12f;

        for (int i = 0; i < MAX_PLAYERS; i++) {

            slotX[i] = panelX + 30f;

            slotY[i] = firstSlotY - (i * slotSpacing);
        }
    }

    @Override
    public void resize(int width, int height) {

        if (width <= 0 || height <= 0) {
            return;
        }

        camera.setToOrtho(false, width, height);

        camera.update();

        calculateLayout(width, height);
    }

    @Override
    public void hide() {

        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

        shapeRenderer.dispose();
    }
}
