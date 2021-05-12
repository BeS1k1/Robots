package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;

import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends RestorableJInternalFrame implements LogChangeListener
{
    private LogWindow window = this;
    private LogWindowSource m_logSource;
    private TextArea m_logContent;
    private ClosingHandler closingHandler = new ClosingHandler();
    private StatesKeeper m_keeper;

    public LogWindow(LogWindowSource logSource, StatesKeeper keeper)
    {
        super("Протокол работы", true, true, true, true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        m_logSource = logSource;
        m_keeper = keeper;
        m_keeper.register(this, "LogWindow");
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                closingHandler.handleClosing(window, e, 2);
            }
        });
    }

    public void dispose()
    {
        m_logSource.unregisterListener(this);
        m_keeper.unregister(this.getName());
        setVisible(false);
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }
}
