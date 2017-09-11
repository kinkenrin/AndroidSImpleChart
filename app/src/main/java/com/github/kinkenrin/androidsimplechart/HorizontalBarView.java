package com.github.kinkenrin.androidsimplechart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 水平方向的柱状图
 * Created by jinxl on 2017/9/7.
 */
public class HorizontalBarView extends View {
    Paint mPaint;
    Rect mBound;
    float mTitleTextSize;
    int mTitleTextColor;
    float mValueTextSize;
    int mBarWidth;
    int mValueTextColor;
    int mBarColor;
    int mLableRigthMargin;
    int mValueLeftMargin;

    int mLableWidth;
    int mLableHeight;
    int mMaxBarHeightSpace;
    int mDividerWidth;

    int mRealWidth;
    int mRealHeight;

    float mMaxCount = 0;

    List<BarEntry> datas = null;

    public HorizontalBarView(Context context) {
        this(context, null);
    }

    public HorizontalBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefaultAttr();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalBarView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.HorizontalBarView_hbv_titleTextColor:
                    mTitleTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.HorizontalBarView_hbv_valueTextColor:
                    mValueTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.HorizontalBarView_hbv_barColor:
                    mBarColor = a.getColor(attr, Color.parseColor("#00d1d1"));
                    break;
                case R.styleable.HorizontalBarView_hbv_titleTextSize:
                    mTitleTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HorizontalBarView_hbv_valueTextSize:
                    mValueTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HorizontalBarView_hbv_barWidth:
                    mBarWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HorizontalBarView_hbv_dividerWidth:
                    mDividerWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HorizontalBarView_hbv_lableRigthMagin:
                    mLableRigthMargin = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 9, getResources().getDisplayMetrics()));
                    break;
            }

        }
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBound = new Rect();
    }

    private void setDefaultAttr() {
        mTitleTextColor = Color.rgb(139, 152, 173);
        mValueTextColor = Color.rgb(61, 73, 102);
        mBarColor = Color.parseColor("#00d1d1");
        mTitleTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        mValueTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        mBarWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        mDividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        mLableRigthMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, getResources().getDisplayMetrics());
        mValueLeftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isEmpty()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            mRealWidth = width;
            mRealHeight = height;
            int maxLableLength = 0;
            String maxLengthStr = "";
            for (int i = 0; i < datas.size(); i++) {
                int length = datas.get(i).lable.length();
                if (maxLableLength < length) {
                    maxLableLength = length;
                    maxLengthStr = datas.get(i).lable;
                }
                if (mMaxCount < datas.get(i).count) {
                    mMaxCount = datas.get(i).count;
                }
            }
            mPaint.setTextSize(mTitleTextSize);
            mPaint.getTextBounds(maxLengthStr, 0, maxLableLength, mBound);

            mLableWidth = mBound.width();
            mLableHeight = Math.max(mBarWidth, mBound.height());
            mMaxBarHeightSpace = (int) ((width - getPaddingLeft() - getPaddingRight() - mLableWidth - mLableRigthMargin) * 0.9);

            mRealHeight = datas.size() * mBarWidth + (datas.size() - 1) * mDividerWidth + getPaddingTop() + getPaddingBottom();
            int realHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mRealHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, realHeightMeasureSpec);
        }

    }

    private boolean isEmpty() {
        if (datas == null || datas.size() == 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isEmpty()) {
            return;
        }
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            mPaint.setTextSize(mTitleTextSize);
            mPaint.setColor(mTitleTextColor);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
            float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

            BarEntry barEntry = datas.get(i);
            int lineStartY = getPaddingTop() + i * (mBarWidth + mDividerWidth);
            int lineCenterY = lineStartY + mBarWidth / 2;
            int lineEndY = lineStartY + mBarWidth;
            int barStartX = mLableWidth + mLableRigthMargin + getPaddingLeft();
            int baseLineY = (int) (lineCenterY - top / 2 - bottom / 2);//基线中间点的y轴计算公式
            canvas.drawText(barEntry.lable, mLableWidth + getPaddingLeft(), baseLineY, mPaint);

            //draw bar
            mPaint.setColor(mBarColor);
            mPaint.setStyle(Paint.Style.FILL);
            float barHeight = 0;
            if (mMaxCount > 0) {
                barHeight = barEntry.count / mMaxCount * mMaxBarHeightSpace;
                canvas.drawRect(barStartX, lineStartY, barHeight + barStartX, lineEndY, mPaint);
            }

            mPaint.setTextSize(mValueTextSize);
            mPaint.setColor(mValueTextColor);
            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(barEntry.count + "", barHeight + barStartX + mValueLeftMargin, baseLineY, mPaint);

        }
    }

    public void setDatas(List<BarEntry> datas) {
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
        this.datas.addAll(datas);
        invalidate();
        requestLayout();
    }

    public static class BarEntry {
        private int count;
        private String lable;

        private BarEntry() {
        }

        public BarEntry(String lable, int count) {
            this.lable = lable;
            this.count = count;
        }

    }
}
