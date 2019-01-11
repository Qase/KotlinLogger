package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.file.bundle.CircleLogBundle
import quanti.com.kotlinlog.utils.*
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Vladislav Trnka on 11.1.2019
 *
 *
 */



/**
 * it also starts new 5MB every day
 */
class CircleLogFile(
        ctx: Context,
        private val bundle: CircleLogBundle
) : AbstractLogFile(ctx) {


    override val logIdentifier: String = "circle"

    override var fileName: String = createNewFileName()
    override var file: File = File(ctx.filesDir, fileName)
    override var fos: FileOutputStream = ctx.openFileOutput(fileName, Context.MODE_APPEND)


    override fun createNewFileName(): String {
        var fileName = "${getFormattedFileNameDayNow()}_$logIdentifier.log"

        //now check if file exists
        if (File(ctx.filesDir, fileName).exists()) {
            fileName = "${getFormattedFileNameDayNow()}_${getRandomString(4)}_$logIdentifier.log"
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
        listOfLoggerFiles().deleteAllZips()

        //remove all files that exceeds specified limit
        listOfLoggerFiles()
                .sortByAge()
                .drop(bundle.numOfFiles)
                .deleteAll()
    }

}