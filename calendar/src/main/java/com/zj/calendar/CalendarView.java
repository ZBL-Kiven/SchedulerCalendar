package com.zj.calendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日历布局
 * 各个类使用包权限，避免不必要的public
 */
@SuppressWarnings({"unused"})
public class CalendarView extends FrameLayout {

    /**
     * 抽取自定义属性
     */
    private final CalendarViewDelegate mDelegate;

    /**
     * 自定义自适应高度的ViewPager
     */
    private MonthViewPager mMonthPager;

    /**
     * 日历周视图
     */
    private WeekViewPager mWeekPager;

    /**
     * 星期栏的线
     */
    private View mWeekLine;

    /**
     * 月份快速选取
     */
    private YearViewPager mYearViewPager;

    /**
     * 星期栏
     */
    private WeekBar mWeekBar;

    /**
     * 日历外部收缩布局
     */
    CalendarLayout mParentLayout;


    public CalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDelegate = new CalendarViewDelegate(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.cv_layout_calendar_view, this, true);
        FrameLayout frameContent = findViewById(R.id.frameContent);
        this.mWeekPager = findViewById(R.id.vp_week);
        this.mWeekPager.setup(mDelegate);

        try {
            Constructor<?> constructor = mDelegate.getWeekBarClass().getConstructor(Context.class);
            mWeekBar = (WeekBar) constructor.newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frameContent.addView(mWeekBar, 2);
        mWeekBar.setup(mDelegate);
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());

        this.mWeekLine = findViewById(R.id.line);
        this.mWeekLine.setBackgroundColor(mDelegate.getWeekLineBackground());
        LayoutParams lineParams = (LayoutParams) this.mWeekLine.getLayoutParams();
        lineParams.setMargins(mDelegate.getWeekLineMargin(), mDelegate.getWeekBarHeight(), mDelegate.getWeekLineMargin(), 0);
        this.mWeekLine.setLayoutParams(lineParams);

        this.mMonthPager = findViewById(R.id.vp_month);
        this.mMonthPager.mWeekPager = mWeekPager;
        this.mMonthPager.mWeekBar = mWeekBar;
        LayoutParams params = (LayoutParams) this.mMonthPager.getLayoutParams();
        params.setMargins(0, mDelegate.getWeekBarHeight() + CalendarUtil.dipToPx(context, 1), 0, 0);
        mWeekPager.setLayoutParams(params);


        mYearViewPager = findViewById(R.id.selectLayout);
        mYearViewPager.setPadding(mDelegate.getYearViewPaddingLeft(), 0, mDelegate.getYearViewPaddingRight(), 0);
        mYearViewPager.setBackgroundColor(mDelegate.getYearViewBackground());
        mYearViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mWeekPager.getVisibility() == VISIBLE) {
                    return;
                }
                if (mDelegate.mYearChangeListener != null) {
                    mDelegate.mYearChangeListener.onYearChange(position + mDelegate.getMinYear());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDelegate.mInnerListener = new OnInnerDateSelectedListener() {
            /**
             * 月视图选择事件
             * @param calendar calendar
             * @param isClick  是否是点击
             */
            @Override
            public void onMonthDateSelected(Calendar calendar, boolean isClick) {

                if (calendar.getYear() == mDelegate.getCurrentDay().getYear() && calendar.getMonth() == mDelegate.getCurrentDay().getMonth() && mMonthPager.getCurrentItem() != mDelegate.mCurrentMonthViewItem) {
                    return;
                }
                mDelegate.mIndexCalendar = calendar;
                if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick) {
                    mDelegate.mSelectedCalendar = calendar;
                }
                mWeekPager.updateSelected(mDelegate.mIndexCalendar, false);
                mMonthPager.updateSelected();
                if (mWeekBar != null && (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick)) {
                    mWeekBar.onDateSelected(calendar, mDelegate.getWeekStart(), isClick);
                }
            }

            /**
             * 周视图选择事件
             * @param calendar calendar
             * @param isClick 是否是点击
             */
            @Override
            public void onWeekDateSelected(Calendar calendar, boolean isClick) {
                mDelegate.mIndexCalendar = calendar;
                if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick || calendar.equals(mDelegate.mSelectedCalendar)) {
                    mDelegate.mSelectedCalendar = calendar;
                }
                int y = calendar.getYear() - mDelegate.getMinYear();
                int position = 12 * y + calendar.getMonth() - mDelegate.getMinYearMonth();
                mWeekPager.updateSingleSelect();
                mMonthPager.setCurrentItem(position, false);
                mMonthPager.updateSelected();
                if (mWeekBar != null && (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT || isClick || calendar.equals(mDelegate.mSelectedCalendar))) {
                    mWeekBar.onDateSelected(calendar, mDelegate.getWeekStart(), isClick);
                }
            }
        };


        if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            if (isInRange(mDelegate.getCurrentDay())) {
                mDelegate.mSelectedCalendar = mDelegate.createCurrentDate();
            } else {
                mDelegate.mSelectedCalendar = mDelegate.getMinRangeCalendar();
            }
        } else {
            mDelegate.mSelectedCalendar = new Calendar();
        }

        mDelegate.mIndexCalendar = mDelegate.mSelectedCalendar;

        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, mDelegate.getWeekStart(), false);

        mMonthPager.setup(mDelegate);
        mMonthPager.setCurrentItem(mDelegate.mCurrentMonthViewItem);
        mYearViewPager.setOnMonthSelectedListener((year, month) -> {
            int position = 12 * (year - mDelegate.getMinYear()) + month - mDelegate.getMinYearMonth();
            closeSelectLayout(position);
            mDelegate.isShowYearSelectedLayout = false;
        });
        mYearViewPager.setup(mDelegate);
        mWeekPager.updateSelected(mDelegate.createCurrentDate(), false);
    }

    /**
     * 设置日期范围
     *
     * @param minYear      最小年份
     * @param minYearMonth 最小年份对应月份
     * @param minYearDay   最小年份对应天
     * @param maxYear      最大月份
     * @param maxYearMonth 最大月份对应月份
     * @param maxYearDay   最大月份对应天
     */
    public void setRange(int minYear, int minYearMonth, int minYearDay, int maxYear, int maxYearMonth, int maxYearDay) {
        if (CalendarUtil.compareTo(minYear, minYearMonth, minYearDay, maxYear, maxYearMonth, maxYearDay) > 0) {
            return;
        }
        mDelegate.setRange(minYear, minYearMonth, minYearDay, maxYear, maxYearMonth, maxYearDay);
        mWeekPager.notifyDataSetChanged();
        mYearViewPager.notifyDataSetChanged();
        mMonthPager.notifyDataSetChanged();
        if (!isInRange(mDelegate.mSelectedCalendar)) {
            mDelegate.mSelectedCalendar = mDelegate.getMinRangeCalendar();
            mDelegate.updateSelectCalendarSchedule();
            mDelegate.mIndexCalendar = mDelegate.mSelectedCalendar;
        }
        mWeekPager.updateRange();
        mMonthPager.updateRange();
        mYearViewPager.updateRange();
    }

    /**
     * 获取当天
     *
     * @return 返回今天
     */
    public int getCurDay() {
        return mDelegate.getCurrentDay().getDay();
    }

    /**
     * 获取本月
     *
     * @return 返回本月
     */
    public int getCurMonth() {
        return mDelegate.getCurrentDay().getMonth();
    }

    /**
     * 获取本年
     *
     * @return 返回本年
     */
    public int getCurYear() {
        return mDelegate.getCurrentDay().getYear();
    }


    /**
     * 打开日历年月份快速选择
     *
     * @param year 年
     */
    public void showMonthOfYearLayout(final int year) {
        showSelectLayout(year);
    }

    /**
     * 打开日历年月份快速选择
     * 请使用 showYearSelectLayout(final int year) 代替，这个没什么，越来越规范
     *
     * @param year 年
     */
    private void showSelectLayout(final int year) {
        if (mParentLayout != null && mParentLayout.mContentView != null) {
            if (!mParentLayout.isExpand()) {
                mParentLayout.expand();
                //return;
            }
        }
        mWeekPager.setVisibility(GONE);
        mDelegate.isShowYearSelectedLayout = true;
        if (mParentLayout != null) {
            mParentLayout.hideContentView();
        }
        mWeekBar.animate().translationY(-mWeekBar.getHeight()).setInterpolator(new LinearInterpolator()).setDuration(260).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mWeekBar.setVisibility(GONE);
                mYearViewPager.setVisibility(VISIBLE);
                mYearViewPager.scrollToYear(year, false);
                if (mParentLayout != null && mParentLayout.mContentView != null) {
                    mParentLayout.expand();
                }
            }
        });

        mMonthPager.animate().scaleX(0).scaleY(0).setDuration(260).setInterpolator(new LinearInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mDelegate.mYearViewChangeListener != null) {
                    mDelegate.mYearViewChangeListener.onYearViewChange(false);
                }
            }
        });
    }


    /**
     * 年月份选择视图是否打开
     *
     * @return true or false
     */
    public boolean isYearSelectLayoutVisible() {
        return mYearViewPager.getVisibility() == VISIBLE;
    }

    /**
     * 关闭年月视图选择布局
     */
    public void closeYearSelectLayout() {
        if (mYearViewPager.getVisibility() == GONE) {
            return;
        }
        int position = 12 * (mDelegate.mSelectedCalendar.getYear() - mDelegate.getMinYear()) + mDelegate.mSelectedCalendar.getMonth() - mDelegate.getMinYearMonth();
        closeSelectLayout(position);
        mDelegate.isShowYearSelectedLayout = false;
    }

    /**
     * 关闭日历布局，同时会滚动到指定的位置
     *
     * @param position 某一年
     */
    private void closeSelectLayout(final int position) {
        mYearViewPager.setVisibility(GONE);
        mWeekBar.setVisibility(VISIBLE);
        if (position == mMonthPager.getCurrentItem()) {
            if (mDelegate.mCalendarSelectListener != null && mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_SINGLE) {
                mDelegate.mCalendarSelectListener.onCalendarSelect(mDelegate.mSelectedCalendar, false);
            }
        } else {
            mMonthPager.setCurrentItem(position, false);
        }
        mWeekBar.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(280).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mWeekBar.setVisibility(VISIBLE);
            }
        });
        mMonthPager.animate().scaleX(1).scaleY(1).setDuration(180).setInterpolator(new LinearInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mDelegate.mYearViewChangeListener != null) {
                    mDelegate.mYearViewChangeListener.onYearViewChange(true);
                }
                if (mParentLayout != null) {
                    mParentLayout.showContentView();
                    if (mParentLayout.isExpand()) {
                        mMonthPager.setVisibility(VISIBLE);
                    } else {
                        mWeekPager.setVisibility(VISIBLE);
                        mParentLayout.shrink();
                    }
                } else {
                    mMonthPager.setVisibility(VISIBLE);
                }
                mMonthPager.clearAnimation();
            }
        });
    }

    /**
     * 滚动到当前
     */
    public void scrollToCurrent() {
        scrollToCurrent(false);
    }

    /**
     * 滚动到当前
     *
     * @param smoothScroll smoothScroll
     */
    public void scrollToCurrent(boolean smoothScroll) {
        if (!isInRange(mDelegate.getCurrentDay())) {
            return;
        }
        Calendar calendar = mDelegate.createCurrentDate();
        if (mDelegate.mCalendarInterceptListener != null && mDelegate.mCalendarInterceptListener.onCalendarIntercept(calendar)) {
            mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(calendar, false);
            return;
        }
        mDelegate.mSelectedCalendar = mDelegate.createCurrentDate();
        mDelegate.mIndexCalendar = mDelegate.mSelectedCalendar;
        mDelegate.updateSelectCalendarSchedule();
        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, mDelegate.getWeekStart(), false);
        if (mMonthPager.getVisibility() == VISIBLE) {
            mMonthPager.scrollToCurrent(smoothScroll);
            mWeekPager.updateSelected(mDelegate.mIndexCalendar, false);
        } else {
            mWeekPager.scrollToCurrent(smoothScroll);
        }
        mYearViewPager.scrollToYear(mDelegate.getCurrentDay().getYear(), smoothScroll);
    }


    /**
     * 滚动到下一个月
     */
    public void scrollToNext() {
        scrollToNext(false);
    }

    /**
     * 滚动到下一个月
     *
     * @param smoothScroll smoothScroll
     */
    public void scrollToNext(boolean smoothScroll) {
        if (isYearSelectLayoutVisible()) {
            mYearViewPager.setCurrentItem(mYearViewPager.getCurrentItem() + 1, smoothScroll);
        } else if (mWeekPager.getVisibility() == VISIBLE) {
            mWeekPager.setCurrentItem(mWeekPager.getCurrentItem() + 1, smoothScroll);
        } else {
            mMonthPager.setCurrentItem(mMonthPager.getCurrentItem() + 1, smoothScroll);
        }

    }

    /**
     * 滚动到上一个月
     */
    public void scrollToPre() {
        scrollToPre(false);
    }

    /**
     * 滚动到上一个月
     *
     * @param smoothScroll smoothScroll
     */
    public void scrollToPre(boolean smoothScroll) {
        if (isYearSelectLayoutVisible()) {
            mYearViewPager.setCurrentItem(mYearViewPager.getCurrentItem() - 1, smoothScroll);
        } else if (mWeekPager.getVisibility() == VISIBLE) {
            mWeekPager.setCurrentItem(mWeekPager.getCurrentItem() - 1, smoothScroll);
        } else {
            mMonthPager.setCurrentItem(mMonthPager.getCurrentItem() - 1, smoothScroll);
        }
    }

    /**
     * 滚动到选择的日历
     */
    public void scrollToSelectCalendar() {
        if (!mDelegate.mSelectedCalendar.isAvailable()) {
            return;
        }
        scrollToCalendar(mDelegate.mSelectedCalendar.getYear(), mDelegate.mSelectedCalendar.getMonth(), mDelegate.mSelectedCalendar.getDay(), false, true);
    }

    /**
     * 滚动到指定日期
     *
     * @param year  year
     * @param month month
     * @param day   day
     */
    public void scrollToCalendar(int year, int month, int day) {
        scrollToCalendar(year, month, day, false, true);
    }

    /**
     * 滚动到指定日期
     *
     * @param year         year
     * @param month        month
     * @param day          day
     * @param smoothScroll smoothScroll
     */
    public void scrollToCalendar(int year, int month, int day, boolean smoothScroll) {
        scrollToCalendar(year, month, day, smoothScroll, true);
    }

    /**
     * 滚动到指定日期
     *
     * @param year           year
     * @param month          month
     * @param day            day
     * @param smoothScroll   smoothScroll
     * @param invokeListener 调用日期事件
     */
    public void scrollToCalendar(int year, int month, int day, boolean smoothScroll, boolean invokeListener) {

        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        if (!calendar.isAvailable()) {
            return;
        }
        if (!isInRange(calendar)) {
            return;
        }
        if (mDelegate.mCalendarInterceptListener != null && mDelegate.mCalendarInterceptListener.onCalendarIntercept(calendar)) {
            mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(calendar, false);
            return;
        }

        if (mWeekPager.getVisibility() == VISIBLE) {
            mWeekPager.scrollToCalendar(year, month, day, smoothScroll, invokeListener);
        } else {
            mMonthPager.scrollToCalendar(year, month, day, smoothScroll, invokeListener);
        }
    }

    /**
     * 滚动到某一年
     *
     * @param year 快速滚动的年份
     */
    public void scrollToYear(int year) {
        scrollToYear(year, false);
    }

    /**
     * 滚动到某一年
     *
     * @param year         快速滚动的年份
     * @param smoothScroll smoothScroll
     */
    public void scrollToYear(int year, boolean smoothScroll) {
        if (mYearViewPager.getVisibility() != VISIBLE) {
            return;
        }
        mYearViewPager.scrollToYear(year, smoothScroll);
    }

    /**
     * 设置月视图是否可滚动
     *
     * @param monthViewScrollable 设置月视图是否可滚动
     */
    public final void setMonthViewScrollable(boolean monthViewScrollable) {
        mDelegate.setMonthViewScrollable(monthViewScrollable);
    }


    /**
     * 设置周视图是否可滚动
     *
     * @param weekViewScrollable 设置周视图是否可滚动
     */
    public final void setWeekViewScrollable(boolean weekViewScrollable) {
        mDelegate.setWeekViewScrollable(weekViewScrollable);
    }

    /**
     * 设置年视图是否可滚动
     *
     * @param yearViewScrollable 设置年视图是否可滚动
     */
    public final void setYearViewScrollable(boolean yearViewScrollable) {
        mDelegate.setYearViewScrollable(yearViewScrollable);
    }


    public final void setDefaultMonthViewSelectDay() {
        mDelegate.setDefaultCalendarSelectDay(CalendarViewDelegate.FIRST_DAY_OF_MONTH);
    }

    public final void setLastMonthViewSelectDay() {
        mDelegate.setDefaultCalendarSelectDay(CalendarViewDelegate.LAST_MONTH_VIEW_SELECT_DAY);
    }

    public final void setLastMonthViewSelectDayIgnoreCurrent() {
        mDelegate.setDefaultCalendarSelectDay(CalendarViewDelegate.LAST_MONTH_VIEW_SELECT_DAY_IGNORE_CURRENT);
    }

    /**
     * 清除单选
     */
    public final void clearSingleSelect() {
        mDelegate.mSelectedCalendar = new Calendar();
        mMonthPager.clearSingleSelect();
        mWeekPager.clearSingleSelect();
    }

    /**
     * 设置月视图项高度
     *
     * @param calendarItemHeight MonthView item height
     */
    public final void setCalendarItemHeight(int calendarItemHeight) {
        if (mDelegate.getCalendarItemHeight() == calendarItemHeight) {
            return;
        }
        mDelegate.setCalendarItemHeight(calendarItemHeight);
        mMonthPager.updateItemHeight();
        mWeekPager.updateItemHeight();
        if (mParentLayout == null) {
            return;
        }
        mParentLayout.updateCalendarItemHeight();
    }


    /**
     * 设置月视图
     *
     * @param cls MonthView.class
     */
    public final void setMonthView(Class<?> cls) {
        if (cls == null) {
            return;
        }
        if (mDelegate.getMonthViewClass().equals(cls)) {
            return;
        }
        mDelegate.setMonthViewClass(cls);
        mMonthPager.updateMonthViewClass();
    }

    /**
     * 设置周视图
     *
     * @param cls WeekView.class
     */
    public final void setWeekView(Class<?> cls) {
        if (cls == null) {
            return;
        }
        if (mDelegate.getWeekBarClass().equals(cls)) {
            return;
        }
        mDelegate.setWeekViewClass(cls);
        mWeekPager.updateWeekViewClass();
    }

    /**
     * 设置周栏视图
     *
     * @param cls WeekBar.class
     */
    public final void setWeekBar(Class<?> cls) {
        if (cls == null) {
            return;
        }
        if (mDelegate.getWeekBarClass().equals(cls)) {
            return;
        }
        mDelegate.setWeekBarClass(cls);
        FrameLayout frameContent = findViewById(R.id.frameContent);
        frameContent.removeView(mWeekBar);

        try {
            Constructor<?> constructor = cls.getConstructor(Context.class);
            mWeekBar = (WeekBar) constructor.newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frameContent.addView(mWeekBar, 2);
        mWeekBar.setup(mDelegate);
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());
        this.mMonthPager.mWeekBar = mWeekBar;
        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, mDelegate.getWeekStart(), false);
    }


    /**
     * 添加日期拦截事件
     * 使用此方法，只能基于select_mode = single_mode
     * 否则的话，如果标记全部日期为不可点击，那是没有意义的，
     * 框架本身也不可能在滑动的过程中全部去判断每个日期的可点击性
     *
     * @param listener listener
     */
    public final void setOnCalendarInterceptListener(OnCalendarInterceptListener listener) {
        if (listener == null) {
            mDelegate.mCalendarInterceptListener = null;
        }
        if (listener == null || mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            return;
        }
        mDelegate.mCalendarInterceptListener = listener;
        if (!listener.onCalendarIntercept(mDelegate.mSelectedCalendar)) {
            return;
        }
        mDelegate.mSelectedCalendar = new Calendar();
    }

    /**
     * 点击视图Padding位置的事件
     *
     * @param listener listener
     */
    public final void setOnClickCalendarPaddingListener(OnClickCalendarPaddingListener listener) {
        if (listener == null) {
            mDelegate.mClickCalendarPaddingListener = null;
        }
        if (listener == null) {
            return;
        }
        mDelegate.mClickCalendarPaddingListener = listener;
    }

    /**
     * 年份改变事件
     *
     * @param listener listener
     */
    public void setOnYearChangeListener(OnYearChangeListener listener) {
        this.mDelegate.mYearChangeListener = listener;
    }

    /**
     * 月份改变事件
     *
     * @param listener listener
     */
    public void setOnMonthChangeListener(OnMonthChangeListener listener) {
        this.mDelegate.mMonthChangeListener = listener;
    }


    /**
     * 周视图切换监听
     *
     * @param listener listener
     */
    public void setOnWeekChangeListener(OnWeekChangeListener listener) {
        this.mDelegate.mWeekChangeListener = listener;
    }

    /**
     * 日期选择事件
     *
     * @param listener listener
     */
    public void setOnCalendarSelectListener(OnCalendarSelectListener listener) {
        this.mDelegate.mCalendarSelectListener = listener;
        if (mDelegate.mCalendarSelectListener == null) {
            return;
        }
        if (mDelegate.getSelectMode() != CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            return;
        }
        if (!isInRange(mDelegate.mSelectedCalendar)) {
            return;
        }
        mDelegate.updateSelectCalendarSchedule();
    }


    /**
     * 日期选择事件
     *
     * @param listener listener
     */
    public final void setOnCalendarRangeSelectListener(OnCalendarRangeSelectListener listener) {
        this.mDelegate.mCalendarRangeSelectListener = listener;
    }

    /**
     * 日期多选事件
     *
     * @param listener listener
     */
    public final void setOnCalendarMultiSelectListener(OnCalendarMultiSelectListener listener) {
        this.mDelegate.mCalendarMultiSelectListener = listener;
    }

    /**
     * 设置最小范围和最大范围，default：minRange = -1，maxRange = -1 没有限制
     *
     * @param minRange minRange
     * @param maxRange maxRange
     */
    public final void setSelectRange(int minRange, int maxRange) {
        if (minRange > maxRange) {
            return;
        }
        mDelegate.setSelectRange(minRange, maxRange);
    }

    /**
     * 是否拦截日期，此设置续设置mCalendarInterceptListener
     *
     * @param calendar calendar
     * @return 是否拦截日期
     */
    protected final boolean onCalendarIntercept(Calendar calendar) {
        return mDelegate.mCalendarInterceptListener != null && mDelegate.mCalendarInterceptListener.onCalendarIntercept(calendar);
    }


    /**
     * 获得最大多选数量
     *
     * @return 获得最大多选数量
     */
    public final int getMaxMultiSelectSize() {
        return mDelegate.getMaxMultiSelectSize();
    }

    /**
     * 设置最大多选数量
     *
     * @param maxMultiSelectSize 最大多选数量
     */
    public final void setMaxMultiSelectSize(int maxMultiSelectSize) {
        mDelegate.setMaxMultiSelectSize(maxMultiSelectSize);
    }

    /**
     * 最小选择范围
     *
     * @return 最小选择范围
     */
    public final int getMinSelectRange() {
        return mDelegate.getMinSelectRange();
    }

    /**
     * 最大选择范围
     *
     * @return 最大选择范围
     */
    public final int getMaxSelectRange() {
        return mDelegate.getMaxSelectRange();
    }

    /**
     * 日期长按事件
     *
     * @param listener listener
     */
    public void setOnCalendarLongClickListener(OnCalendarLongClickListener listener) {
        this.mDelegate.mCalendarLongClickListener = listener;
    }

    /**
     * 日期长按事件
     *
     * @param preventLongPressedSelect 防止长按选择日期
     * @param listener                 listener
     */
    public void setOnCalendarLongClickListener(OnCalendarLongClickListener listener, boolean preventLongPressedSelect) {
        this.mDelegate.mCalendarLongClickListener = listener;
        this.mDelegate.setPreventLongPressedSelected(preventLongPressedSelect);
    }

    /**
     * 视图改变事件
     *
     * @param listener listener
     */
    public void setOnViewChangeListener(OnViewChangeListener listener) {
        this.mDelegate.mViewChangeListener = listener;
    }


    public void setOnYearViewChangeListener(OnYearViewChangeListener listener) {
        this.mDelegate.mYearViewChangeListener = listener;
    }

    /**
     * 保持状态
     *
     * @return 状态
     */
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        if (mDelegate == null) {
            return super.onSaveInstanceState();
        }
        Bundle bundle = new Bundle();
        Parcelable parcelable = super.onSaveInstanceState();
        bundle.putParcelable("super", parcelable);
        bundle.putSerializable("selected_calendar", mDelegate.mSelectedCalendar);
        bundle.putSerializable("index_calendar", mDelegate.mIndexCalendar);
        return bundle;
    }

    /**
     * 恢复状态
     *
     * @param state 状态
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superData = bundle.getParcelable("super");
        mDelegate.mSelectedCalendar = (Calendar) bundle.getSerializable("selected_calendar");
        mDelegate.mIndexCalendar = (Calendar) bundle.getSerializable("index_calendar");
        if (mDelegate.mCalendarSelectListener != null) {
            mDelegate.mCalendarSelectListener.onCalendarSelect(mDelegate.mSelectedCalendar, false);
        }
        if (mDelegate.mIndexCalendar != null) {
            scrollToCalendar(mDelegate.mIndexCalendar.getYear(), mDelegate.mIndexCalendar.getMonth(), mDelegate.mIndexCalendar.getDay());
        }
        update();
        super.onRestoreInstanceState(superData);
    }


    /**
     * 初始化时初始化日历卡默认选择位置
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getParent() != null && getParent() instanceof CalendarLayout) {
            mParentLayout = (CalendarLayout) getParent();
            mMonthPager.mParentLayout = mParentLayout;
            mWeekPager.mParentLayout = mParentLayout;
            mParentLayout.mWeekBar = mWeekBar;
            mParentLayout.setup(mDelegate);
            mParentLayout.initStatus();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mDelegate == null || !mDelegate.isFullScreenCalendar()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        setCalendarItemHeight((height - mDelegate.getWeekBarHeight()) / 6);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 标记哪些日期有事件
     *
     * @param mScheduleDates mScheduleDatesMap 通过自己的需求转换即可
     */
    public final void setScheduleDate(ArrayList<Calendar> mScheduleDates) {
        if (this.mDelegate.mScheduleDatesMap == null) {
            this.mDelegate.mScheduleDatesMap = new HashMap<>();
        } else {
            this.mDelegate.mScheduleDatesMap.clear();
        }
        for (Calendar calendar : mScheduleDates) {
            this.mDelegate.mScheduleDatesMap.put(calendar.toString(), calendar);
        }
        this.mDelegate.updateSelectCalendarSchedule();
        this.mYearViewPager.update();
        this.mMonthPager.updateSchedule();
        this.mWeekPager.updateSchedule();
    }

    /**
     * 清空日期标记
     */
    public final void clearScheduleDate() {
        this.mDelegate.mScheduleDatesMap = null;
        this.mDelegate.clearSelectedSchedule();
        mYearViewPager.update();
        mMonthPager.updateSchedule();
        mWeekPager.updateSchedule();
    }

    /**
     * 添加事物标记
     *
     * @param calendar calendar
     */
    public final void addScheduleDate(Calendar calendar) {
        if (calendar == null || !calendar.isAvailable()) {
            return;
        }
        if (mDelegate.mScheduleDatesMap == null) {
            mDelegate.mScheduleDatesMap = new HashMap<>();
        }
        mDelegate.mScheduleDatesMap.remove(calendar.toString());
        mDelegate.mScheduleDatesMap.put(calendar.toString(), calendar);
        this.mDelegate.updateSelectCalendarSchedule();
        this.mYearViewPager.update();
        this.mMonthPager.updateSchedule();
        this.mWeekPager.updateSchedule();
    }

    /**
     * 添加事物标记
     *
     * @param mScheduleDates mScheduleDates
     */
    public final void addScheduleDate(Map<String, Calendar> mScheduleDates) {
        if (this.mDelegate == null || mScheduleDates == null || mScheduleDates.size() == 0) {
            return;
        }
        if (this.mDelegate.mScheduleDatesMap == null) {
            this.mDelegate.mScheduleDatesMap = new HashMap<>();
        }
        this.mDelegate.addSchedules(mScheduleDates);
        this.mDelegate.updateSelectCalendarSchedule();
        this.mYearViewPager.update();
        this.mMonthPager.updateSchedule();
        this.mWeekPager.updateSchedule();
    }

    /**
     * 移除某天的标记
     * 这个API是安全的
     *
     * @param calendar calendar
     */
    public final void removeScheduleDate(Calendar calendar) {
        if (calendar == null) {
            return;
        }
        if (mDelegate.mScheduleDatesMap == null || mDelegate.mScheduleDatesMap.size() == 0) {
            return;
        }
        mDelegate.mScheduleDatesMap.remove(calendar.toString());
        if (mDelegate.mSelectedCalendar.equals(calendar)) {
            mDelegate.clearSelectedSchedule();
        }

        mYearViewPager.update();
        mMonthPager.updateSchedule();
        mWeekPager.updateSchedule();
    }

    /**
     * 设置背景色
     *
     * @param yearViewBackground 年份卡片的背景色
     * @param weekBackground     星期栏背景色
     * @param lineBg             线的颜色
     */
    public void setBackground(int yearViewBackground, int weekBackground, int lineBg) {
        mWeekBar.setBackgroundColor(weekBackground);
        mYearViewPager.setBackgroundColor(yearViewBackground);
        mWeekLine.setBackgroundColor(lineBg);
    }


    /**
     * 设置文本颜色
     *
     * @param currentDayTextColor 今天字体颜色
     * @param curMonthTextColor   当前月份字体颜色
     * @param otherMonthColor     其它月份字体颜色
     */
    public void setTextColor(int currentDayTextColor, int curMonthTextColor, int otherMonthColor) {
        if (mDelegate == null || mMonthPager == null || mWeekPager == null) {
            return;
        }
        mDelegate.setTextColor(currentDayTextColor, curMonthTextColor, otherMonthColor);
        mMonthPager.updateStyle();
        mWeekPager.updateStyle();
    }

    /**
     * 设置选择的效果
     *
     * @param selectedThemeColor 选中的标记颜色
     * @param selectedTextColor  选中的字体颜色
     */
    public void setSelectedColor(int selectedThemeColor, int selectedTextColor, int selectedTodayTextColor, int selectedTodayThemeColor) {
        if (mDelegate == null || mMonthPager == null || mWeekPager == null) {
            return;
        }
        mDelegate.setSelectColor(selectedThemeColor, selectedTextColor, selectedTodayTextColor, selectedTodayThemeColor);
        mMonthPager.updateStyle();
        mWeekPager.updateStyle();
    }

    /**
     * 定制颜色
     *
     * @param selectedThemeColor 选中的标记颜色
     * @param schemeColor        标记背景色
     */
    public void setThemeColor(int selectedThemeColor, int schemeColor) {
        if (mDelegate == null || mMonthPager == null || mWeekPager == null) {
            return;
        }
        mDelegate.setThemeColor(selectedThemeColor, schemeColor);
        mMonthPager.updateStyle();
        mWeekPager.updateStyle();
    }

    /**
     * 设置标记的色
     *
     * @param schemeLunarTextColor 标记农历颜色
     * @param schemeColor          标记背景色
     * @param schemeTextColor      标记字体颜色
     */
    public void setScheduleColor(int schemeColor, int schemeTextColor, int schemeLunarTextColor) {
        if (mDelegate == null || mMonthPager == null || mWeekPager == null) {
            return;
        }
        mDelegate.setScheduleColor(schemeColor, schemeTextColor);
        mMonthPager.updateStyle();
        mWeekPager.updateStyle();
    }

    /**
     * 设置年视图的颜色
     *
     * @param yearViewMonthTextColor   年视图月份颜色
     * @param yearViewDayTextColor     年视图天的颜色
     * @param yarViewScheduleTextColor 年视图标记颜色
     */
    public void setYearViewTextColor(int yearViewMonthTextColor, int yearViewCurrentMonthTextColor, int yearViewDayTextColor, int yarViewScheduleTextColor) {
        if (mDelegate == null || mYearViewPager == null) {
            return;
        }
        mDelegate.setYearViewTextColor(yearViewMonthTextColor, yearViewCurrentMonthTextColor, yearViewDayTextColor, yarViewScheduleTextColor);
        mYearViewPager.updateStyle();
    }

    /**
     * 设置星期栏的背景和字体颜色
     *
     * @param weekBackground 背景色
     * @param weekTextColor  字体颜色
     */
    public void setWeeColor(int weekBackground, int weekTextColor) {
        if (mWeekBar == null) {
            return;
        }
        mWeekBar.setBackgroundColor(weekBackground);
        mWeekBar.setTextColor(weekTextColor);
    }


    public void setCalendarPadding(int mCalendarPadding) {
        if (mDelegate == null) {
            return;
        }
        mDelegate.setCalendarPadding(mCalendarPadding);
        update();
    }


    public void setCalendarPaddingLeft(int mCalendarPaddingLeft) {
        if (mDelegate == null) {
            return;
        }
        mDelegate.setCalendarPaddingLeft(mCalendarPaddingLeft);
        update();
    }

    public void setCalendarPaddingRight(int mCalendarPaddingRight) {
        if (mDelegate == null) {
            return;
        }
        mDelegate.setCalendarPaddingRight(mCalendarPaddingRight);
        update();
    }


    /**
     * 默认选择模式
     */
    public final void setSelectDefaultMode() {
        if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_DEFAULT) {
            return;
        }
        mDelegate.mSelectedCalendar = mDelegate.mIndexCalendar;
        mDelegate.setSelectMode(CalendarViewDelegate.SELECT_MODE_DEFAULT);
        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, mDelegate.getWeekStart(), false);
        mMonthPager.updateDefaultSelect();
        mWeekPager.updateDefaultSelect();

    }

    /**
     * 单选模式
     */
    public void setSelectSingleMode() {
        if (mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_SINGLE) {
            return;
        }
        mDelegate.setSelectMode(CalendarViewDelegate.SELECT_MODE_SINGLE);
        mWeekPager.updateSelected();
        mMonthPager.updateSelected();
    }

    /**
     * 设置星期日周起始
     */
    public void setWeekStarWithSun() {
        setWeekStart(CalendarViewDelegate.WEEK_START_WITH_SUN);
    }

    /**
     * 设置星期一周起始
     */
    public void setWeekStarWithMon() {
        setWeekStart(CalendarViewDelegate.WEEK_START_WITH_MON);
    }

    /**
     * 设置星期六周起始
     */
    public void setWeekStarWithSat() {
        setWeekStart(CalendarViewDelegate.WEEK_START_WITH_SAT);
    }

    /**
     * 设置周起始
     * CalendarViewDelegate.WEEK_START_WITH_SUN
     * CalendarViewDelegate.WEEK_START_WITH_MON
     * CalendarViewDelegate.WEEK_START_WITH_SAT
     *
     * @param weekStart 周起始
     */
    private void setWeekStart(int weekStart) {
        if (weekStart != CalendarViewDelegate.WEEK_START_WITH_SUN && weekStart != CalendarViewDelegate.WEEK_START_WITH_MON && weekStart != CalendarViewDelegate.WEEK_START_WITH_SAT) return;
        if (weekStart == mDelegate.getWeekStart()) return;
        mDelegate.setWeekStart(weekStart);
        mWeekBar.onWeekStartChange(weekStart);
        mWeekBar.onDateSelected(mDelegate.mSelectedCalendar, weekStart, false);
        mWeekPager.updateWeekStart();
        mMonthPager.updateWeekStart();
        mYearViewPager.updateWeekStart();
    }

    /**
     * 是否是单选模式
     *
     * @return isSingleSelectMode
     */
    public boolean isSingleSelectMode() {
        return mDelegate.getSelectMode() == CalendarViewDelegate.SELECT_MODE_SINGLE;
    }

    /**
     * 设置显示模式为全部
     */
    public void setAllMode() {
        setShowMode(CalendarViewDelegate.MODE_ALL_MONTH);
    }

    /**
     * 设置显示模式为仅当前月份
     */
    public void setOnlyCurrentMode() {
        setShowMode(CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH);
    }

    /**
     * 设置显示模式为填充
     */
    public void setFixMode() {
        setShowMode(CalendarViewDelegate.MODE_FIT_MONTH);
    }

    /**
     * 设置显示模式
     * CalendarViewDelegate.MODE_ALL_MONTH
     * CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH
     * CalendarViewDelegate.MODE_FIT_MONTH
     *
     * @param mode 月视图显示模式
     */
    private void setShowMode(int mode) {
        if (mode != CalendarViewDelegate.MODE_ALL_MONTH && mode != CalendarViewDelegate.MODE_ONLY_CURRENT_MONTH && mode != CalendarViewDelegate.MODE_FIT_MONTH) return;
        if (mDelegate.getMonthViewShowMode() == mode) return;
        mDelegate.setMonthViewShowMode(mode);
        mWeekPager.updateShowMode();
        mMonthPager.updateShowMode();
        mWeekPager.notifyDataSetChanged();
    }

    /**
     * 更新界面，
     * 重新设置颜色等都需要调用该方法
     */
    public final void update() {
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());
        mYearViewPager.update();
        mMonthPager.updateSchedule();
        mWeekPager.updateSchedule();
    }

    /**
     * 更新周视图
     */
    public void updateWeekBar() {
        mWeekBar.onWeekStartChange(mDelegate.getWeekStart());
    }


    /**
     * 更新当前日期
     */
    public final void updateCurrentDate() {
        if (mDelegate == null || mMonthPager == null || mWeekPager == null) {
            return;
        }
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        if (getCurDay() == day) {
            return;
        }
        mDelegate.updateCurrentDay();
        mMonthPager.updateCurrentDate();
        mWeekPager.updateCurrentDate();
    }

    /**
     * 获取当前周数据
     *
     * @return 获取当前周数据
     */
    public List<Calendar> getCurrentWeekCalendars() {
        return mWeekPager.getCurrentWeekCalendars();
    }


    /**
     * 获取当前月份日期
     *
     * @return return
     */
    public List<Calendar> getCurrentMonthCalendars() {
        return mMonthPager.getCurrentMonthCalendars();
    }

    /**
     * 获取选择的日期
     *
     * @return 获取选择的日期
     */
    public Calendar getSelectedCalendar() {
        return mDelegate.mSelectedCalendar;
    }

    /**
     * 获得最小范围日期
     *
     * @return 最小范围日期
     */
    public Calendar getMinRangeCalendar() {
        return mDelegate.getMinRangeCalendar();
    }


    /**
     * 获得最大范围日期
     *
     * @return 最大范围日期
     */
    public Calendar getMaxRangeCalendar() {
        return mDelegate.getMaxRangeCalendar();
    }

    /**
     * MonthViewPager
     *
     * @return 获得月视图
     */
    public MonthViewPager getMonthViewPager() {
        return mMonthPager;
    }

    /**
     * 获得周视图
     *
     * @return 获得周视图
     */
    public WeekViewPager getWeekViewPager() {
        return mWeekPager;
    }

    /**
     * 是否在日期范围内
     *
     * @param calendar calendar
     * @return 是否在日期范围内
     */
    protected final boolean isInRange(Calendar calendar) {
        return mDelegate != null && CalendarUtil.isCalendarInRange(calendar, mDelegate);
    }


    /**
     * 年份视图切换事件，快速年份切换
     */
    public interface OnYearChangeListener {
        void onYearChange(int year);
    }

    /**
     * 月份切换事件
     */
    public interface OnMonthChangeListener {
        void onMonthChange(int year, int month);
    }


    /**
     * 周视图切换事件
     */
    public interface OnWeekChangeListener {
        void onWeekChange(List<Calendar> weekCalendars);
    }

    /**
     * 内部日期选择，不暴露外部使用
     * 主要是用于更新日历CalendarLayout位置
     */
    interface OnInnerDateSelectedListener {
        /**
         * 月视图点击
         *
         * @param calendar calendar
         * @param isClick  是否是点击
         */
        void onMonthDateSelected(Calendar calendar, boolean isClick);

        /**
         * 周视图点击
         *
         * @param calendar calendar
         * @param isClick  是否是点击
         */
        void onWeekDateSelected(Calendar calendar, boolean isClick);
    }


    /**
     * 日历范围选择事件
     */
    public interface OnCalendarRangeSelectListener {

        /**
         * 范围选择超出范围越界
         *
         * @param calendar calendar
         */
        void onCalendarSelectOutOfRange(Calendar calendar);

        /**
         * 选择范围超出范围
         *
         * @param calendar        calendar
         * @param isOutOfMinRange 是否小于最小范围，否则为最大范围
         */
        void onSelectOutOfRange(Calendar calendar, boolean isOutOfMinRange);

        /**
         * 日期选择事件
         *
         * @param calendar calendar
         * @param isEnd    是否结束
         */
        void onCalendarRangeSelect(Calendar calendar, boolean isEnd);
    }


    /**
     * 日历多选事件
     */
    public interface OnCalendarMultiSelectListener {

        /**
         * 多选超出范围越界
         *
         * @param calendar calendar
         */
        void onCalendarMultiSelectOutOfRange(Calendar calendar);

        /**
         * 多选超出大小
         *
         * @param maxSize  最大大小
         * @param calendar calendar
         */
        void onMultiSelectOutOfSize(Calendar calendar, int maxSize);

        /**
         * 多选事件
         *
         * @param calendar calendar
         * @param curSize  curSize
         * @param maxSize  maxSize
         */
        void onCalendarMultiSelect(Calendar calendar, int curSize, int maxSize);
    }

    /**
     * 日历选择事件
     */
    public interface OnCalendarSelectListener {

        /**
         * 超出范围越界
         *
         * @param calendar calendar
         */
        void onCalendarOutOfRange(Calendar calendar);

        /**
         * 日期选择事件
         *
         * @param calendar calendar
         * @param isClick  isClick
         */
        void onCalendarSelect(Calendar calendar, boolean isClick);
    }

    public interface OnCalendarLongClickListener {

        /**
         * 超出范围越界
         *
         * @param calendar calendar
         */
        void onCalendarLongClickOutOfRange(Calendar calendar);

        /**
         * 日期长按事件
         *
         * @param calendar calendar
         */
        void onCalendarLongClick(Calendar calendar);
    }

    /**
     * 视图改变事件
     */
    public interface OnViewChangeListener {
        /**
         * 视图改变事件
         *
         * @param isMonthView isMonthView是否是月视图
         */
        void onViewChange(boolean isMonthView);
    }

    /**
     * 年视图改变事件
     */
    public interface OnYearViewChangeListener {
        /**
         * 年视图变化
         *
         * @param isClose 是否关闭
         */
        void onYearViewChange(boolean isClose);
    }

    /**
     * 拦截日期是否可用事件
     */
    public interface OnCalendarInterceptListener {
        boolean onCalendarIntercept(Calendar calendar);

        void onCalendarInterceptClick(Calendar calendar, boolean isClick);
    }

    /**
     * 点击Padding位置事件
     */
    public interface OnClickCalendarPaddingListener {
        /**
         * 点击Padding位置的事件
         *
         * @param x                x坐标
         * @param y                y坐标
         * @param isMonthView      是否是月视图，不是则为周视图
         * @param adjacentCalendar 相邻的日历日期
         * @param obj              此处的对象，自行设置
         */
        void onClickCalendarPadding(float x, float y, boolean isMonthView, Calendar adjacentCalendar, Object obj);
    }
}
