/**
 *
 * @author Gaurab R. Gautam
 */

package boggle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Graph
{
    final Node[] adjList;
    private final int SIZE;
    private final int ROW_COL_SIZE;
    
    // for tracking purpose
    Map<Character, List<Integer>> mapLetterIndex = new HashMap();
    
    // constructor to initialize adjList with empty nodes
    Graph (char[][] boggle) {
        ROW_COL_SIZE = boggle.length;
        SIZE = ROW_COL_SIZE * ROW_COL_SIZE; // total cells of the matrix/grid
        adjList = new Node[SIZE];
        
        // initialize graph with nodes and edges
        initialize(boggle);
    }
    
    
    private void initialize(char[][] boggle) {
        // create adjacency list of nodes of matrix
        for (int index = 0; index < SIZE; index++) {
            int row = index/ROW_COL_SIZE;
            int col = ((index % ROW_COL_SIZE) == 0 ? 0 : (index % ROW_COL_SIZE));
            char letter = boggle[row][col];
            adjList[index] = new Node(letter, index);
            
            // mapping purpose
            // letter already present in hash map, just add the indices
            if (this.mapLetterIndex.get(letter) != null)
            {
                this.mapLetterIndex.get(letter).add(index);
            }
            // letter yet not inserted into hash map
            else
            {
                List<Integer> indices = new LinkedList();
                indices.add(index);
                
                this.mapLetterIndex.put(letter, indices);
            }
            
            //System.out.println("->" + mapLetterIndex);
        }
        
        for (int index = 0; index < SIZE; index++) {
            int row = index/ROW_COL_SIZE;
            int col = ((index % ROW_COL_SIZE) == 0 ? 0 : (index % ROW_COL_SIZE));
            
            // create edges connected to the node
            // east edge
            if (isValidMove(row, col + 1)) {
                adjList[index].getEdges().add(adjList[index + 1]);
            }
            
            // west edge
            if(isValidMove(row, col - 1)) {
                adjList[index].getEdges().add(adjList[index - 1]);
            }
            
            // north edge
            if (isValidMove(row - 1, col)) {
                adjList[index].getEdges().add(adjList[index - ROW_COL_SIZE]);
            }
            
            // south edge
            if (isValidMove(row + 1, col)) {
                adjList[index].getEdges().add(adjList[index + ROW_COL_SIZE]);
            }
            
            // southeast edge
            if (isValidMove(row + 1, col + 1)) {
                adjList[index].getEdges().add(adjList[index + (ROW_COL_SIZE + 1)]);
            }
            
            // southwest edge
            if (isValidMove(row + 1, col - 1)) {
                adjList[index].getEdges().add(adjList[index + (ROW_COL_SIZE - 1)]);
            }
            
            // northeast edge
            if (isValidMove(row - 1, col + 1)) {
                adjList[index].getEdges().add(adjList[index - (ROW_COL_SIZE - 1)]);
            }
            
            // southeast edge
            if (isValidMove(row -1, col - 1)) {
                adjList[index].getEdges().add(adjList[index - (ROW_COL_SIZE + 1)]);
            }
            
        }
    }
    
    private boolean isValidMove(int row, int col) {
        return ((row >= 0) && (row < ROW_COL_SIZE)) &&
                ((col >= 0) && (col < ROW_COL_SIZE));
    }
    
    public void print()
    {
        for (Node n : this.adjList)
        {
            System.out.println(n.getName() + " : " + n.getEdges());
        }
        
        System.out.println();
    }
}
