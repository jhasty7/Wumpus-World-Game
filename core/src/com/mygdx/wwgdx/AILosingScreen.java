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

public class AILosingScreen extends ScreenAdapter {
	public static int AI_DEATH_COUNTER = 0;
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
	private String WumpusWorldType;
	private Long completionTime;
	private int numberOfMoves;
	private float refinedTime;
	
	public AILosingScreen(WumpusWorldGame EDT, Long completionTime, int numberOfMoves, String WumpusWorldType) {
		
		this.EDT = EDT;
		this.WumpusWorldType = WumpusWorldType;
		this.completionTime = completionTime;
		this.numberOfMoves = numberOfMoves;
	}

	@Override
	public void resize(int width, int height) {

		viewport.update(width, height);

	}

	@Override
	public void show() {
		super.show();
		
		AI_DEATH_COUNTER++;
		AIWinningScreen.AI_WINNING_SCORE = 0;
		
		camera = new OrthographicCamera();
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		camera.update();
		
		refinedTime = completionTime / (float)1000;

		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		startBatch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		wumpusLogo = new Texture(Gdx.files.internal("thewumpus.png"));
		evilWumpusLogo = new Texture(Gdx.files.internal("evilwumpus.png"));
		if(AI_DEATH_COUNTER % 3 == 0){
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("wumpus_lose_one.mp3"));
			sound.play(.2f);
		}
		else if(AI_DEATH_COUNTER % 5 == 0){
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("wumpus_lose_two.mp3"));
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
		
		font.draw(startBatch, "AI Failed", WORLD_HEIGHT / 2 - 26, WORLD_WIDTH / 2);
		font.draw(startBatch, "Try Again - ENTER", WORLD_HEIGHT / 2 - 62, WORLD_WIDTH / 2 - 25);
		font.draw(startBatch, "Menu - ESC", WORLD_HEIGHT / 2 - 40, WORLD_WIDTH / 2 - 50);

		font.draw(startBatch, "AI Finished in " + refinedTime + " seconds. Number of Moves " + numberOfMoves + ".", WORLD_HEIGHT / 2 - 150, WORLD_WIDTH / 2 - 250);
		font.draw(startBatch, "You Finished in " + (float)WumpusWorld.humanRunningTime/(float)1000 + " seconds. Number of Moves " + WumpusWorld.humanNumberOfMoves + ".", WORLD_HEIGHT / 2 - 150, WORLD_WIDTH / 2 - 275);

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