package SOS;

import javax.swing.SwingUtilities;

public class launcher_SOS {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
			mainWindow main = new mainWindow();
			main.show();
		}

	});
	}
}
		
