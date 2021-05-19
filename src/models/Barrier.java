package models;

import java.awt.*;
import java.awt.geom.Line2D;

public class Barrier extends Rectangle {
    private volatile int m_barrierPositionX1;
    private volatile int m_barrierPositionY1;
    private volatile int m_barrierPositionX2;
    private volatile int m_barrierPositionY2;

    public Barrier(int m_barrierPositionX1, int m_barrierPositionY1 , int m_barrierPositionX2, int m_barrierPositionY2) {
        this.m_barrierPositionX1 = m_barrierPositionX1;
        this.m_barrierPositionY1 = m_barrierPositionY1;
        this.m_barrierPositionX2 = m_barrierPositionX2;
        this.m_barrierPositionY2 = m_barrierPositionY2;
    }

    public boolean hasInBarrier(Point p){
        return  (p.x > this.m_barrierPositionX1 && p.y > this.m_barrierPositionY1 &&
                p.x < this.m_barrierPositionX2 && p.y < this.m_barrierPositionY2);
    }

    public boolean intersect(Line2D line){
        return this.intersectsLine(line);
    }

    public boolean intersectLines(Point p1, Point p2, int x1, int x2, int y1, int y2){
        boolean intersection = false;
        if(Line2D.linesIntersect(p1.x, p1.y, p2.x, p2.y, x1, y1, x1, y2) ||
                Line2D.linesIntersect(p1.x, p1.y, p2.x, p2.y, x1, y2, x2, y2) ||
                Line2D.linesIntersect(p1.x, p1.y, p2.x, p2.y, x2, y2, x2, y1) ||
                Line2D.linesIntersect(p1.x, p1.y, p2.x, p2.y, x2, y1, x1, y1)){
            intersection = true;
        }
        return intersection;
    }

    public Point getAnother(Point p){
        if(p.equals(new Point(getM_barrierPositionX1(), getM_barrierPositionY1())))
            return new Point(getM_barrierPositionX1()-5, getM_barrierPositionY1()-5);
        else if(p.equals(new Point(getM_barrierPositionX1(), getM_barrierPositionY2())))
            return new Point(getM_barrierPositionX1()-5, getM_barrierPositionY2()+5);
        else if(p.equals(new Point(getM_barrierPositionX2(), getM_barrierPositionY1())))
            return new Point(getM_barrierPositionX2()+5, getM_barrierPositionY1()-5);
        else return new Point(getM_barrierPositionX2()+5, getM_barrierPositionY2()+5);
    }

    public int getM_barrierPositionX1() {
        return m_barrierPositionX1;
    }

    public int getM_barrierPositionX2() {
        return m_barrierPositionX2;
    }

    public int getM_barrierPositionY1() {
        return m_barrierPositionY1;
    }

    public int getM_barrierPositionY2() {
        return m_barrierPositionY2;
    }
}
