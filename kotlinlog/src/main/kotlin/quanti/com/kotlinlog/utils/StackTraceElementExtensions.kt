package quanti.com.kotlinlog.utils

/**
 * Created by Trnka Vladislav on 19.09.2017.
 *
 */


fun StackTraceElement.getClassNameWithoutPackage(): String {

    val indexOfLastDot = className.lastIndexOf('.')
    return className.removeRange(0, indexOfLastDot + 1)
}
