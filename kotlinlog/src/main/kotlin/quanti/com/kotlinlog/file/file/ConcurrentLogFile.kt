package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.file.base.FileLoggerBundle
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Created by Trnka Vladislav on 13.09.2017.
 *
 * File that has thread lock on writing
 */

open class ConcurrentLogFile(
        ctx: Context,
        fileName: String,
        bun: FileLoggerBundle
) : BaseLogFile(
        ctx,
        fileName,
        bun.maxDaysSaved
) {

    val dayLock = ReentrantLock()

    override fun write(string: String) {
        dayLock.withLock {
            super.write(string)
        }
    }
}