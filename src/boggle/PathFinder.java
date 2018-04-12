/**
 *
 * @author Gaurab R. Gautam
 */
package boggle;

import static boggle.BoggleDemo.COL_SIZE;
import static boggle.BoggleDemo.ROW_SIZE;
import static boggle.BoggleDemo.boggle;
import static boggle.BoggleDemo.userInput;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;


public class PathFinder 
{
    // list of paths
    List<List<Integer>> paths = new LinkedList();
    private final Graph g;
    List<Integer> currentPath = new LinkedList();
    
    public PathFinder(Graph g)
    {
        this.g = g;
    }
    
    private boolean isCompletePath(List<Integer> path)
    {
        StringBuilder letterSequence = new StringBuilder();
        
        for (int index : path)
        {
            int row = index/ROW_SIZE;
            int col = ((index % COL_SIZE) == 0 ? 0 : (index % COL_SIZE));
            
            if (boggle[row][col] == 'Q' && userInput.toString().contains("QU"))
            {
                letterSequence.append("QU");
            }
            else
            {
                letterSequence.append(boggle[row][col]);
            }
        }
        
        return (letterSequence.toString().equals(userInput.toString()));
    }
    
    public boolean find(String s)
    {
        boolean isPathFound = false;
        List<List<Integer>> newFoundPaths = new LinkedList();
        ListIterator<List<Integer>> iter = this.paths.listIterator();
        
        // the letter not found on the board
        if (!g.mapLetterIndex.containsKey(s.charAt(0)))
        {
            return false;
        }
        
        // new path
        if (this.paths.isEmpty())
        {
           
            List<Integer> indices = g.mapLetterIndex.get(s.charAt(0));

            // add every source s on the board to the list
            for (Integer i : indices)
            {
                List<Integer> aPath = new LinkedList();
                aPath.add(i);
                this.paths.add(aPath);

            }
            
            this.currentPath = this.paths.get(0);
            
            return true;
        }
        
        while (iter.hasNext())
        {
            List<Integer> aPath = iter.next();
            boolean pathFound = false;
            int index = aPath.get(aPath.size() - 1);
            List<Integer> originalPath = new LinkedList(aPath);
            int row = index/ROW_SIZE;
            int col = ((index % COL_SIZE) == 0 ? 0 : (index % COL_SIZE));
            
            
            
            // east edge
            if (isValidMove(row, col + 1) && (s.charAt(0) == boggle[row][col + 1])) {
                int itemIndex = row * COL_SIZE + (col + 1);
                
                // duplicate move, using the same cell letter twice or more not allowed
                // and it is a complete path
                if (!aPath.contains(itemIndex) && isCompletePath(originalPath))
                {
                    aPath.add(itemIndex);
                    pathFound = true;
                }
            }
            
            // west edge
            if (isValidMove(row, col - 1) && (s.charAt(0) == boggle[row][col - 1])) {
                int itemIndex = row * COL_SIZE + (col - 1);
                
                // duplicate move, using the same cell letter twice or more not allowed
                // and it is a complete path
                // a new path is added if path has already been found with different move
                if (!aPath.contains(itemIndex) && isCompletePath(originalPath))
                {
                    if (!pathFound)
                    {
                        aPath.add(itemIndex);
                    }
                    else
                    {
                        List<Integer> newPath = new LinkedList(aPath.subList(0, aPath.size() - 1));
                        newPath.add(itemIndex);
                        newFoundPaths.add(newPath);
                    }
                    
                    pathFound = true;
                }
            }
            
            // north edge
            if (isValidMove(row - 1, col) && (s.charAt(0) == boggle[row - 1][col])) {
                int itemIndex = (row - 1) * COL_SIZE + col;
                
                // duplicate move, using the same cell letter twice or more not allowed
                // and it is a complete path
                // a new path is added if path has already been found with different move
                if (!aPath.contains(itemIndex) && isCompletePath(originalPath))
                {
                    if (!pathFound)
                    {
                        aPath.add(itemIndex);
                    }
                    else
                    {
                        List<Integer> newPath = new LinkedList(aPath.subList(0, aPath.size() - 1));
                        newPath.add(itemIndex);
                        newFoundPaths.add(newPath);
                    }
                    
                    pathFound = true;
                }
            }

            
            // south edge
            if (isValidMove(row + 1, col) && (s.charAt(0) == boggle[row + 1][col])) {
                int itemIndex = (row + 1) * COL_SIZE + col;
                
                // duplicate move, using the same cell letter twice or more not allowed
                // and it is a complete path
                // a new path is added if path has already been found with different move
                if (!aPath.contains(itemIndex) && isCompletePath(originalPath))
                {
                    if (!pathFound)
                    {
                        aPath.add(itemIndex);
                    }
                    else
                    {
                        List<Integer> newPath = new LinkedList(aPath.subList(0, aPath.size() - 1));
                        newPath.add(itemIndex);
                        newFoundPaths.add(newPath);
                    }
                    
                    pathFound = true;
                }
            }

            
            // southeast edge
            if (isValidMove(row + 1, col + 1) && (s.charAt(0) == boggle[row + 1][col + 1])) {
                int itemIndex = (row + 1)* COL_SIZE + (col + 1);
                
                // duplicate move, using the same cell letter twice or more not allowed
                // and it is a complete path
                // a new path is added if path has already been found with different move
                if (!aPath.contains(itemIndex) && isCompletePath(originalPath))
                {
                    if (!pathFound)
                    {
                        aPath.add(itemIndex);
                    }
                    else
                    {
                        List<Integer> newPath = new LinkedList(aPath.subList(0, aPath.size() - 1));
                        newPath.add(itemIndex);
                        newFoundPaths.add(newPath);
                    }
                    
                    pathFound = true;
                }
            }

            
            // southwest edge
            if (isValidMove(row + 1, col - 1) && (s.charAt(0) == boggle[row + 1][col - 1])) {
                int itemIndex = (row + 1) * COL_SIZE + (col - 1);
                
                // duplicate move, using the same cell letter twice or more not allowed
                // and it is a complete path
                // a new path is added if path has already been found with different move
                if (!aPath.contains(itemIndex) && isCompletePath(originalPath))
                {
                    if (!pathFound)
                    {
                        aPath.add(itemIndex);
                    }
                    else
                    {
                        List<Integer> newPath = new LinkedList(aPath.subList(0, aPath.size() - 1));
                        newPath.add(itemIndex);
                        newFoundPaths.add(newPath);
                    }
                    
                    pathFound = true;
                }
            }

            
            // northeast edge
            if (isValidMove(row - 1, col + 1) && (s.charAt(0) == boggle[row - 1][col + 1])) {
                int itemIndex = (row - 1)* COL_SIZE + (col + 1);
                
                // duplicate move, using the same cell letter twice or more not allowed
                // and it is a complete path
                // a new path is added if path has already been found with different move
                if (!aPath.contains(itemIndex) && isCompletePath(originalPath))
                {
                    if (!pathFound)
                    {
                        aPath.add(itemIndex);
                    }
                    else
                    {
                        List<Integer> newPath = new LinkedList(aPath.subList(0, aPath.size() - 1));
                        newPath.add(itemIndex);
                        newFoundPaths.add(newPath);
                    }
                    
                    pathFound = true;
                }
            }

            
            // northwest edge
            if (isValidMove(row -1, col - 1) && (s.charAt(0) == boggle[row - 1][col - 1])) {
                int itemIndex = (row -1 )* COL_SIZE + (col - 1);
                
                // duplicate move, using the same cell letter twice or more not allowed
                // and it is a complete path
                // a new path is added if path has already been found with different move
                if (!aPath.contains(itemIndex) && isCompletePath(originalPath))
                {
                    if (!pathFound)
                    {
                        aPath.add(itemIndex);
                    }
                    else
                    {
                        List<Integer> newPath = new LinkedList(aPath.subList(0, aPath.size() - 1));
                        newPath.add(itemIndex);
                        newFoundPaths.add(newPath);
                    }
                    
                    pathFound = true;
                }
            }
            
            
            
            if (pathFound)
            {
                this.currentPath = new LinkedList(aPath);
                isPathFound = true;
            }
        }

        // add all the new found paths to the list of paths
        if (!newFoundPaths.isEmpty()) 
        {
            this.paths.addAll(newFoundPaths);
        }
        
        return isPathFound;
    }
    
    void removeLastFromPath(char letter)
    {
        for (List<Integer> path : this.paths)
        {
            int index = path.get(path.size() - 1);
            int row = index/ROW_SIZE;
            int col = ((index % COL_SIZE) == 0 ? 0 : (index % COL_SIZE));
            
            if ( (this.currentPath.size() == path.size()) && 
                    (boggle[row][col] == letter) )
            {
                path.remove(path.size() - 1);
            }
        }
        
        if (this.paths.size() > 1)
        {
            // check for duplicate entries after removal
            Set<List<Integer>> set = new LinkedHashSet(this.paths);
            this.paths = new LinkedList(set);
        }
        
    }
    
    private boolean isValidMove(int row, int col) {
        return ((row >= 0) && (row < ROW_SIZE)) &&
                ((col >= 0) && (col < COL_SIZE));
    }
}
