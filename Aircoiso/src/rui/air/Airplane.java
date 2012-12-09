package rui.air;


import java.awt.Point;
import java.util.LinkedList;
import rui.control.GameController;


public class Airplane extends Thread implements Runnable, AirThing {

	
	private final static int SLEEP_TIME = 1000;
	public static final double RESERVA = 0.30;
	private static final int CONSUMO = 10;

	private final static AirType type = AirType.AIRPLANE;
	private GameController controller;

	//currente
	private Point pos;
	//prox no trajecto
	private Point prox;
	//ponto intermedio
	private Point intermedio;
	private LinkedList<Aircell> trajecto = new LinkedList<Aircell>();
	private Aircell proximaCelula;
	private Airspace espaco;

	private int initFuel;
	private int currentFuel;
	
	// Este campo � preenchido pelo run com o sentido, para ser usado no paint
	private int rotation = 0;
	// Quando est� � espera de uma ordem
	private boolean waiting = false;

	// Representacao Destino Final e Interm�dio
	boolean destinoIntermedio = false; 
	private Point destinoFinal;
	private boolean cheguei = false;

	public Airplane(GameController controller, Point pos,
			Airspace espaco) {
		this.controller = controller;
		this.pos = pos;
		this.currentFuel = initFuel;
		this.espaco = espaco;
	}

	@Override
	public void run() {
		try {
			prox = (Point) pos.clone();
			//esta linha vai ser alterada
			while (!cheguei && currentFuel>0) {
				controller.updateUI();
				if (!waiting) {
					if (trajecto.size() > 0) {
						//Se pos=prox consegui chegar � pr�xima c�lula, 
						//tenho de ir buscar mais
						//Se a proximaC�lula estiver ocupada, eu tenho pos!=prox
						//Mas preciso de ir buscar uma c�lula nova na mesma 
						//Se j� tiver um destinoInterm�dio atribuido
						//Para contornar o deadlock
						//Se n�o juntar esta �ltima guarda ele vai buscar a c�lula
						//Que estaria a seguir ao avi�o que est� a bloquear o caminho e 
						//passa por cima dele.
						
						//Tenho de fazer um refactoring neste avi�o, e vai passar por um acesso mais inteligente �s pr�cximas c�lulas, controlo de fluxo mais inteligente
						//A classe est� martelada, bem como algumas deste projecto.
						if (pos.equals(prox)
								|| (!pos.equals(prox) && destinoIntermedio && proximaCelula
										.isOccupied())) {
							proximaCelula = trajecto.pollFirst();
							if (proximaCelula != null) {
								prox = proximaCelula.getPos();
								// Mete o avi�o no sentido certo.
								rotateDirection();
							}
						}
						
						//Aqui move para a pr�xima c�lula 
						if(proximaCelula.tryOcupyCell(this)){
							Aircell anterior = this.espaco.getCell(pos);
							this.pos=proximaCelula.getPos();
							anterior.leaveCell();
						}else{
							moveInCirc();
						}
						
						
						//Meti estes aqui para n�o executar quando estava no
						//destino interm�dio, mas preciso de perder tb combustivel
						//quando ele est� � espera.
						
						currentFuel -= CONSUMO;
						sleep(SLEEP_TIME);
					} else {
						//Chegou ao destino
						if(destinoIntermedio){
							//Era interm�dio, volto a fazer setDestino com o final
							destinoIntermedio=false;
							setDestino(destinoFinal);
						}else{
							//N�o era intermedio mas final
							//O que acontece quando eu chego ao meu destino?
							
							synchronized (controller.getAirplanes()) {
								controller.getAirplanes().remove(this);
							} 
							this.cheguei=true;
							controller.getPointCounter().addPoints(10);
							controller.getPointCounter().addPoints((int)(this.currentFuel/CONSUMO));
						}
					}
				}else{
					
					//waiting for command, consome tb combust�vel

					currentFuel -= CONSUMO;
					sleep(SLEEP_TIME);
				}
				
			}
			if(!(currentFuel>0)){
				controller.getPointCounter().addPoints(-100);
			}
			Aircell anterior = this.espaco.getCell(pos);
			anterior.leaveCell();
			
			synchronized (controller.getAirplanes()) {
				controller.getAirplanes().remove(this);
			} 
			controller.updateUI();

		} catch (InterruptedException e) {
		}

	}
	
	private void rotateDirection(){
		//tu nao trocaste isto? onde esta x nao e suposto estar y? D
		if(prox.x>pos.x){
			rotation = 90;
		}else if(prox.x<pos.x){
			rotation=270;
		}else if(prox.y>pos.y){
			rotation=180;
		}else{
			rotation=0;
		}
	}

	private void moveInCirc() {
		rotation += 90;
	}

	public void setDestino(Point pointoDestino) {
		//Se n�o estou para destino interm�dio, � o destino final, guarda-o.
		if(!destinoIntermedio){
			this.destinoFinal = pointoDestino;
		}
		Point proxCelula = (Point) pos.clone();
		trajecto.clear();
		while (espaco.getCell(proxCelula) != espaco.getCell(pointoDestino)) {
			int dx = pointoDestino.x - proxCelula.x;
			int dy = pointoDestino.y - proxCelula.y;
			if (Math.abs(dx) > Math.abs(dy)) {
				if (dx > 0) {
					proxCelula.x++;
				} else {
					proxCelula.x--;
				}
			} else {
				if (dy > 0) {
					proxCelula.y++;
				} else {
					proxCelula.y--;
				}
			}
			trajecto.addLast(espaco.getCell(proxCelula));
		}
	}
	
	public void waitCommand() {
		this.waiting = true;
	}
	
	//pq lhe chamas Time? D
	public int getTimeToDestino(){
		if(trajecto!=null){
			return trajecto.size();
		}
		return 0;
	}
	
	public void setCurrentFuel(int fuel){
		this.currentFuel=fuel;
	}
	
	public int getCurrentFuel(){
		return currentFuel;
	}
	
	public int getInitFuel(){
		return initFuel;
	}
	
	public void setInitFuel(int fuel){
		this.initFuel=fuel;
	}
	
	public void abastece(int neededFuel){
		this.initFuel = (int)((double)(neededFuel*(double)(1+RESERVA))*CONSUMO);
		this.currentFuel=this.initFuel;
	}
	
	public boolean isWaitingCommand() {
		return waiting;
	}
	
	public void stopWaiting() {
		this.waiting = false;
		
	}
	
	public void setIntermedio(Point intermedio){
		this.intermedio = intermedio;
		destinoIntermedio = true;
		controller.getPointCounter().addPoints(-1);
		setDestino(intermedio);
		
	}
	
	@Override
	public AirType getAirType() {
		return type;
	}

	public Point getPos() {
		return pos;
	}

	public int getRotation() {
		return rotation;
	}

	public boolean isHeadingDestinoIntermedio() {
		return destinoIntermedio;
	}
	
	public Point getIntermedio(){
		return intermedio;
	}

}
