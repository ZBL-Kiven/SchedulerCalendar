package com.zj.schedule.files

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zj.cf.annotations.Constrain
import com.zj.cf.fragments.ConstrainFragment
import com.zj.schedule.R

@Constrain(id = "file_selector", backMode = 1)
class FileSelector : ConstrainFragment() {

    companion object {
        const val FILE_SELECTOR_CODE = 0x1212
    }

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.empty, null)
    }

    override fun onCreate() {
        super.onCreate()
        openFileSelector()
    }

    @Suppress("DEPRECATION")
    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FILE_SELECTOR_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_SELECTOR_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri: Uri? = data?.data
                setResult(Bundle().apply {
                    this.putString("uri", uri?.path)
                })
            }
            finish()
        }
    }
}