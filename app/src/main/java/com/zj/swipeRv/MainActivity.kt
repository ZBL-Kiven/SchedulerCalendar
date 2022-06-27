package com.zj.swipeRv

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zj.calendar.Calendar
import com.zj.calendar.CalendarLayout
import com.zj.calendar.CalendarView
import com.zj.swipeRv.cv.ScheduleStatusItemView
import com.zj.swipeRv.cv.i.ScheduleItemIn
import com.zj.swipeRv.test.TestScheduleData
import com.zj.swipeRv.utl.Utl
import com.zj.views.list.adapters.BaseAdapterDataSet
import com.zj.views.list.holders.BaseViewHolder
import com.zj.views.list.views.EmptyRecyclerView
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), CalendarView.OnCalendarSelectListener, CalendarView.OnYearChangeListener, CalendarView.OnViewChangeListener {

    private var mTextMonthDay: TextView? = null
    private var mTextYear: TextView? = null
    private var mCalendarView: CalendarView? = null
    private var mYear = 0
    private var mCalendarLayout: CalendarLayout? = null
    private var mIvBack: ImageView? = null
    private var mIvPre: View? = null
    private var mIvNext: View? = null
    private var mRvSchedule: EmptyRecyclerView<ScheduleItemIn>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_schedule_layout)
        initView()
        initData()
    }

    @SuppressLint("SetTextI18n")
    fun initView() {
        mTextMonthDay = findViewById(R.id.calendar_tv_month)
        mTextYear = findViewById(R.id.calendar_tv_year)
        mCalendarLayout = findViewById(R.id.calendar_calendar_layout)
        mCalendarView = findViewById(R.id.calendar_schedule_calendar_view)
        mIvBack = findViewById(R.id.calendar_iv_back)
        mIvPre = findViewById(R.id.calendar_iv_pre)
        mIvNext = findViewById(R.id.calendar_iv_next)
        mRvSchedule = findViewById(R.id.calendar_rv_schedule)

        mIvPre?.setOnClickListener { mCalendarView?.scrollToPre() }
        mIvNext?.setOnClickListener { mCalendarView?.scrollToNext() }
        mTextYear?.setOnClickListener {
            if (mCalendarLayout?.isExpand == false) {
                mCalendarLayout?.expand()
            } else {
                mCalendarView?.showMonthOfYearLayout(mYear)
            }
        }
        mCalendarView?.setOnCalendarSelectListener(this)
        mCalendarView?.setOnYearChangeListener(this)
        mCalendarView?.setOnViewChangeListener(this)
        mYear = mCalendarView?.curYear ?: 0
        mTextMonthDay?.text = mCalendarView?.monthName
        mTextYear?.text = "$mYear"
    }

    //test
    private fun initData() {
        val year: Int = mCalendarView?.curYear ?: 0
        val month: Int = mCalendarView?.curMonth ?: 0
        val schedules = arrayListOf<Calendar>()
        schedules.add(getScheduleCalendar(year, month, 15, Color.parseColor("#FFFFFF")))
        schedules.add(getScheduleCalendar(year, month, 21, Color.WHITE))
        mCalendarView?.setScheduleDate(schedules)
        initListener()
        val lst = mutableListOf<TestScheduleData>()
        repeat(12) {
            lst.add(TestScheduleData.getCurDayCalendarInfo(it))
        }
        setScheduleData(lst)
    }

    private fun initListener() {
        mIvBack?.setOnClickListener {
            finish()
        }
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

    override fun onCalendarOutOfRange(calendar: Calendar?) {}

    @SuppressLint("SetTextI18n")
    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        mTextYear?.visibility = View.VISIBLE
        mTextMonthDay?.text = calendar.getMonthName(this)
        mTextYear?.text = "${calendar.year}"
        mYear = calendar.year
    }

    override fun onViewChange(isMonthView: Boolean) {
        mRvSchedule?.isSelected = !isMonthView
    }

    private fun setScheduleData(data: List<ScheduleItemIn>) {
        mRvSchedule?.setData(R.layout.calendar_item_schedule, false, data, object : BaseAdapterDataSet<ScheduleItemIn>() {
            override fun initData(holder: BaseViewHolder<ScheduleItemIn>?, position: Int, module: ScheduleItemIn?) {
                holder?.getView<TextView>(R.id.calendar_item_tv_start_time)?.let {
                    val date = module?.getStartCalendar()?.time ?: return@let
                    it.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                }
                holder?.getView<TextView>(R.id.calendar_item_tv_end_time)?.let {
                    val startCalendar = module?.getStartCalendar()
                    val endCalendar = module?.getEndCalendar()
                    val y1 = startCalendar?.get(java.util.Calendar.YEAR)
                    val m1 = startCalendar?.get(java.util.Calendar.MONTH)
                    val day1 = startCalendar?.get(java.util.Calendar.DAY_OF_MONTH)
                    val y2 = endCalendar?.get(java.util.Calendar.YEAR)
                    val m2 = endCalendar?.get(java.util.Calendar.MONTH)
                    val day2 = endCalendar?.get(java.util.Calendar.DAY_OF_MONTH)
                    val sb = StringBuilder()
                    if (y1 != y2 || m1 != m2 || day1 != day2) {
                        sb.append("${Utl.getMonthString(m2)} $day2\n")
                    }
                    val date = endCalendar?.time ?: return@let
                    sb.append(SimpleDateFormat("HH:mm", Locale.getDefault()).format(date))
                    it.text = sb
                }
                holder?.getView<ScheduleStatusItemView<ScheduleItemIn>>(R.id.calendar_item_cl)?.setData(module)
            }

            override fun onItemClick(position: Int, v: View?, m: ScheduleItemIn?) {
                if (v == null || m == null) return
                MeetingDetailActivity.start(v.context, m)
            }
        })
    }
}