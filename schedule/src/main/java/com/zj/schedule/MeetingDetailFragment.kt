package com.zj.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.zj.cf.annotations.Constrain
import com.zj.cf.fragments.ConstrainFragment
import com.zj.loading.DisplayMode
import com.zj.loading.OverLapMode
import com.zj.loading.ZRotateLoadingView
import com.zj.schedule.cv.ScheduleHeaderView
import com.zj.schedule.cv.ScheduleNavBar
import com.zj.schedule.cv.i.MeetingItemIn
import com.zj.schedule.cv.pop.FilesMenuPop
import com.zj.schedule.data.api.ScheduleApi
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.files.FileListAdapter
import com.zj.schedule.files.FilePreviewFragment
import com.zj.views.list.listeners.ItemClickListener
import kotlinx.coroutines.launch


@Constrain(id = "MeetingDetailFragment", backMode = 1)
class MeetingDetailFragment : ConstrainFragment() {

    private var headerView: ScheduleHeaderView? = null
    private var navView: ScheduleNavBar? = null
    private var rvFiles: RecyclerView? = null
    private var vBack: View? = null
    private var originData: MeetingItemIn? = null
    private var blv: ZRotateLoadingView? = null
    private val fileAdapter = FileListAdapter()

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
        rvFiles?.adapter = fileAdapter
        vBack?.setOnClickListener {
            finish()
        }
        blv?.setOnTapListener {
            originData?.let {
                setData(it)
            }
        }
        fileAdapter.setOnItemClickListener(object : ItemClickListener<ScheduleFileInfo>() {

            override fun onItemClick(position: Int, v: View?, m: ScheduleFileInfo?) {
                val b = Bundle()
                b.putSerializable("fileInfo", m)
                startFragment(FilePreviewFragment::class.java, b)
            }

            override fun onItemLongClick(position: Int, v: View?, m: ScheduleFileInfo?): Boolean {
                v?.let {
                    FilesMenuPop.dismiss()
                    FilesMenuPop.show(v, m, position) { b, b1, p, _ ->
                        if (b) {
                            blv?.setMode(DisplayMode.LOADING, OverLapMode.FLOATING)
                        } else {
                            blv?.setMode(DisplayMode.NORMAL)
                            if (b1) {
                                fileAdapter.remove(p)
                                Toast.makeText(v.context, R.string.Delete_files_success, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(v.context, R.string.Delete_files_failed, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                return super.onItemLongClick(position, v, m)
            }
        })
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
                fileAdapter.change(fs)
            }
        }
    }
}