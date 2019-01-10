package quanti.com.kotlinlog

import android.content.Context
import quanti.com.kotlinlog.android.MetadataLogger
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.LogLevel

/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Main logger to all subloggers
 */

class Log {

    companion object {

        @JvmStatic
        fun v(text: String) = allLog(LogLevel.VERBOSE, text)

        @JvmStatic
        fun i(text: String) = allLog(LogLevel.INFO, text)

        @JvmStatic
        fun w(text: String) = allLog(LogLevel.WARN, text)

        @JvmStatic
        fun d(text: String) = allLog(LogLevel.DEBUG, text)

        @JvmStatic
        fun e(text: String) = allLog(LogLevel.ERROR, text)

        @JvmStatic
        fun v(tag: String, text: String) = allLog(LogLevel.VERBOSE, tag, "", text)

        @JvmStatic
        fun i(tag: String, text: String) = allLog(LogLevel.INFO, tag, "", text)

        @JvmStatic
        fun w(tag: String, text: String) = allLog(LogLevel.WARN, tag, "", text)

        @JvmStatic
        fun d(tag: String, text: String) = allLog(LogLevel.DEBUG, tag, "", text)

        @JvmStatic
        fun e(tag: String, text: String) = allLog(LogLevel.ERROR, tag, "", text)

        @JvmStatic
        fun e(text: String = "", e: Throwable) = allLogThrowable(text, e)

        @JvmStatic
        fun e(tag: String, text: String = "", e: Throwable) = allLogThrowable(tag, "", text, e)


        @JvmStatic
        fun vSync(text: String) = allLogSync(LogLevel.VERBOSE, text)

        @JvmStatic
        fun iSync(text: String) = allLogSync(LogLevel.INFO, text)

        @JvmStatic
        fun wSync(text: String) = allLogSync(LogLevel.WARN, text)

        @JvmStatic
        fun dSync(text: String) = allLogSync(LogLevel.DEBUG, text)

        @JvmStatic
        fun vSync(tag: String, text: String) = allLogSync(LogLevel.VERBOSE, tag, "", text)

        @JvmStatic
        fun iSync(tag: String, text: String) = allLogSync(LogLevel.INFO, tag, "", text)

        @JvmStatic
        fun wSync(tag: String, text: String) = allLogSync(LogLevel.WARN, tag, "", text)

        @JvmStatic
        fun dSync(tag: String, text: String) = allLogSync(LogLevel.DEBUG, tag, "", text)

        /**
         * Logs some useful system data to all connected loggers
         */
        @JvmStatic
        fun logMetadata(context: Context) {
            d(MetadataLogger.getLogStrings(context.applicationContext))
        }

        /**
         * Activate to log even unchecked system errors
         */
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

    }

}


