/**
 *
 * @author Gaurab R. Gautam
 */
package boggle;

import java.util.LinkedList;
import java.util.List;


public class Node   // Node represents each character in boggle matrix
{
    final int nodeIndex;
    private String name = null;
    VISIT_STATE isVisited = VISIT_STATE.NOT_VISITED;
    private Node parent = null;
    private final List<Node> edges = new LinkedList();
    
    Node (char n, int index) {
        this.name = Character.toString(n);
        this.nodeIndex = index;
    }
    
    public Node getParent() {
        return parent;
    }
    
    public void setParent(Node p) {
        parent = p;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<Node> getEdges() {
        return edges;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
    
    
    public enum VISIT_STATE {
        VISITED("visited"),
        NOT_VISITED("not visited");
        
        private final String name;
        
        VISIT_STATE(String state) {
            this.name = state;
        }
        
        @Override
        public String toString()
        {
            return this.name;
        }
    };
    
    
}
