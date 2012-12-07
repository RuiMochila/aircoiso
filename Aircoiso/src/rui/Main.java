package rui;

import rui.control.GameController;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GameController controller = new GameController(15,20);
//		controller.createGameRandom(numAirports);
		
		controller.initUI();
		
		controller.createGameByFile();

		controller.initAirports();
		
		controller.updateUI();
		
	}

}
