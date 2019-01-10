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

/**
 * Returns how old is this file
 * -1 --> created tomorrow
 * 0  --> created today
 * 1  --> created yesterday
 * and so on
 */
fun File.fileAge(): Int {
    if (!exists()) {
        //file does not exists
        throw FileNotFoundException("File does not exists $absolutePath")
    }

    //i do not want the basic difference
    //i want
    val todayMillis = ActualTime.currentDate().time
    val fileMillis = lastModified()

    val todayDay = TimeUnit.DAYS.convert(todayMillis, TimeUnit.MILLISECONDS)
    val fileDay = TimeUnit.DAYS.convert(fileMillis, TimeUnit.MILLISECONDS)
    return (todayDay - fileDay).toInt()
}


fun Array<File>.zip(zipFile: File): File {
    if (isEmpty()) {
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

@Suppress("ConstantConditionIf")
fun mergeToFile(from: String, to: String, ctx: Context): Boolean {

    val fromFile = File(ctx.filesDir, from)

    if (!fromFile.existsAndIsFile()) {
        loga("Cannot merge files - this file does not exists " + fromFile.absoluteFile)
    }

    loga("MERGE")

    val fis = ctx.openFileInput(from)
    val fos = ctx.openFileOutput(to, android.content.Context.MODE_APPEND)

    fis.copyTo(fos)

    fis.close()
    fos.close()

    return true
}


/**
 * Removes all files that are older than specified number of days inclusive
 */
fun Array<File>.removeAllOldFiles(maxDaysOld: Int) = removeAll { it.fileAge() > maxDaysOld }

/**
 * Removes all zip files
 */
fun Array<File>.removeAllZips() = removeAll { it.name.contains(".zip") }

fun Array<File>.removeAll(filter: (File) -> Boolean): Array<File> {
    this.filter(filter)
            .forEach {
                val del = it.delete()
                loga("File deleted: " + del + "/t" + it.absolutePath)
            }
    return this
}

fun Array<File>.sortByAge(): Array<File> {
    this.sortWith(comparator)
    return this
}

private val comparator = Comparator<File> { o1, o2 ->
    val firstTime = Date(o1.lastModified()).time
    val secondTime = Date(o2.lastModified()).time

    return@Comparator (secondTime - firstTime).toInt()
}
