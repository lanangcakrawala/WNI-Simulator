
package com.Kelompok4.wni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class NewGameScreen extends ScreenAdapter {

    private static final int MAX_USERNAME_LENGTH = 20;

    private final WNISimulatorGame game;
    private final PlayerDataManager playerDataManager;

    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout layout;

    private final BitmapFont font;

    private String username = "";
    private String message = "";

    private boolean inputFocused = false;
    private boolean usernameConfirmed = false;
    private boolean usernameSaved = false;

    private Color messageColor = Color.WHITE;

    // Posisi komponen tampilan.
    private float panelX;
    private float panelY;
    private float panelWidth;
    private float panelHeight;

    private float inputX;
    private float inputY;
    private float inputWidth;
    private float inputHeight;

    private float checkX;
    private float checkY;
    private float checkWidth;
    private float checkHeight;

    private float saveX;
    private float saveY;
    private float saveWidth;
    private float saveHeight;

    private float startX;
    private float startY;
    private float startWidth;
    private float startHeight;

    private float backX;
    private float backY;
    private float backWidth;
    private float backHeight;

    public NewGameScreen(WNISimulatorGame game) {

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

        calculateLayout(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        setInputHandler();
    }

    @Override
    public void show() {

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
            public boolean keyTyped(char character) {

                if (!inputFocused || usernameSaved) {
                    return false;
                }

                if (Character.isLetterOrDigit(character)
                    || character == '_'
                    || character == '-') {

                    if (username.length() < MAX_USERNAME_LENGTH) {

                        username += character;

                        resetConfirmation();
                    }

                    return true;
                }

                if (character == ' ') {
                    message = "Username tidak boleh mengandung spasi.";
                    messageColor = Color.ORANGE;
                    return true;
                }

                return false;
            }

            @Override
            public boolean keyDown(int keycode) {

                if (keycode == Input.Keys.BACKSPACE) {

                    if (inputFocused && !usernameSaved
                        && !username.isEmpty()) {

                        username = username.substring(
                            0,
                            username.length() - 1
                        );

                        resetConfirmation();
                    }

                    return true;
                }

                if (keycode == Input.Keys.ENTER) {

                    if (!usernameConfirmed) {
                        confirmUsername();
                    }

                    return true;
                }

                if (keycode == Input.Keys.ESCAPE) {

                    returnToMainMenu();

                    return true;
                }

                return false;
            }
        });
    }

    private void setInputHandler() {

        inputFocused = false;
    }

    private void handleTouch(float x, float y) {

        // Tombol Back.
        if (contains(
            x, y,
            backX, backY,
            backWidth, backHeight
        )) {

            returnToMainMenu();
            return;
        }

        // Area input username.
        if (contains(
            x, y,
            inputX, inputY,
            inputWidth, inputHeight
        )) {

            if (!usernameSaved) {

                inputFocused = true;

                Gdx.input.setOnscreenKeyboardVisible(true);

                message = "";
            }

            return;
        }

        // Tombol checklist.
        if (contains(
            x, y,
            checkX, checkY,
            checkWidth, checkHeight
        )) {

            confirmUsername();
            return;
        }

        // Tombol SAVE.
        if (contains(
            x, y,
            saveX, saveY,
            saveWidth, saveHeight
        )) {

            saveUsername();
            return;
        }

        // Tombol START.
        if (contains(
            x, y,
            startX, startY,
            startWidth, startHeight
        )) {

            startGame();
        }
    }

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

    private void confirmUsername() {

        if (usernameSaved) {
            return;
        }

        String cleanUsername = username.trim();

        if (cleanUsername.isEmpty()) {

            setMessage(
                "Username tidak boleh kosong.",
                Color.ORANGE
            );

            return;
        }

        if (playerDataManager.isUsernameRegistered(
            cleanUsername
        )) {

            setMessage(
                "Username sudah terdaftar.",
                Color.ORANGE
            );

            usernameConfirmed = false;

            return;
        }

        if (playerDataManager.getPlayerCount()
            >= PlayerDataManager.MAX_PLAYERS) {

            setMessage(
                "Maksimal 5 pemain sudah tercapai.",
                Color.ORANGE
            );

            return;
        }

        username = cleanUsername;

        usernameConfirmed = true;

        setMessage(
            "Username berhasil dikonfirmasi.",
            Color.GREEN
        );
    }

    private void saveUsername() {

        if (usernameSaved) {
            return;
        }

        if (!usernameConfirmed) {

            setMessage(
                "Konfirmasi username terlebih dahulu.",
                Color.ORANGE
            );

            return;
        }

        boolean saved = playerDataManager.registerUsername(
            username
        );

        if (!saved) {

            setMessage(
                "Username gagal disimpan.",
                Color.ORANGE
            );

            usernameConfirmed = false;

            return;
        }

        usernameSaved = true;

        inputFocused = false;

        Gdx.input.setOnscreenKeyboardVisible(false);

        setMessage(
            "Username berhasil disimpan!",
            Color.GREEN
        );
    }

    private void startGame() {

        if (!usernameSaved) {

            setMessage(
                "Simpan username sebelum memulai permainan.",
                Color.ORANGE
            );

            return;
        }

        Gdx.input.setOnscreenKeyboardVisible(false);

        game.setScreen(new GameScreen(game));
    }

    private void returnToMainMenu() {

        Gdx.input.setOnscreenKeyboardVisible(false);

        game.setScreen(new MainMenuScreen(game));
    }

    private void resetConfirmation() {

        usernameConfirmed = false;

        message = "";
        messageColor = Color.WHITE;
    }

    private void setMessage(String text, Color color) {

        message = text;
        messageColor = color;
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

    private void drawBackground() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

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

        // Area input username.
        shapeRenderer.setColor(
            inputFocused
                ? new Color(0.25f, 0.35f, 0.50f, 1f)
                : new Color(0.20f, 0.23f, 0.32f, 1f)
        );

        shapeRenderer.rect(
            inputX,
            inputY,
            inputWidth,
            inputHeight
        );

        // Tombol checklist.
        shapeRenderer.setColor(
            usernameConfirmed
                ? Color.GREEN
                : new Color(0.25f, 0.55f, 0.85f, 1f)
        );

        shapeRenderer.rect(
            checkX,
            checkY,
            checkWidth,
            checkHeight
        );

        // Tombol SAVE.
        shapeRenderer.setColor(
            usernameConfirmed && !usernameSaved
                ? new Color(0.15f, 0.55f, 0.35f, 1f)
                : Color.DARK_GRAY
        );

        shapeRenderer.rect(
            saveX,
            saveY,
            saveWidth,
            saveHeight
        );

        // Tombol START.
        shapeRenderer.setColor(
            usernameSaved
                ? new Color(0.10f, 0.65f, 0.35f, 1f)
                : Color.DARK_GRAY
        );

        shapeRenderer.rect(
            startX,
            startY,
            startWidth,
            startHeight
        );

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

    private void drawInterface() {

        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();

        drawCentered(
            "NEW GAME",
            panelY + panelHeight - 55f,
            2f,
            Color.WHITE
        );

        drawCentered(
            "Enter your username",
            inputY + inputHeight + 35f,
            1f,
            Color.LIGHT_GRAY
        );

        String displayedUsername = username.isEmpty()
            ? "Username..."
            : username;

        font.setColor(
            username.isEmpty()
                ? Color.GRAY
                : Color.WHITE
        );

        font.getData().setScale(1.2f);

        font.draw(
            game.getBatch(),
            displayedUsername,
            inputX + 15f,
            inputY + 32f
        );

        font.getData().setScale(1f);

        drawCentered(
            usernameConfirmed ? "OK" : "✓",
            checkY + checkHeight / 2f + 8f,
            1.5f,
            Color.WHITE
        );

        drawCentered(
            "SAVE",
            saveY + saveHeight / 2f + 8f,
            1.2f,
            Color.WHITE
        );

        drawCentered(
            "START",
            startY + startHeight / 2f + 8f,
            1.2f,
            Color.WHITE
        );

        drawCentered(
            "BACK",
            backY + backHeight / 2f + 8f,
            1f,
            Color.WHITE
        );

        drawCentered(
            message,
            panelY + 45f,
            0.85f,
            messageColor
        );

        game.getBatch().end();
    }

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

    private void calculateLayout(int width, int height) {

        panelWidth = Math.min(520f, width - 40f);

        panelHeight = Math.min(430f, height - 40f);

        panelX = (width - panelWidth) / 2f;

        panelY = (height - panelHeight) / 2f;

        // Username input.
        inputWidth = panelWidth - 130f;
        inputHeight = 50f;

        inputX = panelX + 30f;
        inputY = panelY + panelHeight - 180f;

        // Checklist.
        checkWidth = 55f;
        checkHeight = 50f;

        checkX = panelX + panelWidth - 85f;
        checkY = inputY;

        // SAVE.
        saveWidth = 160f;
        saveHeight = 45f;

        saveX = (width - saveWidth) / 2f;
        saveY = panelY + 120f;

        // START.
        startWidth = 160f;
        startHeight = 45f;

        startX = (width - startWidth) / 2f;
        startY = panelY + 60f;

        // BACK.
        backWidth = 75f;
        backHeight = 35f;

        backX = width - backWidth - 20f;
        backY = height - backHeight - 20f;
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

        Gdx.input.setOnscreenKeyboardVisible(false);

        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

        shapeRenderer.dispose();
    }
}
