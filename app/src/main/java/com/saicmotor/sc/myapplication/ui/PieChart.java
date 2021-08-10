package com.saicmotor.sc.myapplication.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

import com.github.mikephil.charting.data.PieEntry;
import com.saicmotor.sc.myapplication.R;
import com.saicmotor.sc.myapplication.ScreenUtil;

import java.util.ArrayList;


public class PieChart extends View {
    private static final String TAG = "PieChart";
    private static final boolean DEBUG = true;

    private static final int MaxWidth = 9999999;

    private static final String Simple_Title = "标题内容";
    private static final float Default_Anim_Time = 1.5F;
    private static final int Default_Size = 90;
    private static final int Default_Bold = 10;
    private static final int Default_Background_Color = 0xFFCCCCCC;
    private static final int Default_Foreground_Color = 0xFF0797FC;
    private static final int Default_Progress_Text_Color = 0xFF4d4d4d;
    private static final int Default_Title_Color = 0xFF808080;
    private static final int Default_Width = 100;


    private TextPaint mTextPaint;
    private Paint mTitlePaint;
    private Paint mBackPaints;
    private Paint mUnitTextPaint;
    private Paint mPaint;
    private Paint mPaintStartCircle;
    private Paint mPaintEndCircle;
    private RectF mOval;

    private int mForegroundColor = Default_Foreground_Color;
    private int mForegroundEndColor;
    private int mBackgroundColor;
    private int mBackgroundRingColor = Default_Background_Color;
    private float mBold = Default_Bold;

    // 环形直径
    private int mDiameter;
    private float mProgressTextSize = Default_Size;
    private String mTitle = null;
    private String mUnitText = null;
    private int mUnitTextSize;
    private float mTitleSize = mProgressTextSize * 7F / 20F;
    private int mTitleColor = Default_Title_Color;
    private float mCurrentAngle;
    private long mStartTime;
    private int mProgress;
    private int mTotle;
    private boolean mIsComplete;

    // 速度计算用变量.
    private float mAnimTime = Default_Anim_Time;
    private double mMaxSpeed;
    private int mMaxAngle;
    private int mProgressTextColor = Default_Progress_Text_Color;
    private float mActualBold;
    private int mStartAngle;
    private Paint mBgPaints;
    private float preUnitX;
    private int[] colors;
    @Nullable
    private float[] positions;
    private ArrayList<PieEntry> mEntries;
    private Paint[] mPaints;
    private float[] mSweepAngles;
    private float[] mPercents;
    private float[] mStrokeWidths;
    private ArrayList<Integer> mColors;
    private int color;
    private int mCentX;
    private int mCentY;
    private Region mPieRegion;
    private Region[] mRegions;
    private Region mAllRegion;
    private int mNearDip;


    public void setForegroundColor(@ColorInt int foregroundColor) {
        this.mForegroundColor = foregroundColor;
        mPaintStartCircle.setColor(mForegroundColor);
    }

    public void setForegroundColor(int colors[], @Nullable float positions[]) {
        this.colors = colors;
        this.positions = positions;

        this.mForegroundColor = colors[0];
        this.mForegroundEndColor = colors[colors.length - 1];
//        mPaintStartCircle.setColor(mForegroundColor);
//        mPaintEndCircle.setColor(mForegroundEndColor);

        setSweepGradient();
    }

    public void setForegroundColor(@ColorInt int foregroundColor, @ColorInt int foregroundEndColor) {
        int[] colors = {foregroundColor, foregroundEndColor};
        setForegroundColor(colors, null);
    }

    public void setForegroundColorStartAngle(int mStartAngle) {
        this.mStartAngle = mStartAngle;
    }


    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RingProgressView);
            mTitle = a.getString(R.styleable.RingProgressView_title);
            mUnitText = a.getString(R.styleable.RingProgressView_unitText);
            mUnitTextSize = a.getDimensionPixelSize(R.styleable.RingProgressView_unitTextSize, Default_Size);
            mTitleColor = a.getColor(R.styleable.RingProgressView_titleColor, Default_Title_Color);
            mTitleSize = a.getDimensionPixelSize(R.styleable.RingProgressView_titleSize,
                    (int) (mProgressTextSize * 7F / 20F));
            mTotle = a.getInt(R.styleable.RingProgressView_total, 100);
            mStartAngle = a.getInt(R.styleable.RingProgressView_foregroundRingColorStart, 0);
            mProgress = a.getInt(R.styleable.RingProgressView_progress, 30);
            mProgressTextColor = a.getColor(R.styleable.RingProgressView_progressColor, Default_Progress_Text_Color);
            mProgressTextSize = a.getDimensionPixelSize(R.styleable.RingProgressView_progressSize, Default_Size);
            mBold = a.getDimensionPixelSize(R.styleable.RingProgressView_bold, Default_Bold);
            mBackgroundRingColor = a.getColor(R.styleable.RingProgressView_backgroundRingColor, Default_Background_Color);
            mForegroundColor = a.getColor(R.styleable.RingProgressView_foregroundRingColor, Default_Foreground_Color);
            mForegroundEndColor = a.getColor(R.styleable.RingProgressView_foregroundRingEndColor, mForegroundColor);
            mAnimTime = a.getFloat(R.styleable.RingProgressView_animTime, Default_Anim_Time);
            mBackgroundColor = a.getColor(R.styleable.RingProgressView_backgroundColor, 0);
            a.recycle();
        }
        initView();
        setProgress(mTotle, mProgress);
    }

    public PieChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChart(Context context) {
        this(context, null, 0);
    }

    private void initView() {
        Paint paints = new Paint();
        paints.setAntiAlias(true);
        paints.setStyle(Paint.Style.FILL);

        mBgPaints = new Paint(paints);
        mBgPaints.setColor(mBackgroundColor);

        mBackPaints = new Paint(paints);
        mBackPaints.setStyle(Paint.Style.STROKE);
        mBackPaints.setStrokeWidth(mBold);
        mBackPaints.setColor(mBackgroundRingColor);


        mPaint = new Paint(paints);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(mForegroundColor);

        mPaintStartCircle = new Paint(paints);
        mPaintStartCircle.setColor(getResources().getColor(R.color.red_900));
//
//        mPaintEndCircle = new Paint(paints);
//        mPaintEndCircle.setColor(mForegroundEndColor);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mProgressTextSize);
        mTextPaint.setTypeface(Typeface.MONOSPACE);
        mTextPaint.setAntiAlias(true);

        mTitlePaint = new Paint();
        mTitlePaint.setColor(mTitleColor);
        mTextPaint.setTypeface(Typeface.MONOSPACE);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setAntiAlias(true);

        mUnitTextPaint = new Paint();
        mUnitTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTypeface(Typeface.MONOSPACE);
        mUnitTextPaint.setTextAlign(Paint.Align.LEFT);
        mUnitTextPaint.setTextSize(mUnitTextSize);
        mUnitTextPaint.setAntiAlias(true);

        mOval = new RectF();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        setForegroundColor(mForegroundColor, mForegroundEndColor);

        mNearDip = 15;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (DEBUG)
            Log.v(TAG, "onMeasure()1 : widthMeasureSpec=" + widthMeasureSpec + ", heightMeasureSpec"
                    + heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (DEBUG)
            Log.v(TAG,
                    "onMeasure()2 : widthMode=" + MeasureSpec.toString(widthMode) + ", heightMode="
                            + MeasureSpec.toString(heightMode));
        if (DEBUG)
            Log.v(TAG, "onMeasure()3 : widthSize=" + widthSize + ", heightSize=" + heightSize);

        final int minWidth = dip2px(Default_Width) + getPaddingLeft() + getPaddingRight();
        final int minHeight = dip2px(Default_Width) + getPaddingLeft() + getPaddingRight();

        if (DEBUG) Log.v(TAG, "onMeasure()4 :minWidth= " + minWidth + " , minHeight=" + minHeight);
        int measuredWidth = ViewCompat.resolveSizeAndState(minWidth, widthMeasureSpec, 0);
        int measuredHeight = ViewCompat.resolveSizeAndState(minHeight, heightMeasureSpec, 0);
        // Bug#Eclipse ADT 中该View包含在ScrollView中后无法预览
        if (measuredHeight > MaxWidth) {
            measuredHeight = 0;
        }

        if (DEBUG)
            Log.v(TAG, "onMeasure()5 :measuredWidth= " + measuredWidth + " , measuredHeight=" + measuredHeight);

        // 计算直径的最大值
        mDiameter = Math.min((measuredWidth - getPaddingLeft() - getPaddingRight()),
                (measuredHeight - getPaddingTop() - getPaddingBottom()));
//        measuredWidth = mDiameter + getPaddingLeft() + getPaddingRight();
//        measuredHeight = mDiameter + getPaddingTop() + getPaddingBottom();

        mCentX = (measuredWidth - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
        mCentY = (measuredHeight - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();

        mDiameter = mDiameter - 40;
        if (DEBUG)
            Log.v(TAG, "onMeasure with padding :measuredWidth= " + measuredWidth + " , measuredHeight="
                    + measuredHeight);
        setMeasuredDimension(measuredWidth, measuredHeight);
        float r = mDiameter / 2;
        mBold = r / 2F;
        r = r - mBold / 2;
        mOval.set(mCentX - r, mCentY - r, mCentX + r, mCentY + r);
        mDiameter = (int) (mOval.right - mOval.left);

        prepareData(mEntries, mColors);
    }

    private void setSweepGradient() {
        if (mForegroundEndColor != 0) {
            SweepGradient sweepGradient = new SweepGradient(mOval.centerX(), mOval.centerY(), colors, positions);
            Matrix gradientMatrix = new Matrix();
            gradientMatrix.preRotate(mStartAngle, mOval.centerX(), mOval.centerY());
            sweepGradient.setLocalMatrix(gradientMatrix);
            mPaint.setShader(sweepGradient);
//            mPaintStartCircle.setShader(sweepGradient);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (DEBUG) Log.v(TAG, "onLayout()");
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 绘制背景环形
        double asin = Math.asin(mBold / (mDiameter - mBold));
        double toDegrees = Math.toDegrees(asin);


        float centerX = mOval.centerX();
        float centerY = mOval.centerY();

        int centerColor = 0xFFededed;
        mBgPaints.setShadowLayer(10, 0, 2, centerColor);

//        canvas.drawCircle(centerX, centerY, mDiameter / 2F, mBgPaints);
//
//        canvas.drawArc(mOval, 0, 360, false, mBackPaints);

        // 绘制圆环
        if (mSweepAngles != null && mSweepAngles.length > 0) {
            float startDegrees = 0;
            for (int i = 0; i < mSweepAngles.length; i++) {
                float sweepAngle = mSweepAngles[i];
                Paint paint = mPaints[i];
                canvas.drawArc(mOval, (float) (mStartAngle + startDegrees), sweepAngle, false, paint);
                startDegrees = startDegrees + sweepAngle;
                Log.d(TAG, "onDraw() called with: startDegrees = [" + startDegrees + "]");
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
                float x = (float) (centerX + x1);
                float y = (float) (centerY + y1);

                TextPaint textPaint = new TextPaint();
                textPaint.setColor(getResources().getColor(R.color.white));
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTextSize(ScreenUtil.sp2px(getContext(), 10));
                textPaint.setTypeface(Typeface.MONOSPACE);
                textPaint.setAntiAlias(true);
                Rect rect = new Rect();
                String text = (int) mPercents[i] + "%";
                textPaint.getTextBounds(text, 0, text.length(), rect);
                Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
                int j = fontMetricsInt.descent + fontMetricsInt.ascent;
                float y2 = y - j / 2;
                if (DEBUG)
//                    canvas.drawLine(0, y, canvas.getWidth(), y, mPaintStartCircle);

                    canvas.drawText(text, x, y2, textPaint);

                if (DEBUG)
                    canvas.drawCircle(x, y, 4, mPaintStartCircle);

                startDegrees = startDegrees + sweepAngle;
                Log.d(TAG, "onDraw() called with: startDegrees = [" + startDegrees + "]");
            }
        }


        drawMy(canvas);

        // 绘制图例折线
        if (mSweepAngles != null && mSweepAngles.length > 0) {
            float startDegrees = 0;
            for (int i = 0; i < mSweepAngles.length; i++) {
                float sweepAngle = mSweepAngles[i];
                float strokeWidth = mStrokeWidths[i];

                float v = mStartAngle + startDegrees + sweepAngle / 2;

                double radians = Math.toRadians(v);
                double x1 = Math.cos(radians) * (mDiameter / 2F + strokeWidth / 2F);
                double y1 = Math.sin(radians) * (mDiameter / 2F + strokeWidth / 2F);
                float x = (float) (centerX + x1);
                float y = (float) (centerY + y1);

                // 辅助点，标明折线起始位置
                if (DEBUG)
                    canvas.drawCircle(x, y, 4, mPaintStartCircle);

                PieEntry pieEntry = mEntries.get(i);

                TextPaint textPaint = new TextPaint();
                textPaint.setColor(mColors.get(i));
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTextSize(ScreenUtil.sp2px(getContext(), 14));
                textPaint.setTypeface(Typeface.MONOSPACE);
                textPaint.setAntiAlias(true);
                Rect rect = new Rect();
                String text = pieEntry.getLabel();
                textPaint.getTextBounds(text, 0, text.length(), rect);

                Paint paint = new Paint();
                paint.setColor(getResources().getColor(R.color.black));
                paint.setStrokeWidth(2);
                paint.setStyle(Paint.Style.FILL);

                int textWidth = rect.left - rect.right;
                int textHeight = rect.top - rect.bottom;

                RectF textRectF = new RectF(x + textWidth / 2, y + textHeight / 2, x - textWidth / 2, y - textHeight / 2);
                Rect textRect = new Rect();
                textRectF.round(textRect);
                Log.d(TAG, "onDraw() called with: i = [" + i + "]");
                textRect = computeTextXY(textRect, mPieRegion, canvas);

                if (DEBUG)
                    canvas.drawRect(textRect, mPaint);

                canvas.drawText(text, textRect.centerX(), textRect.centerY() - textHeight / 2, textPaint);


                startDegrees = startDegrees + sweepAngle;
                Log.d(TAG, "onDraw() called with: startDegrees = [" + startDegrees + "]");

            }
        }
    }

    private Rect computeTextXY(Rect rect, Region region, Canvas canvas) {
        adjustRect(rect);
        rect = tryMove(rect, region, canvas);
        return rect;
    }

    /**
     * 如果rect超过有效边界则回调.
     *
     * @param rect
     */
    private void adjustRect(Rect rect) {
        int width = getWidth();
        int height = getHeight();
        int paddingRight = getPaddingRight();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int cx = rect.centerX() < mCentX ? 1 : -1;
        int cy = rect.centerY() < mCentY ? 1 : -1;

        boolean b1 = rect.right - width + paddingRight > -5;
        if (b1) {
            int i = rect.right - width + paddingRight;
            rect.offset(-i, -cy);
        }
        boolean b2 = rect.left - paddingLeft < 5;
        if (b2) {
            int i = rect.left - paddingLeft;
            rect.offset(-i, -cy);
        }
        boolean b3 = rect.top - paddingTop < 5;
        if (b3) {
            int i = rect.top - paddingLeft;
            rect.offset(-cx, -i);
        }

        boolean b4 = rect.bottom - height + paddingBottom > -5;
        if (b4) {
            int i = (height - paddingBottom) - rect.bottom;
            rect.offset(-cx, i);
        }
        if ((b1 && b3) || (b1 && b4)) {
            mDiameter--;
            rect.right = 0;
            invalidate();
        }

        if ((b2 && b3) || (b2 && b4)) {
            mDiameter--;
            rect.right = 0;
            invalidate();
        }
    }

    /**
     * 寻找合适的图例位置
     *
     * @param rect
     * @param region
     * @param canvas
     * @return
     */
    private Rect tryMove(Rect rect, Region region, Canvas canvas) {
        if (rect.right == 0) {
            return rect;
        }
        Log.d(TAG, "tryMove() called with: rect = [" + rect + "], region = [" + region + "], canvas = [" + canvas + "]");
        adjustRect(rect);
        Region region1 = new Region();
        region1.set(rect);
        boolean op = region1.op(region, Region.Op.INTERSECT);
        if (op) {
            //  仍然与饼图有交集
            Rect bounds = region1.getBounds();
            int dx = (bounds.centerX() - mCentX) < 0 ? -1 : 1;
            int dy = (bounds.centerY() - mCentY) < 0 ? -1 : 1;
            rect.offset(dx, dy);
            return tryMove(rect, region, canvas);
        } else {
            // 已经与饼图没有交集了
            // 仍然需要保证距离Pie的最近距离不得小于特定值，
            Point nearPoint = getNearPoint(rect);
            Pair<Float, Float> nearPiePoint = getNearPiePoint(nearPoint);

            double sqrt = Math.sqrt(Math.pow(nearPoint.x - nearPiePoint.first, 2) + Math.pow(nearPoint.y - nearPiePoint.second, 2));
            int dip2px = ScreenUtil.dip2px(getContext(), mNearDip);
            if (sqrt < dip2px) {
                double v = sqrt - dip2px;
                if (v < 2) {
                    v = 2;
                }
                double radians = Math.atan2(nearPoint.y - mCentY, nearPoint.x - mCentX);
                double x1 = Math.cos(radians) * v;
                double y1 = Math.sin(radians) * v;
                int x = (int) (x1 + mCentX);
                int y = (int) (y1 + mCentY);
                Point point = new Point(x, y);
                int dx = (int) (point.x - nearPoint.x);
                int dy = (int) (point.y - nearPoint.y);
                rect.offset(dx, dy);
                adjustRect(rect);
                Log.d(TAG, "rect() called with: rect = [" + rect + "]");
                return tryMove(rect, region, canvas);
            }
            if (DEBUG && true) {
                Path path = new Path();
                path.moveTo(nearPoint.x, nearPoint.y);
                path.lineTo(nearPiePoint.first, nearPiePoint.second);
                path.close();
                canvas.drawPath(path, mPaint);
                canvas.drawCircle(nearPoint.x, nearPoint.y, 4, mPaintStartCircle);
                canvas.drawCircle(nearPiePoint.first, nearPiePoint.second, 4, mPaint);
            }
            return rect;
        }
    }

    private Pair<Float, Float> getTargetPoint(Point nearPoint) {
        int x = nearPoint.x - mCentX;
        int y = nearPoint.y - mCentY;
        double radians = Math.atan2(y, x);
        double degrees = Math.toDegrees(radians);


        float strokeWidth = getStrokeWidth(degrees);
        float v = mDiameter / 2F + strokeWidth / 2F;
        v = v + ScreenUtil.dip2px(getContext(), mNearDip);
        double x1 = Math.cos(radians) * v;
        double y1 = Math.sin(radians) * v;
        float x2 = (float) (mCentX + x1);
        float y2 = (float) (mCentY + y1);
        return Pair.create(x2, y2);
    }

    private Pair<Float, Float> getNearPiePoint(Point nearPoint) {
        int x = nearPoint.x - mCentX;
        int y = nearPoint.y - mCentY;
        double radians = Math.atan2(y, x);
        double degrees = Math.toDegrees(radians);


        float strokeWidth = getStrokeWidth(degrees);
        float v = mDiameter / 2F + strokeWidth / 2F;
//        v = v + ScreenUtil.dip2px(getContext(), mNearDip);
        double x1 = Math.cos(radians) * v;
        double y1 = Math.sin(radians) * v;
        float x2 = (float) (mCentX + x1);
        float y2 = (float) (mCentY + y1);
        return Pair.create(x2, y2);
    }

    private float getStrokeWidth(double degrees) {
        degrees = (degrees + 360) % 360;
        if (mSweepAngles != null && mSweepAngles.length > 0) {
            float startDegrees = mStartAngle;
            for (int i = 0; i < mSweepAngles.length; i++) {
                float sweepAngle = mSweepAngles[i];
                float endDegrees = startDegrees + sweepAngle;
                startDegrees = (startDegrees + 360) % 360;
                endDegrees = (endDegrees + 360) % 360;
                if (startDegrees > endDegrees) {
                    endDegrees += 360;
                }
                if (degrees > startDegrees && degrees <= endDegrees) {
                    return mStrokeWidths[i];
                }
                startDegrees = endDegrees;
                Log.d(TAG, "onDraw() called with: startDegrees = [" + startDegrees + "]");
            }
        }
        return 0;
    }

    private Point getNearPoint(Rect rect) {
        boolean dx = (rect.centerX() - mCentX) < 0;
        boolean dy = (rect.centerY() - mCentY) < 0;
        int x, x1;
        int y, y1;
        if (dx) {
            x = rect.right;
            x1 = rect.left;
        } else {
            x = rect.left;
            x1 = rect.right;
        }
        if (dy) {
            y = rect.bottom;
            y1 = rect.bottom;
        } else {
            y = rect.top;
            y1 = rect.top;
        }
        // 只考虑X轴与圆环相交的情况
        double degree = getDegree(new Point(x, y), new Point(x1, y1), new Point(mCentX, mCentY));
        if (degree < 90) {
            Point foot = getFoot(new Point(mCentX, mCentY), new Point(x, y), new Point(x1, y1));
            return foot;
        }
        return new Point(x, y);
    }


    /**
     * p3到p1，p2所在线段的垂足.
     *
     * @param p  线段外的点
     * @param p1 线段上的点1
     * @param p2 线段上的点1
     * @return
     */
    private Point getFoot(Point p, Point p1, Point p2) {
        Point foot = new Point();

        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;

        float u = (p.x - p1.x) * dx + (p.y - p1.y) * dy;
        u /= dx * dx + dy * dy;

        foot.x = (int) (p1.x + u * dx);
        foot.y = (int) (p1.y + u * dy);

        return foot;
    }

    private Point getFoot2(Point p1, Point p2, Point p3) {
        Point foot = new Point();

        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;

        float u = (p3.x - p1.x) * dx + (p3.y - p1.y) * dy;
        u /= dx * dx + dy * dy;

        foot.x = (int) (p1.x + u * dx);
        foot.y = (int) (p1.y + u * dy);

        float d = Math.abs((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
        float d1 = Math.abs((p1.x - foot.x) * (p1.x - foot.x) + (p1.y - foot.y) * (p1.y - foot.y));
        float d2 = Math.abs((p2.x - foot.x) * (p2.x - foot.x) + (p2.y - foot.y) * (p2.y - foot.y));

        if (d1 > d || d2 > d) {
            if (d1 > d2) return p2;
            else return p1;
        }

        return foot;
    }


    /**
     * 通过三点坐标计算对应顶点的角度.
     *
     * @param p  顶点
     * @param p1
     * @param p2
     * @return
     */
    private double getDegree(Point p, Point p1, Point p2) {
        //向量的点乘
        long vector = (p1.x - p.x) * (p2.x - p.x) * 1l + (p1.y - p.y) * (p2.y - p.y);
        //向量的模乘
        long i = Math.abs((p1.x - p.x) * (p1.x - p.x)) + Math.abs((p1.y - p.y) * (p1.y - p.y));
        long i1 = Math.abs((p2.x - p.x) * (p2.x - p.x)) + Math.abs((p2.y - p.y) * (p2.y - p.y));
        double sqrt = Math.sqrt(i * i1);
        //反余弦计算弧度
        double radian = Math.acos(vector / sqrt);
        //弧度转角度制
        return Math.toDegrees(radian);
    }

    private void drawMy(Canvas canvas) {
        mAllRegion = new Region();
        mAllRegion.set(0, 0, getWidth(), getBottom());

        color = getResources().getColor(R.color.cyan_500);
        //设置paint
        float startDegrees = mStartAngle;

        mRegions = new Region[mSweepAngles.length];
        for (int j = 0; j < mSweepAngles.length; j++) {
            float sweepAngle = mSweepAngles[j];
            float strokeWidth = mStrokeWidths[j];

            int d1 = (int) (mDiameter - strokeWidth);
            d1 = d1 / 2;
            RectF baseRectF = new RectF(mCentX - d1, mCentY - d1, mCentX + d1, mCentY + d1);

            Path path = new Path();
            //画一个弧。注意，角度的设置。这样设置，正方型的边长就是直径。方便计算。
            path.addArc(baseRectF, startDegrees, sweepAngle);

            //向外扩张这个正方形。此时正方形的边长是外弧的直径。
            baseRectF.inset(strokeWidth * -1, strokeWidth * -1);
            path.arcTo(baseRectF, startDegrees + sweepAngle, sweepAngle * -1); //注意角度的设置要反转一下。
            path.close();

            //取path和region的交集
            Region rgn = new Region();
            rgn.setPath(path, mAllRegion);
            Paint paint = new Paint();
            paint.setColor(mColors.get(j));
            paint.setStyle(Paint.Style.FILL);

            mRegions[j] = rgn;

            startDegrees = startDegrees + sweepAngle;
        }
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        mPieRegion = new Region();
        for (int i = 0; i < mRegions.length; i++) {
            Region region1 = mRegions[i];
            mPieRegion.op(region1, Region.Op.UNION);
        }

        Path path = new Path();
        path.addCircle(mCentX, mCentY, mDiameter / 2, Path.Direction.CCW);

        Region rgn = new Region();
        rgn.setPath(path, mAllRegion);

        mPieRegion.op(rgn, Region.Op.UNION);


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

    private boolean isComplete() {
        return mIsComplete;
    }

    public void setProgress(int totle, int progress) {
        mIsComplete = false;
        mCurrentAngle = -1;
        mTotle = totle;
        if (mTotle < 1) {
            mTotle = 1;
        }
        mProgress = progress;
        if (mAnimTime < 1) {
            mAnimTime = 1;
        }
        mMaxAngle = mProgress * 360 / mTotle;
        mMaxSpeed = mMaxAngle * 2 / mAnimTime;
        invalidate();
    }

    /**
     * 获取当前需要绘制的角度.
     *
     * @return
     */
    private float getCurrentAngle() {
        if (mCurrentAngle == -1F) {
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mCurrentAngle = 0;
        }
        double t = (AnimationUtils.currentAnimationTimeMillis() - mStartTime) / 1000D;
        double s = 0;
        if (t < (mAnimTime / 2)) {
            s = mMaxSpeed * Math.pow(t, 2) / mAnimTime;
        } else if (t < mAnimTime) {
            s = mMaxAngle - (mMaxSpeed * Math.pow((mAnimTime - t), 2) / mAnimTime);
        } else {
            s = mCurrentAngle;
            mIsComplete = true;
        }
        mCurrentAngle = (float) s;
        return mCurrentAngle;
    }

    public void setAnimTime(float mMaxAnimTime) {
        this.mAnimTime = mMaxAnimTime;
    }

    public void setData(ArrayList<PieEntry> entries, ArrayList<Integer> colors) {
        this.mEntries = entries;
        this.mColors = colors;
        requestLayout();
    }
}
