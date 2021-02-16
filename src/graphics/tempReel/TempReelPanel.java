package graphics.tempReel;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import captors.CaptorListener;
import captors.CaptorSocket;

public class TempReelPanel extends JPanel implements CaptorListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable tableTempReel;
	private CaptorTableModel model;
	private TreeSet<CaptorSocket> capteurs;
	
	public TempReelPanel() {	
		capteurs = new TreeSet<CaptorSocket>();
		model = new CaptorTableModel(capteurs);
		tableTempReel = new JTable(model);
		tableTempReel.setAutoCreateRowSorter(true);
		tableTempReel.setDefaultRenderer(Object.class, new RendererCell());
		JScrollPane scrollPane = new JScrollPane(tableTempReel);
		this.setLayout(new BorderLayout());
		this.add(scrollPane);
	}
	
	public void captorValueChanged(CaptorSocket c) {
		Iterator<CaptorSocket> it = capteurs.iterator();
		int indice = 0;
		while (it.hasNext() && it.next() != c) {
			indice++;
		}
		model.fireTableCellUpdated(indice, 3);
	}
	
	public void captorDisconnected(CaptorSocket c) {
		capteurs.remove(c);
		Iterator<CaptorSocket> it = capteurs.iterator();
		int indice = 0;
		while (it.hasNext() && it.next() != c) {
			indice++;
		}
		model.fireTableRowsDeleted(indice, indice);
	}
	
	public void captorConnected(CaptorSocket c) {
		capteurs.add(c);
		Iterator<CaptorSocket> it = capteurs.iterator();
		int indice = 0;
		while (it.hasNext() && it.next() != c) {
			indice++;
		}
		model.fireTableRowsInserted(indice, indice);
	}
	
	
	
	

}
