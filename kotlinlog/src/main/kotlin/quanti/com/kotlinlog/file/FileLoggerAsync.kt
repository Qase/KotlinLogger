package quanti.com.kotlinlog.file

import android.content.Context
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.file.base.FileLoggerBase
import quanti.com.kotlinlog.file.base.FileLoggerBundle
import quanti.com.kotlinlog.file.file.CrashLogFile
import quanti.com.kotlinlog.file.file.DayLogFile
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.utils.mergeToFile
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Implementation of async file logger
 *
 * Does not have temp file as backing
 *
 *
 * @param appCtx application (!) context
 * @param bun file logger settings
 */

class FileLoggerAsync @JvmOverloads constructor(
        appCtx: Context,
        bun: FileLoggerBundle = FileLoggerBundle()
) : FileLoggerBase(appCtx, bun) {

    val dayLock = ReentrantLock()
    val dayFile = DayLogFile(appCtx, bun)

    val blockingQueue = LinkedBlockingQueue<String>()
    val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    init {
        threadExecutor.scheduleAtFixedRate(
                {
                    val fl = blockingQueue.size
                    while (blockingQueue.isNotEmpty()) {
                            dayLock.withLock {
                                dayFile.write(blockingQueue.poll())
                            }
                    }
                    android.util.Log.i("Tag", "Flushed: $fl")
                }, 1, 5, TimeUnit.SECONDS
        )
    }

    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {

        if (androidLogLevel < bun.minimalLogLevel) {
            return
        }

        val formattedString = getFormatedString(androidLogLevel, tag, methodName, text)

        blockingQueue.add(formattedString)
    }

    override fun logThrowable(tag: String, methodName: String, text: String, t: Throwable) {
        val errorFile = CrashLogFile(ctx, bun, t.javaClass.simpleName)

        val formattedString = getFormatedString(LogLevel.ERROR, tag, methodName, text)

        errorFile.write(formattedString)
        errorFile.write(t.convertToLogCatString())
        errorFile.closeOutputStream()

        dayLock.withLock {
            mergeToFile(errorFile.getFileName(), dayFile.getFileName(), ctx)
        }
    }
}
