@file:Suppress("UNUSED_PARAMETER", "FunctionName")

package quanti.com.kotlinlog3

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.android.AndroidLogger
import quanti.com.kotlinlog.android.MetadataLogger
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.base.getLogLevel
import quanti.com.kotlinlog.crashlytics.CrashlyticsLogger
import quanti.com.kotlinlog.file.FileLogger
import quanti.com.kotlinlog.file.SendLogDialogFragment
import quanti.com.kotlinlog.file.bundle.BaseBundle
import quanti.com.kotlinlog.file.bundle.CircleLogBundle
import quanti.com.kotlinlog.file.bundle.DayLogBundle
import quanti.com.kotlinlog.file.bundle.StrictCircleLogBundle
import quanti.com.kotlinlog.weblogger.WebLogger
import quanti.com.kotlinlog.weblogger.bundle.RestLoggerBundle
import quanti.com.kotlinlog.weblogger.bundle.WebSocketLoggerBundle
import quanti.com.kotlinlog.weblogger.rest.IServerActive
import java.text.SimpleDateFormat
import java.util.Date

const val REQUEST = 98
const val RANDOM_TEXT = "qwertyuiop"

const val TAG_FILELOGGER = "tfl"
const val TAG_RESTLOGGER = "trl"
const val TAG_WSLOGGER = "twsl"

class MainActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener, IServerActive {

    private var checked = 3

    private var bundle: BaseBundle = StrictCircleLogBundle()


    private val restButtonCallback = View.OnClickListener {

        val editText = findViewById<EditText>(R.id.apiServer_editText)
        val url = getTextFrom(editText, false)
        val restLoggerBundle = RestLoggerBundle(url, this)
        try {
            Log.addLogger(WebLogger(restLoggerBundle), TAG_RESTLOGGER)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private val wsButtonCallback = View.OnClickListener {
        val editText = findViewById<EditText>(R.id.wsServer_editText)
        val url = getTextFrom(editText, true)
        val wsLoggerBundle = WebSocketLoggerBundle(url, this)
        try {
            Log.addLogger(WebLogger(wsLoggerBundle), TAG_WSLOGGER)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private val switchButtonLongClickCallback = View.OnLongClickListener {
        Log.removeLogger(TAG_FILELOGGER)
        (findViewById<TextView>(R.id.loggerInUseTextView)).text = "Disabled"
        return@OnLongClickListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLog()

        findViewById<RadioGroup>(R.id.radioGroup).setOnCheckedChangeListener(this)
        findViewById<Button>(R.id.apiServer_button).setOnClickListener(restButtonCallback)
        findViewById<Button>(R.id.wsServer_button).setOnClickListener(wsButtonCallback)
        findViewById<Button>(R.id.switchButton).setOnLongClickListener(switchButtonLongClickCallback)
    }

    override fun isServerActive(isActive: Boolean) {
        val text = if (isActive) {
            "Successfully connected"
        } else {
            "Connection error, have you written address correctly?"
        }
        runOnUiThread {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
    }


    private fun initLog() {
        Log.initialise(this.applicationContext)
        Log.useUncheckedErrorHandler()
        Log.addLogger(AndroidLogger())

        // No need initialize Firebase Crashlytics. It is initialized automatically by
        // com.google.firebase.crashlytics plugin and google-services.json
        Log.addLogger(CrashlyticsLogger())

        switchLogger_clicked(null)
    }

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {
        val text = group.findViewById<RadioButton>(checkedId).text.toString()
        checked = text.getLogLevel()
    }

    fun hitme_clicked(view: View) {
        when (checked) {
            LogLevel.DEBUG -> Log.d(RANDOM_TEXT)
            LogLevel.ERROR -> Log.e(RANDOM_TEXT)
            LogLevel.INFO -> Log.i(RANDOM_TEXT)
            LogLevel.VERBOSE -> Log.v(RANDOM_TEXT)
            LogLevel.WARN -> Log.w(RANDOM_TEXT)
        }
    }

    @SuppressLint("CheckResult")
    fun testone_clicked(view: View) {
        val end = 50

        Flowable
                .range(0, end)
                .map { it.toString() }
                .subscribe { Log.i(it) }


    }

    fun testtwo_clicked(view: View) {

        val threads = 5
        val end = 1000
        (1..threads)
                .onEach { thread ->
                    Flowable.range(0, end)
                            .subscribeOn(Schedulers.newThread())
                            .subscribe { Log.i("Thread $thread\t Log: $it") }
                }

    }

    @SuppressLint("CheckResult")
    fun testthree_clicked(view: View) {
        Flowable.range(0, 10000)
                .subscribeOn(Schedulers.newThread())
                .subscribe {
                    if (it % 1000 == 0) {
                        Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    Log.i("Log: $it")
                }
    }

    fun testfour_clicked(view: View) {
        (1..3).forEach { thread ->
            Flowable.range(0, 10000)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe {
                        if (it % 1000 == 0) {
                            Log.e("Something bad happened $thread $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                        }
                        Log.i("Thread $thread\t Log: $it")
                    }
        }
    }

    fun throwException_clicked(view: View) {
        Log.e("Something bad happened", ArrayIndexOutOfBoundsException("Message in exception"))
    }

    fun throwUncheckedException_clicked(view: View) {
        throw ArrayIndexOutOfBoundsException("unchecked exception")
    }

    fun logMetadata_clicked(view: View) {
        MetadataLogger.customMetadataLambda = {
            val time = Date(System.currentTimeMillis())
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            val uptime = SystemClock.uptimeMillis()
            listOf(Pair("CURRENT_TIME", format.format(time)),
                    Pair("SYSTEM_UPTIME", uptime.toString()))
        }
        Log.logMetadata(applicationContext)
    }


    fun send_clicked(view: View) {
        SendLogDialogFragment.newInstance("kidal5@centrum.cz").show(supportFragmentManager, "OMG")
    }

    fun deleteLogs_clicked(view: View) {
        FileLogger.deleteAllLogs(applicationContext)
        Toast.makeText(this, "Logs deleted", Toast.LENGTH_SHORT).show()
    }

    fun switchLogger_clicked(view: View?) {
        bundle = when (bundle) {
            is DayLogBundle -> CircleLogBundle(maxFileSizeMegaBytes = 1)
            is CircleLogBundle -> StrictCircleLogBundle(maxFileSizeMegaBytes = 1, numOfFiles = 4)
            is StrictCircleLogBundle -> DayLogBundle()
            else -> throw Exception("Unknown bundle, should not arise at all")
        }

        Log.addLogger(FileLogger(applicationContext, bundle), TAG_FILELOGGER)
        val text = bundle.javaClass.simpleName.replace("Bundle", "")
        (findViewById<TextView>(R.id.loggerInUseTextView)).text = text
    }

    fun askForPermission_clicked(view: View) {

        val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE

        val granted = ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED


        if (granted) {
            showGoodPermission()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(perm), REQUEST)
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showGoodPermission()
        } else {
            showBadPermission()
        }
    }

    private fun showGoodPermission() {
        showDialog("<3", "Thanks for file write permission.")
        Log.i("Thanks for permission")
    }

    private fun showBadPermission() {
        showDialog("</3", "Give me permission omg")
        Log.i("Give me permission. Xd")
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create()
                .show()

    }

    fun testShare_clicked(view: View) {

        SendLogDialogFragment.newInstance("kidal5@centrum.cz").show(supportFragmentManager, "STRING")

    }

    private fun getTextFrom(editText: EditText, ws: Boolean): String {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val url = when {
            editText.text.isNotEmpty() -> editText.text
            clipboard.hasPrimaryClip() -> {
                val baseUrl = clipboard.primaryClip.getItemAt(0).text
                when (ws) {
                    true -> "ws://$baseUrl/ws/v1/"
                    false -> "http://$baseUrl/api/v1/"
                }
            }
            else -> ""
        }.toString()

        editText.setText(url)

        return url
    }

}




































