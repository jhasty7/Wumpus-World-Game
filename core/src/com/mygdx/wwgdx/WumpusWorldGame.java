package com.mygdx.wwgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class WumpusWorldGame extends Game {
	private final AssetManager assetManager = new AssetManager();

	@Override
	public void create() {

		setScreen(new StartMenu(this,true));
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}
}