package quanti.com.kotlinlog.file

import android.annotation.SuppressLint
import android.content.Context
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.file.base.FileLoggerBase
import quanti.com.kotlinlog.file.base.FileLoggerBundle
import quanti.com.kotlinlog.file.file.CrashLogFile
import quanti.com.kotlinlog.file.file.DayLogFile
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.utils.getApplicationName
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

object FileLogger : FileLoggerBase() {

    private lateinit var dayFile: DayLogFile

    private val blockingQueue = LinkedBlockingQueue<String>()
    private val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var appName: String

    public override fun init(appCtx: Context, bundle: FileLoggerBundle) {
        //idiot proof solution :D
        super.init(appCtx.applicationContext, bundle)
        dayFile = DayLogFile(ctx, bun)

        threadExecutor.scheduleAtFixedRate({dayFile.writeBatch(blockingQueue)}, 1, 5, TimeUnit.SECONDS)

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
     * @param excludeZips   do not delete zipped files
     */
    fun deleteAllLogs(excludeZips: Boolean = false) {
        removeAllOldTemps(FileLogger.ctx, -1, true, excludeZips)
        dayFile.emptyFile()
    }


}
