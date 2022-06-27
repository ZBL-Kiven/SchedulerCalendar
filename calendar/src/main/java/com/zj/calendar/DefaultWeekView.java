package com.zj.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

final class DefaultWeekView extends WeekView {
    private final Paint mScheduleBasicPaint = new Paint();
    private final float mRadio;
    private final int mPadding;

    public DefaultWeekView(Context context) {
        super(context);
        mScheduleBasicPaint.setAntiAlias(true);
        mScheduleBasicPaint.setStyle(Paint.Style.FILL);
        mScheduleBasicPaint.setTextAlign(Paint.Align.CENTER);
        mScheduleBasicPaint.setColor(0xffed5353);
        mScheduleBasicPaint.setFakeBoldText(true);
        mRadio = CalendarUtil.dipToPx(getContext(), 4);
        mPadding = CalendarUtil.dipToPx(getContext(), 4);
    }

    /**
     * 如果需要点击Schedule没有效果，则return true
     *
     * @param canvas      canvas
     * @param calendar    日历日历calendar
     * @param x           日历Card x起点坐标
     * @param hasSchedule hasSchedule 非标记的日期
     * @return true 则绘制onDrawSchedule，因为这里背景色不是是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasSchedule) {
        Paint paint = new Paint(mSelectedPaint);
        if (calendar.isCurrentDay()) {
            paint.setColor(mDelegate.getSelectedTodayThemeColor());
        }
        paint.setStyle(Paint.Style.FILL);
        float cy = (mTextBaseLine + mPadding) / 2f;
        float radius = (mTextBaseLine - mPadding) / 2f;
        canvas.drawCircle(x + getRectCenter(), cy, radius, paint);
        return true;
    }


    @Override
    protected void onDrawSchedule(Canvas canvas, Calendar calendar, int x, int y) {
        mScheduleBasicPaint.setColor(calendar.getScheduleColor());
        canvas.drawCircle(x + getRectCenter(), y + (mPadding / 2f) + mRadio + mTextBaseLine, mRadio, mScheduleBasicPaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasSchedule, boolean isSelected) {
        Paint paint = getPaintWithCalendarState(calendar, isSelected);
        if (calendar.isCurrentDay()) {
            if (isSelected) {
                paint.setColor(mDelegate.getSelectedTodayTextColor());
            } else {
                paint.setColor(mDelegate.getCurDayTextColor());
            }
        } else {
            if (mDelegate.mSelectedCalendar != null && calendar.getMonth() != mDelegate.mSelectedCalendar.getMonth()) {
                paint.setColor(mDelegate.getOtherMonthDayTextColor());
            }
        }
        canvas.drawText(String.valueOf(calendar.getDay()), x + getRectCenter(), getTextTopStart(), paint);
    }

    private float getTextTopStart() {
        return mTextBaseLine - mItemHeight / 6f;
    }

    private float getRectCenter() {
        return mItemWidth / 2f;
    }
}
