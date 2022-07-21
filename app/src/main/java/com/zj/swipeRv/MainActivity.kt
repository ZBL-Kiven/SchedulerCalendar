package com.zj.swipeRv

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zj.cf.managers.ConstrainFragmentManager


class MainActivity : AppCompatActivity() {

    private var fm: ConstrainFragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startFrg(view: View) {
        fm?.clearStack(false)
        val time = System.currentTimeMillis()
        fm = com.zj.schedule.CalendarFragment.start(this, findViewById(android.R.id.content), Config(), com.zj.schedule.utl.InitScheduleInfo(time, "1000001"))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fm?.getTopOfStack() != null) {
                fm?.finishTopFragment()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    class Config : com.zj.schedule.utl.Config {
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
    }
}