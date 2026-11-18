
package com.Kelompok4.wni;

// Import library bawaan libGDX
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// Import untuk mengatur format angka
import java.util.Locale;


// Mengatur tampilan dan proses gameplay
public class GameScreen extends ScreenAdapter {

    // Menyimpan referensi ke class utama game
    private final WNISimulatorGame game;

    // Mengatur sudut pandang kamera 2D
    private final OrthographicCamera camera;

    // Menghitung ukuran teks untuk menentukan posisi tengah
    private final GlyphLayout layout;

    // Mengatur logika utama permainan
    private final GameController controller;

    // Menggambar bentuk sementara untuk Player dan FallingObject
    private final ShapeRenderer shapeRenderer;

    // Menandai apakah ShapeRenderer sudah dibersihkan
    private boolean disposed = false;

    // Constructor untuk menginisialisasi GameScreen
    public GameScreen(WNISimulatorGame game) {

        // Menyimpan referensi game utama
        this.game = game;

        // Membuat GameController untuk mengatur gameplay
        controller = new GameController(new ObjectFactory());

        // Membuat objek Player dengan ukuran layar saat ini
        Player player = new Player(Gdx.graphics.getWidth());

        // Menghubungkan Player dengan GameController
        controller.setPlayer(player);

        // Membuat kamera 2D
        camera = new OrthographicCamera();

        // Menyesuaikan kamera dengan ukuran layar
        camera.setToOrtho(
            false,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        // Membuat objek untuk mengatur tata letak teks
        layout = new GlyphLayout();

        // Membuat ShapeRenderer untuk menggambar objek sementara
        shapeRenderer = new ShapeRenderer();
    }

    // Method yang dipanggil berulang kali oleh Game Loop libGDX
    @Override
    public void render(float delta) {

        // Memeriksa apakah pemain menekan tombol P
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {

            // Jika permainan berjalan, hentikan sementara
            if (controller.getGameState() == GameState.PLAYING) {
                controller.pause();
            }

            // Jika permainan dijeda, lanjutkan kembali
            else if (controller.getGameState() == GameState.PAUSED) {
                controller.resume();
            }
        }

        // Memeriksa tombol R ketika permainan berakhir
        if (controller.getGameState() == GameState.GAME_OVER &&
            Gdx.input.isKeyJustPressed(Input.Keys.R)) {

            // Mengatur ulang seluruh kondisi permainan
            controller.reset();
        }

        // Memperbarui logika permainan setiap frame
        controller.update(
            delta,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        // Mengatur warna background permainan
        Gdx.gl.glClearColor(0.08f, 0.12f, 0.08f, 1);

        // Membersihkan tampilan frame sebelumnya
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Memperbarui kamera
        camera.update();

        // Mengatur proyeksi ShapeRenderer mengikuti kamera
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Memulai proses menggambar bentuk
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Menggambar Player dengan warna biru
        if (controller.getPlayer() != null) {
            shapeRenderer.setColor(Color.CYAN);

            shapeRenderer.rect(
                controller.getPlayer().getBounds().x,
                controller.getPlayer().getBounds().y,
                controller.getPlayer().getBounds().width,
                controller.getPlayer().getBounds().height
            );
        }

        // Menggambar seluruh objek yang sedang aktif
        for (FallingObject object : controller.getFallingObjects()) {

            switch (object.getType()) {
                case SALARY:
                    shapeRenderer.setColor(Color.GREEN);
                    break;

                case BONUS:
                    shapeRenderer.setColor(Color.YELLOW);
                    break;

                case TAX:
                    shapeRenderer.setColor(Color.RED);
                    break;

                case JUDOL:
                    shapeRenderer.setColor(Color.MAGENTA);
                    break;
            }

            shapeRenderer.rect(
                object.getBounds().x,
                object.getBounds().y,
                object.getBounds().width,
                object.getBounds().height
            );
        }

        // Mengakhiri proses menggambar bentuk
        shapeRenderer.end();

        // Mengatur perspektif SpriteBatch berdasarkan kamera
        game.getBatch().setProjectionMatrix(camera.combined);

        // Memulai proses menggambar objek
        game.getBatch().begin();

        // Mengambil tinggi layar
        float height = Gdx.graphics.getHeight();


        // Menampilkan informasi sesuai kondisi permainan
        if (controller.getGameState() == GameState.PAUSED) {

            // Memberikan informasi bahwa permainan sedang dijeda
            drawCentered("PAUSED - Tekan P untuk melanjutkan",
                height * 0.10f, 1.2f);

        } else if (controller.getGameState() == GameState.GAME_OVER) {

            // Memberikan informasi bahwa permainan telah berakhir
            drawCentered("GAME OVER - Tekan R untuk mengulang",
                height * 0.10f, 1.2f);
        }

        // Menampilkan judul gameplay
        drawCentered("GAMEPLAY", height * 0.70f, 2.0f);

        // Menampilkan saldo aktual dari GameController
        drawCentered(
            "Saldo: " + controller.getFormattedBalance(),
            height * 0.53f,
            1.5f
        );


        // Mengambil dan menampilkan waktu permainan
        String timeText = String.format(
            Locale.US,
            "%.1f",
            controller.getElapsedTime()
        );

        drawCentered(
            "Waktu bermain: " + timeText + " detik",
            height * 0.38f,
            1.0f
        );

        // Menampilkan instruksi kembali ke menu
        drawCentered(
            "Tekan ESC untuk kembali ke menu",
            height * 0.20f,
            1.0f
        );

        // Mengakhiri proses menggambar
        game.getBatch().end();

        // Memeriksa apakah pemain menekan tombol ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

            // Berpindah kembali ke Main Menu
            game.setScreen(new MainMenuScreen(game));
        }
    }

    // Method untuk menggambar teks di posisi horizontal tengah layar
    private void drawCentered(String text, float y, float scale) {

        // Mengambil font yang dikelola oleh game utama
        BitmapFont font = game.getFont();

        // Mengatur ukuran teks
        font.getData().setScale(scale);

        // Menghitung ukuran teks
        layout.setText(font, text);

        // Menghitung posisi horizontal agar teks berada di tengah
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;

        // Menggambar teks ke layar
        font.draw(game.getBatch(), text, x, y);

        // Mengembalikan ukuran font ke ukuran normal
        font.getData().setScale(1f);
    }

    // Method yang dipanggil ketika ukuran jendela berubah
    @Override
    public void resize(int width, int height) {

        // Menghindari ukuran layar yang tidak valid
        if (width <= 0 || height <= 0) {
            return;
        }

        // Menyesuaikan kamera dengan ukuran layar baru
        camera.setToOrtho(false, width, height);

        // Memperbarui kamera
        camera.update();

        // Menyesuaikan batas gerak Player dengan lebar layar baru
        if (controller.getPlayer() != null) {
            controller.getPlayer().resize(width);
        }
    }

    // Membersihkan resource ketika GameScreen tidak digunakan
    @Override
    public void hide() {
        dispose();
    }

    // Membersihkan ShapeRenderer agar tidak menyebabkan memory leak
    @Override
    public void dispose() {

        // Memastikan resource hanya dibersihkan satu kali
        if (!disposed) {
            shapeRenderer.dispose();
            disposed = true;
        }
    }
}
