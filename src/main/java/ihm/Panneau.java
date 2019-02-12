package ihm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;


public class Panneau extends JPanel{
	public void paintComponent(Graphics g) {
		Font font = new Font("Arial", Font.ITALIC, 20);
		int x = this.getWidth()/4;
		int y = this.getHeight()/4;
		//g.fillRect(x, y, this.getWidth()/2, this.getHeight()/2); //draw --> plein, fill --> vide
		g.drawRect(20, 20, this.getWidth()/4, this.getHeight()/4);
		g.drawRoundRect(350, 350, this.getWidth()/4, this.getHeight()/4, 10, 10);
		g.drawLine(0, 0, this.getWidth(), this.getHeight());
		g.drawLine(0, this.getHeight(), this.getWidth(), 0);
		g.setFont(font);
		g.setColor(Color.BLUE);
		g.drawString("je suis fatiguée", 10, 20);
		this.setBackground(Color.yellow);
	}
}
