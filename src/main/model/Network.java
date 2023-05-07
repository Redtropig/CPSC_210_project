package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Network {
    public static final String DEFAULT_NETWORK_NAME = "Unnamed";
    // Service Range
    public static final double MAX_X = 250;
    public static final double MIN_X = -MAX_X;
    public static final double MAX_Y = 250;
    public static final double MIN_Y = -MAX_Y;

    private String name;
    private final Set<Station> stations;

    // EFFECTS: instantiate a new Network
    public Network() {
        this.name = DEFAULT_NETWORK_NAME;
        stations = new TreeSet<>();

        // Log
        EventLog.getInstance().logEvent(new Event("Network Created: \"" + name + "\""));
    }

    // REQUIRES: legal netJsonObj
    // EFFECTS: load JSONObject -> Network
    public Network(JSONObject netJsonObj) {
        name = netJsonObj.getString("name");
        stations = new TreeSet<>();

        // Fill stations
        JSONArray stationsArr = netJsonObj.getJSONArray("stations");
        for (Object o : stationsArr) {
            JSONObject stationJsonObj = (JSONObject)o;
            stations.add(new Station(stationJsonObj));
        }

        // Log
        EventLog.getInstance().logEvent(new Event("Network Loaded from JSON: \"" + name + "\""));
    }

    /* Getters */
    private Set<Station> getOnlineStations() {
        Set<Station> result = new TreeSet<>();
        for (Station s : stations) {
            if (s.isOnline()) {
                result.add(s);
            }
        }
        return result;
    }

    public Set<Station> getStations() {
        return stations;
    }

    public String getNetworkStatus() {
        StringBuilder result = new StringBuilder();
        Set<Station> onlineStations = getOnlineStations();

        result.append("Network: ").append(name).append("\n");
        result.append("Total Stations: ").append(stations.size()).append("\n");
        result.append(getStations()).append("\n");
        result.append("Online: ").append(onlineStations.size()).append("\n");
        result.append(onlineStations).append("\n");

        return result.toString();
    }

    // REQUIRES: stationId > 0
    // EFFECTS: get status String of Station
    public String getStationStatus(int stationId) {
        Station station = getStationById(stationId);
        // Station not found
        if (station == null) {
            return "Station #" + stationId + " Not Found!\n";
        }

        String result = "Station: #" + station.getId() + "\n";
        result += "Position: " + "(" + String.format("%.2f", station.getPosX()) + ","
                + String.format("%.2f", station.getPosY()) + ")\n";
        result += "Online: " + station.isOnline() + "\n";
        result += "Signal Radius: " + station.getSignalRadiusStatus() + "\n";

        return result;
    }

    /* Setters */
    public void setName(String name) {
        this.name = name;
        // Log
        EventLog.getInstance().logEvent(new Event("Network Name -> \"" + this.name + "\""));
    }

    // REQUIRES: stationId, signalRadius > 0
    // MODIFIES: this
    // EFFECTS: add a new Station to this Network, initially online
    //          @exceptions: 1. stationId <= 0
    //                       2. signalRadius <= 0
    //                       3. out of service range of X,Y
    //                       4. stationId already exists
    public void addStation(int stationId, double posX, double posY, double signalRadius) throws Exception {
        // Check stationId <= 0
        if (stationId <= 0) {
            throw new Exception("Station ID must > 0\n");
        }
        // Check signalRadius <= 0
        if (signalRadius <= 0) {
            throw new Exception("Signal Radius must > 0\n");
        }
        // Check not within service range
        if (posX < MIN_X || posX > MAX_X || posY < MIN_Y || posY > MAX_Y) {
            throw new Exception("Out of Service Range!\n"
                    + "X must within [" + MIN_X + "," + MAX_X + "]\n"
                    + "Y must within [" + MIN_Y + "," + MAX_Y + "]\n");
        }

        Station station = new Station(stationId, posX, posY,signalRadius);
        // Check stationId existence
        if (!stations.add(station)) {
            throw new Exception("Station ID #" + stationId + " has already exist!\n");
        }

        // Log
        EventLog.getInstance().logEvent(new Event("Station Added: #" + stationId));
    }

    // REQUIRES: stationId > 0
    // MODIFIES: this
    // EFFECTS: remove a Station by id
    //          @return true if success, false if the Station does not exist
    public boolean removeStation(int stationId) {
        Station station = getStationById(stationId);
        // Station not found?
        if (station == null) {
            return false;
        }

        // Log
        EventLog.getInstance().logEvent(new Event("Station Removed: #" + stationId));

        return stations.remove(station); // always true
    }

    // REQUIRES: stationId > 0
    // MODIFIES: none
    // EFFECTS: get Station ref by stationId
    //          @return null if not found
    private Station getStationById(int stationId) {
        for (Station s : stations) {
            if (s.getId() == stationId) {
                return s;
            }
        }
        return null;
    }

    // REQUIRES: a,b != null
    // MODIFIES: none
    // EFFECTS: calculate physical distance of 2 Stations
    private double getPhysicalDistance(Station a, Station b) {
        return Math.sqrt(Math.pow(a.getPosX() - b.getPosX(),2) + Math.pow(a.getPosY() - b.getPosY(),2));
    }

    // REQUIRES: stationId1,stationId2 > 0
    // MODIFIES: none
    // EFFECTS: calculate physical distance of 2 Stations
    //          @return distance if 2 stations both exist, otherwise -1
    public double getPhysicalDistance(int stationId1, int stationId2) {
        Station station1 = getStationById(stationId1);
        Station station2 = getStationById(stationId2);
        // Check stations exist
        if (station1 == null || station2 == null) {
            return -1;
        }
        return getPhysicalDistance(station1, station2);
    }

    // REQUIRES: fromStationId, toStationId > 0
    // MODIFIES: this
    // EFFECTS: try to deliver content to destination inbox
    //          @return arrival message packet if delivered, otherwise null
    @SuppressWarnings("methodlength") // BFS Algorithm takes more statements reasonably
    public Message sendMessage(int fromStationId, int toStationId, String content) {
        Queue<Message> bfsQueue = new LinkedList<>();
        Set<Station> onBusy = new HashSet<>();

        Station fromStation = getStationById(fromStationId);
        // Check source station exists
        if (fromStation == null) {
            return null;
        }

        Message initialMsg = new Message(fromStationId, toStationId, content);

        // Self-looping message?
        if (fromStationId == toStationId) {
            // Not probing test
            if (!initialMsg.getContent().equals("$PROBE$")) {
                fromStation.pushInbox(initialMsg);
            }
            return initialMsg;
        }

        // Push root
        bfsQueue.add(initialMsg);
        onBusy.add(fromStation);
        // BFS
        while (!bfsQueue.isEmpty()) {
            Message headMessage = bfsQueue.poll();
            Station currentStation = getStationById(headMessage.getCurrentStationId());

            // Peripheral expanding
            for (Station s : stations) {
                // On busy (visited) || Offline
                if (onBusy.contains(s) || !s.isOnline()) {
                    continue;
                }
                // Out of signal range
                // assert currentStation != null; // always true due to Message class implementation
                if (getPhysicalDistance(currentStation,s) > currentStation.getCurrentSignalRadius()) {
                    continue;
                }

                Message newMessage = new Message(headMessage, s.getId());
                // Message reached destination
                if (s.getId() == headMessage.getToStationId()) {
                    // Not probing test
                    if (!newMessage.getContent().equals("$PROBE$")) {
                        s.pushInbox(newMessage);

                        // Log
                        EventLog.getInstance().logEvent(new Event(
                                "Message Delivered: #"
                                        + fromStationId
                                        + " -> #"
                                        + toStationId
                        ));
                    }
                    return newMessage;
                }
                // Legitimate message relay
                bfsQueue.add(newMessage);
                onBusy.add(s);
            }
        }
        // Not reachable
        return null;
    }

    // REQUIRES: sourceStationId, destinationStationId > 0
    // MODIFIES: none
    // EFFECTS: check reachable from source station to destination station
    public boolean isReachable(int sourceStationId, int destinationStationId) {
        return (sendMessage(sourceStationId, destinationStationId, "$PROBE$") != null);
    }

    // REQUIRES: fromStationId, toStationId > 0
    // MODIFIES: none
    // EFFECTS: get path String with the least hops
    public String getPathStringWithLeastHops(int fromStationId, int toStationId) {
        Message probeMsg = sendMessage(fromStationId, toStationId, "$PROBE$");
        // Check not reachable
        if (probeMsg == null) {
            return "No Path Found! (#" + fromStationId + " -> #" + toStationId + ")\n";
        }
        // Reachable
        return probeMsg.getPathString() + " (" + probeMsg.getHopCount() + " hops)\n";
    }

    // REQUIRES: stationId > 0
    // MODIFIES: none
    // EFFECTS: retrieve inbox list of the Station
    public String inboxStatus(int stationId) {
        Station station = getStationById(stationId);
        // Check Station exists
        if (station == null) {
            return "Station #" + stationId + " does not exist!\n";
        }

        List<Message> inboxMsgList = station.getInbox();
        StringBuilder result = new StringBuilder();

        result.append("STA#").append(stationId).append(" Inbox:\n");
        for (Message msg : inboxMsgList) {
            result.append(msg.toString());
        }

        return result.toString();
    }

    // REQUIRES: stationId > 0
    // MODIFIES: this
    // EFFECTS: set signal radius of designated Station
    //          @return the new radius value, -1 if no such Station
    public double setSignalRadius(int stationId, double signalRadius) {
        Station station = getStationById(stationId);
        // Check station does not exist
        if (station == null) {
            return -1;
        }

        // Log
        EventLog.getInstance().logEvent(new Event(
                "Station #"
                        + stationId
                        + ": Signal Radius -> "
                        + String.format("%.2f", signalRadius)
        ));

        return station.setSignalRadius(signalRadius);
    }

    // REQUIRES: stationId > 0
    // MODIFIES: this
    // EFFECTS: set online/offline net state to designated Station
    //          @return online status after set operation processed,
    //          offline -> 0, online -> 1, station not found -> -1
    public int setStationNetState(int stationId, boolean isOnline) {
        Station station = getStationById(stationId);
        // Check station does not exist
        if (station == null) {
            return -1;
        }
        boolean newNetState = station.setNetState(isOnline);

        // Log
        EventLog.getInstance().logEvent(new Event(
                "Station #"
                        + stationId
                        + ": Online -> "
                        + newNetState
        ));

        return newNetState ? 1 : 0;
    }

    // EFFECTS: parse this Network to JSONObject
    public JSONObject toJsonObject() {
        JSONObject netJsonObj = new JSONObject();

        netJsonObj.put("name", name);
        netJsonObj.put("stations", stationsToJsonArray());

        return netJsonObj;
    }

    // EFFECTS: parse field Stations to JSONArray
    private JSONArray stationsToJsonArray() {
        JSONArray stationsJsonArr = new JSONArray();

        for (Station s : stations) {
            stationsJsonArr.put(s.toJsonObject());
        }

        return stationsJsonArr;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, stations);
    }
}
