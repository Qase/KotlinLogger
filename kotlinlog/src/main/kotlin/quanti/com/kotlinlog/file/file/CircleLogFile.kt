package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.file.bundle.CircleLogBundle
import quanti.com.kotlinlog.utils.*
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Vladislav Trnka on 11.1.2019
 *
 * File logger that creates new file when old file size limit is reached
 * When maximum files is reached then oldest used file is deleted
 *
 * Maximum file limit can be overflowed, check @see StrictCircleLogFile for better but slower solution
 */

class CircleLogFile(
        ctx: Context,
        private val bundle: CircleLogBundle
) : AbstractLogFile(ctx) {


    override val logIdentifier: String = "circle"

    override var fileName: String = createNewFileName()
    override var file: File = File(ctx.logFilesDir, fileName)
    override var fos: FileOutputStream = ctx.openLogFileOutput(fileName, true)


    override fun createNewFileName(): String {
        var fileName = "${getFormattedFileNameDayNowWithSeconds()}_$logIdentifier.log"

        //now check if file exists
        if (File(ctx.logFilesDir, fileName).exists()) {
            fileName = "${getFormattedFileNameDayNowWithSeconds()}_${getRandomString(4)}_$logIdentifier.log"
        }

        loga("New filename $fileName")
        return fileName
    }

    override fun cleanFolder() {
        //find if my current file has more the fileSize MB
        if (file.length() > bundle.maxFileSize) {
            //create new file
            createNewFile()
        }

        //remove all zips
        listOfLoggerFiles()?.deleteAllZips()

        //remove all files that exceeds specified limit
        listOfLoggerFiles()
                ?.sortByAge()
                ?.drop(bundle.numOfFiles)
                ?.deleteAll()
    }

}