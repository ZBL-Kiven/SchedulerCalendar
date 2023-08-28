package com.zj.calendar

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import com.zj.calendar.CalendarView.*
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
internal class CalendarViewDelegate constructor(context: Context, attrs: AttributeSet?) {

    companion object {

        //周起始：周日
        const val WEEK_START_WITH_SUN = 1

        //周起始：周一
        const val WEEK_START_WITH_MON = 2

        //周起始：周六
        const val WEEK_START_WITH_SAT = 7

        //默认选择日期1号first_day_of_month
        const val FIRST_DAY_OF_MONTH = 0

        //跟随上个月last_select_day
        const val LAST_MONTH_VIEW_SELECT_DAY = 1

        //跟随上个月last_select_day_ignore_current忽视今天
        const val LAST_MONTH_VIEW_SELECT_DAY_IGNORE_CURRENT = 2

        //全部显示
        const val MODE_ALL_MONTH = 0

        //仅显示当前月份
        const val MODE_ONLY_CURRENT_MONTH = 1

        //自适应显示，不会多出一行，但是会自动填充
        const val MODE_FIT_MONTH = 2

        //默认选择模式
        const val SELECT_MODE_DEFAULT = 0

        //单选模式
        const val SELECT_MODE_SINGLE = 1

        //支持转换的最小年份
        const val MIN_YEAR = 1900

        //支持转换的最大年份
        private const val MAX_YEAR = 2099
    }

    var defaultCalendarSelectDay: Int

    //周起始
    var weekStart: Int

    //月份显示模式
    var monthViewShowMode: Int

    //选择模式
    var selectMode: Int

    //各种字体颜色，看名字知道对应的地方
    var curDayTextColor: Int; private set
    var otherMonthDayTextColor: Int; private set
    var scheduleTextColor: Int; private set
    var otherMonthTextColor: Int; private set
    var currentMonthTextColor: Int; private set
    var selectedTextColor: Int; private set
    var selectedTodayTextColor: Int; private set
    val weekTextColor: Int
    var isPreventLongPressedSelected = false

    //年视图一些padding
    val yearViewPadding: Int
    var yearViewPaddingLeft: Int
    var yearViewPaddingRight: Int

    //年视图一些padding
    val yearViewMonthPaddingLeft: Int
    val yearViewMonthPaddingRight: Int
    val yearViewMonthPaddingTop: Int
    val yearViewMonthPaddingBottom: Int

    //日历内部左右padding
    private var mCalendarPadding: Int

    //日历内部左padding
    var calendarPaddingLeft: Int

    //日历内部右padding
    var calendarPaddingRight: Int

    //年视图字体大小
    val yearViewMonthTextSize: Int
    val yearViewDayTextSize: Int
    val yearViewWeekTextSize: Int

    //年视图月份高度和周的高度
    val yearViewMonthHeight: Int
    val yearViewWeekHeight: Int

    //年视图字体和标记颜色
    var yearViewMonthTextColor: Int; private set
    var yearViewCurrentMonthTextColor: Int; private set
    var yearViewDayTextColor: Int; private set
    var yearViewScheduleTextColor: Int; private set
    val yearViewSelectTextColor: Int
    val yearViewCurDayTextColor: Int
    val yearViewWeekTextColor: Int

    //星期栏的背景、线的背景、年份背景
    val weekLineBackground: Int
    val yearViewBackground: Int
    val weekBackground: Int

    //星期栏Line margin
    val weekLineMargin: Int

    //星期栏字体大小
    val weekTextSize: Int

    //标记的主题色和选中的主题色
    var scheduleThemeColor: Int; private set
    var selectedThemeColor: Int; private set
    var selectedTodayThemeColor: Int; private set

    //自定义的日历路径
    private val mMonthViewClassPath: String

    //月视图类
    var monthViewClass: Class<*>? = null

    //自定义周视图路径
    private val mWeekViewClassPath: String

    //周视图类
    var weekViewClass: Class<*>? = null

    //自定义年视图路径
    val yearViewClassPath: String

    //周视图类
    var yearViewClass: Class<*>? = null; private set

    //自定义周栏路径
    private val mWeekBarClassPath: String

    //自定义周栏
    var weekBarClass: Class<*>? = null

    //年月视图是否打开
    @JvmField var isShowYearSelectedLayout = false

    //最小年份和最大年份
    var minYear: Int; private set
    var maxYear: Int; private set

    //最小年份和最大年份对应最小月份和最大月份
    // when you want set 2015-07 to 2017-08
    var minYearMonth: Int; private set
    var maxYearMonth: Int; private set

    //最小年份和最大年份对应最小天和最大天数
    //when you want set like 2015-07-08 to 2017-08-30
    var minYearDay: Int; private set
    var maxYearDay: Int; private set

    //日期文本大小
    val dayTextSize: Int

    //日历卡的项高度
    var calendarItemHeight: Int

    //是否是全屏日历
    val isFullScreenCalendar: Boolean

    //星期栏的高度
    val weekBarHeight: Int

    //今天的日子
    lateinit var currentDay: Calendar; private set
    var isMonthViewScrollable: Boolean
    var isWeekViewScrollable: Boolean
    var isYearViewScrollable: Boolean

    //当前月份和周视图的item位置
    @JvmField var mCurrentMonthViewItem = 0

    //标记的日期,数量巨大，请使用这个
    @JvmField var mScheduleDatesMap: MutableMap<String, Calendar>? = null

    //点击Padding位置事件
    @JvmField var mClickCalendarPaddingListener: OnClickCalendarPaddingListener? = null

    //日期拦截事件
    @JvmField var mCalendarInterceptListener: OnCalendarInterceptListener? = null

    //日期选中监听
    @JvmField var mCalendarSelectListener: OnCalendarSelectListener? = null

    //范围选择
    @JvmField var mCalendarRangeSelectListener: OnCalendarRangeSelectListener? = null

    //多选选择事件
    @JvmField var mCalendarMultiSelectListener: OnCalendarMultiSelectListener? = null

    //外部日期长按事件
    @JvmField var mCalendarLongClickListener: OnCalendarLongClickListener? = null

    //内部日期切换监听，用于内部更新计算
    @JvmField var mInnerListener: OnInnerDateSelectedListener? = null

    //快速年份切换
    @JvmField var mYearChangeListener: OnYearChangeListener? = null

    //月份切换事件
    @JvmField var mMonthChangeListener: OnMonthChangeListener? = null

    //周视图改变事件
    @JvmField var mWeekChangeListener: OnWeekChangeListener? = null

    //视图改变事件
    @JvmField var mViewChangeListener: OnViewChangeListener? = null

    //年视图改变事件
    @JvmField var mYearViewChangeListener: OnYearViewChangeListener? = null

    //保存选中的日期
    @JvmField var mSelectedCalendar: Calendar? = null

    //保存标记位置
    @JvmField var mIndexCalendar: Calendar? = null

    var maxMultiSelectSize: Int
    var minSelectRange: Int; private set
    var maxSelectRange: Int; private set

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
        mCalendarPadding = array.getDimension(R.styleable.CalendarView_calendar_padding, 0f).toInt()
        calendarPaddingLeft = array.getDimension(R.styleable.CalendarView_calendar_padding_left, 0f).toInt()
        calendarPaddingRight = array.getDimension(R.styleable.CalendarView_calendar_padding_right, 0f).toInt()
        if (mCalendarPadding != 0) {
            calendarPaddingLeft = mCalendarPadding
            calendarPaddingRight = mCalendarPadding
        }
        scheduleTextColor = array.getColor(R.styleable.CalendarView_schedule_text_color, -0x1)
        scheduleThemeColor = array.getColor(R.styleable.CalendarView_schedule_theme_color, 0x50CFCFCF)
        mMonthViewClassPath = array.getString(R.styleable.CalendarView_month_view) ?: ""
        yearViewClassPath = array.getString(R.styleable.CalendarView_year_view) ?: ""
        mWeekViewClassPath = array.getString(R.styleable.CalendarView_week_view) ?: ""
        mWeekBarClassPath = array.getString(R.styleable.CalendarView_week_bar_view) ?: ""
        weekBarHeight = array.getDimension(R.styleable.CalendarView_week_bar_height, CalendarUtil.dipToPx(context, 40f).toFloat()).toInt()
        weekTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_week_text_size, CalendarUtil.dipToPx(context, 12f))
        weekLineMargin = array.getDimension(R.styleable.CalendarView_week_line_margin, CalendarUtil.dipToPx(context, 0f).toFloat()).toInt()
        isMonthViewScrollable = array.getBoolean(R.styleable.CalendarView_month_view_scrollable, true)
        isWeekViewScrollable = array.getBoolean(R.styleable.CalendarView_week_view_scrollable, true)
        isYearViewScrollable = array.getBoolean(R.styleable.CalendarView_year_view_scrollable, true)
        defaultCalendarSelectDay = array.getInt(R.styleable.CalendarView_month_view_auto_select_day, FIRST_DAY_OF_MONTH)
        monthViewShowMode = array.getInt(R.styleable.CalendarView_month_view_show_mode, MODE_ALL_MONTH)
        weekStart = array.getInt(R.styleable.CalendarView_week_start_with, WEEK_START_WITH_SUN)
        selectMode = array.getInt(R.styleable.CalendarView_select_mode, SELECT_MODE_DEFAULT)
        maxMultiSelectSize = array.getInt(R.styleable.CalendarView_max_multi_select_size, Int.MAX_VALUE)
        minSelectRange = array.getInt(R.styleable.CalendarView_min_select_range, -1)
        maxSelectRange = array.getInt(R.styleable.CalendarView_max_select_range, -1)
        setSelectRange(minSelectRange, maxSelectRange)
        weekBackground = array.getColor(R.styleable.CalendarView_week_background, Color.WHITE)
        weekLineBackground = array.getColor(R.styleable.CalendarView_week_line_background, Color.TRANSPARENT)
        yearViewBackground = array.getColor(R.styleable.CalendarView_year_view_background, Color.WHITE)
        weekTextColor = array.getColor(R.styleable.CalendarView_week_text_color, -0xcccccd)
        curDayTextColor = array.getColor(R.styleable.CalendarView_current_day_text_color, Color.RED)
        otherMonthDayTextColor = array.getColor(R.styleable.CalendarView_other_month_day_text_color, 0x50FFFFFF)
        selectedThemeColor = array.getColor(R.styleable.CalendarView_selected_theme_color, 0x50CFCFCF)
        selectedTodayTextColor = array.getColor(R.styleable.CalendarView_selected_today_text_color, 0x50CFCFCF)
        selectedTodayThemeColor = array.getColor(R.styleable.CalendarView_selected_today_theme_color, 0x50CFCFCF)
        selectedTextColor = array.getColor(R.styleable.CalendarView_selected_text_color, -0xeeeeef)
        currentMonthTextColor = array.getColor(R.styleable.CalendarView_current_month_text_color, -0xeeeeef)
        otherMonthTextColor = array.getColor(R.styleable.CalendarView_other_month_text_color, -0x1e1e1f)
        minYear = array.getInt(R.styleable.CalendarView_min_year, 1971)
        maxYear = array.getInt(R.styleable.CalendarView_max_year, 2055)
        minYearMonth = array.getInt(R.styleable.CalendarView_min_year_month, 1)
        maxYearMonth = array.getInt(R.styleable.CalendarView_max_year_month, 12)
        minYearDay = array.getInt(R.styleable.CalendarView_min_year_day, 1)
        maxYearDay = array.getInt(R.styleable.CalendarView_max_year_day, -1)
        dayTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_day_text_size, CalendarUtil.dipToPx(context, 16f))
        calendarItemHeight = array.getDimension(R.styleable.CalendarView_calendar_height, CalendarUtil.dipToPx(context, 56f).toFloat()).toInt()
        isFullScreenCalendar = array.getBoolean(R.styleable.CalendarView_calendar_match_parent, false)

        //年视图相关
        yearViewMonthTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_month_text_size, CalendarUtil.dipToPx(context, 18f))
        yearViewDayTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_day_text_size, CalendarUtil.dipToPx(context, 7f))
        yearViewMonthTextColor = array.getColor(R.styleable.CalendarView_year_view_month_text_color, -0xeeeeef)
        yearViewCurrentMonthTextColor = array.getColor(R.styleable.CalendarView_year_view_current_month_text_color, -0xeeeeef)
        yearViewDayTextColor = array.getColor(R.styleable.CalendarView_year_view_day_text_color, -0xeeeeef)
        yearViewScheduleTextColor = array.getColor(R.styleable.CalendarView_year_view_scheme_color, scheduleThemeColor)
        yearViewWeekTextColor = array.getColor(R.styleable.CalendarView_year_view_week_text_color, -0xcccccd)
        yearViewCurDayTextColor = array.getColor(R.styleable.CalendarView_year_view_current_day_text_color, curDayTextColor)
        yearViewSelectTextColor = array.getColor(R.styleable.CalendarView_year_view_select_text_color, -0xcccccd)
        yearViewWeekTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_week_text_size, CalendarUtil.dipToPx(context, 8f))
        yearViewMonthHeight = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_month_height, CalendarUtil.dipToPx(context, 32f))
        yearViewWeekHeight = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_week_height, CalendarUtil.dipToPx(context, 0f))
        yearViewPadding = array.getDimension(R.styleable.CalendarView_year_view_padding, CalendarUtil.dipToPx(context, 12f).toFloat()).toInt()
        yearViewPaddingLeft = array.getDimension(R.styleable.CalendarView_year_view_padding_left, CalendarUtil.dipToPx(context, 12f).toFloat()).toInt()
        yearViewPaddingRight = array.getDimension(R.styleable.CalendarView_year_view_padding_right, CalendarUtil.dipToPx(context, 12f).toFloat()).toInt()
        if (yearViewPadding != 0) {
            yearViewPaddingLeft = yearViewPadding
            yearViewPaddingRight = yearViewPadding
        }
        yearViewMonthPaddingTop = array.getDimension(R.styleable.CalendarView_year_view_month_padding_top, CalendarUtil.dipToPx(context, 4f).toFloat()).toInt()
        yearViewMonthPaddingBottom = array.getDimension(R.styleable.CalendarView_year_view_month_padding_bottom, CalendarUtil.dipToPx(context, 4f).toFloat()).toInt()
        yearViewMonthPaddingLeft = array.getDimension(R.styleable.CalendarView_year_view_month_padding_left, CalendarUtil.dipToPx(context, 4f).toFloat()).toInt()
        yearViewMonthPaddingRight = array.getDimension(R.styleable.CalendarView_year_view_month_padding_right, CalendarUtil.dipToPx(context, 4f).toFloat()).toInt()
        if (minYear <= MIN_YEAR) minYear = MIN_YEAR
        if (maxYear >= MAX_YEAR) maxYear = MAX_YEAR
        array.recycle()
        init()
    }

    private fun init() {
        currentDay = Calendar()
        val d = Date()
        currentDay.year = CalendarUtil.getDate("yyyy", d)
        currentDay.month = CalendarUtil.getDate("MM", d)
        currentDay.day = CalendarUtil.getDate("dd", d)
        currentDay.isCurrentDay = true
        setRange(minYear, minYearMonth, maxYear, maxYearMonth)
        try {
            weekBarClass = if (TextUtils.isEmpty(mWeekBarClassPath)) EnglishWeekBar::class.java.also { weekBarClass = it } else Class.forName(mWeekBarClassPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            yearViewClass = if (TextUtils.isEmpty(yearViewClassPath)) DefaultYearView::class.java.also { yearViewClass = it } else Class.forName(yearViewClassPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            monthViewClass = if (TextUtils.isEmpty(mMonthViewClassPath)) DefaultMonthView::class.java else Class.forName(mMonthViewClassPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            weekViewClass = if (TextUtils.isEmpty(mWeekViewClassPath)) DefaultWeekView::class.java else Class.forName(mWeekViewClassPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setRange(minYear: Int, minYearMonth: Int, maxYear: Int, maxYearMonth: Int) {
        this.minYear = minYear
        this.minYearMonth = minYearMonth
        this.maxYear = maxYear
        this.maxYearMonth = maxYearMonth
        if (this.maxYear < currentDay.year) {
            this.maxYear = currentDay.year
        }
        if (maxYearDay == -1) {
            maxYearDay = CalendarUtil.getMonthDaysCount(this.maxYear, this.maxYearMonth)
        }
        val y = currentDay.year - this.minYear
        mCurrentMonthViewItem = 12 * y + currentDay.month - this.minYearMonth
    }

    fun setRange(minYear: Int, minYearMonth: Int, minYearDay: Int, maxYear: Int, maxYearMonth: Int, maxYearDay: Int) {
        this.minYear = minYear
        this.minYearMonth = minYearMonth
        this.minYearDay = minYearDay
        this.maxYear = maxYear
        this.maxYearMonth = maxYearMonth
        this.maxYearDay = maxYearDay
        if (this.maxYearDay == -1) {
            this.maxYearDay = CalendarUtil.getMonthDaysCount(this.maxYear, this.maxYearMonth)
        }
        val y = currentDay.year - this.minYear
        mCurrentMonthViewItem = 12 * y + currentDay.month - this.minYearMonth
    }

    fun setTextColor(curDayTextColor: Int, curMonthTextColor: Int, otherMonthTextColor: Int) {
        this.curDayTextColor = curDayTextColor
        this.otherMonthTextColor = otherMonthTextColor
        currentMonthTextColor = curMonthTextColor
    }

    fun setScheduleColor(schemeColor: Int, schemeTextColor: Int) {
        scheduleThemeColor = schemeColor
        scheduleTextColor = schemeTextColor
    }

    fun setYearViewTextColor(yearViewMonthTextColor: Int, yearViewCurrentMonthTextColor: Int, yearViewDayTextColor: Int, yarViewScheduleTextColor: Int) {
        this.yearViewMonthTextColor = yearViewMonthTextColor
        this.yearViewCurrentMonthTextColor = yearViewCurrentMonthTextColor
        this.yearViewDayTextColor = yearViewDayTextColor
        yearViewScheduleTextColor = yarViewScheduleTextColor
    }

    fun setSelectColor(selectedColor: Int, selectedTextColor: Int, selectedTodayTextColor: Int, selectedTodayThemeColor: Int) {
        this.selectedThemeColor = selectedColor
        this.selectedTextColor = selectedTextColor
        this.selectedTodayTextColor = selectedTodayTextColor
        this.selectedTodayThemeColor = selectedTodayThemeColor
    }

    fun setThemeColor(selectedThemeColor: Int, schemeColor: Int) {
        this.selectedThemeColor = selectedThemeColor
        scheduleThemeColor = schemeColor
    }

    fun setSelectRange(minRange: Int, maxRange: Int) {
        if (maxRange in 1 until minRange) {
            maxSelectRange = minRange
            minSelectRange = minRange
            return
        }
        minSelectRange = if (minRange <= 0) -1 else minRange
        maxSelectRange = if (maxRange <= 0) -1 else maxRange
    }

    fun updateCurrentDay() {
        val d = Date()
        currentDay.year = CalendarUtil.getDate("yyyy", d)
        currentDay.month = CalendarUtil.getDate("MM", d)
        currentDay.day = CalendarUtil.getDate("dd", d)
    }

    var calendarPadding: Int
        get() = mCalendarPadding
        set(mCalendarPadding) {
            this.mCalendarPadding = mCalendarPadding
            calendarPaddingLeft = mCalendarPadding
            calendarPaddingRight = mCalendarPadding
        }

    fun clearSelectedSchedule() {
        mSelectedCalendar?.clearSchedule()
    }

    fun updateSelectCalendarSchedule() {
        if (!mScheduleDatesMap.isNullOrEmpty()) {
            val key = mSelectedCalendar.toString()
            if (mScheduleDatesMap?.containsKey(key) == true) {
                val d = mScheduleDatesMap?.get(key)
                mSelectedCalendar?.schedule = d?.schedule
            }
        } else {
            clearSelectedSchedule()
        }
    }

    fun createCurrentDate(): Calendar {
        val calendar = Calendar()
        calendar.year = currentDay.year
        calendar.week = currentDay.week
        calendar.month = currentDay.month
        calendar.day = currentDay.day
        calendar.isCurrentDay = true
        return calendar
    }

    val minRangeCalendar: Calendar
        get() {
            val calendar = Calendar()
            calendar.year = minYear
            calendar.month = minYearMonth
            calendar.day = minYearDay
            calendar.isCurrentDay = calendar == currentDay
            return calendar
        }
    val maxRangeCalendar: Calendar
        get() {
            val calendar = Calendar()
            calendar.year = maxYear
            calendar.month = maxYearMonth
            calendar.day = maxYearDay
            calendar.isCurrentDay = calendar == currentDay
            return calendar
        }

    //添加事件标记，来自Map
    fun addSchedulesFromMap(mItems: List<Calendar>) {
        if (mScheduleDatesMap.isNullOrEmpty()) {
            return
        }
        for (a in mItems) {
            if (mScheduleDatesMap?.containsKey(a.toString()) == true) {
                val d = mScheduleDatesMap?.get(a.toString()) ?: continue
                a.schedule = d.schedule
            } else {
                a.clearSchedule()
            }
        }
    }

    //添加数据
    fun addSchedules(mScheduleDates: Map<String, Calendar?>?) {
        if (mScheduleDates == null || mScheduleDates.isEmpty()) {
            return
        }
        if (mScheduleDatesMap == null) {
            mScheduleDatesMap = HashMap()
        }
        for (key in mScheduleDates.keys) {
            mScheduleDatesMap?.remove(key)
            val calendar = mScheduleDates[key] ?: continue
            mScheduleDatesMap?.put(key, calendar)
        }
    }
}