package rui.control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class Game extends JFrame {

	private static final String interfaceTitle = "AirController";

	public Game() {
		setTitle(interfaceTitle);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 400);
		setResizable(false);

		setLayout(new FlowLayout());

		JButton buttonFile = new JButton("GameByFile");
		buttonFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameController controller = new GameController();

				controller.createGameByFile();

				controller.initUI();
			}
		});

		JButton buttonRandom = new JButton("RandomGame");
		buttonRandom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int x = Integer.parseInt(JOptionPane.showInputDialog("Insert Number of Columns"));
				int y = Integer.parseInt(JOptionPane.showInputDialog("Insert Number of Lines"));
				int airports = Integer.parseInt(JOptionPane.showInputDialog("Insert Number of Airports"));

				GameController controller = new GameController();

				controller.createGameRandom(x, y, airports);

				controller.initUI();

			}
		});

		JButton buttonTest = new JButton("TestGame");
		buttonTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameController controller = new GameController();

				controller.createGameTest();

				controller.initUI();
			}
		});
		getContentPane().add(buttonFile);
		getContentPane().add(buttonRandom);
		getContentPane().add(buttonTest);

		setVisible(true);
//		pack();// let JFrame adapt to fit content.

	}

}
