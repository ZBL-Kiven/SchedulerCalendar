package com.zj.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

class DefaultYearView extends YearView {

    private final int mTextPadding;

    public DefaultYearView(Context context) {
        super(context);
        mTextPadding = CalendarUtil.dipToPx(context, 3);
    }

    @Override
    protected void onDrawMonth(Canvas canvas, int year, int month, int x, int y, int width, int height) {

        String text = getContext().getResources().getStringArray(R.array.month_string_array)[month - 1];
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (calendar.get(java.util.Calendar.YEAR) == year && (calendar.get(java.util.Calendar.MONTH) + 1) == month) {
            mMonthTextPaint.setColor(mDelegate.getYearViewCurrentMonthTextColor());
        } else {
            mMonthTextPaint.setColor(mDelegate.getYearViewMonthTextColor());
        }
        canvas.drawText(text, x + mItemWidth / 2f - mTextPadding, y + mMonthTextBaseLine, mMonthTextPaint);
    }

    @Override
    protected void onDrawWeek(Canvas canvas, int week, int x, int y, int width, int height) {
        String text = getContext().getResources().getStringArray(R.array.year_view_week_string_array)[week];
        canvas.drawText(text, x + width / 2f, y + mWeekTextBaseLine, mWeekTextPaint);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasSchedule) {
        Paint paint = new Paint(mSelectedPaint);
        if (calendar.isCurrentDay()) {
            paint.setColor(mDelegate.getSelectedTodayThemeColor());
        } else {
            paint.setColor(mDelegate.getSelectedThemeColor());
        }
        float cy = mItemHeight / 2f + y;
        float cx = x + mItemWidth / 2f;
        if (calendar.isCurrentDay()) {
            paint.setColor(mDelegate.getSelectedTodayThemeColor());
        }
        paint.setStyle(Paint.Style.FILL);
        float radius = (mTextBaseLine) / 2f;
        canvas.drawCircle(cx, cy, radius, paint);
        return true;
    }

    @Override
    protected void onDrawSchedule(Canvas canvas, Calendar calendar, int x, int y) {
        Paint paint = new Paint();
        float baselineY = mTextBaseLine + y;
        float cx = x + mItemWidth / 2f;
        paint.setStrokeWidth(2);
        paint.setColor(calendar.getScheduleColor());
        float ty = baselineY + mTextPadding + paint.descent();
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        canvas.drawCircle(cx, ty + metrics.density * 2.5f, metrics.density * 1.5f, paint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasSchedule, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        float cx = x + mItemWidth / 2f;
        Paint paint = getPaintWithCalendarState(calendar, isSelected);
        String day = String.valueOf(calendar.getDay());
        if (calendar.isCurrentDay()) {
            if (isSelected) {
                paint.setColor(mDelegate.getSelectedTodayTextColor());
            } else {
                paint.setColor(mDelegate.getCurDayTextColor());
            }
        } else {
            if (isSelected) {
                paint.setColor(mDelegate.getSelectedTextColor());
            }
        }
        canvas.drawText(day, cx, baselineY, paint);
    }
}
