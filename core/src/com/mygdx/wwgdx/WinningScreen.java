package com.mygdx.wwgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WinningScreen extends ScreenAdapter {
	public static int WINNING_SCORE = 0;
	// World Width and height
	private static final float WORLD_WIDTH = 800;
	private static final float WORLD_HEIGHT = 800;

	private Viewport viewport;
	private Camera camera;
	private Texture wumpusLogo;
	private Texture evilWumpusLogo;
	
	// Extra
	private final WumpusWorldGame EDT;
	private BitmapFont font;
	private SpriteBatch startBatch;

	public WinningScreen(WumpusWorldGame EDT) {
		
		this.EDT = EDT;

	}

	@Override
	public void resize(int width, int height) {

		viewport.update(width, height);

	}

	@Override
	public void show() {
		super.show();
		
		WINNING_SCORE++;
		LosingScreen.DEATH_COUNTER = 0;

		camera = new OrthographicCamera();
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		camera.update();

		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		startBatch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		wumpusLogo = new Texture(Gdx.files.internal("thewumpus.png"));
		evilWumpusLogo = new Texture(Gdx.files.internal("evilwumpus.png"));
		
		if(WumpusWorld.humanRunningTime <= 3000){
			
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("under_two_seconds.mp3"));
			sound.play(.2f);
			
		}
	}

	@Override
	public void render(float delta) {
		clearScreen();
		update(delta);
	}

	private void update(float delta) {

		startBatch.begin();

		if (StartMenu.throttleAI)
			startBatch.draw(wumpusLogo, 38, 200);
		else
			startBatch.draw(evilWumpusLogo, 38, 200);
		
		font.draw(startBatch, "Run AI - ENTER", WORLD_HEIGHT / 2 -54, WORLD_WIDTH / 2 - 25);
		font.draw(startBatch, "Menu - ESC", WORLD_HEIGHT / 2 - 40, WORLD_WIDTH / 2 - 50);
		font.draw(startBatch, "Finished in " + (float)WumpusWorld.humanRunningTime/(float)1000 + " seconds. Number of Moves " + WumpusWorld.humanNumberOfMoves + ".", WORLD_HEIGHT / 2 - 150, WORLD_WIDTH / 2 - 250);
		if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {

			EDT.setScreen(new AIMenu(EDT));
			this.dispose();

		} else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			
			
			EDT.setScreen(new StartMenu(EDT,false));
			this.dispose();

		}
		startBatch.end();

	}

	private void clearScreen() {

		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	}

}