package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StationTest {

    private Station station1;

    @BeforeEach
    void setUp() {
        station1 = new Station(1, 2.2, 3.3, 4.4);

        assertTrue(station1.isOnline());
        assertEquals(1, station1.getId());
        assertEquals(2.2, station1.getPosX());
        assertEquals(3.3, station1.getPosY());
        assertEquals(4.4, station1.getCurrentSignalRadius());
        assertTrue(station1.getInbox().isEmpty());

        String expectedPrint = String.format("%.2f/%.2f", 4.4, Station.MAX_SIGNAL_RADIUS);
        assertEquals(expectedPrint, station1.getSignalRadiusStatus());

        assertEquals("1", station1.toString());
    }

    @Test
    void testSetOnline() {
        // Online -> Online
        assertTrue(station1.setNetState(true));

        // Online -> Offline
        assertFalse(station1.setNetState(false));

        // Offline -> Offline
        assertFalse(station1.setNetState(false));

        // Offline -> Online
        assertTrue(station1.setNetState(true));
    }

    @Test
    void testSetSignalRadius() {
        double radius;

        // Within (MIN_SIGNAL_RADIUS, MAX_SIGNAL_RADIUS)
        radius = (Station.MIN_SIGNAL_RADIUS + Station.MAX_SIGNAL_RADIUS) / 2;
        station1.setSignalRadius(radius);
        assertEquals(radius, station1.getCurrentSignalRadius());

        // MIN_SIGNAL_RADIUS & MAX_SIGNAL_RADIUS boundary
        station1.setSignalRadius(Station.MIN_SIGNAL_RADIUS);
        assertEquals(Station.MIN_SIGNAL_RADIUS, station1.getCurrentSignalRadius());
        station1.setSignalRadius(Station.MAX_SIGNAL_RADIUS);
        assertEquals(Station.MAX_SIGNAL_RADIUS, station1.getCurrentSignalRadius());

        // < MIN_SIGNAL_RADIUS
        station1.setSignalRadius(Station.MIN_SIGNAL_RADIUS - 1);
        assertEquals(Station.MIN_SIGNAL_RADIUS, station1.getCurrentSignalRadius());

        // > MAX_SIGNAL_RADIUS
        station1.setSignalRadius(Station.MAX_SIGNAL_RADIUS + 1);
        assertEquals(Station.MAX_SIGNAL_RADIUS, station1.getCurrentSignalRadius());
    }

    @Test
    void testPushInbox() {
        List<Message> result;
        Message msg1 = new Message(1, 5, "SomeThing");
        Message msg2 = new Message(7, 2, "Hello");

        // One msg
        station1.pushInbox(msg1);
        result = station1.getInbox();
        assertEquals(1, result.size());
        assertEquals(msg1, result.get(0));

        // Two msg
        station1.pushInbox(msg2);
        result = station1.getInbox();
        assertEquals(2, result.size());
        assertEquals(msg2, result.get(1));
    }

    @Test
    void testCompareTo() {
        Station tmpStation;

        // tmpStation.id > station1.id
        tmpStation = new Station(2, 4, 6, 8);
        assertTrue((station1.compareTo(tmpStation) < 0));
        assertTrue((tmpStation.compareTo(station1) > 0));

        // tmpStation.id == station1.id
        tmpStation = new Station(1, 4, 6, 8);
        assertEquals(0, station1.compareTo(tmpStation));
    }
}