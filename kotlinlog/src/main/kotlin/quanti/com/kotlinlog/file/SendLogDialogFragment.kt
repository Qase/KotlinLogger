package quanti.com.kotlinlog.file

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.Toast
import kotlinx.coroutines.*
import quanti.com.kotlinlog.R
import quanti.com.kotlinlog.utils.*
import java.io.File

/**
 * Created by Trnka Vladislav on 20.06.2017.
 *
 * Dialog that shows user options to save or send logs
 */

class SendLogDialogFragment : DialogFragment() {

    companion object {
        const val MESSAGE = "send_message"
        const val TITLE = "send_title"
        const val EMAIL_BUTTON_TEXT = "email_button"
        const val FILE_BUTTON_TEXT = "file_button"
        const val SEND_EMAIL_ADDRESSES = "send_address"

        @JvmOverloads
        @JvmStatic
        fun newInstance(
                sendEmailAddress: String,
                message: String = "Would you like to send logs by email or save them to SD card?",
                title: String = "Send logs",
                emailButtonText: String = "Email",
                fileButtonText: String = "Save"
        ) = newInstance(arrayOf(sendEmailAddress), message, title, emailButtonText, fileButtonText)

        @JvmOverloads
        @JvmStatic
        fun newInstance(
                sendEmailAddress: Array<String>,
                message: String = "Would you like to send logs by email or save them to SD card?",
                title: String = "Send logs",
                emailButtonText: String = "Email",
                fileButtonText: String = "Save"
        ): SendLogDialogFragment {
            val myFragment = SendLogDialogFragment()

            val args = Bundle()
            args.putString(MESSAGE, message)
            args.putString(TITLE, title)
            args.putString(EMAIL_BUTTON_TEXT, emailButtonText)
            args.putString(FILE_BUTTON_TEXT, fileButtonText)
            args.putStringArray(SEND_EMAIL_ADDRESSES, sendEmailAddress)

            myFragment.arguments = args

            return myFragment
        }
    }

    var zipFile: Deferred<File>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zipFile = GlobalScope.async { getZipOfLogs(activity!!.applicationContext) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hasFilePermission = activity!!.applicationContext.hasFileWritePermission()

        return AlertDialog
                .Builder(context!!)
                .apply {
                    setMessage(arguments!!.getString(MESSAGE))
                    setTitle(arguments!!.getString(TITLE))
                    setPositiveButton(arguments!!.getString(EMAIL_BUTTON_TEXT), this@SendLogDialogFragment::positiveButtonClick)

                    if (hasFilePermission) {
                        setNeutralButton(arguments!!.getString(FILE_BUTTON_TEXT), this@SendLogDialogFragment::neutralButtonClick)
                    }
                }.create()
    }

    /**
     * On positive button click
     * Create zip of all logs and open email client to send
     */
    private fun positiveButtonClick(dialog: DialogInterface, which: Int) = runBlocking {

        val appContext = this@SendLogDialogFragment.context!!.applicationContext

        val addresses = arguments!!.getStringArray(SEND_EMAIL_ADDRESSES)
        val subject = getString(R.string.logs_email_subject) + " " + getFormattedFileNameDayNow()
        val bodyText = getString(R.string.logs_email_text)

        //await non block's current thread
        val zipFileUri = zipFile?.await()?.getUriForFile(appContext)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" //email
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, bodyText)
            putExtra(Intent.EXTRA_STREAM, zipFileUri)
        }

        try {
            startActivity(Intent.createChooser(intent, "Send mail..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(appContext, getString(R.string.logs_email_no_client_installed), Toast.LENGTH_LONG).show()
        }

    }

    /**
     * On neutral button click
     * Copy ZIP of all logs to sd card
     */
    private fun neutralButtonClick(dialog: DialogInterface, which: Int) {

        val appContext = this@SendLogDialogFragment.context!!.applicationContext

        GlobalScope.launch(Dispatchers.IO) {
            val file = zipFile!!.await().copyLogsTOSDCard()
            launch(Dispatchers.Main) {
                Toast.makeText(appContext, "File successfully copied" + "\n" + file.absolutePath, Toast.LENGTH_LONG).show()
            }
        }
    }
}
