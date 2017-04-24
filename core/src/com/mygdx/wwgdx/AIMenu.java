package com.mygdx.wwgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
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

public class AIMenu extends ScreenAdapter {
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

	public AIMenu(WumpusWorldGame EDT) {

		this.EDT = EDT;
		
	}

	@Override
	public void resize(int width, int height) {

		viewport.update(width, height);

	}

	@Override
	public void show() {
		try{ Thread.sleep(150);}catch(InterruptedException e){}
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
		option = OPTION.HILLCLIMBING;
		
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
		
		font.draw(startBatch, "Hill Climbing", WORLD_HEIGHT / 2 - 40, WORLD_WIDTH / 2);
		font.draw(startBatch, "Sim Annealing", WORLD_HEIGHT / 2 - 40, WORLD_WIDTH / 2 -25);
		
		switch (option) {
		case HILLCLIMBING:
			font.draw(startBatch, ">", WORLD_HEIGHT / 2 - 55, WORLD_WIDTH / 2);
			break;
		case SAS:
			font.draw(startBatch, ">", WORLD_HEIGHT / 2 - 55, WORLD_WIDTH / 2 - 25);
			break;
		default:

		}
		if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
			
			if (option == OPTION.HILLCLIMBING) {
				EDT.setScreen(new AIWumpusWorld(EDT));
				this.dispose();

			} else if (option == OPTION.SAS) {
				EDT.setScreen(new SimulatedAnnealingWumpusWorld(EDT));
				this.dispose();
			}

		} else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
			if (option == OPTION.HILLCLIMBING)
				option = OPTION.SAS;
			else if (option == OPTION.SAS)
				option = OPTION.SAS;
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			if (option == OPTION.SAS)
				option = OPTION.HILLCLIMBING;
			else if (option == OPTION.HILLCLIMBING)
				option = OPTION.HILLCLIMBING;
		}
		
		startBatch.end();

	}

	private void clearScreen() {

		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	}

	private enum OPTION {
		HILLCLIMBING, SAS, BACK;
	}

}