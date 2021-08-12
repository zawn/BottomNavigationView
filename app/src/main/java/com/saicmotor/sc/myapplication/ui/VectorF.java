package com.saicmotor.sc.myapplication.ui;

import java.util.Objects;

/**
 * 一个包含起点和终点的向量表示.
 *
 * @author zhangzhenli
 */
public class VectorF {
    private PointF pointStart;
    private PointF pointEnd;
    private PointF offsetPointF = null;
    private Float distance = null;
    private Double radians = null;
    private Double degrees = null;

    private void reset() {
        this.offsetPointF = null;
        this.distance = null;
        this.radians = null;
        this.degrees = null;
    }


    public VectorF(float x, float y) {
        this.setPointStart(new PointF(0, 0));
        this.setPointEnd(new PointF(x, y));
    }


    public VectorF(PointF pointF) {
        this.setPointStart(new PointF(0, 0));
        this.setPointEnd(pointF);
    }

    public VectorF(PointF start, PointF end) {
        this.setPointStart(start);
        this.setPointEnd(end);
    }

    public VectorF(float ax, float ay, float bx, float by) {
        this(new PointF(ax, ay), new PointF(bx, by));
    }

    public VectorF() {
        this(0, 0, 0, 0);
    }


    public PointF offset() {
        if (offsetPointF == null) {
            offsetPointF = new PointF(getPointEnd().x - getPointStart().x, getPointEnd().y - getPointStart().y);
        }
        return offsetPointF;
    }

    public float length() {
        if (distance == null) {
            distance = (float) PointF.distance(getPointStart(), getPointEnd());
        }
        return distance;
    }

    public void plus(VectorF vectorF) {
        PointF offset = vectorF.offset();
        this.setPointEnd(new PointF(getPointEnd().x + offset.x, getPointEnd().y + offset.y));
        reset();
    }

    public void plus(float x, float y) {
        plus(new VectorF(x, y));
    }

    public double getDegrees() {
        if (degrees == null) {
            degrees = Math.toDegrees(getRadians());
        }
        return degrees;
    }

    public double getRadians() {
        if (radians == null) {
            radians = Math.atan2(offset().y, offset().x);
        }
        return radians;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VectorF vectorF = (VectorF) o;
        return Objects.equals(getPointStart(), vectorF.getPointStart()) &&
                Objects.equals(getPointEnd(), vectorF.getPointEnd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPointStart(), getPointEnd());
    }

    @Override
    public String toString() {
        return "VectorF{(" + getPointStart().x + ", " + getPointStart().y + ")" +
                ",(" + getPointEnd().x + ", " + getPointEnd().y + ")}" +
                ",(" + offset().x + "," + offset().y + ")" +
                ",length = " + length() + ", angle = " + getDegrees();
    }

    public PointF getPointStart() {
        return pointStart;
    }

    public void setPointStart(PointF pointStart) {
        this.pointStart = pointStart;
        reset();
    }

    public PointF getPointEnd() {
        return pointEnd;
    }

    public void setPointEnd(PointF pointEnd) {
        this.pointEnd = pointEnd;
        reset();
    }
}
