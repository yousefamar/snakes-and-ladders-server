
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SnakesAndLaddersGUI extends JPanel implements Runnable {

	public static final Color[] playerCols = { Color.BLUE, Color.GREEN, Color.RED, Color.ORANGE, Color.YELLOW };
	
	private int[] playerPoss = new int[5];
	private final double[] shiftx = { 0.3D, 0.3D, -0.3D, -0.3D, 0.0D };
	private final double[] shifty = { 0.3D, -0.3D, 0.3D, -0.3D, 0.0D };
	private Image background = null;
	private int noofplayers = 0;

	public SnakesAndLaddersGUI() {
		background = new ImageIcon(getClass().getResource("board.gif")).getImage();
	}

	public void setNumberOfPlayers(int num) {
		noofplayers = num;
		syncRequestRepaint();
	}

	public void setPosition(int playerIndex, int square) {
		if ((playerIndex >= 0) && (playerIndex <= noofplayers)) {
			playerPoss[playerIndex] = square;
			syncRequestRepaint();
		} else {
			System.out.println("Error: Trying to access player number "	+ playerIndex + " which doesn't exist\n");
		}
	}

	public void paint(Graphics gfx) {
		int width = getWidth();
		int height = getHeight();
		if (width < 1)
			width = 350;
		if (height < 1)
			height = 350;
		if (width > 2000)
			width = 350;
		if (height > 2000)
			height = 350;

		gfx.drawImage(background, 0, 0, width, height, null);
		double squareWidth = width / 10.0D;
		double squareHeight = height / 10.0D;

		for (int playerIndex = 0; playerIndex < noofplayers; playerIndex++) {
			int playerPos = playerPoss[playerIndex];
			if ((playerPos < 1) || (playerPos > 100))
				continue;
			int row = (playerPos - 1) % 10;
			int col = (playerPos - 1) / 10;

			if (col / 2 * 2 != col)
				row = 9 - row;

			row = (int) ((row + 0.25D) * squareWidth);
			col = height - (int) ((col + 0.75D) * squareHeight);

			gfx.setColor(playerCols[playerIndex]);
			gfx.fillOval(row + (int) (shiftx[playerIndex] * squareWidth / 2.0D), col + (int) (shifty[playerIndex] * squareHeight / 2.0D), (int) (squareWidth / 2.2D + 1.0D), (int) (squareHeight / 2.2D + 1.0D));
			gfx.setColor(Color.BLACK);
			gfx.drawOval(row + (int) (shiftx[playerIndex] * squareWidth / 2.0D), col + (int) (shifty[playerIndex] * squareHeight / 2.0D), (int) (squareWidth / 2.2D + 1.0D), (int) (squareHeight / 2.2D + 1.0D));
		}
	}

	public void run() {
		repaint();
	}

	private void syncRequestRepaint() {
		try {
			EventQueue.invokeAndWait(this);
		} catch (Exception e) {
		}
	}
}