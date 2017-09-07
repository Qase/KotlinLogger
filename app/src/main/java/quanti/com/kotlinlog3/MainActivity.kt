package quanti.com.kotlinlog3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import io.reactivex.Flowable
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.android.AndroidLogger
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.file.FileLogger
import quanti.com.kotlinlog.file.SendLogDialogFragment


class MainActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {

    internal var checked = 3
    internal var MY_PERMISSIONS_REQUEST = 98

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //resolve permission
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // Here, thisActivity is the current activity
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST)
        } else {
            FileLogger.setUp(this)
            Log.addLogger(FileLogger)
        }



        Log.addLogger(AndroidLogger())

        (findViewById(R.id.radio_group) as RadioGroup).setOnCheckedChangeListener(this)

    }


    fun button_clicked(view: View) {
        when (checked) {
            LogLevel.DEBUG -> Log.d("laisdjlakdj")
            LogLevel.ERROR -> Log.e("laisdjlakdj")
            LogLevel.INFO -> Log.i("laisdjlakdj")
            LogLevel.VERBOSE -> Log.v("laisdjlakdj")
            LogLevel.WARN -> Log.w("laisdjlakdj")
        }
    }

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {
        val text = (group.findViewById(checkedId) as RadioButton).text
        checked = Integer.parseInt(text[text.length - 1] + "")
    }

    fun burst_clicked(view: View) {

        val editText = findViewById(R.id.burst_number) as EditText

        val end = editText.text.toString().toInt()

        Flowable
                .range(0, end)
                .map { it.toString() }
                .subscribe { Log.i(it) }
    }

    fun cz_clicked(view: View) {
        SendLogDialogFragment.newInstance().show(supportFragmentManager, "OMG")
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == MY_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("Diky za permission")
            FileLogger.setUp(this)
            Log.addLogger(FileLogger)
        } else {
            //show some shit
            AlertDialog.Builder(this)
                    .setTitle("</3")
                    .setMessage("Give me permission omg")
                    .setOnDismissListener {
                        Log.i("After dialog")
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                MY_PERMISSIONS_REQUEST)
                    }
                    .create()
                    .show()
        }
    }
}










