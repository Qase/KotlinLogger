package quanti.com.kotlinlog.utils

import android.content.Context
import java.io.PrintWriter
import java.io.StringWriter


/**
 * Created by Trnka Vladislav on 11.09.2017.
 *
 * Another helper class ;);););]
 */

fun Context.getApplicationName(): String {
    val stringId = applicationInfo.labelRes
    val str = if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)

    return str.replace(' ', '_')
}

fun StackTraceElement.getClassNameWithoutPackage(): String {

    val indexOfLastDot = className.lastIndexOf('.')
    return className.removeRange(0, indexOfLastDot + 1)
}

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
fun getRandomString(length: Int = 10): String {
    return (1..length)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
}