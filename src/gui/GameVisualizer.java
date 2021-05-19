package gui;

import log.Logger;
import models.Barrier;
import models.Robot;
import models.Target;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private Target target = new Target(150, 100);
    private Robot robot = new Robot(100, 100, 0, target);

    private CopyOnWriteArrayList<Barrier> barriers = new CopyOnWriteArrayList<>();
    
    private static final double maxVelocity = 0.1; 
    private static final double maxAngularVelocity = 0.005;
    volatile Map<Point, ArrayList<Point>> map = new HashMap<>();
    
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
                    Logger.debug("нажал ПКМ координаты " + e.getPoint());
                }
                if (e.getButton()==MouseEvent.BUTTON2){
                    for (int i =0;i< barriers.size();i++){
                        Barrier barrier = barriers.get(i);
                        if (barrier.hasInBarrier(e.getPoint())){
                            barriers.remove(i);
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
                    Logger.debug("отжал ЛКМ");
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

        Point newTarget = algDijkstra();
        robot.setM_robotDirection(angleTo(robot.getM_robotPositionX(),  robot.getM_robotPositionY(), newTarget.x, newTarget.y)) ;
        double velocity = maxVelocity;
        moveRobot(velocity, 10);
    }
    
    private static double applyLimits(double value, double min, double max)
    {
        return Math.min(max, Math.max(value,min));
    }
    private void moveRobot(double velocity, double duration)
    {
        double newX = robot.getM_robotPositionX() + velocity * duration * Math.cos(robot.getM_robotDirection());
        double newY = robot.getM_robotPositionY() + velocity * duration * Math.sin(robot.getM_robotDirection());
        robot.setM_robotPositionX(newX);
        robot.setM_robotPositionY(newY);
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

    private Point algDijkstra() {
        map=new HashMap<>();//коллекция ключей-вершин и значений-списков вершин, до которых они могут дойти
        HashMap<Point,Double> distance = new HashMap<>();//дистанция от стартовой точки до всех
        HashMap<Point,Point> prev = new HashMap<>();//список предыдущих, по которому восстановится маршрут
        ArrayList<Point> track = new ArrayList<>();
        ArrayList<Point> vertices = new ArrayList<>();//множество вершин
        Point finish = new Point(target.getM_targetPositionX(), target.getM_targetPositionY());
        Point start = new Point((int) robot.getM_robotPositionX(), (int) robot.getM_robotPositionY());
        vertices.add(finish);
        for (int i =0;i< barriers.size();i++) {
            ArrayList<Point> list = new ArrayList<>();
            Barrier barrier = barriers.get(i);
            list.add(new Point(barrier.getM_barrierPositionX1(), barrier.getM_barrierPositionY1()));//добавляю все точки препятствий
            list.add(new Point(barrier.getM_barrierPositionX1(), barrier.getM_barrierPositionY2()));
            list.add(new Point(barrier.getM_barrierPositionX2(), barrier.getM_barrierPositionY1()));
            list.add(new Point(barrier.getM_barrierPositionX2(), barrier.getM_barrierPositionY2()));
            for (Point vertex : list) {//для каждой точки препятствия
                Point anotherVertex = barrier.getAnother(vertex);
                if (distance(robot.getM_robotPositionX(), robot.getM_robotPositionY(), vertex.x, vertex.y) <= 2)//чтобы робот случайно не зацепил угол фигуры
                    return anotherVertex;
                vertices.add(anotherVertex);
                for (int j=i;j<barriers.size();j++) {//построение графа со всеми вершинами препятствий
                    Barrier barrier2 = barriers.get(j);
                    mappingLines(anotherVertex, new Point(barrier2.getM_barrierPositionX1()-5, barrier2.getM_barrierPositionY2()+5));
                    mappingLines(anotherVertex, new Point(barrier2.getM_barrierPositionX2()+5, barrier2.getM_barrierPositionY2()+5));
                    mappingLines(anotherVertex, new Point(barrier2.getM_barrierPositionX2()+5, barrier2.getM_barrierPositionY1()-5));
                    mappingLines(anotherVertex, new Point(barrier2.getM_barrierPositionX1()-5, barrier2.getM_barrierPositionY1()-5));
                }
                if(barrier.contains(start)||barrier.contains(finish)){
                    return start;
                }
                mappingLines(start, anotherVertex);//достроение графа точкой старта и финиша
                mappingLines(anotherVertex, finish);
            }

        }
        if (barriers.isEmpty()) {
            return finish;
        }

        mappingLines(start, finish);
        for (Point p : map.keySet()) {
            if (map.containsKey(start)) {
                if (map.get(start).contains(p)) {//инициализация
                    map.get(p).remove(start);
                    distance.put(p, distance(start.x, start.y, p.x, p.y));
                    prev.put(p, start);//старт - перед p
                } else distance.put(p, 1000000.0);
            }
        }
        map.remove(finish);
        int n = vertices.size();//количество вершин
        for (int k = 1; k < n; k++) {//количество итераций
            Point w = minV(vertices, distance);//беру точку с наименьшим расстоянием от начала

            vertices.remove(w);//удаляю из множества вершин
            if(!map.containsKey(w))
                continue;
            for (Point v : map.get(w)) {
                if (distance.containsKey(v)) {
                    if (distance.get(w) + distance(w.x, w.y, v.x, v.y) < distance.get(v)) {
                        distance.put(v, distance.get(w) + distance(w.x, w.y, v.x, v.y));
                        prev.put(v, w);
                    }
                } else {
                    if (distance.containsKey(w)) {
                        distance.put(v, distance.get(w) + distance(w.x, w.y, v.x, v.y));
                        prev.put(v, w);
                    }
                }
            }

        }
        try {//возврат ближайшей точки
            Point t = finish;
            track.add(t);
            while (!(t.equals(start))) {
                t = prev.get(t);
                track.add(t);
            }
            if (track.size() == 2)//если вершины 2, то это старт и финиш
                return finish;
            else if (track.size() > 2)//если больше, то сразу после стартовой ближайшая
                return track.get(track.size() - 2);//стартовая записана последней
            else return start;//необъяснимая ситуация, вернуть стартовую точку
        } catch (NullPointerException e) {
            if (track.size() > 2)
                return track.get(1);
            else return start;
        }
    }

    private void mappingLines(Point p1, Point p2) {
        boolean intersection = false;
        if (p1.equals(p2))
            return;
        for (int i = 0; i< barriers.size(); i++) {
            Barrier barrier = barriers.get(i);
            if (barrier.intersect(new Line2D.Double(p1.x, p1.y, p2.x, p2.y))) {
                intersection = true;
                break;
            }
            if (barrier.getM_barrierPositionX1()==p1.x && barrier.getM_barrierPositionX1() == p2.x ||
                    barrier.getM_barrierPositionX2()== p1.x && barrier.getM_barrierPositionX2() == p2.x){
                intersection = true;
                break;
            }
            if (barrier.intersectLines(p1, p2, barrier.getM_barrierPositionX1(), barrier.getM_barrierPositionX2(), barrier.getM_barrierPositionY1(), barrier.getM_barrierPositionY2())){
                intersection = true;
                break;
            }
        }
        if(!intersection) {
            if (!map.containsKey(p1))
                map.put(p1, new ArrayList<>());
            ArrayList<Point> list = map.get(p1);
            list.add(p2);
            if (!map.containsKey(p2))
                map.put(p2, new ArrayList<>());
            list = map.get(p2);
            list.add(p1);
        }
    }

    private Point minV(ArrayList<Point> list, HashMap<Point,Double> distance) {
        Point min= list.get(0);
        for (Point p : list) {
            if (distance.containsKey(p) && distance.containsKey(min))
                if (distance.get(p) < distance.get(min))
                    min = p;
        }
        return min;
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

    private void drawRectangle(Graphics2D g, int x1, int y1, int x2, int y2){
        g.setColor(Color.BLUE);
        g.fillRect(x1, y1, Math.abs(x1-x2), Math.abs(y1-y2));
    }
}
