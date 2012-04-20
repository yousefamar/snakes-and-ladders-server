package gui;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

import javax.swing.*;

import core.GameServer;

@SuppressWarnings("serial")
public class ServerGUI extends JPanel implements ActionListener {

	private JFormattedTextField portField;
	private JSlider playerNumSlider;
	private JButton startButton;
	
	public ServerGUI() {
		super(new GridBagLayout());

		/* Create and add title. */
		JLabel title = new JLabel(" Snakes And Ladders Server ", JLabel.CENTER);
		title.setFont(new Font("MV Boli", Font.BOLD, 25));
		GridBagConstraints cons0 = new GridBagConstraints();
		cons0.gridy = 0;
		cons0.insets = new Insets(6, 0, 0, 0);
		add(title, cons0);
		
		/* Create and add subtitle. */
		JLabel subtitle = new JLabel("(C) Yousef Amar 2012");
		subtitle.setFont(new Font("MV Boli", Font.PLAIN, 10));
		GridBagConstraints cons1 = new GridBagConstraints();
		cons1.gridy = 1;
		cons1.anchor = GridBagConstraints.NORTHEAST;
		cons1.insets = new Insets(0, 0, 25, 40);
		add(subtitle, cons1);
		
		/* Create and add port input. */
		JPanel portPanel = new JPanel();
  		portPanel.add(new JLabel("Port:"));
  		portPanel.add(new JPanel());
  		NumberFormat format = NumberFormat.getIntegerInstance();
  		format.setGroupingUsed(false);
  		portField = new JFormattedTextField(format);
  		portField.setPreferredSize(new Dimension(45, 25));
  		portField.setValue(8250);
  		portPanel.add(portField);
  		GridBagConstraints cons2 = new GridBagConstraints();
		cons2.gridy = 2;
  		add(portPanel, cons2);
  		
  		/* Create and add player number slider. */
  		JPanel sliderSettingsPanel = new JPanel(new GridLayout(0, 1));
  		sliderSettingsPanel.add(new JLabel("Number of Players", JLabel.CENTER));
  		playerNumSlider = new JSlider(JSlider.HORIZONTAL, 2, 5, 3);
  		playerNumSlider.setSnapToTicks(true);
  		playerNumSlider.setMajorTickSpacing(1);
  		playerNumSlider.setPaintTicks(true);
  		playerNumSlider.setPaintLabels(true);
  		/* Create a sub-panel that uses FlowLayout to fix size issues. */
  		JPanel sliderPanel = new JPanel();
  		sliderPanel.add(playerNumSlider);
  		sliderSettingsPanel.add(sliderPanel);
  		GridBagConstraints cons3 = new GridBagConstraints();
		cons3.gridy = 3;
  		add(sliderSettingsPanel, cons3);

  		/* Create and add start button. */
  		startButton = new JButton("Start");
  		startButton.addActionListener(this);
  		/* Create a sub-panel that uses FlowLayout to fix size issues. */
  		JPanel buttonPanel = new JPanel();
  		buttonPanel.add(startButton);
  		GridBagConstraints cons4 = new GridBagConstraints();
		cons4.gridy = 4;
		cons4.insets = new Insets(10, 0, 5, 0);
  		add(buttonPanel, cons4);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == startButton) {
			ServerConsole serverConsole = new ServerConsole();
			/* Clear GUI and add the server console panel instead. */
			removeAll();
			add(serverConsole);
			validate();

			/* Start the server in a new thread (doesn't interfere with GUI rendering).
	  		 * All subsequent code is called from this thread. */
			serverConsole.println("Starting Snakes and Ladders Server.");
			new Thread(new GameServer((Integer)portField.getValue(), playerNumSlider.getValue(), serverConsole), "Main Server Thread").start();
		}
	}
}
