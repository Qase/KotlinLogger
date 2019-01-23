package quanti.com.kotlinlog.file

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import quanti.com.kotlinlog.SECRET_CODE_UNHANDLED
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.getLogLevelString
import quanti.com.kotlinlog.file.bundle.BaseBundle
import quanti.com.kotlinlog.file.bundle.CircleLogBundle
import quanti.com.kotlinlog.file.bundle.DayLogBundle
import quanti.com.kotlinlog.file.bundle.StrictCircleLogBundle
import quanti.com.kotlinlog.file.file.*
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.utils.getApplicationName
import quanti.com.kotlinlog.utils.getFormattedNow
import quanti.com.kotlinlog.utils.loga
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Implementation of async file logger
 */
class FileLogger(
        private val appCtx: Context,
        private val bun: BaseBundle
) : ILogger {


    private var logFile: AbstractLogFile = when (bun) {
        is DayLogBundle -> DayLogFile(appCtx, bun.maxDaysSaved)
        is CircleLogBundle -> CircleLogFile(appCtx, bun)
        is StrictCircleLogBundle -> StrictCircleLogFile(appCtx, bun)
        else -> {
            throw Exception("Unknown file bundle.")
        }
    }

    private val blockingQueue = LinkedBlockingQueue<String>()
    private val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var appName: String = appCtx.getApplicationName()

    private val func = Runnable {
        //first write everything form queue to file
        loga("TASK: Writing data from queue: ${blockingQueue.size}")
        logFile.writeBatch(blockingQueue)

        //perform cleaning
        loga("TASK: Cleaning folder")
        logFile.cleanFolder()

        //clean error files
        CrashLogFile.clean(appCtx, bun.maxDaysSavedThrowable)
    }


    init {
        threadExecutor.scheduleAtFixedRate(func, 1, 5, TimeUnit.SECONDS)
    }

    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        val formattedString = getFormatedString(appName, androidLogLevel, tag, methodName, text)
        blockingQueue.add(formattedString)
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        val formattedString = getFormatedString(appName, androidLogLevel, tag, methodName, text)
        logFile.write(formattedString)
    }


    override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {

        val formattedString = getFormatedString(appName, androidLogLevel, tag, methodName, text)

        val sb = StringBuilder()
        sb.append(formattedString)
        sb.append(t.convertToLogCatString())

        val finalText = sb.toString()

        if (bun.minimalOwnFileLogLevelThrowable <= androidLogLevel) {
            GlobalScope.launch(Dispatchers.IO) {
                CrashLogFile(
                        appCtx,
                        t.javaClass.simpleName,
                        text == SECRET_CODE_UNHANDLED
                ).apply {
                    write(finalText)
                    closeOutputStream()
                }
            }
        }

        //we want errors to be sync
        logFile.write(finalText)
    }

    override fun cleanResources() {
        threadExecutor.shutdown()
    }

    override fun getMinimalLoggingLevel(): Int = bun.minimalLogLevel

    /**
     * Returns android-log like formatted string
     */
    private fun getFormatedString(appName: String, logLevel: Int, className: String, methodName: String, text: String): String {
        return "${getFormattedNow()}/$appName ${logLevel.getLogLevelString()}/${className}_$methodName: $text\n"
    }

    /**
     * Forces all stored data in queue to be written
     */
    internal fun forceWrite(){
        threadExecutor.execute(func)
    }

    companion object {
        fun deleteAllLogs(appCtx: Context) {
            appCtx.filesDir.listFiles().forEach { it.delete() }
        }
    }


}
