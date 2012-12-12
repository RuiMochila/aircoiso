package rui.air;

import java.awt.Point;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A classe que representa as c�lulas no espa�o a�reo.
 * S�o o recurso partilhado pelos avi�es.
 * 
 * @author Rui
 *
 */
public class Aircell {
	/**
	 * A posi��o em que a c�lula se encontra
	 */
	private Point pos;

	/**
	 * Refer�ncia do objecto que ocupa actualmente a c�lula 
	 */
	private Airplane ocupante;
	private boolean ocupada;
	private ReentrantLock lock;
	private Airport aeroporto;
	
	public Aircell(Point pos) {
		super();
		this.pos = pos;
		this.ocupada=false;
		this.lock = new ReentrantLock();
	}

	public synchronized boolean tryOccupyCell(Airplane ocupante){
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

	public Airplane getOcupante(){
		return this.ocupante;
	}
	
	public boolean isOccupied(){
		return ocupada;
	}

	public Airport getAeroporto() {
		return aeroporto;
	}

	public void setAeroporto(Airport aeroporto) {
		this.aeroporto = aeroporto;
	}

}
