package persistence;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class JsonFileIO {
    private static final int INDENTATION = 4;
    private final File file;
    private JSONObject jsonObj;

    // REQUIRES: filePath != null
    // EFFECTS: construct a JsonFileIO with a given filePath
    public JsonFileIO(String filePath) {
        this.file = new File(filePath);
    }

    public JSONObject getJsonObject() {
        return jsonObj;
    }

    public void setJsonObject(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
    }

    // MODIFIES: this
    // EFFECTS: load the entire file from "filePath"
    //          @return true -> success, false -> otherwise
    public boolean loadFile() {
        StringBuilder buffer = new StringBuilder();
        Scanner fileLoader;

        try {
            fileLoader = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found!");
            return false;
        }

        // Read the entire file (one line per iteration)
        while (fileLoader.hasNextLine()) {
            buffer.append(fileLoader.nextLine());
        }

        // Load JSONString -> JSONObject
        jsonObj = new JSONObject(buffer.toString());

        fileLoader.close();
        return true;
    }

    // MODIFIES: file
    // EFFECTS: write the JSONObject buffer to "filePath"
    //          @return true -> success, false -> otherwise
    public boolean writeFile() {
        PrintWriter fileWriter;

        try {
            fileWriter = new PrintWriter(file);
        } catch (Exception e) {
            System.err.println("Failed to create file!");
            return false;
        }

        // Write to file
        fileWriter.print(jsonObj.toString(INDENTATION));

        fileWriter.close();
        return true;
    }

}
