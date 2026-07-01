package quanti.com.kotlinlog.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import java.io.File
import kotlinx.coroutines.launch
import quanti.com.kotlinlog.file.SendLogDialogCore
import quanti.com.kotlinlog.file.SendLogDialogOptions

/**
 * Single-address convenience overload. See the [List] overload for full documentation.
 */
@Composable
fun SendLogDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    sendEmailAddress: String,
    message: String = SendLogDialogOptions.DEFAULT_MESSAGE,
    title: String = SendLogDialogOptions.DEFAULT_TITLE,
    emailButtonText: String = SendLogDialogOptions.DEFAULT_EMAIL_BUTTON_TEXT,
    fileButtonText: String = SendLogDialogOptions.DEFAULT_FILE_BUTTON_TEXT,
    extraFiles: List<File> = emptyList(),
    dialogTheme: Int? = null,
    saveLogsDestinationDirName: String = SendLogDialogOptions.DEFAULT_SAVE_LOGS_DIR_NAME,
    maxFileAge: Int = SendLogDialogOptions.DEFAULT_MAX_FILE_AGE,
) = SendLogDialog(
    show = show,
    onDismissRequest = onDismissRequest,
    sendEmailAddresses = listOf(sendEmailAddress),
    message = message,
    title = title,
    emailButtonText = emailButtonText,
    fileButtonText = fileButtonText,
    extraFiles = extraFiles,
    dialogTheme = dialogTheme,
    saveLogsDestinationDirName = saveLogsDestinationDirName,
    maxFileAge = maxFileAge,
)

@Composable
fun SendLogDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    sendEmailAddresses: List<String>,
    message: String = SendLogDialogOptions.DEFAULT_MESSAGE,
    title: String = SendLogDialogOptions.DEFAULT_TITLE,
    emailButtonText: String = SendLogDialogOptions.DEFAULT_EMAIL_BUTTON_TEXT,
    fileButtonText: String = SendLogDialogOptions.DEFAULT_FILE_BUTTON_TEXT,
    extraFiles: List<File> = emptyList(),
    dialogTheme: Int? = null,
    saveLogsDestinationDirName: String = SendLogDialogOptions.DEFAULT_SAVE_LOGS_DIR_NAME,
    maxFileAge: Int = SendLogDialogOptions.DEFAULT_MAX_FILE_AGE,
) {
    val context = LocalContext.current

    // Captured unconditionally, before the `show` check below, so this scope isn't torn down
    // in the same frame `show` flips to false - otherwise the email/save coroutine launched
    // from a button click would be cancelled before it finishes.
    val coroutineScope = rememberCoroutineScope()
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    if (!show) return

    val options = remember(
        sendEmailAddresses, message, title, emailButtonText, fileButtonText,
        extraFiles, dialogTheme, saveLogsDestinationDirName, maxFileAge,
    ) {
        SendLogDialogOptions(
            sendEmailAddresses = sendEmailAddresses,
            message = message,
            title = title,
            emailButtonText = emailButtonText,
            fileButtonText = fileButtonText,
            extraFiles = extraFiles,
            dialogTheme = dialogTheme ?: 0,
            saveLogsDestinationDirName = saveLogsDestinationDirName,
            maxFileAge = maxFileAge,
        )
    }

    // Re-created every time `show` transitions to true (this block only runs while show ==
    // true, and re-keys on `options`), so the file-write-permission check inside
    // SendLogDialogCore.createDialog is re-evaluated fresh each time, matching the Fragment's
    // per-onCreateDialog check.
    DisposableEffect(context, options) {
        val dialog = SendLogDialogCore.createDialog(
            context = context,
            options = options,
            onPositiveButtonClick = { _, _ ->
                coroutineScope.launch { SendLogDialogCore.sendLogsByEmail(context, options) }
                // Do not call currentOnDismissRequest() here: AlertDialog auto-dismisses after
                // this listener runs, which triggers setOnDismissListener below - calling it
                // here too would fire onDismissRequest() twice.
            },
            onNeutralButtonClick = { _, _ ->
                coroutineScope.launch { SendLogDialogCore.saveLogsToSdCard(context, options) }
            },
        )
        dialog.setOnDismissListener { currentOnDismissRequest() }
        dialog.show()

        onDispose {
            dialog.setOnDismissListener(null)
            dialog.dismiss()
        }
    }
}
