/**
 *
 * @author Gaurab R. Gautam
 */

package boggle;

import static boggle.BoggleDemo.COL_SIZE;
import static boggle.BoggleDemo.ROW_SIZE;
import java.util.LinkedList;
import java.util.List;



public final class DFS
{
    private static final StringBuilder WORD = new StringBuilder();
    
    private static List<Integer> getLetterIndices(char c)
    {
        List<Integer> indices = new LinkedList();
        
        for (int i = 0; i < ROW_SIZE ; i++)
        {
            for (int j = 0; j < COL_SIZE ; j++)
            {
                if (boggle.BoggleDemo.boggle[i][j] == c)
                {
                    indices.add((i * COL_SIZE) + j);
                }
            }
        }
            
        return indices;
    }
    
    public static boolean matchWord(String word, List<Integer> path) {
        boolean isFound = false;
        //System.out.println(word.toString() + ",");
        List<Integer> p = new LinkedList(path);
        
        // match found
        if (word.equals(WORD.toString())) {
            if (!boggle.BoggleDemo.wordsFound.containsKey(word)) {
                // must copy the path so it won't get modified during dfs
                List<List<Integer>> paths = new LinkedList();
                paths.add(p);
                
                boggle.BoggleDemo.wordsFound.put(word, paths);
            }
            else {
                // must copy
                boggle.BoggleDemo.wordsFound.get(word).add(p);
            }
            
            isFound = true;
        }
        
        return isFound;
    }
    
    static void resetGraph(Graph g) {
        WORD.setLength(0);
        
        for (Node n : g.adjList) {
            n.setParent(null);
            n.isVisited = Node.VISIT_STATE.NOT_VISITED;
        }
    }
    
    static void dfs (Graph g, String word) {
        List<Integer> path = new LinkedList();
        
        for (int i : getLetterIndices(word.charAt(0)))
        {
            Node source = g.adjList[i];
            dfs_Visit(word, g, source, path);    // first start from source
        }
    }
    
    private static void dfs_Visit(String aword, Graph g, Node parent, List<Integer> path) {
        
        parent.isVisited = Node.VISIT_STATE.VISITED;
        WORD.append(parent.getName());
        path.add(parent.nodeIndex);
        
        if (!aword.contains(WORD.toString()))
        {
            return;
        }
        else if (!matchWord(aword, path) && (WORD.toString().length() >= aword.length()))
        {
            return;
        }
        
        for (Node n : parent.getEdges())
        {
            if (n.isVisited == Node.VISIT_STATE.NOT_VISITED)
            {
                if (WORD.toString().length() <= Constants.MAX_WORD_LENGTH) 
                {
                    n.setParent(parent);
                    dfs_Visit(aword, g, n, path);
                    
                    // reset
                    n.isVisited = Node.VISIT_STATE.NOT_VISITED;
                    
                    // remove the last letter
                    WORD.deleteCharAt(WORD.length() -1);
                    
                    // remove the last node
                    path.remove(path.size() - 1);
                }
            }
        }
    }
}
