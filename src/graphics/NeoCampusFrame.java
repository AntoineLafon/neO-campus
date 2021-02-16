package graphics;

import captors.*;
import graphics.tempReel.TempReelPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;


import bdd.*;

public class NeoCampusFrame extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane onglet = new JTabbedPane(JTabbedPane.TOP);

	
	private APosterioriPanel aPosterioriPanel;
	private ParametresPanel captorsPanel;
	private TempReelPanel tempReelPanel;


	public NeoCampusFrame(BaseDonnes bdd) {
		/* Main window initialisation */
		super("NeOCampus");
		this.setLocationRelativeTo(null);
		this.setSize(500, 500);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout(20, 20));
		this.setPreferredSize(new Dimension(600, 400));
		aPosterioriPanel = new APosterioriPanel(bdd);
		captorsPanel = new ParametresPanel(bdd);
		tempReelPanel = new TempReelPanel();

		/* TabbedPane initialisation */
		onglet.addTab("Temps reel", tempReelPanel);
		onglet.addTab("A posteriori", aPosterioriPanel);
		onglet.addTab("Parametres", captorsPanel);

		this.add(onglet);
		this.pack();
	}
	public void ecouter(CaptorSocket c) {
		c.addListeners(aPosterioriPanel);
		c.addListeners(tempReelPanel);
		c.addListeners(captorsPanel);
	}

	public int getPort(int base) {
		String result = (String) JOptionPane.showInputDialog(this, "Choisissez un port", "Port",
				JOptionPane.PLAIN_MESSAGE, null, null, String.valueOf(base));
		try {
			return Integer.parseInt(result);
		} catch (Exception e) {
			return getPort(base);
		}
	}

}
