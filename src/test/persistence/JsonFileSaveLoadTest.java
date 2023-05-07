package persistence;

import model.Network;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonFileSaveLoadTest {
    private Network network1;
    private final JsonFileIO fileHandler1 = new JsonFileIO("./data/testJsonFileSaveLoad.json");

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
    void jsonFileSaveLoadTest() {
        /* Save-Load test */
        network1.sendMessage(1, 3, "test-1");
        network1.sendMessage(1, 3, "test-2");
        network1.sendMessage(1, 3, "test-3");
        // isolate S1 & S3
        network1.setStationNetState(2, false);
        network1.setStationNetState(7, false);

        int originalHashCode = network1.hashCode();

        /* Save */
        JSONObject net1JsonObj = network1.toJsonObject();
        fileHandler1.setJsonObject(net1JsonObj);

        assertTrue(fileHandler1.writeFile());

        /* Load */
        assertTrue(fileHandler1.loadFile());

        JSONObject net1JsonObjBack = fileHandler1.getJsonObject();
        network1 = new Network(net1JsonObjBack);

        /* Check equals */
        assertEquals(originalHashCode, network1.hashCode());
        assertEquals(net1JsonObj.toString(), net1JsonObjBack.toString());
    }

    @Test
    void jsonFileNotFoundTest() {
        // Load failed
        JsonFileIO handler = new JsonFileIO("./data/test/JsonFileSaveLoad.json");
        assertFalse(handler.loadFile());

        // Save failed
        handler = new JsonFileIO("****");
        assertFalse(handler.writeFile());
    }
}