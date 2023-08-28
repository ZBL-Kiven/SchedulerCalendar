package com.zj.schedule.files

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.zj.cf.annotations.Constrain
import com.zj.cf.fragments.ConstrainFragment
import com.zj.schedule.R

@Suppress("DEPRECATION")
@Constrain(id = "file_selector", backMode = 1)
class FileSelector : ConstrainFragment() {

    companion object {
        const val FILE_SELECTOR_CODE = 0x1212
    }

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.empty, container, false)
    }

    override fun onCreate() {
        super.onCreate()
        checkPermission()
    }

    private fun checkPermission() {
        val act = kotlin.runCatching {
            requireActivity()
        }.getOrNull() ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val r = act.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (r == PackageManager.PERMISSION_GRANTED) {
                openFileSelector()
            } else {
                ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0x112)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FILE_SELECTOR_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermission()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_SELECTOR_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri: Uri? = data?.data
                setResult(Bundle().apply {
                    this.putParcelable("uri", uri)
                })
            }
            finish()
        } else {
            checkPermission()
        }
    }
}