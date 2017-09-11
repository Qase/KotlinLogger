package quanti.com.kotlinlog

import android.util.Log
import quanti.com.kotlinlog.base.ILogger


/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Main logger to all subloggers
 */


class Log {

    companion object {
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
        fun e(text: String, e: Throwable) = allLogThrowable(text, e)

        @JvmStatic
        fun useUncheckedErrorHandler() {

            val oldHandler = Thread.getDefaultUncaughtExceptionHandler()

            Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
                e("", paramThrowable)

                if (oldHandler != null)
                    oldHandler.uncaughtException( paramThread, paramThrowable) //Delegates to Android's error handling
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

            if (loggers.size == 0) {
                android.util.Log.e("Logger", "There is not logger to log to. Did not you forget to add logger?")
                return
            }

            val element = getMethodStackTraceElement()

            loggers.forEach {
                it.log(androidLogLevel, element.className, element.methodName, text)
            }
        }

        private fun allLogThrowable(text: String, t: Throwable) {
            if (loggers.size == 0) {
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
