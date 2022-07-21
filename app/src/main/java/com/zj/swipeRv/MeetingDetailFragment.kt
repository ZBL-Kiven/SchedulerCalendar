package com.zj.swipeRv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.zj.cf.annotations.Constrain
import com.zj.cf.fragments.ConstrainFragment
import com.zj.loading.DisplayMode
import com.zj.loading.ZRotateLoadingView
import com.zj.swipeRv.cv.ScheduleHeaderView
import com.zj.swipeRv.cv.ScheduleNavBar
import com.zj.swipeRv.cv.i.MeetingItemIn
import com.zj.swipeRv.data.api.ScheduleApi
import com.zj.swipeRv.test.TestMember
import com.zj.views.list.adapters.BaseAdapterDataSet
import com.zj.views.list.holders.BaseViewHolder
import com.zj.views.list.views.EmptyRecyclerView
import kotlinx.coroutines.launch


@Constrain(id = "MeetingDetailFragment", backMode = 1)
class MeetingDetailFragment : ConstrainFragment() {

    private var headerView: ScheduleHeaderView? = null
    private var navView: ScheduleNavBar? = null
    private var rvFiles: EmptyRecyclerView<Int>? = null
    private var vBack: View? = null
    private var originData: MeetingItemIn? = null
    private var blv: ZRotateLoadingView? = null

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.calendar_schedule_detail_layout, container, false)
    }

    override fun onPostValue(bundle: Bundle?) {
        super.onPostValue(bundle)
        originData = bundle?.getSerializable("MeetingItem") as MeetingItemIn
    }

    override fun onCreate() {
        super.onCreate()
        initView()
        originData?.let {
            setData(it)
        }
    }

    private fun initView() {
        vBack = find(R.id.calendar_schedule_detail_iv_back)
        headerView = find(R.id.calendar_schedule_detail_header)
        rvFiles = find(R.id.calendar_schedule_detail_rv_files)
        navView = find(R.id.calendar_schedule_detail_nav)
        blv = find(R.id.calendar_schedule_detail_blv)
        vBack?.setOnClickListener {
            finish()
        }
        blv?.setOnTapListener {
            originData?.let {
                setData(it)
            }
        }
    }

    private fun setData(meetingItemIn: MeetingItemIn) {
        headerView?.setData(meetingItemIn)
        navView?.setData(meetingItemIn)
        if (!meetingItemIn.hasBeenRemoved()) {
            loadMembers(meetingItemIn.getMeetingId())
        }
    }

    private fun loadMembers(meetingId: String) {
        lifecycleScope.launch {
            val members = ScheduleApi.getMeetingMembers(meetingId)
            val files = ScheduleApi.getMeetingFiles(meetingId)
            if (members.error != null || files.error != null) {
                blv?.setMode(DisplayMode.NO_DATA)
                return@launch
            }
            headerView?.setMembers(members.data)
            val fs = files.data ?: return@launch
            headerView?.setFoldersCount(fs.size)
            if (fs.isNullOrEmpty().not()) {
//                rvFiles?.setData(R.layout.calendar_item_schedule, false, , object : BaseAdapterDataSet<Int>() {
//                    override fun initData(holder: BaseViewHolder<Int>?, position: Int, module: Int?) {
//                        holder?.getView<TextView>(R.id.calendar_item_tv_start_time)?.let {
//                            it.text = "$module"
//                        }
//                    }
//                })
            }
        }
    }
}