package rui.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;


import rui.control.GameController;

@SuppressWarnings("serial")
public class GameInterface extends JFrame{

	protected GameController controller; //pq Ž que Ž protected? pq est‡ nouto pacote? D
	//e protected para ser acessivel dentro da inner class sem recorrer a um getter
	private String interfaceTitle = "AirController";
	
	public GameInterface(final GameController controller) { //final?
		this.controller=controller;
		setTitle(interfaceTitle);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		//criar componente de jogo e darlhe o controller
		//introduzir componente de jogo
		//Outros componentes na janela
		setLayout(new BorderLayout());
		
		getContentPane().add(controller.getPointCounter(), BorderLayout.NORTH);
		getContentPane().add(new Gamefield(), BorderLayout.CENTER);
		JButton button = new JButton("Start");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton aux = (JButton) e.getSource();
				if(aux.getText()=="Start"){
					aux.setText("End");
					controller.initAirplanes();
				}else{
					controller.interruptGame();
					
				}
				
			}
		});
		getContentPane().add(button, BorderLayout.SOUTH);
		
		
		
		setVisible(true);
		pack();//let JFrame adapt to fit content.
	}
	
	
	private class Gamefield extends JComponent{ // esta classe e responsavel por fazer o que exactamente? D
		//esta classe representa o quadrado onde o jogo decorre. o que tem a grelha e 
		//a sentinela para tratar os clicks.
		//fica assim mais modular. Se tivesse dependente do resto da interface podias ás
		//tantas não conseguir ter a certeza de nada. Por exemplo, a coordenada 0,0 na interface
		//completa é diferente da 0,0 deste componente. Bastava adicionar na interface um cena 
		//por cima deste quadrado estragavate tudo. assim fica um objecto sólido
		AirplaneGraphic airplaneGraphics; 
		AirportGraphic airportGraphic;
		
		public Gamefield(){
			final double baseDim = GameController.cellBaseDim; // supostamente o cellBaseDim nao foi criado no controlador para ser visto por todo o lado? entao pq inicias aqui outra x? D
			//por nada em especial xD só para ficar mais pequeno no codigo aqui lol
			int cols = controller.getColsNum();
			int rows = controller.getRowsNum();
			airplaneGraphics = new AirplaneGraphic(controller);
			airportGraphic = new AirportGraphic(controller);
			
			
			setPreferredSize(new Dimension((int)(cols*baseDim), (int)(rows*baseDim))); 
			// pq e que multiplicas por baseDim? acho que ja percebi D 
			//por causa do que te expliquei. 3 colunas * 30 por cada uma fica 90 largura
			addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {				
				}
				public void mousePressed(MouseEvent e) {
					//Aqui não faço inteligência, 
					//apenas obtenho a coordenada na matriz e mando para o controler
					//ele saberá o que fazer.
					//mando para o controller um click(X, Y)
					//ele lá evoca o método que deve evocar.
					
					
					//Converte a coordenada em célula da matriz
					int x = (int) Math.floor(e.getX()/baseDim); // nao percebi mt bem o metodo floor nem pq divides por baseDim D
					int y = (int) Math.floor(e.getY()/baseDim);
					//pela mesma razao que expliquei anterior.
					//coordenada X=67, baseDim=30 -> 67/30 = 2,23
					//o floor é para arredondar para baixo, o ceil para arredondar para cima
					//entao dame coluna 2.
					
					//Evoca o método click de conveniência
					controller.click(new Point(x,y));

					
				}
				public void mouseExited(MouseEvent e) {
					
					
				}
				public void mouseEntered(MouseEvent e) {
					
					
				}
				public void mouseClicked(MouseEvent e) {	
				}
			});
		}
		
		
		
		@Override
		protected void paintComponent(Graphics g) { // pq e que umas sao protected outras private outras public? :s D
			//epah sinceramente acho que muito aqui pode ser private e ainda posso mudar
			//esta veio protected porque estou a fazer override do paintComponent 
			//que ja existe na classe JComponent, e la fizeram assim aqui tb tenho de fazer assim
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g; // este aqi nao podia ser ja Grafics2D? D
			//sim, simplesmente como o Graphics e mais comum que o 2D faco o cast ca dentro
			g.setColor(Color.gray);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			drawGrid(g2);
			
			airportGraphic.paintAll(g);
			airplaneGraphics.paintAll(g);
			
			
			
//			controller.paintOtherComponents(g);
		}
		
		private void drawGrid(Graphics2D g2){

			double baseDim = GameController.cellBaseDim;
			int cols = controller.getColsNum();
			int rows = controller.getRowsNum();
			
			// São usados estes mecanismos de impressão com double para ser mais
			// preciso.
			// Os arredondamentos e as margens provocam falhas na impressão
			// correcta.
			g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.black);

			// verticais
			for (int i = 1; i < cols; i++) {
				Line2D line = new Line2D.Double((double) i * baseDim, 0.0,
						(double) i * baseDim, (double) getHeight());
				g2.draw(line);
			}

			// horizontais
			for (int i = 1; i < rows; i++) {
				Line2D line = new Line2D.Double(0.0, (double) i * baseDim,
						(double) getWidth(), (double) i * baseDim);
				g2.draw(line);
			}
		}
	
		

	}
	
	

	
}
