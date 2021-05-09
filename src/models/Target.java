package models;

public class Target {
    private volatile int m_targetPositionX;
    private volatile int m_targetPositionY;

    public Target(int x, int y)
    {
        m_targetPositionX = x;
        m_targetPositionY = y;
    }

    public int getM_targetPositionX()
    {
        return m_targetPositionX;
    }

    public int getM_targetPositionY()
    {
        return m_targetPositionY;
    }

    public void setM_targetPositionX(int x)
    {
        m_targetPositionX = x;
    }

    public void setM_targetPositionY(int y)
    {
        m_targetPositionY = y;
    }
}
