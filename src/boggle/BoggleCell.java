/**
 *
 * @author Gaurab R. Gautam
 */
package boggle;


import static boggle.BoggleDemo.COL_SIZE;
import static boggle.BoggleDemo.ROW_SIZE;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class BoggleCell extends JTextField
{
    
    
    BoggleCell() {
        this.setHorizontalAlignment(JTextField.CENTER);
        this.setHighlighter(null);
        this.setAutoscrolls(false);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(true);

        this.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if((getLength() + str.length() <= 1) && (Character.isLetter(str.charAt(0))))
                    super.insertString(offs, str.toUpperCase(), a);
            }
        });

        this.addKeyListener(new java.awt.event.KeyAdapter()
        {
            
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                boggleCellKeyReleased(evt);
            }
        });
        
        this.addFocusListener(new java.awt.event.FocusAdapter()
        {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt)
            {
                boggleCellFocusLost(evt);
            }

        });
    }
    
    
    private void boggleCellFocusLost(FocusEvent evt)
    {
//        JTable table = (JTable)(evt.getComponent().getParent());
//        
//        if ((table != null) && (table.getEditingRow()!= -1) && (table.getEditingColumn() != -1)) 
//        {
//            int row = table.getEditingRow();
//            int col = table.getEditingColumn();
//            
//            table.getModel().setValueAt(this.lastVal, row, col);
//            table.getCellEditor(row, col).cancelCellEditing();
//        }
    }
        
   // String lastVal = null;
    private void boggleCellKeyReleased(KeyEvent evt)
    {
        if (!Character.isLetter(evt.getKeyChar())) {
            return;
        }
        
       // this.lastVal = String.valueOf(evt.getKeyChar()).toUpperCase();
        JTable table = (JTable)(evt.getComponent().getParent());
        int row = table.getEditingRow();
        int col = table.getEditingColumn();
        
        if ((col + 1) == COL_SIZE) {
            row += 1;
            col = 0;
        }
        else {
            col += 1;
        }
        
        table.requestFocus();
        
        if ((row < ROW_SIZE) && (col < COL_SIZE)) {
            table.editCellAt(row, col);
            ((BoggleCell)table.getEditorComponent()).requestFocus();
        }
    }
}
