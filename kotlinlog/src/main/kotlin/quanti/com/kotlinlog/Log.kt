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
        @JvmStatic @JvmOverloads
        fun v(text: String, tag: String? = null) = allLog(LogLevel.VERBOSE, tag, text)

        @JvmStatic @JvmOverloads
        fun d(text: String, tag: String? = null) = allLog(LogLevel.DEBUG, tag, text)

        @JvmStatic @JvmOverloads
        fun i(text: String, tag: String? = null) = allLog(LogLevel.INFO, tag, text)

        @JvmStatic @JvmOverloads
        fun w(text: String, tag: String? = null) = allLog(LogLevel.WARN, tag, text)

        @JvmStatic @JvmOverloads
        fun e(text: String, tag: String? = null) = allLog(LogLevel.ERROR, tag, text)


        //SYNC METHODS
        @JvmStatic @JvmOverloads
        fun vSync(text: String, tag: String? = null) = allLogSync(LogLevel.VERBOSE, tag, text)

        @JvmStatic @JvmOverloads
        fun iSync(text: String, tag: String? = null) = allLogSync(LogLevel.INFO, tag, text)

        @JvmStatic @JvmOverloads
        fun wSync(text: String, tag: String? = null) = allLogSync(LogLevel.WARN, tag, text)

        @JvmStatic @JvmOverloads
        fun dSync(text: String, tag: String? = null) = allLogSync(LogLevel.DEBUG, tag, text)

        @JvmStatic @JvmOverloads
        fun eSync(text: String, tag: String? = null) = allLogSync(LogLevel.ERROR, tag, text)

        //THROWABLE METHODS
        @JvmStatic @JvmOverloads
        fun v(text: String, t: Throwable, tag: String? = null) = allLogThrowable(LogLevel.VERBOSE, tag, text, t)

        @JvmStatic @JvmOverloads
        fun d(text: String, t: Throwable, tag: String? = null) = allLogThrowable(LogLevel.INFO, tag, text, t)

        @JvmStatic @JvmOverloads
        fun i(text: String, t: Throwable, tag: String? = null) = allLogThrowable(LogLevel.WARN, tag, text, t)

        @JvmStatic @JvmOverloads
        fun w(text: String, t: Throwable, tag: String? = null) = allLogThrowable(LogLevel.DEBUG, tag, text, t)

        @JvmStatic @JvmOverloads
        fun e(text: String, t: Throwable, tag: String? = null) = allLogThrowable(LogLevel.ERROR, tag, text, t)


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


