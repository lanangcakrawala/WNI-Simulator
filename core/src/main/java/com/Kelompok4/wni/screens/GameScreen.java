
package com.Kelompok4.wni.screens;

import com.Kelompok4.wni.*;
import com.Kelompok4.wni.entities.FallingObject;
import com.Kelompok4.wni.entities.Player;
import com.Kelompok4.wni.factory.ObjectFactory;
import com.Kelompok4.wni.game.GameController;
import com.Kelompok4.wni.game.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.Kelompok4.wni.managers.PlayerDataManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

import java.util.Locale;

public class GameScreen extends ScreenAdapter {

    private static final float PLAYER_DRAW_WIDTH = 80f;
    private static final float PLAYER_DRAW_HEIGHT = 140f;

    private static final float OBJECT_DRAW_WIDTH = 88f;
    private static final float OBJECT_DRAW_HEIGHT = 40f;

    private final WNISimulatorGame game;
    private final OrthographicCamera camera;
    private final GlyphLayout layout;
    private final GameController controller;

    private ShapeRenderer shapeRenderer;

    // Mengelola penyimpanan data pemain.
    private final PlayerDataManager playerDataManager;

    // Menyimpan username pemain yang sedang bermain.
    private final String username;

    // Menandai apakah permainan dihentikan oleh lifecycle aplikasi.
    private boolean pausedByLifecycle = false;

    private Texture backgroundTexture;
    private Texture standardCharacterTexture;

    private Texture salaryTexture;
    private Texture bonusTexture;
    private Texture taxTexture;
    private Texture judolTexture;

    private boolean showHitboxes = true;

    private boolean disposed;


    public GameScreen(WNISimulatorGame game, String username) {

        this.game = game;
        this.username = username;

        shapeRenderer = new ShapeRenderer();

        // Membuat pengelola data pemain.
        playerDataManager = new PlayerDataManager();

        // Membuat GameController.
        controller = new GameController(new ObjectFactory());

        // Mengambil waktu permainan yang pernah disimpan.
        float savedElapsedTime =
            playerDataManager.getElapsedTime(username);

        // Mengembalikan waktu permainan sebelumnya.
        controller.restoreElapsedTime(savedElapsedTime);

        int savedBalance = playerDataManager.getBalance(username);
        int savedScore = playerDataManager.getScore(username);
        controller.restoreBalanceAndScore(savedBalance, savedScore);

        // Membuat Player.
        controller.setPlayer(
            new Player(Gdx.graphics.getWidth())
        );

        // Mengatur kamera.
        camera = new OrthographicCamera();

        camera.setToOrtho(
            false,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        layout = new GlyphLayout();

        // Memuat seluruh asset gameplay.
        backgroundTexture =
            loadTexture("wni-simulator_background.png");

        standardCharacterTexture =
            loadTexture("karakter_standar.png");

        salaryTexture =
            loadTexture("objek_50K.png");

        bonusTexture =
            loadTexture("objek_100K.png");

        taxTexture =
            loadTexture("objek_tax.png");

        judolTexture =
            loadTexture("objek_judol.png");
    }


    private Texture loadTexture(String fileName) {

        Texture texture = new Texture(
            Gdx.files.internal(fileName)
        );

        texture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        return texture;
    }

    @Override
    public void render(float delta) {

        if (handleInput()) {
            return;
        }

        // Memperbarui logika permainan.
        controller.update(
            delta,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        SpriteBatch batch = game.getBatch();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        drawBackground(batch);

        drawFallingObjects(batch);

        drawPlayer(batch);

        drawHud(batch);

        batch.end();

        if (showHitboxes) {
            drawHitboxes();
        }
    }


    // Menyimpan progress permainan pemain.
    private void saveProgress() {
        if (username == null || username.trim().isEmpty()) {
            return;
        }

        if (controller.getGameState() == GameState.GAME_OVER) {
            playerDataManager.saveElapsedTime(username, 0f);
            playerDataManager.saveBalanceAndScore(username, 500000, 0);
            Gdx.app.log("GameScreen", "Game Over. Progress waktu, saldo, dan skor direset.");
            return;
        }

        boolean savedTime = playerDataManager.saveElapsedTime(
            username,
            controller.getElapsedTime()
        );
        boolean savedStats = playerDataManager.saveBalanceAndScore(
            username,
            controller.getBalance(),
            controller.getScore()
        );

        if (!savedTime || !savedStats) {
            Gdx.app.error("GameScreen", "Gagal menyimpan progress permainan.");
        }
    }


    private boolean handleInput() {

        // Pause dan Resume.
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {

            if (controller.getGameState() == GameState.PLAYING) {

                controller.pause();

            } else if (
                controller.getGameState() == GameState.PAUSED
            ) {

                controller.resume();
            }
        }

        // Restart ketika Game Over.
        if (
            controller.getGameState() == GameState.GAME_OVER
                && Gdx.input.isKeyJustPressed(Input.Keys.R)
        ) {

            controller.reset();
        }

        // Kembali ke Main Menu.
        // Menyimpan progress sebelum kembali ke Main Menu.
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            // Menyimpan waktu permainan.
            saveProgress();

            // Kembali ke Main Menu.
            game.setScreen(new MainMenuScreen(game));

            return true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showHitboxes = !showHitboxes;
        }

        return false;
    }

    private void drawBackground(SpriteBatch batch) {

        batch.draw(
            backgroundTexture,
            0f,
            0f,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
    }

    private void drawPlayer(SpriteBatch batch) {

        Player player = controller.getPlayer();

        if (player == null) {
            return;
        }

        float x =
            player.getBounds().x
                + player.getBounds().width / 2f
                - PLAYER_DRAW_WIDTH / 2f;

        float y = player.getBounds().y - 45f;

        batch.draw(
            standardCharacterTexture,
            x,
            y,
            PLAYER_DRAW_WIDTH,
            PLAYER_DRAW_HEIGHT
        );
    }

    private void drawFallingObjects(SpriteBatch batch) {

        for (
            FallingObject object :
            controller.getFallingObjects()
        ) {

            Texture texture =
                getObjectTexture(object.getType());

            if (texture == null) {
                continue;
            }

            float x =
                object.getBounds().x
                    + object.getBounds().width / 2f
                    - OBJECT_DRAW_WIDTH / 2f;

            float y =
                object.getBounds().y
                    + object.getBounds().height / 2f
                    - OBJECT_DRAW_HEIGHT / 2f;

            batch.draw(
                texture,
                x,
                y,
                OBJECT_DRAW_WIDTH,
                OBJECT_DRAW_HEIGHT
            );
        }
    }

    private Texture getObjectTexture(FallingObject.Type type) {

        if (type == null) {
            return null;
        }

        switch (type) {

            case SALARY:
                return salaryTexture;

            case BONUS:
                return bonusTexture;

            case TAX:
                return taxTexture;

            case JUDOL:
                return judolTexture;

            default:
                return null;
        }
    }

    private void drawHud(SpriteBatch batch) {

        float width = Gdx.graphics.getWidth();

        float height = Gdx.graphics.getHeight();

        // Informasi saldo.
        drawText(
            batch,
            "Saldo: " + controller.getFormattedBalance(),
            20f,
            height - 25f,
            1.15f
        );

        // Informasi skor.
        drawText(
            batch,
            "Objek tertangkap: " + controller.getScore(),
            20f,
            height - 55f,
            1f
        );

        // Informasi waktu.
        String time = String.format(
            Locale.US,
            "%.1f",
            controller.getElapsedTime()
        );

        drawText(
            batch,
            "Waktu: " + time + " detik",
            width - 190f,
            height - 25f,
            1f
        );

        drawCentered(
            batch,
            "P: Pause   |   ESC: Menu",
            height - 55f,
            0.9f
        );

        // Tampilan Pause.
        if (controller.getGameState() == GameState.PAUSED) {

            drawCentered(
                batch,
                "PAUSED",
                height * 0.55f,
                2.2f
            );

            drawCentered(
                batch,
                "Tekan P untuk melanjutkan",
                height * 0.48f,
                1.1f
            );
        }

        // Tampilan Game Over.
        else if (
            controller.getGameState() == GameState.GAME_OVER
        ) {

            drawCentered(
                batch,
                "GAME OVER",
                height * 0.55f,
                2.2f
            );

            drawCentered(
                batch,
                "Tekan R untuk bermain kembali",
                height * 0.48f,
                1.1f
            );

            drawCentered(
                batch,
                "Saldo akhir: " + controller.getFormattedBalance(),
                height * 0.41f,
                1f
            );
        }
    }

    private void drawText(
        SpriteBatch batch,
        String text,
        float x,
        float y,
        float scale
    ) {

        BitmapFont font = game.getFont();

        font.getData().setScale(scale);

        font.setColor(
            com.badlogic.gdx.graphics.Color.WHITE
        );

        font.draw(batch, text, x, y);

        font.getData().setScale(1f);
    }

    private void drawCentered(
        SpriteBatch batch,
        String text,
        float y,
        float scale
    ) {

        BitmapFont font = game.getFont();

        font.getData().setScale(scale);

        font.setColor(
            com.badlogic.gdx.graphics.Color.WHITE
        );

        layout.setText(font, text);

        float x =
            (Gdx.graphics.getWidth() - layout.width) / 2f;

        font.draw(batch, text, x, y);

        font.getData().setScale(1f);
    }

    @Override
    public void resize(int width, int height) {

        if (width <= 0 || height <= 0) {
            return;
        }

        camera.setToOrtho(false, width, height);

        camera.update();

        if (controller.getPlayer() != null) {

            controller.getPlayer().resize(width);
        }
    }

    // Menghentikan gameplay ketika aplikasi masuk ke background.
    @Override
    public void pause() {

        // Menyimpan progress sebelum aplikasi masuk background.
        saveProgress();

        // Mencatat apakah permainan sedang berjalan.
        pausedByLifecycle =
            controller.getGameState() == GameState.PLAYING;

        // Menghentikan permainan sementara.
        controller.pause();
    }

    @Override
    public void resume() {

        // Melanjutkan permainan hanya jika sebelumnya
        // dihentikan oleh lifecycle aplikasi.
        if (pausedByLifecycle) {

            controller.resume();

            pausedByLifecycle = false;
        }
    }

    @Override
    public void hide() {

        dispose();
    }

    @Override
    public void dispose() {

        if (disposed) {
            return;
        }

        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        if (standardCharacterTexture != null) {
            standardCharacterTexture.dispose();
        }

        if (salaryTexture != null) {
            salaryTexture.dispose();
        }

        if (bonusTexture != null) {
            bonusTexture.dispose();
        }

        if (taxTexture != null) {
            taxTexture.dispose();
        }

        if (judolTexture != null) {
            judolTexture.dispose();
        }

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        disposed = true;
    }

    private void drawHitboxes() {

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Hitbox Player (Merah)
        shapeRenderer.setColor(Color.RED);

        Player player = controller.getPlayer();

        if (player != null) {
            shapeRenderer.rect(
                player.getBounds().x,
                player.getBounds().y,
                player.getBounds().width,
                player.getBounds().height
            );
        }

        // Hitbox FallingObject (Hijau)
        shapeRenderer.setColor(Color.GREEN);

        for (FallingObject object : controller.getFallingObjects()) {

            shapeRenderer.rect(
                object.getBounds().x,
                object.getBounds().y,
                object.getBounds().width,
                object.getBounds().height
            );
        }

        shapeRenderer.end();
    }
}


