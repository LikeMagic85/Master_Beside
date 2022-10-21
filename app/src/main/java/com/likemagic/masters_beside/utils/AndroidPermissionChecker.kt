package com.likemagic.masters_beside.utils

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class AndroidPermissionChecker(private val activity: FragmentActivity) : PermissionManager {


    override fun checkPermissions(){
        setupPermissions()
    }

    private fun setupPermissions() {

        val permission = ContextCompat.checkSelfPermission(activity,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_REQUEST_CODE)
    }
}