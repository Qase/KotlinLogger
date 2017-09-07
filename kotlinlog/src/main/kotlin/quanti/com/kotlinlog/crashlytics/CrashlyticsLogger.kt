//package quanti.com.kotlinlog.crashlytics
//
//import com.crashlytics.android.Crashlytics
//import quanti.com.kotlinlog.base.ILogger
//import quanti.com.kotlinlog.base.LoggerBundle
//
///**
// * Created by Trnka Vladislav on 27.06.2017.
// *
// * Implementation of logger to crashlytics
// */
//
//class CrashlyticsLogger(
//        private val bun: LoggerBundle = LoggerBundle()
//) : ILogger {
//
//    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
//        if (androidLogLevel >= bun.minimalLogLevel) {
//            Crashlytics.log("$tag $methodName $text")
//        }
//    }
//
//    override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {
//        if (androidLogLevel >= bun.minimalLogLevel) {
//            Crashlytics.logException(t)
//        }
//    }
//
//}
