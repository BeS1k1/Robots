package gui;

import log.Logger;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;

public class ClosingHandler {
    public void handleClosing() {
        int answer = showWarningMessage(1);
        if (answer == JOptionPane.YES_OPTION) {
            Logger.info("getQuit");
            System.exit(0);
        }
        else{
            Logger.info("getNotQuit");
        }
    }
    public void handleClosing(JInternalFrame window, InternalFrameEvent e, int type) {
        int answer = showWarningMessage(2);
        String yesMessage;
        String noMessage;
        if (type == 1) {
            yesMessage = "Close game window";
            noMessage = "Don't close game window";
        }
        else{
            yesMessage = "Close log window";
            noMessage = "Don't close log window";
        }
        if (answer == JOptionPane.YES_OPTION) {
            Logger.info(yesMessage);
            window.dispose();
        }
        else{
            Logger.info(noMessage);
        }
    }

    private int showWarningMessage(int context) {
        String[] buttonLabels = new String[] {"Yes", "No"};
        String defaultOption = buttonLabels[0];
        String message;
        Icon icon = null;
        if (context==1)
            message = "Do you really want to exit?";
        else
            message = "Do you really want to close this window?";
        return JOptionPane.showOptionDialog(null,
                message,
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                icon,
                buttonLabels,
                defaultOption);
    }


}
