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

        //ASYNC METHODS
        @JvmStatic
        fun v(text: String) = allLog(LogLevel.VERBOSE, null, text)

        @JvmStatic
        fun d(text: String) = allLog(LogLevel.DEBUG, null, text)

        @JvmStatic
        fun i(text: String) = allLog(LogLevel.INFO, null, text)

        @JvmStatic
        fun w(text: String) = allLog(LogLevel.WARN, null, text)

        @JvmStatic
        fun e(text: String) = allLog(LogLevel.ERROR, null, text)


        @JvmStatic
        fun v(tag: String, text: String) = allLog(LogLevel.VERBOSE, tag, text)

        @JvmStatic
        fun d(tag: String, text: String) = allLog(LogLevel.DEBUG, tag, text)

        @JvmStatic
        fun i(tag: String, text: String) = allLog(LogLevel.INFO, tag, text)

        @JvmStatic
        fun w(tag: String, text: String) = allLog(LogLevel.WARN, tag, text)

        @JvmStatic
        fun e(tag: String, text: String) = allLog(LogLevel.ERROR, tag, text)


        //SYNC METHODS
        @JvmStatic
        fun vSync(text: String) = allLogSync(LogLevel.VERBOSE, null, text)

        @JvmStatic
        fun iSync(text: String) = allLogSync(LogLevel.INFO, null, text)

        @JvmStatic
        fun wSync(text: String) = allLogSync(LogLevel.WARN, null, text)

        @JvmStatic
        fun dSync(text: String) = allLogSync(LogLevel.DEBUG, null, text)

        @JvmStatic
        fun eSync(text: String) = allLogSync(LogLevel.ERROR, null, text)

        @JvmStatic
        fun vSync(text: String, tag: String) = allLogSync(LogLevel.VERBOSE, tag, text)

        @JvmStatic
        fun iSync(text: String, tag: String) = allLogSync(LogLevel.INFO, tag, text)

        @JvmStatic
        fun wSync(text: String, tag: String) = allLogSync(LogLevel.WARN, tag, text)

        @JvmStatic
        fun dSync(text: String, tag: String) = allLogSync(LogLevel.DEBUG, tag, text)

        @JvmStatic
        fun eSync(text: String, tag: String) = allLogSync(LogLevel.ERROR, tag, text)

        //THROWABLE METHODS

        @JvmStatic @JvmOverloads
        fun v(t: Throwable, text: String = "") = allLogThrowable(LogLevel.VERBOSE, null, text, t)

        @JvmStatic @JvmOverloads
        fun d(t: Throwable, text: String = "") = allLogThrowable(LogLevel.DEBUG, null, text, t)

        @JvmStatic @JvmOverloads
        fun i(t: Throwable, text: String = "") = allLogThrowable(LogLevel.INFO, null, text, t)

        @JvmStatic @JvmOverloads
        fun w(t: Throwable, text: String = "") = allLogThrowable(LogLevel.WARN, null, text, t)

        @JvmStatic @JvmOverloads
        fun e(t: Throwable, text: String = "") = allLogThrowable(LogLevel.ERROR, null, text, t)


        @JvmStatic @JvmOverloads
        fun v(tag: String, t: Throwable, text: String = "") = allLogThrowable(LogLevel.VERBOSE, tag, text, t)

        @JvmStatic @JvmOverloads
        fun d(tag: String, t: Throwable, text: String = "") = allLogThrowable(LogLevel.DEBUG, tag, text, t)

        @JvmStatic @JvmOverloads
        fun i(tag: String, t: Throwable, text: String = "") = allLogThrowable(LogLevel.INFO, tag, text, t)

        @JvmStatic @JvmOverloads
        fun w(tag: String, t: Throwable, text: String = "") = allLogThrowable(LogLevel.WARN, tag, text, t)

        @JvmStatic @JvmOverloads
        fun e(tag: String, t: Throwable, text: String = "") = allLogThrowable(LogLevel.ERROR, tag, text, t)


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
            loggers.forEach { it.cleanResources() }
            loggers.clear()
        }
    }


}


