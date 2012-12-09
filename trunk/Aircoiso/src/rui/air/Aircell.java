package rui.air;

import java.awt.Point;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A classe que representa as células no espaço aéreo.
 * São o recurso partilhado pelos aviões.
 * 
 * @author Rui
 *
 */
public class Aircell {
	/**
	 * A posição em que a célula se encontra
	 */
	private Point pos;
	//Alterar para receber também os aeroportos
	/**
	 * Referência do objecto que ocupa actualmente a célula 
	 */
	private AirThing ocupante;
	private boolean ocupada;
	private ReentrantLock lock;
	
	public Aircell(Point pos) {
		super();
		this.pos = pos;
		this.ocupada=false;
		this.lock = new ReentrantLock();
	}

	public synchronized boolean tryOcupyCell(AirThing ocupante){
		if(lock.tryLock()){
			this.ocupante=ocupante;
			ocupada=true;
			return true;
		}else{
			return false;
		}
	}
	
	public synchronized void leaveCell(){
		if(lock.isHeldByCurrentThread()){
			lock.unlock();
			ocupada = false;
			ocupante=null;
		}
	}
	
	public Point getPos() {
		return pos;
	}

	@Override
	public String toString() {
		return new String("X= " + pos.x + " Y= " + pos.y);
	}

	public AirThing getOcupante(){
		return this.ocupante;
	}
	
	public boolean isOccupied(){
		return ocupada;
	}

}
