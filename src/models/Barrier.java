package models;

import java.awt.*;

public class Barrier {
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

    public void setM_barrierPositionX1(int m_barrierPositionX1) {
        this.m_barrierPositionX1 = m_barrierPositionX1;
    }

    public void setM_barrierPositionX2(int m_barrierPositionX2) {
        this.m_barrierPositionX2 = m_barrierPositionX2;
    }

    public void setM_barrierPositionY1(int m_barrierPositionY1) {
        this.m_barrierPositionY1 = m_barrierPositionY1;
    }

    public void setM_barrierPositionY2(int m_barrierPositionY2) {
        this.m_barrierPositionY2 = m_barrierPositionY2;
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
