package quanti.com.kotlinlog.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Created by Trnka Vladislav on 04.07.2017.
 *
 */


/**
 * Returns PackageManager.PERMISSION_DENIED or PackageManager.PERMISSION_GRANTED
 */
fun Context.checkPermission(permission: String) = ContextCompat.checkSelfPermission(this,permission)

/**
 * Returns true when have permission
 */
fun Context.hasPermission(permission: String) = checkPermission(permission) == PackageManager.PERMISSION_GRANTED

/**
 * Returns true if Write external storage permission is provided
 * or if the Android is >= Android 10 since this permission is irrelevant for those versions
 */
fun Context.hasFileWritePermission() = hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) || (Build.VERSION.SDK_INT >= 29)






