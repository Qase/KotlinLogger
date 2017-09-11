package quanti.com.kotlinlog.file

import android.content.Context
import quanti.com.kotlinlog.base.LogLevel
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
 * Implementation of file logger
 *
 * @param appCtx application (!) context
 * @param bun file logger settings
 */

class FileLoggerAsync @JvmOverloads constructor(
        appCtx: Context,
        bun: FileLoggerBundle = FileLoggerBundle()
) : FileLoggerBase(appCtx, bun) {

    val dayLock = ReentrantLock()

    val blockingQueue = LinkedBlockingQueue<String>()
    val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    init {
        threadExecutor.scheduleAtFixedRate(
                {
                    val fl = blockingQueue.size
                    while (blockingQueue.isNotEmpty()) {
                        if (actualLogFile.isFull()) {
                            actualLogFile.closeOutputStream()

                            dayLock.withLock {
                                mergeToFile(actualLogFile.getName(), getDayTemp(), ctx)
                            }

                            actualLogFile.delete()

                            actualLogFile = LogFile(ctx, bun)
                        }
                        actualLogFile.write(blockingQueue.poll())
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
        val errorFile = LogFile(ctx, bun, true, t.javaClass.simpleName)

        val formattedString = getFormatedString(LogLevel.ERROR, tag, methodName, text)

        errorFile.write(formattedString)
        errorFile.write(t.convertToLogCatString())
        errorFile.closeOutputStream()

        dayLock.withLock {
            mergeToFile(errorFile.getName(), getDayTemp(), ctx)
        }
    }
}
