package quanti.com.kotlinlog.android

import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.LoggerBundle

/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Implementation to standard android logger
 */


class AndroidLogger(
        private var bun: LoggerBundle = LoggerBundle()
) : ILogger {

    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        if (androidLogLevel >= bun.minimalLogLevel) {
            android.util.Log.println(androidLogLevel, tag, text)
        }
    }

    override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {
        val newText = text + '\n' + android.util.Log.getStackTraceString(t)
        android.util.Log.println(androidLogLevel, tag, newText)
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) = log(androidLogLevel, tag, methodName, text)

    override fun cleanResources() {}

}
