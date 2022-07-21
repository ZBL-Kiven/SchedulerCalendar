package com.zj.schedule.data.api

import com.zj.api.call.coroutine.SuspendObservable
import com.zj.ok3.http.GET
import com.zj.ok3.http.Query
import com.zj.schedule.data.entity.CalendarMeetingInfo
import com.zj.schedule.data.entity.MeetingMemberInfo
import com.zj.schedule.data.entity.ScheduleFileInfo
import io.reactivex.Observable

internal interface ScheduleService {

    @GET("/app/scene-support/meeting/list")
    fun getMeetingList(@Query("startTimeMin") min: Long, @Query("startTimMax") max: Long): Observable<List<CalendarMeetingInfo>?>

    @GET("/app/scene-support/meeting/meeting-files")
    suspend fun getMeetingFiles(@Query("meetingId") meetId: String): SuspendObservable<List<ScheduleFileInfo>?>

    @GET("/app/scene-support/meeting/active-member")
    suspend fun getMeetingMembers(@Query("meetingId") meetingId: String): SuspendObservable<List<MeetingMemberInfo>?>
}