package quanti.com.kotlinlog.utils

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import java.util.jar.Manifest

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

fun Context.hasFileWritePermission() = hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)






