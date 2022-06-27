package com.zj.swipeRv.cv;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;


public class CollapsedLayout extends ViewGroup {

    protected boolean fromLeftToRight = true;
    protected float verticalSpace;
    protected float collapseWidth;

    public CollapsedLayout(Context context) {
        this(context, null, 0);
    }

    public CollapsedLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        verticalSpace = dp2px(10);
        collapseWidth = dp2px(10);
        setClipChildren(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() <= 0) return;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        int rawWidth = 0;
        int rawHeight = 0;
        int rowIndex = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                if (i == count - 1) {
                    height += rawHeight;
                    width = Math.max(width, rawWidth);
                }
                continue;
            }
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (rawWidth + childWidth - (rowIndex > 0 ? collapseWidth : 0) > widthSpecSize - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(width, rawWidth);
                rawWidth = childWidth;
                height += rawHeight + verticalSpace;
                rawHeight = childHeight;
                rowIndex = 0;
            } else {
                rawWidth += childWidth;
                if (rowIndex > 0) {
                    rawWidth -= collapseWidth;
                }
                rawHeight = Math.max(rawHeight, childHeight);
            }

            if (i == count - 1) {
                width = Math.max(rawWidth, width);
                height += rawHeight;
            }
            rowIndex++;
        }
        setMeasuredDimension(widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : width + getPaddingLeft() + getPaddingRight(), heightSpecMode == MeasureSpec.EXACTLY ? heightSpecSize : height + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() <= 0) {
            return;
        }
        int viewWidth = r - l;
        int leftOffset = getPaddingLeft();
        int topOffset = getPaddingTop();
        int rowMaxHeight = 0;
        int rowIndex = 0;
        View childView;
        int maxHeight = 1000;
        for (int w = 0, count = getChildCount(); w < count; w++) {
            childView = getChildAt(w);
            if (childView.getVisibility() == GONE) continue;

            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int occupyWidth = lp.leftMargin + childView.getMeasuredWidth() + lp.rightMargin;
            if (leftOffset + occupyWidth + getPaddingRight() > viewWidth) {
                leftOffset = getPaddingLeft();
                topOffset += rowMaxHeight + verticalSpace;
                rowMaxHeight = 0;
                rowIndex = 0;
            }
            if (fromLeftToRight) childView.setZ(maxHeight - (2.0f * w));
            int left = leftOffset + lp.leftMargin;
            int top = topOffset + lp.topMargin;
            int right = leftOffset + lp.leftMargin + childView.getMeasuredWidth();
            int bottom = topOffset + lp.topMargin + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);
            leftOffset += occupyWidth;
            int occupyHeight = lp.topMargin + childView.getMeasuredHeight() + lp.bottomMargin;
            if (rowIndex != count - 1) {
                leftOffset -= collapseWidth;
            }
            rowMaxHeight = Math.max(rowMaxHeight, occupyHeight);
            rowIndex++;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    public float dp2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }
}