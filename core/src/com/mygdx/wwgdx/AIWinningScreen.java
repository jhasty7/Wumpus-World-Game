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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AIWinningScreen extends ScreenAdapter {
	public static int AI_WINNING_SCORE = 0;
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
	private Long completionTime;
	private int numberOfMoves;
	private float refinedTime;
	public AIWinningScreen(WumpusWorldGame EDT, Long completionTime, int numberOfMoves, String WumpusWorldType) {
		AI_WINNING_SCORE++;
		AILosingScreen.AI_DEATH_COUNTER = 0;
		this.EDT = EDT;
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
		camera = new OrthographicCamera();
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		camera.update();

		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		startBatch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		wumpusLogo = new Texture(Gdx.files.internal("thewumpus.png"));
		evilWumpusLogo = new Texture(Gdx.files.internal("evilwumpus.png"));
		Music music = Gdx.audio.newMusic(Gdx.files.internal("death1.mp3"));
		MusicHandler.change(music);
		MusicHandler.play();
		refinedTime = completionTime / (float)1000;
		
		if( WumpusWorld.humanRunningTime < completionTime){
			//you win
			int rng = MathUtils.random(3);
			switch(rng){
			case 0:
				Sound sound1 = Gdx.audio.newSound(Gdx.files.internal("wumpus_lose_one.mp3"));
				sound1.play(.2f);
				break;
			case 1:
				Sound sound2 = Gdx.audio.newSound(Gdx.files.internal("wumpus_lose_two.mp3"));
				sound2.play(.2f);
				break;
			case 2:
			case 3:
				default:
			}
		}
		else if( WumpusWorld.humanRunningTime > completionTime){
			int rng = MathUtils.random(3);
			if(rng == 0){
				Sound sound = Gdx.audio.newSound(Gdx.files.internal("wumpus_laugh_one.mp3"));
				sound.play(.5f);
			}
		}
		
		if(refinedTime <= 3.000){
			
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
		
		if( WumpusWorld.humanRunningTime < completionTime){
			//you win
			font.draw(startBatch, "Human wins", WORLD_HEIGHT / 2 - 36, WORLD_WIDTH / 2);
			
		}
		else if( WumpusWorld.humanRunningTime > completionTime){
			//Wumpus wins
			font.draw(startBatch, "AI wins", WORLD_HEIGHT / 2 - 28, WORLD_WIDTH / 2);
			
		}

		
		font.draw(startBatch, "AI Menu - ENTER", WORLD_HEIGHT / 2 - 63, WORLD_WIDTH / 2 - 25);
		font.draw(startBatch, "Start Over - ESC", WORLD_HEIGHT / 2 - 63, WORLD_WIDTH / 2 - 50);
		font.draw(startBatch, "AI Finished in " + refinedTime + " seconds. Number of Moves " + numberOfMoves + ".", WORLD_HEIGHT / 2 - 150, WORLD_WIDTH / 2 - 250);
		font.draw(startBatch, "You Finished in " + (float)WumpusWorld.humanRunningTime/(float)1000 + " seconds. Number of Moves " + WumpusWorld.humanNumberOfMoves + ".", WORLD_HEIGHT / 2 - 150, WORLD_WIDTH / 2 - 275);

		if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
			
			MusicHandler.stop();
			Music music = Gdx.audio.newMusic(Gdx.files.internal("music1.mp3"));
			MusicHandler.change(music);
			music.play();
			
			EDT.setScreen(new AIMenu(EDT));
			this.dispose();

		} else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			
			MusicHandler.stop();
			EDT.setScreen(new StartMenu(EDT,true));
			this.dispose();

		}
		startBatch.end();

	}

	private void clearScreen() {

		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	}

}