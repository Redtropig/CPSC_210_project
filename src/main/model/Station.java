package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Station implements Comparable<Station> {
    public static final double MAX_SIGNAL_RADIUS = 100;
    public static final double MIN_SIGNAL_RADIUS = 1;

    private final int id;
    private final double posX;
    private final double posY;
    private boolean isOnline;
    private double signalRadius;

    private final List<Message> inbox;

    // REQUIRES: id, signalRadius > 0
    // EFFECTS: instantiate a new Station, initially online
    protected Station(int id, double posX, double posY, double signalRadius) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
        this.setSignalRadius(signalRadius);
        isOnline = true;
        inbox = new LinkedList<>();
    }

    // REQUIRES: legal stationJsonObj
    // EFFECTS: load JSONObject -> Station
    protected Station(JSONObject stationJsonObj) {
        id = stationJsonObj.getInt("id");
        posX = stationJsonObj.getDouble("posX");
        posY = stationJsonObj.getDouble("posY");
        isOnline = stationJsonObj.getBoolean("isOnline");
        signalRadius = stationJsonObj.getDouble("signalRadius");
        inbox = new LinkedList<>();

        // Fill inbox
        JSONArray inboxArr = stationJsonObj.getJSONArray("inbox");
        for (Object o : inboxArr) {
            JSONObject msgJsonObj = (JSONObject)o;
            inbox.add(new Message(msgJsonObj));
        }
    }

    /* Getters */
    public int getId() {
        return id;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public double getCurrentSignalRadius() {
        return signalRadius;
    }

    protected String getSignalRadiusStatus() {
        return String.format("%.2f/%.2f", signalRadius, MAX_SIGNAL_RADIUS);
    }

    protected List<Message> getInbox() {
        return inbox;
    }

    /* Setters */
    protected boolean setNetState(boolean isOnline) {
        return this.isOnline = isOnline;
    }

    // REQUIRES: signalRadius > 0
    // MODIFIES: this
    // EFFECTS: set signal radius
    //          (or MIN/MAX bound if radius value not within [MIN_SIGNAL_RADIUS, MAX_SIGNAL_RADIUS])
    //          @return the new radius value
    protected double setSignalRadius(double signalRadius) {
        return this.signalRadius = Math.max(MIN_SIGNAL_RADIUS, Math.min(MAX_SIGNAL_RADIUS, signalRadius));
    }

    // REQUIRES: msg != null
    // MODIFIES: this
    // EFFECTS: add a Message to this Station's inbox
    protected void pushInbox(Message msg) {
        inbox.add(msg);
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    @Override
    public int compareTo(Station station) {
        return id - station.id;
    }

    // EFFECTS: parse this Station to JSONObject
    protected JSONObject toJsonObject() {
        JSONObject stationJsonObj = new JSONObject();

        stationJsonObj.put("id", id);
        stationJsonObj.put("posX", posX);
        stationJsonObj.put("posY", posY);
        stationJsonObj.put("isOnline", isOnline);
        stationJsonObj.put("signalRadius", signalRadius);
        stationJsonObj.put("inbox", inboxToJsonArray());

        return stationJsonObj;
    }

    // EFFECTS: parse field inbox to JSONArray
    private JSONArray inboxToJsonArray() {
        JSONArray inboxJsonArr = new JSONArray();

        for (Message m : inbox) {
            inboxJsonArr.put(m.toJsonObject());
        }

        return inboxJsonArr;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, posX, posY, isOnline, signalRadius, inbox);
    }
}
