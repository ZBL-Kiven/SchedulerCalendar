package com.zj.schedule.data.entity

import com.zj.schedule.cv.i.ScheduleItemIn
import com.zj.schedule.cv.i.Status
import com.zj.schedule.utl.Utl
import kotlin.math.abs

@Suppress("MemberVisibilityCanBePrivate", "unused")
class CalendarMeetingInfo : ScheduleItemIn {

    var id: Int = 0

    // 会议状态 0-未开始,1-进行中,2-已结束,3已取消
    var status: Int = 0

    // 会议CODE
    var meetingCode: String = ""

    // 创建者id
    var ownerId: Long = 0

    // 创建者名
    var ownerName: String = ""

    // 创建者形象
    var ownerAvatar: String = ""

    // 主题
    var name: String = ""

    // 开始时间 时间搓(1646992448000)
    var startTime: Long = 0

    // 结束时间 时间搓(1646992448000)
    var endTime: Long = 0

    // 有无秘钥
    var needSecret: Boolean = false

    // 秘钥
    var secret: String = ""

    // 提醒时间 时间搓(1646992448000)
    var remindTime: Long = 0

    // 实际开始时间 （时间戳)
    var startAt: Long = 0

    // 实际结束时间 （时间戳)
    var endAt: Long = 0

    // 群员入会时关闭摄像头
    var closeCameraWhenJoin: Boolean = false

    // 群员入会时关闭麦克风
    var screenShareSetting: Int = 0

    // 屏幕共享设置  0-允许全员共享屏幕  1-仅允许主持人共享屏幕
    var closeMicWhenJoin: Boolean = false

    var roomId: Long = 0

    // 是否有会议文件
    var hasMeetingFile: Boolean = false

    // 主持人用户id，可能为空
    var hostUserId: Long? = 0

    // 主持人用户名，可能为空
    var hostUserName: String? = ""

    var kicked: Boolean = false

    var originData: String? = null

    override fun getScheduleName(): String {
        return getMeetingName()
    }

    override fun getMeetingStartTime(): Long {
        return startTime
    }

    override fun getMeetingEndTime(): Long {
        return endTime
    }

    override fun getDuration(): Long {
        if (endAt <= 0) return 0
        return abs(endAt - startAt)
    }

    override fun getStatus(): Status {
        return Status.getWithId(status)
    }

    override fun getMeetingName(): String {
        return name
    }

    override fun selfIsOwner(): Boolean {
        return ownerId == Utl.config?.getUserId()
    }

    override fun hasBeenRemoved(): Boolean {
        return kicked
    }

    override fun hasFiles(): Boolean {
        return hasMeetingFile
    }

    override fun getMeetingId(): String {
        return "$id"
    }

    override fun getMeetingKey(): String {
        return meetingCode
    }

    override fun getMeetingSecret(): String {
        return secret
    }

    override fun getMeetingOwnerName(): String {
        return ownerName
    }

    override fun getHostName(): String {
        return hostUserName ?: ""
    }

    override fun getMeetingOwnerId(): Long {
        return ownerId
    }

    override fun getHostId(): Long {
        return hostUserId ?: -1
    }
}