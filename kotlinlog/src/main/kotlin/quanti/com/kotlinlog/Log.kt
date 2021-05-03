package quanti.com.kotlinlog

import android.content.Context
import quanti.com.kotlinlog.android.MetadataLogger
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.migration.KotlinLogMigrator
import quanti.com.kotlinlog.utils.logFilesDir
import quanti.com.kotlinlog.utils.loga

/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Main logger to all subloggers
 */

class Log(private val context: Context) {

    companion object {

        private var instance: Log? = null
        private var isNdkInitialised: Boolean = false

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
        fun vSync(tag: String, text: String) = allLogSync(LogLevel.VERBOSE, tag, text)

        @JvmStatic
        fun iSync(tag: String, text: String) = allLogSync(LogLevel.INFO, tag, text)

        @JvmStatic
        fun wSync(tag: String, text: String) = allLogSync(LogLevel.WARN, tag, text)

        @JvmStatic
        fun dSync(tag: String, text: String) = allLogSync(LogLevel.DEBUG, tag, text)

        @JvmStatic
        fun eSync(tag: String, text: String) = allLogSync(LogLevel.ERROR, tag, text)

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
         * The library has to be initialised before any loggers are added.
         * Initialisation handles mainly migration to newer library versions.
         */
        @JvmStatic
        fun initialise(context: Context) {
            if (instance == null) {
                instance = Log(context)
            }
            KotlinLogMigrator.migrate(context)
        }

        private fun checkInitialisation() {
            if (instance == null) {
                throw IllegalStateException("Log uninitialized - please first initialise the library by calling Log.initialise(context)")
            }
        }

        @JvmStatic
        fun initialiseNdk() {
            checkInitialisation()
            instance?.init(instance!!.context.logFilesDir.absolutePath)
        }

        @JvmStatic
        fun deInitialiseNdk() {
            checkInitialisation()
            instance?.deInit()
        }

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
         * Add new logger with specified TAG for later use
         * Replace old one associated with the same TAG
         */
        @JvmStatic
        fun addLogger(logger: ILogger, tag:String = logger.describe()){
            checkInitialisation()
            //clean resources of old logger
            loggers[tag]?.cleanResources()

            //replace with new one
            loggers[tag] = logger
        }

        /**
         * Remove logger using TAG
         * @return true when logger found and deleted, false otherwise
         */
        @JvmStatic
        fun removeLogger(tag:String) : Boolean{
            //function remove return current value associated with given key
            loga("Before delete", loggers.size, loggers.keys.toString())
            val removedLogger = loggers.remove(tag)
            removedLogger?.cleanResources()

            loga("After delete", loggers.size, loggers.keys.toString())
            return removedLogger != null
        }

        /**
         * Removes all loggers
         */
        @JvmStatic
        fun removeAllLoggers() {
            loggers.values.forEach { it.cleanResources() }
            loggers.clear()
        }
    }

    init {
        if (!isNdkInitialised) {
            isNdkInitialised = true
            System.loadLibrary("native-logger")
        }
    }

    external fun init(path: String)

    external fun deInit()
}


