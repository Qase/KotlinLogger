package quanti.com.kotlinlog.file

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import quanti.com.kotlinlog.R
import quanti.com.kotlinlog.utils.copyLogsToSDCard
import quanti.com.kotlinlog.utils.getFormattedFileNameDayNow
import quanti.com.kotlinlog.utils.getUriForFile
import quanti.com.kotlinlog.utils.getZipOfLogs
import quanti.com.kotlinlog.utils.hasFileWritePermission

/**
 * Created by Trnka Vladislav on 20.06.2017.
 *
 * Dialog that shows user options to save or send logs
 */

class SendLogDialogFragment : DialogFragment() {

    companion object {
        private const val MESSAGE = "send_message"
        private const val TITLE = "send_title"
        private const val EMAIL_BUTTON_TEXT = "email_button"
        private const val FILE_BUTTON_TEXT = "file_button"
        private const val SEND_EMAIL_ADDRESSES = "send_address"
        private const val EXTRA_FILES = "extra_files"
        private const val DIALOG_THEME = "dialog_theme"
        private const val SAVE_LOGS_DIR_NAME = "save_logs_dir_name"
        private const val MAX_FILE_AGE = "max_file_age"

        private const val DEFAULT_SAVE_LOGS_DIR_NAME = "KotlinLogger"
        private const val DEFAULT_MAX_FILE_AGE = 4

        @JvmOverloads
        @JvmStatic
        fun newInstance(
            sendEmailAddress: String,
            message: String = "Would you like to send logs by email or save them to SD card?",
            title: String = "Send logs",
            emailButtonText: String = "Email",
            fileButtonText: String = "Save",
            extraFiles: List<File> = arrayListOf(),
            dialogTheme: Int? = null,
            saveLogsDestinationDirName: String = DEFAULT_SAVE_LOGS_DIR_NAME,
            maxFileAge: Int = DEFAULT_MAX_FILE_AGE,
        ) = newInstance(
            arrayOf(sendEmailAddress),
            message,
            title,
            emailButtonText,
            fileButtonText,
            extraFiles,
            dialogTheme,
            saveLogsDestinationDirName,
            maxFileAge
        )

        @JvmOverloads
        @JvmStatic
        fun newInstance(
            sendEmailAddress: Array<String>,
            message: String = "Would you like to send logs by email or save them to SD card?",
            title: String = "Send logs",
            emailButtonText: String = "Email",
            fileButtonText: String = "Save",
            extraFiles: List<File> = arrayListOf(),
            dialogTheme: Int? = null,
            saveLogsDestinationDirName: String = DEFAULT_SAVE_LOGS_DIR_NAME,
            maxFileAge: Int = DEFAULT_MAX_FILE_AGE,
        ): SendLogDialogFragment {
            val myFragment = SendLogDialogFragment()

            val args = Bundle()
            args.putString(MESSAGE, message)
            args.putString(TITLE, title)
            args.putString(EMAIL_BUTTON_TEXT, emailButtonText)
            args.putString(FILE_BUTTON_TEXT, fileButtonText)
            args.putStringArray(SEND_EMAIL_ADDRESSES, sendEmailAddress)
            args.putSerializable(EXTRA_FILES, ArrayList(extraFiles))
            args.putString(SAVE_LOGS_DIR_NAME, saveLogsDestinationDirName)
            args.putInt(MAX_FILE_AGE, maxFileAge)
            if (dialogTheme != null) {
                args.putInt(DIALOG_THEME, dialogTheme)
            }

            myFragment.arguments = args

            return myFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hasFilePermission = requireActivity().applicationContext.hasFileWritePermission()

        return AlertDialog
            .Builder(requireContext(), requireArguments().getInt(DIALOG_THEME))
            .apply {
                setMessage(requireArguments().getString(MESSAGE))
                setTitle(requireArguments().getString(TITLE))
                setPositiveButton(
                    requireArguments().getString(EMAIL_BUTTON_TEXT),
                    this@SendLogDialogFragment::positiveButtonClick
                )

                if (hasFilePermission) {
                    setNeutralButton(
                        requireArguments().getString(FILE_BUTTON_TEXT),
                        this@SendLogDialogFragment::neutralButtonClick
                    )
                }
            }.create()
    }

    /**
     * On positive button click
     * Create zip of all logs and open email client to send
     */
    @Suppress("UNUSED_PARAMETER")
    private fun positiveButtonClick(dialog: DialogInterface, which: Int) =
        runBlocking {
            val appContext = this@SendLogDialogFragment.requireContext().applicationContext

            val addresses = requireArguments().getStringArray(SEND_EMAIL_ADDRESSES)
            val subject = getString(R.string.logs_email_subject) + " " + getFormattedFileNameDayNow()
            val bodyText = getString(R.string.logs_email_text)
            val zipFileUri = getZipFileDeferred().await().getUriForFile(appContext)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822" // email
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra(Intent.EXTRA_EMAIL, addresses)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, bodyText)
                putExtra(Intent.EXTRA_STREAM, zipFileUri)
            }

            try {
                startActivity(Intent.createChooser(intent, "Send mail..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(
                    appContext,
                    getString(R.string.logs_email_no_client_installed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    /**
     * On neutral button click
     * Copy ZIP of all logs to sd card
     */
    @Suppress("UNUSED_PARAMETER")
    private fun neutralButtonClick(dialog: DialogInterface, which: Int) =
        runBlocking {
            val appContext = this@SendLogDialogFragment.requireContext().applicationContext

            val destinationDir = requireArguments().getString(SAVE_LOGS_DIR_NAME)
            val resultPath = getZipFileDeferred().await().copyLogsToSDCard(requireContext(), destinationDir ?: DEFAULT_SAVE_LOGS_DIR_NAME)

            val text = if (resultPath == null) {
                "File copy failed"
            } else {
                "File successfully copied to\n$resultPath"
            }

            Toast.makeText(
                appContext,
                text,
                Toast.LENGTH_LONG
            ).show()
        }

    private fun getZipFileDeferred(): Deferred<File> {
        return CoroutineScope(Dispatchers.IO).async {
            val extraFiles = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireArguments().getSerializable(EXTRA_FILES, ArrayList::class.java)
            } else {
                @Suppress("DEPRECATION")
                requireArguments().getSerializable(EXTRA_FILES)
            } as ArrayList<File>
            val maxFileAge = requireArguments().getInt(MAX_FILE_AGE)
            getZipOfLogs(requireActivity().applicationContext, maxFileAge, extraFiles)
        }
    }
}
