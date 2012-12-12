package rui.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import rui.control.GameController;
import rui.air.Airport;

public class AirportGraphic {

	private static final double BASE_DIM = 30;

	private LinkedList<Airport> airports;
	
	public AirportGraphic(GameController controller){
		this.airports = controller.getAirports();
	}
	
	public void paintAll(Graphics g){
		synchronized (airports) {
			for (Airport airport : airports) {
				paint(g, airport);
			}
		}
	}
	
	public void paint(Graphics g, Airport airport) {
		Point pos = airport.getPos();
		
		g.setColor(Color.blue);
		g.fillRect((int)(pos.x*BASE_DIM)+1, (int)(pos.y*BASE_DIM)+1, (int)BASE_DIM-1, (int)BASE_DIM-1);
	}
	
}
