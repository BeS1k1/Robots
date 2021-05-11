package log;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

public class LogWindowSource implements Serializable
{
    private int m_iQueueLength;
    
    private final Queue<LogEntry> m_messages;
    private final ArrayList<LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;
    
    public LogWindowSource(int iQueueLength) 
    {
        m_iQueueLength = iQueueLength;
        m_messages = new ArrayDeque<LogEntry>(m_iQueueLength);
        m_listeners = new ArrayList<LogChangeListener>();
    }
    
    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }
    
    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }
    
    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        synchronized (m_messages) {
            if (m_messages.size() == m_iQueueLength)
                m_messages.poll();
            m_messages.offer(entry);
        }
        LogChangeListener [] activeListeners = m_activeListeners;
        if (activeListeners == null)
        {
            synchronized (m_listeners)
            {
                activeListeners = m_listeners.toArray(new LogChangeListener[0]);
                m_activeListeners = activeListeners;
            }
        }
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }
    
    public int size()
    {
        return m_messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        ArrayList<LogEntry> messages = new ArrayList<LogEntry>(m_messages);
        if (startFrom < 0 || startFrom >= messages.size())
        {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, m_messages.size());
        return messages.subList(startFrom, indexTo);
    }

    public Iterable<LogEntry> all()
    {
        return m_messages;
    }
}
