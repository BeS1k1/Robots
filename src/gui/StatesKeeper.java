package gui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class StatesKeeper {
    private HashMap<String, Restorable> restorableObjects;
    private JSONObject savedProperties;
    private File storageFile;

    StatesKeeper(File storageFile)
    {
        restorableObjects = new HashMap<String, Restorable>();
        this.storageFile = storageFile;
    }

    void register(Restorable restorableObject, String name)
    {
        restorableObjects.put(name, restorableObject);
    }

    void unregister(String name)
    {
        restorableObjects.remove(name);
    }

    void save() throws IOException
    {
        savedProperties = null;
        FileWriter fw = new FileWriter(storageFile);
        fw.write(getAllProperties().toJSONString());
        fw.close();
    }

    JSONObject getAllProperties()
    {
        JSONObject properties = new JSONObject();
        for(ConcurrentMap.Entry<String, Restorable> entry : restorableObjects.entrySet())
        {
            String name = entry.getKey();
            Restorable restorableObj = entry.getValue();
            properties.put(name, restorableObj.getProperties());
        }
        return properties;
    }

    void setAllProperties(JSONObject properties)
    {
        for(Object obj : properties.entrySet()) {
            ConcurrentMap.Entry<String, HashMap<String, Object>> entry = (ConcurrentMap.Entry<String, HashMap<String, Object>>) obj;
            String name = entry.getKey();
            HashMap<String, Object> objProperies = entry.getValue();
            Restorable restorableObj = restorableObjects.get(name);
            restorableObj.setproperties(objProperies);
        }
    }

    JSONObject getPropertiesFromFile() throws IOException, ParseException {
        if (storageFile.length() == 0) {
            return null;
        }
        JSONParser parser = new JSONParser();
        Object parsedFile;
        parsedFile = parser.parse(new FileReader(storageFile));
        JSONObject properties = (JSONObject) parsedFile;
        return properties;
    }
    boolean canLoad()
    {
        try {
            if (savedProperties == null) {
                savedProperties = getPropertiesFromFile();
                return savedProperties != null;
            }
            else
                return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    void load() throws ParseException, IOException
    {
        JSONObject properties;
        if (savedProperties == null) {
            savedProperties = getPropertiesFromFile();
        }
        properties = savedProperties;
        setAllProperties(properties);
    }
}
