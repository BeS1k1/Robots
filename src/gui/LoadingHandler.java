package gui;

import log.Logger;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;

public class LoadingHandler {

    public void handleLoading(StatesKeeper keeper) throws ParseException, IOException {
        int answer = showSaveMessage();
        if (answer == JOptionPane.YES_OPTION) {
            keeper.load();
        } else {
            Logger.info("Don't load a save");
        }
    }

    private int showSaveMessage() {
        String[] buttonLabels = new String[] {"Yes", "No"};
        String defaultOption = buttonLabels[0];
        Icon icon = null;
        return JOptionPane.showOptionDialog(null,
                "You have a save. Do you want to load it?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                icon,
                buttonLabels,
                defaultOption);
    }

}
