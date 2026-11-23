
package com.Kelompok4.wni.screens;

import com.Kelompok4.wni.WNISimulatorGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

// Mengatur tampilan dan navigasi menu utama
public class MainMenuScreen extends ScreenAdapter {

    // Menghubungkan MainMenuScreen dengan game utama
    private final WNISimulatorGame game;

    // Mengatur sudut pandang kamera
    private final OrthographicCamera camera;

    // Menghitung ukuran teks
    private final GlyphLayout layout;

    // Constructor MainMenuScreen
    public MainMenuScreen(WNISimulatorGame game) {

        this.game = game;

        // Membuat kamera untuk tampilan menu
        camera = new OrthographicCamera();

        camera.setToOrtho(
            false,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        // Membuat pengatur ukuran teks
        layout = new GlyphLayout();
    }

    // Menggambar tampilan menu setiap frame
    @Override
    public void render(float delta) {

        // Membersihkan layar dengan warna background gelap
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.12f, 1f);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Memperbarui kamera
        camera.update();

        // Mengatur kamera untuk SpriteBatch
        game.getBatch().setProjectionMatrix(camera.combined);

        // Memulai proses menggambar teks
        game.getBatch().begin();

        float height = Gdx.graphics.getHeight();

        // Menampilkan judul permainan
        drawCentered(
            "WNI SIMULATOR",
            height * 0.70f,
            2.5f
        );

        // Menampilkan subtitle
        drawCentered(
            "Simulator Kehidupan Finansial",
            height * 0.56f,
            1.2f
        );

        // Menampilkan pilihan New Game
        drawCentered(
            "N / ENTER - NEW GAME",
            height * 0.38f,
            1.3f
        );

        // Menampilkan pilihan Continue
        drawCentered(
            "C - CONTINUE",
            height * 0.28f,
            1.3f
        );

        // Menampilkan petunjuk navigasi
        drawCentered(
            "Pilih mode permainan menggunakan keyboard",
            height * 0.17f,
            0.9f
        );

        // Mengakhiri proses menggambar
        game.getBatch().end();

        // Membuka NewGameScreen ketika N atau ENTER ditekan
        if (
            Gdx.input.isKeyJustPressed(Input.Keys.N)
                || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
        ) {

            game.setScreen(new NewGameScreen(game));

            // Membuka ContinueScreen ketika C ditekan
        } else if (
            Gdx.input.isKeyJustPressed(Input.Keys.C)
        ) {

            game.setScreen(new ContinueScreen(game));
        }
    }

    // Menggambar teks pada posisi horizontal di tengah layar
    private void drawCentered(
        String text,
        float y,
        float scale
    ) {

        // Mengambil font dari game utama
        BitmapFont font = game.getFont();

        // Mengatur ukuran font
        font.getData().setScale(scale);

        // Menghitung ukuran teks
        layout.setText(font, text);

        // Menghitung posisi horizontal agar teks berada di tengah
        float x = (
            Gdx.graphics.getWidth() - layout.width
        ) / 2f;

        // Menggambar teks
        font.draw(
            game.getBatch(),
            text,
            x,
            y
        );

        // Mengembalikan ukuran font ke ukuran normal
        font.getData().setScale(1f);
    }

    // Menyesuaikan kamera ketika ukuran jendela berubah
    @Override
    public void resize(int width, int height) {

        if (width <= 0 || height <= 0) {
            return;
        }

        camera.setToOrtho(false, width, height);

        camera.update();
    }
}
