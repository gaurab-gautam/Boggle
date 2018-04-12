/**
 *
 * @author Gaurab R. Gautam
 */
package boggle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Boggle
{
//    final static char [][] boggleArr = new char [][]{
//                        {'H', 'A', 'W'},
//                        {'Y', 'J', 'N'},
//                        {'A', 'T', 'S'}
//    };
    
    final static char [][] boggleArr = new char [][]{
                        {'y', 's', 'o', 'c'},
                        {'e', 'p', 'h', 'p'},
                        {'e', 'o', 'r', 'i'},
                        {'a', 'l', 'p', 'i'}
    };
    
    public static List<String> wordlist = new LinkedList<>();
    private static final int ROW_COL_SIZE = 4;  // for this demo, side of matrix
    static StringBuilder word = new StringBuilder();
    static Graph g;
    
    private static void findWord(int index) {
//        DFS.dfs(g, index);
//        DFS.resetGraph(g);
    }
    
    

    private static void matchWord() {
//        System.out.println(word.toString());
//        if (wordlist.contains(word.toString())) {
//            System.out.println ("Matched: " + word.toString());
//        }
    }

    private static void init() {
        initWords();
        createGraph();
    }
    
    private static void createGraph() {
        g = new Graph(boggleArr);
        //g.print();
        
    }
    
    // some possible words from boggleArr board
    private static void initWords()
    {
//        wordlist.add("HAY");
//        wordlist.add("ANT");
//        wordlist.add("STAY");
//        wordlist.add("JAW");
//        wordlist.add("NAY");
//        wordlist.add("WAY");
//        wordlist.add("HAN");
        
        wordlist.add("propose");
        wordlist.add("alephs");
        wordlist.add("chirps");
        wordlist.add("chirpy");
        wordlist.add("elopes");
        wordlist.add("ephori");
        wordlist.add("epochs");
        wordlist.add("hirple");
        wordlist.add("people");
        wordlist.add("propel");
        wordlist.add("aleph");
        wordlist.add("aloes");
        wordlist.add("chiro");
        wordlist.add("chirp");
        wordlist.add("chola");
        wordlist.add("chops");
        wordlist.add("chose");
        wordlist.add("copes");
        wordlist.add("hole");
        wordlist.add("holp");
        wordlist.add("hope");
        wordlist.add("hops");
        wordlist.add("ship");
        wordlist.add("shoe");
        wordlist.add("shop");
        wordlist.add("shri");
        wordlist.add("lee");
        wordlist.add("lop");
        wordlist.add("oes");
        wordlist.add("ose");
        wordlist.add("pea");
        wordlist.add("see");
        wordlist.add("sop");
        wordlist.add("spy");
        wordlist.add("yep");
        wordlist.add("yes");
    }
    
    private static void playBoggle()
    {
//        // Each cell in the matrix is source node for dfs search algorithm
//        for (int index = 0; index < (ROW_COL_SIZE * ROW_COL_SIZE); index++) {
//            findWord(index);
//            
//            //break;
//        }
//        
//        
//        
//        System.out.println();
//        List<String> sorted = new LinkedList(wordsFound.keySet());
//        Collections.sort(sorted, (String t1, String t2) -> (t2.length() - t1.length()));
//        System.out.println(sorted);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        init();
        playBoggle();
    }
    
    private static void goEast(Node node, int index, int row, int col) {
            while (isValidMove(row, col + 1)) {
                index += 1;
                col += 1;
                word.append(boggleArr[row][col]);
                matchWord();
            }
        }
        
    private static void goWest(Node node, int index, int row, int col) {
        while (isValidMove(row, col - 1)) {
            index -= 1;
            col -= 1;
            word.append(boggleArr[row][col]);
            //this.visited.add(boggleArr[row][col]);
            matchWord();
        }
    }

    private static void goNorth(Node node, int index, int row, int col) {
        while (isValidMove(row - 1, col)) {
            index -= ROW_COL_SIZE;
            row -= 1;
            word.append(boggleArr[row][col]);
            //this.visited.add(boggleArr[row][col]);
            matchWord();
        }
    }

    private static void goSouth(Node node, int index, int row, int col) {
        while (isValidMove(row + 1, col)) {
            index += ROW_COL_SIZE;
            row += 1;
            word.append(boggleArr[row][col]);
            //this.visited.add(boggleArr[row][col]);
            matchWord();
        }
    }

    private static void goSouthEast(Node node, int index, int row, int col) {
        while (isValidMove(row + 1, col + 1)) {
            index += (ROW_COL_SIZE + 1);
            row += 1;
            col += 1;
            word.append(boggleArr[row][col]);
            //this.visited.add(boggleArr[row][col]);
            matchWord();
        }
    }

    private static void goSouthWest(Node node, int index, int row, int col)    {
        while (isValidMove(row + 1, col - 1)) {
            index += (ROW_COL_SIZE - 1);
            row += 1;
            col -= 1;
            word.append(boggleArr[row][col]);
            //this.visited.add(boggleArr[row][col]);
            matchWord();
        }
    }

    private static void goNorthEast(Node node, int index, int row, int col) {
        while (isValidMove(row - 1, col + 1)) {
            index -= (ROW_COL_SIZE - 1);
            row -= 1;
            col += 1;
            word.append(boggleArr[row][col]);
            //this.visited.add(boggleArr[row][col]);
            matchWord();
        }
    }

    private static void goNorthWest(Node node, int index, int row, int col) {
        while (isValidMove(row -1, col - 1)) {
            index -= (ROW_COL_SIZE + 1);
            row -= 1;
            col -= 1;
            word.append(boggleArr[row][col]);
            //this.visited.add(boggleArr[row][col]);
            matchWord();
        }
    }
    
    private static boolean isValidMove(int row, int col) {
        return ((row >= 0) && (row < ROW_COL_SIZE)) &&
                ((col >= 0) && (col < ROW_COL_SIZE));
    }
    
    
}
