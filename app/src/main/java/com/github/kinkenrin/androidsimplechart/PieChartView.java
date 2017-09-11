package com.github.kinkenrin.androidsimplechart;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计用到的饼图
 * Created by jinxl on 2017/8/31.
 */
public class PieChartView extends View {
    // 数据
    private List<PieEntry> mDataMap = null;
    private List<SectorsData> mSectorsDataList = null;
    private List<LinesData> mLinesDataList = null;
    private Paint mPaint;

    private Path mPath;
    private TextPaint mCurrentPaint;
    private int mRadius;//圆的半径
    private int mRingWidth = 100;//圆环宽度
    private int mWidth;//宽
    private int mHeight;//高
    private int mSectorsX;
    private int mSectorsY;
    private int mCount;
    private float mLineWidth;
    private float mLineLenght1;//斜线的长度
    private float mLineLenght2;//横线的长度
    private float oneDp = 1;

    private int mEmptyColor;

    private String mTitleText;
    private Rect mBound;

    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /*TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTitleView, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.CustomTitleView_titleText:
                    mTitleText = a.getString(attr);
                    break;

            }

        }
        a.recycle();*/
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
        mDataMap = new ArrayList<>();
        oneDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        mEmptyColor = Color.rgb(238, 238, 238);

        mCurrentPaint = new TextPaint();

        mTitleText = "这是标题";
        mBound = new Rect();
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
        mLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        mLineLenght1 = Math.max(mBound.height(), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()));
        mLineLenght2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics());
        mRingWidth = (int) (30 * oneDp);
    }

    private int calculateCount() {
        int count = 0;
        if (mDataMap != null) {
            for (int i = 0; i < mDataMap.size(); i++) {
                count += mDataMap.get(i).count;
            }
        }
        return count;
    }

    public void setPieData(List<PieEntry> datas) {
        mDataMap.clear();
        mDataMap.addAll(datas);
        invalidate();
        requestLayout();
    }

    public void setTitle(String titleText) {
        mTitleText = titleText;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (mRadius == 0) {
            mRadius = (int) (mWidth / 4 * 0.9);
        }

        // 扇形中心点
        mSectorsX = mWidth / 2;
        mSectorsY = mHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCount = calculateCount();//计算总数
        if (mCount == 0) {
            //画空态页面
            drawEmptyView(canvas);
            drawTitleText(canvas, true);
            return;
        }

        //画扇形
        drawSectorsView(canvas);
        drawLines(canvas);
        drawTitleText(canvas, false);

    }

    private void drawTitleText(Canvas canvas, boolean isEmpty) {
        mCurrentPaint.setTextAlign(Paint.Align.CENTER);
        mCurrentPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mCurrentPaint.setColor(Color.rgb(139, 152, 173));
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        int titleStartY = mSectorsY - mBound.height() / 2;
        StaticLayout currentLayout = new StaticLayout(mTitleText, mCurrentPaint, mRadius * 2 - mRingWidth * 2 - 2 * padding,
                Layout.Alignment.ALIGN_NORMAL, 1.5f, 0f, false);
        canvas.save();
        canvas.translate(mSectorsX, titleStartY);
        currentLayout.draw(canvas);

        if (isEmpty) {
            mCurrentPaint.setColor(Color.BLACK);
            StaticLayout currentLayout2 = new StaticLayout("无数据", mCurrentPaint, mRadius * 2 - mRingWidth * 2 - 2 * padding,
                    Layout.Alignment.ALIGN_NORMAL, 1.5f, 0f, false);

            canvas.translate(0, currentLayout.getHeight() + padding * 5);
            currentLayout2.draw(canvas);
        }
        canvas.restore();
    }

    private void drawSectorsView(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL); // 填充模式

        mSectorsDataList = calculateSectorsDatas();
        SectorsData sectorsData;
        for (int i = 0; i < mSectorsDataList.size(); i++) {
            sectorsData = mSectorsDataList.get(i);
            mPaint.setColor(sectorsData.color);
            canvas.drawArc(new RectF(sectorsData.left, sectorsData.top, sectorsData.right, sectorsData.bottom), sectorsData.startAngle, sectorsData.sweepAngle, true, mPaint);
        }
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mSectorsX, mSectorsY, mRadius - mRingWidth, mPaint);

    }

    private List<SectorsData> calculateSectorsDatas() {
        List<SectorsData> sectorsDataList = new ArrayList<>();
        float startAngle = 270; // 开始角度
        float sweepAngle; // 扇形角度
        for (PieEntry data : mDataMap) {
            sweepAngle = data.count * 360 / mCount;
            SectorsData sectorsData = calculateDirectionCoord(startAngle, sweepAngle);
            sectorsData.startAngle = startAngle;
            sectorsData.sweepAngle = sweepAngle;
            sectorsData.color = data.color;
            sectorsDataList.add(sectorsData);
            startAngle += sectorsData.sweepAngle;
        }
        return sectorsDataList;
    }

    /**
     * 根据扇形角度计算扇形偏移方向及最终坐标
     */
    private SectorsData calculateDirectionCoord(float startAngle, float sweepAngle) {

        SectorsData sectorsData = new SectorsData();
        sectorsData.middleAngle = (startAngle + sweepAngle / 2) % 360; // 中间角度，用于计算偏移方向角度

        sectorsData.left = mSectorsX - mRadius;
        sectorsData.top = mSectorsY - mRadius;
        sectorsData.right = mSectorsX + mRadius;
        sectorsData.bottom = mSectorsY + mRadius;

        sectorsData.sectorsX = mSectorsX;
        sectorsData.sectorsY = mSectorsY;

        return sectorsData;
    }

    private void drawEmptyView(Canvas canvas) {
        mPaint.setColor(mEmptyColor);
        canvas.drawCircle(mSectorsX, mSectorsY, mRadius, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mSectorsX, mSectorsY, mRadius - mRingWidth, mPaint);
    }

    /**
     * 画线
     **/
    private void drawLines(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setColor(Color.rgb(221, 224, 232));
        mCurrentPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, getResources().getDisplayMetrics()));
        mCurrentPaint.setColor(Color.rgb(139, 152, 173));
        mLinesDataList = calculateLinesDatas();
        LinesData linesData;
        for (int i = 0; i < mLinesDataList.size(); i++) {
            linesData = mLinesDataList.get(i);
            mPath.moveTo(linesData.startX, linesData.startY);
            mPath.lineTo(linesData.turnX, linesData.turnY);
            mPath.lineTo(linesData.endX, linesData.endY);
            canvas.drawPath(mPath, mPaint);
            canvas.save();
            mCurrentPaint.setTextAlign(Paint.Align.LEFT);
            mCurrentPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
            mPaint.getTextBounds(linesData.lable, 0, linesData.lable.length(), mBound);
            float textpadding = mBound.height() + 5 * oneDp;
            StaticLayout currentLayout = new StaticLayout(linesData.lable, mCurrentPaint, (int) (mBound.width() + 3 * oneDp),
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, false);
            float leftX = 0;
            if (linesData.isRight) {
                leftX = linesData.lableX;
            } else {
                leftX = linesData.lableX - mBound.width() < 0 ? 0 : linesData.lableX - mBound.width();
            }
            canvas.translate(leftX, linesData.lableY);
            currentLayout.draw(canvas);
            canvas.restore();
            canvas.save();

//            mCurrentPaint.setTextAlign(Paint.Align.LEFT);
            mPaint.getTextBounds(linesData.pecent, 0, linesData.pecent.length(), mBound);
            float leftPX = 0;
            if (linesData.isRight) {
                leftPX = linesData.lableX;
            } else {
                leftPX = linesData.lableX - mBound.width() < 0 ? 0 : linesData.lableX - mBound.width();
            }
            StaticLayout currentLayout2 = new StaticLayout(linesData.pecent, mCurrentPaint, (int) (mBound.width() + 3 * oneDp),
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, false);
            canvas.translate(leftPX, linesData.lableY + textpadding + currentLayout.getHeight());
            currentLayout2.draw(canvas);
            canvas.restore();
//            canvas.drawText(linesData.lable, linesData.lableX, linesData.lableY, mCurrentPaint);
//            canvas.drawText(linesData.pecent, linesData.lableX, linesData.lableY + mBound.height() + 5 * oneDp, mCurrentPaint);
        }
    }

    /**
     * 计算各线的 Path
     **/
    private List<LinesData> calculateLinesDatas() {

        List<LinesData> linesDataList = new ArrayList<>();

        for (int i = 0; i < mDataMap.size(); i++) {
            LinesData linesData = new LinesData();
            SectorsData sectorsData = mSectorsDataList.get(i);

            // 线的起点
            float startX;
            float startY;
            startX = (float) (mRadius * Math.cos(sectorsData.middleAngle * Math.PI / 180));
            startY = (float) (mRadius * Math.sin(sectorsData.middleAngle * Math.PI / 180));

            linesData.startX = sectorsData.sectorsX + startX;
            linesData.startY = sectorsData.sectorsY + startY;

            // 线的转折点
            float turnX;
            float turnY;
            turnX = (float) ((mRadius + mLineLenght1) * Math.cos(sectorsData.middleAngle * Math.PI / 180));
            turnY = (float) ((mRadius + mLineLenght1) * Math.sin(sectorsData.middleAngle * Math.PI / 180));
            linesData.turnX = sectorsData.sectorsX + turnX;
            linesData.turnY = sectorsData.sectorsY + turnY;

            PieEntry pieEntry = mDataMap.get(i);
            DecimalFormat df = new DecimalFormat("0.00");
            String pecent = df.format((float) pieEntry.count / mCount * 100);
            linesData.lable = pieEntry.lable;
            linesData.pecent = pieEntry.count + "(" + pecent + ")%";
            // 线的终点
            if (sectorsData.middleAngle > 90 && sectorsData.middleAngle < 270) {
                linesData.endX = mSectorsX - mRadius - mLineLenght2;
            } else {
                linesData.endX = mSectorsX + mRadius + mLineLenght2;
            }

            linesData.endY = linesData.turnY;
            linesData.middleAngle = sectorsData.middleAngle;

            if (sectorsData.middleAngle >= 0 && sectorsData.middleAngle <= 90 || sectorsData.middleAngle > 270 && sectorsData.middleAngle <= 360) {
                linesData.lableX = linesData.turnX;
                linesData.isRight = true;
            } else {
                linesData.lableX = linesData.turnX;
                linesData.isRight = false;
            }
            linesData.lableY = -mBound.height() * 2 + linesData.turnY;
            linesDataList.add(linesData);
        }

        return linesDataList;
    }

    /**
     * 线数据类
     **/
    private static class LinesData {
        float startX;
        float startY;
        float turnX;
        float turnY;
        float endX;
        float endY;
        float middleAngle;

        float lableX;
        float lableY;
        String lable;
        String pecent;
        boolean isRight;

    }

    public static class PieEntry {
        private int count;
        private String lable;
        private int color;

        public PieEntry() {
        }

        public PieEntry(String lable, int count, int color) {
            this.lable = lable;
            this.count = count;
            this.color = color;
        }

    }

    /**
     * 扇形数据类
     **/
    private static class SectorsData {
        float left;
        float top;
        float right;
        float bottom;
        float startAngle;//扇形开始的角度
        float sweepAngle;
        float middleAngle;
        float sectorsX;
        float sectorsY;
        int color;
    }
}
