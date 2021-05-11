package gui;

import log.Logger;

import javax.swing.*;

public class LoadingHandler {

    public void handleLoading(MainApplicationFrame mainFrame, JInternalFrame[] frames) {
        int answer = showSaveMessage();
        if (answer == JOptionPane.YES_OPTION) {
            for (JInternalFrame frame : frames) {
                switch (frame.getClass().getSimpleName()) {
                    case "LogWindow": {
                        LogWindow logWindow = (LogWindow) frame;
                        logWindow.setLogSource(Logger.getDefaultLogSource());
                        break;
                    }
                    case "GameWindow": {
                        GameWindow gameWindow = (GameWindow) frame;
                        gameWindow.setMetadata();
                        break;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
                mainFrame.addWindow(frame);
            }
        } else {
            mainFrame.addWindow(mainFrame.createLogWindow());
            mainFrame.addWindow(mainFrame.createGameWindow());
            Logger.info("Don't load a save");
        }
    }

    private int showSaveMessage() {
        String[] buttonLabels = new String[] {"Yes", "No"};
        String defaultOption = buttonLabels[0];
        String message;
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
