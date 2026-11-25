
package com.Kelompok4.wni.screens;

import com.Kelompok4.wni.managers.PlayerDataManager;
import com.Kelompok4.wni.WNISimulatorGame;
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

    private boolean disposed = false;

    // Mengatur ukuran tombol Delete
    private float deleteWidth;
    private float deleteHeight;

    // Menentukan apakah dialog konfirmasi sedang ditampilkan
    private boolean showDeleteConfirmation = false;

    // Menyimpan username pemain yang akan dihapus
    private String pendingDeleteUsername;

    // Menyimpan pesan jika penghapusan gagal
    private String deleteMessage = "";

    // Mengatur posisi dan ukuran dialog konfirmasi
    private float dialogX;
    private float dialogY;
    private float dialogWidth;
    private float dialogHeight;

    // Mengatur posisi tombol konfirmasi dan batal
    private float confirmX;
    private float confirmY;
    private float cancelX;
    private float cancelY;

    // Mengatur ukuran tombol dialog
    private float dialogButtonWidth;
    private float dialogButtonHeight;

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
        // Jika dialog konfirmasi sedang terbuka,
        // hanya tombol konfirmasi dan batal yang bisa digunakan
        if (showDeleteConfirmation) {

            // Jika tombol konfirmasi ditekan
            if (contains(
                x, y,
                confirmX, confirmY,
                dialogButtonWidth, dialogButtonHeight
            )) {

                confirmDeletePlayer();
                return;
            }

            // Jika tombol batal ditekan
            if (contains(
                x, y,
                cancelX, cancelY,
                dialogButtonWidth, dialogButtonHeight
            )) {

                // Menutup dialog tanpa menghapus pemain
                showDeleteConfirmation = false;
                pendingDeleteUsername = null;
                deleteMessage = "";
                return;
            }

            // Mengabaikan klik lain selama dialog terbuka
            return;
        }

        if (contains(
            x, y,
            backX, backY,
            backWidth, backHeight
        )) {

            returnToMainMenu();
            return;
        }


        for (int i = 0; i < usernames.size; i++) {

            // Menghitung posisi tombol Delete sesuai posisi visual.
            float deleteX = slotX[i]
                + slotWidth - deleteWidth - 15f;

            float deleteY = slotY[i]
                + (slotHeight - deleteHeight) / 2f;

            // Menghitung posisi tombol Play di sebelah kiri Delete.
            float playX = deleteX - playWidth - 10f;

            float playY = slotY[i]
                + (slotHeight - playHeight) / 2f;

            // Memeriksa apakah tombol Delete ditekan.
            if (contains(
                x, y,
                deleteX, deleteY,
                deleteWidth, deleteHeight
            )) {

                // Menampilkan dialog konfirmasi penghapusan.
                requestDelete(usernames.get(i));
                return;
            }

            // Memeriksa apakah tombol Play ditekan.
            if (contains(
                x, y,
                playX, playY,
                playWidth, playHeight
            )) {

                // Memulai permainan dengan pemain yang dipilih.
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
        // Melanjutkan permainan berdasarkan username yang dipilih.
        game.setScreen(new GameScreen(game, username));
    }
    // Menampilkan dialog konfirmasi penghapusan pemain
    private void requestDelete(String username) {

        // Menyimpan username yang akan dihapus
        pendingDeleteUsername = username;

        // Menampilkan dialog konfirmasi
        showDeleteConfirmation = true;

        // Mengosongkan pesan error sebelumnya
        deleteMessage = "";

        // Menghitung posisi dialog
        calculateConfirmationLayout();
    }


    // Menghitung posisi dan ukuran dialog berdasarkan layar
    private void calculateConfirmationLayout() {

        dialogWidth = Math.min(
            420f,
            Gdx.graphics.getWidth() - 40f
        );

        dialogHeight = 190f;

        // Menempatkan dialog di tengah layar
        dialogX = (Gdx.graphics.getWidth() - dialogWidth) / 2f;
        dialogY = (Gdx.graphics.getHeight() - dialogHeight) / 2f;

        // Mengatur posisi dua tombol dialog
        float gap = 20f;

        confirmX = dialogX
            + (dialogWidth - (2f * dialogButtonWidth + gap)) / 2f;

        cancelX = confirmX + dialogButtonWidth + gap;

        confirmY = dialogY + 25f;
        cancelY = confirmY;
    }

    // Menggambar dialog konfirmasi sebelum pemain dihapus
    private void drawConfirmationDialog() {

        // Menggambar lapisan gelap di belakang dialog
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0f, 0f, 0f, 0.65f);

        shapeRenderer.rect(
            0f,
            0f,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        // Menggambar panel dialog
        shapeRenderer.setColor(
            new Color(0.13f, 0.16f, 0.25f, 1f)
        );

        shapeRenderer.rect(
            dialogX,
            dialogY,
            dialogWidth,
            dialogHeight
        );

        // Menggambar tombol konfirmasi berwarna merah
        shapeRenderer.setColor(
            new Color(0.75f, 0.18f, 0.22f, 1f)
        );

        shapeRenderer.rect(
            confirmX,
            confirmY,
            dialogButtonWidth,
            dialogButtonHeight
        );

        // Menggambar tombol batal berwarna abu-abu
        shapeRenderer.setColor(
            new Color(0.30f, 0.34f, 0.43f, 1f)
        );

        shapeRenderer.rect(
            cancelX,
            cancelY,
            dialogButtonWidth,
            dialogButtonHeight
        );

        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Menggambar teks dialog
        game.getBatch().begin();

        drawCentered(
            "HAPUS PEMAIN?",
            dialogY + dialogHeight - 40f,
            1.5f,
            Color.WHITE
        );

        drawCentered(
            "Hapus data " + pendingDeleteUsername + "?",
            dialogY + dialogHeight - 85f,
            1f,
            Color.LIGHT_GRAY
        );

        // Menampilkan pesan jika penghapusan gagal
        if (!deleteMessage.isEmpty()) {

            drawCentered(
                deleteMessage,
                dialogY + dialogHeight - 110f,
                0.8f,
                Color.RED
            );
        }

        // Tulisan tombol konfirmasi
        drawButtonText(
            "YES",
            confirmX,
            confirmY
        );

        // Tulisan tombol batal
        drawButtonText(
            "CANCEL",
            cancelX,
            cancelY
        );

        game.getBatch().end();
    }


    // Menggambar teks di tengah tombol dialog
    private void drawButtonText(
        String text,
        float x,
        float y
    ) {

        font.getData().setScale(1f);
        font.setColor(Color.WHITE);

        layout.setText(font, text);

        float textX = x
            + (dialogButtonWidth - layout.width) / 2f;

        float textY = y
            + (dialogButtonHeight + layout.height) / 2f;

        font.draw(
            game.getBatch(),
            text,
            textX,
            textY
        );
    }

    // Menghapus pemain setelah pengguna menyetujui konfirmasi
    private void confirmDeletePlayer() {

        if (pendingDeleteUsername == null) {
            return;
        }

        // Meminta PlayerDataManager menghapus pemain berdasarkan username
        boolean deleted = playerDataManager.deletePlayer(
            pendingDeleteUsername
        );

        if (deleted) {

            // Memperbarui daftar username setelah berhasil dihapus
            refreshUsernames();

            // Menutup dialog
            showDeleteConfirmation = false;
            pendingDeleteUsername = null;
            deleteMessage = "";

        } else {

            // Menampilkan pesan jika penghapusan gagal
            deleteMessage = "Gagal menghapus pemain.";
        }
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

        // Menggambar background dan seluruh komponen Continue Screen
        drawBackground();
        drawInterface();

        // Menggambar dialog di atas seluruh komponen jika sedang aktif
        if (showDeleteConfirmation) {
            drawConfirmationDialog();
        }
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

            // Tombol hanya ditampilkan untuk slot yang memiliki pemain
            if (i < usernames.size) {

                // Menghitung posisi tombol Delete
                float deleteX = slotX[i]
                    + slotWidth - deleteWidth - 15f;

                float deleteY = slotY[i]
                    + (slotHeight - deleteHeight) / 2f;

                // Menghitung posisi tombol Play
                float playX = deleteX - playWidth - 10f;

                float playY = slotY[i]
                    + (slotHeight - playHeight) / 2f;

                // Menggambar tombol Play berwarna hijau
                shapeRenderer.setColor(
                    new Color(0.10f, 0.60f, 0.35f, 1f)
                );

                shapeRenderer.rect(
                    playX,
                    playY,
                    playWidth,
                    playHeight
                );

                // Menggambar tombol Delete berwarna merah
                shapeRenderer.setColor(
                    new Color(0.75f, 0.18f, 0.22f, 1f)
                );

                shapeRenderer.rect(
                    deleteX,
                    deleteY,
                    deleteWidth,
                    deleteHeight
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
            2.8f,
            Color.WHITE
        );

        if (usernames.isEmpty()) {

            drawCentered(
                "Belum ada pemain yang terdaftar.",
                panelY + panelHeight / 2f,
                1.3f,
                Color.LIGHT_GRAY
            );

            drawCentered(
                "Silakan buat pemain baru terlebih dahulu.",
                panelY + panelHeight / 2f - 35f,
                1.0f,
                Color.GRAY
            );

        } else {

            for (int i = 0; i < usernames.size; i++) {
                drawPlayerSlot(i, usernames.get(i));
            }
        }

        // Menampilkan tulisan Back
        drawCentered(
            "BACK",
            backY + backHeight / 2f + 7f,
            1.3f,
            Color.WHITE
        );

        game.getBatch().end();
    }

    /**
     Menggambar username dan tombol Play pada satu slot.
     **/
    private void drawPlayerSlot(int index, String username) {

        float textX = slotX[index] + 20f;

        // Mengatur ukuran teks username
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);

        // Menghitung posisi vertikal username agar berada di tengah slot
        layout.setText(font, username);

        float textY = slotY[index]
            + (slotHeight + layout.height) / 2f;

        font.draw(
            game.getBatch(),
            username,
            textX,
            textY
        );

        // Menghitung posisi tombol Delete
        float deleteX = slotX[index]
            + slotWidth - deleteWidth - 15f;

        // Menghitung posisi tombol Play di sebelah kiri Delete
        float playX = deleteX - playWidth - 10f;

        float playY = slotY[index]
            + (slotHeight - playHeight) / 2f;

        // Menghitung posisi teks PLAY agar tepat di tengah tombol
        font.getData().setScale(1.2f);
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

        // Menghitung posisi vertikal tombol Delete
        float deleteY = slotY[index]
            + (slotHeight - deleteHeight) / 2f;

        // Mengatur ukuran tulisan DELETE
        font.getData().setScale(1.1f);

        // Menghitung ukuran teks DELETE
        layout.setText(font, "DELETE");

        // Menghitung posisi teks agar berada di tengah tombol
        float deleteTextX = deleteX
            + (deleteWidth - layout.width) / 2f;

        float deleteTextY = deleteY
            + (deleteHeight + layout.height) / 2f;

        // Menggambar tulisan DELETE
        font.draw(
            game.getBatch(),
            "DELETE",
            deleteTextX,
            deleteTextY
        );

        // Mengembalikan ukuran font ke ukuran normal
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);

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
        backWidth = 100f;
        backHeight = 44f;

        backX = width - backWidth - 20f;
        backY = height - backHeight - 20f;

        // Slot username.
        slotWidth = panelWidth - 60f;

        slotHeight = Math.min(
            65f,
            (panelHeight - 180f) / 5f
        );

        // Tombol Play.
        playWidth = 100f;
        playHeight = 46f;

        // Mengatur ukuran tombol Delete
        deleteWidth = 100f;
        deleteHeight = 46f;

        // Mengatur ukuran tombol pada dialog konfirmasi
        dialogButtonWidth = 130f;
        dialogButtonHeight = 44f;

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

        // Memperbarui posisi dialog ketika ukuran layar berubah
        if (showDeleteConfirmation) {
            calculateConfirmationLayout();
        }
    }

    @Override
    public void hide() {

        // Melepaskan InputProcessor.
        Gdx.input.setInputProcessor(null);

        // Membersihkan resource Screen.
        dispose();
    }

    @Override
    public void dispose() {

        // Mencegah resource dibuang lebih dari sekali.
        if (disposed) {
            return;
        }

        shapeRenderer.dispose();

        disposed = true;
    }
}
