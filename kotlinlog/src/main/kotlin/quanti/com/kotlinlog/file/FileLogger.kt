package quanti.com.kotlinlog.file

import android.annotation.SuppressLint
import android.content.Context
import quanti.com.kotlinlog.SECRET_CODE_UNHANDLED
import quanti.com.kotlinlog.TAG
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.base.getLogLevelString
import quanti.com.kotlinlog.file.bundle.DayLogBundle
import quanti.com.kotlinlog.file.file.CrashLogFile
import quanti.com.kotlinlog.file.file.DayLogFile
import quanti.com.kotlinlog.file.file.ILogFile
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.utils.getApplicationName
import quanti.com.kotlinlog.utils.getFormattedNow
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


@SuppressLint("StaticFieldLeak") //it is ok since it is app context
/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Implementation of async file logger
 */
//todo java annotation
class FileLogger(
        private val appCtx: Context,
        private val bun: DayLogBundle = DayLogBundle(),
        useDayLog: Boolean = true
) : ILogger{

    private var logFile: ILogFile = if (useDayLog) DayLogFile(appCtx, bun.maxDaysSaved) else DayLogFile(appCtx, bun.maxDaysSaved)

    private val blockingQueue = LinkedBlockingQueue<String>()
    private val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var appName: String = appCtx.getApplicationName()


    init {
        val func = Runnable {
            //first write everything form queue to file
            android.util.Log.i(TAG, "TASK: Writing data from queue: ${blockingQueue.size}")
            logFile.writeBatch(blockingQueue)

            //perform cleaning
            android.util.Log.i(TAG, "TASK: Cleaning folder")
            logFile.cleanFolder()
        }
        threadExecutor.scheduleAtFixedRate(func, 1, 5, TimeUnit.SECONDS)
    }

    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {

        if (androidLogLevel < bun.minimalLogLevel) {
            return
        }

        val formattedString = getFormatedString(appName, androidLogLevel, tag, methodName, text)

        blockingQueue.add(formattedString)
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        if (androidLogLevel < bun.minimalLogLevel) {
            return
        }

        val formattedString = getFormatedString(appName, androidLogLevel, tag, methodName, text)

        logFile.write(formattedString)
    }


    override fun logThrowable(tag: String, methodName: String, text: String, t: Throwable) {
        val errorFile = CrashLogFile(
                appCtx,
                t.javaClass.simpleName,
                text == SECRET_CODE_UNHANDLED
        )

        val formattedString = getFormatedString(appName, LogLevel.ERROR, tag, methodName, text)

        val sb = StringBuilder()
        sb.append(formattedString)
        sb.append(t.convertToLogCatString())

        val str = sb.toString()
        errorFile.write(str)
        errorFile.closeOutputStream()

        //we want errors to be sync
        logFile.write(str)
    }

    /**
     * Returns android-log like formatted string
     */
    private fun getFormatedString(appName: String, logLevel: Int, className: String, methodName: String, text: String): String {
        return "${getFormattedNow()}/$appName ${logLevel.getLogLevelString()}/${className}_$methodName: $text\n"
    }

    companion object {
        fun deleteAllLogs(appCtx: Context){
            appCtx.filesDir.listFiles().forEach { it.delete() }
        }
    }




}
