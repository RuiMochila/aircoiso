package rui.control;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class PointCounter extends JLabel{
	//pq e que esta classe esta no controlador? D
	//nao e suposto JLabel estar ligado ao swing? pq e que esta aqui no cntrolador? D

	private int points=0;
	
	public PointCounter(){
		this.setText("0");
		setHorizontalAlignment(SwingConstants.RIGHT);
	}
	
	//pq e que tem de ser sincronizado? D
 	//de onde e que vem os morePoints? D
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
