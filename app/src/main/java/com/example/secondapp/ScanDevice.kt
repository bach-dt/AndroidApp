package com.example.secondapp

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.HashMap

private const val CAMERA_REQUEST_CODE = 1888

class ScanDevice : AppCompatActivity() {

    companion object {
        val AC_REMOTE_ID:String = "com.example.application.example.AC_REMOTE_"
        val HDMI_WIRE_ID:String = "com.example.application.example.HDMI_WIRE_"
        val LASER_PEN_ID:String = "com.example.application.example.LASER_PEN_"
        val MCR_PHONE_ID:String = "com.example.application.example.MCR_PHONE_"
        val DEVICE_ID:String = "com.example.application.example.DEVICE_ID"
    }

    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_device)

        val intent_:Intent = intent
        val mail:String = intent_.getStringExtra(Login.EMAIL_NAME).toString()

        val intent = intent
        var ac_remote = intent.getStringExtra(SelectEquipment.AC_REMOTE)
        var hdmi_wire = intent.getStringExtra(SelectEquipment.HDMI_WIRE)
        var laser_pen = intent.getStringExtra(SelectEquipment.LASER_PEN)
        var mcr_phone = intent.getStringExtra(SelectEquipment.MCR_PHONE)

        val ac_cb = findViewById<CheckBox>(R.id.ac_cb)
        val hdmi_cb = findViewById<CheckBox>(R.id.hdmi_cb)
        val laser_cb = findViewById<CheckBox>(R.id.laser_cb)
        val micro_cb = findViewById<CheckBox>(R.id.micro_cb)
        var deviceID = ""

        ac_cb.isChecked = false
        hdmi_cb.isChecked = false
        laser_cb.isChecked = false
        micro_cb.isChecked = false

        setupPermissions()
        val scannerView: CodeScannerView = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                if (it.text.length == 4) {
                    val id = it.text

                    deviceID = it.text.toString()
                    if (id[3] == '1'){
                        ac_remote = id.take(3)
                        ac_cb.isChecked = true
                    }
                    if (id[3] == '2'){
                        hdmi_wire = id.take(3)
                        hdmi_cb.isChecked = true
                    }
                    if (id[3] == '3'){
                        laser_pen = id.take(3)
                        laser_cb.isChecked = true
                    }
                    if (id[3] == '4'){
                        mcr_phone = id.take(3)
                        micro_cb.isChecked = true
                    }

                } else {
                    Toast.makeText(this, "Quét mã không thành công!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera error: ${it.message}", Toast.LENGTH_SHORT).show()

            }
        }

        ac_cb.setOnClickListener {
            if (!ac_cb.isChecked){
                ac_cb.isChecked = false
                ac_remote = ""
            }
        }

        hdmi_cb.setOnClickListener {
            if (!hdmi_cb.isChecked){
                hdmi_cb.isChecked = false
                hdmi_wire = ""
            }
        }

        laser_cb.setOnClickListener {
            if (!laser_cb.isChecked){
                laser_cb.isChecked = false
                laser_pen = ""
            }
        }

        micro_cb.setOnClickListener {
            if (!micro_cb.isChecked){
                micro_cb.isChecked = false
                mcr_phone = ""
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
        startScanning()

        val exit2 = findViewById<Button>(R.id.exit2)
        exit2.setOnClickListener(View.OnClickListener {
            val intent_ = Intent(this, SelectEquipment::class.java)
            intent_.putExtra(Login.EMAIL_NAME, mail)
            intent_.putExtra(AC_REMOTE_ID, ac_remote)
            intent_.putExtra(HDMI_WIRE_ID, hdmi_wire)
            intent_.putExtra(LASER_PEN_ID, laser_pen)
            intent_.putExtra(MCR_PHONE_ID, mcr_phone)
            intent_.putExtra(DEVICE_ID, deviceID)
            startActivity(intent_)
        })


    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun startScanning() {

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need the camera permission to able to use this app",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //success
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}



