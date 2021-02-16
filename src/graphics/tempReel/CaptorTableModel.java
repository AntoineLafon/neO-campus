package graphics.tempReel;
import captors.*;

import java.util.TreeSet;

import javax.swing.table.AbstractTableModel;

public class CaptorTableModel extends AbstractTableModel{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private static TreeSet<CaptorSocket> captors;
	private String[] headers = {"Nom","Type","Localisation","Valeur"};	
	public CaptorTableModel(TreeSet<CaptorSocket> captors) {
		CaptorTableModel.captors = captors;
	}
	
	@Override
	public int getRowCount() {
		return captors.size();
	}
	
	@Override
	public String getColumnName(int col) {
	    return headers[col];
	}

	
	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		CaptorSocket c = (CaptorSocket) (captors.toArray()[rowIndex]);	
		switch(columnIndex) {
			case 0:
				return c;
			case 1:
				return c.getType();
			case 2:
				return c.getBatiment()+ ", " + String.valueOf(c.getEtage()) + ", "+ c.getLieu();
				
			case 3:
				if(c.isTheValueCorrect()) {
					return c.getValue()+" "+c.getType().getUnity();
				}else {
					if(c.isTheValueTooHigh()) {
						return c.getValue()+" (> "+String.valueOf(c.getMax())+") "+c.getType().getUnity();
					}else {
						return c.getValue()+" (< "+String.valueOf(c.getMin())+") "+c.getType().getUnity();
					}
				}
				
			default:
				return null;
		}
	}

	

}
