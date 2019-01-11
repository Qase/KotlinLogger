package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.TAG
import quanti.com.kotlinlog.utils.ActualTime
import quanti.com.kotlinlog.utils.loga
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


const val LOG_FILE_EXTENSION = ".log"


/**
 * AbstractLogFile class
 * provides basic write and delete operations
 *
 * every implementation has to provide its own file rotating mechanism
 * every implementation has to put its own identifier into filename to be able to filter against
 *
 */
abstract class AbstractLogFile(
        private val ctx: Context
) {

    private val lock = ReentrantLock()

    protected abstract var fileName: String
    protected abstract var file: File

    //this writes to internal storage so no need for permissions
    protected abstract var fos: FileOutputStream

    protected abstract val logIdentifier: String


    /**
     * Write string to associated file
     */
    fun write(string: String) {
        lock.withLock {
            fos.write(string.toByteArray())
        }
    }

    /**
     * Write whole queue of strings to associated file
     */
    fun writeBatch(queue: LinkedBlockingQueue<String>) {
        lock.withLock {
            while (queue.isNotEmpty()) {
                fos.write(queue.poll().toByteArray())
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
            android.util.Log.i(TAG, "Deleting file: $del")
            fos = ctx.openFileOutput(fileName, Context.MODE_APPEND)
        }
    }


    /**
     * Close associated file output stream
     */
    fun closeOutputStream() = fos.close()


    /**
     * Returns all files that corresponds to this logger
     */
    protected fun listOfLoggerFiles() = ctx.filesDir.listFiles().filter { it.name.contains(logIdentifier) }


    /**
     * Close old file stream and create new empty file to be ready
     */
    protected fun createNewFile() {
        lock.withLock {
            fos.close()
            fileName = createNewFileName()
            loga("Creating new file $fileName")
            file = File(ctx.filesDir, fileName)
            file.setLastModified(ActualTime.currentTimeMillis()) //needed for testing
            fos = ctx.openFileOutput(fileName, Context.MODE_APPEND)
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