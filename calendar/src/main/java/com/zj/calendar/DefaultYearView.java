package com.zj.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

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
        return false;
    }

    @Override
    protected void onDrawSchedule(Canvas canvas, Calendar calendar, int x, int y) {

    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasSchedule, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        float cx = x + mItemWidth / 2f;
        Paint paint = getPaintWithCalendarState(calendar, isSelected);
        String day = String.valueOf(calendar.getDay());
        canvas.drawText(day, cx, baselineY, paint);
        float mTextWidth = paint.measureText(day);
        if (hasSchedule) {
            paint.setStrokeWidth(2);
            paint.setColor(calendar.getScheduleColor());
            float ty = baselineY + mTextPadding + paint.descent();
            float tx = cx - mTextWidth / 2f;
            canvas.drawLine(tx, ty, tx + mTextWidth, ty, paint);
        }
    }
}
