package ui;

import model.Network;
import model.Station;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class NetworkVisualizationWindow extends JFrame {
    private static final String WINDOW_TITLE = "Net Visualization";
    private static final int LABEL_OFFSET = 2; // px
    private static final float STROKE_WIDTH = 2; // px

    private JPanel panel;
    private JScrollPane visualPanel;

    private final StationNetworkManagerGUI networkManager;
    private int prevStationsHash;

    // EFFECTS: Create Network status window
    public NetworkVisualizationWindow(StationNetworkManagerGUI networkManager) {

        /* Field Assignments */
        this.networkManager = networkManager;
        prevStationsHash = networkManager.getStations().hashCode();

        /* UI Frame Settings */
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(StationNetworkManagerGUI.ICON_PATH).getImage());
        setResizable(false);
        setVisible(true);

        /* Components */
        setContentPane(panel);

        pack();

        // Update visual
        new Timer(StationNetworkManagerGUI.UPDATE_TIME_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Change detection
                if (networkManager.getStations().hashCode() != prevStationsHash) {
                    prevStationsHash = networkManager.getStations().hashCode();
                    panel.repaint();
                }
            }
        }).start();
    }

    @SuppressWarnings("methodlength")
    private void createUIComponents() {
        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                Graphics2D painter = (Graphics2D)g;

                Random randomColor = new Random();

                for (Station s : networkManager.getStations()) {

                    painter.setStroke(new BasicStroke(STROKE_WIDTH));
                    painter.setPaint(new Color(randomColor.nextInt(0xFFFFFF)));

                    // Center Point
                    painter.fillOval(
                            (int)(s.getPosX() - STROKE_WIDTH / 2 + Network.MAX_X),
                            (int)(s.getPosY() - STROKE_WIDTH / 2 + Network.MAX_Y),
                            (int)STROKE_WIDTH,
                            (int)STROKE_WIDTH
                    );
                    // ID Label
                    painter.drawString(
                            String.valueOf(s.getId()),
                            (int)(s.getPosX() - STROKE_WIDTH / 2 + Network.MAX_X + LABEL_OFFSET),
                            (int)(s.getPosY() - STROKE_WIDTH / 2 + Network.MAX_Y + LABEL_OFFSET)
                    );
                    // Radius Circle
                    if (!s.isOnline()) {
                        float[] dashed = {2, 0, 2};
                        painter.setStroke(new BasicStroke(
                                1,
                                BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_ROUND,
                                1,
                                dashed,
                                0));
                    }
                    painter.drawOval(
                            (int)(s.getPosX() - s.getCurrentSignalRadius() + Network.MAX_X),
                            (int)(s.getPosY() - s.getCurrentSignalRadius() + Network.MAX_Y),
                            (int)(s.getCurrentSignalRadius() * 2),
                            (int)(s.getCurrentSignalRadius() * 2)
                    );
                }
            }
        };
    }
}
