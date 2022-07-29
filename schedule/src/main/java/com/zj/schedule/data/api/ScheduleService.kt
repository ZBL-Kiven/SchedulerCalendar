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

    @HTTP(method = "DELETE", path = "/app/scene-support/meeting/delete-meeting-file", hasBody = true)
    fun deleteMeetingFile(@Body file: com.zj.schedule.data.api.Body.DeleteFiles): Observable<Any>
}