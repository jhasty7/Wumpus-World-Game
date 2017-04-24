package com.mygdx.wwgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StartMenu extends ScreenAdapter {
	// World Width and height
	private static final float WORLD_WIDTH = 800;
	private static final float WORLD_HEIGHT = 800;

	private Viewport viewport;
	private Camera camera;
	private Texture wumpusLogo;
	private Texture evilWumpusLogo;
	private final WumpusWorldGame EDT;
	private BitmapFont font;
	private SpriteBatch startBatch;
	private OPTION option;
	private Music music;
	private boolean playMusic;
	public static boolean throttleAI = true;

	public StartMenu(WumpusWorldGame EDT, boolean playMusic) {
		this.EDT = EDT;
		this.playMusic = playMusic;
	}

	@Override
	public void resize(int width, int height) {

		viewport.update(width, height);

	}

	@Override
	public void show() {
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
		}
		camera = new OrthographicCamera();
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		camera.update();

		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		new ShapeRenderer();
		startBatch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		wumpusLogo = new Texture(Gdx.files.internal("thewumpus.png"));
		evilWumpusLogo = new Texture(Gdx.files.internal("evilwumpus.png"));
		option = OPTION.HUMAN;

		music = Gdx.audio.newMusic(Gdx.files.internal("music1.mp3"));
		if (playMusic) {
			MusicHandler.change(music);
			MusicHandler.play();
		}
	}

	@Override
	public void render(float delta) {
		clearScreen();
		update(delta);

	}

	private void update(float delta) {

		startBatch.begin();
		if (throttleAI)
			startBatch.draw(wumpusLogo, 38, 200);
		else
			startBatch.draw(evilWumpusLogo, 38, 200);
		
		font.draw(startBatch, "Start", WORLD_HEIGHT / 2 - 18, WORLD_WIDTH / 2);
		font.draw(startBatch, "Quit", WORLD_HEIGHT / 2 - 18, WORLD_WIDTH / 2 - 25);

		switch (option) {
		case HUMAN:
			font.draw(startBatch, ">", WORLD_HEIGHT / 2 - 36, WORLD_WIDTH / 2);
			break;
		case QUIT:
			font.draw(startBatch, ">", WORLD_HEIGHT / 2 - 36, WORLD_WIDTH / 2 - 25);
			break;
		default:

		}
		if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
			System.out.println(option == OPTION.HUMAN);
			if (option == OPTION.HUMAN) {

				EDT.setScreen(new WumpusWorld(EDT));
				this.dispose();

			} else if (option == OPTION.QUIT) {

				this.dispose();
				System.exit(0);
			}

		} else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
			if (option == OPTION.HUMAN)
				option = OPTION.QUIT;
			else if (option == OPTION.QUIT)
				option = OPTION.QUIT;
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			if (option == OPTION.HUMAN)
				option = OPTION.HUMAN;
			else if (option == OPTION.QUIT)
				option = OPTION.HUMAN;
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
			if(throttleAI){
				throttleAI = false;
				Sound sound = Gdx.audio.newSound(Gdx.files.internal("wumpus_laugh_three.mp3"));
				sound.play(.4f);
			}
			else if(!throttleAI)
				throttleAI = true;
		}

		startBatch.end();

	}

	private void clearScreen() {

		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	}

	private enum OPTION {
		HUMAN, AI, QUIT;
	}

}