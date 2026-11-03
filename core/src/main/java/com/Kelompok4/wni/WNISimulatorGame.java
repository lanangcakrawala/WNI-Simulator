package com.Kelompok4.wni;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class WNISimulatorGame extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}