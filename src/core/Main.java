package core;

import gui.ServerGUI;
import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Snakes And Ladders Server");
      	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      	/* Add ServerGUI panel to frame (the frame's contentPane functionality isn't needed).
      	 * All subsequent code is triggered within the GUI. */
		frame.add(new ServerGUI());
  		frame.pack();
  		frame.setResizable(false);
  		frame.setLocationRelativeTo(null);
      	frame.setVisible(true);
	}
}
