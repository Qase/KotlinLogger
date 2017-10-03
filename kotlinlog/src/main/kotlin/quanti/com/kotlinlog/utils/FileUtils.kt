package quanti.com.kotlinlog.utils

import android.content.Context
import android.media.MediaScannerConnection
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.Log.Companion.i
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 *
 * File extensions written in kotlin
 *
 * @author Sylvain Berfini
 * @author Vladislav Trnka
 */

/**
 * Scan file system to make file available
 * according to this {@see http://developer.android.com/reference/android/os/Environment.html#getExternalStoragePublicDirectory(java.lang.String)}
 * @param ctx  app context
 */
fun File.scanFile(ctx: Context) {
    scanFiles(ctx, this.toString())
}

/**
 * Scan file system to make file available
 * according to this {@see http://developer.android.com/reference/android/os/Environment.html#getExternalStoragePublicDirectory(java.lang.String)}
 * @param ctx  app context
 * @param args File.tostrings(0)
 */
private fun scanFiles(ctx: Context, vararg args: String) {
    MediaScannerConnection.scanFile(ctx, args, null) { path, _ -> i("Scanned $path:") }
}

fun File.fileAge(): Int {
    if (!exists()){
        //file does not exists
        throw FileNotFoundException("File does not exists $absolutePath")
    }

    val today = ActualTime.currentDate()
    val fDate = Date(this.lastModified())

    val diff = today.time - fDate.time
    val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
    //android.util.Log.i("FileUtils", this.name + "\t" + this.lastModified() + "\t" + days)
    return days
}

fun Array<File>.zip(zipFile: File): File {
    if (isEmpty()){
        return zipFile
    }
    try {
        val dest = FileOutputStream(zipFile)
        val out = ZipOutputStream(BufferedOutputStream(dest))

        for (i in this.indices) {
            //Log.v("Adding: " + files[i])
            val fi = FileInputStream(this[i])
            val entry = ZipEntry(this[i].name)
            out.putNextEntry(entry)

            fi.copyTo(out)
            fi.close()
        }
        out.close()
    } catch (ex: Exception) {
        Log.e("Zipping error", ex)
    }

    return zipFile
}

fun File.existsAndIsFile() = exists() && isFile

fun mergeToFile(from: String, to: String, ctx: Context): Boolean {

    val fromFile = File(ctx.filesDir, from)

    if (!fromFile.existsAndIsFile()) {
        android.util.Log.i("FileLogger", "Cannot merge files - this file does not exists " + fromFile.absoluteFile)
        return false
    }

    android.util.Log.i("Tek", "MERGE")

    val fis = ctx.openFileInput(from)
    val fos = ctx.openFileOutput(to, android.content.Context.MODE_APPEND)

    fis.copyTo(fos)

    fis.close()
    fos.close()

    return true
}
