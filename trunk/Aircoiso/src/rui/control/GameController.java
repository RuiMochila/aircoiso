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

//Ainda haver� alguma parte daqui que possa ser separada?
//Mais tarde pensa-se na outra arquitectura.
public class GameController {

	public static final double cellBaseDim = 30.0; //pois nao consigo entender pq nao esta mesmo na classe Celula D
	//o cellBasedim est� a� porque � uma "constante" 
	//de projecto e pensei logo que ia ser partilhada, 
	//e como o controlador � uma classe com uma �ptima 
	//exposi��o ao projecto useia para guardar essa constante
	
	//acho que continua a nao fazer sentido. e uma constante, tudo bem, mas podia estar noutra classe qq :s sei la.. nas definicoes tipo num linhas/num colunas prai D

	private LinkedList<Airport> airports;
	private LinkedList<Airplane> airplanes;
	private PointCounter pointCounter;
	private Airspace espaco;
	private GameInterface ui;

	private Airplane waitingPlane;

	private int colsNum;
	private int rowsNum;

	// campos referentes ao jogo
	// m�todos de acesso �s infos
	// set jogo local e servidor
	// m�todos de contru��o do jogo, criar interface e criar campo a�reo
	// procura pelo ficheiro de jogo se � caso disso

	// construtores, por ficheiro, e por par�metros

	public GameController() {

	}

	//pois tmb nao entendo este metodo aqui. supostamente o controlador nao devia apenas fazer o update e esse tipo de coisas a iterface grafica? 
	// O criar jogo tmb acho que devia estar num pacote jogo com as definicoes todas do jogo ou assim ou ceu como o prof lhe chama D
	public void createGameByFile() {
		
		try {
			// Falta meter intelig�ncia de controlo de coordenadas
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
			//repetir pares X,Y at� encontrar um que esteja livre
			boolean posLivre;
			do {
				x = rand.nextInt(colsNum);
				y = rand.nextInt(rowsNum);
				posLivre = true;
				for (Airport airport : airports) {
					//Se j� existe um aeroporto nestas coordenadas d� false 
					if (airport.getPos().x == x && airport.getPos().y == y) {
						posLivre = false;
					}
					//Se ja existir um aeroporto nas c�lulas � volta tb d� false
					//acabo depois se der tempo, � mais cr�tico o aeroporto.
				}
			} while (!posLivre);
			
			Airport airport = new Airport(this, new Point(x, y), espaco);
			synchronized (airports) {
				airports.add(airport);
			}
		}
	}

	public void initUI() {
		ui = new GameInterface(this); // pq e que recebe este objecto? D
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

	//sinceramente tambem nao entendo o pq deste metodo aqui D 
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
		} else { //nao percebi mto bem este D
			//se chegar aqui ent�o a thing que saiu da c�lula clicada � != null
			//ent�o pergunta se existe um avi�o � espera de ordens =)
			if (waitingPlane != null) {
				//se sim d�-lhe um destino interm�dio
				waitingPlane.setIntermedio(p);
				//j� n est� � espera de comando
				waitingPlane.stopWaiting();
				//digo aqui que mais nenhum avi�o est� � espera de ordens
				waitingPlane = null;
			}
			//eu fa�o isto para chamar apenas um m�todo do controlador
			//e aqui vejo o que se faz. se clicar num avi�o guado-o para
			//a seguir receber ordens aqui.
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
