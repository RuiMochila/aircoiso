package rui.control;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import rui.air.AirThing;
import rui.air.AirType;
import rui.air.Airplane;
import rui.air.Airport;
import rui.air.Airspace;

import rui.ui.GameInterface;

//Ainda haverá alguma parte daqui que possa ser separada?
//Mais tarde pensa-se na outra arquitectura.
public class GameController {

	public static final double cellBaseDim = 30.0; // pois nao consigo entender
													// pq nao esta mesmo na
													// classe Celula D
	// o cellBasedim está aí porque é uma "constante"
	// de projecto e pensei logo que ia ser partilhada,
	// e como o controlador é uma classe com uma óptima
	// exposição ao projecto useia para guardar essa constante

	// acho que continua a nao fazer sentido. e uma constante, tudo bem, mas
	// podia estar noutra classe qq :s sei la.. nas definicoes tipo num
	// linhas/num colunas prai D
	// epah e um bocado irrelevante nao te preocupes com isso.
	// meti aqui logo de inicio porque sabia que a partida seria
	// visivel por todas as classes
//	private LinkedList<Airport> airports;
//	private LinkedList<Airplane> airplanes;
	private ConcurrentLinkedQueue<Airplane> airplanes;
	private ConcurrentLinkedQueue<Airport> airports;
	
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

	// e preciso haver construtor ou nao? Senao tiver nao faz mal certo? D
	// nao, isto nao faz aqui nada
	public GameController() {

	}

	public void createGameByFile() {

		try {
			// Falta meter inteligência de controlo de coordenadas
			Scanner reader = new Scanner(new FileReader("aeroportos.txt"));
			this.pointCounter = new PointCounter();
			if (reader.hasNext()) {
				this.colsNum = reader.nextInt();
				this.rowsNum = reader.nextInt();
				this.espaco = new Airspace(colsNum, rowsNum);
			}

			airplanes = new ConcurrentLinkedQueue<Airplane>();
			airports = new ConcurrentLinkedQueue<Airport>();

			while (reader.hasNext()) {
				int x = reader.nextInt();
				int y = reader.nextInt();
				Point ponto = new Point(x, y);
				Airport airport = new Airport(this, ponto, espaco);
				espaco.getCell(ponto).setAeroporto(airport);
//				synchronized (airports) {
					airports.add(airport);
//				}

			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void createGameRandom(int colsNum, int rowsNum, int numAirports) {
		// tratar erros de casos extremos
		assert colsNum > 0 && rowsNum > 0 && numAirports > 0;
		assert (colsNum * rowsNum) > numAirports;

		this.colsNum = colsNum;
		this.rowsNum = rowsNum;
		this.pointCounter = new PointCounter();
		this.espaco = new Airspace(colsNum, rowsNum);
		airplanes = new ConcurrentLinkedQueue<Airplane>();
		airports = new ConcurrentLinkedQueue<Airport>();

		for (int i = 0; i < numAirports; i++) {
			Random rand = new Random();
			int x;
			int y;
			// repetir pares X,Y até encontrar um que esteja livre
			boolean posLivre;
			do {
				x = rand.nextInt(colsNum);
				y = rand.nextInt(rowsNum);
				posLivre = true;
				for (Airport airport : airports) {
					// Se já existe um aeroporto nestas coordenadas dá false
					if (airport.getPos().x == x && airport.getPos().y == y) {
						posLivre = false;
					}
					// Se ja existir um aeroporto nas células á volta tb dá
					// false
					// acabo depois se der tempo, é mais crítico o aeroporto.
				}
			} while (!posLivre);

			Point ponto = new Point(x, y);
			Airport airport = new Airport(this, ponto, espaco);
			espaco.getCell(ponto).setAeroporto(airport);
//			synchronized (airports) {
				airports.add(airport);
//			}
		}
	}

	public void createGameTest() {

		try {
			// Falta meter inteligência de controlo de coordenadas
			Scanner reader = new Scanner(new FileReader("aeroportosBase.txt"));
			this.pointCounter = new PointCounter();
			if (reader.hasNext()) {
				this.colsNum = reader.nextInt();
				this.rowsNum = reader.nextInt();
				this.espaco = new Airspace(colsNum, rowsNum);
			}

			airplanes = new ConcurrentLinkedQueue<Airplane>();
			airports = new ConcurrentLinkedQueue<Airport>();

			while (reader.hasNext()) {
				int x = reader.nextInt();
				int y = reader.nextInt();
				Point ponto = new Point(x, y);
				Airport airport = new Airport(this, ponto, espaco);
				espaco.getCell(ponto).setAeroporto(airport);
//				synchronized (airports) {
					airports.add(airport);
//				}

			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void initUI() {
		ui = new GameInterface(this); // pq e que recebe este objecto? D
		// como te epliquei ontem, a GameInterface precisa de falar com o
		// controller
		// entao quando o controller a cria dase a ela, para se ficarem a
		// conhecer ;)
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

	public ConcurrentLinkedQueue<Airport> getAirports() {
		return this.airports;
	}

	public ConcurrentLinkedQueue<Airplane> getAirplanes() {
		return this.airplanes;
	}

	public void click(Point p) {

		// If is null vê se há avião à espera e faz setIntermédio
		// Se não null pergunta se é avião.

		Airplane airplane = espaco.getCell(p).getOcupante();
		// se calhar isto fica um bocado confuso, o ocupante podia estar mesmo
		// no espaco nao? D
		// eu acho que ficava melhor porque a celula e como um auxilio para o
		// espaco D

		// nao percebi... isto vai as espaco buscar a celula naquele ponto, e
		// depois
		// a celula pede o ocupante, se e que existe la algum agora

		if (airplane != null) {

			// Airplane airplane = (Airplane) thing; // esta instrucao nao esta
			// ao cntrario? thing = Airoplane aeroplane? D
			// nao. se a thing e do tipo Airplane ent faco caste para airplane e
			// uso airplane a partir daqui
			// desculpa amor entretanto mudei isto...
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
		} else { // nao percebi mto bem este D
			// se chegar aqui então a thing que saiu da célula clicada é != null
			// então pergunta se existe um avião à espera de ordens =)
			if (waitingPlane != null) {
				// se sim dá-lhe um destino intermédio
				waitingPlane.setIntermedio(p);
				// já n está à espera de comando
				waitingPlane.stopWaiting();
				// digo aqui que mais nenhum avião está à espera de ordens
				waitingPlane = null;
			}
			// eu faço isto para chamar apenas um método do controlador
			// e aqui vejo o que se faz. se clicar num avião guado-o para
			// a seguir receber ordens aqui.
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

	public void updateUI() {
		ui.repaint();
	}

	public Airspace getAirspace() {

		return this.espaco;
	}

}
