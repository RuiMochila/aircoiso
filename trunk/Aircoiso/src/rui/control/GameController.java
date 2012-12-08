package rui.control;

import java.awt.Graphics;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Scanner;

import rui.air.AirThing;
import rui.air.AirType;
import rui.air.Airplane;
import rui.air.Airport;
import rui.air.Airspace;

import rui.ui.GameInterface;

//Ainda haver� alguma parte daqui que possa ser separada?
//Mais tarde pensa-se na outra arquitectura.
public class GameController {

	public static final double cellBaseDim = 30.0;
	private int colsNum; // pq � que isto est� aqui no controlador?
	private int rowsNum;

	private LinkedList<Airport> airports;
	private LinkedList<Airplane> airplanes;
	private PointCounter pointCounter;
	private Airspace espaco;
	private GameInterface ui;

	private Airplane waitingPlane;

	// campos referentes ao jogo
	// m�todos de acesso �s infos
	// set jogo local e servidor
	// m�todos de contru��o do jogo, criar interface e criar campo a�reo
	// procura pelo ficheiro de jogo se � caso disso

	// construtores, por ficheiro, e por par�metros

	public GameController(int colsNum, int rowsNum) {
		this.colsNum = colsNum;
		this.rowsNum = rowsNum;
		this.pointCounter = new PointCounter();
		this.espaco = new Airspace(colsNum, rowsNum);
		airplanes = new LinkedList<Airplane>();
		airports = new LinkedList<Airport>();
		// apenas para teste agora
	}

	public void createGameByFile() {
		try {
			// Falta meter intelig�ncia de controlo de coordenadas
			Scanner reader = new Scanner(new FileReader("aeroportos.txt"));
			while (reader.hasNext()) {
				int x = reader.nextInt();
				int y = reader.nextInt();
				Airport airport = new Airport(this, new Point(x, y), espaco);
				synchronized (airports) {
					airports.add(airport);
				}
				
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void createGameRandom(int numAirports) {

		// Random para as posi��es existentes.
		// Situa��es extremas

	}

	public void initUI() {
		ui = new GameInterface(this);
	}

	public int getColsNum() {
		return colsNum;

	}

	public int getRowsNum() {
		return rowsNum;

	}

	public int getPoints() {
		return pointCounter.getPoints();
	}

	public PointCounter getPointCounter() {
		return pointCounter;
	}

	public LinkedList<Airport> getAirports() {
		return this.airports;
	}

	public LinkedList<Airplane> getAirplanes() {
		return this.airplanes;
	}

	public void click(Point p) {
		
		// If is null v� se h� avi�o � espera e faz setInterm�dio
		// Se n�o null pergunta se � avi�o.

		AirThing thing = espaco.getCell(p).getOcupante();

		if (thing != null) {

			// � avi�o?
			if (thing.getAirType() == AirType.AIRPLANE) {
				Airplane airplane = (Airplane) thing;
				if (airplane.isWaitingCommand()) {
					airplane.stopWaiting();
					waitingPlane = null;
				} else {
					if (waitingPlane != null) {
						// Saltou de um avi�o para outro
						waitingPlane.stopWaiting();
					}

					airplane.waitCommand();
					waitingPlane = airplane;
				}
			}
		} else {
			if (waitingPlane != null) {
				waitingPlane.setIntermedio(p);
				waitingPlane.stopWaiting();
				waitingPlane = null;
			}
		}

	}

	public void initAirports() {
		for (Airport airport : airports) {
			airport.start();
		}
	}

	public void initAirplanes() {
		synchronized (airplanes) {
			for (Airplane airplane : airplanes) {
				airplane.start();
			}
		}
		
	}

	public void interruptGame() {
		for (Airport airport : airports) {
			airport.interrupt();
		}
		for (Airplane airplane : airplanes) {
			airplane.interrupt();
		}

	}
	
	public void updateUI(){
		ui.repaint();
	}

	public Airspace getAirspace() {
		
		return this.espaco;
	}

}
