package ui;

import model.Event;
import model.EventLog;
import model.Network;
import model.Station;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Locale;

public class StationNetworkManagerGUI extends StationNetworkManagerConsole {

    protected static final int UPDATE_TIME_INTERVAL = 100; // ms
    protected static final String ICON_PATH = "./data/programIcon.png";
    private static final String PREVIEW_SIGNAL_RADIUS_TO = "->";
    private static final int SLIDER_MAJOR_TICK_NUM = 3;
    private static final int SLIDER_MINOR_TICK_NUM = 9;
    private static final double DEFAULT_STATION_SIGNAL_RADIUS = Station.MIN_SIGNAL_RADIUS;
    private static final String PROGRAM_TITLE = "Station Network Manager";
    private static final Dimension WINDOW_SIZE_MIN = new Dimension(600, 615);

    private JPanel panel;
    private JTextArea consoleArea;
    private JScrollPane consoleScrollPanel;
    private JTabbedPane sourceStationPanel;
    private JSplitPane twinStationsPanel;
    private JTabbedPane destinationStationPanel;
    private JTextArea sourceStatusArea;
    private JTextArea destinationStatusArea;
    private JComboBox<Station> sourceSelector;
    private JComboBox<Station> destinationSelector;
    private JToolBar addStationTool;
    private JTextField inputStationId;
    private JTextField inputStationX;
    private JTextField inputStationY;
    private JButton btnAddStation;
    private JButton btnReachable;
    private JButton btnPathLeastHops;
    private JButton btnPhyDistance;
    private JButton btnSendMsg;
    private JToolBar networkTools;
    private JButton btnRmvSrcStation;
    private JButton btnRmvDestStation;
    private JCheckBox cbSrcOn;
    private JCheckBox cbDestOn;
    private JSlider sldSrcSignalR;
    private JSlider sldDestSignalR;
    private JLabel previewDestSignalR;
    private JLabel previewSrcSignalR;
    private JButton btnSrcInbox;
    private JButton btnDestInbox;
    private JMenuBar menuBar;

    private File currentFile;

    // EFFECTS: instantiate a network manager (GUI)
    @SuppressWarnings("methodlength")
    public StationNetworkManagerGUI() {
        /* Global Settings */
        java.util.Locale.setDefault(Locale.ENGLISH);
        // redirect System out & err PrintStream
        OutputStream outGUI = new OutputStream() {
            @Override
            public void write(int b) {
                updateConsole(String.valueOf((char)b));
            }
        };
        PrintStream printGUI = new PrintStream(outGUI, true);
        redirectSystemOutErrStream(printGUI, printGUI);

        /* Field Assignments */
        currentFile = new File(DEFAULT_SAVE_PATH);

        /* UI Frame Settings */
        setTitle(PROGRAM_TITLE);
        setMinimumSize(WINDOW_SIZE_MIN);
        setPreferredSize(WINDOW_SIZE_MIN);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon(ICON_PATH).getImage());
        setResizable(false);
        setVisible(true);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                // restore System out & err PrintStream
                redirectSystemOutErrStream(DEFAULT_SYS_OUT, DEFAULT_SYS_ERR);

                // Print Log
                System.out.println("Event Log:");
                for (Event event : EventLog.getInstance()) {
                    System.out.println(event);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        /* Components */
        createUIComponents();
        setJMenuBar(menuBar);
        setContentPane(panel);

        pack();
    }

    // MODIFIES: this
    // EFFECTS: manually manage some GUI components
    @SuppressWarnings("methodlength")
    private void createUIComponents() {
        createConsoleArea();
        createMenuBar();
        // Source Station
        createStationArea(
                sourceSelector,
                sourceStatusArea,
                btnRmvSrcStation,
                cbSrcOn,
                sldSrcSignalR,
                previewSrcSignalR,
                btnSrcInbox
        );
        // Destination Station
        createStationArea(
                destinationSelector,
                destinationStatusArea,
                btnRmvDestStation,
                cbDestOn,
                sldDestSignalR,
                previewDestSignalR,
                btnDestInbox
        );
        createAddStationToolbar();
        createNetworkToolbar();
    }

    // MODIFIES: this
    // EFFECTS: create console area
    private void createConsoleArea() {
        JScrollBar verticalScrollBar = consoleScrollPanel.getVerticalScrollBar();

        verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                // Scroll bar max changed (typically console was updated)
                if (!String.valueOf(verticalScrollBar.getMaximum()).equals(verticalScrollBar.getName())) {
                    // Update viewport of console to bottom
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                    // ScrollBar.max.toString() -> ScrollBar.name
                    verticalScrollBar.setName(String.valueOf(verticalScrollBar.getMaximum()));
                }
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: create Network tools
    @SuppressWarnings("methodlength")
    private void createNetworkToolbar() {
        btnReachable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!areSrcDestStationsBothSelected()) {
                    return;
                }

                assert sourceSelector.getSelectedItem() != null;
                assert destinationSelector.getSelectedItem() != null;
                int srcStationId = Integer.parseInt(sourceSelector.getSelectedItem().toString());
                int destStationId = Integer.parseInt(destinationSelector.getSelectedItem().toString());

                // Check Reachable
                JOptionPane.showMessageDialog(
                        panel,
                        String.valueOf(isReachable(srcStationId, destStationId)).toUpperCase(),
                        "Reachable Check",
                        JOptionPane.INFORMATION_MESSAGE,
                        null
                );
            }
        });
        btnPathLeastHops.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!areSrcDestStationsBothSelected()) {
                    return;
                }

                assert sourceSelector.getSelectedItem() != null;
                assert destinationSelector.getSelectedItem() != null;
                int srcStationId = Integer.parseInt(sourceSelector.getSelectedItem().toString());
                int destStationId = Integer.parseInt(destinationSelector.getSelectedItem().toString());

                // Get Path with the Least Hops
                JOptionPane.showMessageDialog(
                        panel,
                        getPathStringWithLeastHops(srcStationId, destStationId),
                        "Path Least Hops",
                        JOptionPane.INFORMATION_MESSAGE,
                        null
                );
            }
        });
        btnPhyDistance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!areSrcDestStationsBothSelected()) {
                    return;
                }

                assert sourceSelector.getSelectedItem() != null;
                assert destinationSelector.getSelectedItem() != null;
                int srcStationId = Integer.parseInt(sourceSelector.getSelectedItem().toString());
                int destStationId = Integer.parseInt(destinationSelector.getSelectedItem().toString());

                // Get Physical Distance between src & dest
                JOptionPane.showMessageDialog(
                        panel,
                        String.format(
                                "#%d <-> #%d = %.2f",
                                srcStationId,
                                destStationId,
                                getPhysicalDistanceTwoStations(srcStationId, destStationId)),
                        "Physical Distance",
                        JOptionPane.INFORMATION_MESSAGE,
                        null
                );
            }
        });
        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!areSrcDestStationsBothSelected()) {
                    return;
                }

                assert sourceSelector.getSelectedItem() != null;
                assert destinationSelector.getSelectedItem() != null;
                int srcStationId = Integer.parseInt(sourceSelector.getSelectedItem().toString());
                int destStationId = Integer.parseInt(destinationSelector.getSelectedItem().toString());

                // Send Msg
                String content = (String) JOptionPane.showInputDialog(
                        panel,
                        "Msg Content:",
                        "Send Message",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        null
                );
                if (content != null) {
                    sendMessage(srcStationId, destStationId, content);
                }
            }
        });
    }

    // MODIFIES: none
    // EFFECTS: @return true -> src & dest Stations are both selected, otherwise false
    //          With System.err print feedback
    private boolean areSrcDestStationsBothSelected() {
        return isStationSelected(sourceSelector) && isStationSelected(destinationSelector);
    }

    // MODIFIES: none
    // EFFECTS: @return true -> Station in stationSelector is selected, otherwise false
    //          With System.err print feedback
    private boolean isStationSelected(JComboBox<Station> stationSelector) {
        if (stationSelector.getSelectedItem() == null) {
            System.err.println("Please select a " + stationSelector.getName() + " Station!");
            return false;
        }
        return true;
    }

    // MODIFIES: this
    // EFFECTS: create add Station tool
    @SuppressWarnings("methodlength")
    private void createAddStationToolbar() {
        btnAddStation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int stationId;
                double posX;
                double posY;

                // Parse stationId (int)
                try {
                    stationId = Integer.parseInt(inputStationId.getText());
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid [stationID]!");
                    return;
                }

                // Parse posX & posY (double)
                try {
                    posX = Double.parseDouble(inputStationX.getText());
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid [posX]!");
                    return;
                }
                try {
                    posY = Double.parseDouble(inputStationY.getText());
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid [posY]!");
                    return;
                }

                // Add Station & If succeeded
                if (addNetworkStation(stationId, posX, posY, DEFAULT_STATION_SIGNAL_RADIUS)) {
                    // Clear input fields
                    inputStationId.setText(null);
                    inputStationX.setText(null);
                    inputStationY.setText(null);
                }
            }
        });
    }

    // REQUIRES: stationSelector != null
    // MODIFIES: this
    // EFFECTS: create Station display area
    @SuppressWarnings("methodlength")
    private void createStationArea(
            JComboBox<Station> stationSelector,
            JTextArea statusArea,
            JButton btnRmvStation,
            JCheckBox cbStaOn,
            JSlider sldStaSignalR,
            JLabel previewSignalR,
            JButton btnInbox) {
        /* Station Selector */
        // Update Station popup list
        stationSelector.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                updateStationSelectorList(stationSelector);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        /* Station Status Area */
        // Auto update statusArea
        new Timer(UPDATE_TIME_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selectedObj = stationSelector.getSelectedItem();
                if (selectedObj == null) {
                    return;
                }

                Station selectedStation = (Station) selectedObj;
                // Detect Station state change
                // statusArea.name stores the hashCode of the prev state of Station
                if (!String.valueOf(selectedStation.hashCode()).equals(statusArea.getName())) {
                    updateStationStatusArea(statusArea, selectedStation);
                }
            }
        }).start();

        /* Station Remove Button */
        btnRmvStation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isStationSelected(stationSelector)) {
                    assert stationSelector.getSelectedItem() != null;
                    int stationId = Integer.parseInt(stationSelector.getSelectedItem().toString());

                    removeStationById(stationId);
                    // Update Station Selectors
                    updateStationSelectorList(sourceSelector);
                    updateStationSelectorList(destinationSelector);
                }
            }
        });

        /* Station Online CheckBox */
        new Timer(UPDATE_TIME_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stationSelector.getSelectedItem() != null) {
                    cbStaOn.setEnabled(true);
                    cbStaOn.setSelected(((Station)stationSelector.getSelectedItem()).isOnline());
                } else {
                    cbStaOn.setEnabled(false);
                }
            }
        }).start();
        cbStaOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isStationSelected(stationSelector)) {
                    assert stationSelector.getSelectedItem() != null;
                    int stationId = Integer.parseInt(stationSelector.getSelectedItem().toString());

                    setStationNetStat(stationId, cbStaOn.isSelected());
                }
            }
        });

        /* Station Signal Radius Slider */
        sldStaSignalR.setMaximum((int)Math.floor(Station.MAX_SIGNAL_RADIUS));
        sldStaSignalR.setMinimum((int)Math.ceil(Station.MIN_SIGNAL_RADIUS));
        sldStaSignalR.setMajorTickSpacing(
                (sldStaSignalR.getMaximum() - sldStaSignalR.getMinimum()) / SLIDER_MAJOR_TICK_NUM
        );
        sldStaSignalR.setMinorTickSpacing(
                (sldStaSignalR.getMaximum() - sldStaSignalR.getMinimum()) / SLIDER_MINOR_TICK_NUM
        );
        previewSignalR.setText(PREVIEW_SIGNAL_RADIUS_TO);
        new Timer(UPDATE_TIME_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stationSelector.getSelectedItem() != null) {
                    sldStaSignalR.setEnabled(true);

                    int radius = (int) ((Station) stationSelector.getSelectedItem()).getCurrentSignalRadius();
                    // Station signal radius changed -> update slider
                    if (!String.valueOf(radius).equals(sldStaSignalR.getName())) {
                        sldStaSignalR.setName(String.valueOf(radius));
                        sldStaSignalR.setValue(radius);
                    }
                } else {
                    sldStaSignalR.setEnabled(false);
                }
            }
        }).start();
        sldStaSignalR.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Show preview while adjusting
                if (sldStaSignalR.getValueIsAdjusting()) {
                    previewSignalR.setText(PREVIEW_SIGNAL_RADIUS_TO + sldStaSignalR.getValue());
                }

                // Released slider thumb -> set new signal radius
                if (!sldStaSignalR.getValueIsAdjusting() && isStationSelected(stationSelector)) {
                    assert stationSelector.getSelectedItem() != null;

                    int stationCurrentSigR = (int)((Station)stationSelector.getSelectedItem()).getCurrentSignalRadius();
                    // Station.signalRadius != Slider.value -> update Station's
                    if (stationCurrentSigR != sldStaSignalR.getValue()) {
                        int stationId = Integer.parseInt(stationSelector.getSelectedItem().toString());

                        setStationSignalRadiusById(stationId, sldStaSignalR.getValue());
                        previewSignalR.setText(PREVIEW_SIGNAL_RADIUS_TO);
                    }
                }
            }
        });

        /* Station Inbox Button */
        btnInbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isStationSelected(stationSelector)) {
                    assert stationSelector.getSelectedItem() != null;
                    int stationId = Integer.parseInt(stationSelector.getSelectedItem().toString());
                    JOptionPane.showMessageDialog(
                            panel,
                            getInboxStatus(stationId),
                            "Inbox",
                            JOptionPane.INFORMATION_MESSAGE,
                            null
                    );
                }
            }
        });
    }

    // REQUIRES: statusArea, selectedStation != null
    // MODIFIES: this
    // EFFECTS: update Station status area
    private void updateStationStatusArea(JTextArea statusArea, Station selectedStation) {
        statusArea.setText(getStationStatus(selectedStation.getId()));
        // Update Station state record (hashCode)
        statusArea.setName(String.valueOf(selectedStation.hashCode()));
    }

    // REQUIRES: stationSelector != null
    // MODIFIES: this
    // EFFECTS: update Station selector popup list
    private void updateStationSelectorList(JComboBox<Station> stationSelector) {
        Station selected = (Station) stationSelector.getSelectedItem();

        stationSelector.removeAllItems();
        for (Station s : getStations()) {
            stationSelector.addItem(s);
        }

        // Maintain the last selection after list updated (if that selection still exists)
        stationSelector.setSelectedItem(selected);
    }

    // MODIFIES: this
    // EFFECTS: create JMenuBar
    @SuppressWarnings("methodlength")
    private void createMenuBar() {
        menuBar = new JMenuBar();

        /* File Menu */
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Menu items
        JMenuItem loadNetwork = new JMenuItem("Load");
        JMenuItem saveNetwork = new JMenuItem("Save");

        // Shortcuts
        fileMenu.setMnemonic(KeyEvent.VK_F);
        loadNetwork.setMnemonic(KeyEvent.VK_L);
        saveNetwork.setMnemonic(KeyEvent.VK_S);

        // Listeners
        loadNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser loadFileChooser = new JFileChooser();

                // Only accept ".json" file
                loadFileChooser.setAcceptAllFileFilterUsed(false);
                loadFileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        // Directory
                        if (f.isDirectory()) {
                            return true;
                        }
                        // File
                        return f.getName().toLowerCase().endsWith(".json");
                    }

                    @Override
                    public String getDescription() {
                        return "JSON File (*.json)";
                    }
                });
                // Default directory
                loadFileChooser.setCurrentDirectory(currentFile);
                // Default selected file
                if (currentFile.exists()) {
                    loadFileChooser.setSelectedFile(currentFile);
                }

                // Loading
                if (loadFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    currentFile = loadFileChooser.getSelectedFile();
                    loadNetwork(currentFile.getAbsolutePath());
                    updateTitle();
                }
            }
        });
        saveNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveNetwork(currentFile.getAbsolutePath());
                updateTitle();
            }
        });

        fileMenu.add(loadNetwork);
        fileMenu.add(saveNetwork);

        /* Net menu */
        JMenu netMenu = new JMenu("Net");
        menuBar.add(netMenu);

        // Menu items
        JMenuItem renameNet = new JMenuItem("Rename");
        JMenuItem netStatus = new JMenuItem("Status");
        JMenuItem visualize = new JMenuItem("Visualize");

        // Shortcuts
        netMenu.setMnemonic(KeyEvent.VK_N);
        renameNet.setMnemonic(KeyEvent.VK_R);
        netStatus.setMnemonic(KeyEvent.VK_S);
        visualize.setMnemonic(KeyEvent.VK_V);

        // Listeners
        renameNet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = (String) JOptionPane.showInputDialog(
                        panel,
                        "New network name:",
                        "Rename",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        Network.DEFAULT_NETWORK_NAME
                );
                if (name != null && !name.isEmpty()) {
                    renameNetwork(name);
                }
            }
        });
        netStatus.addActionListener(e -> new NetworkStatusWindow(this));
        visualize.addActionListener(e -> new NetworkVisualizationWindow(this));

        netMenu.add(renameNet);
        netMenu.add(netStatus);
        netMenu.add(visualize);

        /* Help menu */
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        // Menu items
        JMenuItem getCommandList = new JMenuItem("Commands");

        // Shortcuts
        helpMenu.setMnemonic(KeyEvent.VK_H);
        getCommandList.setMnemonic(KeyEvent.VK_C);

        // Listeners
        getCommandList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCommandHelp();
                System.out.println();
            }
        });

        helpMenu.add(getCommandList);
    }

    // MODIFIES: this
    // EFFECTS: print outputs on GUI console
    private void updateConsole(String output) {
        consoleArea.append(output);
    }

    // MODIFIES: System.out & System.err
    // EFFECTS: redirect System output streams
    private void redirectSystemOutErrStream(PrintStream out, PrintStream err) {
        System.setOut(out);
        System.setErr(err);
    }

    // MODIFIES: this
    // EFFECTS: update window title to current save file name
    private void updateTitle() {
        setTitle(currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')));
    }

    // MODIFIES: this
    // EFFECTS: do something in the main thread behind the scene
    @Override
    protected void runNetworkManager() {
        printProgramInfo();
    }

}
