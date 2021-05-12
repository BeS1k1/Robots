package gui;

import java.util.HashMap;

public interface Restorable {
    HashMap<String, Object> getProperties();
    void setproperties(HashMap<String, Object> properties);
}
