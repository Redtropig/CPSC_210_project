package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {
    private Network network1;

    @BeforeEach
    void setUp() {
        network1 = new Network();

        String expectedPrint = "Network: " + Network.DEFAULT_NETWORK_NAME + "\n" +
                "Total Stations: 0\n" +
                "[]\n" +
                "Online: 0\n" +
                "[]\n";
        assertEquals(expectedPrint, network1.getNetworkStatus());

        try {
            network1.addStation(1, 0, 0, 100);
            network1.addStation(2, 90, 0, 50);
            network1.addStation(3, 130, 0, 25);
            network1.addStation(4, 0, 90, 90 * 1.414 + 2);
            network1.addStation(5, -114.514, 114.514, 11.45);
            network1.addStation(6, 100, 0, 11);
            network1.addStation(8, 110, 0, 15);
            network1.addStation(7, 123, 0, 9);
        } catch (Exception e) {
            fail();
        }

        expectedPrint = "Network: " + Network.DEFAULT_NETWORK_NAME + "\n" +
                "Total Stations: 8\n" +
                "[1, 2, 3, 4, 5, 6, 7, 8]\n" +
                "Online: 8\n" +
                "[1, 2, 3, 4, 5, 6, 7, 8]\n";
        assertEquals(expectedPrint, network1.getNetworkStatus());
    }

    @Test
    void testSetName() {
        network1.setName("MyNet");

        String expectedPrint = "Network: MyNet\n" +
                "Total Stations: 8\n" +
                "[1, 2, 3, 4, 5, 6, 7, 8]\n" +
                "Online: 8\n" +
                "[1, 2, 3, 4, 5, 6, 7, 8]\n";
        assertEquals(expectedPrint, network1.getNetworkStatus());
    }

    @Test
    void testAddStation() {
        String expectedPrint;

        // Legal add
        try {
            network1.addStation(11, 4.4, -5.5, 1.4);
            expectedPrint = "Network: " + Network.DEFAULT_NETWORK_NAME + "\n" +
                    "Total Stations: 9\n" +
                    "[1, 2, 3, 4, 5, 6, 7, 8, 11]\n" +
                    "Online: 9\n" +
                    "[1, 2, 3, 4, 5, 6, 7, 8, 11]\n";
            assertEquals(expectedPrint, network1.getNetworkStatus());
        } catch (Exception e) {
            fail();
        }

        // stationId <= 0
        try {
            network1.addStation(0, 11, 11, 1);
            fail();
        } catch (Exception e) {
            assertEquals("Station ID must > 0\n", e.getMessage());
        }
        try {
            network1.addStation(-1, 11, 11, 1);
            fail();
        } catch (Exception e) {
            assertEquals("Station ID must > 0\n", e.getMessage());
        }

        // signalRadius <= 0
        try {
            network1.addStation(11, 11, 11, 0);
            fail();
        } catch (Exception e) {
            assertEquals("Signal Radius must > 0\n", e.getMessage());
        }
        try {
            network1.addStation(11, 11, 11, -1);
            fail();
        } catch (Exception e) {
            assertEquals("Signal Radius must > 0\n", e.getMessage());
        }

        // out of service range of X,Y
        expectedPrint = "Out of Service Range!\n"
                + "X must within [" + Network.MIN_X + "," + Network.MAX_X + "]\n"
                + "Y must within [" + Network.MIN_Y + "," + Network.MAX_Y + "]\n";
        try {
            network1.addStation(11, Network.MIN_X - 1, 11, 11);
            fail();
        } catch (Exception e) {
            assertEquals(expectedPrint, e.getMessage());
        }
        try {
            network1.addStation(11, Network.MAX_X + 1, 11, 11);
            fail();
        } catch (Exception e) {
            assertEquals(expectedPrint, e.getMessage());
        }
        try {
            network1.addStation(11, 11, Network.MIN_Y - 1, 11);
            fail();
        } catch (Exception e) {
            assertEquals(expectedPrint, e.getMessage());
        }
        try {
            network1.addStation(11, 11, Network.MAX_Y + 1, 11);
            fail();
        } catch (Exception e) {
            assertEquals(expectedPrint, e.getMessage());
        }

        // stationId already exists
        expectedPrint = "Station ID #2 has already exist!\n";
        try {
            network1.addStation(2, 11, 11, 11);
            fail();
        } catch (Exception e) {
            assertEquals(expectedPrint, e.getMessage());
        }
    }

    @Test
    void testRemoveStation() {
        String expectedPrint;

        // Legal remove
        assertTrue(network1.removeStation(5));
        expectedPrint = "Network: " + Network.DEFAULT_NETWORK_NAME + "\n" +
                "Total Stations: 7\n" +
                "[1, 2, 3, 4, 6, 7, 8]\n" +
                "Online: 7\n" +
                "[1, 2, 3, 4, 6, 7, 8]\n";
        assertEquals(expectedPrint, network1.getNetworkStatus());

        // Station does not exist
        assertFalse(network1.removeStation(38));
        expectedPrint = "Network: " + Network.DEFAULT_NETWORK_NAME + "\n" +
                "Total Stations: 7\n" +
                "[1, 2, 3, 4, 6, 7, 8]\n" +
                "Online: 7\n" +
                "[1, 2, 3, 4, 6, 7, 8]\n";
        assertEquals(expectedPrint, network1.getNetworkStatus());
    }

    @Test
    void testGetPhysicalDistance() {
        String result;

        // Legal get distance
        result = String.format("%.2f", network1.getPhysicalDistance(4, 5));
        assertEquals("117.11", result);
        result = String.format("%.2f", network1.getPhysicalDistance(1, 5));
        assertEquals("161.95", result);
        result = String.format("%.2f", network1.getPhysicalDistance(1, 1));
        assertEquals("0.00", result);

        // Either/Both of stations do not exist
        assertEquals(-1, network1.getPhysicalDistance(-1, 8));
        assertEquals(-1, network1.getPhysicalDistance(34, 2));
        assertEquals(-1, network1.getPhysicalDistance(2, 34));
        assertEquals(-1, network1.getPhysicalDistance(11, 45));
    }

    @Test
    void testSendMessage() {
        Message result;
        String expectedPrint;

        // Delivered
        result = network1.sendMessage(1, 3, "Hell o!");
        assertNotNull(result);
        assertEquals("Hell o!", result.getContent());
        assertEquals(2, result.getHopCount());
        assertEquals(3, result.getCurrentStationId());
        assertEquals(3, result.getToStationId());
        assertEquals(1, result.getFromStationId());
        assertEquals("#1 -> #2 -> #3", result.getPathString());
        expectedPrint = "STA#3 Inbox:\n" +
                "From: #1\n" +
                "Content: Hell o!\n";
        assertEquals(expectedPrint, network1.inboxStatus(3));
        expectedPrint = "STA#1 Inbox:\n";
        assertEquals(expectedPrint, network1.inboxStatus(1));

        // Self-looping delivery
        result = network1.sendMessage(1, 1, "Myself!");
        assertNotNull(result);
        expectedPrint = "STA#1 Inbox:\n" +
                "From: #1\n" +
                "Content: Myself!\n";
        assertEquals(expectedPrint, network1.inboxStatus(1));

        // Self-offline-looping delivery
        network1.setStationNetState(5, false);
        result = network1.sendMessage(5, 5, "Myself!");
        assertNotNull(result);
        expectedPrint = "STA#5 Inbox:\n" +
                "From: #5\n" +
                "Content: Myself!\n";
        assertEquals(expectedPrint, network1.inboxStatus(5));

        // Failed
        assertNull(network1.sendMessage(1, 5, "Hi"));
        assertNull(network1.sendMessage(9, 2, "Hi"));
        assertNull(network1.sendMessage(1, 11, "Hi"));
    }

    @Test
    void testIsReachable() {
        String expectedPrint;

        // Reachable
        assertTrue(network1.isReachable(1, 3));
        assertTrue(network1.isReachable(1, 2));
        assertTrue(network1.isReachable(1, 4));
        assertTrue(network1.isReachable(4, 2));
        assertTrue(network1.isReachable(1, 1));
        expectedPrint = "STA#3 Inbox:\n";
        assertEquals(expectedPrint, network1.inboxStatus(3));
        expectedPrint = "STA#1 Inbox:\n";
        assertEquals(expectedPrint, network1.inboxStatus(1));

        // Not reachable
        assertFalse(network1.isReachable(3, 1));
        assertFalse(network1.isReachable(2, 1));
        assertFalse(network1.isReachable(1, 5));
        assertFalse(network1.isReachable(9, 4));
        expectedPrint = "STA#2 Inbox:\n";
        assertEquals(expectedPrint, network1.inboxStatus(2));
        expectedPrint = "STA#4 Inbox:\n";
        assertEquals(expectedPrint, network1.inboxStatus(4));
    }

    @Test
    void testGetPathStringWithLeastHops() {
        String expectedPrint;

        // Least-hop path of multiple paths
        expectedPrint = "#1 -> #2 -> #3 (2 hops)\n";
        assertEquals(expectedPrint, network1.getPathStringWithLeastHops(1, 3));
        expectedPrint = "#2 -> #3 (1 hops)\n";
        assertEquals(expectedPrint, network1.getPathStringWithLeastHops(2, 3));

        // Only 1 path
        expectedPrint = "#3 -> #8 -> #6 -> #2 (3 hops)\n";
        assertEquals(expectedPrint, network1.getPathStringWithLeastHops(3, 2));

        // No path reachable
        expectedPrint = "No Path Found! (#" + 7 + " -> #" + 1 + ")\n";
        assertEquals(expectedPrint, network1.getPathStringWithLeastHops(7, 1));

        expectedPrint = "No Path Found! (#" + 1 + " -> #" + 5 + ")\n";
        assertEquals(expectedPrint, network1.getPathStringWithLeastHops(1, 5));

        expectedPrint = "No Path Found! (#" + 11 + " -> #" + 5 + ")\n";
        assertEquals(expectedPrint, network1.getPathStringWithLeastHops(11, 5));

        expectedPrint = "No Path Found! (#" + 5 + " -> #" + 11 + ")\n";
        assertEquals(expectedPrint, network1.getPathStringWithLeastHops(5, 11));

        // With offline station(s) (the least-hop path updated)
        network1.setStationNetState(2, false);
        expectedPrint = "#1 -> #6 -> #8 -> #7 -> #3 (4 hops)\n";
        assertEquals(expectedPrint, network1.getPathStringWithLeastHops(1, 3));

        // With offline station(s) (no path anymore)
        network1.setStationNetState(1, false);
        expectedPrint = "No Path Found! (#" + 4 + " -> #" + 3 + ")\n";
        assertEquals(expectedPrint, network1.getPathStringWithLeastHops(4, 3));
    }

    @Test
    void testSetSignalRadius() {
        double radius;

        // Within (MIN_SIGNAL_RADIUS, MAX_SIGNAL_RADIUS)
        radius = (Station.MIN_SIGNAL_RADIUS + Station.MAX_SIGNAL_RADIUS) / 2;
        assertEquals(radius, network1.setSignalRadius(1, radius));

        // Hit MIN_SIGNAL_RADIUS boundary
        assertEquals(Station.MIN_SIGNAL_RADIUS, network1.setSignalRadius(1, Station.MIN_SIGNAL_RADIUS));

        // < MIN_SIGNAL_RADIUS
        assertEquals(Station.MIN_SIGNAL_RADIUS, network1.setSignalRadius(1, Station.MIN_SIGNAL_RADIUS - 1));

        // Hit MAX_SIGNAL_RADIUS boundary
        assertEquals(Station.MAX_SIGNAL_RADIUS, network1.setSignalRadius(1, Station.MAX_SIGNAL_RADIUS));

        // > MAX_SIGNAL_RADIUS
        assertEquals(Station.MAX_SIGNAL_RADIUS, network1.setSignalRadius(1, Station.MAX_SIGNAL_RADIUS + 1));

        // Station does not exist
        assertEquals(-1, network1.setSignalRadius(23, 25));
    }

    @Test
    void testSetStationNetState() {
        String expectedPrint;

        // Online -> Online
        assertEquals(1, network1.setStationNetState(1, true));
        expectedPrint = "Station: #1\n" +
                "Position: (0.00,0.00)\n" +
                "Online: true\n" +
                "Signal Radius: 100.00/100.00\n";
        assertEquals(expectedPrint, network1.getStationStatus(1));

        // Online -> Offline
        assertEquals(0, network1.setStationNetState(1, false));
        expectedPrint = "Station: #1\n" +
                "Position: (0.00,0.00)\n" +
                "Online: false\n" +
                "Signal Radius: 100.00/100.00\n";
        assertEquals(expectedPrint, network1.getStationStatus(1));

        // Offline -> Offline
        assertEquals(0, network1.setStationNetState(1, false));
        expectedPrint = "Station: #1\n" +
                "Position: (0.00,0.00)\n" +
                "Online: false\n" +
                "Signal Radius: 100.00/100.00\n";
        assertEquals(expectedPrint, network1.getStationStatus(1));

        // Offline -> Online
        assertEquals(1, network1.setStationNetState(1, true));
        expectedPrint = "Station: #1\n" +
                "Position: (0.00,0.00)\n" +
                "Online: true\n" +
                "Signal Radius: 100.00/100.00\n";
        assertEquals(expectedPrint, network1.getStationStatus(1));

        // Station does not exist
        assertEquals(-1, network1.setStationNetState(11, true));
        assertEquals(-1, network1.setStationNetState(11, false));
    }

    @Test
    void testInboxStatusWithStationIdDNE() {
        assertEquals("Station #" + 23 + " does not exist!\n", network1.inboxStatus(23));
    }

    @Test
    void testGetStationStatusWithIdDNE() {
        assertEquals("Station #" + 23 + " Not Found!\n", network1.getStationStatus(23));
    }

    @Test
    void testGetNetworkStatusWithOfflineStations() {
        String expectedPrint;

        network1.setStationNetState(2, false);
        network1.setStationNetState(6, false);
        network1.setStationNetState(7, false);
        expectedPrint = "Network: " + Network.DEFAULT_NETWORK_NAME + "\n" +
                "Total Stations: 8\n" +
                "[1, 2, 3, 4, 5, 6, 7, 8]\n" +
                "Online: 5\n" +
                "[1, 3, 4, 5, 8]\n";
        assertEquals(expectedPrint, network1.getNetworkStatus());
    }
}