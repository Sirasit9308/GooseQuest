package com.goosequest.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.goosequest.GameManager;
import com.goosequest.GooseQuestGame;

/** จุดเริ่มโปรแกรมบนคอม (Windows / macOS / Linux) */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Goose Quest");
        config.setWindowedMode((int) GameManager.VIEW_W, (int) GameManager.VIEW_H);
        config.setResizable(false);
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new GooseQuestGame(), config);
    }
}
