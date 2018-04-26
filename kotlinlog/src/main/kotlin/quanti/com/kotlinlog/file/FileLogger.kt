package quanti.com.kotlinlog.file

import android.content.Context
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.file.base.FileLoggerBase
import quanti.com.kotlinlog.file.base.FileLoggerBundle
import quanti.com.kotlinlog.file.file.BaseLogFile
import quanti.com.kotlinlog.file.file.CrashLogFile
import quanti.com.kotlinlog.file.file.DayLogFile
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.utils.getApplicationName
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Implementation of async file logger
 *
 *
 * @param appCtx application (!) context
 * @param bun file logger settings
 */

class FileLogger @JvmOverloads constructor(
        appCtx: Context,
        bun: FileLoggerBundle = FileLoggerBundle()
) : FileLoggerBase(appCtx, bun) {

    private val dayFile = DayLogFile(appCtx, bun)

    private val blockingQueue = LinkedBlockingQueue<String>()
    private val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val appName: String

    init {
        threadExecutor.scheduleAtFixedRate(
                {
                    //val fl = blockingQueue.size
                    while (blockingQueue.isNotEmpty()) {
                        dayFile.write(blockingQueue.poll())
                    }
                    //android.util.Log.i("Tag", "Flushed: $fl")
                }, 1, 5, TimeUnit.SECONDS
        )

        appName = ctx.getApplicationName()
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

        dayFile.write(formattedString)
    }


    override fun logThrowable(tag: String, methodName: String, text: String, t: Throwable) {
        val errorFile = CrashLogFile(
                ctx,
                bun,
                t.javaClass.simpleName,
                text == Log.SECRET_CODE_UNHANDLED
        )

        val formattedString = getFormatedString(appName, LogLevel.ERROR, tag, methodName, text)

        val sb = StringBuilder()
        sb.append(formattedString)
        sb.append(t.convertToLogCatString())

        val str = sb.toString()
        errorFile.write(str)
        errorFile.closeOutputStream()

        dayFile.write(str)
    }

    /**
     * Deletes all logs from application
     */
    companion object {

        @JvmStatic
        public fun deleteAllLogs(appCtx: Context){
            FileLoggerBase.removeAllOldTemps(appCtx, -1)
        }
    }



}
