package rui.control;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import rui.air.AirThing;
import rui.air.AirType;
import rui.air.Airplane;
import rui.air.Airport;
import rui.air.Airspace;

import rui.ui.GameInterface;

//Ainda haverá alguma parte daqui que possa ser separada?
//Mais tarde pensa-se na outra arquitectura.
public class GameController {

	public static final double cellBaseDim = 30.0;

	private LinkedList<Airport> airports;
	private LinkedList<Airplane> airplanes;
	private PointCounter pointCounter;
	private Airspace espaco;
	private GameInterface ui;

	private Airplane waitingPlane;

	private int colsNum;
	private int rowsNum;

	// campos referentes ao jogo
	// métodos de acesso ás infos
	// set jogo local e servidor
	// métodos de contrução do jogo, criar interface e criar campo aéreo
	// procura pelo ficheiro de jogo se é caso disso

	// construtores, por ficheiro, e por parâmetros

	public GameController() {

	}

	public void createGameByFile() {
		
		try {
			// Falta meter inteligência de controlo de coordenadas
			Scanner reader = new Scanner(new FileReader("aeroportos.txt"));
			this.pointCounter = new PointCounter();
			if(reader.hasNext()){
				this.colsNum = reader.nextInt();
				this.rowsNum = reader.nextInt();
				this.espaco = new Airspace(colsNum, rowsNum);
			}
			
			airplanes = new LinkedList<Airplane>();
			airports = new LinkedList<Airport>();
			
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

	public void createGameRandom(int colsNum, int rowsNum, int numAirports) {
		//tratar erros de casos extremos
		assert colsNum>0 && rowsNum>0 && numAirports>0;
		assert (colsNum*rowsNum)>numAirports;
		
		this.colsNum=colsNum;
		this.rowsNum=rowsNum;
		this.pointCounter = new PointCounter();
		this.espaco = new Airspace(colsNum, rowsNum);
		airplanes = new LinkedList<Airplane>();
		airports = new LinkedList<Airport>();
		
		for(int i=0; i<numAirports; i++){
			Random rand = new Random();
			int x;
			int y;
			//repetir pares X,Y até encontrar um que esteja livre
			boolean posLivre;
			do {
				x = rand.nextInt(colsNum);
				y = rand.nextInt(rowsNum);
				posLivre = true;
				for (Airport airport : airports) {
					if (airport.getPos().x == x && airport.getPos().y == y) {
						posLivre = false;
					}
				}
			} while (!posLivre);
			
			Airport airport = new Airport(this, new Point(x, y), espaco);
			synchronized (airports) {
				airports.add(airport);
			}
		}
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
		
		// If is null vê se há avião à espera e faz setIntermédio
		// Se não null pergunta se é avião.

		AirThing thing = espaco.getCell(p).getOcupante();

		if (thing != null) {

			// É avião?
			if (thing.getAirType() == AirType.AIRPLANE) {
				Airplane airplane = (Airplane) thing;
				if (airplane.isWaitingCommand()) {
					airplane.stopWaiting();
					waitingPlane = null;
				} else {
					if (waitingPlane != null) {
						// Saltou de um avião para outro
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
