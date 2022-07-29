package com.zj.schedule.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zj.cf.annotations.Constrain
import com.zj.cf.fragments.ConstrainFragment
import com.zj.schedule.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zj.schedule.utl.io.FileNetInfo
import com.zj.schedule.utl.sp.FileSPHelper

@Constrain(id = "FileUploadingFragment", backMode = 1)
class FileUploadingFragment : ConstrainFragment() {

    private var uploaded: MutableList<FileNetInfo>? = null

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.files_uploading_fragment_layout, container, false)
    }

    override fun onPostValue(bundle: Bundle?) {
        super.onPostValue(bundle)
        val data = bundle?.getString("lst")
        uploaded = Gson().fromJson<MutableList<FileNetInfo>>(data, TypeToken.getArray(FileNetInfo::class.java).type)
    }

    override fun onCreate() {
        super.onCreate()
        if (uploaded == null) finish()
        val uploading = FileSPHelper.getAllFiles()

    }


}