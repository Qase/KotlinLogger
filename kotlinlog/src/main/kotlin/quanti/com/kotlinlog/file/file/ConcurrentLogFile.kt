package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.file.base.FileLoggerBundle
import java.util.concurrent.LinkedBlockingQueue
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

    private val dayLock = ReentrantLock()

    override fun write(string: String) {
        dayLock.withLock {
            super.write(string)
        }
    }

    override fun writeBatch(queue: LinkedBlockingQueue<String>) {
        dayLock.withLock {
            super.writeBatch(queue)
        }
    }

    override fun delete() {
        dayLock.withLock {
            super.delete()
        }
    }

    override fun emptyFile() {
        dayLock.withLock {
            super.emptyFile()
        }
    }
}