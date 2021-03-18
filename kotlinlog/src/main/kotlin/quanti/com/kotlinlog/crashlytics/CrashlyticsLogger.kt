package quanti.com.kotlinlog.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
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
class CrashlyticsLogger(
        private var bun: LoggerBundle = LoggerBundle(LogLevel.WARN)
) : ILogger {

    override fun getMinimalLoggingLevel(): Int = bun.minimalLogLevel

    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        FirebaseCrashlytics.getInstance().log("$tag $methodName $text")
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) = log(androidLogLevel, tag, methodName, text)

    override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {
        FirebaseCrashlytics.getInstance().apply {
            log("$tag $methodName $text")
            recordException(t)
        }
    }

    override fun cleanResources() {}

    override fun describe() = "CrashlyticsLogger"


}
