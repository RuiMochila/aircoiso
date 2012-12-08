package rui.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import rui.control.GameController;

import rui.air.Airplane;


public class AirplaneGraphic {

	private LinkedList<Airplane> airplanes;
	
	public AirplaneGraphic(GameController controller){
		this.airplanes = controller.getAirplanes();
	}
	
	/**
	 * Method to paint all the airplanes in the a Graphics object
	 *
	 * The method iterates over the list of airplanes e calls paint upon
	 * <p>
	 * each one in order to make a specialized paint.
	 * 
	 * @param  Graphics g : the graphics object to paint to.
	 * @return sdnklnsd  
	 */
	public void paintAll(Graphics g){
		synchronized (airplanes) {
			
			for (Airplane airplane : airplanes) {
				paint(g, airplane);
			}
		}
	}
	
	/**
	 * 
	 * @param g
	 * @param airplane
	 */
	public void paint(Graphics g, Airplane airplane) {

		Graphics2D g2 = (Graphics2D) g;
		BufferedImage image;
		Point pos = airplane.getPos();
		double baseDim = GameController.cellBaseDim;
		
		
		try {
//			if (airplane.getCurrentFuel()< airplane.getInitFuel()* Airplane.RESERVA) {
//				image = ImageIO.read(new File("images/aviao_vermelho.png"));
//			} else {
//				image = ImageIO.read(new File("images/aviao.png"));
//			}
			image = ImageIO.read(new File("images/aviao.png"));
			double locationX = image.getWidth() / 2;
			double locationY = image.getHeight() / 2;
			AffineTransform tx = AffineTransform.getRotateInstance(
					Math.toRadians(airplane.getRotation()), locationX, locationY);
			AffineTransformOp op = new AffineTransformOp(tx,
					AffineTransformOp.TYPE_BILINEAR);
			// Drawing the rotated image at the required drawing locations
			g2.drawImage(op.filter(image, null), (int) (pos.x * baseDim) + 2,
					(int) (pos.y * baseDim) + 1, null);
			// bem centrado com x+2, y+1

			if (airplane.isWaitingCommand()) {
				// Desenhar símbolo de pausa
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.yellow);
				Line2D line1 = new Line2D.Double((pos.x * baseDim) + 3,
						(pos.y * baseDim) + 3, (pos.x * baseDim) + 3,
						(pos.y * baseDim) + 7);
				Line2D line2 = new Line2D.Double((pos.x * baseDim) + 7,
						(pos.y * baseDim) + 3, (pos.x * baseDim) + 7,
						(pos.y * baseDim) + 7);
				g2.draw(line1);
				g2.draw(line2);
			}

			if (airplane.isHeadingDestinoIntermedio()) {
				// desenhar algo indicativo
				// (int)(posX * baseDim) + 2,(int) (posY * baseDim) + 1
				g2.setColor(Color.green);
				int X = (int) (pos.x * baseDim + baseDim) - 2;
				int Y = (int) (pos.y * baseDim + baseDim) - 1;
				int[] xPoints = { X, X - 4, X, X - 4 };
				int[] yPoints = { Y, Y, Y - 4, Y - 4 };
				int nPoints = xPoints.length;
				g2.drawPolygon(xPoints, yPoints, nPoints);
				
				BufferedImage flag = ImageIO.read(new File("images/flag.png"));
				g2.drawImage(flag, (int)(airplane.getIntermedio().x*baseDim+1), (int)(airplane.getIntermedio().y*baseDim)+1, (int)(baseDim-1), (int)(baseDim-1), null);
				
			}

			drawFuelBars(g, airplane);
		} catch (IOException ex) {
		}

	}
	
	/**
	 * 
	 * @param g
	 * @param airplane
	 */
	public void drawFuelBars(Graphics g, Airplane airplane) {
		Point pos = airplane.getPos();
		double baseDim = GameController.cellBaseDim;
		
		int x = (int) ((baseDim* pos.x)+4);
		int y = (int) ((baseDim * pos.y)-2);
		int comprimentoBase = (int) (baseDim - 5); 
		int comprimento = (int)(comprimentoBase * (double)((double)airplane.getCurrentFuel()/(double)airplane.getInitFuel()));
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		if(comprimento<(0.2*comprimentoBase)){
			g2.setColor(Color.red);
		}else if(comprimento<(0.6*comprimentoBase)){
			g2.setColor(Color.yellow);
		}else{
			g2.setColor(Color.green);
		}
		
		Line2D line2 = new Line2D.Double(x, y+baseDim, x + comprimento, y+baseDim);
		g2.draw(line2);

		g2.setColor(Color.black);
	}
}
