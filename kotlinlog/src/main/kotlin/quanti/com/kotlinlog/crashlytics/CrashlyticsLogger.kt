package quanti.com.kotlinlog.crashlytics

import com.crashlytics.android.Crashlytics
import quanti.com.kotlinlog.android.AndroidLogger
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.base.LoggerBundle

/**
 * Created by Trnka Vladislav on 27.06.2017.
 *
 * Implementation of logger to crashlytics
 *
 * Default log is from Warn since crashlytics is strict to file size
 */
object CrashlyticsLogger : ILogger {

    private var bun: LoggerBundle = LoggerBundle(LogLevel.WARN)

    fun init(bundle: LoggerBundle = LoggerBundle()){
        bun = bundle
    }

    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        if (androidLogLevel >= bun.minimalLogLevel) {
            Crashlytics.log("$tag $methodName $text")
        }
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String)  = log(androidLogLevel, tag, methodName, text)

    override fun logThrowable(tag: String, methodName: String, text: String, t: Throwable) {
        Crashlytics.log("$tag $methodName $text")
        Crashlytics.logException(t)
    }

}
