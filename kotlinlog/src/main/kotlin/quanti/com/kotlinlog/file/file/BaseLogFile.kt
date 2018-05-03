package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.DEBUG_LIBRARY
import quanti.com.kotlinlog.file.base.FileLoggerBase
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by Trnka Vladislav on 11.09.2017.
 *
 * Base file for log files
 */

val TAG = "LogFile"

abstract class BaseLogFile(
        private val ctx: Context,
        val name: String,
        maxDaysSaved: Int = 3
) {

    private val file: File = File(ctx.filesDir, name)
    internal var fos: FileOutputStream = ctx.openFileOutput(name, Context.MODE_APPEND)

    init {
        if (DEBUG_LIBRARY) {
            android.util.Log.i(TAG, "Creating new text file: " + file.absolutePath)
        }
        FileLoggerBase.removeAllOldTemps(ctx, maxDaysSaved)
    }

    fun getFileName() = name

    fun closeOutputStream() = fos.close()

    open fun write(string: String) {
        fos.write(string.toByteArray())
    }

    open fun writeBatch(queue: LinkedBlockingQueue<String>){
        while (queue.isNotEmpty()) {
            write(queue.poll())
        }
    }

    open fun delete() {
        val del = file.delete()
        if (DEBUG_LIBRARY) {
            android.util.Log.i(TAG, "File ${file.absolutePath} was deleted: $del")
        }
    }

    open fun emptyFile() {
        fos.close()
        val del = file.delete()
        android.util.Log.i(TAG, "Deleting file: $del")
        fos = ctx.openFileOutput(name, Context.MODE_APPEND)
    }

}
