package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.utils.fileAge
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Trnka Vladislav on 11.09.2017.
 *
 * Base file for log files
 */

abstract class BaseLogFile(
        ctx: Context,
        val name: String,
        maxDaysSaved: Int = 7
) {

    private val file: File = File(ctx.filesDir, name)
    internal val fos: FileOutputStream = ctx.openFileOutput(name, Context.MODE_PRIVATE)

    init {
        android.util.Log.i(TAG, "Creating new text file: " + file.absolutePath)
        removeAllOldTemps(ctx, maxDaysSaved)
    }

    fun getFileName() = name

    fun closeOutputStream() = fos.close()

    open fun write(string: String) {
        fos.write(string.toByteArray())
    }

    fun delete() {
        val del = file.delete()
        android.util.Log.i(TAG, "File ${file.absolutePath} was deleted: $del")
    }

    companion object {

        val TAG = "LogFile"

        fun removeAllOldTemps(ctx: Context, maxDaysSaved: Int) {
            //check old files and remove them
            ctx.filesDir.listFiles().filter {
                it.fileAge() > maxDaysSaved
            }.forEach {
                android.util.Log.i("FileLogger", "Deleting old temp file" + it.absolutePath + "\tSuccess: " + it.delete())
            }
        }
    }

}
