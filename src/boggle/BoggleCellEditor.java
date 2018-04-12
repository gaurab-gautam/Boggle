/**
 *
 * @author Gaurab R. Gautam
 */
package boggle;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


public class BoggleCellEditor extends AbstractCellEditor implements TableCellEditor {

    JComponent component = new BoggleCell();

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
        int rowIndex, int vColIndex) {

      ((BoggleCell) component).setText((String) value);

      return component;
    }

    @Override
    public Object getCellEditorValue() {
      return ((BoggleCell) component).getText();

    }
}
