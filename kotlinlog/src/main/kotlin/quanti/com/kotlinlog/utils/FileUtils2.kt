package quanti.com.kotlinlog.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import java.io.File
import java.io.FileNotFoundException

/**
 * Get URI to zip of logs
 * @param appCtx application context
 * @param fileAge how many days backward you want to go - default 4
 */

fun getZipOfLogsUri(appCtx: Context, fileAge: Int = 4): Uri {
    val zipFile = getZipOfLogs(appCtx, fileAge)
    return FileProvider.getUriForFile(appCtx, appCtx.packageName, zipFile)
}

private fun getZipOfLogs(appCtx: Context, fileAge: Int): File {
    //first perform clean of mess
    appCtx.filesDir.listFiles().deleteAllZips()
    appCtx.filesDir.listFiles().deleteAllOldFiles(fileAge)

    if (appCtx.filesDir.listFiles().isEmpty()) {
        throw FileNotFoundException("No files were found")
    }

    val zipFileName = "Logs_${getFormattedFileNameForDayTemp()}_${appCtx.getApplicationName()}.zip"
    val zipFile = File(appCtx.filesDir, zipFileName)
    zipFile.createNewFile()  //create file if not exists

    appCtx.filesDir
            .listFiles()
            .filter { it.isFile } //take only files
            .filter { it.name.contains(".log", ignoreCase = true) } //take only .log
            .zip(zipFile)
    return zipFile
}


/**
 * Copy zip of logs to sd card
 *
 * @param appCtx application context
 * @param fileAge how many days backward you want to go - default 4
 * @param sdCardFolderName name of folder in sd card - default KotlinLogger
 */
fun copyLogsToSDCard(appCtx: Context, fileAge: Int = 4, sdCardFolderName: String = "KotlinLogger"): File {
    val zipFile = getZipOfLogs(appCtx, fileAge)
    val outputFolder = File(Environment.getExternalStorageDirectory(), sdCardFolderName)
    val outputFile = File(outputFolder, zipFile.name)


    zipFile.copyTo(outputFile, overwrite = true)

    return zipFile


}


