package com.zj.schedule.utl.io

import java.util.*

/**
 * @param type  0 上传  ，1 下载
 * */
internal data class FileNetInfo(val type: Int, var name: String, var originalPath: String, var localPath: String, var url: String?, var state: String = FileState.Waiting.name) {

    var progress: Int = 0

    var uploadId: String = UUID.randomUUID().toString()
}