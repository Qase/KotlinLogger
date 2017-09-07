package quanti.com.kotlinlog.utils

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

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





