package com.zj.calendar;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * 基本的日历View，派生出MonthView 和 WeekView
 */
@SuppressWarnings("unused")
public abstract class BaseView extends View implements View.OnClickListener, View.OnLongClickListener {

    CalendarViewDelegate mDelegate;

    /**
     * 当前月份日期的笔
     */
    protected Paint mCurMonthTextPaint = new Paint();

    /**
     * 其它月份日期颜色
     */
    protected Paint mOtherMonthTextPaint = new Paint();

    /**
     * 标记的日期背景颜色画笔
     */
    protected Paint mSchedulePaint = new Paint();

    /**
     * 被选择的日期背景色
     */
    protected Paint mSelectedPaint = new Paint();

    /**
     * 标记的文本画笔
     */
    protected Paint mScheduleTextPaint = new Paint();

    /**
     * 选中的文本画笔
     */
    protected Paint mSelectTextPaint = new Paint();

    /**
     * 当前日期文本颜色画笔
     */
    protected Paint mCurDayTextPaint = new Paint();

    /**
     * 日历布局，需要在日历下方放自己的布局
     */
    CalendarLayout mParentLayout;

    /**
     * 日历项
     */
    protected List<Calendar> mItems;

    /**
     * 每一项的高度
     */
    protected int mItemHeight;

    /**
     * 每一项的宽度
     */
    protected int mItemWidth;

    /**
     * Text的基线
     */
    protected float mTextBaseLine;

    /**
     * 点击的x、y坐标
     */
    protected float mX, mY;

    /**
     * 是否点击
     */
    boolean isClick = true;

    /**
     * 字体大小
     */
    static final int TEXT_SIZE = 14;

    /**
     * 当前点击项
     */
    int mCurrentItem = -1;

    /**
     * 周起始
     */
    int mWeekStartWidth;

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    /**
     * 初始化配置
     *
     * @param context context
     */
    private void initPaint(Context context) {
        mCurMonthTextPaint.setAntiAlias(true);
        mCurMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurMonthTextPaint.setColor(0xFF111111);
        mCurMonthTextPaint.setFakeBoldText(true);
        mCurMonthTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        mOtherMonthTextPaint.setAntiAlias(true);
        mOtherMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mOtherMonthTextPaint.setColor(0xFFe1e1e1);
        mOtherMonthTextPaint.setFakeBoldText(true);
        mOtherMonthTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));
        mScheduleTextPaint.setAntiAlias(true);
        mScheduleTextPaint.setStyle(Paint.Style.FILL);
        mScheduleTextPaint.setTextAlign(Paint.Align.CENTER);
        mScheduleTextPaint.setColor(0xffed5353);
        mScheduleTextPaint.setFakeBoldText(true);
        mScheduleTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        mSelectTextPaint.setAntiAlias(true);
        mSelectTextPaint.setStyle(Paint.Style.FILL);
        mSelectTextPaint.setTextAlign(Paint.Align.CENTER);
        mSelectTextPaint.setColor(0xffed5353);
        mSelectTextPaint.setFakeBoldText(true);
        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        mSchedulePaint.setAntiAlias(true);
        mSchedulePaint.setStyle(Paint.Style.FILL);
        mSchedulePaint.setStrokeWidth(2);
        mSchedulePaint.setColor(0xffefefef);

        mCurDayTextPaint.setAntiAlias(true);
        mCurDayTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurDayTextPaint.setColor(Color.RED);
        mCurDayTextPaint.setFakeBoldText(true);
        mCurDayTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setStrokeWidth(2);

        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    /**
     * 初始化所有UI配置
     *
     * @param delegate delegate
     */
    final void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        mWeekStartWidth = mDelegate.getWeekStart();
        updateStyle();
        updateItemHeight();

        initPaint();
    }


    final void updateStyle() {
        if (mDelegate == null) {
            return;
        }
        this.mCurDayTextPaint.setColor(mDelegate.getCurDayTextColor());
        this.mCurMonthTextPaint.setColor(mDelegate.getCurrentMonthTextColor());
        this.mOtherMonthTextPaint.setColor(mDelegate.getOtherMonthTextColor());
        this.mSelectTextPaint.setColor(mDelegate.getSelectedTextColor());
        this.mSchedulePaint.setColor(mDelegate.getScheduleThemeColor());
        this.mScheduleTextPaint.setColor(mDelegate.getScheduleTextColor());
        this.mCurMonthTextPaint.setTextSize(mDelegate.getDayTextSize());
        this.mOtherMonthTextPaint.setTextSize(mDelegate.getDayTextSize());
        this.mCurDayTextPaint.setTextSize(mDelegate.getDayTextSize());
        this.mScheduleTextPaint.setTextSize(mDelegate.getDayTextSize());
        this.mSelectTextPaint.setTextSize(mDelegate.getDayTextSize());
        this.mSelectedPaint.setStyle(Paint.Style.FILL);
        this.mSelectedPaint.setColor(mDelegate.getSelectedThemeColor());
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    void updateItemHeight() {
        this.mItemHeight = mDelegate.getCalendarItemHeight();
        Paint.FontMetrics metrics = mCurMonthTextPaint.getFontMetrics();
        mTextBaseLine = mItemHeight / 2 - metrics.descent + (metrics.bottom - metrics.top) / 2;
    }

    /**
     * 移除事件
     */
    final void clearSchedule() {
        for (Calendar a : mItems) {
            a.clearSchedule();
        }
    }

    /**
     * 添加事件标记，来自Map
     */
    final void addSchedulesFromMap() {
        if (mDelegate.mScheduleDatesMap == null || mDelegate.mScheduleDatesMap.size() == 0) {
            return;
        }
        for (Calendar a : mItems) {
            if (mDelegate.mScheduleDatesMap.containsKey(a.toString())) {
                Calendar d = mDelegate.mScheduleDatesMap.get(a.toString());
                if (d == null) {
                    continue;
                }
                a.setSchedule(d.getSchedule());
            } else {
                a.clearSchedule();
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                isClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float mDY;
                if (isClick) {
                    mDY = event.getY() - mY;
                    isClick = Math.abs(mDY) <= 50;
                }
                break;
            case MotionEvent.ACTION_UP:
                mX = event.getX();
                mY = event.getY();
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 开始绘制前的钩子，这里做一些初始化的操作，每次绘制只调用一次，性能高效
     * 没有需要可忽略不实现
     * 例如：
     * 1、需要绘制圆形标记事件背景，可以在这里计算半径
     * 2、绘制矩形选中效果，也可以在这里计算矩形宽和高
     */
    protected void onPreviewHook() {
    }

    /**
     * 是否是选中的
     *
     * @param calendar calendar
     * @return true or false
     */
    protected boolean isSelected(Calendar calendar) {
        return mItems != null && mItems.indexOf(calendar) == mCurrentItem;
    }

    /**
     * 更新事件
     */
    final void update() {
        if (mDelegate.mScheduleDatesMap == null || mDelegate.mScheduleDatesMap.size() == 0) {//清空操作
            clearSchedule();
            invalidate();
            return;
        }
        addSchedulesFromMap();
        invalidate();
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
     * 是否在日期范围内
     *
     * @param calendar calendar
     * @return 是否在日期范围内
     */
    protected final boolean isInRange(Calendar calendar) {
        return mDelegate != null && CalendarUtil.isCalendarInRange(calendar, mDelegate);
    }

    /**
     * 跟新当前日期
     */
    abstract void updateCurrentDate();

    /**
     * 销毁
     */
    protected abstract void onDestroy();

    protected int getWeekStartWith() {
        return mDelegate != null ? mDelegate.getWeekStart() : CalendarViewDelegate.WEEK_START_WITH_SUN;
    }


    protected int getCalendarPaddingLeft() {
        return mDelegate != null ? mDelegate.getCalendarPaddingLeft() : 0;
    }


    protected int getCalendarPaddingRight() {
        return mDelegate != null ? mDelegate.getCalendarPaddingRight() : 0;
    }


    /**
     * 初始化画笔相关
     */
    protected void initPaint() {

    }

    protected Paint getPaintWithCalendarState(Calendar calendar, boolean isSelected) {
        Paint paint;
        if (isSelected) {
            paint = new Paint(mSelectTextPaint);
        } else if (calendar.isCurrentDay()) {
            paint = new Paint(mCurDayTextPaint);
        } else {
            if (calendar.isCurrentMonth()) {
                paint = new Paint(mCurMonthTextPaint);
            } else {
                paint = new Paint(mOtherMonthTextPaint);
            }
        }
        return paint;
    }
}
