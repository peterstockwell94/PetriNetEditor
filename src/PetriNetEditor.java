import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;

public class PetriNetEditor implements ActionListener {

    private PetriNetImpl pn;
    private String savefile;
    private File cf;

    private JFrame frame;

    // Sidebar
    private JTextField findTransition;
    private JButton btnFire;
    private JButton btnReset;

    // Status area
    public static JTextArea log;

    // Menus
    private JMenuBar menuBar;

    private JMenu mnFile;
    private JMenuItem mntmLoadPetriNet;
    private JMenuItem mntmSavePetriNet;
    private JMenuItem mntmPrintPetriNet;
    private JMenuItem mnClear;
    private JMenuItem mntmQuit;

    private JMenu mnEdit;
    private JMenuItem mntmAddPlace;
    private JMenuItem mntmAddTransition;
    private JMenuItem mntmAddArc;
    private JMenuItem mntmAddInitialArc;
    private JMenuItem mntmRemoveInitialArc;
    private JMenuItem mntmDelete;

    // Panels
    private JPanel simControlPanel;
    private JPanel statusPanel;
    private JPanel bottomPanel;
    private JPanel mainPanel;
    private EditorPanel editor;

    /**
     * Launch the Petri Net Editor
     */
    public static void main(String[] args) {
        new PetriNetEditor();
    }

    /**
     * Create the Petri Net Editor
     */
    public PetriNetEditor() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        pn = new PetriNetImpl();
        savefile = pn.toString();
        cf = new File(System.getProperty("user.dir"));

        initialise(pn);
    }

    /**
     * Initialize the contents of the frame
     */
    private void initialise(PetriNetImpl pn) {
        // Frame
        frame = new JFrame();
        frame.setTitle("Petri Net Editor");
        frame.setSize(675,600);
        frame.setPreferredSize(new Dimension(675,600));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Status area
        log = new JTextArea();
        log.setRows(5);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setEditable(false);
        ((DefaultCaret)log.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Editor
        editor = new EditorPanel(pn);
        pn.addListener(editor);

        // Simulation control panel
        simControlPanel = new JPanel();
        GridBagLayout gbl_simControlPanel = new GridBagLayout();
        gbl_simControlPanel.columnWidths = new int[] {50, 50};
        gbl_simControlPanel.rowHeights = new int[] {40, 60, 0};
        gbl_simControlPanel.columnWeights = new double[]{0.0, 0.0};
        gbl_simControlPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        simControlPanel.setLayout(gbl_simControlPanel);

        // Transition text field
        findTransition = new JTextField("Transition");
        GridBagConstraints gbc_searchTransition = new GridBagConstraints();
        gbc_searchTransition.fill = GridBagConstraints.BOTH;
        gbc_searchTransition.gridwidth = 2;
        gbc_searchTransition.insets = new Insets(0, 0, 5, 0);
        gbc_searchTransition.gridx = 0;
        gbc_searchTransition.gridy = 0;
        simControlPanel.add(findTransition, gbc_searchTransition);
        findTransition.setColumns(5);
        findTransition.addActionListener(this);

        // Fire button
        btnFire = new JButton("Fire Transition");
        btnFire.addActionListener(this);
        GridBagConstraints gbc_btnFire = new GridBagConstraints();
        gbc_btnFire.fill = GridBagConstraints.BOTH;
        gbc_btnFire.gridx = 1;
        gbc_btnFire.gridy = 1;
        simControlPanel.add(btnFire, gbc_btnFire);

        // Status panel
        statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(log);
        statusPanel.add(scrollPane, BorderLayout.CENTER);

        // Sim control + status panels
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(simControlPanel, BorderLayout.WEST);

        // Reset button
        btnReset = new JButton("Reset Simulation");
        btnReset.addActionListener(this);
        GridBagConstraints gbc_btnReset = new GridBagConstraints();
        gbc_btnReset.insets = new Insets(0, 0, 0, 5);
        gbc_btnReset.fill = GridBagConstraints.BOTH;
        gbc_btnReset.gridx = 0;
        gbc_btnReset.gridy = 1;
        simControlPanel.add(btnReset, gbc_btnReset);
        bottomPanel.add(statusPanel, BorderLayout.CENTER);

        // Main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(editor, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // mainPanel.addMouseListener(this);
        editor.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                findTransition.setText(editor.lastSelectedTransition);
            }

            public void mouseClicked(MouseEvent e) {
                findTransition.setText(editor.lastSelectedTransition);
            }
        });

        // Menu bar
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // File menu
        mnFile            = new JMenu("File");
        mntmLoadPetriNet  = new JMenuItem("Load Petri Net");
        mntmSavePetriNet  = new JMenuItem("Save Petri Net");
        mntmPrintPetriNet = new JMenuItem("Print Petri Net");
        mnClear           = new JMenuItem("Clear Log");
        mntmQuit          = new JMenuItem("Quit");

        mntmLoadPetriNet.addActionListener(this);
        mntmSavePetriNet.addActionListener(this);
        mntmPrintPetriNet.addActionListener(this);
        mnClear.addActionListener(this);
        mntmQuit.addActionListener(this);

        menuBar.add(mnFile);
        mnFile.add(mntmLoadPetriNet);
        mnFile.add(mntmSavePetriNet);
        mnFile.add(mntmPrintPetriNet);
        mnFile.add(mnClear);
        mnFile.add(mntmQuit);

        // Edit menu
        mnEdit               = new JMenu("Edit");
        mntmAddPlace         = new JMenuItem("Add Place");
        mntmAddTransition    = new JMenuItem("Add Transition");
        mntmAddArc           = new JMenuItem("Add Arc");
        mntmAddInitialArc    = new JMenuItem("Add Initial Arc");
        mntmRemoveInitialArc = new JMenuItem("Remove Initial Arc");
        mntmDelete           = new JMenuItem("Delete");

        mnEdit              .addActionListener(this);
        mntmAddPlace        .addActionListener(this);
        mntmAddTransition   .addActionListener(this);
        mntmAddArc          .addActionListener(this);
        mntmAddInitialArc   .addActionListener(this);
        mntmRemoveInitialArc.addActionListener(this);
        mntmDelete          .addActionListener(this);

        menuBar.add(mnEdit);
        mnEdit.add(mntmAddPlace);
        mnEdit.add(mntmAddTransition);
        mnEdit.add(mntmAddArc);
        mnEdit.add(mntmAddInitialArc);
        mnEdit.add(mntmRemoveInitialArc);
        mnEdit.add(mntmDelete);

        /*
        // Debug
        JMenuItem mnLoadSimple;
        mnLoadSimple = new JMenuItem("Load simple.pn");
        mnLoadSimple.addActionListener(this);
        menuBar.add(mnLoadSimple);
        */

        log("Petri Net Editor Initialised", false);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        log(action);
        switch (action) {
            case "Clear Log": log.setText(null); break;

            case "Print Petri Net": log(pn.toString()); break;

            case "Load simple.pn":
                {
                    cf = new File("./pn/simple.pn");
                    try {
                        BufferedReader r = new BufferedReader(new FileReader(cf));
                        PetriNetReaderWriter prw = new PetriNetReaderWriter();
                        PetriNetImpl pi = new PetriNetImpl();
                        pi.addListener(editor);
                        prw.read(r, pi);
                        log("Loaded " + cf.getName());
                        pn = pi;
                        editor.setPetriNet(pi);
                    } catch (PetriNetFormatException pfe) {
                        log(pfe.toString());
                        JOptionPane.showMessageDialog(frame, pfe, "Error Loading File", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception e) {
                        log(e.toString());
                    }
                }
                break;

            case "Save Petri Net":
                {
                    JFileChooser fc = new JFileChooser(cf);
                    fc.setDialogTitle("Save Petri Net to a file");

                    FileNameExtensionFilter pnFilter = new FileNameExtensionFilter("pn files (*.pn)", "pn");
                    fc.setFileFilter(pnFilter);

                    if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            PrintWriter w = new PrintWriter(file.getPath());
                            PetriNetReaderWriter prw = new PetriNetReaderWriter();
                            log(pn.toString());
                            savefile = pn.toString();
                            prw.write(w, pn);
                            w.close();
                            cf = file;
                            log("Saved " + cf.getName());
                        } catch (IOException e) {
                            log(e.toString());
                        }
                    }
                }
                break;

            case "Load Petri Net":
                {
                    if (!pn.toString().equals(savefile)) {
                        int confirm = JOptionPane.showConfirmDialog(frame, "Unsaved changes in Petri Net, load new file anyway?", "Unsaved Changes", JOptionPane.YES_NO_OPTION);
                        if (confirm != JOptionPane.YES_OPTION) break;
                    }
                    // System.out.println(pn.toString());
                    // System.out.println(savefile);
                    JFileChooser fc = new JFileChooser(cf);
                    fc.setDialogTitle("Load Petri Net from a file");

                    FileNameExtensionFilter pnFilter = new FileNameExtensionFilter("pn files (*.pn)", "pn");
                    fc.setFileFilter(pnFilter);

                    if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        cf = fc.getSelectedFile();
                        try {
                            BufferedReader r = new BufferedReader(new FileReader(cf));
                            PetriNetReaderWriter prw = new PetriNetReaderWriter();
                            PetriNetImpl pi = new PetriNetImpl();
                            pi.addListener(editor);
                            prw.read(r, pi);
                            pn = pi;
                            savefile = pi.toString();
                            editor.setPetriNet(pi);
                            log(pn.toString());
                            log("Loaded " + cf.getName(), false);
                        } catch (PetriNetFormatException pfe) {
                            log(pfe.getMessage());
                            JOptionPane.showMessageDialog(frame, pfe.getMessage(), "Error Loading File", JOptionPane.ERROR_MESSAGE);
                        } catch (Exception e) {
                            log(e.toString());
                        }
                    }
                }
                break;

            case "Quit": frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)); break;

            case "Reset Simulation":
                {
                    pn.simInitialise();
                    log("Simulation Initialised");
                    LinkedList<Transition> enabled = new LinkedList<Transition>(pn.simEnabledTransitions());
                    if (!enabled.isEmpty() && !enabled.contains(pn.findTransition(findTransition.getText()))) findTransition.setText(enabled.getFirst().getName());
                }
                break;

            case "Fire Transition":
                Transition t = pn.findTransition(findTransition.getText());
                try {
                    pn.simFire(t);
                    log("Transition \"" + findTransition.getText() + "\" fired successfully");
                    LinkedList<Transition> enabled = new LinkedList<Transition>(pn.simEnabledTransitions());
                    if (!enabled.isEmpty() && !enabled.contains(pn.findTransition(findTransition.getText()))) findTransition.setText(enabled.getFirst().getName());
                } catch (IllegalArgumentException | NullPointerException e) {
                    log(e.getMessage());
                    JOptionPane.showMessageDialog(frame, e.getMessage(), "Error Firing Transition", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "Add Initial Arc":
                if (editor.selectedPlaces().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No Places have been selected to add an Initial Arc to.", "Invalid Selection", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                String weight = (String) JOptionPane.showInputDialog(frame, "Enter the desired weight of the Initial Arc:", "Add Initial Arc", JOptionPane.PLAIN_MESSAGE);
                if (weight != null) {
                    try {
                        int w = Integer.parseInt(weight);
                        for (PlaceIcon pi : editor.selectedPlaces()) {
                            pn.addInitialArc(pi.getPlace(), w);
                            log("Initial Arc with weight " + w + " added to Place: " + pi.getPlace().getName());
                        }
                    } catch (IllegalArgumentException e) {
                        log("Error Adding Initial Arc: \"" + weight + "\" is not a valid integer");
                        actionPerformed(event);
                        break;
                    }
                }
                break;

            case "Remove Initial Arc":
                if (editor.selectedPlaces().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No Places have been selected to remove an Initial Arc from.", "Invalid Selection", JOptionPane.ERROR_MESSAGE);
                    break;
                }
                for (PlaceIcon pi : editor.selectedPlaces()) {
                    if (pi.getPlace().getInitialArcWeight() > 0) {
                        pn.removeInitialArc(pi.getPlace());
                        log("Initial Arc removed from Place: " + pi.getPlace().getName());
                    } else {
                        log(pi.getPlace().getName() + " didn't have an Initial Arc to remove");
                    }
                }
                break;

            case "Add Place":
                try {
                    String newPlaceName = (String) JOptionPane.showInputDialog(frame, "Enter the desired name of the Place:", "Add Place", JOptionPane.PLAIN_MESSAGE);
                    if (newPlaceName == null || newPlaceName.equals("")) break;
                    log("Added Place: " + pn.newPlace(newPlaceName, editor.getWidth()/2, editor.getHeight()/2));
                } catch (IllegalArgumentException e) {
                    log(e.getMessage());
                    JOptionPane.showMessageDialog(frame, e.getMessage(), "Error Adding Place", JOptionPane.ERROR_MESSAGE);
                    actionPerformed(event);
                }
                break;

            case "Add Transition":
                try {
                    String newTransitionName = (String) JOptionPane.showInputDialog(frame, "Enter the desired name of the Transition:", "Add Transition", JOptionPane.PLAIN_MESSAGE);
                    if (newTransitionName == null || newTransitionName.equals("")) break;
                    log("Added Transition: " + pn.newTransition(newTransitionName, editor.getWidth()/2, editor.getHeight()/2));
                } catch (IllegalArgumentException e) {
                    log(e.getMessage());
                    JOptionPane.showMessageDialog(frame, e.getMessage(), "Error Adding Transition", JOptionPane.ERROR_MESSAGE);
                    actionPerformed(event);
                }
                break;

            case "Delete":
                Set<TransitionIcon> selectedTransitions = editor.selectedTransitions();
                for (PlaceIcon pi : editor.selectedPlaces()) {
                    pn.removePlace(pi.getPlace());
                    log("Removed Place: " + pi.getPlace());
                }
                for (TransitionIcon ti : selectedTransitions) {
                    pn.removeTransition(ti.getTransition());
                    log("Removed Transition: " + ti.getTransition());
                }
                break;

            case "Add Arc": editor.addArc(); break;

            default: break;

        }
    }

    public static void log(String str, boolean newLine) {
        if (newLine) str = "\n" + str;
        System.out.print(str);
        log.append(str);
    }

    public static void log(String str) {
        log(str, true);
    }
}
