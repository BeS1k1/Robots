package gui;

import log.Logger;
import models.Barrier;
import models.RobotMove;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.*;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel
{
    private final Timer m_timer = initTimer();
    
    private static Timer initTimer() 
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    private volatile int m_robotDiam1 = 30;
    private volatile int m_robotDiam2 = 10;

    volatile RobotMove robotMove = new RobotMove();
    
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
                robotMove.onModelUpdateEvent();
            }
        }, 0, 10);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getButton()==MouseEvent.BUTTON3) {
                    robotMove.setTargetPosition(e.getPoint());
                    repaint();
                    Logger.debug("Добавлена цель с координатами: х=" + e.getPoint().x + ", у=" + e.getPoint().y);
                }
                if (e.getButton()==MouseEvent.BUTTON2){
                    for (int i =0;i< robotMove.barriers.size();i++){
                        Barrier barrier = robotMove.barriers.get(i);
                        if (barrier.hasInBarrier(e.getPoint())){
                            robotMove.barriers.remove(i);
                            Logger.debug("Удалена преграда");
                        }
                    }
                }
            }
            final Point[] point1 = new Point[1];
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    point1[0] = e.getPoint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    robotMove.barriers.add(new Barrier(Math.min(point1[0].x, e.getPoint().x), Math.min(point1[0].y, e.getPoint().y),
                            Math.max(point1[0].x, e.getPoint().x), Math.max(point1[0].y, e.getPoint().y)));
                    Logger.debug("Добавлена преграда");
                }
            }
        });
        setDoubleBuffered(true);
    }
    
    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g; 
        drawRobot(g2d, robotMove.getRobotPosition().x, robotMove.getRobotPosition().y, robotMove.getRobotDirection());
        drawTarget(g2d, robotMove.getTargetPosition().x, robotMove.getTargetPosition().y);
        for(int i=0; i<robotMove.barriers.size(); i++){
            Barrier barrier = robotMove.barriers.get(i);
            drawRectangle(g2d, barrier.getM_barrierPositionX1(), barrier.getM_barrierPositionY1(),
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
        int robotCenterX = robotMove.getRobotPosition().x;
        int robotCenterY = robotMove.getRobotPosition().y;
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

    private void drawRectangle(Graphics2D g, int x1, int y1, int x2, int y2){
        g.setColor(Color.BLUE);
        g.fillRect(x1, y1, Math.abs(x1-x2), Math.abs(y1-y2));
    }
}
