package demo.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.Bean;
import org.jpropeller.reference.impl.ReferenceToChangeable;
import org.jpropeller.ui.impl.ImmutableIcon;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.bean.impl.BeanEditor;

import test.example.LotsOfProps;

/**
 * Demonstration of {@link BeanEditor}
 */
public class BeanViewDemo {

	/**
	 * Demonstrate
	 * 
	 * @param args		ignored
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				//GeneralUtils.enableConsoleLogging(UpdateManagerDefault.class, SwingUpdateDispatcher.class);//, BeanPropListEditor.class, NumberSpinnerEditor.class);
				GeneralUtils.enableNimbus();
				
		        //Locale.setDefault(Locale.FRANCE);
		        
				final LotsOfProps a = new LotsOfProps();
				
				/*
				a.props().addListener(new PropListener() {
					@Override
					public <T> void propChanged(PropEvent<T> event) {
						System.out.println(event);
					}
				});
				*/

				//Make a ref to "a"
				final ReferenceToChangeable<Bean> model = ReferenceToChangeable.create(Bean.class, a);
				
				final BeanEditor<Bean> editor = BeanEditor.create(model);
				
				final JFrame frame = new JFrame("Bean View Demo");
				frame.setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				frame.add(editor.getComponent(), BorderLayout.CENTER);

				JPanel panel = new JPanel(new FlowLayout());

				JButton changeImage = new JButton("change current image");
				changeImage.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						LotsOfProps current = (LotsOfProps) model.value().get();
						
						JFileChooser chooser = new JFileChooser();
						chooser.setDialogTitle("Choose an image to set");
						if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
							try {
								Image image = ImageIO.read(chooser.getSelectedFile());
								current.imageProp().set(ImmutableIcon.fromImage(image));
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(frame, "Can't load image:\n" + e1.getMessage());
							}
						}
						
					}
				});
				panel.add(changeImage);

				JButton newBean = new JButton("new bean");
				newBean.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						model.value().set(new LotsOfProps());
					}
				});
				panel.add(newBean);

				JButton print = new JButton("print original");
				print.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(a);
					}
				});
				panel.add(print);

				JButton randomChange = new JButton("randomChange original");
				randomChange.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						a.stringProp().set("Random String " + Math.random()*100);
						a.intProp().set((int)(Math.random()*100));
						a.longProp().set((long)(Math.random()*100));
						a.floatProp().set((float)Math.random()*100);
						a.doubleProp().set((double)Math.random()*100);
					}
				});
				panel.add(randomChange);

				JButton printCurrent = new JButton("print current");
				printCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(model.value().get());
					}
				});
				panel.add(printCurrent);

				JButton randomChangeCurrent = new JButton("randomChange current");
				randomChangeCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						LotsOfProps current = (LotsOfProps) model.value().get();
						current.stringProp().set("Random String " + Math.random()*100);
						current.intProp().set((int)(Math.random()*100));
						current.longProp().set((long)(Math.random()*100));
						current.floatProp().set((float)Math.random()*100);
						current.doubleProp().set((double)Math.random()*100);
					}
				});
				panel.add(randomChangeCurrent);
				
				JButton manyRandomChangesCurrent = new JButton("many random changes to current");
				manyRandomChangesCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
						
						LotsOfProps current = (LotsOfProps) model.value().get();
						long startTime = System.currentTimeMillis();
						int repeats = 15360;
						for (int i = 0; i < repeats; i++) {
							current.stringProp().set("Random String " + Math.random()*100);
							current.intProp().set((int)(Math.random()*100));
							current.longProp().set((long)(Math.random()*100));
							current.floatProp().set((float)Math.random()*100);
							current.doubleProp().set((double)Math.random()*100);
						}
						
						long endTime = System.currentTimeMillis();
						double time = (endTime-startTime);
						System.out.println(repeats + " updates took " + time + "ms");
						System.out.println(time/((double)repeats) + " ms per update");
					}
				});
				panel.add(manyRandomChangesCurrent);

				
				frame.add(panel, BorderLayout.SOUTH);
				
				frame.pack();
				frame.setVisible(true);
			}
		});
		
	}
	
}
