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

	private Point pos;
	private Point prox;
	private Point intermedio;
	private LinkedList<Aircell> trajecto = new LinkedList<Aircell>();
	private Aircell proximaCelula;
	private Airspace espaco;

	private int initFuel;
	private int currentFuel;
	
	
	// Este campo é preenchido pelo run com o sentido, para ser usado no paint
	private int rotation = 0;
	// Quando está à espera da célula livre
	private boolean waiting = false;

	// Representacao Destino Final e Intermédio
	boolean destinoIntermedio = false;
	private Point pointoDestino;
	private boolean cheguei = false;

	// Pensar na dinâmica de conhecer os aeroportos e destinos.
	// Pertence ao aeroporto, ao avião, ao controller?
	// Tb tem de ter aqui acesso ao espacoAereo.
	// Para os pontos tb precisa de acesso ao counter
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
						//Se pos=prox consegui chegar à próxima célula, 
						//tenho de ir buscar mais
						//Se a proximaCélula estiver ocupada, eu tenho pos!=prox
						//Mas preciso de ir buscar uma célula nova na mesma 
						//Se já tiver um destinoIntermédio atribuido
						//Para contornar o deadlock
						//Se não juntar esta última guarda ele vai buscar a célula
						//Que estaria a seguir ao avião que está a bloquear o caminho e 
						//passa por cima dele.
						
						//Tenho de fazer um refactoring neste avião, e vai passar por um acesso mais inteligente às prócximas células, controlo de fluxo mais inteligente
						//A classe está martelada, bem como algumas deste projecto.
						if (pos.equals(prox)
								|| (!pos.equals(prox) && destinoIntermedio && proximaCelula
										.isOcupada())) {
							proximaCelula = trajecto.pollFirst();
							if (proximaCelula != null) {
								prox = proximaCelula.getPos();
								// Mete o avião no sentido certo.
								rotate();
							}
						}
						
						//Aqui move para a próxima célula 
						//Apenas se estiver livre
						
						
						synchronized (proximaCelula) {
							if(proximaCelula.isOcupada()){
								moveInCirc();
								//Há algo na falta do move que frita isto
//								move();
								//no pos ou prox
								//proxima celula
							}else{
								proximaCelula.ocupa();
								Aircell anterior = this.espaco.getCell(pos);
								
								move();
								//há stress por não estar a concorrer à minha anterior?
								//para me tirar de lá n há stress...
								anterior.desocupa();
							}
						}
						
						
						//Meti estes aqui para não executar quando estava no
						//destino intermédio, mas preciso de perder tb combustivel
						//quando ele está à espera.
						
						currentFuel -= CONSUMO;
						sleep(SLEEP_TIME);
					} else {
						//Chegou ao destino
						if(destinoIntermedio){
							//Era intermédio
							destinoIntermedio=false;
							setDestino(pointoDestino);
							//o prox agora continua igual a antes de meter novo destino.
						}else{
							//Não era intermedio mas final
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
					
					//waiting for command, consome tb combustível

					//Move em circulos apenas quando está à espera da célula 
					//não é aqui
					//moveInCirc();
					currentFuel -= CONSUMO;
					sleep(SLEEP_TIME);
				}
				
			}
			if(!(currentFuel>0)){
				controller.getPointCounter().addPoints(-100);
			}
			Aircell anterior = this.espaco.getCell(pos);
			anterior.removeOcupante();
			anterior.desocupa();
			synchronized (controller.getAirplanes()) {
				controller.getAirplanes().remove(this);
			} 
			controller.updateUI();

		} catch (InterruptedException e) {
		}

	}
	
	private void rotate(){
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
		// Falta cenas acho eu
	}


	/*
	 * Tenho de reformular aspectos para o acesso às células. Concorrente. Aqui?
	 * Vou fazer agora assim: Quando ele move acede ao espaço e à célula. Tem de
	 * tentar acesso sem fazer wait para gastar fuel. Outra coisa, se me movo
	 * para outro lado, tenho de me retirar da anterior.
	 */
	private void move() {
		int dx = prox.x - pos.x;
		int dy = prox.y - pos.y;
		espaco.getCell(pos).removeOcupante();
		if (Math.abs(dx) > Math.abs(dy)) {
			if (dx > 0) {
				pos.x++;
			} else {
				pos.x--;
			}
		} else {
			if (dy > 0) {
				pos.y++;
			} else {
				pos.y--;
			}
		}
		espaco.getCell(pos).setOcupante(this);
	}

	public void setDestino(Point pointoDestino) {
		//Se for usado no âmbito de um destino intermédio não mexe
		if(!destinoIntermedio){
			this.pointoDestino = pointoDestino;
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
			if(destinoIntermedio){
				System.out.println(proxCelula);
			}
		}
		if(destinoIntermedio){
			System.out.println(trajecto);
		}
		
	}
	
	public void waitCommand() {
		this.waiting = true;
	}
	
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
		this.initFuel = ((int)(neededFuel* (1+RESERVA)))*CONSUMO;
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
