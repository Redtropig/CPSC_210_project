package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    private Message msg1;
    private Message msg2;

    @BeforeEach
    void setUp() {
        msg1 = new Message(5, 3, "Hi!");
        msg2 = new Message(msg1, 8);

        assertEquals(5, msg1.getFromStationId());
        assertEquals(5, msg2.getFromStationId());

        assertEquals(3, msg1.getToStationId());
        assertEquals(3, msg2.getToStationId());

        assertEquals(5, msg1.getCurrentStationId());
        assertEquals(8, msg2.getCurrentStationId());

        assertEquals("#5", msg1.getPathString());
        assertEquals("#5 -> #8", msg2.getPathString());

        assertEquals(0, msg1.getHopCount());
        assertEquals(1, msg2.getHopCount());

        assertEquals("Hi!", msg1.getContent());
        assertEquals("Hi!", msg2.getContent());
    }

    @Test
    void testToString() {
        String expectedPrint = "";
        expectedPrint += "From: #" + msg2.getFromStationId() + "\n";
        expectedPrint += "Content: " + msg2.getContent() + "\n";
        assertEquals(expectedPrint, msg2.toString());

        expectedPrint = "";
        expectedPrint += "From: #" + msg1.getFromStationId() + "\n";
        expectedPrint += "Content: " + msg1.getContent() + "\n";
        assertEquals(expectedPrint, msg1.toString());
    }
}