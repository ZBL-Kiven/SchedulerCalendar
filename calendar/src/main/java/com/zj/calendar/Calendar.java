package com.zj.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * 日历对象
 */
@SuppressWarnings("unused")
public final class Calendar implements Serializable, Comparable<Calendar> {

    /**
     * 年
     */
    private int year;

    /**
     * 月1-12
     */
    private int month;

    /**
     * 日1-31
     */
    private int day;


    /**
     * 是否是本月,这里对应的是月视图的本月，而非当前月份，请注意
     */
    private boolean isCurrentMonth;

    /**
     * 是否是今天
     */
    private boolean isCurrentDay;

    /**
     * 星期,0-6 对应周日到周一
     */
    private int week;

    private Schedule schedule;


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public String getMonthName(android.content.Context context) {
        return CalendarUtil.getMonthDetailName(context, month);
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }


    public void setCurrentMonth(boolean currentMonth) {
        this.isCurrentMonth = currentMonth;
    }

    public boolean isCurrentDay() {
        return isCurrentDay;
    }

    public void setCurrentDay(boolean currentDay) {
        isCurrentDay = currentDay;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public boolean hasSchedule() {
        return schedule != null;
    }

    /**
     * 是否是相同月份
     *
     * @param calendar 日期
     * @return 是否是相同月份
     */
    public boolean isSameMonth(Calendar calendar) {
        return year == calendar.getYear() && month == calendar.getMonth();
    }

    /**
     * 比较日期
     *
     * @param calendar 日期
     * @return <0 0 >0
     */
    public int compareTo(Calendar calendar) {
        if (calendar == null) {
            return 1;
        }
        return toString().compareTo(calendar.toString());
    }

    public boolean isAvailable() {
        return year > 0 & month > 0 & day > 0 & day <= 31 & month <= 12 & year >= 1900 & year <= 2099;
    }

    public long getTimeInMillis() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.YEAR, year);
        calendar.set(java.util.Calendar.MONTH, month - 1);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, day);
        return calendar.getTimeInMillis();
    }

    public void setTimeInMillis(long ts) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(ts);
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        setYear(year);
        setMonth(month);
        setDay(day);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof Calendar) {
            if (((Calendar) o).getYear() == year && ((Calendar) o).getMonth() == month && ((Calendar) o).getDay() == day) return true;
        }
        return super.equals(o);
    }

    /**
     * 这里 Month 并没有 -1 ，意味着此处返回的实际时间戳的 calendar 比系统默认的 Month 多 1，目前用于接口请求。
     */
    public long[] getMonthDayRange() {
        java.util.Calendar cs = java.util.Calendar.getInstance();
        java.util.Calendar ce = java.util.Calendar.getInstance();
        if (month + 1 > 12) {
            ce.set(year + 1, 0, 0, 23, 59, 59);
        } else {
            ce.set(year, month, 0, 23, 59, 59);
        }
        cs.set(year, month - 1, 1, 0, 0, 0);

        long monthStartTime = setEndMillisData(cs.getTimeInMillis());
        long monthEndTime = setEndMillisData(ce.getTimeInMillis()) - 1;
        return new long[]{monthStartTime, monthEndTime};
    }

    /**
     * 这里 Month 做了处理，用于时间过滤的，与系统 Month Index 一致。
     */
    public long[] getDayRange() {
        java.util.Calendar cs = java.util.Calendar.getInstance();
        java.util.Calendar ce = java.util.Calendar.getInstance();
        cs.set(year, month - 1, day, 0, 0, 0);
        ce.set(year, month - 1, day + 1, 0, 0, 0);
        long hourStartTime = setEndMillisData(cs.getTimeInMillis());
        long hourEndTime = setEndMillisData(ce.getTimeInMillis()) - 1;
        return new long[]{hourStartTime, hourEndTime};
    }

    /**
     * 忽略毫秒值，便于更精确的过滤，误差缩小至 1ms
     */
    private long setEndMillisData(long ts) {
        return (((long) (ts / 1000f)) * 1000L);
    }

    @NonNull
    @Override
    public String toString() {
        return year + "" + (month < 10 ? "0" + month : month) + "" + (day < 10 ? "0" + day : day);
    }


    public int getScheduleColor() {
        return schedule.getScheduleColor();
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(@Nullable Schedule schedule) {
        this.schedule = schedule;
    }

    public void newSchedule(int schemeColor, String name, Object obj) {
        this.schedule = new Schedule(schemeColor, name, obj);
    }

    public void newSchedule(int schemeColor, String name) {
        this.schedule = new Schedule(schemeColor, name, null);
    }

    final void clearSchedule() {
        this.schedule = null;
    }
}
