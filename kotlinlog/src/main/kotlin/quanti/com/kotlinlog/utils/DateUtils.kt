package quanti.com.kotlinlog.utils

import java.text.SimpleDateFormat
import java.util.*


val sdf1 = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
val sdf2 = SimpleDateFormat("yyyy_MMdd_HHmm_ss", Locale.ENGLISH)
val sdf3 = SimpleDateFormat("yyyy_MMdd_HHmm", Locale.ENGLISH)
val sdf4 = SimpleDateFormat("yyyy_MMdd", Locale.ENGLISH)


/**
 * MM-dd HH:mm:ss.SS
 */
fun getFormatedNow(): String {
    return sdf1.format(ActualTime.currentDate())
}

/**
 * yyyy_MMdd_HHmm_ss
 */
fun getFormatedFileNameDayNowWithSeconds() : String{
    return sdf2.format(ActualTime.currentDate())
}

/**
 * yyyy_MMdd_HHmm
 */
fun getFormatedFileNameDayNow() : String{
    return sdf3.format(ActualTime.currentDate())
}

/**
 * yyyy_MMdd
 */
fun getFormattedFileNameForDayTemp() : String{
    return sdf4.format(ActualTime.currentDate())
}
