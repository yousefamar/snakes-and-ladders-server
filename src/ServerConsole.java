import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ServerConsole extends JPanel {

	JTextArea consoleText = new JTextArea();
	
	public ServerConsole() {
		super(new GridLayout());
		//setPreferredSize(new Dimension(800, 800));
		consoleText.setEditable(false);
        consoleText.setRows(17);
        consoleText.setColumns(33);
        add(new JScrollPane(consoleText));
	}

	public void println(String text) {
		consoleText.append(" > "+text+"\n");
		consoleText.setCaretPosition(consoleText.getDocument().getLength());
	}
}
