package quanti.com.kotlinlog

import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.utils.getClassNameWithoutPackage

/***
 * PRIVATE
 */

const val TAG = "LogFileDEBUG"
const val SECRET_CODE_UNHANDLED = "UNHANDLED"
var loggerNotAdded = true
internal val loggers = arrayListOf<ILogger>()


internal fun allLog(androidLogLevel: Int, tag: String?, text: String) {
    if (emptyCheck()) return

    val element = getMethodStackTraceElement()
    val safeTag = tag ?: element.getClassNameWithoutPackage()

    loggers.filterLogLevel(androidLogLevel).forEach {
        it.log(androidLogLevel, safeTag, element.methodName, text)
    }
}

internal fun allLogSync(androidLogLevel: Int, tag: String?, text: String) {
    if (emptyCheck()) return

    val element = getMethodStackTraceElement()
    val safeTag = tag ?: element.getClassNameWithoutPackage()

    loggers.filterLogLevel(androidLogLevel).forEach {
        it.logSync(androidLogLevel, safeTag, element.methodName, text)
    }
}

internal fun allLogThrowable(androidLogLevel: Int, tag: String?, text: String, t: Throwable) {
    if (emptyCheck()) return

    val element = getMethodStackTraceElement()
    val safeTag = tag ?: element.getClassNameWithoutPackage()

    loggers.filterLogLevel(androidLogLevel).forEach {
        it.logThrowable(androidLogLevel, safeTag, element.methodName, text, t)
    }
}

private fun ArrayList<ILogger>.filterLogLevel(logLevel: Int) = filter { logLevel >= it.getMinimalLoggingLevel()}

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