package quanti.com.kotlinlog3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.compose.SendLogDialog
import quanti.com.kotlinlog.file.FileLogger
import quanti.com.kotlinlog.file.bundle.DayLogBundle

/**
 * Plain ComponentActivity (no Fragments/AppCompatActivity) demonstrating that
 * SendLogDialog works without a FragmentManager - reproduces the scenario that used
 * to crash SendLogDialogFragment with "requires a FragmentActivity context".
 */
class ComposeDemoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLog()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var showSendLogDialog by rememberSaveable { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Button(onClick = { showSendLogDialog = true }) {
                            Text("Send logs")
                        }
                    }

                    SendLogDialog(
                        show = showSendLogDialog,
                        onDismissRequest = { showSendLogDialog = false },
                        sendEmailAddress = "kidal5@centrum.cz",
                    )
                }
            }
        }
    }

    private fun initLog() {
        Log.initialise(applicationContext)
        Log.addLogger(FileLogger(applicationContext, DayLogBundle()))
    }
}
