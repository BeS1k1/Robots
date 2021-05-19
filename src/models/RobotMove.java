package models;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class RobotMove {
    private static final double maxVelocity = 0.1;
    private Map<Point, ArrayList<Point>> map = new HashMap<>();
    public CopyOnWriteArrayList<Barrier> barriers = new CopyOnWriteArrayList<>();

    volatile Target target = new Target(150, 100);
    volatile Robot robot = new Robot(100, 100, 0, target);

    public void setTargetPosition(Point p)
    {
        target.setM_targetPositionX(p.x);
        target.setM_targetPositionY(p.y);
    }

    public Point getTargetPosition() {
        return new Point(target.getM_targetPositionX(), target.getM_targetPositionY());
    }

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    public Point getRobotPosition() {
        return new Point(round(robot.getM_robotPositionX()), round(robot.getM_robotPositionY()));
    }

    public double getRobotDirection(){
        return robot.getM_robotDirection();
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

    public void onModelUpdateEvent()
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

    private void moveRobot(double velocity, double duration)
    {
        double newX = robot.getM_robotPositionX() + velocity * duration * Math.cos(robot.getM_robotDirection());
        double newY = robot.getM_robotPositionY() + velocity * duration * Math.sin(robot.getM_robotDirection());
        robot.setM_robotPositionX(newX);
        robot.setM_robotPositionY(newY);
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
            if (barrier.intersectLines(p1, p2, barrier.getM_barrierPositionX1(), barrier.getM_barrierPositionX2(),
                    barrier.getM_barrierPositionY1(), barrier.getM_barrierPositionY2())){
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
}
