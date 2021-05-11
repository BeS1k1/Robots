package models;

public class Robot {
    private volatile double m_robotPositionX;
    private volatile double m_robotPositionY;
    private volatile double m_robotDirection;
    private volatile Target m_target;

    public Robot(double x, double y, double direction, Target target)
    {
        m_robotPositionX = x;
        m_robotPositionY = y;
        m_robotDirection = direction;
        m_target = target;
    }

    public double getM_robotPositionX()
    {
        return m_robotPositionX;
    }

    public double getM_robotPositionY()
    {
        return m_robotPositionY;
    }

    public double getM_robotDirection()
    {
        return m_robotDirection;
    }

    public void setM_robotPositionX(double m_robotPositionX) {
        this.m_robotPositionX = m_robotPositionX;
    }

    public void setM_robotPositionY(double m_robotPositionY) {
        this.m_robotPositionY = m_robotPositionY;
    }

    public void setM_robotDirection(double m_robotDirection) {
        this.m_robotDirection = m_robotDirection;
    }
}
