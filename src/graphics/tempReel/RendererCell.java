package graphics.tempReel;

import java.awt.Color;
import java.awt.Component;

import captors.CaptorSocket;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RendererCell extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        
        CaptorSocket capteur = ((CaptorSocket) table.getValueAt(row, 0));
        if((column == 3) && !(capteur.isTheValueCorrect() )) {
        	this.setValue(table.getValueAt(row, column));
            this.setBackground(Color.RED);
            return this;
        }
        switch (capteur.getType()) {
        case ELECTRICITY:
            this.setValue(table.getValueAt(row, column));
            this.setBackground(Color.YELLOW);
            return this;
        case WATER:
            this.setValue(table.getValueAt(row, column));
            this.setBackground(Color.cyan);
            return this;
        case PRESSURIZEDAIR:
        	this.setValue(table.getValueAt(row, column));
            this.setBackground(Color.ORANGE);
            return this;
        case TEMPERATURE:
        	this.setValue(table.getValueAt(row, column));
            this.setBackground(Color.PINK);
            return this;
        default:
        	this.setValue(table.getValueAt(row, column));
            this.setBackground(Color.BLACK);
            return this;
        }
    }
}