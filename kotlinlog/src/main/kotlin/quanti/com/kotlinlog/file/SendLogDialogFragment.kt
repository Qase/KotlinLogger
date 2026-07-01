package quanti.com.kotlinlog.file

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.io.File
import kotlinx.coroutines.runBlocking

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

        private const val DEFAULT_SAVE_LOGS_DIR_NAME = SendLogDialogOptions.DEFAULT_SAVE_LOGS_DIR_NAME
        private const val DEFAULT_MAX_FILE_AGE = SendLogDialogOptions.DEFAULT_MAX_FILE_AGE

        @JvmOverloads
        @JvmStatic
        fun newInstance(
            sendEmailAddress: String,
            message: String = SendLogDialogOptions.DEFAULT_MESSAGE,
            title: String = SendLogDialogOptions.DEFAULT_TITLE,
            emailButtonText: String = SendLogDialogOptions.DEFAULT_EMAIL_BUTTON_TEXT,
            fileButtonText: String = SendLogDialogOptions.DEFAULT_FILE_BUTTON_TEXT,
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
            message: String = SendLogDialogOptions.DEFAULT_MESSAGE,
            title: String = SendLogDialogOptions.DEFAULT_TITLE,
            emailButtonText: String = SendLogDialogOptions.DEFAULT_EMAIL_BUTTON_TEXT,
            fileButtonText: String = SendLogDialogOptions.DEFAULT_FILE_BUTTON_TEXT,
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
        return SendLogDialogCore.createDialog(
            context = requireContext(),
            options = optionsFromArguments(),
            onPositiveButtonClick = this::positiveButtonClick,
            onNeutralButtonClick = this::neutralButtonClick,
        )
    }

    private fun optionsFromArguments(): SendLogDialogOptions {
        val args = requireArguments()

        val extraFiles = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getSerializable(EXTRA_FILES, ArrayList::class.java)
        } else {
            @Suppress("DEPRECATION")
            args.getSerializable(EXTRA_FILES)
        } as ArrayList<File>

        return SendLogDialogOptions(
            sendEmailAddresses = args.getStringArray(SEND_EMAIL_ADDRESSES)?.toList().orEmpty(),
            message = args.getString(MESSAGE) ?: SendLogDialogOptions.DEFAULT_MESSAGE,
            title = args.getString(TITLE) ?: SendLogDialogOptions.DEFAULT_TITLE,
            emailButtonText = args.getString(EMAIL_BUTTON_TEXT) ?: SendLogDialogOptions.DEFAULT_EMAIL_BUTTON_TEXT,
            fileButtonText = args.getString(FILE_BUTTON_TEXT) ?: SendLogDialogOptions.DEFAULT_FILE_BUTTON_TEXT,
            extraFiles = extraFiles,
            dialogTheme = args.getInt(DIALOG_THEME),
            saveLogsDestinationDirName = args.getString(SAVE_LOGS_DIR_NAME) ?: SendLogDialogOptions.DEFAULT_SAVE_LOGS_DIR_NAME,
            maxFileAge = args.getInt(MAX_FILE_AGE, SendLogDialogOptions.DEFAULT_MAX_FILE_AGE),
        )
    }

    /**
     * On positive button click
     * Create zip of all logs and open email client to send
     */
    @Suppress("UNUSED_PARAMETER")
    private fun positiveButtonClick(dialog: DialogInterface, which: Int) =
        runBlocking {
            SendLogDialogCore.sendLogsByEmail(requireContext(), optionsFromArguments())
        }

    /**
     * On neutral button click
     * Copy ZIP of all logs to sd card
     */
    @Suppress("UNUSED_PARAMETER")
    private fun neutralButtonClick(dialog: DialogInterface, which: Int) =
        runBlocking {
            SendLogDialogCore.saveLogsToSdCard(requireContext(), optionsFromArguments())
        }
}
