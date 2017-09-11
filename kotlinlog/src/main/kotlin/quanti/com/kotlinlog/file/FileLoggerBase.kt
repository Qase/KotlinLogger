package quanti.com.kotlinlog.file

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.getLogLevelString
import quanti.com.kotlinlog.utils.*
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException

/**
 * Created by Trnka Vladislav on 11.09.2017.
 *
 * TODO: description
 */

abstract class FileLoggerBase @JvmOverloads constructor(
        protected var ctx: Context,
        protected var bun: FileLoggerBundle = FileLoggerBundle()
): ILogger {

    protected var actualLogFile: LogFile

    init {
        //check permission
        if (!ctx.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            android.util.Log.e("FileLogger", "Give me write external storage permission to use this logger")
            throw SecurityException("Give me ${Manifest.permission.WRITE_EXTERNAL_STORAGE}")
        }

        LogFile.removeAllOldTemps(ctx, bun.maxDaysSaved)

        actualLogFile = LogFile(ctx, bun)
    }

    protected fun getDayTemp() = "${getFormattedFileNameForDayTemp()}_daytemp.log"

    /**
     * Returns android-log like formatted string
     */
    protected fun getFormatedString(logLevel: Int, tag: String, methodName: String, text: String): String {
        val s = "%s/%s %s/%s:\t%s\n" //date/class  LOG_TYPE/method name: text
        return String.format(
                s,
                getFormatedNow(),
                tag,
                logLevel.getLogLevelString(),
                methodName,
                text
        )
    }

    /**
     * Static methods
     * They do not exactly match ILogger interface
     */
    companion object {

        /**
         * Get URI to zip of logs
         * @param appCtx application context
         * @param fileAge how many days backward you want to go - default 4
         */
        fun getZipOfLogsUri(appCtx: Context, fileAge: Int = 4): Observable<Uri> {
            return getZipOfLogs(appCtx, fileAge)
                    .map {
                        FileProvider.getUriForFile(appCtx, appCtx.packageName, it) //todo this
                    }
                    .observeOn(AndroidSchedulers.mainThread())
        }


        /**
         * Copy zip of logs to sd card
         *
         * @param appCtx application context
         * @param fileAge how many days backward you want to go - default 4
         * @param sdCardFolderName name of folder in sd card - default KotlinLogger
         */
        fun copyLogsToSDCard(appCtx: Context, fileAge: Int = 4, sdCardFolderName: String = "KotlinLogger"): Observable<File> {
            return getZipOfLogs(appCtx, fileAge)
                    .map {
                        val dir = File(Environment.getExternalStorageDirectory(), sdCardFolderName)
                        dir.mkdirs()
                        val output = File(dir, it.name)
                        it.copyTo(output, true)
                        return@map output
                    }
                    .map {
                        it.scanFile(appCtx)
                        return@map it
                    }
                    .observeOn(AndroidSchedulers.mainThread())
        }

        private fun getZipOfLogs(appCtx: Context, fileAge: Int): Observable<File> {
            val listFiles = appCtx.filesDir.listFiles(FileFilter {
                it.name.endsWith(".log") and (it.fileAge() <= fileAge)
            })

            if (listFiles.isEmpty()) {
                return Observable.error { FileNotFoundException("No files were found") }
            }

            val zipFile = File(appCtx.filesDir, "${getFormattedFileNameForDayTemp()}.zip")
            zipFile.createNewFile()  //create file if not exists

            return Observable.just(zipFile).map { listFiles.zip(it) }.observeOn(AndroidSchedulers.mainThread())
        }
    }



}