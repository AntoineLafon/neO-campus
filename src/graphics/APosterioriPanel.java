package graphics;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import bdd.BaseDonnes;
import bdd.Valeur;
import captors.Captor;
import captors.CaptorListener;
import captors.CaptorSocket;
import captors.TypeMesure;

public class APosterioriPanel extends JPanel implements ActionListener, ItemListener, CaptorListener {

	private static final long serialVersionUID = 1L;

	private BaseDonnes bdd;
	private HashMap<JCheckBox, Captor> dictionnaireCheckboxCapteur;
	private Set<JCheckBox> capteursSelectionnes;

	private Timestamp debut;
	private Timestamp fin;

	private JPanel listeCapteursPanel;
	private JTextField dateDebut;
	private JTextField dateFin;
	private JButton boutonValider;
	private JComboBox<Object> valueComboBox;
	private ChartPanel graph;

	@SuppressWarnings("deprecation")
	public APosterioriPanel(BaseDonnes bdd) {

		// initialisation variables
		this.bdd = bdd;
		capteursSelectionnes = new HashSet<JCheckBox>();
		dictionnaireCheckboxCapteur = new HashMap<>();
		for (Captor c : bdd.recupListeCapteursComplete()) {
			ajouterCapteur(c);
		}

		dateDebut = new JTextField();
		dateFin = new JTextField();

		graph = new ChartPanel(null);

		this.setLayout(new BorderLayout());

		/** partie gauche **/
		JSplitPane gauche = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		gauche.enable(false);
		listeCapteursPanel = new JPanel();
		listeCapteursPanel.setLayout(new BoxLayout(listeCapteursPanel, BoxLayout.Y_AXIS));
		valueComboBox = new JComboBox<Object>(TypeMesure.values());
		valueComboBox.addActionListener(this);
		JScrollPane s = new JScrollPane(listeCapteursPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		gauche.setBottomComponent(s);
		gauche.setTopComponent(valueComboBox);

		/** partie droite **/
		boutonValider = new JButton("Valider");
		boutonValider.addActionListener(this);
		JSplitPane droite = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		droite.enable(false);
		droite.setResizeWeight(1);
		JPanel basDroite = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.05;
		basDroite.add(new JLabel("   debut: "), c);
		c.gridx = 1;
		c.weightx = 0.45;
		basDroite.add(dateDebut, c);
		c.gridx = 2;
		c.weightx = 0.05;
		basDroite.add(new JLabel("   fin: "), c);
		c.gridx = 3;
		c.weightx = 0.45;
		basDroite.add(dateFin, c);
		c.gridwidth = 4;
		c.gridy = 1;
		c.gridx = 0;
		c.weightx = 1;
		basDroite.add(boutonValider, c);

		droite.setTopComponent(graph);
		droite.setBottomComponent(basDroite);

		/** panneau principal **/
		JSplitPane aPosterioriPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		aPosterioriPane.setResizeWeight(0.1);
		aPosterioriPane.setLeftComponent(gauche);
		aPosterioriPane.setRightComponent(droite);
		aPosterioriPane.enable(false);
		this.add(aPosterioriPane);

		afficherListeCapteur(TypeMesure.WATER);

	}

	public void afficherListeCapteur(TypeMesure type) {
		listeCapteursPanel.removeAll();
		for (JCheckBox j : dictionnaireCheckboxCapteur.keySet()) {
			Captor c = dictionnaireCheckboxCapteur.get(j);
			if (c.getType() == type) {
				listeCapteursPanel.add(j);
			}
		}
		listeCapteursPanel.setVisible(false);
		listeCapteursPanel.setVisible(true);
	}

	public void ajouterCapteur(Captor c) {
		JCheckBox checkBox = new JCheckBox(c.getNom());
		checkBox.addItemListener(this);
		dictionnaireCheckboxCapteur.put(checkBox, c);
	}

	public void setButtonEnable(boolean bool) {
		for (JCheckBox j : dictionnaireCheckboxCapteur.keySet()) {
			if (!j.isSelected()) {
				j.setEnabled(bool);
			}
		}
	}

	public void deselectJCheckBox() {
		for (JCheckBox j : dictionnaireCheckboxCapteur.keySet()) {
			if (j.isSelected()) {
				j.setSelected(false);
			}
		}
	}

	public void updateDataSet() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		boolean trouve = false;
		for (JCheckBox j : capteursSelectionnes) {
			Captor c = dictionnaireCheckboxCapteur.get(j);
			LinkedList<Valeur> val = bdd.vueCapteur(c, debut, fin);
			if (val.size() > 0)
				trouve = true;
			for (Valeur v : val) {
				System.out.print(v.getValeur());
				if (v.getValeur() > max)
					max = v.getValeur();
				else if (v.getValeur() < min)
					min = v.getValeur();
				dataset.setValue(v.getValeur(), c.getNom(), v.getTemps());
			}
		}

		if (trouve) {
			JFreeChart chart = ChartFactory.createLineChart("Capteurs", "Temps", "Valeurs", dataset,
					PlotOrientation.VERTICAL, true, true, false);

			System.out.println(min + " " + max);
			chart.getCategoryPlot().getRangeAxis().setRange(min - 1, max + 1);
			graph.setChart(chart);
		} else {
			messageErreur("Aucune données trouvées pour les capteur");
		}

	}

	private void messageErreur(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.WARNING_MESSAGE);
	}

	/*******************  Listeners ***********************/
	@Override
	public void actionPerformed(ActionEvent e) {

		// combobox
		if (e.getSource() == valueComboBox) {
			deselectJCheckBox();
			afficherListeCapteur((TypeMesure) valueComboBox.getSelectedItem());
		}

		// bouton
		if (e.getSource() == boutonValider && capteursSelectionnes.size() > 0) {
			try {
				debut = Timestamp.valueOf(dateDebut.getText());
				fin = Timestamp.valueOf(dateFin.getText());
				updateDataSet();
			} catch (Exception f) {
				messageErreur(f.getMessage());
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		//checkbox
		int state = e.getStateChange();
		if (state == ItemEvent.SELECTED) {
			capteursSelectionnes.add((JCheckBox) e.getSource());
		} else if (state == ItemEvent.DESELECTED) {
			capteursSelectionnes.remove((JCheckBox) e.getSource());
		}

		if (state == ItemEvent.DESELECTED && capteursSelectionnes.size() == 2) {
			setButtonEnable(true);
		} else if (capteursSelectionnes.size() == 3) {
			setButtonEnable(false);
		}
	}

	public void captorConnected(CaptorSocket c) {
		boolean existe = false;
		for (JCheckBox jc : dictionnaireCheckboxCapteur.keySet()) {
			Captor cpt = dictionnaireCheckboxCapteur.get(jc);
			if (cpt.getNom().equals(c.getNom()))
				existe = true;
		}

		if (!existe) {
			ajouterCapteur(c);
			if(((TypeMesure)valueComboBox.getSelectedItem()).equals(c.getType())) {
				afficherListeCapteur((TypeMesure) valueComboBox.getSelectedItem());
			}
		}
	}

}
