package com.zj.schedule.files.l

import com.zj.api.exception.ApiException
import com.zj.schedule.data.entity.ScheduleFileInfo
import com.zj.schedule.utl.io.FileNetInfo

internal interface UploadListener {

    fun onStart(uploadId: String, fInfo: FileNetInfo) {}

    fun onCompleted(uploadId: String, totalBytes: Long) {}

    fun onError(uploadId: String, exception: ApiException?, errorBody: Any?) {}

    fun onProgress(uploadId: String, progress: Int, contentLength: Long) {}

    fun onSuccess(uploadId: String, body: ScheduleFileInfo?, totalBytes: Long) {}

}
