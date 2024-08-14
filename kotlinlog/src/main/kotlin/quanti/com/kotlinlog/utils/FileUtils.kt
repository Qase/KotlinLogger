package quanti.com.kotlinlog.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit
import quanti.com.kotlinlog.Log.Companion.i
import quanti.com.kotlinlog.file.file.MetadataFile
import quanti.com.kotlinlog.forceWrite

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
fun File.getUriForFile(appCtx: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(appCtx, appCtx.packageName, this)
    } else{
        Uri.fromFile(this)
    }
}

/**
 * Copy zip of logs to sd card and returns path if successful or null if failed
 */
fun File.copyLogsToSDCard(context: Context, folderName: String): String? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            copyLogsToSDCardNewPostQ(context, folderName)
        } else {
            copyLogsToSDCardLegacyPreQ(folderName)
        }
    } catch (e: Exception) {
        null
    }
}

private fun File.copyLogsToSDCardLegacyPreQ(folderName: String): String? {
    val path = "${Environment.DIRECTORY_DOCUMENTS}/$folderName"
    val outputFile = Environment.getExternalStorageDirectory()
        ?.addPath(path, name)
        ?: return null

    outputFile.mkdirs()
    copyTo(outputFile, overwrite = true)

    return outputFile.absolutePath
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun File.copyLogsToSDCardNewPostQ(context: Context, folderName: String): String? {
    val resolver = context.contentResolver
    val volume = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val path = "${Environment.DIRECTORY_DOCUMENTS}/" + if (folderName.isNotBlank()) "$folderName/" else ""
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
        put(MediaStore.MediaColumns.RELATIVE_PATH, path)
    }

    val inputUri = Uri.fromFile(this)
    val outputUri: Uri? = getExistingFileUriOrNull(resolver, path, volume, name) ?: resolver.insert(volume, contentValues)

    if (outputUri != null) {
        resolver.openInputStream(inputUri).use { input ->
            // rwt mode should overwrite file if currently exists
            resolver.openOutputStream(outputUri,"rwt").use { output ->
                output?.let { input?.copyTo(it, DEFAULT_BUFFER_SIZE) }
            }
        }
        return "$path$name"
    }

    return null
}

@RequiresApi(Build.VERSION_CODES.Q)
fun getExistingFileUriOrNull(resolver: ContentResolver, relativePath: String, volumeUri: Uri, name: String): Uri? {
    val projection = arrayOf(
        MediaStore.MediaColumns._ID
    )

    val selection = "${MediaStore.MediaColumns.RELATIVE_PATH}='$relativePath' AND ${MediaStore.MediaColumns.DISPLAY_NAME}='$name'"

    resolver.query( volumeUri,
        projection, selection, null, null ).use { cursor ->
        if (cursor != null && cursor.count >= 1) {
            cursor.moveToFirst().let {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                return ContentUris.withAppendedId(volumeUri, id)
            }
        }
    }

    return null
}

/**
 * Create zip of all logs in current app directory
 *
 * @param appCtx application context
 * @param fileAge how many days backward you want to go - default 4
 * @param extraFiles extra files can be added to the zip file (for example screenshots from app)
 */

fun getZipOfLogs(appCtx: Context, fileAge: Int = 4, extraFiles: List<File> = arrayListOf()): File {
    val listOfFiles = getLogFiles(appCtx, fileAge).toMutableList()
    listOfFiles.addAll(extraFiles)

    val zipFileName = "Logs_${getFormattedFileNameForDayTemp()}_${appCtx.getApplicationName()}.zip"
    val zipFile = File(appCtx.logFilesDir, zipFileName)
    zipFile.createNewFile()  //create file if not exists
    listOfFiles.zip(zipFile)

    return zipFile
}

fun getLogFiles(appCtx: Context, fileAge: Int = 4): List<File> {
    //first perform clean of mess
    appCtx.logFilesDir.listFiles()?.deleteAllZips()
    appCtx.logFilesDir.listFiles()?.deleteAllOldFiles(fileAge)

    //create metadata info file
    MetadataFile(appCtx).apply {
        write()
        closeOutputStream()
    }

    loga("Force write")
    forceWrite()

    return appCtx.logFilesDir
        .listFiles()
        ?.filter { it.isFile } //take only files
        ?.filter { it.name.contains(".log", ignoreCase = true) }
        ?: listOf()
}
