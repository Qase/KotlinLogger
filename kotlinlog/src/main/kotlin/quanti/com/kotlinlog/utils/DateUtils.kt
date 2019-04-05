package quanti.com.kotlinlog.utils

import java.text.SimpleDateFormat
import java.util.*


val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
val sdf2 = SimpleDateFormat("yyyy_MMdd_HHmm_ss", Locale.ENGLISH)
val sdf3 = SimpleDateFormat("yyyy_MMdd_HHmm", Locale.ENGLISH)
val sdf4 = SimpleDateFormat("yyyy_MMdd", Locale.ENGLISH)


/**
 * MM-dd HH:mm:ss.SS
 */
fun getFormattedNow() = sdf1.formatActualTime()

/**
 * yyyy_MMdd_HHmm_ss
 */
fun getFormattedFileNameDayNowWithSeconds() = sdf2.formatActualTime()

/**
 * yyyy_MMdd_HHmm
 */
fun getFormattedFileNameDayNow() = sdf3.formatActualTime()

/**
 * yyyy_MMdd
 */
fun getFormattedFileNameForDayTemp() = sdf4.formatActualTime()

fun SimpleDateFormat.formatActualTime(): String = this.format(ActualTime.currentDate())