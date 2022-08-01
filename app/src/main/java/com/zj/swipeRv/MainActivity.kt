package com.zj.swipeRv

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.zj.schedule.CalendarFragment
import com.zj.schedule.CalendarFragment.Companion.SCHEDULE_ID
import com.zj.schedule.NavigationToConstrainFragment
import com.zj.schedule.cv.i.MeetingFuncIn
import com.zj.schedule.files.FileListFragment
import com.zj.schedule.utl.Config
import com.zj.schedule.utl.InitScheduleInfo

class MainActivity : AppCompatActivity(), MeetingFuncIn {

    private val config = object : Config {

        override fun getApiHost(): String {
            return "https://api.dev.utown.io:3080"
        }

        override fun getUserId(): Long {
            return 1000842
        }

        override fun getToken(): String {
            return "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NTc3NzAwNjcsImV4cCI6MTY2MDM2MjA2Nywiand0X3VzZXIiOnsiZ3VpZCI6IjYxMjc4YjUxOGQxZDQwODBiMWQ3YTY4NjZiYWI5NGQyIiwidXNlcklkIjoxMDAwODQyLCJpZGVudGlmaWVyIjoiamF5dEB0ZW1sLm5ldCIsIm5pY2tuYW1lIjoiSmF5VCIsImF2YXRhciI6Imh0dHBzOi8vY2RuLmRldi51dG93bi5pby9pLzIwMjIwNzExLzUvOC8zLzU4M2ViOTMzNmYwODQ5YzFiMzFjNGNiMWM4OWIxN2ZhLnBuZyIsImZhY2UiOiJodHRwczovL2Nkbi5kZXYudXRvd24uaW8vaS8yMDIyMDcxMS9lL2YvNC9lZjRhM2M5YzA4ZGI0MWU4OWZkNTE0YmZjYTgzODRjZS5wbmciLCJhbm9ueW1vdXMiOmZhbHNlfX0.MJ2afFRWoEGnEEKJQMRUbRTqt5q3ILvK2UX1MaGIlbU"
        }

        override fun getNickName(): String {
            return "666"
        }

        override fun getAvatar(): String {
            return "assa"
        }

        override fun getHeader(): Map<String, String> {
            return mutableMapOf<String, String>().apply {
                this["userId"] = "${getUserId()}"
                this["Authorization"] = "Bearer ${getToken()}"
                this["Content-Type"] = "application/json"
            }
        }

        override val meetingFuncIn: MeetingFuncIn; get() = this@MainActivity
    }

    private lateinit var c: NavigationToConstrainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        c = NavigationToConstrainFragment.create(config, this)
    }

    fun startCalendarFrg(view: View) {
        val scheduleInfo = InitScheduleInfo(System.currentTimeMillis(), "1041")
        val b = bundleOf(Pair(SCHEDULE_ID, scheduleInfo))
        c.start(this, findViewById(android.R.id.content), CalendarFragment::class.java, b) {
            true
        }
    }

    fun startFileListFrg(view: View) {
        val b = bundleOf(Pair("meetingId", 1041L))
        c.start(this, findViewById(android.R.id.content), FileListFragment::class.java, b) {
            true
        }
    }

    override fun joinMeeting(meetingId: Long) {
        Log.e("------- ", "joinMeeting")
    }

    override fun invite(meetingId: Long) {
        Log.e("------- ", "invite")
    }

    override fun start(meetingId: Long) {
        Log.e("------- ", "start")
    }

    override fun edit(meetingId: Long) {
        Log.e("------- ", "edit")
    }

    override fun cancel(meetingId: Long) {
        Log.e("------- ", "cancel")
    }
}