package rui.air;

import java.awt.Point;

public class Airspace {

	private int numCelX;
	private int numCelY;
	private Aircell[][] celulas;

	public Airspace(int numCelX, int numCelY) {
		super();
		this.numCelX = numCelX;
		this.numCelY = numCelY;
		celulas = new Aircell[numCelX][numCelY];
		for (int x = 0; x < numCelX; x++) {
			for (int y = 0; y < numCelY; y++) {
				Point ponto = new Point(x,y);
				celulas[x][y] = new Aircell(ponto);
			}
		}

	}

	public Aircell getCell(Point posicao) {
		int x = posicao.x;
		int y = posicao.y;
		return celulas[x][y];
	}
	
}
