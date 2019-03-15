package quanti.com.kotlinlog.file.file

import android.content.Context
import quanti.com.kotlinlog.android.MetadataLogger
import quanti.com.kotlinlog.utils.openLogFileOutput
import java.io.FileOutputStream

class MetadataFile(appCtx: Context) {

    private val fileName = "PhoneMetadata.log"

    private var fos: FileOutputStream = appCtx.openLogFileOutput(fileName, false)

    private val metadata = MetadataLogger.getLogStrings(appCtx.applicationContext)


    fun write(){
        fos.write(metadata.toByteArray())
    }

    fun closeOutputStream() = fos.close()
}