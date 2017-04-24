package com.mygdx.wwgdx;

import com.badlogic.gdx.Gdx;
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

public class SimulatedAnnealingWumpusWorld extends ScreenAdapter {

	private static final float WORLD_WIDTH = 656;
	private static final float WORLD_HEIGHT = 656;
	private static final int STENCH_COST = 1;
	private static final int BREEZE_COST = 4;
	private static final int STENCH_BREEZE_COST = 7;
	private static int temperature;
	private static final int RANDOM_VALUE = 20;

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
	private int landedSquare = 0;
	private int numberOfMoves = 0;
	private int numberOfStenches = 0;
	private int stenchCounter = 0;
	WumpusWorldGame EDT;
	private STATE state = STATE.PLAYING;
	private AI_DECISION aiDecision = AI_DECISION.DOWN;

	private static boolean useArrow;
	public ArrayList<MapCell> mapCell;
	public ArrayList<Integer> pitList;
	public ArrayList<Integer> stenchList;
	public ArrayList<Integer> breezeList;
	public ArrayList<Integer> stenchAndBreezeList;
	public int wumpusCell;
	private Long runningTime;

	public SimulatedAnnealingWumpusWorld(WumpusWorldGame EDT) {
		this.EDT = EDT;
		System.out.println("----------------new game--------------");
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		shapeRenderer = new ShapeRenderer();
		camera.update();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		batch = new SpriteBatch();
		playerOne = new Texture(Gdx.files.internal("whumpus.gif"));
		wumpus = new Texture(Gdx.files.internal("player_one.png"));
		stench = new Texture(Gdx.files.internal("stench.png"));
		pit = new Texture(Gdx.files.internal("pit.png"));
		breeze = new Texture(Gdx.files.internal("wind.png"));
		stenchAndBreeze = new Texture(Gdx.files.internal("stenchAndBreeze.png"));
		arrow = new Texture(Gdx.files.internal("arrow.gif"));
		useArrow = false;

		this.mapCell = WumpusWorld.mapCell;
		this.stenchList = WumpusWorld.stenchList;
		this.pitList = WumpusWorld.pitList;
		this.breezeList = WumpusWorld.breezeList;
		this.stenchAndBreezeList = WumpusWorld.stenchAndBreezeList;
		this.wumpusCell = WumpusWorld.wumpusCell;
		
		numberOfStenches = stenchList.size() + stenchAndBreezeList.size();
		temperature = 10;
		resetCostsAndVisited();
		runningTime = System.currentTimeMillis();

	}

	@Override
	public void render(float delta) {

		switch (state) {

		case PLAYING: {

			clearScreen();
			drawGrid();
			draw();
			drawMapElements();
			if (StartMenu.throttleAI) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		} // end case
		case WIN: {
			MusicHandler.stop();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

			// clearScreen();

			runningTime = System.currentTimeMillis() - runningTime;
			EDT.setScreen(new AIWinningScreen(EDT, runningTime, numberOfMoves, "HC"));
			this.dispose();
			break;
		}
		case LOSE: {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			clearScreen();
			runningTime = System.currentTimeMillis() - runningTime;
			EDT.setScreen(new AILosingScreen(EDT,runningTime,numberOfMoves, "HC"));
			this.dispose();
			break;
		} // end case
		default:
		} // end switch

	} // end render

	private void draw() {
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);

		batch.begin();

		aiDecision = SimulatedAnnealingSearch(landedSquare);

		if (!useArrow)
			batch.draw(playerOne, playerX, playerY);

		switch (aiDecision) {
		case UP:
			if (playerY != 492) {
				playerY += GRID_CELL;
				if (!useArrow)
					batch.draw(playerOne, playerX, playerY);

			}
			break;
		case LEFT:
			if (playerX != 0) {
				playerX -= GRID_CELL;
				if (!useArrow)
					batch.draw(playerOne, playerX, playerY);

			}
			break;
		case RIGHT:
			if (playerX != 492) {
				playerX += GRID_CELL;
				if (!useArrow)
					batch.draw(playerOne, playerX, playerY);

			}
			break;
		case DOWN:
			if (playerY != 0) {
				playerY -= GRID_CELL;
				if (!useArrow)
					batch.draw(playerOne, playerX, playerY);

			}
			break;

		case ARROW:
			useArrow = true;
			break;
		}
		landedSquare = checkCell(playerX, playerY);
		numberOfMoves++;
		batch.end();

	}

	/*
	 * implement Simulated Annealing search "bad moves" are at higher costs
	 */

	public AI_DECISION SimulatedAnnealingSearch(int i) {
		
		
		int nextCell = 0;
		int rng;
		int highestCost = mapCell.get(i).getAdjacentCells().get(0).getCost();
		int lowestCost = mapCell.get(i).getAdjacentCells().get(0).getCost();

		int currentCost;
		int[] costs = new int[mapCell.get(i).getAdjacentCells().size()];
		ArrayList<Integer> lowestCostCheck = new ArrayList<Integer>();

		int j;
		for (j = 0; j < mapCell.get(i).getAdjacentCells().size(); j++) {

			costs[j] = mapCell.get(i).getAdjacentCells().get(j).getCost();
			if (costs[j] > highestCost) {
				highestCost = costs[j];
			}
			if (costs[j] < lowestCost) {
				lowestCost = costs[j];
			}

		}

		if (lowestCost == highestCost) {
			
			rng = MathUtils.random(mapCell.get(i).getAdjacentCells().size() - 1);
			nextCell = mapCell.get(i).getAdjacentCells().get(rng).getCellNumber();
			
		} else {

			rng = MathUtils.random(RANDOM_VALUE) + 1;

			if (rng > temperature) {
				System.out.println(rng + "    " + temperature);
				// pick lower cost
				for (j = 0; j < mapCell.get(i).getAdjacentCells().size(); j++) {
					if (lowestCost == mapCell.get(i).getAdjacentCells().get(j).getCost()) {
						lowestCostCheck.add(mapCell.get(i).getAdjacentCells().get(j).getCellNumber());

					}
				}

				rng = MathUtils.random(lowestCostCheck.size() - 1);
				nextCell = lowestCostCheck.get(rng);

			} else {
				// pick higher cost
				System.out.println("I am here");
				for (j = 0; j < mapCell.get(i).getAdjacentCells().size(); j++) {
					if (highestCost == mapCell.get(i).getAdjacentCells().get(j).getCost()) {
						nextCell = mapCell.get(i).getAdjacentCells().get(j).getCellNumber();
					}
				}
			}
		}

		if (mapCell.get(nextCell).getBreeze() && mapCell.get(nextCell).getStench()) {
			temperature--;
		}
		else if(mapCell.get(nextCell).getStench()){
			temperature--;
		}
		else if (mapCell.get(nextCell).getBreeze()) {
			temperature--;
		}
		
		if(temperature == 0){
			state = STATE.LOSE;
		}

		System.out.println("Moved from: " + i + "  to  " + nextCell + " difference in cells: "
				+ (nextCell - mapCell.get(i).getCellNumber()));
		switch (nextCell - mapCell.get(i).getCellNumber()) {
		case 1:
			System.out.println("Wumpus moved UP");
			return AI_DECISION.UP;
		case -1:
			System.out.println("Wumpus moved DOWN");
			return AI_DECISION.DOWN;
		case -4:
			System.out.println("Wumpus moved LEFT");
			return AI_DECISION.LEFT;
		case 4:
			System.out.println("Wumpus moved RIGHT");
			return AI_DECISION.RIGHT;
		default:
		}
		return AI_DECISION.UP;
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

	int checkCell(int playerX, int playerY) {
		int square = 0;
		int i;
		for (i = 0; i < mapCell.size(); i++) {

			if (playerX == mapCell.get(i).getX() && playerY == mapCell.get(i).getY()) {
				square = i;

				if (mapCell.get(i).getBreeze() && mapCell.get(i).getStench()) {
					// add breeze and stench cost
					if(!mapCell.get(i).getVisited()){
					stenchCounter++;
					addCellCost(i, STENCH_BREEZE_COST);
					}
				} else if (mapCell.get(i).getStench()) {
					if (!mapCell.get(i).getVisited()){
						// add stench cost
						addCellCost(i, STENCH_COST);
						stenchCounter++;
					}
				} else if (mapCell.get(i).getBreeze()) {
					if (!mapCell.get(i).getVisited()){
						// add breeze cost
						addCellCost(i, BREEZE_COST);
					}
				} else {
					mapCell.get(i).setCost(1);
				}
				
				if(numberOfStenches == stenchCounter)
					mapCell.get(wumpusCell).setCost(1);
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
					// wumpus wins without the arrow
					state = STATE.WIN;
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
		return square;

	}

	void addCellCost(int i, int cellCost) {
		// integer i is the cell number
		int j;
		for (j = 0; j < mapCell.get(i).getAdjacentCells().size(); j++) {
			if (!mapCell.get(i).getAdjacentCells().get(j).getVisited())
				mapCell.get(i).getAdjacentCells().get(j).addCost(cellCost);
		}

	}

	void resetCostsAndVisited() {

		int i;
		for (i = 0; i < mapCell.size(); i++) {
			mapCell.get(i).setVisited(false);
			mapCell.get(i).setCost(1);

		}

	}

	private enum AI_DECISION {
		RIGHT, LEFT, UP, DOWN, ARROW;
	}

}
