package com.saicmotor.sc.myapplication.ui;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static java.lang.Math.abs;

/**
 * float类型的Point.
 *
 * @author zhangzhenli
 */
public class PointF {
    public float x;
    public float y;

    public PointF() {
    }

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF(Point src) {
        this.x = src.x;
        this.y = src.y;
    }

    @NotNull
    public static PointF[] fourCornerPointF(RectF rawRectF) {
        PointF[] pointFS = new PointF[4];
        pointFS[0] = new PointF(rawRectF.left, rawRectF.top);
        pointFS[1] = new PointF(rawRectF.right, rawRectF.top);
        pointFS[2] = new PointF(rawRectF.right, rawRectF.bottom);
        pointFS[3] = new PointF(rawRectF.left, rawRectF.bottom);
        return pointFS;
    }

    /**
     * 返回点到矩形的最小，最大夹角
     *
     * @param pointF
     * @param rawRectF
     * @return
     */
    public static double[] getDegrees(PointF pointF, RectF rawRectF) {
        PointF[] p1 = fourCornerPointF(rawRectF);
        Double min = null, max = null;
        for (PointF point : p1) {
            VectorF vectorF = new VectorF(pointF, point);
            double degrees = vectorF.getDegrees();
            if (min == null) {
                min = degrees;
            }
            if (max == null) {
                max = degrees;
            }
            if (min > degrees) {
                min = degrees;
            }
            if (max < degrees) {
                max = degrees;
            }
        }
        return new double[]{min, max};
    }


    /**
     * Set the point's x and y coordinates
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointF pointF = (PointF) o;
        return Float.compare(pointF.x, x) == 0 &&
                Float.compare(pointF.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "PointF(" + x + ", " + y + ")";
    }

    /**
     * 计算两个点之间的距离.
     *
     * @param p1
     * @param p2
     * @return
     */
    public static double distance(PointF p1, PointF p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * 通过三点坐标计算对应顶点的角度.
     *
     * @param p  顶点
     * @param p1
     * @param p2
     * @return
     */
    public static double getDegree(Point p, Point p1, Point p2) {
        //向量的点乘
        long vector = (p1.x - p.x) * (p2.x - p.x) * 1l + (p1.y - p.y) * (p2.y - p.y);
        //向量的模乘
        long i = abs((p1.x - p.x) * (p1.x - p.x)) + abs((p1.y - p.y) * (p1.y - p.y));
        long i1 = abs((p2.x - p.x) * (p2.x - p.x)) + abs((p2.y - p.y) * (p2.y - p.y));
        double sqrt = Math.sqrt(i * i1);
        //反余弦计算弧度
        double radian = Math.acos(vector / sqrt);
        //弧度转角度制
        return Math.toDegrees(radian);
    }

    /**
     * p到p1，p2所在线段的垂足.
     *
     * @param p  线段外的点
     * @param p1 线段上的点1
     * @param p2 线段上的点1
     * @return
     */
    public static PointF footOfPerpendicular(PointF p, PointF p1, PointF p2) {
        PointF foot = new PointF();

        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;

        float u = (p.x - p1.x) * dx + (p.y - p1.y) * dy;
        u /= dx * dx + dy * dy;

        foot.x = p1.x + u * dx;
        foot.y = p1.y + u * dy;

        return foot;
    }

    /**
     * 寻找p到线段p1，p2的最近的点.0
     *
     * @param p
     * @param p1
     * @param p2
     * @return
     */
    public static PointF nearPoint(PointF p, PointF p1, PointF p2) {
        PointF foot = footOfPerpendicular(p, p1, p2);

        float d = abs((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
        float d1 = abs((p1.x - foot.x) * (p1.x - foot.x) + (p1.y - foot.y) * (p1.y - foot.y));
        float d2 = abs((p2.x - foot.x) * (p2.x - foot.x) + (p2.y - foot.y) * (p2.y - foot.y));

        if (d1 > d || d2 > d) {
            if (d1 > d2) return p2;
            else return p1;
        }
        return foot;
    }

    /**
     * 寻找p到Rect最近的点
     *
     * @param p
     * @param rect
     * @return
     */
    public static PointF nearPoint(PointF p, RectF rect) {
        PointF p1 = new PointF(rect.left, rect.top);
        PointF p2 = new PointF(rect.right, rect.top);
        PointF p3 = new PointF(rect.right, rect.bottom);
        PointF p4 = new PointF(rect.left, rect.bottom);

        PointF[] points = new PointF[4];
        points[0] = nearPoint(p, p1, p2);
        points[1] = nearPoint(p, p2, p3);
        points[2] = nearPoint(p, p3, p4);
        points[3] = nearPoint(p, p4, p1);

        double[] distances = new double[4];
        for (int i = 0; i < points.length; i++) {
            distances[i] = distance(p, points[i]);
        }

        int best = 0;
        for (int i = 0; i < distances.length; i++) {
            double distance = distances[i];
            if (Double.compare(distance, distances[best]) < 0) {
                best = i;
            }
        }
        return points[best];
    }

    /**
     * 计算两个矩形区域的距离
     *
     * @param rect1
     * @param rect2
     * @return
     */
    public static float distance(Rect rect1, Rect rect2) {
        double distance;
        RectF rectF1 = toRectF(rect1);
        RectF rectF2 = toRectF(rect2);
        return distance(rectF1, rectF2);
    }


    /**
     * 寻找p到Rect最近的点
     *
     * @param p
     * @param rect
     * @return
     */
    public static PointF nearPoint(PointF p, Rect rect) {
        return nearPoint(p, toRectF(rect));
    }

    /**
     * 转化Rect为RectF.
     *
     * @param rect
     * @return
     */
    @NotNull
    public static RectF toRectF(Rect rect) {
        return new RectF(rect.left, rect.top, rect.right, rect.bottom);
    }

    /**
     * 计算两个矩形区域的距离
     *
     * @param rect1
     * @param rect2
     * @return
     */
    public static float distance(RectF rect1, RectF rect2) {
        float distance;

        //首先计算两个矩形中心点
        PointF c1 = new PointF();
        PointF c2 = new PointF();
        c1.x = rect1.centerX();
        c1.y = rect1.centerY();
        c2.x = rect2.centerX();
        c2.y = rect2.centerY();

        // 分别计算两矩形中心点在X轴和Y轴方向的距离
        float dx, dy;
        dx = Math.abs(c2.x - c1.x);
        dy = Math.abs(c2.y - c1.y);

        if ((dx < ((rect1.width() + rect2.width()) / 2)) && (dy >= ((rect1.height() + rect2.height()) / 2))) {
            // 两矩形不相交，在X轴方向有部分重合的两个矩形，最小距离是上矩形的下边线与下矩形的上边线之间的距离
            distance = dy - ((rect1.height() + rect2.height()) / 2);
        } else if ((dx >= ((rect1.width() + rect2.width()) / 2)) && (dy < ((rect1.height() + rect2.height()) / 2))) {
            // 两矩形不相交，在Y轴方向有部分重合的两个矩形，最小距离是左矩形的右边线与右矩形的左边线之间的距离
            distance = dx - ((rect1.width() + rect2.width()) / 2);
        } else if ((dx >= ((rect1.width() + rect2.width()) / 2)) && (dy >= ((rect1.height() + rect2.height()) / 2))) {
            // 两矩形不相交，在X轴和Y轴方向无重合的两个矩形，最小距离是距离最近的两个顶点之间的距离，
            // 利用勾股定理，很容易算出这一距离
            float delta_x = dx - ((rect1.width() + rect2.width()) / 2);
            float delta_y = dy - ((rect1.height() + rect2.height()) / 2);
            distance = (float) Math.sqrt(delta_x * delta_x + delta_y * delta_y);
        } else {
            // 两矩形相交，最小距离为负值，返回-1
            distance = -1;
        }
        return distance;
    }

    public static Rect toRect(RectF rectF) {
        RectF rectF1 = new RectF(rectF);
        Rect rect = new Rect();
        rectF1.round(rect);
        return rect;
    }

    public static Rect toRect(RectF rawRectF, VectorF vectorF) {
        RectF rectF = new RectF(rawRectF);
        rectF.offset(vectorF.offset().x, vectorF.offset().y);
        Rect rect = new Rect();
        rectF.round(rect);
        return rect;
    }

    public static int absCeil(double a) {
        return (int) (Math.ceil(abs(a)) * (a >= 0 ? 1 : -1));
    }
}
