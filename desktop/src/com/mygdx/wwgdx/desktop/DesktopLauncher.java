package com.mygdx.wwgdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.wwgdx.WumpusWorldGame;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Wumpus World";
		cfg.height = 800;
		cfg.width = 800;
		cfg.resizable = false;
		new LwjglApplication(new WumpusWorldGame(), cfg);
	}
}
