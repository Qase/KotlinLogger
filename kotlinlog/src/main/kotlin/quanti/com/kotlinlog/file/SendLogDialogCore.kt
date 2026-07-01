package quanti.com.kotlinlog.file

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import quanti.com.kotlinlog.R
import quanti.com.kotlinlog.utils.copyLogsToSDCard
import quanti.com.kotlinlog.utils.getFormattedFileNameDayNow
import quanti.com.kotlinlog.utils.getUriForFile
import quanti.com.kotlinlog.utils.getZipOfLogs
import quanti.com.kotlinlog.utils.hasFileWritePermission

/**
 * Options for the send-logs dialog, shared by [SendLogDialogFragment] and the
 * Compose `SendLogDialog` (in the `:kotlinlog-compose` module).
 */
data class SendLogDialogOptions(
    val sendEmailAddresses: List<String>,
    val message: String = DEFAULT_MESSAGE,
    val title: String = DEFAULT_TITLE,
    val emailButtonText: String = DEFAULT_EMAIL_BUTTON_TEXT,
    val fileButtonText: String = DEFAULT_FILE_BUTTON_TEXT,
    val extraFiles: List<File> = emptyList(),
    val dialogTheme: Int = 0,
    val saveLogsDestinationDirName: String = DEFAULT_SAVE_LOGS_DIR_NAME,
    val maxFileAge: Int = DEFAULT_MAX_FILE_AGE,
) {
    companion object {
        const val DEFAULT_MESSAGE = "Would you like to send logs by email or save them to SD card?"
        const val DEFAULT_TITLE = "Send logs"
        const val DEFAULT_EMAIL_BUTTON_TEXT = "Email"
        const val DEFAULT_FILE_BUTTON_TEXT = "Save"
        const val DEFAULT_SAVE_LOGS_DIR_NAME = "KotlinLogger"
        const val DEFAULT_MAX_FILE_AGE = 4
    }
}

/**
 * Framework-agnostic implementation of the send-logs dialog. Only needs a [Context],
 * so it works from a Fragment, a plain Activity, or a Compose `ComponentActivity` alike.
 */
object SendLogDialogCore {

    fun createDialog(
        context: Context,
        options: SendLogDialogOptions,
        onPositiveButtonClick: DialogInterface.OnClickListener,
        onNeutralButtonClick: DialogInterface.OnClickListener,
    ): AlertDialog {
        val hasFilePermission = context.applicationContext.hasFileWritePermission()

        return AlertDialog.Builder(context, options.dialogTheme)
            .apply {
                setMessage(options.message)
                setTitle(options.title)
                setPositiveButton(options.emailButtonText, onPositiveButtonClick)

                if (hasFilePermission) {
                    setNeutralButton(options.fileButtonText, onNeutralButtonClick)
                }
            }.create()
    }

    /**
     * Zips the logs and opens the email chooser to send them; shows a toast if no
     * email client is installed.
     */
    suspend fun sendLogsByEmail(context: Context, options: SendLogDialogOptions) {
        val appContext = context.applicationContext

        val subject = appContext.getString(R.string.logs_email_subject) + " " + getFormattedFileNameDayNow()
        val bodyText = appContext.getString(R.string.logs_email_text)
        val zipFileUri = buildZipOfLogs(appContext, options).getUriForFile(appContext)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // email
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_EMAIL, options.sendEmailAddresses.toTypedArray())
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, bodyText)
            putExtra(Intent.EXTRA_STREAM, zipFileUri)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                appContext,
                appContext.getString(R.string.logs_email_no_client_installed),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Zips the logs and copies them to SD card, showing a result toast.
     */
    suspend fun saveLogsToSdCard(context: Context, options: SendLogDialogOptions) {
        val appContext = context.applicationContext

        val resultPath = buildZipOfLogs(appContext, options)
            .copyLogsToSDCard(context, options.saveLogsDestinationDirName)

        val text = if (resultPath == null) {
            "File copy failed"
        } else {
            "File successfully copied to\n$resultPath"
        }

        Toast.makeText(appContext, text, Toast.LENGTH_LONG).show()
    }

    private suspend fun buildZipOfLogs(context: Context, options: SendLogDialogOptions): File =
        withContext(Dispatchers.IO) {
            getZipOfLogs(context.applicationContext, options.maxFileAge, options.extraFiles)
        }
}
