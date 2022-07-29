package com.zj.schedule.data.api

import com.zj.api.call.coroutine.SuspendObservable
import com.zj.api.mock.Mock
import com.zj.ok3.http.*
import com.zj.ok3.http.Body
import com.zj.schedule.data.entity.CalendarMeetingInfo
import com.zj.schedule.data.entity.MeetingMemberInfo
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.mok.FilesMock
import io.reactivex.Observable

internal interface ScheduleService {

    @GET("/app/scene-support/meeting/list")
    fun getMeetingList(@Query("startTimeMin") min: Long, @Query("startTimMax") max: Long): Observable<List<CalendarMeetingInfo>?>

    //@Mock(FilesMock::class)
    @GET("/app/scene-support/meeting/meeting-files")
    suspend fun getMeetingFiles(@Query("meetingId") meetId: String): SuspendObservable<List<ScheduleFileInfo>?>

    @GET("/app/scene-support/meeting/active-member")
    suspend fun getMeetingMembers(@Query("meetingId") meetingId: String): SuspendObservable<List<MeetingMemberInfo>?>

    @DELETE("/app/scene-support/meeting/delete-meeting-file/{meetingId}/{fileId}")
    fun deleteMeetingFile(@Path("meetingId") meetingId: Long, @Path("fileId") fileId: Long): Observable<Any>
}