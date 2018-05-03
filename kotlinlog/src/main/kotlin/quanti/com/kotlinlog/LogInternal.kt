package quanti.com.kotlinlog

import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.utils.getClassNameWithoutPackage

/***
 * PRIVATE
 */

const val DEBUG_LIBRARY = true
const val SECRET_CODE_UNHANDLED = "UNHANDLED"
var loggerNotAdded = true
internal val loggers = arrayListOf<ILogger>()


internal fun allLog(androidLogLevel: Int, text: String) {
    val element = getMethodStackTraceElement()
    allLog(androidLogLevel, element.getClassNameWithoutPackage(), element.methodName, text)
}

internal fun allLog(androidLogLevel: Int, tag: String, methodName: String = "", text: String) {
    if (emptyCheck()) return
    loggers.forEach {
        it.log(androidLogLevel, tag, methodName, text)
    }
}

internal fun allLogSync(androidLogLevel: Int, text: String) {
    val element = getMethodStackTraceElement()
    allLogSync(androidLogLevel, element.getClassNameWithoutPackage(), element.methodName, text)
}

internal fun allLogSync(androidLogLevel: Int, tag: String, methodName: String = "", text: String) {
    if (emptyCheck()) return
    loggers.forEach {
        it.logSync(androidLogLevel, tag, methodName, text)
    }
}


internal fun allLogThrowable(text: String, t: Throwable) {
    if (emptyCheck()) return
    val element = getMethodStackTraceElement()
    loggers.forEach {
        it.logThrowable(element.className, element.methodName, text, t)
    }
}

internal fun allLogThrowable(tag: String, methodName: String = "", text: String, t: Throwable) {
    if (emptyCheck()) return
    loggers.forEach {
        it.logThrowable(tag, methodName, text, t)
    }
}

private fun emptyCheck(): Boolean {
    if (loggerNotAdded) {
        android.util.Log.e("Logger", "There is not logger to log to. Did not you forget to add logger?")
        return true
    }
    return false
}

/**
 * @return Returns method name by reflexion
 */
@Suppress("UNREACHABLE_CODE")
private fun getMethodStackTraceElement(): StackTraceElement {
    val ste = Thread.currentThread().stackTrace
    var found = false
    ste.forEach {
        if (it.className.contains("log.Log")) {
            found = true
        } else if (found) {
            return it
        }
    }

    return null!!
}