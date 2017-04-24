package com.mygdx.wwgdx;

import java.util.ArrayList;

public class MapCell {
	private int cellX;
	private int cellY;
	private int cellNumber;
	private boolean wumpus;
	private boolean stench;
	private boolean pit;
	private boolean breeze;
	private boolean visited;
	private int cost;
	private ArrayList<MapCell> AdjacentCells;
	public MapCell(int x, int y, int cellNumber) {
		this.cellX = x;
		this.cellY = y;
		this.cellNumber = cellNumber;
		cost = 1;
		
		AdjacentCells = new ArrayList<MapCell>();
		wumpus = false;
		stench = false;
		pit = false;
		breeze = false;
		visited = false;
	}

	public void setWumpus(boolean wumpus) {
		this.wumpus = wumpus;
	}

	public boolean getWumpus() {
		return wumpus;
	}

	public void setStench(boolean stench) {
		this.stench = stench;
	}

	public boolean getStench() {
		return stench;
	}

	public void setPit(boolean pit) {
		this.pit = pit;
	}

	public boolean getPit() {
		return pit;
	}

	public void setBreeze(boolean breeze) {
		this.breeze = breeze;
	}

	public boolean getBreeze() {
		return breeze;
	}

	public int getX() {
		return cellX;
	}

	public int getY() {
		return cellY;
	}

	public int getCellNumber() {
		return cellNumber;
	}
	
	public void setCellNumber(int cellNumber){
		this.cellNumber = cellNumber;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public boolean getVisited() {
		return visited;
	}
	
	public void addCost(int cost){
		this.cost += cost;
	}
	
	public int getCost(){
		return cost;
	}
	
	public void addAdjacentCell(MapCell cell){
		AdjacentCells.add(cell);
	}
	
	public ArrayList<MapCell> getAdjacentCells(){
		return AdjacentCells;
	}

	@Override
	public String toString() {

		return "Cell Number: " + cellNumber + "\tWumpus: " + wumpus + "\tStench: " + stench + "\tPit: " + pit
				+ "\tBreeze: " + breeze;
	}

	public void setCost(int cost) {
		this.cost = cost;
		
	}
}
