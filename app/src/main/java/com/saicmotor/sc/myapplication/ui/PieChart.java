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
    private Region mAllRegion;
    private Region mPieRegion;
    private Region[] mCircleRegions;
    private Region[] mTextRegions;
    private Region[] mTextBkRegions;
    private int mNearDip;
    private Point mCentPoint;
    private TextPaint[] mTextPaints;
    private Region mAvailableRegion;
    private int[] mTextHeight;


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
        mPaint.setColor(getResources().getColor(R.color.black));

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
        mCentPoint = new Point(mCentX, mCentY);

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
        prepareData(mEntries, mColors);
        prepareText();
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
            RectF baseRectF = new RectF(mCentX - d1, mCentY - d1, mCentX + d1, mCentY + d1);

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
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        mPieRegion = new Region();
        for (int i = 0; i < mCircleRegions.length; i++) {
            mPieRegion.op(mCircleRegions[i], Region.Op.UNION);
        }

        Path path = new Path();
        path.addCircle(mCentX, mCentY, mDiameter / 2, Path.Direction.CCW);

        Region circleRegion = new Region();
        circleRegion.setPath(path, mAllRegion);

        mPieRegion.op(circleRegion, Region.Op.UNION);
    }

    private void prepareText() {
        float startDegrees;
        mTextRegions = new Region[mCircleRegions.length];
        mTextBkRegions = new Region[mCircleRegions.length];
        mTextPaints = new TextPaint[mCircleRegions.length];
        mTextHeight = new int[mCircleRegions.length];
        for (int i = 0; i < mColors.size(); i++) {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(mColors.get(i));
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(ScreenUtil.sp2px(getContext(), 14));
            textPaint.setTypeface(Typeface.MONOSPACE);
            textPaint.setAntiAlias(true);
            mTextPaints[i] = textPaint;
        }

        startDegrees = 0;
        for (int i = 0; i < mSweepAngles.length; i++) {
            float sweepAngle = mSweepAngles[i];
            float strokeWidth = mStrokeWidths[i];

            float v = mStartAngle + startDegrees + sweepAngle / 2;

            double radians = Math.toRadians(v);
            double x1 = Math.cos(radians) * (mDiameter / 2F + strokeWidth / 2F);
            double y1 = Math.sin(radians) * (mDiameter / 2F + strokeWidth / 2F);
            float x = (float) (mCentX + x1);
            float y = (float) (mCentY + y1);

            PieEntry pieEntry = mEntries.get(i);


            String text = pieEntry.getLabel();
            Rect rect = new Rect();
            mTextPaints[i].getTextBounds(text, 0, text.length(), rect);

            int textWidth = rect.left - rect.right;
            int textHeight = rect.top - rect.bottom;

            RectF textRectF = new RectF(x + textWidth / 2, y + textHeight / 2, x - textWidth / 2, y - textHeight / 2);
            Rect textRect = new Rect();
            textRectF.round(textRect);

            mTextHeight[i] = textRect.height();

            int p = ScreenUtil.dip2px(getContext(), 8);

            textRect = new Rect(textRect.left - p, textRect.top - p, textRect.right + p, textRect.bottom + p);

            Region region = new Region();
            region.set(textRect);


            mTextRegions[i] = region;
            startDegrees = startDegrees + sweepAngle;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw() called with: canvas = [" + "]");
        // 绘制Padding参考线
        if (DEBUG) {
//            canvas.drawLine(getPaddingLeft(), 0, getPaddingLeft(), getHeight(), mPaint);
//            canvas.drawLine(getWidth() - getPaddingRight(), 0, getWidth() - getPaddingRight(), getHeight(), mPaint);
//            canvas.drawLine(0, getPaddingTop(), getWidth(), getPaddingTop(), mPaint);
//            canvas.drawLine(0, getHeight() - getPaddingBottom(), getWidth(), getHeight() - getPaddingBottom(), mPaint);
            drawRegion(canvas, mAvailableRegion, mPaint);
        }

        // 绘制背景环形
        double asin = Math.asin(mBold / (mDiameter - mBold));
        double toDegrees = Math.toDegrees(asin);

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
                float x = (float) (mCentX + x1);
                float y = (float) (mCentY + y1);

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
                if (DEBUG) {
//                    canvas.drawLine(0, y, canvas.getWidth(), y, mPaintStartCircle);
                }
                canvas.drawText(text, x, y2, textPaint);

                if (DEBUG)
                    canvas.drawCircle(x, y, 4, mPaintStartCircle);

                startDegrees = startDegrees + sweepAngle;
            }
        }

        boolean invalidate = isInvalidate(canvas);

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
                float x = (float) (mCentX + x1);
                float y = (float) (mCentY + y1);

                // 辅助点，标明折线起始位置
                if (DEBUG)
                    canvas.drawCircle(x, y, 4, mPaintStartCircle);

                Region textRegion = mTextRegions[i];
                if (DEBUG) {
//                    drawRegion(canvas, textRegion, mPaint);
                }
                int height = mTextHeight[i];
                Rect textRect = textRegion.getBounds();

                if (DEBUG) {
                    canvas.drawRect(textRect, mPaint);
                }

                String text = mEntries.get(i).getLabel();
                canvas.drawText(text, textRect.centerX(), textRect.centerY() + height / 2, mTextPaints[i]);

                startDegrees = startDegrees + sweepAngle;
            }
        }
        if (invalidate) {
            invalidate();
        }

    }

    private boolean isInvalidate(Canvas canvas) {
        boolean invalidate = false;
        for (int i = 0; i < mTextRegions.length; i++) {
            computeTextXY(mTextRegions[i], mPieRegion);
        }
        // 检查图例是否有交叉,否超过边界
        for (int i = 0; i < mTextRegions.length; i++) {
            Region textRegion1 = mTextRegions[i];
            Rect textRegion1Bounds = textRegion1.getBounds();
            for (int j = 0; j < mTextRegions.length; j++) {
                if (i == j) {
                    continue;
                } else {
                    Region textRegion2 = mTextRegions[j];
                    Region region = new Region(textRegion2);
                    boolean op = region.op(textRegion1, Region.Op.INTERSECT);
                    Rect bounds = region.getBounds();
                    if (op) {
                        boolean left = bounds.centerX() < textRegion1Bounds.centerX();
                        boolean top = bounds.centerY() < textRegion1Bounds.centerY();
                        invalidate = translateRegion(textRegion1, region);
                        boolean b = translateRegion(textRegion2, region);
                        invalidate = invalidate || b;
                    }
                }
            }
        }


        // 检查图例是否超过边界
        for (int i = 0; i < mTextRegions.length; i++) {
            Region textRegion1 = mTextRegions[i];
            Region region = new Region(textRegion1);
            boolean op = region.op(mAvailableRegion, Region.Op.DIFFERENCE);
            if (op) {
                if (DEBUG)
                    drawRegion(canvas, region, mPaintStartCircle);
                Rect bounds = region.getBounds();
                if (bounds.centerY() < getPaddingTop()) {
                    textRegion1.translate(0, 1);
                    invalidate = true;
                }
                if (bounds.centerY() > getHeight() - getPaddingBottom()) {
                    textRegion1.translate(0, -1);
                    invalidate = true;
                }
                if (bounds.centerX() < getPaddingLeft()) {
                    textRegion1.translate(1, 0);
                    invalidate = true;
                }
                if (bounds.centerX() > getWidth() - getPaddingRight()) {
                    textRegion1.translate(-1, 0);
                    invalidate = true;
                }
            }
        }
        return invalidate;
    }

    private boolean translateRegion(Region region, Region intersectRegion) {
//        Rect topLeftRect = new Rect(0, 0, getPaddingLeft(), getPaddingTop());
//        Region topRegion = new Region(topLeftRect);
//        Rect topRightRect = new Rect(getWidth() - getPaddingRight(), 0, getWidth(), getPaddingTop());
//        Region rightRegion = new Region(topRightRect);
//        Rect bottomRightRect = new Rect(getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(), getWidth(), getHeight());
//        Region leftRegion = new Region(bottomRightRect);
//        Rect bottomLeftRect = new Rect(0, getHeight() - getPaddingBottom(), getPaddingLeft(), getHeight());
//        Region bottomRegion = new Region(bottomLeftRect);

        boolean invalidate = false;
        Rect bounds = region.getBounds();

        boolean topLeft = bounds.contains(getPaddingLeft(), getPaddingTop());
        boolean topRight = bounds.contains(getWidth() - getPaddingRight(), getPaddingTop());
        boolean bottomRight = bounds.contains(getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        boolean bottomLeft = bounds.contains(getPaddingLeft(), getHeight() - getPaddingBottom());

        if (topLeft || topRight || bottomRight || bottomLeft) {
            Log.d(TAG, "translateRegion() called with: region = [" + region + "], intersectRegion = [" + intersectRegion + "]");
        }

        if (topLeft) {
            region.translate(0, bounds.height() + 1);
        } else if (bottomLeft) {
            region.translate(0, bounds.height() * -1 - 1);
        } else if (topRight) {
            region.translate(0, bounds.height() + 1);
        } else if (bottomRight) {
            region.translate(0, bounds.height() * -1 - 1);
        }
        Rect bounds1 = intersectRegion.getBounds();
        boolean b1 = bounds1.centerX() < bounds.centerX();
        boolean b2 = bounds1.centerY() < bounds.centerY();
        int dx = b1 ? 1 : -1;
        int dy = b2 ? 1 : -1;
        region.translate(dx, dy);
        invalidate = true;

        return invalidate;
    }


    /**
     * 寻找合适的图例位置，
     *
     * @param rawRegion
     * @param region
     * @return
     */
    private void computeTextXY(Region rawRegion, Region region) {
        Region region1 = new Region(rawRegion);
        boolean op = region1.op(region, Region.Op.INTERSECT);
        Rect bounds1 = region1.getBounds();
        Point nearPoint = nearPoint(mCentPoint, bounds1);
        Rect bounds = rawRegion.getBounds();
        if (op) {
            //  仍然与饼图有交集
            int x = nearPoint.x - mCentX;
            int y = nearPoint.y - mCentY;
            double radians = Math.atan2(y, x);
            double degrees = Math.toDegrees(radians);
            float strokeWidth = getStrokeWidth(degrees);
            float radius = mDiameter / 2F + strokeWidth / 2F;
            double distance = distance(nearPoint, mCentPoint);
            double offsetX = Math.cos(radians) * (radius - distance);
            double offsetY = Math.sin(radians) * (radius - distance);

            int oX = (int) (Math.ceil(Math.abs(offsetX)) * (offsetX >= 0 ? 1 : -1));
            int oY = (int) (Math.ceil(Math.abs(offsetY)) * (offsetY >= 0 ? 1 : -1));
            rawRegion.translate(oX, oY);
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


    /**
     * p到p1，p2所在线段的垂足.
     *
     * @param p  线段外的点
     * @param p1 线段上的点1
     * @param p2 线段上的点1
     * @return
     */
    private Point footOfPerpendicular(Point p, Point p1, Point p2) {
        Point foot = new Point();

        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;

        float u = (p.x - p1.x) * dx + (p.y - p1.y) * dy;
        u /= dx * dx + dy * dy;

        foot.x = (int) (p1.x + u * dx);
        foot.y = (int) (p1.y + u * dy);

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
    private Point nearPoint(Point p, Point p1, Point p2) {
        Point foot = footOfPerpendicular(p, p1, p2);

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
     * 寻找p到线段p1，p2的最近的点.0
     *
     * @param p
     * @param rect
     * @return
     */
    private Point nearPoint(Point p, Rect rect) {
        Point p1 = new Point(rect.left, rect.top);
        Point p2 = new Point(rect.right, rect.top);
        Point p3 = new Point(rect.right, rect.bottom);
        Point p4 = new Point(rect.left, rect.bottom);

        Point[] points = new Point[4];
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
     * 计算两个点之间的距离.
     *
     * @param p1
     * @param p2
     * @return
     */
    private double distance(Point p1, Point p2) {
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
