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
}
