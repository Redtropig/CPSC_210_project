package ui;

import model.Message;
import model.Network;
import model.Station;
import persistence.JsonFileIO;

import javax.swing.*;
import java.io.PrintStream;
import java.util.*;

// Wireless Station Network Management System (WSNMS)
public class StationNetworkManagerConsole extends JFrame {
    private static final String PROGRAM_NAME = "Wireless Station Network Management System";
    private static final String PROGRAM_VERSION = "ver. Phase 4";
    private static final String PROGRAM_DESCRIPTION = "(Responsive Feedback Area for User Actions, NOT Logging)";
    protected static final String DEFAULT_SAVE_PATH = "./data/network.json";
    protected static final PrintStream DEFAULT_SYS_OUT = System.out;
    protected static final PrintStream DEFAULT_SYS_ERR = System.err;
    private static Queue<String> COMMAND_LIST;

    /* Fields */
    private final Scanner input;

    private Network network;

    // EFFECTS: display program info in console
    protected static void printProgramInfo() {
        System.out.println(PROGRAM_NAME);
        System.out.println(PROGRAM_VERSION);
        System.out.println(PROGRAM_DESCRIPTION);
        System.out.println();
    }

    // EFFECT: display command list in console
    @SuppressWarnings("methodlength")
    protected static void getCommandHelp() {
        // First load command?
        if (COMMAND_LIST == null) {
            COMMAND_LIST = new PriorityQueue<>();

            // COMMAND DOCUMENTATION
            COMMAND_LIST.add("quit program -> quit");
            COMMAND_LIST.add("rename network -> rnm [name]");
            COMMAND_LIST.add("get network status -> netstat");
            COMMAND_LIST.add("get station status -> stastat [stationID]");
            COMMAND_LIST.add("set station signal radius -> setsr [stationID] [signalRadius]");
            COMMAND_LIST.add("add station -> addsta [stationID] [posX] [posY] [signalRadius]");
            COMMAND_LIST.add("get physical distance -> dist [stationID1] [stationID2]");
            COMMAND_LIST.add("remove station -> rmv [stationID]");
            COMMAND_LIST.add("send message -> send [srcStationID] [destStationID] [content]");
            COMMAND_LIST.add("check reachable (one-way) -> reach? [srcStationID] [destStationID]");
            COMMAND_LIST.add("get path with least hops -> pathlh [srcStationID] [destStationID]");
            COMMAND_LIST.add("get station inbox -> inbox [stationID]");
            COMMAND_LIST.add("online/offline station -> net [stationID] {on/off}");
            COMMAND_LIST.add("save network -> save");
            COMMAND_LIST.add("load network -> load");
        }

        // Shallow copy for print
        Queue<String> tmpCommandList = new PriorityQueue<>();
        tmpCommandList.addAll(COMMAND_LIST);

        System.out.println("Commands:");
        // Print command list in
        while (!tmpCommandList.isEmpty()) {
            System.out.println(tmpCommandList.poll());
        }
        System.out.print("\nEND");
    }

    // MODIFIES: none
    // EFFECTS: prompt message: "command not found"
    private static void commandNotFound() {
        System.out.println("Command Not Found!");
    }

    /* Instance Methods Below */

    // EFFECTS: instantiate a network manager (console)
    public StationNetworkManagerConsole() {
        input = new Scanner(System.in);
        network = new Network();
    }

    // MODIFIES: this
    // EFFECTS: replace Network name
    protected void renameNetwork(String name) {
        network.setName(name);
        System.out.println("Network name -> \"" + name + "\"");
    }

    // MODIFIES: this
    // EFFECTS: set Station signal radius by ID
    protected void setStationSignalRadiusById(int stationId, double signalRadius) {
        double result = network.setSignalRadius(stationId, signalRadius);
        // Station not found?
        if (result == -1) {
            System.err.println("Station #" + stationId + " Not Found!");
            return;
        }
        // Print new signalRadius
        String resultInfo = String.format("Station #%d: signalRadius -> %.2f", stationId, result);
        System.out.println(resultInfo);
    }

    // MODIFIES: this
    // EFFECTS: add a Station to this Network
    //          @return true if succeeded, otherwise false
    protected boolean addNetworkStation(int stationId, double posX, double posY, double signalRadius) {
        try {
            network.addStation(stationId, posX, posY, signalRadius);
            System.out.println("Station #" + stationId + " added!");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to add new Station:");
            System.err.println(e.getMessage());
            return false;
        }
    }

    // EFFECTS: get physical distance between 2 Stations
    protected double getPhysicalDistanceTwoStations(int stationId1, int stationId2) {
        double result = network.getPhysicalDistance(stationId1, stationId2);
        // Either/Both of Stations DNE?
        if (result == -1) {
            System.err.println("Either/Both of Stations do not exist!");
            return result;
        }
        // Print physical distance
        String resultInfo = String.format("Distance: #%d <-> #%d = %.2f", stationId1, stationId2, result);
        System.out.println(resultInfo);

        return result;
    }

    // MODIFIES: this
    // EFFECTS: try to remove a station by id, and print the result
    protected void removeStationById(int stationId) {
        // Remove success?
        if (network.removeStation(stationId)) {
            System.out.println("Station removed: #" + stationId);
        } else {
            System.err.println("Station #" + stationId + " does not exist, nothing to remove");
        }
    }

    // MODIFIES: this
    // EFFECTS: send a message from a Station to another
    protected void sendMessage(int srcStationId, int destStationId, String content) {
        Message msg = network.sendMessage(srcStationId, destStationId, content);
        // Deliver failed (not reachable)
        if (msg == null) {
            System.err.println("Not reachable: #" + srcStationId + " -> #" + destStationId);
            return;
        }

        System.out.println("Message delivered!");
    }

    // MODIFIES: this
    // EFFECTS: set online/offline to a Station
    protected void setStationNetStat(int stationId, boolean online) {
        int result = network.setStationNetState(stationId, online);
        switch (result) {
            case 1: {
                System.out.println("Station #" + stationId + " -> Online");
                break;
            }
            case 0: {
                System.out.println("Station #" + stationId + " -> Offline");
                break;
            }
            default: {
                System.err.println("Station #" + stationId + " does not exist!");
            }
        }
    }

    // MODIFIES: file
    // EFFECTS: save Network to file as JSON String
    protected void saveNetwork(String savePath) {
        JsonFileIO fileHandler = new JsonFileIO(savePath);

        // Push Network -> JSONObject buffer
        fileHandler.setJsonObject(network.toJsonObject());

        // Check failed to write file
        if (!fileHandler.writeFile()) {
            System.err.println("Unable to save -> \"" + savePath + "\"");
            return;
        }

        // Saved!
        System.out.println("Saved -> \"" + savePath + "\"");
    }

    // MODIFIES: this
    // EFFECTS: load Network from JSON file
    protected void loadNetwork(String loadPath) {
        JsonFileIO fileHandler = new JsonFileIO(loadPath);

        // Check failed to read file
        if (!fileHandler.loadFile()) {
            System.err.println("Unable to load <- \"" + loadPath + "\"");
            return;
        }

        // Pull JSONObject buffer -> Network
        network = new Network(fileHandler.getJsonObject());

        // Loaded!
        System.out.println("Loaded <- \"" + loadPath + "\"");
    }

    /* Getters */
    protected String getNetworkStatus() {
        return network.getNetworkStatus();
    }

    protected Set<Station> getStations() {
        return network.getStations();
    }

    protected String getStationStatus(int stationId) {
        return network.getStationStatus(stationId);
    }

    protected boolean isReachable(int srcStationID, int destStationID) {
        return network.isReachable(srcStationID, destStationID);
    }

    protected String getPathStringWithLeastHops(int srcStationID, int destStationID) {
        return network.getPathStringWithLeastHops(srcStationID, destStationID);
    }

    protected String getInboxStatus(int stationId) {
        return network.inboxStatus(stationId);
    }

    // MODIFIES: this
    // EFFECTS: handle user inputs
    @SuppressWarnings("methodlength")
    protected void runNetworkManager() {
        printProgramInfo();
        System.out.println("System Ready.\nStanding By...\n(\"help\" to get command list):");

        // Program life period
        while (true) {
            System.out.print("\n>");
            String operation = input.next();

            // Parse input command
            switch (operation) {
                case "quit": {
                    System.out.println("Terminating...\n");
                    return;
                }
                case "rnm": {
                    String name = input.next();
                    renameNetwork(name);
                    input.nextLine();
                    break;
                }
                case "netstat": {
                    System.out.print(getNetworkStatus());
                    input.nextLine();
                    break;
                }
                case "stastat": {
                    // Fetch stationId
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [stationID]!");
                        input.nextLine();
                        break;
                    }
                    int stationId = input.nextInt();

                    System.out.print(getStationStatus(stationId));
                    input.nextLine();
                    break;
                }
                case "setsr": {
                    // Fetch stationId
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [stationID]!");
                        input.nextLine();
                        break;
                    }
                    int stationId = input.nextInt();

                    // Fetch signalRadius
                    if (!input.hasNextDouble()) {
                        System.err.println("Invalid [signalRadius]!");
                        input.nextLine();
                        break;
                    }
                    double signalRadius = input.nextDouble();

                    setStationSignalRadiusById(stationId, signalRadius);
                    input.nextLine();
                    break;
                }
                case "addsta": {
                    // Fetch stationId
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [stationID]!");
                        input.nextLine();
                        break;
                    }
                    int stationId = input.nextInt();
                    // Fetch posX & posY
                    if (!input.hasNextDouble()) {
                        System.err.println("Invalid [posX]!");
                        input.nextLine();
                        break;
                    }
                    double posX = input.nextDouble();
                    if (!input.hasNextDouble()) {
                        System.err.println("Invalid [posY]!");
                        input.nextLine();
                        break;
                    }
                    double poxY = input.nextDouble();
                    // Fetch signalRadius
                    if (!input.hasNextDouble()) {
                        System.err.println("Invalid [signalRadius]!");
                        input.nextLine();
                        break;
                    }
                    double signalRadius = input.nextDouble();

                    addNetworkStation(stationId, posX, poxY, signalRadius);
                    input.nextLine();
                    break;
                }
                case "dist": {
                    // Fetch stationId1, stationId2
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [stationID1]!");
                        input.nextLine();
                        break;
                    }
                    int stationId1 = input.nextInt();
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [stationID2]!");
                        input.nextLine();
                        break;
                    }
                    int stationId2 = input.nextInt();

                    getPhysicalDistanceTwoStations(stationId1, stationId2);
                    input.nextLine();
                    break;
                }
                case "rmv": {
                    // Fetch stationId
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [stationID]!");
                        input.nextLine();
                        break;
                    }
                    int stationId = input.nextInt();

                    removeStationById(stationId);
                    input.nextLine();
                    break;
                }
                case "send": {
                    // Fetch srcStationID, destStationID
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [srcStationID]!");
                        input.nextLine();
                        break;
                    }
                    int srcStationID = input.nextInt();
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [destStationID]!");
                        input.nextLine();
                        break;
                    }
                    int destStationID = input.nextInt();

                    String content = input.nextLine();
                    sendMessage(srcStationID, destStationID, content);
                    break;
                }
                case "reach?": {
                    // Fetch srcStationID, destStationID
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [srcStationID]!");
                        input.nextLine();
                        break;
                    }
                    int srcStationID = input.nextInt();
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [destStationID]!");
                        input.nextLine();
                        break;
                    }
                    int destStationID = input.nextInt();

                    System.out.println(isReachable(srcStationID, destStationID));
                    input.nextLine();
                    break;
                }
                case "pathlh": {
                    // Fetch srcStationID, destStationID
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [srcStationID]!");
                        input.nextLine();
                        break;
                    }
                    int srcStationID = input.nextInt();
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [destStationID]!");
                        input.nextLine();
                        break;
                    }
                    int destStationID = input.nextInt();

                    System.out.print(getPathStringWithLeastHops(srcStationID, destStationID));
                    input.nextLine();
                    break;
                }
                case "inbox": {
                    // Fetch stationId
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [stationID]!");
                        input.nextLine();
                        break;
                    }
                    int stationId = input.nextInt();

                    System.out.print(getInboxStatus(stationId));
                    input.nextLine();
                    break;
                }
                case "net": {
                    // Fetch stationId
                    if (!input.hasNextInt()) {
                        System.err.println("Invalid [stationID]!");
                        input.nextLine();
                        break;
                    }
                    int stationId = input.nextInt();

                    String toState = input.next();
                    // On/Off/Invalid
                    if (toState.equals("on")) {
                        setStationNetStat(stationId, true);
                    } else if (toState.equals("off")) {
                        setStationNetStat(stationId, false);
                    } else {
                        System.err.println("Invalid Option: {on/off}");
                    }

                    input.nextLine();
                    break;
                }
                case "help": {
                    getCommandHelp();
                    input.nextLine();
                    break;
                }
                case "save": {
                    saveNetwork(DEFAULT_SAVE_PATH);
                    input.nextLine();
                    break;
                }
                case "load": {
                    loadNetwork(DEFAULT_SAVE_PATH);
                    input.nextLine();
                    break;
                }
                default: {
                    commandNotFound();
                    input.nextLine();
                }
            }
        }
    }

}
