package demo.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropeller.ui.impl.PaneBottom;
import org.jpropeller.ui.impl.PaneTopSection;
import org.jpropeller.ui.impl.PaneTopToggleButton;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.ViewUtils;

/**
 * Demonstrate use of {@link PaneBottom} and {@link PaneTopSection}s to
 * build a titled pane
 */
public class PaneDemo {
	
	/**
	 * Demonstrate the border
	 * 
	 * @param args
	 *            Ignored
	 */
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				showDemo();
			}
		});
	}

	/**
	 * Show demonstration
	 */
	public static void showDemo() {
		
		GeneralUtils.enableNimbus();
		
		JFrame frame = new JFrame("Custom Buttons Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel();
		mainPanel.add(buildPanel(1, "Option 1", "Option 2", "Option 3", "Option 4"));
		mainPanel.add(buildPanel(0, "Single Contents Title"));
		mainPanel.add(buildPanel(0, "Option 1", "Option 2"));

		mainPanel.add(ViewUtils.groupPane(new JLabel("No Title on this one")));
		
		frame.add(mainPanel);
		
		frame.setSize(700, 85);
		frame.setVisible(true);
	}

	private static JPanel buildPanel(int selected, String... s) {
		
		JPanel top = new JPanel(new GridLayout(1, s.length, 0, 0));
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < s.length; i++) {
			PaneTopToggleButton toggleButton = new PaneTopToggleButton(s[i]);
			toggleButton.setDrawLeft(i==0);
			toggleButton.setDrawRight(i==s.length-1);
			top.add(toggleButton);
			group.add(toggleButton);
			if (i==0) {
				toggleButton.setSelected(true);
			}
		}
		
		JPanel center = new JPanel();
		center.setBorder(new PaneBottom());
		
		center.add(new JLabel("<html>Line 1<br />Line 2<br />Line 3<br /></html>"));
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(top, BorderLayout.NORTH);
		panel.add(center, BorderLayout.CENTER);
		
		return panel;
	}
}
