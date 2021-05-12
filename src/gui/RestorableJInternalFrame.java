package gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RestorableJInternalFrame extends JInternalFrame implements Restorable {

    public RestorableJInternalFrame(String title, boolean resizable,
                                    boolean closable, boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
    }

    public HashMap<String, Object> getProperties() {
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("isMaximum", Boolean.toString(isMaximum));
        properties.put("Width", Integer.toString(getWidth()));
        properties.put("Height", Integer.toString(getHeight()));
        properties.put("isClosed", Boolean.toString(isClosed()));
        properties.put("LocationX", Integer.toString(getLocation().x));
        properties.put("LocationY", Integer.toString(getLocation().y));
        return properties;
    }

    public void setproperties(HashMap<String, Object> properties)
    {
        int width = getWidth();
        int height = getHeight();
        Point location = getLocation();

        for (Map.Entry<String, Object> property : properties.entrySet()) {
            String propertyName = property.getKey();
            Object propertyValue = property.getValue();
            switch (propertyName) {
                case "isMaximum":
                    isMaximum = Boolean.parseBoolean((String)propertyValue);
                    break;
                case "Width":
                    width = Integer.parseInt((String)propertyValue);
                    setSize(width, height);
                    break;
                case "Height":
                    height = Integer.parseInt((String)propertyValue);
                    setSize(width, height);
                case "isClosed":
                    isClosed = Boolean.parseBoolean((String)propertyValue);
                    break;
                case "LocationX":
                    location.x = Integer.parseInt((String)propertyValue);
                    setLocation(location);
                    break;
                case "LocationY":
                    location.y = Integer.parseInt((String)propertyValue);
                    setLocation(location);
                    break;
                default:
                    break;
            }
        }
    }
}
