package com.zj.swipeRv

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zj.calendar.Calendar
import com.zj.calendar.CalendarLayout
import com.zj.calendar.CalendarView
import com.zj.calendar.WeekBar

class MainActivity : AppCompatActivity(), CalendarView.OnCalendarSelectListener, CalendarView.OnYearChangeListener {

    private var mTextMonthDay: TextView? = null
    private var mTextYear: TextView? = null
    private var mTextCurrentDay: TextView? = null
    private var mCalendarView: CalendarView? = null
    private var mRelativeTool: RelativeLayout? = null
    private var mYear = 0
    private var mCalendarLayout: CalendarLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        initView()
        initData()
    }

    @SuppressLint("SetTextI18n")
    fun initView() {
        mTextMonthDay = findViewById(R.id.tv_month_day)
        mTextYear = findViewById(R.id.tv_year)
        mRelativeTool = findViewById(R.id.rl_tool)
        mCalendarView = findViewById(R.id.calendarView)
        mTextCurrentDay = findViewById(R.id.tv_current_day)
        mTextMonthDay?.setOnClickListener(View.OnClickListener {
            if (mCalendarLayout?.isExpand == false) {
                mCalendarLayout?.expand()
                return@OnClickListener
            }
            mCalendarView?.showMonthOfYearLayout(mYear)
            mTextYear?.visibility = View.GONE
            mTextMonthDay?.text = mYear.toString()
        })
        findViewById<View>(R.id.fl_current)?.setOnClickListener {
            mCalendarView?.scrollToCurrent()
        }
        mCalendarLayout = findViewById(R.id.calendarLayout)
        mCalendarView?.setOnCalendarSelectListener(this)
        mCalendarView?.setOnYearChangeListener(this)
        mYear = mCalendarView?.curYear ?: 0
        mTextYear?.text = "$mYear"
        mTextMonthDay?.text = mCalendarView?.curMonth.toString() + "月" + mCalendarView?.curDay + "日"
        mTextCurrentDay?.text = "${mCalendarView?.curDay}"
    }

    private fun initData() {
        val year: Int = mCalendarView?.curYear ?: 0
        val month: Int = mCalendarView?.curMonth ?: 0
        val schedules = arrayListOf<Calendar>()
        schedules.add(getScheduleCalendar(year, month, 15, Color.CYAN))
        schedules.add(getScheduleCalendar(year, month, 21, Color.BLUE))
        mCalendarView?.setScheduleDate(schedules)
    }

    private fun getScheduleCalendar(year: Int, month: Int, day: Int, color: Int): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.newSchedule(color, "1")
        return calendar
    }

    override fun onYearChange(year: Int) {
        mTextMonthDay?.text = year.toString()
    }


    override fun onCalendarOutOfRange(calendar: Calendar?) {

    }

    @SuppressLint("SetTextI18n")
    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        mTextYear?.visibility = View.VISIBLE
        mTextMonthDay?.text = calendar.month.toString() + "月" + calendar.day + "日"
        mTextYear?.text = "${calendar.year}"
        mYear = calendar.year
    }
}