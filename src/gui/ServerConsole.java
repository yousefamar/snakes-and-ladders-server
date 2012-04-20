package gui;
import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ServerConsole extends JPanel {

	private JTextArea consoleText = new JTextArea();
	
	/**
	 * Creates a JPanel to which text can be output similar to the actual console.
	 */
	public ServerConsole() {
		super(new GridLayout());
		//setPreferredSize(new Dimension(800, 800));
		consoleText.setEditable(false);
        consoleText.setRows(17);
        consoleText.setColumns(33);
        add(new JScrollPane(consoleText));
	}

	/**
	 * Appends a new line to the console text area.
	 * @param text
	 */
	public void println(String text) {
		consoleText.append(" > "+text+"\n");
		consoleText.setCaretPosition(consoleText.getDocument().getLength());
	}
}
