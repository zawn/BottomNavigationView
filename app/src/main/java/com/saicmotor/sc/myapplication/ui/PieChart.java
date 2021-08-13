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

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.github.mikephil.charting.data.PieEntry;
import com.saicmotor.sc.myapplication.R;
import com.saicmotor.sc.myapplication.ScreenUtil;

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
    public static final float TRY_MOVE_DEGREES = 0.2f;


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
    private int mNearDip;
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
        mCenterPoint = new PointF(mCentX, mCentY);

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
        mTryMoveCount = 0;
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
        mTextRectF = new RectF[mCircleRegions.length];
        mTextPaints = new TextPaint[mCircleRegions.length];
        mTextHeight = new float[mCircleRegions.length];
        for (int i = 0; i < mColors.size(); i++) {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(mColors.get(i));
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(ScreenUtil.sp2px(getContext(), 14));
            textPaint.setTypeface(Typeface.MONOSPACE);
            textPaint.setAntiAlias(true);
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
            float x = (float) (mCentX + x1);
            float y = (float) (mCentY + y1);

            PieEntry pieEntry = mEntries.get(i);


            String text = pieEntry.getLabel();
            Rect rect = new Rect();
            mTextPaints[i].getTextBounds(text, 0, text.length(), rect);

            int textWidth = rect.left - rect.right;
            int textHeight = rect.top - rect.bottom;

            mTextHeight[i] = textHeight;

            int p = ScreenUtil.dip2px(getContext(), 8);
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
                canvas.drawText(text, x, y2, textPaint);

                if (DEBUG)
                    canvas.drawCircle(x, y, 4, mPaintStartCircle);

                startDegrees = startDegrees + sweepAngle;
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
                float x = (float) (mCentX + x1);
                float y = (float) (mCentY + y1);

                // 辅助点，标明折线起始位置
                if (DEBUG)
                    canvas.drawCircle(x, y, 4, mPaintStartCircle);

                float height = mTextHeight[i];

                RectF rectF = mTextRectF[i];
                if (DEBUG) {
                    canvas.drawRect(rectF, mPaint);
                }

                String text = mEntries.get(i).getLabel();
                canvas.drawText(text, rectF.centerX(), rectF.centerY() - height / 2, mTextPaints[i]);

                startDegrees = startDegrees + sweepAngle;
            }
        }

        boolean invalidate = isInvalidate(canvas);
        if (invalidate) {
            invalidate();
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
        float px = (float) (mCentX + x0);
        float py = (float) (mCentY + y0);
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

    public void setData(ArrayList<PieEntry> entries, ArrayList<Integer> colors) {
        this.mEntries = entries;
        this.mColors = colors;
        requestLayout();
    }
}
