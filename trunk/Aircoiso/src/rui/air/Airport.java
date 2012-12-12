package rui.air;


import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import rui.control.GameController;


public class Airport extends Thread implements Runnable{

	
//	private static final AirType type = AirType.AIRPORT;
	private static final int MIN_AVIOES = 1;
	private static final int MAX_AVIOES = 3;
	private GameController controller;
	private Airspace espaco;
	private Point pos;
	private AtomicInteger plainsToLaunch;
	
	public Airport(GameController controller, Point pos, Airspace espaco){
		this.pos=pos;
		this.controller= controller;
		this.espaco= espaco;
		this.plainsToLaunch = new AtomicInteger(MIN_AVIOES);
		Random rand = new Random();
		this.plainsToLaunch.addAndGet(rand.nextInt(MAX_AVIOES));
	}
	

	@Override
	public void run() {
		//eu lanço aviões quando os há para lançar
		//não faço nada entrenanto depois, nesta fase do trabalho
//		controller.updateUI();
		while(!isInterrupted()){
			if(plainsToLaunch.get()>0){
				lancaAviao();
				plainsToLaunch.decrementAndGet();
			}
			
		}

		
		
		
	}


	private void lancaAviao() {
		//LANCA 1 AVIAO
		//////////
		//faço uma copia da lista de aeroportos
		LinkedList<Airport> copia = new LinkedList<Airport>(controller.getAirports());
		//excluo-me da lista e fico com todos os outros destinos
		copia.remove(this);
		//se eu não era o único
		if(copia.size()>0){
			//escolho um aleatóriamente
			Random r = new Random();
			int i = r.nextInt(copia.size());
			Point destino = copia.get(i).pos;//n há stress se n fizer clone
			Airplane airplane = new Airplane(controller, new Point(pos), espaco);
			airplane.setDestino(destino);
			airplane.abastece(airplane.getTimeToDestino());
//			synchronized (controller.getAirplanes()) {
				controller.getAirplanes().add(airplane);
//			}
			airplane.start();
		}
		/////////
	}

	public void aterraAviao(){
		this.plainsToLaunch.incrementAndGet();
		System.out.println("recebi aterragem");
		System.out.println(plainsToLaunch.get());
	}


	public Point getPos() {
		return this.pos;
	}

	
}
