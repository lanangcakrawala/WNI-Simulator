package com.Kelompok4.wni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class GameScreen extends ScreenAdapter {

    private final WNISimulatorGame game;
    private final OrthographicCamera camera;
    private final GlyphLayout layout;
    private final GameController controller;

    public GameScreen(WNISimulatorGame game) {
        this.game = game;
        controller = new GameController();

        camera = new OrthographicCamera();
        camera.setToOrtho(
            false,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        controller.update(delta);

        Gdx.gl.glClearColor(0.08f, 0.12f, 0.08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();

        float height = Gdx.graphics.getHeight();

        drawCentered("GAMEPLAY", height * 0.65f, 2.0f);
        drawCentered("Saldo: Rp500.000", height * 0.50f, 1.5f);
        drawCentered("Gameplay akan kita kembangkan!", height * 0.35f, 1.0f);
        drawCentered("Tekan ESC untuk kembali ke menu", height * 0.20f, 1.0f);

        game.getBatch().end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void drawCentered(String text, float y, float scale) {

        BitmapFont font = game.getFont();

        font.getData().setScale(scale);
        layout.setText(font, text);

        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;

        font.draw(game.getBatch(), text, x, y);

        font.getData().setScale(1f);
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        camera.setToOrtho(false, width, height);
        camera.update();
    }
}
