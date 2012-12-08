package rui.air;

import java.awt.Point;

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
	
	public Aircell(Point pos) {
		super();
		this.pos = pos;
		this.ocupada=false;
	}

	public Point getPos() {
		return pos;
	}

	@Override
	public String toString() {
		return new String("X= " + pos.x + " Y= " + pos.y);
	}

	public void setOcupante(Airplane airplane) {
		this.ocupante = airplane;
	}
	
	public AirThing getOcupante(){
		return this.ocupante;
	}

	//Mudar
	public void removeOcupante() {
		this.ocupante=null;
		//notifica?
		//altera bool para acesso
	}
	
	public boolean isOcupada(){
		return ocupada;
	}

	public void ocupa() {
		this.ocupada=true;
		
	}

	public void desocupa() {
		this.ocupada=false;
	}

}
