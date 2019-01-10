package quanti.com.kotlinlog.utils

import android.content.Context




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

fun Throwable.convertToLogCatString(): String {

    val sb = StringBuilder()

    sb.append(this.toString())
    sb.append("\n")

    this.stackTrace.forEach {
        sb.append("\t\t\t\t")
        if (it.isNativeMethod)
            sb.append("\t")
        sb.append(it.className)
        sb.append(".")
        sb.append(it.methodName)
        sb.append("(")
        sb.append(it.fileName)
        sb.append(":")
        sb.append(it.lineNumber.toString())
        sb.append(")")
        sb.append("\n")
    }

    return sb.toString()

}

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
fun getRandomString(length: Int = 10): String {
    return (1..length)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
}