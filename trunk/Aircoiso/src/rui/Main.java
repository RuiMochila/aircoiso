package rui;

import rui.control.GameController;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GameController controller = new GameController();
		
//		controller.createGameByFile();
		
		controller.createGameRandom(15, 15, 4);
		
		controller.initUI();
		
		controller.initAirports();
		
		controller.updateUI();
		
	}

}
