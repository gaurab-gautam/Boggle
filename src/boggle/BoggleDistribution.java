/**
 *
 * @author Gaurab R. Gautam
 */
package boggle;

import java.util.Random;


public class BoggleDistribution 
{
    final private static long SEED;
    final private static Random RANDOM;
    
    static {
        SEED = System.currentTimeMillis();
        RANDOM = new Random(SEED);
    }
        
    // prevent class from instantiating
    private BoggleDistribution() {}
    
    // the 16 Boggle dice (1992 version)
    private static final String[] BOGGLE_1992 = {
        "LRYTTE", "VTHRWE", "EGHWNE", "SEOTIS",
        "ANAEEG", "IDSYTT", "OATTOW", "MTOICU",
        "AFPKFS", "XLDERI", "HCPOAS", "ENSIEU",
        "YLDEVR", "ZNRNHL", "NMIQHU", "OBBAOJ"
    };

    // the 16 Boggle dice (1983 version)
    private static final String[] BOGGLE_1983 = {
        "AACIOT", "ABILTY", "ABJMOQ", "ACDEMP",
        "ACELRS", "ADENVZ", "AHMORS", "BIFORX",
        "DENOSW", "DKNOTU", "EEFHIY", "EGINTV",
        "EGKLUY", "EHINPS", "ELPSTU", "GILRUW",
    };

    // the 25 Boggle Master / Boggle Deluxe dice
    private static final String[] BOGGLE_MASTER = {
        "AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM",
        "AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCNSTW",
        "CEIILT", "CEILPT", "CEIPST", "DDLNOR", "DHHLOR",
        "DHHNOT", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU",
        "FIPRSY", "GORRVW", "HIPRRY", "NOOTUW", "OOOTTU"
    };

    // the 25 Big Boggle dice
    private static final String[] BOGGLE_BIG = {
        "AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM",
        "AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCENST",
        "CEIILT", "CEILPT", "CEIPST", "DDHNOT", "DHHLOR",
        "DHLNOR", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU",
        "FIPRSY", "GORRVW", "IPRRRY", "NOOTUW", "OOOTTU"
    };


    // letters and frequencies of letters in the English alphabet
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final double[] PROBABILITIES = {
        0.08167, 0.01492, 0.02782, 0.04253, 0.12703, 0.02228,
        0.02015, 0.06094, 0.06966, 0.00153, 0.00772, 0.04025,
        0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987,
        0.06327, 0.09056, 0.02758, 0.00978, 0.02360, 0.00150,
        0.01974, 0.00074
    };
    
    public static char[][] getBoardFromRollingHashbroDice1992(int rows, int cols)
    {
        shuffle(BOGGLE_1992);
        char[][] board = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String letters = BOGGLE_1992[cols*i+j];
                int r = uniform(letters.length());
                board[i][j] = letters.charAt(r);
            }
        }
        
        return board;
    }
    
    public static char[][] getRandomBoardFromAlphabetProbabilities(int rows, int cols)
    {
        if (rows <= 0) throw new IllegalArgumentException("number of rows must be a positive integer");
        if (cols <= 0) throw new IllegalArgumentException("number of columns must be a positive integer");
        char[][] board = new char[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int r = discrete(PROBABILITIES);
                board[i][j] = ALPHABET.charAt(r);
            }
        }
        
        return board;
    }
    
    private static int discrete(double[] probabilities)
    {
        if (probabilities == null) throw new IllegalArgumentException("argument array is null");
        double EPSILON = 1E-14;
        double sum = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            if (!(probabilities[i] >= 0.0))
                throw new IllegalArgumentException("array entry " + i + " must be nonnegative: " + probabilities[i]);
            sum += probabilities[i];
        }
        if (sum > 1.0 + EPSILON || sum < 1.0 - EPSILON)
            throw new IllegalArgumentException("sum of array entries does not approximately equal 1.0: " + sum);

        
        // the for loop may not return a value when both r is (nearly) 1.0 and when the
        // cumulative sum is less than 1.0 (as a result of floating-point roundoff error)
        while (true) {
            double r = uniform();
            sum = 0.0;
            for (int i = 0; i < probabilities.length; i++) {
                sum = sum + probabilities[i];
                if (sum > r) return i;
            }
        }
    }
    
    // return random double uniformly [0, 1]
    private static double uniform()
    {
        return RANDOM.nextDouble();
    }
    
    // returns random int uniformly [0, n)
    private static int uniform(int n)
    {
        if (n <= 0)
        {
            throw new IllegalArgumentException("argument must be positive: " + n);
        }
        
        return RANDOM.nextInt(n);
    }
    
    public static void shuffle(Object[] a) {
        if (a == null)
        {
            throw new IllegalArgumentException("argument is null");
        }

        int n = a.length;
        for (int i = 0; i < n; i++) {
            int r = i + uniform(n-i);     // between i and n-1
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }
}
