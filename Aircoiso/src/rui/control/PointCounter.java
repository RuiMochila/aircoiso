package rui.control;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class PointCounter extends JLabel{
	//pq e que esta classe esta no controlador? D
	//nao e suposto JLabel estar ligado ao swing? pq e que esta aqui no cntrolador? D

	//esta � a classe para gerir os pontos que vais ganhando no jogo
	//� um jlabel e como tal a swing agarra nele e mete no ecr�
	//mas os avi�es tb concorrem a ele, e acedem a ele a partir do controlador
	//que todos eles conhecem
	private int points=0;
	
	public PointCounter(){
		this.setText("0");
		setHorizontalAlignment(SwingConstants.RIGHT);
	}
	
	//pq e que tem de ser sincronizado? D
 	//de onde e que vem os morePoints? D
	
	//v�m dos avi�es todos. � sincronizado para n�o haver conflitos
	//ao adicionar pontos, tipo 2 avi�es a adicionar pontos e alguns perdem-se
	//o que j� vimos acontecer em tua casa com exemplos de aula =)
	//ponto b do enunciado, ponto numero 4
	public synchronized void addPoints(int morePoints){
		this.points+=morePoints;
		this.setText(""+points);
	}
	
	public synchronized void removePoints(int lessPoints){
		this.points-=lessPoints;
		this.setText(""+points);
	}
	
	public int getPoints(){
		return points;
	}

}
