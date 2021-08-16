package com.saicmotor.sc.myapplication.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.github.mikephil.charting.data.PieEntry;
import com.saicmotor.sc.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 饼图，圆环的宽度与百分比关联.
 *
 * @author zhangzhenli
 */
public class PieChart extends View {
    private static final String TAG = "PieChart";
    private static final boolean DEBUG = false;

    private static final int Default_Title_Color = 0xFF000000;
    private static final int Default_Pie_Text_Color = 0xFFFFFFFF;
    public static final float TRY_MOVE_DEGREES = 0.2f;

    private Paint mPaint;
    // 圆环边距
    private int mPiePadding;

    // 图例文字，四周的文字
    private Paint mTitlePaint;
    private Paint mUnitTextPaint;
    // 圆环上面的文字
    private TextPaint mPieTextPaint;

    private Paint mPaintCircle;
    private RectF mOval;

    // 环形直径
    private int mDiameter;
    private float mPieTextSize;
    private int mUnitTextSize;
    private float mTitleSize;
    private int mTitleColor;
    private final int mPieTextColor;

    private int mStartAngle;
    private ArrayList<PieEntry> mEntries;
    private Paint[] mPaints;
    private float[] mSweepAngles;
    private float[] mPercents;
    private float[] mStrokeWidths;
    private ArrayList<Integer> mColors;
    private Region mAvailableRegion;
    private Region mAllRegion;
    private Region mPieRegion;
    private PointF mCenterPoint;
    private RectF[] mTextRectF;
    private float[] mTextHeight;
    private Region[] mCircleRegions;
    private TextPaint[] mTextPaints;
    private float[] mTextDegrees;
    private int mTryMoveCount;
    private int mTitlePadding;

    public void setForegroundColorStartAngle(int mStartAngle) {
        this.mStartAngle = mStartAngle;
    }


    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PieChart,
                0, 0);
        try {
            mStartAngle = a.getInt(R.styleable.PieChart_pieStart, 0);
            mTitleColor = a.getColor(R.styleable.PieChart_pieTextColor, Default_Title_Color);
            mTitleSize = a.getDimensionPixelSize(R.styleable.PieChart_pieTextSize, (int) getDimensionFromSP(14));
            mTitlePadding = a.getDimensionPixelSize(R.styleable.PieChart_pieTitlePadding, (int) getDimensionFromDP(8));

            mUnitTextSize = a.getDimensionPixelSize(R.styleable.PieChart_pieUnitTextSize, (int) getDimensionFromSP(10));

            mPieTextSize = a.getDimensionPixelSize(R.styleable.PieChart_pieTextSize, (int) getDimensionFromSP(10));
            mPieTextColor = a.getColor(R.styleable.PieChart_pieTextColor, Default_Pie_Text_Color);
            mPiePadding = a.getDimensionPixelSize(R.styleable.PieChart_piePadding, (int) getDimensionFromDP(16));
        } finally {
            a.recycle();
        }
        initView();
    }

    public PieChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChart(Context context) {
        this(context, null, 0);
    }

    private float getDimensionFromSP(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

    private float getDimensionFromDP(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(getResources().getColor(R.color.black));

        mPaintCircle = new Paint(mPaint);
        mPaintCircle.setColor(0xD50000);

        mPieTextPaint = new TextPaint();
        mPieTextPaint.setTextAlign(Paint.Align.CENTER);
        mPieTextPaint.setTextSize(mPieTextSize);
        mPieTextPaint.setTypeface(Typeface.MONOSPACE);
        mPieTextPaint.setAntiAlias(true);
        mPieTextPaint.setColor(mPieTextColor);

        mTitlePaint = new Paint();
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTypeface(Typeface.MONOSPACE);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setAntiAlias(true);

        mUnitTextPaint = new Paint();
        mUnitTextPaint.setTypeface(Typeface.MONOSPACE);
        mUnitTextPaint.setTextAlign(Paint.Align.LEFT);
        mUnitTextPaint.setTextSize(mUnitTextSize);
        mUnitTextPaint.setAntiAlias(true);

        mOval = new RectF();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        addSampleData();
    }

    private void addSampleData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(300F, "Idle speed"));
        entries.add(new PieEntry(660F, "1-30km/h"));
        entries.add(new PieEntry(1200F, "31-60km/h"));
        entries.add(new PieEntry(1740F, "61-80km/h"));
        entries.add(new PieEntry(2010F, "81-100km/h"));
        entries.add(new PieEntry(900F, ">100km/h"));
        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed0));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed1));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed2));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed3));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed4));
        colors.add(getResources().getColor(R.color.map_vehicle_state_speed5));
        mColors = colors;
        mEntries = entries;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        // Figure out how big we can make the pie.
        mDiameter = (int) Math.min(ww, hh);
        mDiameter = mDiameter - mPiePadding * 2;

        int centerX = (w - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
        int centerY = (h - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();
        mCenterPoint = new PointF(centerX, centerY);

        float r = mDiameter / 2;
        r = r - r / 4F;
        mOval.set(centerX - r, centerY - r, centerX + r, centerY + r);
        mDiameter = (int) (mOval.right - mOval.left);

        prepareData(mEntries, mColors);
        prepareText();
        mTryMoveCount = 0;
    }

    private void prepareData(ArrayList<PieEntry> entries, ArrayList<Integer> colors) {
        int size = entries.size();
        if (size != colors.size()) {
            throw new UnsupportedOperationException("The entries size must be the same as the colors size");
        }
        int d = this.mDiameter;
        float count = 0;
        for (PieEntry entry : entries) {
            float value = entry.getValue();
            count = count + value;
        }
        mPercents = new float[size];
        for (int i = 0; i < size; i++) {
            PieEntry entry = entries.get(i);
            float percentage = entry.getValue() * 100f / count;
            mPercents[i] = percentage;
        }
        float maxPercent = 0;
        for (float percent : mPercents) {
            if (Double.compare(percent, maxPercent) > 0) {
                maxPercent = percent;
            }
        }

        float minPercent = 0;
        for (float percent : mPercents) {
            if (Double.compare(percent, minPercent) < 0) {
                minPercent = percent;
            }
        }
        mSweepAngles = new float[size];
        for (int i = 0; i < mPercents.length; i++) {
            mSweepAngles[i] = 360 * mPercents[i] / 100;
        }

        float r = d / 2;
        float maxStrokeWidth = r / 2F;
        float minStrokeWidth = maxStrokeWidth / 5;
        mStrokeWidths = new float[size];
        for (int i = 0; i < mPercents.length; i++) {
            float percent = mPercents[i];
            mStrokeWidths[i] = (maxStrokeWidth - minStrokeWidth) * (percent - minPercent) / (maxPercent - minPercent) + minStrokeWidth;
        }
        Paint paints = new Paint();
        paints.setAntiAlias(true);
        paints.setStyle(Paint.Style.FILL);
        mPaints = new Paint[size];
        for (int i = 0; i < mStrokeWidths.length; i++) {
            Paint paint = new Paint(paints);
            paint.setStyle(Paint.Style.STROKE);
            float strokeWidth = mStrokeWidths[i];
            paint.setStrokeWidth(strokeWidth);
            paint.setColor(colors.get(i));
            mPaints[i] = paint;
        }


        mAllRegion = new Region();
        mAllRegion.set(0, 0, getWidth(), getBottom());

        mAvailableRegion = new Region();
        mAvailableRegion.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getBottom() - getPaddingBottom());

        //设置paint
        float startDegrees = mStartAngle;

        mCircleRegions = new Region[mSweepAngles.length];
        for (int j = 0; j < mSweepAngles.length; j++) {
            float sweepAngle = mSweepAngles[j];
            float strokeWidth = mStrokeWidths[j];

            int d1 = (int) (mDiameter - strokeWidth);
            d1 = d1 / 2;
            RectF baseRectF = new RectF(mCenterPoint.x - d1, mCenterPoint.y - d1, mCenterPoint.x + d1, mCenterPoint.y + d1);

            Path path = new Path();
            //画一个弧。注意，角度的设置。这样设置，正方型的边长就是直径。方便计算。
            path.addArc(baseRectF, startDegrees, sweepAngle);

            //向外扩张这个正方形。此时正方形的边长是外弧的直径。
            baseRectF.inset(strokeWidth * -1, strokeWidth * -1);
            path.arcTo(baseRectF, startDegrees + sweepAngle, sweepAngle * -1); //注意角度的设置要反转一下。
            path.close();

            //取path和region的交集
            Region circleRegion = new Region();
            circleRegion.setPath(path, mAllRegion);
            Paint paint = new Paint();
            paint.setColor(mColors.get(j));
            paint.setStyle(Paint.Style.FILL);

            mCircleRegions[j] = circleRegion;

            startDegrees = startDegrees + sweepAngle;
        }
        mPieRegion = new Region();
        for (int i = 0; i < mCircleRegions.length; i++) {
            mPieRegion.op(mCircleRegions[i], Region.Op.UNION);
        }

        Path path = new Path();
        path.addCircle(mCenterPoint.x, mCenterPoint.y, mDiameter / 2, Path.Direction.CCW);

        Region circleRegion = new Region();
        circleRegion.setPath(path, mAllRegion);

        mPieRegion.op(circleRegion, Region.Op.UNION);
    }

    private void prepareText() {
        float startDegrees;
        mTextRectF = new RectF[mCircleRegions.length];
        mTextPaints = new TextPaint[mCircleRegions.length];
        mTextHeight = new float[mCircleRegions.length];
        for (int i = 0; i < mColors.size(); i++) {
            TextPaint textPaint = new TextPaint(mTitlePaint);
            textPaint.setColor(mColors.get(i));
            mTextPaints[i] = textPaint;
        }
        mTextDegrees = new float[mSweepAngles.length];
        startDegrees = 0;
        for (int i = 0; i < mSweepAngles.length; i++) {
            float sweepAngle = mSweepAngles[i];
            float strokeWidth = mStrokeWidths[i];

            float v = mStartAngle + startDegrees + sweepAngle / 2;
            float ang = v % 360;
            mTextDegrees[i] = ang >= 0 ? ang : ang + 360;
            double radians = Math.toRadians(v);
            double x1 = Math.cos(radians) * (mDiameter / 2F + strokeWidth / 2F);
            double y1 = Math.sin(radians) * (mDiameter / 2F + strokeWidth / 2F);
            float x = (float) (mCenterPoint.x + x1);
            float y = (float) (mCenterPoint.y + y1);

            PieEntry pieEntry = mEntries.get(i);


            String text = pieEntry.getLabel();
            Rect rect = new Rect();
            mTextPaints[i].getTextBounds(text, 0, text.length(), rect);

            int textWidth = rect.left - rect.right;
            int textHeight = rect.top - rect.bottom;

            mTextHeight[i] = textHeight;

            int p = mTitlePadding;
            RectF textRectF = new RectF(x + textWidth / 2 - p, y + textHeight / 2 - p, x - textWidth / 2 + p, y - textHeight / 2 + p);
            mTextRectF[i] = textRectF;
            startDegrees = startDegrees + sweepAngle;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw() called with: canvas ");
        // 绘制Padding参考线
        if (DEBUG) {
            drawRegion(canvas, mAvailableRegion, mPaint);
        }

        // 绘制圆环
        if (mSweepAngles != null && mSweepAngles.length > 0) {
            float startDegrees = 0;
            for (int i = 0; i < mSweepAngles.length; i++) {
                float sweepAngle = mSweepAngles[i];
                Paint paint = mPaints[i];
                canvas.drawArc(mOval, (float) (mStartAngle + startDegrees), sweepAngle, false, paint);
                startDegrees = startDegrees + sweepAngle;
            }
        }


        if (DEBUG) {
            //绘制基线圆环（测试参考线）
            Paint paint1 = new Paint();
            paint1.setAntiAlias(true);
            paint1.setStyle(Paint.Style.FILL);
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setStrokeWidth(1);
            paint1.setColor(getResources().getColor(R.color.black));
            canvas.drawArc(mOval, 0, 360, false, paint1);
        }

        // 绘制圆环百分比数字
        if (mSweepAngles != null && mSweepAngles.length > 0) {
            float startDegrees = 0;
            for (int i = 0; i < mSweepAngles.length; i++) {
                float sweepAngle = mSweepAngles[i];
                float v = mStartAngle + startDegrees + sweepAngle / 2;

                double radians = Math.toRadians(v);
                double x1 = Math.cos(radians) * (mDiameter / 2F);
                double y1 = Math.sin(radians) * (mDiameter / 2F);
                float x = (float) (mCenterPoint.x + x1);
                float y = (float) (mCenterPoint.y + y1);


                Rect rect = new Rect();
                String text = (int) mPercents[i] + "%";
                mPieTextPaint.getTextBounds(text, 0, text.length(), rect);
                Paint.FontMetricsInt fontMetricsInt = mPieTextPaint.getFontMetricsInt();
                int j = fontMetricsInt.descent + fontMetricsInt.ascent;
                float y2 = y - j / 2;
                canvas.drawText(text, x, y2, mPieTextPaint);

                if (DEBUG)
                    canvas.drawCircle(x, y, 4, mPaintCircle);

                startDegrees = startDegrees + sweepAngle;
            }
        }

        // 绘制图例折线
        if (mSweepAngles != null && mSweepAngles.length > 0) {
            float startDegrees = 0;
            for (int i = 0; i < mSweepAngles.length; i++) {
                float sweepAngle = mSweepAngles[i];
                float strokeWidth = mStrokeWidths[i];

                float angleStart = mStartAngle + startDegrees;
                float angleEnd = angleStart + sweepAngle;
                float v = angleStart + sweepAngle / 2;

                double radians = Math.toRadians(v);
                double x1 = Math.cos(radians) * (mDiameter / 2F + strokeWidth / 2F);
                double y1 = Math.sin(radians) * (mDiameter / 2F + strokeWidth / 2F);
                float x = (float) (mCenterPoint.x + x1);
                float y = (float) (mCenterPoint.y + y1);

                // 辅助点，标明折线起始位置
                if (DEBUG)
                    canvas.drawCircle(x, y, 4, mPaintCircle);

                PointF pointF0 = new PointF(x, y);

                float height = mTextHeight[i];
                RectF rectF = mTextRectF[i];
                // 选择一个是的起始点
                double[] degrees0 = PointF.getDegrees(mCenterPoint, rectF);
                boolean b = degrees0[1] - degrees0[0] > 180;
                if (b) {
                    double v1 = degrees0[0] + 360;
                    degrees0[0] = degrees0[1];
                    degrees0[1] = v1;
                }
                degrees0[0] = formatPositive(degrees0[0]);
                degrees0[1] = formatPositive(degrees0[1]);
                if (degrees0[0] > degrees0[1]) {
                    degrees0[1] = degrees0[1] + 360;
                }

                double[] degrees1 = new double[]{angleStart, angleEnd};
                degrees1[0] = formatPositive(degrees1[0]);
                degrees1[1] = formatPositive(degrees1[1]);
                if (degrees1[0] > degrees1[1]) {
                    degrees1[1] = degrees1[1] + 360;
                }

                // 寻找重合角
                double[] degrees = new double[]{0, 0};
                degrees[0] = Math.max(degrees0[0], degrees1[0]);
                degrees[1] = Math.min(degrees0[1], degrees1[1]);

                if (degrees[0] > degrees[1]) {
                    double v1 = degrees[0];
                    degrees[0] = degrees[1];
                    degrees[1] = v1;
                    double d;
                    if (degrees[0] >= degrees1[1]) {
                        d = degrees1[1] - sweepAngle / 5;
                    } else {
                        d = degrees1[0] + sweepAngle / 5;
                    }
                    pointF0 = getPiePointFByDegrees((float) d);
                } else {
                    float d = (float) ((degrees[0] + degrees[1]) / 2);
                    pointF0 = getPiePointFByDegrees(d);
                }


                VectorF vectorF = PointF.nearLine(pointF0, rectF);
                double degree1 = PointF.getDegree(vectorF.getPointStart(), vectorF.getPointEnd(), pointF0);
                double degree2 = PointF.getDegree(vectorF.getPointEnd(), vectorF.getPointStart(), pointF0);


                PointF pointF1;
                PointF pointF2; // 最近的端点
                if (degree1 >= degree2) {
                    pointF2 = vectorF.getPointStart();
                } else {
                    pointF2 = vectorF.getPointEnd();
                }
                PointF[] points = PointF.fourCornerPointF(rectF);
                int i1;
                for (i1 = 0; i1 < points.length; i1++) {
                    if (pointF2.equals(points[i1])) {
                        break;
                    }
                }
                int left = (i1 - 1 + 4) % 4;
                int right = (i1 + 1) % 4;
                VectorF vectorF1 = new VectorF(mCenterPoint, pointF0);
                VectorF vectorF11 = new VectorF(points[left], pointF2);
                VectorF vectorF12 = new VectorF(points[right], pointF2);

                degree1 = PointF.getDegree(pointF2, pointF0, points[left]);
                degree2 = PointF.getDegree(pointF2, pointF0, points[right]);
                PointF pointF4;
                if (degree1 > 90 || degree2 > 90) {
                    if (degree1 < degree2) {
                        pointF4 = points[left];
                    } else {
                        pointF4 = points[right];
                    }
                } else {
                    degree1 = PointF.getDegree(vectorF1, new VectorF(pointF0, vectorF11.getCenter()));
                    degree2 = PointF.getDegree(vectorF1, new VectorF(pointF0, vectorF12.getCenter()));
                    if (degree1 < degree2) {
                        pointF4 = points[left];
                    } else {
                        pointF4 = points[right];
                    }
                }


                vectorF = new VectorF(pointF2, pointF4);

                if (DEBUG)
                    canvas.drawCircle(pointF2.x, pointF2.y, 8, mPaintCircle);

                pointF1 = vectorF.getCenter();

                degree1 = PointF.getDegree(vectorF1, new VectorF(pointF0, pointF1));
                if (degree1 > 70) {
                    pointF1 = new VectorF(pointF2, pointF1).getCenter();
                }

                if (DEBUG)
                    canvas.drawCircle(pointF4.x, pointF4.y, 8, mPaintCircle);
                double degrees2 = vectorF.getDegrees();
                PointF pointF5;
                if (degrees2 % 180 != 0) {
                    pointF5 = new PointF((pointF1.x + pointF0.x) / 2, pointF1.y);
                } else {
                    pointF5 = new PointF(pointF1.x, (pointF1.y + pointF0.y) / 2);
                }

                // 纠正折线在文字内部
                boolean contains = rectF.contains(pointF5.x, pointF5.y);
                if (contains) {
                    pointF5 = pointF2;
                    PointF end = new PointF(rectF.centerX(), rectF.centerY());

                    VectorF vectorF13 = new VectorF(pointF2, end);
                    double degrees3 = vectorF13.getDegrees();
                    double toRadians = Math.toRadians(degrees3);
                    float x2 = (float) (Math.cos(toRadians) * mTitlePadding);
                    float y2 = (float) (Math.sin(toRadians) * mTitlePadding);
                    pointF1 = new PointF(pointF2.x + x2, pointF2.y + y2);
                }
                double degree = PointF.getDegree(pointF5, pointF0, pointF1);
                if (degree < 90) {
                    pointF5 = pointF0;
                }

                // 辅助点，标明折线起始位置
                if (DEBUG)
                    canvas.drawCircle(pointF1.x, pointF1.y, 4, mPaint);
                if (DEBUG) {
                    canvas.drawRect(rectF, mPaint);
                }
                Paint paint = new Paint(mPaint);
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(3);
                paint.setColor(mColors.get(i));
                canvas.drawLine(pointF0.x, pointF0.y, pointF5.x, pointF5.y, paint);
                canvas.drawLine(pointF5.x, pointF5.y, pointF1.x, pointF1.y, paint);

                if (DEBUG) {
//                    PointF dp0 = getPiePointFByDegrees((float) degrees[0]);
//                    PointF dp1 = getPiePointFByDegrees((float) degrees[1]);
//                    canvas.drawLine(dp0.x, dp0.y, mCenterPoint.x, mCenterPoint.y, paint);
//                    canvas.drawLine(dp1.x, dp1.y, mCenterPoint.x, mCenterPoint.y, paint);
                }
                // 已经找好了起始点pointF0，终点pointF1，寻找一个中间点，画一条折线


                String text = mEntries.get(i).getLabel();
                canvas.drawText(text, rectF.centerX(), rectF.centerY() - height / 2, mTextPaints[i]);

                startDegrees = startDegrees + sweepAngle;
            }
        }

        if (!isInEditMode()) {
            boolean invalidate = isInvalidate(canvas);
            if (invalidate) {
                invalidate();
            }
        }
    }

    private boolean isInvalidate() {
        return isInvalidate(null);
    }

    private boolean isInvalidate(Canvas canvas) {
        Log.d(TAG, "isInvalidate() called with: canvas = [" + "" + "]");
        VectorF[] vectorFs = new VectorF[mTextRectF.length];
        float startDegrees = mStartAngle;
        for (int i = 0; i < mTextRectF.length; i++) {
            float endDegrees = startDegrees + mSweepAngles[i];

            RectF rawRectF = new RectF(mTextRectF[i]);
            VectorF vectorF = new VectorF();
            // 检查与饼图是否有交集
            Rect rect1 = PointF.toRect(rawRectF, vectorF);
            Region tR1 = new Region(rect1);
            boolean op = tR1.op(mPieRegion, Region.Op.INTERSECT);

            PointF nearPoint;
            // 不相交的时候，需要考虑不规则饼图
            nearPoint = PointF.nearPoint(mCenterPoint, rawRectF);
            float degrees = (float) new VectorF(mCenterPoint, nearPoint).getDegrees();
            PointF piePointFByDegrees = getPiePointFByDegrees(degrees);
            vectorF = new VectorF(nearPoint, piePointFByDegrees);

            RectF rectF = op ? PointF.toRectF(tR1.getBounds()) : rawRectF;
            double[] degrees1 = PointF.getDegrees(mCenterPoint, rectF);
            float sd = mStartAngle;
            ArrayList<VectorF> arrayList = new ArrayList<>();
            for (int j = 0; j < mTextRectF.length; j++) {
                float ed = sd + mSweepAngles[j];
                if (degrees1[0] <= sd && sd <= degrees1[1]) {
                    PointF pointF = getPiePointFByDegrees(sd);
                    PointF n1 = PointF.nearPoint(pointF, rawRectF);
                    VectorF vectorF1 = new VectorF(n1, pointF);
                    if (vectorF1.length() == 0) {
                        arrayList.add(vectorF1);
                    } else {
                        double degree = PointF.getDegree(vectorF1, vectorF);
                        if (-90 <= degree && degree <= 90) {
                            arrayList.add(vectorF1);
                        }
                    }
                }
                sd = ed;
            }
            int unit = 1;
            if (op) {
                unit = -1;
            }

            float length = Float.MAX_VALUE * unit;
            int i2 = -1;
            for (int i1 = 0; i1 < arrayList.size(); i1++) {
                VectorF vectorF1 = arrayList.get(i1);

                if (DEBUG && canvas != null) {
                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(1);
                    paint.setColor(getResources().getColor(R.color.light_green_A700));
                    drawVectorF(canvas, vectorF1, paint);
                }

                float length1 = vectorF1.length() * unit;
                if (length > length1) {
                    i2 = i1;
                    length = length1;
                }
            }
            if (length < vectorF.length() * unit && i2 >= 0) {
                vectorF = arrayList.get(i2);
            }
            degrees = (float) new VectorF(mCenterPoint, vectorF.getPointEnd()).getDegrees();
            if (DEBUG && canvas != null) {
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(getResources().getColor(R.color.blue_gray_900));
                drawVectorF(canvas, vectorF, paint);
            }

            // 检查图例是否有与数组前后交叉交叉,否超过边界
            int left = i - 1;
            if (left < 0) {
                left = mTextRectF.length + left;
            }
            double d1 = PointF.distance(rawRectF, mTextRectF[left]);
            int right = (i + 1) % mTextRectF.length;
            double d2 = PointF.distance(rawRectF, mTextRectF[right]);
            boolean b = false;
            if (d1 <= 0 && d2 > 0) {
                degrees = degrees + TRY_MOVE_DEGREES;
                b = true;
            }
            if (d2 <= 0 && d1 > 0) {
                degrees = degrees - TRY_MOVE_DEGREES;
                b = true;
            }
            if (b) {
                PointF pointEnd = vectorF.getPointEnd();
                PointF piePointF = getCirclePointF(degrees, new VectorF(mCenterPoint, pointEnd).length());
                VectorF vectorF1 = new VectorF(pointEnd, piePointF);
                vectorF.plus(vectorF1);
            }

            boolean opBorder2 = checkOverflowBorder(rawRectF, vectorF);
            // 当前与饼图相接，而且已经触边，但是无法移动了，卡住了，查看最近的两个画布角落，选择一个图例不是最多的移动下
            if (op && opBorder2 && vectorF.length() == 0) {
                int[] ints = new int[mTextDegrees.length];
                for (float d : mTextDegrees) {
                    int v = (int) (d / 90);
                    ints[v] = ints[v] + 1;
                }
                int[] ints2 = new int[mTextDegrees.length];
                for (int i1 = 0; i1 < mTextDegrees.length; i1++) {
                    int v = (int) (mTextDegrees[i] / 90);
                    ints2[i] = v;
                }
                degrees = degrees % 360;
                if (degrees < 0) {
                    degrees = degrees + 360;
                }
                if (degrees % 90 == 0) {
                    int v = (int) (degrees / 90);
                    int anInt1 = ints2[i];
                    ints[anInt1] = ints[anInt1] - 1;
                    int i1 = v - 1;
                    if (i1 < 0) {
                        i1 = ints.length + i1;
                    }
                    if (ints[i1] >= ints[v]) {
                        degrees = degrees + 1;
                    } else {
                        degrees = degrees - 1;
                    }
                    vectorF.setPointEnd(getPiePointFByDegrees(degrees));
                    checkOverflowBorder(rawRectF, vectorF);
                }
            }
            if (vectorF.length() > 0) {
                vectorFs[i] = vectorF;
            } else {
                vectorFs[i] = null;
            }
            startDegrees = endDegrees;
        }
        boolean invalidate = false;
        for (int i = 0; i < mTextRectF.length; i++) {
            RectF rectF = mTextRectF[i];
            VectorF vectorF = vectorFs[i];
            if (vectorF != null) {
                rectF.offset(vectorF.offset().x, vectorF.offset().y);
                invalidate = true;
            }
        }
        mTryMoveCount++;
        if (mTryMoveCount > 2000) {
            return false;
        }
        return invalidate;
    }

    private void drawVectorF(Canvas canvas, VectorF vectorF1, Paint paint) {
        int height = 8;
        int bottom = 8;
        drawEndpoint(canvas, vectorF1, height, bottom, paint);

        canvas.drawLine(vectorF1.getPointStart().x, vectorF1.getPointStart().y,
                vectorF1.getPointEnd().x, vectorF1.getPointEnd().y, paint);
    }

    private void drawEndpoint(Canvas canvas, VectorF vectorF1, int height, int bottom, Paint paint) {
        if (vectorF1.length() > height) {
            canvas.drawCircle(vectorF1.getPointStart().x, vectorF1.getPointStart().y, 4, paint);
        }
        if (vectorF1.length() == 0) {
            canvas.drawCircle(vectorF1.getPointStart().x, vectorF1.getPointStart().y, 4, paint);
        }
        float juli = (float) Math.sqrt((vectorF1.getPointEnd().x - vectorF1.getPointStart().x) * (vectorF1.getPointEnd().x - vectorF1.getPointStart().x)
                + (vectorF1.getPointEnd().y - vectorF1.getPointStart().y) * (vectorF1.getPointEnd().y - vectorF1.getPointStart().y));// 获取线段距离
        float juliX = vectorF1.getPointEnd().x - vectorF1.getPointStart().x;// 有正负，不要取绝对值
        float juliY = vectorF1.getPointEnd().y - vectorF1.getPointStart().y;// 有正负，不要取绝对值
        float dianX = vectorF1.getPointEnd().x - (height / juli * juliX);
        float dianY = vectorF1.getPointEnd().y - (height / juli * juliY);
        float dian2X = vectorF1.getPointStart().x + (height / juli * juliX);
        float dian2Y = vectorF1.getPointStart().y + (height / juli * juliY);
        //终点的箭头
        Path path = new Path();
        path.moveTo(vectorF1.getPointEnd().x, vectorF1.getPointEnd().y);// 此点为三边形的起点
        path.lineTo(dianX + (bottom / juli * juliY), dianY
                - (bottom / juli * juliX));
        path.lineTo(dianX - (bottom / juli * juliY), dianY
                + (bottom / juli * juliX));
        path.close(); // 使这些点构成封闭的三边形
        canvas.drawPath(path, paint);
    }

    private boolean checkOverflowBorder(RectF rawRectF, VectorF vectorF) {
        // 检查是否超过边界
        Rect rect2 = PointF.toRect(rawRectF, vectorF);
        Region tR2 = new Region(rect2);
        boolean op = tR2.op(mAvailableRegion, Region.Op.DIFFERENCE);
        if (op) {
            int dTop, dBottom, dLeft, dRight = 0;
            Rect bounds = tR2.getBounds();
            dTop = bounds.top - getPaddingTop();
            dBottom = (getHeight() - getPaddingBottom()) - bounds.bottom;
            dLeft = bounds.left - getPaddingLeft();
            dRight = getWidth() - getPaddingRight() - bounds.right;

            if (dLeft < 0) {
                vectorF.plus(-dLeft, 0);
            }
            if (dRight < 0) {
                vectorF.plus(dRight, 0);
            }
            if (dTop < 0) {
                vectorF.plus(0, -dTop);
            }
            if (dBottom < 0) {
                vectorF.plus(0, dBottom);
            }
        }
        return op;
    }

    /**
     * 获取饼图上对应角度的点
     *
     * @param angdeg
     * @return
     */
    private PointF getPiePointFByDegrees(float angdeg) {
        float pieRadius = pieRadiusByDegrees(angdeg);
        return getCirclePointF(angdeg, pieRadius);
    }

    @NotNull
    private PointF getCirclePointF(float angdeg, float pieRadius) {
        double radians = Math.toRadians(angdeg);
        double x0 = Math.cos(radians) * pieRadius;
        double y0 = Math.sin(radians) * pieRadius;
        float px = (float) (mCenterPoint.x + x0);
        float py = (float) (mCenterPoint.y + y0);
        PointF pointF = new PointF(px, py);
        return pointF;
    }

    /**
     * 返回角度对应的圆环的最大半径
     *
     * @param degrees
     * @return
     */
    private float pieRadiusByDegrees(float degrees) {
        float v = getStrokeWidth(degrees) / 2F;
        float v1 = mDiameter / 2F;
        return v1 + v;
    }

    /**
     * 返回角度对应的饼图最大宽度.
     *
     * @param degrees 度数
     * @return
     */
    private float getStrokeWidth(float degrees) {
        degrees = formatPositive(degrees);
        if (mSweepAngles != null && mSweepAngles.length > 0) {
            float startDegrees = mStartAngle;
            Float result = null;
            ArrayList<Float> arrayList = new ArrayList<>();
            for (int i = 0; i < mSweepAngles.length; i++) {
                float sweepAngle = mSweepAngles[i];
                float endDegrees = startDegrees + sweepAngle;
                startDegrees = (float) formatPositive(startDegrees);
                endDegrees = (float) formatPositive(endDegrees);
                arrayList.add(startDegrees);
                startDegrees = endDegrees;
            }
            for (int i = 0; i < arrayList.size(); i++) {
                if (Math.abs(degrees - arrayList.get(i)) < 0.01) {
                    float strokeWidth = mStrokeWidths[i];
                    int prev = i - 1;
                    if (prev < 0) {
                        prev = prev + mStrokeWidths.length;
                    }
                    float strokeWidthPrev = mStrokeWidths[prev];
                    float v = strokeWidth > strokeWidthPrev ? strokeWidth : strokeWidthPrev;
                    return v;
                }
            }

            for (int i = 0; i < mSweepAngles.length; i++) {
                float sweepAngle = mSweepAngles[i];
                float endDegrees = startDegrees + sweepAngle;
                startDegrees = (float) formatPositive(startDegrees);
                endDegrees = (float) formatPositive(endDegrees);
                if (startDegrees < endDegrees) {
                    if (degrees > startDegrees && degrees <= endDegrees) {
                        return mStrokeWidths[i];
                    }
                } else {
                    if (degrees > startDegrees && degrees < 360) {
                        return mStrokeWidths[i];
                    }
                    if (degrees >= 0 && degrees <= endDegrees) {
                        return mStrokeWidths[i];
                    }
                }
                startDegrees = endDegrees;
            }
        }
        return 0;
    }

    private double formatPositive(double degrees) {
        degrees = degrees % 360;
        if (degrees < 0) {
            degrees = degrees + 360;
        }
        return degrees;
    }

    private float formatPositive(float degrees) {
        degrees = degrees % 360;
        if (degrees < 0) {
            degrees = degrees + 360;
        }
        return degrees;
    }


    private void drawRegion(Canvas canvas, Region region, Paint paint) {
        //Android还提供了一个RegionIterator来对Region中的所有矩阵进行迭代，
        // 可以使用该类，获得某个Region的所有矩阵
        //通过遍历region中的矩阵，并绘制出来，来绘制region
        RegionIterator iterator = new RegionIterator(region);
        Rect r = new Rect();
        while (iterator.next(r)) {
            canvas.drawRect(r, paint);
        }
    }

    public void setData(ArrayList<PieEntry> entries, ArrayList<Integer> colors) {
        this.mEntries = entries;
        this.mColors = colors;
        requestLayout();
    }
}
