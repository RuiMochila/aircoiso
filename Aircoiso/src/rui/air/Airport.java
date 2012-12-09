package rui.air;


import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;
import rui.control.GameController;


public class Airport extends Thread implements Runnable, AirThing{

	
	private static final AirType type = AirType.AIRPORT; // pq e que isto faz sentido? pq e que nao extende um airtype?pq e enumerado?
//	private static final int NUM_AVIOES = 3;
	private GameController controller;
	private Airspace espaco;
	private Point pos;
	
	public Airport(GameController controller, Point pos, Airspace espaco){
		this.pos=pos;
		this.controller= controller;
		this.espaco= espaco;
	}
	

	
	@Override
	public void run() {
		//eu lan�o avi�es quando os h� para lan�ar
		//n�o fa�o nada entrenanto depois, nesta fase do trabalho
		controller.updateUI();
		//LANCA 1 AVIAO
		//////////
		
		//fa�o uma copia da lista de aeroportos
		LinkedList<Airport> copia = new LinkedList<Airport>(controller.getAirports());
		//excluo-me da lista e fico com todos os outros destinos
		copia.remove(this);
		//se eu n�o era o �nico
		if(copia.size()>0){
			//escolho um aleat�riamente
			Random r = new Random();
			int i = r.nextInt(copia.size());
			Point destino = copia.get(i).pos;//n h� stress se n fizer clone
			Airplane airplane = new Airplane(controller, new Point(pos), espaco);
			airplane.setDestino(destino);
			airplane.abastece(airplane.getTimeToDestino());
			synchronized (controller.getAirplanes()) {
				controller.getAirplanes().add(airplane);
			}
//			airplane.start();
		}
		/////////
		
		//Aqui?
		
	}
	//Na segunda fase, eu recebo avi�es de chegada 
	//(m�todo de aterragem concorrente)
	//depois aqui no run se existirem volta a lan��lo

	
	//Nao consigo perceber pq estes dois metodos onde sao precisos? se calhar vou perceber mais a frente
	@Override
	public AirType getAirType() {
		return type;
	}



	public Point getPos() {
		return this.pos;
	}

	
}
