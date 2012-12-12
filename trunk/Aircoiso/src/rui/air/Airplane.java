package rui.air;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

import rui.control.GameController;

public class Airplane extends Thread implements Runnable {

	private final static int SLEEP_TIME = 1000;
	public static final double RESERVA = 0.30;
	private static final int CONSUMO = 10;

	// private final static AirType type = AirType.AIRPLANE;
	private GameController controller;

	// currente
	private Point pos;
	// prox no trajecto
	private Point prox;
	// ponto intermedio
	private Point intermedio;
	private LinkedList<Aircell> trajecto = new LinkedList<Aircell>();
	private Aircell proximaCelula;
	private Airspace espaco;

	private int initFuel;
	private int currentFuel;

	// Este campo é preenchido pelo run com o sentido, para ser usado no paint
	private int rotation = 0;
	private boolean visible = false;
	// Quando está à espera de uma ordem
	private boolean waiting = false;

	// Representacao Destino Final e Intermédio
	boolean destinoIntermedio = false;
	private Point destinoFinal;
	private boolean cheguei = false;

	public Airplane(GameController controller, Point pos, Airspace espaco) {
		this.controller = controller;
		this.pos = pos;
		this.currentFuel = initFuel;
		this.espaco = espaco;
	}

	@Override
	public void run() {
		// Quando começa torna-se visível
		try {
			esperaTempoAleatorio();
			tentaDescolar();
			this.visible = true;

			prox = (Point) pos.clone();
			while (!cheguei && currentFuel > 0) {
				controller.updateUI();

				if (!waiting) {// Se n está à espera de um comando
					if (trajecto.size() > 0) {// Se existem celulas a percorrer,
												// segue:

						getCellToGo();

						tryMoveNextCell();

					} else {// Chegou ao um destino

						if (destinoIntermedio) {
							// Era intermédio, volto a fazer setDestino com o
							// destino final. Ele retoma o seu caminho para lá
							destinoIntermedio = false;
							setDestino(destinoFinal);
						} else {
							// Não era um intermedio qlqr, mas o final
							arriveProcedure();
						}
					}
				}

				currentFuel -= CONSUMO;
				sleep(SLEEP_TIME);
			}
			// Chegou ao final ou perdeu o combustível

			endProcedure();

		} catch (InterruptedException e) {
		}

	}

	private void tentaDescolar() throws InterruptedException {
		if (proximaCelula != null) {

			while (proximaCelula.isOccupied()) {

				sleep(SLEEP_TIME);

			}
		}
	}

	private void esperaTempoAleatorio() throws InterruptedException {
		Random rand = new Random();
		int time = rand.nextInt(3000);
		sleep(time);
	}

	private void endProcedure() {
		// Se foi por falta de combustivel perde pontos
		// Somo 10 pk depois de chegar ao destino final ainda passa pelo consumo
		//isto n estava bem por alguma razao que n vi e k n preocupa
		if (!((currentFuel) > 0)) {
			System.out.println("Despenhou-se com " + currentFuel);
			controller.getPointCounter().addPoints(-100);
		}
		// Liberto a célula de onde desapareci
		Aircell anterior = this.espaco.getCell(pos);
		anterior.leaveCell();
		// Removo-me e faço update á UI.
		// synchronized (controller.getAirplanes()) {
		controller.getAirplanes().remove(this);
		// }
		controller.updateUI();
	}

	private void arriveProcedure() {
		// Removo-me da lista de aviões
		// synchronized (controller.getAirplanes()) {
		controller.getAirplanes().remove(this);
		// }
		this.cheguei = true;
		// Adiciono a pontuação
		Airport aeroporto = espaco.getCell(pos).getAeroporto();
		if (aeroporto != null) {
			aeroporto.aterraAviao();
		}
		controller.getPointCounter().addPoints(10);
		controller.getPointCounter().addPoints(
				(int) (this.currentFuel / CONSUMO));
	}

	private void tryMoveNextCell() {
		// Aqui move para a próxima célula, se possível
		if (proximaCelula.tryOccupyCell(this)) {
			Aircell anterior = this.espaco.getCell(pos);
			this.pos = proximaCelula.getPos();
			anterior.leaveCell();
		} else {
			moveInCirc();
		}
	}

	private void getCellToGo() {
		// Vai buscar mais uma celula para percorrer se:
		// pos=prox (consegui chegar à próxima célula)
		// OU
		// Se a proximaCélula estiver ocupada, eu tenho pos!=prox
		// Mas preciso de ir buscar uma célula nova na mesma.
		// Faço isto se já tiver um destinoIntermédio atribuído
		// Se não juntar esta última guarda ele vai buscar a
		// célula
		// Que estaria a seguir ao avião que está a bloquear o
		// caminho e passaria por cima dele.
		if (pos.equals(prox)
				|| (!pos.equals(prox) && proximaCelula.isOccupied() && destinoIntermedio)) {

			proximaCelula = trajecto.pollFirst();
			if (proximaCelula != null) {
				prox = proximaCelula.getPos();
				// Gira o avião no sentido certo.
				rotateDirection();
			}
		}
	}

	private void rotateDirection() {
		if (prox.x > pos.x) {
			rotation = 90;
		} else if (prox.x < pos.x) {
			rotation = 270;
		} else if (prox.y > pos.y) {
			rotation = 180;
		} else {
			rotation = 0;
		}
	}

	private void moveInCirc() {
		rotation += 90;
	}

	public void setDestino(Point pointoDestino) {
		// Se não estou para destino intermédio, é o destino final, guarda-o.
		if (!destinoIntermedio) {
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

	public int getTimeToDestino() {
		if (trajecto != null) {
			return trajecto.size();
		}
		return 0;
	}

	public void setCurrentFuel(int fuel) {
		this.currentFuel = fuel;
	}

	public int getCurrentFuel() {
		return currentFuel;
	}

	public int getInitFuel() {
		return initFuel;
	}

	public void setInitFuel(int fuel) {
		this.initFuel = fuel;
	}

	public void abastece(int neededFuel) {
		this.initFuel = (int) ((double) (neededFuel * (double) (1 + RESERVA)) * CONSUMO);
		this.currentFuel = this.initFuel;
	}

	public boolean isWaitingCommand() {
		return waiting;
	}

	public void stopWaiting() {
		this.waiting = false;

	}

	public void setIntermedio(Point intermedio) {
		this.intermedio = intermedio;
		destinoIntermedio = true;
		controller.getPointCounter().addPoints(-1);
		setDestino(intermedio);

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

	public Point getIntermedio() {
		return intermedio;
	}

	public boolean isVisible() {
		return visible;
	}

}
