package quanti.com.kotlinlog

import android.content.Context
import android.util.Log
import quanti.com.kotlinlog.android.MetadataLogger
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.utils.getClassNameWithoutPackage


/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Main logger to all subloggers
 */


class Log {

    companion object {
        const val DEBUG_LIBRARY = true

        const val SECRET_CODE_UNHANDLED = "UNHANDLED"

        var loggerNotAdded = true


        private val loggers = arrayListOf<ILogger>()

        @JvmStatic
        fun v(text: String) = allLog(Log.VERBOSE, text)

        @JvmStatic
        fun i(text: String) = allLog(Log.INFO, text)

        @JvmStatic
        fun w(text: String) = allLog(Log.WARN, text)

        @JvmStatic
        fun d(text: String) = allLog(Log.DEBUG, text)

        @JvmStatic
        fun e(text: String) = allLog(Log.ERROR, text)

        @JvmStatic
        fun e(text: String = "", e: Throwable) = allLogThrowable(text, e)

        @JvmStatic
        fun vSync(text: String) = allLogSync(Log.VERBOSE, text)

        @JvmStatic
        fun iSync(text: String) = allLogSync(Log.INFO, text)

        @JvmStatic
        fun wSync(text: String) = allLogSync(Log.WARN, text)

        @JvmStatic
        fun dSync(text: String) = allLogSync(Log.DEBUG, text)

        @JvmStatic
        fun eSync(text: String) = allLogSync(Log.ERROR, text)

        @JvmStatic
        fun logMetadata(context: Context) {
            d(MetadataLogger.getLogStrings(context))
        }

        @JvmStatic
        fun useUncheckedErrorHandler() {

            val oldHandler = Thread.getDefaultUncaughtExceptionHandler()

            Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
                e(SECRET_CODE_UNHANDLED, paramThrowable)

                if (oldHandler != null)
                    oldHandler.uncaughtException(paramThread, paramThrowable) //Delegates to Android's error handling
                else
                    System.exit(2) //Prevents the service/app from freezing
            }
        }

        /**
         * Adds new logger
         */
        @JvmStatic
        fun addLogger(logger: ILogger) {
            loggers.add(logger)
            loggerNotAdded = false
        }

        /**
         * Removes all loggers
         */
        @JvmStatic
        fun removeAllLoggers() {
            loggers.clear()
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

        /**
         * @param androidLogLevel [android.util.Log] int values
         */
        private fun allLog(androidLogLevel: Int, text: String) {

            if (loggerNotAdded) {
                android.util.Log.e("Logger", "There is not logger to log to. Did not you forget to add logger?")
                return
            }

            val element = getMethodStackTraceElement()

            loggers.forEach {
                it.log(androidLogLevel, element.getClassNameWithoutPackage(), element.methodName, text)
            }


        }

        /**
         * @param androidLogLevel [android.util.Log] int values
         */
        private fun allLogSync(androidLogLevel: Int, text: String) {

            if (loggerNotAdded) {
                android.util.Log.e("Logger", "There is not logger to log to. Did not you forget to add logger?")
                return
            }

            val element = getMethodStackTraceElement()

            loggers.forEach {
                it.logSync(androidLogLevel, element.className, element.methodName, text)
            }
        }


        private fun allLogThrowable(text: String, t: Throwable) {
            if (loggerNotAdded) {
                android.util.Log.e("Logger", "There is not logger to log to. Did not you forget to add logger?")
                return
            }

            val element = getMethodStackTraceElement()

            loggers.forEach {
                it.logThrowable(element.className, element.methodName, text, t)
            }
        }
    }
}
