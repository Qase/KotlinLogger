package quanti.com.kotlinlog.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.support.v4.content.FileProvider
import quanti.com.kotlinlog.Log.Companion.i
import quanti.com.kotlinlog.file.file.CrashLogFile
import quanti.com.kotlinlog.file.file.MetadataFile
import quanti.com.kotlinlog.forceWrite
import java.io.*
import java.util.concurrent.TimeUnit

/**
 * File extensions written in kotlin
 *
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
    val ret = (todayDay - fileDay).toInt()

    loga(name, ret)
    return ret
}

/**
 * Add children path to current filepath
 */
fun File.addPath(vararg childrenPath :String): File {

    var file = this
    childrenPath.forEach {
        file = File(file, it)
    }

    return file
}


/**
 * Get shareable uri fo current file
 */
fun File.getUriForFile(appCtx: Context) = FileProvider.getUriForFile(appCtx, appCtx.packageName, this)

/**
 * Copy zip of logs to sd card
 */
fun File.copyLogsTOSDCard(sdCardFolderName: String = "KotlinLogger"): File {
    val outputFile = Environment
            .getExternalStorageDirectory()
            .addPath(sdCardFolderName, name)

    copyTo(outputFile, overwrite = true)

    return outputFile
}

/**
 * Create zip of all logs in current app directory
 *
 * @param appCtx application context
 * @param fileAge how many days backward you want to go - default 4
 */
fun getZipOfLogs(appCtx: Context, fileAge: Int = 4): File {
    //first perform clean of mess
    appCtx.logFilesDir.listFiles().deleteAllZips()
    appCtx.logFilesDir.listFiles().deleteAllOldFiles(fileAge)

    if (appCtx.logFilesDir.listFiles().isEmpty()) {
        throw FileNotFoundException("No files were found")
    }

    //create metadata info file
    MetadataFile(appCtx).apply {
        write()
        closeOutputStream()
    }

    loga("Force write")
    forceWrite()

    val zipFileName = "Logs_${getFormattedFileNameForDayTemp()}_${appCtx.getApplicationName()}.zip"
    val zipFile = File(appCtx.logFilesDir, zipFileName)
    zipFile.createNewFile()  //create file if not exists

    appCtx.logFilesDir
            .listFiles()
            .filter { it.isFile } //take only files
            .filter { it.name.contains(".log", ignoreCase = true) } //take only .log
            .zip(zipFile)
    return zipFile
}

