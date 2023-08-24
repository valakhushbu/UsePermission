package com.example.permissionaccessmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

public class PermissionAccess  private constructor(private val activity: WeakReference<Activity>?,
                                            private val context: Context
){

    val registerActivityForResult =
        if (activity?.get() is ComponentActivity) {
            (activity.get() as ComponentActivity).registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            )
            { isGranted ->
                if (isGranted) {
                    Log.d("Activity", "Permission granted")
                } else {
                    Log.d("Activity", "Permission denied")
                    showPermissionDeniedDialog()
                }
            }
        } else {
            null
        }

    inline fun sdkIntAboveOreo(call: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            call.invoke()
        }
    }

    inline fun isPermissionGranted(context: Context, permission: String, call: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            call.invoke(true)
        } else {
            call.invoke(false)
        }
    }
    private fun showPermissionDeniedDialog() {
        val alertDialogBuilder = AlertDialog.Builder(context)
            .setTitle("Permission Denied")
            .setMessage("Please grant the permission in the app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialogBuilder.show()
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity?.get()?.packageName, null)
        intent.data = uri
        activity?.get()?.startActivity(intent)
    }

    companion object {
        fun with(activity: Activity, context: Context): PermissionAccess {
            return PermissionAccess(WeakReference(activity), context)
        }
    }

}