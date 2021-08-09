package com.saicmotor.sc.myapplication.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
        mPaint.setStrokeWidth(mBold);
//        mPaint.setColor(mForegroundColor);

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

        int centX = (measuredWidth - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
        int centY = (measuredHeight - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();

        mDiameter = mDiameter - 40;
        if (DEBUG)
            Log.v(TAG, "onMeasure with padding :measuredWidth= " + measuredWidth + " , measuredHeight="
                    + measuredHeight);
        setMeasuredDimension(measuredWidth, measuredHeight);
        float r = mDiameter / 2;
        mBold = r / 2F;
        r = r - mBold / 2;
        mOval.set(centX - r, centY - r, centX + r, centY + r);
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
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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

        canvas.drawCircle(centerX, centerY, mDiameter / 2F, mBgPaints);

        canvas.drawArc(mOval, 0, 360, false, mBackPaints);
/*

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
*/

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
                    canvas.drawLine(0, y, canvas.getWidth(), y, mPaintStartCircle);

                canvas.drawText(text, x, y2, textPaint);

                if (DEBUG)
                    canvas.drawCircle(x, y, 4, mPaintStartCircle);

                startDegrees = startDegrees + sweepAngle;
                Log.d(TAG, "onDraw() called with: startDegrees = [" + startDegrees + "]");
            }
        }

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
                String label = pieEntry.getLabel();

                startDegrees = startDegrees + sweepAngle;
                Log.d(TAG, "onDraw() called with: startDegrees = [" + startDegrees + "]");

                //设置一个目标正方形框。这个边长是内弧的直径。中心点即为圆心
                RectF baseRectf = new RectF(600, 600, 800, 800);

                Path path = new Path();

                //画一个弧。注意，角度的设置。这样设置，正方型的边长就是直径。方便计算。
                path.addArc(baseRectf, -30, 60);

                //向外扩张这个正方形。此时正方形的边长是外弧的直径。
                baseRectf.inset(-100, -100);
                path.arcTo(baseRectf, 30, -60); //注意角度的设置要反转一下。
                path.close();
            }
        }

        //设置画笔
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.blue_200));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5f);//无描边，设置setStrokeWidth无效


        //构造椭圆路径
        Path path = new Path();
        //构建椭圆path
        RectF rectF = new RectF(0, 0, 200, 500);
        path.addOval(rectF, Path.Direction.CCW);//Path.Direction.CCW:逆时针;Path.Direction.CW:顺时针
        //构建Region
        Region region = new Region();
        region.set(540, 300, 980, 500);
        //取path和region的交集
        Region rgn = new Region();
        rgn.setPath(path, region);
        //绘制区域
        drawRegion(canvas, rgn, paint);
//        float sweepAngle = getCurrentAngle();


/*



        if (sweepAngle >= toDegrees) {
            sweepAngle = (float) (sweepAngle - 1 * toDegrees); // 包含起始迁移一个toDegrees,缩短一个toDegrees
            sweepAngle = sweepAngle < 0 ? 0 : sweepAngle;
            canvas.drawArc(mOval, (float) (mStartAngle + toDegrees), sweepAngle, false, mPaint);
            double angdeg = sweepAngle + toDegrees + mStartAngle;
            double totalDeg = angdeg - mStartAngle;
            double radiansEnd = Math.toRadians(angdeg);
            if (radiansEnd >= radians) {
                double x1End = Math.cos(radiansEnd) * (mDiameter / 2F - mActualBold);
                double y1End = Math.sin(radiansEnd) * (mDiameter / 2F - mActualBold);
                if (totalDeg + toDegrees > 360) {
//                    canvas.drawCircle((float) (centerX + x1End), (float) (centerY + y1End), mBold / 2, mPaintEndCircle);
                } else {
//                    canvas.drawCircle((float) (centerX + x1End), (float) (centerY + y1End), mBold / 2, mPaintStartCircle);
                }
            }
        }


        boolean isAnimIn = (sweepAngle < 360 && !isComplete());

//        float progress = sweepAngle * mTotle / 360;
//        BigDecimal bg = new BigDecimal(progress);
//        int intValue = bg.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        if ((intValue / 10000) > 0) {
//            mTextPaint.setTextSize(mProgressTextSize * 3 / 4);
//        }
        if (!isAnimIn) {
//            intValue = mProgress;
        }

//        Rect rect = new Rect();
//        String text = Integer.toString(intValue);
//        mTextPaint.getTextBounds(text, 0, text.length(), rect);
//        float measureText = mTextPaint.measureText(text);
//        float x2 = centerX;
//        Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
//        int i = fontMetricsInt.descent + fontMetricsInt.ascent;
//        float y2 = centerY - i / 2;
//        canvas.drawText(text, x2, y2, mTextPaint);
//        if (mUnitText != null) {
//            float x = x2 + measureText / 2 + 2;
//            if (x < preUnitX) {
//                x = preUnitX;
//            } else {
//                preUnitX = x;
//            }
//            if (!isAnimIn) {
//                preUnitX = 0;
//            }
//            canvas.drawText(mUnitText, x, y2, mUnitTextPaint);
//        }
//        if (mTitle != null && !mTitle.isEmpty()) {
//            canvas.drawText(mTitle, x2, y2 - mProgressTextSize, mTitlePaint);
//        }
        if (isAnimIn) {
            invalidate();
        }

 */
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
        invalidate();
        Log.d(TAG, "setData() called with: entries = [" + entries + "], colors = [" + colors + "]");

    }
}
