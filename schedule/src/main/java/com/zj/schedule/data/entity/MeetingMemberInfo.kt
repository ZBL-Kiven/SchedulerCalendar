package com.zj.schedule.data.entity

import com.zj.schedule.cv.i.MeetingMemberIn

class MeetingMemberInfo : MeetingMemberIn {

    var userId: Long = -1

    var nickname: String? = null

    var role: Int = -1

    var online: Boolean = false

    var avatar: String? = null

    //"UN_KNOW","MALE","FEMALE","OTHER"
    var sex: String? = null

    var presence: Boolean = false

    var anonymous: Boolean = false

    var platformOnline: Boolean = false

    enum class UserSex {
        UN_KNOW, MALE, FEMALE, OTHER
    }

    companion object {
        fun MeetingMemberInfo.getSex(): UserSex {
            return UserSex.valueOf(sex ?: return UserSex.UN_KNOW)
        }
    }

    override fun getHeadPic(): String {
        return avatar ?: ""
    }

    override fun getId(): Long {
        return userId
    }
}