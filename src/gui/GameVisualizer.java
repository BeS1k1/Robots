package gui;

import log.Logger;
import models.Barrier;
import models.Robot;
import models.Target;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel
{
    private transient Timer m_timer = initTimer();
    
    private static Timer initTimer() 
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    private volatile int m_robotDiam1 = 30;
    private volatile int m_robotDiam2 = 10;

    private Target target = new Target(150, 100);
    private Robot robot = new Robot(100, 100, 0, target);

    private ArrayList<Barrier> barriers = new ArrayList<>();
    
    private static final double maxVelocity = 0.1; 
    private static final double maxAngularVelocity = 0.005;
    
    public GameVisualizer() 
    {
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();
            }
        }, 0, 10);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getButton()==MouseEvent.BUTTON3) {
                    setTargetPosition(e.getPoint());
                    repaint();
                    Logger.debug("нажал ПКМ");
                }
                if (e.getButton()==MouseEvent.BUTTON2){
                    for (Barrier barrier : barriers){
                        if (barrier.hasInBarrier(e.getPoint())){
                            barriers.remove(barrier);
                        }
                    }
                    Logger.debug("нажал ЦКМ");
                }
            }
            final Point[] point1 = new Point[1];
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    point1[0] = e.getPoint();
                    Logger.debug("зажал ЛКМ");
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    barriers.add(new Barrier(Math.min(point1[0].x, e.getPoint().x), Math.min(point1[0].y, e.getPoint().y),
                            Math.max(point1[0].x, e.getPoint().x), Math.max(point1[0].y, e.getPoint().y)));
                    Logger.debug("отжал ЛКМ x1 " + point1[0].x + " y1 " + point1[0].y + " x2 " + e.getPoint().x + " y2 " + e.getPoint().y);
                }
            }
        });
        setDoubleBuffered(true);
    }

    protected void setTargetPosition(Point p)
    {
        target.setM_targetPositionX(p.x);
        target.setM_targetPositionY(p.y);
    }
    
    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
    
    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }
    
    protected void onModelUpdateEvent()
    {
        double distance = distance(target.getM_targetPositionX(), target.getM_targetPositionY(),
                robot.getM_robotPositionX(),  robot.getM_robotPositionY());
        if (distance < 0.5)
        {
            return;
        }
        double angleToTarget = angleTo( robot.getM_robotPositionX(),  robot.getM_robotPositionY(), target.getM_targetPositionX(), target.getM_targetPositionY());
        double angularVelocity = 0;
        if (angleToTarget >  robot.getM_robotDirection())
        {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget <  robot.getM_robotDirection())
        {
            angularVelocity = -maxAngularVelocity;
        }
        
        moveRobot(maxVelocity, angularVelocity, 10);
    }
    
    private static double applyLimits(double value, double min, double max)
    {
        return Math.min(max, Math.max(value,min));
    }
    
    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double angleToTarget = angleTo(robot.getM_robotPositionX(), robot.getM_robotPositionY(), target.getM_targetPositionX(), target.getM_targetPositionY());
        if (Math.abs(angleToTarget - robot.getM_robotDirection()) < 0.1) {
            double xValue = robot.getM_robotPositionX() + Math.cos(angleToTarget) * duration * velocity;
            robot.setM_robotPositionX(applyLimits(xValue, Math.max(m_robotDiam1, m_robotDiam2) / 2, this.getWidth() - Math.max(m_robotDiam1, m_robotDiam2) / 2));
            double yValue = robot.getM_robotPositionY() + Math.sin(angleToTarget) * duration * velocity;
            robot.setM_robotPositionY(applyLimits(yValue, Math.max(m_robotDiam1, m_robotDiam2) / 2, this.getHeight() - Math.max(m_robotDiam1, m_robotDiam2) / 2));
        }
        else {
            robot.setM_robotDirection(asNormalizedRadians(robot.getM_robotDirection() + angularVelocity * duration));
        }
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }
    
    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g; 
        drawRobot(g2d, round(robot.getM_robotPositionX()), round(robot.getM_robotPositionY()), robot.getM_robotDirection());
        drawTarget(g2d, target.getM_targetPositionX(), target.getM_targetPositionY());
        for(int i=0; i<barriers.size(); i++){
            Barrier barrier = barriers.get(i);
            drawRectagle(g2d, barrier.getM_barrierPositionX1(), barrier.getM_barrierPositionY1(),
                    barrier.getM_barrierPositionX2(), barrier.getM_barrierPositionY2());
        }
    }
    
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        int robotCenterX = round(robot.getM_robotPositionX());
        int robotCenterY = round(robot.getM_robotPositionY());
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY); 
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, m_robotDiam1, m_robotDiam2);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, m_robotDiam1, m_robotDiam2);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }
    
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private void drawRectagle(Graphics2D g, int x1, int y1, int x2, int y2){
        g.setColor(Color.BLUE);
        g.fillRect(x1, y1, Math.abs(x1-x2), Math.abs(y1-y2));
    }
}
