package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.TAG
import quanti.com.kotlinlog.utils.ActualTime
import quanti.com.kotlinlog.utils.logFilesDir
import quanti.com.kotlinlog.utils.loga
import quanti.com.kotlinlog.utils.openLogFileOutput
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


/**
 * AbstractLogFile class
 * provides basic write and delete operations
 *
 * every implementation has to provide its own file rotating mechanism
 * every implementation has to put its own identifier into filename to be able to filter against
 *
 */
abstract class AbstractLogFile(
        protected val ctx: Context
) {

    protected val lock = ReentrantLock()

    abstract var fileName: String //make it testable
    protected abstract var file: File

    //this writes to internal storage so no need for permissions
    protected abstract var fos: FileOutputStream

    protected abstract val logIdentifier: String


    /**
     * Write string to associated file
     */
    open fun write(string: String) {
        lock.withLock {
            if (!file.exists()){
                createNewFile()
            }
            fos.write(string.toByteArray())
        }
    }

    /**
     * Write whole queue of strings to associated file
     */
    open fun writeBatch(queue: LinkedBlockingQueue<String>) {
        lock.withLock {
            if (!file.exists()){
                createNewFile()
            }
            while (queue.isNotEmpty()) {
                queue.poll()?.toByteArray()?.let{ fos.write(it) }
            }
        }
    }

    /**
     * Delete file
     */
    fun delete() {
        lock.withLock {
            val del = file.delete()
            loga("File ${file.absolutePath} was deleted: $del")
        }
    }

    /**
     * Empty file, delete it and prepare new file for writing
     */
    fun emptyFile() {
        lock.withLock {
            fos.close()
            val del = file.delete()
            loga("Deleting file: $del")
            fos = ctx.openLogFileOutput(fileName, true)
        }
    }


    /**
     * Close associated file output stream
     */
    fun closeOutputStream() = fos.close()


    /**
     * Returns all files that corresponds to this logger
     */
    protected fun listOfLoggerFiles() = ctx.logFilesDir.listFiles()?.filter { it.name.contains(logIdentifier) }


    /**
     * Close old file stream and create new empty file to be ready
     */
    protected fun createNewFile() {
        lock.withLock {
            fos.close()
            fileName = createNewFileName()
            loga("Creating new file $fileName")
            file = File(ctx.logFilesDir, fileName)
            file.setLastModified(ActualTime.currentTimeMillis()) //needed for testing
            fos = ctx.openLogFileOutput(fileName, true)
        }
    }

    /**
     * Clean folder from old logs
     */
    abstract fun cleanFolder()

    /**
     * Create new name for file
     *
     * It has to end with ".log:
     * It has to include logger identificator Ex. "circle"
     */
    protected abstract fun createNewFileName(): String

}