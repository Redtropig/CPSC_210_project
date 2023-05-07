package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NetworkStatusWindow extends JFrame {
    private static final String WINDOW_TITLE = "Net Stat";

    private JTextArea statusArea;
    private JPanel panel;

    private final StationNetworkManagerConsole networkManager;

    // EFFECTS: Create Network status window
    public NetworkStatusWindow(StationNetworkManagerConsole networkManager) {

        /* Field Assignments */
        this.networkManager = networkManager;

        /* UI Frame Settings */
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(StationNetworkManagerGUI.ICON_PATH).getImage());
        setResizable(false);
        setVisible(true);

        /* Components */
        setContentPane(panel);

        // Status updater
        new Timer(StationNetworkManagerGUI.UPDATE_TIME_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStatus();
                pack();
            }
        }).start();
    }

    // MODIFIES: this
    // EFFECTS: update Network status once
    private void updateStatus() {
        statusArea.setText(networkManager.getNetworkStatus());
    }
}
