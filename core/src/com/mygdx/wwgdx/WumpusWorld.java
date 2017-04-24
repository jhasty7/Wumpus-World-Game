package com.mygdx.wwgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;

public class WumpusWorld extends ScreenAdapter {

	private static final float WORLD_WIDTH = 656;
	private static final float WORLD_HEIGHT = 656;
    
	private static final int GRID_CELL = 164;
	private static final int MAX_CELL = (int) WORLD_WIDTH / GRID_CELL;
	private SpriteBatch batch;
	private Texture playerOne;
	private Texture wumpus;
	private Texture stench;
	private Texture pit;
	private Texture breeze;
	private Texture stenchAndBreeze;
	private Texture arrow;

	private Viewport viewport;
	private Camera camera;

	private ShapeRenderer shapeRenderer;

	private int playerX = 0;
	private int playerY = 0;
	public static int humanNumberOfMoves = 0;
	WumpusWorldGame EDT;
	private STATE state = STATE.PLAYING;

	private boolean useArrow;
	public static ArrayList<MapCell> mapCell;
	public static ArrayList<Integer> pitList;
	public static ArrayList<Integer> stenchList;
	public static ArrayList<Integer> breezeList;
	public static ArrayList<Integer> stenchAndBreezeList;
	public static int wumpusCell;
	public static Long humanRunningTime;

	public WumpusWorld(WumpusWorldGame EDT) {
		this.EDT = EDT;
		
		humanNumberOfMoves = 0;
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		shapeRenderer = new ShapeRenderer();
		camera.update();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		batch = new SpriteBatch();
		playerOne = new Texture(Gdx.files.internal("player_one.png"));
		wumpus = new Texture(Gdx.files.internal("whumpus.gif"));
		stench = new Texture(Gdx.files.internal("stench.png"));
		pit = new Texture(Gdx.files.internal("pit.png"));
		breeze = new Texture(Gdx.files.internal("wind.png"));
		stenchAndBreeze = new Texture(Gdx.files.internal("stenchAndBreeze.png"));
		arrow = new Texture(Gdx.files.internal("arrow.gif"));

		useArrow = false;

		mapCell = initiateMap();
		stenchList = new ArrayList<Integer>();
		pitList = new ArrayList<Integer>();
		breezeList = new ArrayList<Integer>();
		stenchAndBreezeList = new ArrayList<Integer>();

		randomizeWumpus();
		randomizePits();
		identifyStenchAndBreezeCells();
		purifyWumpus();
		humanRunningTime = System.currentTimeMillis();
	}

	@Override
	public void render(float delta) {

		switch (state) {

		case PLAYING: {

			clearScreen();
			drawGrid();
			draw();
			drawMapElements();

			break;
		} // end case
		case WIN: {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			clearScreen();

			humanRunningTime = System.currentTimeMillis() - humanRunningTime;
			EDT.setScreen(new WinningScreen(EDT));
			this.dispose();
			break;
		}
		case LOSE: {
			MusicHandler.stop();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			clearScreen();
			EDT.setScreen(new LosingScreen(EDT));
			this.dispose();
			break;
		} // end case
		default:
		} // end switch

	} // end render

	private void draw() {
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);
		boolean leftPressed = Gdx.input.isKeyJustPressed(Input.Keys.LEFT);
		boolean rightPressed = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT);
		boolean upPressed = Gdx.input.isKeyJustPressed(Input.Keys.UP);
		boolean downPressed = Gdx.input.isKeyJustPressed(Input.Keys.DOWN);
		boolean spacePressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

		batch.begin();

		if (!useArrow)
			batch.draw(playerOne, playerX, playerY);

		if (leftPressed && playerX != 0) {
			playerX -= GRID_CELL;
			if (!useArrow)
				batch.draw(playerOne, playerX, playerY);
			checkCell(playerX, playerY);
			humanNumberOfMoves++;
		}
		if (rightPressed && playerX != 492) {
			playerX += GRID_CELL;
			if (!useArrow)
				batch.draw(playerOne, playerX, playerY);
			checkCell(playerX, playerY);
			humanNumberOfMoves++;
		}
		if (upPressed && playerY != 492) {
			playerY += GRID_CELL;
			if (!useArrow)
				batch.draw(playerOne, playerX, playerY);
			checkCell(playerX, playerY);
			humanNumberOfMoves++;
		}
		if (downPressed && playerY != 0) {
			playerY -= GRID_CELL;
			if (!useArrow)
				batch.draw(playerOne, playerX, playerY);
			checkCell(playerX, playerY);
			humanNumberOfMoves++;
		}
		if (spacePressed) {
			if (useArrow)
				useArrow = false;

			else if (!useArrow)
				useArrow = true;
		}

		batch.end();

	}

	void drawMapElements() {

		batch.begin();
		if (useArrow)
			batch.draw(arrow, playerX, playerY);
		if (mapCell.get(wumpusCell).getVisited())
			batch.draw(wumpus, mapCell.get(wumpusCell).getX(), mapCell.get(wumpusCell).getY());

		int i;
		for (i = 0; i < stenchList.size(); i++) {
			if (mapCell.get(stenchList.get(i)).getVisited())
				batch.draw(stench, mapCell.get(stenchList.get(i)).getX(), mapCell.get(stenchList.get(i)).getY());

		}
		for (i = 0; i < pitList.size(); i++) {
			if (mapCell.get(pitList.get(i)).getVisited())
				batch.draw(pit, mapCell.get(pitList.get(i)).getX(), mapCell.get(pitList.get(i)).getY());

		}
		for (i = 0; i < breezeList.size(); i++) {
			if (mapCell.get(breezeList.get(i)).getVisited())
				batch.draw(breeze, mapCell.get(breezeList.get(i)).getX(), mapCell.get(breezeList.get(i)).getY());

		}
		for (i = 0; i < stenchAndBreezeList.size(); i++) {
			if (mapCell.get(stenchAndBreezeList.get(i)).getVisited())
				batch.draw(stenchAndBreeze, mapCell.get(stenchAndBreezeList.get(i)).getX(),
						mapCell.get(stenchAndBreezeList.get(i)).getY());

		}
		batch.end();

	}

	private void drawGrid() {
		shapeRenderer.setProjectionMatrix(camera.projection);
		shapeRenderer.setTransformMatrix(camera.view);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		for (int x = 0; x < viewport.getWorldWidth(); x += GRID_CELL) {

			for (int y = 0; y < viewport.getWorldHeight(); y += GRID_CELL) {

				shapeRenderer.rect(x, y, GRID_CELL, GRID_CELL);

			} // end inner-for

		} // end outer-for

		shapeRenderer.end();

	} // end drawGrid

	private void clearScreen() {
		/* clears it with beige? well, black, but it says beige for RGB */
		Gdx.gl.glClearColor(Color.rgb888(245, 245, 220), Color.rgb888(245, 245, 220), Color.rgb888(245, 245, 220),
				Color.rgb888(245, 245, 220));
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	} // end clearScreen

	private enum STATE {
		WIN, LOSE, PLAYING;
	}

	void checkCell(int playerX, int playerY) {

		int i;
		for (i = 0; i < mapCell.size(); i++) {

			if (playerX == mapCell.get(i).getX() && playerY == mapCell.get(i).getY()) {

				mapCell.get(i).setVisited(true);
				if (useArrow) {
					if (i == wumpusCell) {
						state = STATE.WIN;
						break;
					} else {
						state = STATE.LOSE;
						break;
					}
				} else if (wumpusCell == i) {

					state = STATE.LOSE;
					break;
				}
				int j;
				for (j = 0; j < pitList.size(); j++) {

					if (i == pitList.get(j)) {

						state = STATE.LOSE;
						break;
					}
				}
			}

		}

	}

	void randomizeWumpus() {
		int randomNumber;
		do {
			randomNumber = MathUtils.random(15);
			if (randomNumber != 0 && randomNumber != 4 && randomNumber != 1 && randomNumber != 5) {
				mapCell.get(randomNumber).setWumpus(true);

				if (randomNumber == 3) {
					mapCell.get(randomNumber + 4).setStench(true);
					stenchList.add(randomNumber + 4);

					mapCell.get(randomNumber - 1).setStench(true);
					stenchList.add(randomNumber - 1);
				}

				else if (randomNumber == 2) {

					mapCell.get(randomNumber + 4).setStench(true);
					stenchList.add(randomNumber + 4);

					mapCell.get(randomNumber + 1).setStench(true);
					stenchList.add(randomNumber + 1);

					mapCell.get(randomNumber - 1).setStench(true);
					stenchList.add(randomNumber - 1);

				}

				else if (randomNumber == 7 || randomNumber == 11) {
					mapCell.get(randomNumber + 4).setStench(true);
					stenchList.add(randomNumber + 4);

					mapCell.get(randomNumber - 4).setStench(true);
					stenchList.add(randomNumber - 4);

					mapCell.get(randomNumber - 1).setStench(true);
					stenchList.add(randomNumber - 1);
				}

				else if (randomNumber == 15) {
					mapCell.get(randomNumber - 4).setStench(true);
					stenchList.add(randomNumber - 4);
					mapCell.get(randomNumber - 1).setStench(true);
					stenchList.add(randomNumber - 1);
				}

				else if (randomNumber == 14 || randomNumber == 13) {

					mapCell.get(randomNumber - 4).setStench(true);
					stenchList.add(randomNumber - 4);

					mapCell.get(randomNumber + 1).setStench(true);
					stenchList.add(randomNumber + 1);

					mapCell.get(randomNumber - 1).setStench(true);
					stenchList.add(randomNumber - 1);

				}

				else if (randomNumber == 12) {

					mapCell.get(randomNumber + 1).setStench(true);
					stenchList.add(randomNumber + 1);

					mapCell.get(randomNumber - 4).setStench(true);
					stenchList.add(randomNumber - 4);

				}

				else if (randomNumber == 8) {
					mapCell.get(randomNumber + 4).setStench(true);
					stenchList.add(randomNumber + 4);

					mapCell.get(randomNumber - 4).setStench(true);
					stenchList.add(randomNumber - 4);

					mapCell.get(randomNumber + 1).setStench(true);
					stenchList.add(randomNumber + 1);
				} else {

					mapCell.get(randomNumber + 4).setStench(true);
					stenchList.add(randomNumber + 4);

					mapCell.get(randomNumber - 4).setStench(true);
					stenchList.add(randomNumber - 4);

					mapCell.get(randomNumber + 1).setStench(true);
					stenchList.add(randomNumber + 1);

					mapCell.get(randomNumber - 1).setStench(true);
					stenchList.add(randomNumber - 1);

				}
				wumpusCell = randomNumber;
				break;
			}

		} while (true);

	}

	void randomizePits() {
		int randomNumber;
		randomNumber = MathUtils.random(15);
		if (randomNumber != 0 && randomNumber != 4 && randomNumber != 1 && randomNumber != 5
				&& randomNumber != wumpusCell) {
			mapCell.get(randomNumber).setPit(true);

			if (randomNumber == 3) {
				mapCell.get(randomNumber + 4).setBreeze(true);
				breezeList.add(randomNumber + 4);

				mapCell.get(randomNumber - 1).setBreeze(true);
				breezeList.add(randomNumber - 1);
			}

			else if (randomNumber == 2) {

				mapCell.get(randomNumber + 4).setBreeze(true);
				breezeList.add(randomNumber + 4);

				mapCell.get(randomNumber + 1).setBreeze(true);
				breezeList.add(randomNumber + 1);

				mapCell.get(randomNumber - 1).setBreeze(true);
				breezeList.add(randomNumber - 1);

			}

			else if (randomNumber == 7 || randomNumber == 11) {
				mapCell.get(randomNumber + 4).setBreeze(true);
				breezeList.add(randomNumber + 4);

				mapCell.get(randomNumber - 4).setBreeze(true);
				breezeList.add(randomNumber - 4);

				mapCell.get(randomNumber - 1).setBreeze(true);
				breezeList.add(randomNumber - 1);
			}

			else if (randomNumber == 15) {
				mapCell.get(randomNumber - 4).setBreeze(true);
				breezeList.add(randomNumber - 4);
				mapCell.get(randomNumber - 1).setBreeze(true);
				breezeList.add(randomNumber - 1);
			}

			else if (randomNumber == 14 || randomNumber == 13) {

				mapCell.get(randomNumber - 4).setBreeze(true);
				breezeList.add(randomNumber - 4);

				mapCell.get(randomNumber + 1).setBreeze(true);
				breezeList.add(randomNumber + 1);

				mapCell.get(randomNumber - 1).setBreeze(true);
				breezeList.add(randomNumber - 1);

			}

			else if (randomNumber == 12) {

				mapCell.get(randomNumber + 1).setBreeze(true);
				breezeList.add(randomNumber + 1);

				mapCell.get(randomNumber - 4).setBreeze(true);
				breezeList.add(randomNumber - 4);

			}

			else if (randomNumber == 8) {
				mapCell.get(randomNumber + 4).setBreeze(true);
				breezeList.add(randomNumber + 4);

				mapCell.get(randomNumber - 4).setBreeze(true);
				breezeList.add(randomNumber - 4);

				mapCell.get(randomNumber + 1).setBreeze(true);
				breezeList.add(randomNumber + 1);
			} else {

				mapCell.get(randomNumber + 4).setBreeze(true);
				breezeList.add(randomNumber + 4);

				mapCell.get(randomNumber - 4).setBreeze(true);
				breezeList.add(randomNumber - 4);

				mapCell.get(randomNumber + 1).setBreeze(true);
				breezeList.add(randomNumber + 1);

				mapCell.get(randomNumber - 1).setBreeze(true);
				breezeList.add(randomNumber - 1);

			}
			pitList.add(randomNumber);
		}

	}

	void identifyStenchAndBreezeCells() {

		int i, j;
		if (!stenchList.isEmpty() && !breezeList.isEmpty()) {
			for (i = 0; i < stenchList.size(); i++) {
				for (j = 0; j < breezeList.size(); j++) {
					if (stenchList.get(i) == breezeList.get(j)) {
						stenchAndBreezeList.add(stenchList.get(i));
						stenchList.remove(stenchList.get(i));
						breezeList.remove(breezeList.get(j));
					}
				}

			}
		}

	}

	void purifyWumpus() {
		mapCell.get(wumpusCell).setBreeze(false);
		int i;
		for (i = 0; i < breezeList.size(); i++) {

			if (mapCell.get(breezeList.get(i)).getWumpus())
				breezeList.remove(i);

		}
		for (i = 0; i < stenchAndBreezeList.size(); i++) {
			if (mapCell.get(stenchAndBreezeList.get(i)).getWumpus())
				stenchAndBreezeList.remove(i);

		}

	}

	public ArrayList<MapCell> initiateMap() {
		ArrayList<MapCell> mapCell = new ArrayList<MapCell>();

		int i, j, k = 0;
		for (i = 0; i < MAX_CELL * GRID_CELL; i += GRID_CELL) {

			for (j = 0; j < MAX_CELL * GRID_CELL; j += GRID_CELL) {

				mapCell.add(new MapCell(i, j, k));
				k++;
			}

		}

		// intialize cellNumber
		mapCell.get(0).setCellNumber(0);
		mapCell.get(1).setCellNumber(1);
		mapCell.get(2).setCellNumber(2);
		mapCell.get(3).setCellNumber(3);
		mapCell.get(4).setCellNumber(4);
		mapCell.get(5).setCellNumber(5);
		mapCell.get(6).setCellNumber(6);
		mapCell.get(7).setCellNumber(7);
		mapCell.get(8).setCellNumber(8);
		mapCell.get(9).setCellNumber(9);
		mapCell.get(10).setCellNumber(10);
		mapCell.get(11).setCellNumber(11);
		mapCell.get(12).setCellNumber(12);
		mapCell.get(13).setCellNumber(13);
		mapCell.get(14).setCellNumber(14);
		mapCell.get(15).setCellNumber(15);

		// add adjacent cells
		mapCell.get(0).addAdjacentCell(mapCell.get(1));
		mapCell.get(0).addAdjacentCell(mapCell.get(4));
		mapCell.get(1).addAdjacentCell(mapCell.get(0));
		mapCell.get(1).addAdjacentCell(mapCell.get(5));
		mapCell.get(1).addAdjacentCell(mapCell.get(2));
		mapCell.get(2).addAdjacentCell(mapCell.get(1));
		mapCell.get(2).addAdjacentCell(mapCell.get(6));
		mapCell.get(2).addAdjacentCell(mapCell.get(3));
		mapCell.get(3).addAdjacentCell(mapCell.get(7));
		mapCell.get(3).addAdjacentCell(mapCell.get(2));
		mapCell.get(4).addAdjacentCell(mapCell.get(8));
		mapCell.get(4).addAdjacentCell(mapCell.get(5));
		mapCell.get(4).addAdjacentCell(mapCell.get(0));
		mapCell.get(5).addAdjacentCell(mapCell.get(1));
		mapCell.get(5).addAdjacentCell(mapCell.get(4));
		mapCell.get(5).addAdjacentCell(mapCell.get(6));
		mapCell.get(5).addAdjacentCell(mapCell.get(9));
		mapCell.get(6).addAdjacentCell(mapCell.get(7));
		mapCell.get(6).addAdjacentCell(mapCell.get(2));
		mapCell.get(6).addAdjacentCell(mapCell.get(5));
		mapCell.get(6).addAdjacentCell(mapCell.get(10));
		mapCell.get(7).addAdjacentCell(mapCell.get(3));
		mapCell.get(7).addAdjacentCell(mapCell.get(6));
		mapCell.get(7).addAdjacentCell(mapCell.get(11));
		mapCell.get(11).addAdjacentCell(mapCell.get(7));
		mapCell.get(11).addAdjacentCell(mapCell.get(10));
		mapCell.get(11).addAdjacentCell(mapCell.get(15));
		mapCell.get(10).addAdjacentCell(mapCell.get(6));
		mapCell.get(10).addAdjacentCell(mapCell.get(9));
		mapCell.get(10).addAdjacentCell(mapCell.get(11));
		mapCell.get(10).addAdjacentCell(mapCell.get(14));
		mapCell.get(9).addAdjacentCell(mapCell.get(5));
		mapCell.get(9).addAdjacentCell(mapCell.get(10));
		mapCell.get(9).addAdjacentCell(mapCell.get(13));
		mapCell.get(9).addAdjacentCell(mapCell.get(8));
		mapCell.get(8).addAdjacentCell(mapCell.get(4));
		mapCell.get(8).addAdjacentCell(mapCell.get(9));
		mapCell.get(8).addAdjacentCell(mapCell.get(12));
		mapCell.get(12).addAdjacentCell(mapCell.get(8));
		mapCell.get(12).addAdjacentCell(mapCell.get(13));
		mapCell.get(13).addAdjacentCell(mapCell.get(12));
		mapCell.get(13).addAdjacentCell(mapCell.get(9));
		mapCell.get(13).addAdjacentCell(mapCell.get(14));
		mapCell.get(14).addAdjacentCell(mapCell.get(13));
		mapCell.get(14).addAdjacentCell(mapCell.get(10));
		mapCell.get(14).addAdjacentCell(mapCell.get(15));
		mapCell.get(15).addAdjacentCell(mapCell.get(11));
		mapCell.get(15).addAdjacentCell(mapCell.get(14));

		return mapCell;
	}

}
