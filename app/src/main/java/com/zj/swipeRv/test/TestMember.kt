package com.zj.swipeRv.test

import com.zj.swipeRv.cv.i.MeetingMemberIn

class TestMember : MeetingMemberIn {

    override fun getAvatar(): String {
        return "https://img1.baidu.com/it/u=4263666581,1644404887&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=501"
    }

    override fun getId(): Long {
        return ids
    }

    companion object {
        var ids: Long = 1; get() = ++field
    }
}