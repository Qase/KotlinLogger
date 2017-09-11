package quanti.com.kotlinlog.file

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.Toast
import quanti.com.kotlinlog.R
import quanti.com.kotlinlog.utils.getFormatedFileNameDayNow

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
        fun newInstance(
                sendEmailAddress: String,
                message: String = "Would you like to send logs by email or save them to SD card?",
                title: String = "Send logs",
                emailButtonText: String = "Email",
                fileButtonText: String = "Save"
        ) = newInstance(arrayOf(sendEmailAddress), message, title, emailButtonText, fileButtonText)

        @JvmOverloads
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


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val b = AlertDialog.Builder(context)

        b.setMessage(arguments.getString(MESSAGE))
        b.setTitle(arguments.getString(TITLE))
        b.setPositiveButton(arguments.getString(EMAIL_BUTTON_TEXT)) { _, _ -> this.positiveButtonClick() }
        b.setNeutralButton(arguments.getString(FILE_BUTTON_TEXT)) { _, _ -> this.neutralButtonClick() }
        return b.create()
    }

    /**
     * On positive button click
     * Creates zip of all logs and opens email client to send
     */
    private fun positiveButtonClick() {
        FileLoggerBase
                .getZipOfLogsUri(activity.applicationContext)
                .map {
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "message/rfc822"
                    i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    i.putExtra(Intent.EXTRA_EMAIL, arguments.getStringArray(SEND_EMAIL_ADDRESSES))
                    i.putExtra(
                            Intent.EXTRA_SUBJECT,
                            String.format(getString(R.string.logs_email_subject) + " " + getFormatedFileNameDayNow())
                    )

                    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.logs_email_text))

                    i.putExtra(Intent.EXTRA_STREAM, it)
                }
                .subscribe({
                    try {
                        startActivity(Intent.createChooser(it, "Send mail..."))
                    } catch (ex: android.content.ActivityNotFoundException) {
                        Toast.makeText(context, getString(R.string.logs_email_no_client_installed), Toast.LENGTH_LONG).show()
                    }
                }, {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                })
    }

    /**
     * On neutral button click
     * Copies ZIP of all logs to sd card
     */
    private fun neutralButtonClick() {
        FileLoggerBase
                .copyLogsToSDCard(activity.applicationContext)
                .subscribe({
                    Toast.makeText(
                            context,
                            "File successfully copied" + "\n" + it.absolutePath,
                            Toast.LENGTH_LONG
                    ).show()
                }, {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                })
    }
}
