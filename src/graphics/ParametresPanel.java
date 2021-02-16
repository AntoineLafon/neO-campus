package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import captors.CaptorListener;
import captors.CaptorSocket;
import bdd.BaseDonnes;
import captors.Captor;

public class ParametresPanel extends JPanel implements CaptorListener {
	private Captor capteur;
	private JLabel unit, bat, etage, lieu, type, nom;
	private JSpinner min, max;
	private static final long serialVersionUID = 1L;
	private JTree treecaptors;
	private JPanel dataPanel;
	private JSplitPane parametresPane;
	private JPanel cela = this;
	private BaseDonnes bdd;

	public ParametresPanel(BaseDonnes bdd) {
		parametresPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.setLayout(new BorderLayout());
		this.bdd = bdd;

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Capteurs");
		createNodes(top, bdd.recupListeCapteursComplete());

		treecaptors = new JTree(top);
		treecaptors.addTreeSelectionListener(new TreeSelectionListener());

		dataPanel = creerVue();

		JScrollPane treeView = new JScrollPane(treecaptors);

		parametresPane.add(treeView);
		parametresPane.add(dataPanel);

		this.add(parametresPane);
	}

	public boolean addCaptor(Captor capt) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treecaptors.getModel().getRoot();
		Enumeration<?> liste = root.depthFirstEnumeration();
		DefaultMutableTreeNode noeud = (DefaultMutableTreeNode) liste.nextElement();
		DefaultMutableTreeNode objectif;
		DefaultMutableTreeNode batimentEx = null;
		while (liste.hasMoreElements()) {
			noeud = (DefaultMutableTreeNode) liste.nextElement();
			if (noeud.isLeaf()) {
				Captor c = (Captor) noeud.getUserObject();
				if (c.getNom().equals(capt.getNom())) {
					System.out.println("Capteur " + capt.getNom() + " deja present");
					return true;
				}

				if (c.getBatiment().equals(capt.getBatiment())) {
					batimentEx = noeud;
				}
				if (batimentEx != null && c.getEtage() == capt.getEtage()) {
					objectif = (DefaultMutableTreeNode) noeud.getParent();
					DefaultMutableTreeNode capteur = new DefaultMutableTreeNode(c.getNom());
					capteur.setUserObject(capt);
					objectif.add(capteur);
					System.out.println("Capteur " + capt.getNom() + " Ajouté dans un étage");
					return false;
				}
			}
		}
		if (batimentEx != null) {
			DefaultMutableTreeNode batiment = (DefaultMutableTreeNode) batimentEx.getParent().getParent();
			DefaultMutableTreeNode etage = new DefaultMutableTreeNode("Etage : " + capt.getEtage());
			DefaultMutableTreeNode capteur = new DefaultMutableTreeNode(capt.getNom());
			capteur.setUserObject(capt);
			etage.add(capteur);
			batiment.add(etage);
			root.add(batiment);
			System.out.println("Capteur " + capt.getNom() + " ajouté dans un batiment");
			return false;
		}
		DefaultMutableTreeNode batiment = new DefaultMutableTreeNode(capt.getBatiment());
		DefaultMutableTreeNode etage = new DefaultMutableTreeNode("Etage : " + capt.getEtage());
		DefaultMutableTreeNode capteur = new DefaultMutableTreeNode(capt.getNom());
		capteur.setUserObject(capt);
		etage.add(capteur);
		batiment.add(etage);
		root.add(batiment);
		System.out.println("Capteur " + capt.getNom() + " ajouté à la fin de l'arbre");
		return false;
	}

	private void createNodes(DefaultMutableTreeNode top, TreeSet<Captor> listecapt) {
		DefaultMutableTreeNode batiment = null;
		DefaultMutableTreeNode etage = null;
		DefaultMutableTreeNode capt = null;
		boolean start = true;
		for (Captor c : listecapt) {
			if ((start) || (c.getEtage() != ((Captor) capt.getUserObject()).getEtage())) {
				if ((start) || (!c.getBatiment().equals(((Captor) capt.getUserObject()).getBatiment()))) {
					batiment = new DefaultMutableTreeNode(c.getBatiment());
					top.add(batiment);
					start = false;
				}
				etage = new DefaultMutableTreeNode("Etage : " + c.getEtage());
				batiment.add(etage);
			}
			System.out.println("added captor " + c.getNom() + " in tree");
			capt = new DefaultMutableTreeNode(c.getNom());
			capt.setUserObject(c);
			etage.add(capt);
		}
	}

	private JPanel creerVue() {
		JPanel top = new JPanel(new GridBagLayout());
		JPanel panel;
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10); // top padding
		panel = creerVueSeuil();
		// panel = new JPanel();
		// panel.setBackground(Color.red);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		top.add(panel, c);

		panel = localisation();
		// panel = new JPanel();
		// panel.setBackground(Color.blue);
		c.weighty = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		top.add(panel, c);

		panel = nomCapteur();
		// panel = new JPanel();
		// panel.setBackground(Color.green);
		c.weightx = 0.25;
		c.gridx = 0;
		c.gridy = 1;
		top.add(panel, c);

		panel = typeFluide();
		// panel = new JPanel();
		// panel.setBackground(Color.orange);
		c.ipady = 0; // reset to default
		c.anchor = GridBagConstraints.PAGE_END; // bottom of space
		c.weighty = 0.25;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		top.add(panel, c);

		panel = boutons();
		// panel = new JPanel();
		// panel.setBackground(Color.cyan);
		c.ipady = 0; // reset to default
		c.weighty = 0.25; // request any extra vertical space
		c.anchor = GridBagConstraints.PAGE_END; // bottom of space
		c.gridx = 1; // aligned with button 2
		c.gridwidth = 1; // 1 column wide
		c.gridy = 2; // third row
		top.add(panel, c);

		return top;
	}

	private JPanel localisation() {
		JPanel top = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;

		top.setBackground(Color.black);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 1, 1, 1); // top padding
		c.weighty = 0.25;
		c.weightx = 1.0;

		label = new JLabel("Localisation", SwingConstants.CENTER);
		label.setBackground(Color.white);
		label.setOpaque(true);
		c.gridx = 0;
		c.gridy = 0;
		top.add(label, c);

		bat = new JLabel("Batiment :   --", SwingConstants.CENTER);
		bat.setBackground(Color.lightGray);
		bat.setOpaque(true);
		c.insets.bottom = 0;
		c.gridx = 0;
		c.gridy = 1;
		top.add(bat, c);

		etage = new JLabel("Etage :    --", SwingConstants.CENTER);
		etage.setBackground(Color.lightGray);
		etage.setOpaque(true);
		c.insets.top = 0;
		c.gridx = 0;
		c.gridy = 2;
		top.add(etage, c);

		lieu = new JLabel("Lieu :    --", SwingConstants.CENTER);
		lieu.setBackground(Color.lightGray);
		lieu.setOpaque(true);
		c.insets.bottom = 1;
		c.gridx = 0;
		c.gridy = 3;
		top.add(lieu, c);

		return top;
	}

	private JPanel nomCapteur() {
		JPanel top = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;

		top.setBackground(Color.black);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 1, 1, 1); // top padding
		c.weighty = 0.5;
		c.weightx = 1.0;

		label = new JLabel("Nom du capteur", SwingConstants.CENTER);
		label.setBackground(Color.white);
		label.setOpaque(true);
		c.gridx = 0;
		c.gridy = 0;
		top.add(label, c);

		nom = new JLabel("---", SwingConstants.CENTER);
		nom.setBackground(Color.lightGray);
		nom.setOpaque(true);
		c.gridx = 0;
		c.gridy = 1;
		top.add(nom, c);

		return top;
	}

	private JPanel typeFluide() {
		JPanel top = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;

		top.setBackground(Color.black);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 1, 1, 1); // top padding
		c.weighty = 0.5;
		c.weightx = 1.0;

		label = new JLabel("Type de capteur", SwingConstants.CENTER);
		label.setBackground(Color.white);
		label.setOpaque(true);
		c.gridx = 0;
		c.gridy = 0;
		top.add(label, c);

		type = new JLabel("---", SwingConstants.CENTER);
		type.setBackground(Color.lightGray);
		type.setOpaque(true);
		c.gridx = 0;
		c.gridy = 1;
		top.add(type, c);

		return top;
	}

	private JPanel creerVueSeuil() {
		JPanel top = new JPanel(new GridBagLayout());
		SpinnerNumberModel model;
		Float step = (float) 0.1;
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;

		top.setBackground(Color.black);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 1, 1, 1); // top padding
		c.weighty = 0.5;
		c.weightx = 0.33;

		label = new JLabel("Unité", SwingConstants.CENTER);
		label.setBackground(Color.lightGray);
		label.setOpaque(true);
		c.gridx = 0;
		c.gridy = 0;
		top.add(label, c);

		label = new JLabel("Seuil minimum", SwingConstants.CENTER);
		label.setBackground(Color.lightGray);
		label.setOpaque(true);
		c.gridx = 1;
		c.gridy = 0;
		top.add(label, c);

		label = new JLabel("Seuil maximum", SwingConstants.CENTER);
		label.setBackground(Color.lightGray);
		label.setOpaque(true);
		c.gridx = 2;
		c.gridy = 0;
		top.add(label, c);

		unit = new JLabel("--", SwingConstants.CENTER);
		unit.setBackground(Color.lightGray);
		unit.setOpaque(true);
		c.gridx = 0;
		c.gridy = 1;
		top.add(unit, c);

		model = new SpinnerNumberModel((float) 0.0, null, null, step);
		min = new JSpinner(model);
		min.setBackground(Color.lightGray);
		min.setOpaque(true);
		c.gridx = 1;
		c.gridy = 1;
		top.add(min, c);

		model = new SpinnerNumberModel((float) 0.0, null, null, step);
		max = new JSpinner(model);
		max.setBackground(Color.lightGray);
		max.setOpaque(true);
		c.gridx = 2;
		c.gridy = 1;
		top.add(max, c);

		return top;
	}

	private JPanel boutons() {
		JPanel top = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JButton ok, reset, annuler;

		top.setBackground(Color.black);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 1, 1, 1); // top padding
		c.weighty = 0.5;
		c.weightx = 1.0;

		ok = new JButton("Appliquer");
		ok.setBackground(Color.green);
		ok.setOpaque(true);
		c.gridx = 0;
		c.gridy = 0;
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// display/center the jdialog when the button is pressed
				if ((float) min.getValue() > (float) max.getValue()) {
					JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(cela);
					JDialog d = new JDialog(frame, "Ces valeurs sont interdites");
					d.setLocationRelativeTo(frame);
					d.setVisible(true);
				} else {
					capteur.setMin((float) min.getValue());
					capteur.setMax((float) max.getValue());
					bdd.writeMinMax(capteur);
				}
			}
		});
		top.add(ok, c);

		annuler = new JButton("Annuler");
		annuler.setBackground(Color.red);
		annuler.setOpaque(true);
		c.gridx = 1;
		c.gridy = 0;
		annuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				min.setValue(capteur.getMin());
				max.setValue(capteur.getMax());
			}
		});
		top.add(annuler, c);

		reset = new JButton("Par défaut");
		reset.setBackground(Color.lightGray);
		reset.setOpaque(true);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				capteur.setMin(capteur.getType().getDefaultMin());
				capteur.setMax(capteur.getType().getDefaultMax());
				min.setValue(capteur.getMin());
				max.setValue(capteur.getMax());
			}
		});
		top.add(reset, c);

		return top;
	}

	private class TreeSelectionListener implements javax.swing.event.TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			JTree tree = (JTree) e.getSource();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			/* if nothing is selected */
			if (node == null)
				return;

			/* retrieve the node that was selected */
			Object nodeInfo = node.getUserObject();
			/* React to the node selection. */
			if (nodeInfo instanceof Captor) {
				capteur = (Captor) nodeInfo;
				System.out.println("Select : " + capteur.getNom());
				unit.setText(capteur.getType().getUnity());
				bat.setText("Batiment : " + capteur.getBatiment());
				etage.setText("Etage : " + capteur.getEtage());
				lieu.setText("Lieu : " + capteur.getLieu());
				type.setText(capteur.getType().toString());
				nom.setText(capteur.getNom());
				min.setValue(capteur.getMin());
				max.setValue(capteur.getMax());
			}
		}
	}

	public void captorConnected(CaptorSocket c) {
		addCaptor(c);
		DefaultTreeModel model = (DefaultTreeModel) treecaptors.getModel();
		model.reload();
	}

}
