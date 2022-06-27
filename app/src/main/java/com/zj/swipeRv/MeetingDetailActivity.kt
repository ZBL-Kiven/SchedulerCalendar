package com.zj.swipeRv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.zj.swipeRv.cv.ScheduleHeaderView
import com.zj.swipeRv.cv.ScheduleNavBar
import com.zj.swipeRv.cv.i.MeetingItemIn
import com.zj.swipeRv.test.TestMember
import com.zj.views.list.adapters.BaseAdapterDataSet
import com.zj.views.list.holders.BaseViewHolder
import com.zj.views.list.views.EmptyRecyclerView


class MeetingDetailActivity : AppCompatActivity() {


    companion object {
        fun <T : MeetingItemIn> start(c: Context, info: T) {
            val intent = Intent(c, MeetingDetailActivity::class.java)
            intent.putExtra("MeetingItem", info)
            c.startActivity(intent)
        }
    }

    private var headerView: ScheduleHeaderView? = null
    private var navView: ScheduleNavBar? = null
    private var rvFiles: EmptyRecyclerView<Int>? = null
    private var vBack: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_schedule_detail_layout)
        initView()
        setData(intent.getSerializableExtra("MeetingItem") as MeetingItemIn)
    }

    private fun initView() {
        vBack = findViewById(R.id.calendar_schedule_detail_iv_back)
        headerView = findViewById(R.id.calendar_schedule_detail_header)
        rvFiles = findViewById(R.id.calendar_schedule_detail_rv_files)
        navView = findViewById(R.id.calendar_schedule_detail_nav)
        vBack?.setOnClickListener {
            finish()
        }
    }

    private fun setData(meetingItemIn: MeetingItemIn) {
        headerView?.setData(meetingItemIn)
        navView?.setData(meetingItemIn)
        if (meetingItemIn.hasFiles()) {
            rvFiles?.isVisible = true
            loadFilesData(meetingItemIn.getMeetingId())
        } else {
            rvFiles?.isVisible = false
        }
        loadMembers(meetingItemIn.getMeetingId())
    }

    private fun loadMembers(meetingId: String) {
        val lst = mutableListOf<TestMember>()
        repeat(9) {
            lst.add(TestMember())
        }
        headerView?.setMembers(lst)
    }

    private fun loadFilesData(meetingId: String) {
        headerView?.setFoldersCount(100)
        rvFiles?.setData(R.layout.calendar_item_schedule, false, (0..100).toList(), object : BaseAdapterDataSet<Int>() {
            override fun initData(holder: BaseViewHolder<Int>?, position: Int, module: Int?) {
                holder?.getView<TextView>(R.id.calendar_item_tv_start_time)?.let {
                    it.text = "$module"
                }
            }
        })
    }
}