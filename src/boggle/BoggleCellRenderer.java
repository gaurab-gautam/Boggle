/**
 *
 * @author Gaurab R. Gautam
 */
package boggle;

import static boggle.BoggleDemo.COL_SIZE;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class BoggleCellRenderer extends DefaultTableCellRenderer 
{
    List<Integer> path = new LinkedList();
    final Color HIGHLIGHT_COLOR = new Color(250, 250, 100); //RBG
    final Color DEFAULT_COLOR = Color.WHITE;

    @Override
    public void setFont(Font font) {
        font = new Font("Courier", Font.BOLD, 18);
        super.setFont(font); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setCellColor(List<Integer> path) {
        this.path = path;
    }
    
    public void resetCellColor(int row, int column) {
        this.path = new LinkedList();
    }
    
    public BoggleCellRenderer() {
        this.setHorizontalAlignment(JLabel.CENTER);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        int index = (COL_SIZE * row + column);
        
        if (this.path != null && this.path.contains(index)) {
            cellComponent.setBackground(HIGHLIGHT_COLOR);
        }
        else {
            cellComponent.setBackground(DEFAULT_COLOR);
        }
        
        
        return cellComponent;
    }
}