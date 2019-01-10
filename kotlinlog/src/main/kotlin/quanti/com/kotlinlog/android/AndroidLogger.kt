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

    override fun logThrowable(tag: String, methodName: String, text: String, t: Throwable) {
        android.util.Log.e(tag, text, t)
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) = log(androidLogLevel, tag, methodName, text)
}
