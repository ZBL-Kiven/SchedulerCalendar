package com.zj.schedule.data.entity

import java.io.Serializable

class ScheduleFileInfo : Serializable {

    var meetingId: Long = 0
    var url: String? = ""
    var name: String? = ""
    var uploaderId: Long = 0
    var uploaderName: String? = ""
    var size: Long = 0

    //0-正常 1-已经删除
    var status: Long = 0
    var id: Long = 0
    var createTime: Long = 0
    var updateTime: Long = 0

}