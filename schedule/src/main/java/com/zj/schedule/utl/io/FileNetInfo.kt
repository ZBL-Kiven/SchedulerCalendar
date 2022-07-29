package com.zj.schedule.utl.io

/**
 * @param type  0 上传  ，1 下载
 * */
data class FileNetInfo(val type: Int, var originalPath: String, var translationPath: String, val url: String?, var state: String = FileState.Waiting.name)