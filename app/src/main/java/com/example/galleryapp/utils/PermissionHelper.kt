package com.example.galleryapp.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionHelper(
    private val caller: ActivityResultCaller,
    private val activity: Activity,
    private val onPermissionResult: (granted: Boolean) -> Unit
) {
    var cameFromSettings: Boolean = false

    private val permissionLauncher = caller.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.all { it.value }
        onPermissionResult(granted)
    }

    fun requestMediaPermissions() {
        permissionLauncher.launch(getMediaPermissions())
    }

    fun hasMediaPermissions(): Boolean {
        return getMediaPermissions().all {
            ContextCompat.checkSelfPermission(activity, it) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    fun shouldShowPermissionRationale(): Boolean {
        return getMediaPermissions().any {
            androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
    }

    fun openAppSettings() {
        cameFromSettings = true
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(intent)
    }

    private fun getMediaPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}
