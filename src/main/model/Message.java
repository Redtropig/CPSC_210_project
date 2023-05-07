package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Message packet which transfer within Network
public class Message {
    // Try to prevent modification of the packet (from multiple refs)
    // To modify, re-construct a new Message & deep copy
    private final List<Integer> pathStationsId;
    private final int toStationId;
    private final String content;

    // REQUIRES: fromStationId, toStationId > 0
    // EFFECTS: Create a new message
    protected Message(int fromStationId, int toStationId, String content) {
        this.pathStationsId = new ArrayList<>();
        this.pathStationsId.add(fromStationId);
        this.toStationId = toStationId;
        this.content = content;
    }

    // REQUIRES: stationId > 0, msg != null
    // EFFECTS: Create a new message: old msg with stationId appended to path
    protected Message(Message msg, int stationId) {
        this.pathStationsId = new ArrayList<>();
        // List Copy
        this.pathStationsId.addAll(msg.pathStationsId);

        this.pathStationsId.add(stationId);
        this.toStationId = msg.toStationId;
        this.content = msg.content;
    }

    // REQUIRES: legal msgJsonObj
    // EFFECTS: load JSONObject -> Message
    protected Message(JSONObject msgJsonObj) {
        pathStationsId = new ArrayList<>();
        toStationId = msgJsonObj.getInt("toStationId");
        content = msgJsonObj.getString("content");

        // Fill pathStationsId
        JSONArray pathJsonArr = msgJsonObj.getJSONArray("pathStationsId");
        for (Object o : pathJsonArr) {
            pathStationsId.add((Integer)o);
        }
    }

    /* Getters */
    public int getFromStationId() {
        return pathStationsId.get(0);
    }

    public int getCurrentStationId() {
        return pathStationsId.get(pathStationsId.size() - 1);
    }

    public int getToStationId() {
        return toStationId;
    }

    // MODIFIES: none
    // EFFECTS: path toString
    public String getPathString() {
        StringBuilder result = new StringBuilder();
        for (Integer i : pathStationsId) {
            result.append(" -> #").append(i.toString());
        }
        return result.substring(4);
    }

    public int getHopCount() {
        return pathStationsId.size() - 1;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        String result = "";
        result += "From: #" + getFromStationId() + "\n";
        result += "Content: " + content + "\n";
        return result;
    }

    // EFFECTS: parse this Message to JSONObject
    protected JSONObject toJsonObject() {
        JSONObject msgJsonObj = new JSONObject();

        msgJsonObj.put("pathStationsId", pathStationsId);
        msgJsonObj.put("toStationId", toStationId);
        msgJsonObj.put("content", content);

        return msgJsonObj;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathStationsId, toStationId, content);
    }
}
