package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.DEBUG_LIBRARY
import quanti.com.kotlinlog.TAG
import quanti.com.kotlinlog.utils.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Created by Trnka Vladislav on 13.09.2017.
 *
 * File that has thread lock on writing
 */

class DayLogFile(
        private val ctx: Context,
        private val maxDays: Int
) : ILogFile {
    private val lock = ReentrantLock()

    private var fileName = getFormattedFileNameForDayTemp() + "_day.log"
    private var file = File(ctx.filesDir, fileName)

    //this writes to internal storage so no need for permissions
    private var fos: FileOutputStream = ctx.openFileOutput(fileName, Context.MODE_APPEND)

    override fun write(string: String) {
        lock.withLock {
            fos.write(string.toByteArray())
        }
    }

    override fun writeBatch(queue: LinkedBlockingQueue<String>) {
        lock.withLock {
            while (queue.isNotEmpty()) {
                fos.write(queue.poll().toByteArray())
            }
        }
    }

    override fun delete() {
        lock.withLock {
            val del = file.delete()
            loga("File ${file.absolutePath} was deleted: $del")
        }
    }

    override fun emptyFile() {
        lock.withLock {
            fos.close()
            val del = file.delete()
            android.util.Log.i(TAG, "Deleting file: $del")
            fos = ctx.openFileOutput(fileName, Context.MODE_APPEND)
        }
    }

    /**
     * Closes associated file output stream
     */
    override fun closeOutputStream() = fos.close()

    override fun cleanFolder() {
        //switch to new file if needed
        loga("fileAge: " + file.fileAge())
        if (file.fileAge() > 0 || !file.name!!.contains(getFormattedFileNameDayNow())) {
            createNewFile()
        }

        //remove all zips
        ctx.filesDir.listFiles().removeAllZips()

        //remove all files older than x days
        ctx.filesDir.listFiles().removeAllOldFiles(maxDays)
    }

    private fun createNewFile() {
        fos.close()
        fileName = getFormattedFileNameForDayTemp() + "_day.log"
        loga("fileAge: $fileName")
        file = File(ctx.filesDir, fileName)
        fos = ctx.openFileOutput(fileName, Context.MODE_APPEND)
    }


}