package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.io.Serializable;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener, Serializable
{
    private LogWindow window = this;
    private LogWindowSource m_logSource;
    private TextArea m_logContent;
    private ClosingHandler closingHandler = new ClosingHandler();

    public LogWindow(LogWindowSource logSource) 
    {
        super("Протокол работы", true, true, true, true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        m_logSource = logSource;
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

    public void setLogSource(LogWindowSource logSource) {
        Iterable<LogEntry> entries = m_logSource.all();
        m_logSource = logSource;
        m_logSource.registerListener(this);
        for (LogEntry entry: entries) {
            m_logSource.append(entry.getLevel(), entry.getMessage());
        }
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                closingHandler.handleClosing(window, e, 2);
            }
        });
    }

    public void dispose()
    {
        m_logSource.unregisterListener(this);
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
