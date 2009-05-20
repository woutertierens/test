package demo.sheet.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jpropeller.sheet.ui.CardBorder;
import org.jpropeller.sheet.ui.CardIcons;
import org.jpropeller.sheet.ui.UnderlineBorder;
import org.jpropeller.util.GeneralUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class CardBorderDemo implements Runnable {

	/**
	 * Demonstrate {@link CardBorder}
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new CardBorderDemo());
	}

	public void run() {
		
		GeneralUtils.enableNimbus();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		FormLayout layout = new FormLayout("fill:pref:grow, 4px, pref", "fill:pref:grow, 4px, fill:pref:grow");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		
		builder.append(makeCard(), 3);
		builder.nextRow();
		builder.append(makeCard());
		builder.append(makeCard());
		
		JPanel panel = builder.getPanel();
		panel.setBackground(Color.DARK_GRAY);

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	private JPanel makeCard() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		JPanel title = makeTitle();
		panel.add(title, BorderLayout.NORTH);
		panel.add(makeContents(), BorderLayout.CENTER);
		panel.setBorder(new CardBorder());
		return panel;
	}
	
	private JPanel makeTitle() {
		JLabel label = new JLabel("<html><b><font color=#404feb>Title</font></b></html>");
		
		FormLayout layout = new FormLayout("4px, pref, fill:pref:grow, pref, 0px, pref, 4px");
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		builder.nextColumn();
		builder.append(label);
		builder.append(new JLabel(CardIcons.PENCIL));
		builder.append(new JLabel(CardIcons.CROSS));

		JPanel panel = builder.getPanel();
		panel.setOpaque(false);
		panel.setBorder(new UnderlineBorder());
		
		return panel;
	}
	
	private JPanel makeContents(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(4,4,4,4));
		
		TableModel model = new DefaultTableModel(10, 2);
		JTable table = new JTable(model);
		table.setBorder(null);
		table.setTableHeader(null);
		JScrollPane pane = new JScrollPane(table);
		pane.setBorder(null);
		
		//panel.add(pane);
		return panel;
	}
	
}
