package rui.control;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class PointCounter extends JLabel{

	private int points=0;
	
	public PointCounter(){
		this.setText("0");
		setHorizontalAlignment(SwingConstants.RIGHT);
	}
	
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
