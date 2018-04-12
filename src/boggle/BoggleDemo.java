/**
 *
 * @author Gaurab R. Gautam
 */

package boggle;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


public class BoggleDemo extends javax.swing.JFrame implements PropertyChangeListener
{
    private JPopupMenu popup;
    public static char [][] boggle = null;
    public static Map<String, String> dictionary = new HashMap<>();
    public static int ROW_SIZE = Constants.DEFAULT_BOGGLE_SIZE;  // row of matrix
    public static int COL_SIZE = Constants.DEFAULT_BOGGLE_SIZE;  // column of matrix

    public static Graph g;
    private BackgroundWorker worker;
    public static final Map<String, List<List<Integer>>> wordsFound =  new HashMap();
    private static final  Map<String, String> userEnteredWords = new HashMap();
    public static StringBuilder userInput = new StringBuilder();
    PathFinder pathfinder = null;
    boolean invalidInput = false;
    private int maxScore = 0;
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        
    }
    
    private int getScore(String word)
    {
        int length = word.length();
        
        switch (length)
        {
            case 0:
            case 1:
            case 2:
                return 0;
            
            case 3:
            case 4:
                return 1;
                
            case 5:
                return 2;
                
            case 6:
                return 3;
                
            case 7:
                return 5;
                
            default:    // for world length >= 8
                return 11;
                
        }
    }
    
    private class PopupActionListener implements ActionListener 
    {
        List<Integer> path;
        
        public PopupActionListener(List<Integer> path) {
            this.path = path;
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            showSolutionPath(this.path);
        }
    }
    
    // An inner class to check whether mouse events are the popup trigger
    class MousePopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            checkPopup(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            checkPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            checkPopup(e);
        }

        private void checkPopup(MouseEvent e) {
            if (popup != null) {
                popup.show(displayArea, e.getX(), e.getY());
            }
        }
    }
    
    public class BackgroundWorker extends SwingWorker<Void, Void> 
    {
        private void findWord (String word) {
            DFS.dfs(g, word);
            DFS.resetGraph(g);
        }

        @Override
        protected Void doInBackground() throws Exception
        {
            for (String word : dictionary.keySet()) 
            {
                findWord(word);
            }

            return null;
        }

        /*
        * Executed in event dispatching thread
        */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            List<String> sorted = new LinkedList(wordsFound.keySet());
            Collections.sort(sorted, (String t1, String t2) -> (t2.length() - t1.length()));
            
            DefaultListModel model = (DefaultListModel) displayArea.getModel();
                    
            for (String s : sorted) {
                model.addElement(s);
                maxScore += getScore(s);
            }
            
            setCursor(null);
            jProgressBar2.setVisible(false);
            boggleSolutionBoard1.setMaxPointsPossible(maxScore);
            maxScore = 0;
            findSolutionMenuItem.setEnabled(false);
        }
    }
    
    public class PlayTimeCounter
    {
        ScheduledExecutorService service;
        
        private class Task implements Runnable
        {
            int time = COL_SIZE == 5 ? COL_SIZE * 60 : (COL_SIZE - 1) * 60;
            int min = time >= 60 ? time/60 - 1 : 0;
            int seconds = time >= 60 ? 60 : time;

            @Override
            public void run() {
                time -= 1;
                seconds -= 1;

                timerLabel.setText(min + " : " + String.format("%02d", seconds));

                if (seconds <= 0)
                {
                    seconds = 60;
                    min -= 1;
                }

                if (time <= 0)
                {
                    stop();
                    resultDisplay1.update((DefaultListModel)displayArea.getModel());

                    jSplitPane4.setTopComponent(null);
                    jSplitPane4.setTopComponent(resultDisplay1);
                }
            }
        } 
        
        public void start()
        {
            if (service != null)
            {
                service.shutdownNow();
                service = null;
            }
            
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(new Task(), 0, 1000, TimeUnit.MILLISECONDS);
        }
        
        public void stop()
        {
            service.shutdownNow();
            service = null;
        }
    }
    
    private void createPopup(List<List<Integer>> paths) {
        popup = new JPopupMenu();
       
        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent pme)
            {
                
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent pme)
            {
                popup = null;
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent pme)
            {
                popup = null;
            }
            
        
        });
        
        for (int i = 0; i < paths.size(); i++){
            ActionListener popupListener = new PopupActionListener(paths.get(i));
        
            JMenuItem item = new JMenuItem("Solution " + (i + 1));
            popup.add(item);
            item.setHorizontalTextPosition(JMenuItem.LEFT);
            item.setName(paths.get(i).toString());
            //System.out.println(paths.get(i).toString());
            item.addActionListener(popupListener);
            popup.setBorder(new BevelBorder(BevelBorder.RAISED));
        }  
        
        displayArea.addMouseListener(new MousePopupListener());
    }
    
    private void showSolutionPath(List<Integer> path) {
        for (int index = 0; index < ROW_SIZE * COL_SIZE; index++)  {
            this.boggleSolutionBoard1.setCellBackground(index, path);
        }

        this.boggleSolutionBoard1.getBoggleTable().repaint();
    }
    
    private void showWordPath(List<Integer> path)
    {
        // highlight until key release
        for (int index = 0; index < ROW_SIZE * COL_SIZE; index++)  
        {
            this.boggleBoard1.setCellBackground(index, path);
        }


        this.boggleBoard1.getBoggleTable().repaint();
    }
    
    private void resetBoard()
    {
        // highlight until key release
        for (int index = 0; index < ROW_SIZE * COL_SIZE; index++)  
        {
            this.boggleBoard1.setCellBackground(index,  null);
        }


        this.boggleBoard1.getBoggleTable().repaint();
    }
    
    public void findSolution() {
        this.boggleSolutionBoard1.repaintTable();
        
        boggle = this.boggleSolutionBoard1.getUserInput();
        wordsFound.clear();
        
        if (boggle != null) {
            ((DefaultListModel) this.displayArea.getModel()).clear();
            ROW_SIZE = boggle.length;
            COL_SIZE = boggle.length;
            createGraph();
        
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            this.jProgressBar2.setVisible(true);
            //g.print();
            worker = new BackgroundWorker();
            worker.addPropertyChangeListener(this);
            worker.execute();
        }
    }
  
    
    private void createGraph() {
        g = new Graph(boggle);
    }
    
    private void loadAndSaveDictionaryForProgram()
    {
        // clear the current word list
        dictionary.clear();
        
        // Set the directory to user directory where the file chooser dialog opens
        //this.fileChooser.setCurrentDirectory(new File (System.getProperty("user.dir")));
        String path = this.fileChooser.getFileSystemView().getDefaultDirectory().getPath();
        this.fileChooser.setCurrentDirectory(new File (path));
        
        // User respons on file chooser dialog
        int returnVal = fileChooser.showOpenDialog(this);
        
        // BufferedReader that reads the content of the file
        BufferedReader reader = null;
        BufferedWriter writer = null;
        StringBuilder words = new StringBuilder();
        
        dictionary.clear();
        
        // If the user selects file and okays to proceed, continue
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            // User selected input file 
            File file = fileChooser.getSelectedFile();
            
            try
            {
                // Create a buffered reader object to read the file
                reader = new BufferedReader (new FileReader(file.getAbsolutePath()));
                
                // Variable to hold each line of input file
                String line = null;
               
                // Read a line of the input file until the end
                while ((line = reader.readLine()) != null)
                {
                    // If there is an empty line or line with a digit, just ignore and continue
                    if (line.trim().isEmpty() || line.trim().length() < 3)
                    {
                        continue;
                    }

                    //this.jTextArea1.append(line.trim() + "\r\n");
                    dictionary.put(line.trim().toUpperCase(), line.trim());
                    words.append(line.trim()).append("\r\n");
                }
                
                // close the input file
                reader.close();
                
                // Path of dictionary file
                path = System.getProperty("user.dir") + "\\input\\dictionary.txt";
                writer = new BufferedWriter(new FileWriter((new File(path)).getAbsolutePath()));
                writer.write(words.toString());
                writer.flush();
                writer.close();
                words.setLength(0);

                this.solutionMenu.setEnabled(true);
                
                // Let the user know the input file has been processed successfully
                JOptionPane.showMessageDialog(this, "Dictionary loaded successfully!", 
                        "BoggleDemo", JOptionPane.PLAIN_MESSAGE);
                
            }
            
            // Exception Occured during input file processing
            catch (IOException ex)
            {
                // Print the exception message to standard output
                System.out.println(ex.getMessage());
                
                JOptionPane.showMessageDialog(this, "Dictionary load failed, try again!!!\r\n" + ex.getMessage(), 
                        "BoggleDemo", JOptionPane.ERROR_MESSAGE);
            }
            
            finally
            {
                // Close the BufferedReader, catch exception if it occurs
                if (writer != null)
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(BoggleDemo.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                // Close the BufferedReader, catch exception if it occurs
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(BoggleDemo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        //this.jTextArea1.append(dictionary.toString());
    }
    
    private void loadDictionaryFromFile() {
        // clear the current word list
        dictionary.clear();
        
        // Path of dictionary file
        String path = System.getProperty("user.dir") + "\\input\\dictionary.txt";
        
        // BufferedReader that reads the content of the file
        BufferedReader reader = null;
        
        File file = new File(path);
        
            
        try
        {
             // check if file is empty
            if (file.length() <= 0) {
                throw (new IOException());
            }
        
            // Create a buffered reader object to read the file
            reader = new BufferedReader (new FileReader(file.getAbsolutePath()));
            
            // Variable to hold each line of input file
            String line;

            // Read a line of the input file until the end
            while ((line = reader.readLine()) != null)
            {
                // If there is an empty line or line with a digit, just ignore and continue
                if (line.trim().isEmpty())
                {
                    continue;
                }

                dictionary.put(line.trim().toUpperCase(), line.trim());
            }

            this.solutionMenu.setEnabled(true);
        }

        // Exception Occured during input file processing
        catch (IOException ex)
        {
            // Print the exception message to standard output
            ex.printStackTrace();
            this.solutionMenu.setEnabled(false);
            
            JOptionPane.showMessageDialog(this, "Please load dictionary file first!", 
                        "BoggleDemo", JOptionPane.ERROR_MESSAGE);
        }

        finally
        {
            // Close the BufferedReader, catch exception if it occurs
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(BoggleDemo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private char[][] loadBoardFromFile()
    {
        // Set the directory to user directory where the file chooser dialog opens
        //this.fileChooser.setCurrentDirectory(new File (System.getProperty("user.dir")));
        String path = this.fileChooser.getFileSystemView().getDefaultDirectory().getPath();
        this.fileChooser.setCurrentDirectory(new File (path));
        
        // User respons on file chooser dialog
        int returnVal = fileChooser.showOpenDialog(this);
        
        // BufferedReader that reads the content of the file
        BufferedReader reader = null;
        
        // If the user selects file and okays to proceed, continue
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            // User selected input file 
            File file = fileChooser.getSelectedFile();
            
            try
            {
                // Create a buffered reader object to read the file
                reader = new BufferedReader (new FileReader(file.getAbsolutePath()));
                
                // Variable to hold each line of input file
                String line = null;
                
                // First read the board size
                line = reader.readLine();
                ROW_SIZE = Integer.parseInt(line.split("\\s+")[0]);
                COL_SIZE = Integer.parseInt(line.split("\\s+")[1]);
                boggle = new char[ROW_SIZE][COL_SIZE];
                int index = 0;
                
                // Read a line of the input file until the end
                while ((line = reader.readLine()) != null)
                {
                    // If there is an empty line or line with a digit, just ignore and continue
                    if (line.trim().isEmpty() || line.trim().length() < 3)
                    {
                        continue;
                    }
                    
                    String[] splits = line.split("\\s+|\r\n");
                    
                    for (int i = 0; i < COL_SIZE; i++)
                    {
                        boggle[index][i] = splits[i].trim().charAt(0);
                    }
                                
                    index += 1;          
                }
                
               
                // close the input file
                reader.close();
                this.boggleSolutionBoard1.createTable();
                
                this.solutionMenu.setEnabled(true);
                
            }
            
            // Exception Occured during input file processing
            catch (IOException ex)
            {
                // Print the exception message to standard output
                System.out.println(ex.getMessage());
                boggle = null;
                
                JOptionPane.showMessageDialog(this, "File load failed, try again!!!\r\n" + ex.getMessage(), 
                        "BoggleDemo", JOptionPane.ERROR_MESSAGE);
            }
            
            finally
            {
                // Close the BufferedReader, catch exception if it occurs
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(BoggleDemo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return boggle;
        
    }
    
    private void initBoggle()
    {
        BoggleDemo.boggle = this.boggleBoard1.getBoggle();
        createGraph();
        playTimeCounter = new PlayTimeCounter();
        playTimeCounter.start();
    }
    
    /**
     * Creates new form BoggleDemo
     */
    public BoggleDemo()
    {
        initComponents();
        this.jProgressBar2.setVisible(false);
        initBoggle();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        boggleIconPanel1 = new boggle.BoggleIconPanel();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        userInputTextField = new javax.swing.JTextField();
        userInputTextField.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
                if(Character.isLetter(str.charAt(0)))
                {       
                    super.insertString(offs, str.toUpperCase(), a);
                }
            }
        });

        timerLabel = new javax.swing.JLabel();
        boggleBoard1 = new boggle.BoggleBoard();
        jScrollPane2 = new javax.swing.JScrollPane();
        displayArea = new javax.swing.JList<>();
        jScrollPane2.setViewportView(displayArea);
        jPanel2 = new javax.swing.JPanel();
        jProgressBar2 = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        boggleMenu = new javax.swing.JMenu();
        playMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        solutionMenu = new javax.swing.JMenu();
        boardFromFileMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        loadBoardFromUser = new javax.swing.JMenuItem();
        findSolutionMenuItem = new javax.swing.JMenuItem();
        dictMenu = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(getPreferredSize());
        setResizable(false);
        setSize(getPreferredSize());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setMaximumSize(getPreferredSize());
        jPanel1.setMinimumSize(getPreferredSize());
        jPanel1.setPreferredSize(new java.awt.Dimension(575, 615));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSplitPane1.setDividerLocation(585);
        jSplitPane1.setDividerSize(1);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(570, 610));
        jSplitPane1.setVerifyInputWhenFocusTarget(false);

        jSplitPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jSplitPane2.setDividerLocation(340);
        jSplitPane2.setDividerSize(1);
        jSplitPane2.setAutoscrolls(true);
        jSplitPane2.setMinimumSize(getPreferredSize());
        jSplitPane2.setPreferredSize(new java.awt.Dimension(579, 600));

        jSplitPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(204, 204, 204))); // NOI18N
        jSplitPane3.setDividerLocation(430);
        jSplitPane3.setDividerSize(2);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        boggleIconPanel1.setMaximumSize(getPreferredSize());
        boggleIconPanel1.setMinimumSize(getPreferredSize());
        jPanel3.add(boggleIconPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, 130));

        jSplitPane3.setRightComponent(jPanel3);

        jSplitPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(204, 204, 204))); // NOI18N
        jSplitPane4.setDividerLocation(395);
        jSplitPane4.setDividerSize(2);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        userInputTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                userInputTextFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                userInputTextFieldKeyReleased(evt);
            }
        });

        timerLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        timerLabel.setForeground(new java.awt.Color(255, 0, 0));
        timerLabel.setText("4 : 00");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userInputTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(timerLabel)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userInputTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timerLabel)))
        );

        jSplitPane4.setBottomComponent(jPanel4);
        jSplitPane4.setLeftComponent(boggleBoard1);

        jSplitPane3.setLeftComponent(jSplitPane4);

        jSplitPane2.setLeftComponent(jSplitPane3);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        displayArea.setModel(new DefaultListModel());
        displayArea.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        displayArea.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                displayAreaValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(displayArea);

        jSplitPane2.setRightComponent(jScrollPane2);

        jSplitPane1.setLeftComponent(jSplitPane2);

        jProgressBar2.setFocusable(false);
        jProgressBar2.setIndeterminate(true);
        jProgressBar2.setMaximumSize(new java.awt.Dimension(10, 14));
        jProgressBar2.setPreferredSize(new java.awt.Dimension(10, 14));
        jProgressBar2.setRequestFocusEnabled(false);
        jProgressBar2.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        jSplitPane1.setRightComponent(jPanel2);

        jPanel1.add(jSplitPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, -1, -1));

        boggleMenu.setText("Boggle");

        playMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        playMenuItem.setText("Play");
        playMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playMenuItemActionPerformed(evt);
            }
        });
        boggleMenu.add(playMenuItem);
        boggleMenu.add(jSeparator1);

        solutionMenu.setText("Solution");

        boardFromFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        boardFromFileMenuItem.setText("Load Board From File");
        boardFromFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boardFromFileMenuItemActionPerformed(evt);
            }
        });
        solutionMenu.add(boardFromFileMenuItem);
        solutionMenu.add(jSeparator2);

        loadBoardFromUser.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        loadBoardFromUser.setText("Enter Board Manually");
        loadBoardFromUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBoardFromUserActionPerformed(evt);
            }
        });
        solutionMenu.add(loadBoardFromUser);

        findSolutionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        findSolutionMenuItem.setText("Find Solution");
        findSolutionMenuItem.setEnabled(false);
        findSolutionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findSolutionMenuItemActionPerformed(evt);
            }
        });
        solutionMenu.add(findSolutionMenuItem);

        boggleMenu.add(solutionMenu);

        jMenuBar1.add(boggleMenu);

        dictMenu.setText("Dictionary");
        dictMenu.setName("loadDict"); // NOI18N

        jMenuItem2.setText("Load");
        jMenuItem2.setName("loadDict"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadDictActionPerformed(evt);
            }
        });
        dictMenu.add(jMenuItem2);

        jMenuBar1.add(dictMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    
    private void loadDictActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadDictActionPerformed
    {//GEN-HEADEREND:event_loadDictActionPerformed
        this.loadAndSaveDictionaryForProgram();
    }//GEN-LAST:event_loadDictActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowOpened
    {//GEN-HEADEREND:event_formWindowOpened
        loadDictionaryFromFile();
    }//GEN-LAST:event_formWindowOpened

    
    private void displayAreaValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_displayAreaValueChanged
    {//GEN-HEADEREND:event_displayAreaValueChanged
        if (wordsFound.size() <= 0)
        {
            return;
        }
        
        // user selected word
        if (!evt.getValueIsAdjusting() && (this.displayArea.getModel().getSize() > 0))
        {
            this.boggleSolutionBoard1.repaintTable();
            // first path
            //System.out.println(wordsFound.get(displayArea.getSelectedValue()));
            
            List<List<Integer>> paths = wordsFound.get(displayArea.getSelectedValue());
            
            if (paths.size() > 1) {
                createPopup(paths);
            }
            else {
                showSolutionPath(paths.get(0));
                popup = null;
            }
        }
    }//GEN-LAST:event_displayAreaValueChanged

    private void boardFromFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boardFromFileMenuItemActionPerformed
        jSplitPane4.setTopComponent(null);
        this.loadBoardFromFile();
        jSplitPane4.setTopComponent(boggleSolutionBoard1);
        this.boggleSolutionBoard1.populateTableFromFileData(boggle);
        
        if (boggle != null)
        {
            this.findSolutionMenuItem.setEnabled(true);
        }
        
        this.timerLabel.setText("0 : 00");
        this.userInputTextField.setEnabled(false);
        this.findSolutionMenuItem.setEnabled(true);
        playTimeCounter.stop();
    }//GEN-LAST:event_boardFromFileMenuItemActionPerformed

    private void playMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playMenuItemActionPerformed
        jSplitPane4.setTopComponent(null);
        boggleBoard1 = new BoggleBoard();
        jSplitPane4.setTopComponent(boggleBoard1);
        this.userInputTextField.setEnabled(true);
        this.findSolutionMenuItem.setEnabled(false);
        playTimeCounter.stop();
        initBoggle();
    }//GEN-LAST:event_playMenuItemActionPerformed

    private void loadBoardFromUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBoardFromUserActionPerformed
        jSplitPane4.setTopComponent(null);
        jSplitPane4.setTopComponent(boggleSolutionBoard1);
        this.timerLabel.setText("0 : 00");
        this.userInputTextField.setEnabled(false);
        this.findSolutionMenuItem.setEnabled(true);
        playTimeCounter.stop();
    }//GEN-LAST:event_loadBoardFromUserActionPerformed

    private void findSolutionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findSolutionMenuItemActionPerformed
        findSolution();
    }//GEN-LAST:event_findSolutionMenuItemActionPerformed

    private void userInputTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userInputTextFieldKeyReleased
        if (this.invalidInput)
        {
            this.invalidInput = false;
            
            Document doc = this.userInputTextField.getDocument();
            try {
                doc.remove(doc.getLength()- 1, 1);
            } 
            catch (BadLocationException ex) {
                System.out.println("EXception: " + ex.getMessage());
            }
            
            String key = String.valueOf(Character.toUpperCase(evt.getKeyChar()));
        
            System.out.println(key + ": path not found");
        }
        
        
        // takes care of continuous key press/hold
        this.userInputTextField.setText(userInput.toString());
            
    }//GEN-LAST:event_userInputTextFieldKeyReleased

    String prev = "";
    private void userInputTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userInputTextFieldKeyPressed
        // ignore other keys such as combination/shortcuts for menu, 
        // non-alphabetic, except backspace and enter
        if ((!Character.isAlphabetic(evt.getKeyChar())) &&
                (evt.getKeyChar() != KeyEvent.VK_BACK_SPACE) &&
                (evt.getKeyCode() != KeyEvent.VK_ENTER))
        {
            return;
        }
        
        String key = String.valueOf(Character.toUpperCase(evt.getKeyChar()));
        
        if (key.equals("U") && prev.equals("Q"))
        {
            prev = "";
            this.invalidInput = false;
            userInput.append("U");
            return;
        }
        
        if (Character.isAlphabetic(evt.getKeyChar()))
        {
            // new entry
            if (userInput.length() == 0)
            {
                pathfinder = new PathFinder(g);
            }
        
       
            if (!pathfinder.find(key)) 
            {
                this.invalidInput = true;
                return;
            }
            
            prev = key;
            userInput.append(key);
            
            showWordPath(pathfinder.currentPath);
            
        }
        else if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)
        {
            if ((BoggleDemo.userInput.length() > 0) && (!this.userInputTextField.getText().isEmpty()))
            {
                char letter = BoggleDemo.userInput.charAt(BoggleDemo.userInput.length() - 1);
                BoggleDemo.userInput.deleteCharAt(BoggleDemo.userInput.length() - 1);
                
                if (letter != 'U') 
                        
                {
                    pathfinder.removeLastFromPath(letter);
                
                    if (pathfinder.currentPath.size() > 0)
                    {
                        pathfinder.currentPath.remove(pathfinder.currentPath.size() - 1);
                    }
                }
                
                
                showWordPath(pathfinder.currentPath);
                
                if (userInput.length() <= 0)
                {
                    pathfinder.paths.clear();
                }
            }
        }
         // if ENTER key is pressed
        else if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            userEnteredWords.put(userInput.toString(), userInput.toString());
            DefaultListModel model = (DefaultListModel) displayArea.getModel();
            model.addElement(userInput.toString());
            userInput = new StringBuilder();    // clear it
            this.userInputTextField.setText("");
            pathfinder.paths.clear();
            pathfinder.currentPath.clear();
            this.resetBoard();
        }
        
        //System.out.println("paths: " + pathfinder.paths);
        //System.out.println("current path: " + pathfinder.currentPath);
    }//GEN-LAST:event_userInputTextFieldKeyPressed

        
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Windows".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(BoggleDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(BoggleDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(BoggleDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(BoggleDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
               new BoggleDemo().setVisible(true);
            }
        });
    }
    
    private final boggle.BoggleSolutionBoard boggleSolutionBoard1 = new boggle.BoggleSolutionBoard();
    private final boggle.ResultDisplay resultDisplay1 = new boggle.ResultDisplay();
    public static PlayTimeCounter playTimeCounter;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem boardFromFileMenuItem;
    private boggle.BoggleBoard boggleBoard1;
    private boggle.BoggleIconPanel boggleIconPanel1;
    private javax.swing.JMenu boggleMenu;
    private javax.swing.JMenu dictMenu;
    private javax.swing.JList<String> displayArea;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenuItem findSolutionMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JMenuItem loadBoardFromUser;
    private javax.swing.JMenuItem playMenuItem;
    private javax.swing.JMenu solutionMenu;
    private javax.swing.JLabel timerLabel;
    private javax.swing.JTextField userInputTextField;
    // End of variables declaration//GEN-END:variables

}
