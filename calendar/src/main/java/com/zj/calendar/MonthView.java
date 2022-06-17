package com.zj.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public abstract class MonthView extends BaseMonthView {

    public MonthView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLineCount == 0) return;
        mItemWidth = (getWidth() - mDelegate.getCalendarPaddingLeft() - mDelegate.getCalendarPaddingRight()) / 7;
        onPreviewHook();
        int count = mLineCount * 7;
        int d = 0;
        for (int i = 0; i < mLineCount; i++) {
            for (int j = 0; j < 7; j++) {
                Calendar calendar = mItems.get(d);
                if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH) {
                    if (d > mItems.size() - mNextDiff) {
                        return;
                    }
                    if (!calendar.isCurrentMonth()) {
                        ++d;
                        continue;
                    }
                } else if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_FIT_MONTH) {
                    if (d >= count) {
                        return;
                    }
                }
                draw(canvas, calendar, i, j, d);
                ++d;
            }
        }
    }


    /**
     * 开始绘制
     *
     * @param canvas   canvas
     * @param calendar 对应日历
     * @param i        i
     * @param j        j
     * @param d        d
     */
    private void draw(Canvas canvas, Calendar calendar, int i, int j, int d) {
        int x = j * mItemWidth + mDelegate.getCalendarPaddingLeft();
        int y = i * mItemHeight;
        onLoopStart(x, y);
        boolean isSelected = d == mCurrentItem;
        boolean hasSchedule = calendar.hasSchedule();

        if (hasSchedule) {
            //标记的日子
            boolean isDrawSelected = false;
            //是否继续绘制选中的onDrawSchedule
            if (isSelected) {
                isDrawSelected = onDrawSelected(canvas, calendar, x, y, true);
            }
            if (isDrawSelected || !isSelected) {
                //将画笔设置为标记颜色
                mSchedulePaint.setColor(calendar.getScheduleColor() != 0 ? calendar.getScheduleColor() : mDelegate.getScheduleThemeColor());
                onDrawSchedule(canvas, calendar, x, y);
            }
        } else {
            if (isSelected) {
                onDrawSelected(canvas, calendar, x, y, false);
            }
        }
        onDrawText(canvas, calendar, x, y, hasSchedule, isSelected);
    }


    @Override
    public void onClick(View v) {
        if (!isClick) {
            return;
        }
        Calendar calendar = getIndex();

        if (calendar == null) {
            return;
        }

        if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH && !calendar.isCurrentMonth()) {
            return;
        }

        if (onCalendarIntercept(calendar)) {
            if(mDelegate.mCalendarInterceptListener != null) {
                mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(calendar, true);
            }
            return;
        }


        if (!isInRange(calendar)) {
            if (mDelegate.mCalendarSelectListener != null) {
                mDelegate.mCalendarSelectListener.onCalendarOutOfRange(calendar);
            }
            return;
        }

        mCurrentItem = mItems.indexOf(calendar);

        if (!calendar.isCurrentMonth() && mMonthViewPager != null) {
            int cur = mMonthViewPager.getCurrentItem();
            int position = mCurrentItem < 7 ? cur - 1 : cur + 1;
            mMonthViewPager.setCurrentItem(position);
        }

        if (mDelegate.mInnerListener != null) {
            mDelegate.mInnerListener.onMonthDateSelected(calendar, true);
        }

        if (mParentLayout != null) {
            if (calendar.isCurrentMonth()) {
                mParentLayout.updateSelectPosition(mItems.indexOf(calendar));
            } else {
                mParentLayout.updateSelectWeek(CalendarUtil.getWeekFromDayInMonth(calendar, mDelegate.getWeekStart()));
            }

        }

        if (mDelegate.mCalendarSelectListener != null) {
            mDelegate.mCalendarSelectListener.onCalendarSelect(calendar, true);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mDelegate.mCalendarLongClickListener == null) return false;
        if (!isClick) {
            return false;
        }
        Calendar calendar = getIndex();
        if (calendar == null) {
            return false;
        }

        if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH && !calendar.isCurrentMonth()) {
            return false;
        }

        if (onCalendarIntercept(calendar)) {
            if (mDelegate.mCalendarInterceptListener != null) {
                mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(calendar, true);
            }
            return false;
        }

        boolean isCalendarInRange = isInRange(calendar);

        if (!isCalendarInRange) {
            if (mDelegate.mCalendarLongClickListener != null) {
                mDelegate.mCalendarLongClickListener.onCalendarLongClickOutOfRange(calendar);
            }
            return true;
        }

        if (mDelegate.isPreventLongPressedSelected()) {
            if (mDelegate.mCalendarLongClickListener != null) {
                mDelegate.mCalendarLongClickListener.onCalendarLongClick(calendar);
            }
            return true;
        }


        mCurrentItem = mItems.indexOf(calendar);

        if (!calendar.isCurrentMonth() && mMonthViewPager != null) {
            int cur = mMonthViewPager.getCurrentItem();
            int position = mCurrentItem < 7 ? cur - 1 : cur + 1;
            mMonthViewPager.setCurrentItem(position);
        }

        if (mDelegate.mInnerListener != null) {
            mDelegate.mInnerListener.onMonthDateSelected(calendar, true);
        }

        if (mParentLayout != null) {
            if (calendar.isCurrentMonth()) {
                mParentLayout.updateSelectPosition(mItems.indexOf(calendar));
            } else {
                mParentLayout.updateSelectWeek(CalendarUtil.getWeekFromDayInMonth(calendar, mDelegate.getWeekStart()));
            }

        }

        if (mDelegate.mCalendarSelectListener != null) {
            mDelegate.mCalendarSelectListener.onCalendarSelect(calendar, true);
        }

        if (mDelegate.mCalendarLongClickListener != null) {
            mDelegate.mCalendarLongClickListener.onCalendarLongClick(calendar);
        }
        invalidate();
        return true;
    }

    /**
     * 绘制选中的日期
     *
     * @param canvas      canvas
     * @param calendar    日历日历calendar
     * @param x           日历Card x起点坐标
     * @param y           日历Card y起点坐标
     * @param hasSchedule hasSchedule 非标记的日期
     * @return 是否绘制onDrawSchedule，true or false
     */
    protected abstract boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasSchedule);

    /**
     * 绘制标记的日期,这里可以是背景色，标记色什么的
     *
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标
     * @param y        日历Card y起点坐标
     */
    protected abstract void onDrawSchedule(Canvas canvas, Calendar calendar, int x, int y);


    /**
     * 绘制日历文本
     *
     * @param canvas      canvas
     * @param calendar    日历calendar
     * @param x           日历Card x起点坐标
     * @param y           日历Card y起点坐标
     * @param hasSchedule 是否是标记的日期
     * @param isSelected  是否选中
     */
    protected abstract void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasSchedule, boolean isSelected);
}
