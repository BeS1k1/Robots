package gui;

import java.awt.BorderLayout;
import java.io.Serializable;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class GameWindow extends JInternalFrame implements Serializable
{
    private final GameVisualizer m_visualizer;
    private GameWindow window = this;
    private ClosingHandler closingHandler = new ClosingHandler();

    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                closingHandler.handleClosing(window, e, 1);
            }
        });
        pack();
    }

    public void setMetadata() {
        m_visualizer.setMetadata();
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                closingHandler.handleClosing(window, e, 1);
            }
        });
    }
}
