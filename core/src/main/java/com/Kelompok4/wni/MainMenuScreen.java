
package com.Kelompok4.wni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class MainMenuScreen extends ScreenAdapter {

    private final WNISimulatorGame game;
    private final OrthographicCamera camera;
    private final GlyphLayout layout;

    public MainMenuScreen(WNISimulatorGame game) {
        this.game = game;

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

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();

        float height = Gdx.graphics.getHeight();

        drawCentered("WNI SIMULATOR", height * 0.70f, 2.5f);
        drawCentered("Simulator Kehidupan Finansial", height * 0.56f, 1.2f);
        drawCentered("Tekan ENTER untuk mulai", height * 0.36f, 1.3f);
        drawCentered("A / D atau LEFT / RIGHT untuk bergerak", height * 0.24f, 1.0f);

        game.getBatch().end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
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
