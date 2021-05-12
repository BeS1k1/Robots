package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class GameWindow extends RestorableJInternalFrame
{
    private final GameVisualizer m_visualizer;
    private GameWindow window = this;
    private ClosingHandler closingHandler = new ClosingHandler();
    private StatesKeeper m_keeper;

    public GameWindow(StatesKeeper keeper)
    {
        super("Игровое поле", true, true, true, true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        m_visualizer = new GameVisualizer();
        m_keeper = keeper;
        m_keeper.register(this, "GameWindow");
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

    public void dispose() {
        setVisible(false);
        m_keeper.unregister(this.getName());
    }

}
