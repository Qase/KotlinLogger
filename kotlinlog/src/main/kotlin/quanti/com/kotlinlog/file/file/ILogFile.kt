package quanti.com.kotlinlog.file.file

import java.util.concurrent.LinkedBlockingQueue


interface ILogFile{

    /**
     * Write string to associated file
     */
    fun write(string: String)

    /**
     * Write whole queue of strings to associated file
     */
    fun writeBatch(queue: LinkedBlockingQueue<String>)

    /**
     * Delete file
     */
    fun delete()

    /**
     * Empty file, delete it and prepare new file for writing
     */
    fun emptyFile()

    /**
     * Closes associated file output stream
     */
    fun closeOutputStream()

    /**
     * Clean folder from old logs
     */
    fun cleanFolder()


}